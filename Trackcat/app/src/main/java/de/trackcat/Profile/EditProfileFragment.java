package de.trackcat.Profile;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;
import de.trackcat.APIClient;
import de.trackcat.APIConnector;
import de.trackcat.Database.DAO.UserDAO;
import de.trackcat.Database.Models.User;
import de.trackcat.GlobalFunctions;
import de.trackcat.MainActivity;
import de.trackcat.R;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class EditProfileFragment extends Fragment implements View.OnClickListener {

    /* Variables */
    private static final int READ_REQUEST_CODE = 20;
    DatePickerDialog picker;
    AlertDialog.Builder alert;
    LayoutInflater layoutInflater;
    UserDAO userDAO;
    User currentUser;
    View view, alertView;
    EditText firstName, lastName;
    RadioGroup gender;
    TextView dayOfBirth, size, weight;
    Button btnSave;
    CircleImageView imageUpload;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        /* read variables */
        firstName = view.findViewById(R.id.input_firstName);
        lastName = view.findViewById(R.id.input_lastName);
        gender = view.findViewById(R.id.input_gender);
        weight = view.findViewById(R.id.input_weight);
        size = view.findViewById(R.id.input_height);
        dayOfBirth = view.findViewById(R.id.input_dayOfBirth);
        btnSave = view.findViewById(R.id.btn_save);
        imageUpload = view.findViewById(R.id.profile_image_upload);

        /* set onClick Listener */
        btnSave.setOnClickListener(this);
        dayOfBirth.setOnClickListener(this);
        size.setOnClickListener(this);
        weight.setOnClickListener(this);
        imageUpload.setOnClickListener(this);

        /* get current user */
        userDAO = new UserDAO(MainActivity.getInstance());
        currentUser = userDAO.read(MainActivity.getActiveUser());

        /* read profile values from global db */
        HashMap<String, String> map = new HashMap<>();
        map.put("eMail", currentUser.getMail());

        Retrofit retrofit = APIConnector.getRetrofit();
        APIClient apiInterface = retrofit.create(APIClient.class);

        /* start a call */
        Call<ResponseBody> call = apiInterface.getUserByEmail(map);

        call.enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    /* get jsonString from API */
                    String jsonString = response.body().string();

                    /* parse json */
                    JSONObject userJSON = new JSONObject(jsonString);
                    Log.d(getResources().getString(R.string.app_name) + "-EditProfileInformation", "Edit-Profilinformation erhalten von: " + userJSON.getString("firstName") + " " + userJSON.getString("lastName"));

                    /* read values from global DB */
                    setProfileValues(userJSON.getString("firstName"), userJSON.getString("lastName"), userJSON.getLong("dateOfBirth"), (float) userJSON.getDouble("size"), (float) userJSON.getDouble("weight"), userJSON.getInt("gender"));

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                call.cancel();

                /* read values from local DB */
                setProfileValues(currentUser.getFirstName(), currentUser.getLastName(), currentUser.getDateOfBirth(), currentUser.getSize(), currentUser.getWeight(), currentUser.getGender());
            }
        });

        return view;
    }

    /* function to set profile values */
    private void setProfileValues(String user_firstName, String user_lastName, long user_dayOfBirth, float user_size, float user_weight, int user_gender) {

        firstName.setText(user_firstName);
        lastName.setText(user_lastName);
        String string1 = "" + user_weight;
        weight.setText("" + string1.replace('.', ','));
        String string2 = "" + user_size;
        size.setText("" + "" + string2.replace('.', ','));

        /* set gender */
        switch (user_gender) {
            case 0:
                gender.check(R.id.radioFemale);
                break;
            case 1:
                gender.check(R.id.radioMale);
                break;

        }

        /*set dayOfBirth*/
        String curDateString = GlobalFunctions.getDateFromMillis(user_dayOfBirth, "dd.MM.yyyy");
        dayOfBirth.setText(curDateString);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_save:
                /* Inputfelder auslesen */
                String input_firstName = firstName.getText().toString();
                String input_lastName = lastName.getText().toString();
                String input_weight = weight.getText().toString();
                String input_height = size.getText().toString();
                String input_dayOfBirth = dayOfBirth.getText().toString();

                /* get selected gender */
                int input_gender_id = gender.getCheckedRadioButtonId();
                int gender;
                switch (input_gender_id) {
                    case R.id.radioFemale:
                        gender = 0;
                        break;
                    case R.id.radioMale:
                        gender = 1;
                        break;
                    default:
                        gender = 2;
                        break;
                }

                /* check if all fields are filled and  validate inputs*/
                if (validate() && !input_firstName.equals("") && !input_lastName.equals("")) {

                    /* parse imageView into bytes */
                    ImageView imageView = view.findViewById(R.id.profile_image_upload);
                    Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                    bitmap = Bitmap.createScaledBitmap(bitmap, 500, 500, false);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    byte[] imageBytes = stream.toByteArray();

                    /* parse values */
                    String string1 = "" + input_height;
                    input_height = "" + string1.replace(',', '.');
                    String string2 = "" + input_weight;
                    input_weight = "" + string2.replace(',', '.');
                    long long_dayOfBirth=0;
                    try {
                        long_dayOfBirth=GlobalFunctions.getMillisFromString(input_dayOfBirth, "dd.MM.yyyy");
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    String image = GlobalFunctions.getBase64FromBytes(imageBytes);

                    /* change values in local DB */
                    currentUser.setImage(imageBytes);
                    currentUser.setFirstName(input_firstName);
                    currentUser.setLastName(input_lastName);
                    currentUser.setSize(Float.valueOf(input_height));
                    currentUser.setWeight(Float.valueOf(input_weight));
                    currentUser.setGender(gender);
                    currentUser.isSynchronised(false);
                    currentUser.setDateOfBirth(long_dayOfBirth);
                    userDAO.update(currentUser.getId(), currentUser);

                    /* change values in global DB*/
                    HashMap<String, String> map = new HashMap<>();
                    map.put("image", image);
                    map.put("firstName", input_firstName);
                    map.put("lastName", input_lastName);
                    map.put("height", input_height);
                    map.put("weight", input_weight);
                    map.put("gender", "" + gender);
                    map.put("dateOfBirth", ""+long_dayOfBirth);

                    Retrofit retrofit = APIConnector.getRetrofit();
                    APIClient apiInterface = retrofit.create(APIClient.class);

                    /* start a call */
                    Call<ResponseBody> call = apiInterface.updateUser(map);

                    call.enqueue(new Callback<ResponseBody>() {

                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                            try {
                                /* get jsonString from API */
                                String jsonString = response.body().string();

                                /* parse json */
                                JSONObject successJSON = new JSONObject(jsonString);

                                if (successJSON.getString("success").equals("0")) {

                                    /* save is Synchronized value as true */
                                    currentUser.isSynchronised(true);
                                    userDAO.update(currentUser.getId(), currentUser);
                                }

                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            call.cancel();
                        }
                    });


                    /* UI-Meldung */
                    if (MainActivity.getHints()) {
                        Toast.makeText(getContext(), "Benutzer \"" + input_firstName + " " + input_lastName + "\" wurde erfolgreich geändert!", Toast.LENGTH_LONG).show();
                    }

                } else {
                    if (MainActivity.getHints()) {
                        Toast.makeText(getContext(), getResources().getString(R.string.tFillAllFields), Toast.LENGTH_LONG).show();
                    }
                }

                break;
            case R.id.profile_image_upload:
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                startActivityForResult(intent, READ_REQUEST_CODE);
                if (MainActivity.getHints()) {
                    Toast.makeText(getContext(), "Wählen Sie Ihr Profilbild aus!", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.input_dayOfBirth:

                /* get old values */
                String[] dayOfBirthValues = dayOfBirth.getText().toString().split("\\.");

                /* set datepicker an set value in field */
                int day = Integer.parseInt(dayOfBirthValues[0]);
                int month = Integer.parseInt(dayOfBirthValues[1]) - 1;
                int year = Integer.parseInt(dayOfBirthValues[2]);

             /*   final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);*/
                picker = new DatePickerDialog(getContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                String month = "" + (monthOfYear + 1);
                                String day = "" + dayOfMonth;
                                if (monthOfYear + 1 < 10) {
                                    month = "0" + month;
                                }
                                if (dayOfMonth < 10) {
                                    day = "0" + day;
                                }
                                dayOfBirth.setText(day + "." + month + "." + year);
                            }
                        }, year, month, day);
                picker.show();
                break;
            case R.id.input_height:

                /* create AlertBox */
                alert = new AlertDialog.Builder(getContext());
                alert.setTitle("Größe festlegen");
                layoutInflater = (LayoutInflater) Objects.requireNonNull(getContext()).getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                alertView = layoutInflater != null ? layoutInflater.inflate(R.layout.fragment_numberpicker, null, true) : null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                    /* get old values */
                    String[] heightValues = size.getText().toString().split(",");

                    /* set max, min and unit */
                    alert.setView(alertView);
                    NumberPicker numberPickerInteger = alertView.findViewById(R.id.numberPickerInteger);
                    numberPickerInteger.setMinValue(0);
                    numberPickerInteger.setMaxValue(300);
                    numberPickerInteger.setValue(Integer.parseInt(heightValues[0]));

                    NumberPicker numberPickerDecimal = alertView.findViewById(R.id.numberPickerDecimal);
                    numberPickerDecimal.setMinValue(0);
                    numberPickerDecimal.setMaxValue(10);
                    numberPickerDecimal.setValue(Integer.parseInt(heightValues[1]));

                    TextView unit = alertView.findViewById(R.id.label_unit);
                    unit.setText("cm");
                }

                alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        NumberPicker numberPickerInteger = alertView.findViewById(R.id.numberPickerInteger);
                        NumberPicker numberPickerDecimal = alertView.findViewById(R.id.numberPickerDecimal);
                        size.setText(numberPickerInteger.getValue() + "," + numberPickerDecimal.getValue());
                    }
                });

                alert.setNegativeButton("Verwerfen", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
                alert.show();
                break;

            case R.id.input_weight:

                /* create AlertBox */
                alert = new AlertDialog.Builder(getContext());
                alert.setTitle("Gewicht festlegen");
                layoutInflater = (LayoutInflater) Objects.requireNonNull(getContext()).getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                alertView = layoutInflater != null ? layoutInflater.inflate(R.layout.fragment_numberpicker, null, true) : null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                    /* get old values */
                    String[] weightValues = weight.getText().toString().split(",");

                    /* set max, min and unit */
                    alert.setView(alertView);
                    NumberPicker numberPickerInteger = alertView.findViewById(R.id.numberPickerInteger);
                    numberPickerInteger.setMinValue(0);
                    numberPickerInteger.setMaxValue(500);
                    numberPickerInteger.setValue(Integer.parseInt(weightValues[0]));

                    NumberPicker numberPickerDecimal = alertView.findViewById(R.id.numberPickerDecimal);
                    numberPickerDecimal.setMinValue(0);
                    numberPickerDecimal.setMaxValue(10);
                    numberPickerDecimal.setValue(Integer.parseInt(weightValues[1]));

                    TextView unit = alertView.findViewById(R.id.label_unit);
                    unit.setText("kg");
                }

                alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        NumberPicker numberPickerInteger = alertView.findViewById(R.id.numberPickerInteger);
                        NumberPicker numberPickerDecimal = alertView.findViewById(R.id.numberPickerDecimal);

                        weight.setText(numberPickerInteger.getValue() + "," + numberPickerDecimal.getValue());
                    }
                });

                alert.setNegativeButton("Verwerfen", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
                alert.show();
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (resultData != null) {
                if (MainActivity.getHints()) {
                    Toast.makeText(getContext(), "Bild ausgewählt!", Toast.LENGTH_SHORT).show();
                }
                Bitmap img = null;
                try {
                    InputStream stream = getContext().getContentResolver().openInputStream(resultData.getData());
                    img = BitmapFactory.decodeStream(stream);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                ((CircleImageView) view.findViewById(R.id.profile_image_upload)).setImageBitmap(img);
            }
        }
    }

    public boolean validate() {
        boolean valid = true;

        /* read inputs */
        String input_firstName = firstName.getText().toString();
        String input_lastName = lastName.getText().toString();

        /* validate firstName */
        Pattern pattern3 = Pattern.compile(getResources().getString(R.string.rName));
        Matcher matcher3 = pattern3.matcher(input_firstName);
        if (!matcher3.matches()) {
            firstName.setError(getResources().getString(R.string.errorMsgName));
            Toast.makeText(MainActivity.getInstance().getApplicationContext(), getResources().getString(R.string.tErrorName), Toast.LENGTH_SHORT).show();
            valid = false;
        } else {
            firstName.setError(null);
        }

        /* validate lastName */
        Pattern pattern4 = Pattern.compile(getResources().getString(R.string.rName));
        Matcher matcher4 = pattern4.matcher(input_lastName);
        if (!matcher4.matches()) {
            lastName.setError(getResources().getString(R.string.errorMsgName));
            Toast.makeText(MainActivity.getInstance().getApplicationContext(), getResources().getString(R.string.tErrorName), Toast.LENGTH_SHORT).show();
            valid = false;
        } else {
            lastName.setError(null);
        }

        return valid;
    }
}

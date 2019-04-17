package de.mobcom.group3.gotrack.Profile;

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
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Objects;
import de.hdodenhof.circleimageview.CircleImageView;
import de.mobcom.group3.gotrack.MainActivity;
import de.mobcom.group3.gotrack.R;


public class EditProfileFragment extends Fragment implements View.OnClickListener {

    private static final int READ_REQUEST_CODE = 20;
    DatePickerDialog picker;
    AlertDialog.Builder alert;
    LayoutInflater layoutInflater;
    View alertView;

    /* Variables */
    View view;
    EditText firstName, lastName;
    RadioGroup gender;
    TextView dayOfBirth, height, weight;
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
        height = view.findViewById(R.id.input_height);
        dayOfBirth = view.findViewById(R.id.input_dayOfBirth);
        btnSave = view.findViewById(R.id.btn_save);
        imageUpload = view.findViewById(R.id.profile_image_upload);

        /* set onClick Listener */
        btnSave.setOnClickListener(this);
        dayOfBirth.setOnClickListener(this);
        height.setOnClickListener(this);
        weight.setOnClickListener(this);
        imageUpload.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_save:
                /* Inputfelder auslesen */
                String input_firstName = firstName.getText().toString();
                String input_lastName = lastName.getText().toString();
                String input_weight = weight.getText().toString();
                String input_height = height.getText().toString();

                /* get selected gender */
                int input_gender_id = gender.getCheckedRadioButtonId();
                String gender="";
                switch (input_gender_id) {
                    case R.id.radioFemale:
                        gender="weiblich";
                        break;
                    case R.id.radioMale:
                        gender="männlich";
                        break;
                    case R.id.radioVarious:
                        gender="diverses";
                        break;
                }

                /* check if all fields are filled and  validate inputs*/
                if (validate() && !input_firstName.equals("") && !input_lastName.equals("")) {

                    /* ImageView in Bytes umwandeln */
                    ImageView imageView = view.findViewById(R.id.profile_image_upload);
                    Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                    bitmap = Bitmap.createScaledBitmap(bitmap, 500, 500, false);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    byte[] imageBytes = stream.toByteArray();

                    //TODO Nutzerandereungen an DB senden

                    /* UI-Meldung & Felder löschen */
                    if (MainActivity.getHints()) {
                        Toast.makeText(getContext(), "Benutzer \"" + input_firstName + " " + input_lastName + "\" wurde erfolgreich geändert!", Toast.LENGTH_LONG).show();
                    }

                } else {
                    if (MainActivity.getHints()) {
                        Toast.makeText(getContext(), "Bitte alle Felder ausfüllen!", Toast.LENGTH_LONG).show();
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

                /* set datepicker an set value in field */
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                picker = new DatePickerDialog(getContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                dayOfBirth.setText(dayOfMonth + "." + (monthOfYear + 1) + "." + year);
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

                    /* set max, min and unit */
                    alert.setView(alertView);
                    NumberPicker numberPickerInteger = alertView.findViewById(R.id.numberPickerInteger);
                    numberPickerInteger.setMinValue(0);
                    numberPickerInteger.setMaxValue(300);

                    NumberPicker numberPickerDecimal = alertView.findViewById(R.id.numberPickerDecimal);
                    numberPickerDecimal.setMinValue(0);
                    numberPickerDecimal.setMaxValue(10);

                    TextView unit = alertView.findViewById(R.id.label_unit);
                    unit.setText("cm");
                }

                alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        NumberPicker numberPickerInteger = alertView.findViewById(R.id.numberPickerInteger);
                        NumberPicker numberPickerDecimal = alertView.findViewById(R.id.numberPickerDecimal);
                        height.setText(numberPickerInteger.getValue() + "," + numberPickerDecimal.getValue()+" cm");
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

                    /* set max, min and unit */
                    alert.setView(alertView);
                    NumberPicker numberPickerInteger = alertView.findViewById(R.id.numberPickerInteger);
                    numberPickerInteger.setMinValue(0);
                    numberPickerInteger.setMaxValue(500);

                    NumberPicker numberPickerDecimal = alertView.findViewById(R.id.numberPickerDecimal);
                    numberPickerDecimal.setMinValue(0);
                    numberPickerDecimal.setMaxValue(10);

                    TextView unit = alertView.findViewById(R.id.label_unit);
                    unit.setText("kg");
                }

                alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        NumberPicker numberPickerInteger = alertView.findViewById(R.id.numberPickerInteger);
                        NumberPicker numberPickerDecimal = alertView.findViewById(R.id.numberPickerDecimal);
                        weight.setText(numberPickerInteger.getValue() + "," + numberPickerDecimal.getValue()+" kg");
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
        int nameLength = 15;
        /* read inputs */
        String input_firstName = firstName.getText().toString();
        String input_lastName = lastName.getText().toString();

        /* validate name */
        if ((input_firstName + " " + input_lastName).length() > nameLength) {
            firstName.setError("Ihr kompletter Name darf nicht länger als max. " + nameLength+" + Zeichen sein.");
            lastName.setError("Ihr kompletter Name darf nicht länger als max. " + nameLength+" + Zeichen sein.");
            Toast.makeText(MainActivity.getInstance().getApplicationContext(), "Ihr Name ist zu lang.", Toast.LENGTH_SHORT).show();
            valid = false;
        }

        return valid;
    }
}

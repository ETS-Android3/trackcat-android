package de.trackcat.Profile;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.trackcat.APIClient;
import de.trackcat.APIConnector;
import de.trackcat.Database.DAO.UserDAO;
import de.trackcat.Database.Models.User;
import de.trackcat.MainActivity;
import de.trackcat.R;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class EditPasswordFragment extends Fragment implements View.OnClickListener {

    EditText currentPassword, password1, password2;
    Button btnSave;
    UserDAO userDAO;
    User currentUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_edit_password, container, false);

        /* get fields */
        currentPassword = view.findViewById(R.id.input_currentPassword);
        password1 = view.findViewById(R.id.input_password1);
        password2 = view.findViewById(R.id.input_password2);
        btnSave = view.findViewById(R.id.btn_save);

        btnSave.setOnClickListener(this);

        /* get current User from DB*/
        userDAO = new UserDAO(MainActivity.getInstance());

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_save:

                /* change button */
                btnSave.setEnabled(false);
                btnSave.setBackgroundColor(getResources().getColor(R.color.colorAccentDisable));

                /* Inputfelder auslesen */
                String input_currentPassword = currentPassword.getText().toString();
                String input_password1 = password1.getText().toString();
                String input_password2 = password2.getText().toString();

                /* check if all fields are filled and validate inputs*/
                if (!input_currentPassword.equals("") && !input_password1.equals("") && !input_password2.equals("")) {

                    /* check if passwords are equals */
                    if (input_password1.equals(input_password2)) {

                        /* validate password */
                        if (validate()) {

                            currentUser = userDAO.read(MainActivity.getActiveUser());

                            /* send inputs to server */
                            Retrofit retrofit = APIConnector.getRetrofit();
                            APIClient apiInterface = retrofit.create(APIClient.class);
                            String base = currentUser.getMail() + ":" + input_currentPassword;
                            HashMap<String, String> map = new HashMap<>();
                            map.put("newPw", input_password2);

                            // TODO hashsalt Password
                            /* start a call */
                            String authString = "Basic " + Base64.encodeToString(base.getBytes(), Base64.NO_WRAP);
                            Call<ResponseBody> call = apiInterface.changeUserPassword(authString, map);

                            call.enqueue(new Callback<ResponseBody>() {

                                @Override
                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                    try {

                                        /* get jsonString from API */
                                        String jsonString = response.body().string();
                                        ;

                                        /* parse json */
                                        JSONObject mainObject = new JSONObject(jsonString);
                                        /*  if change password success*/
                                        if (mainObject.getString("success").equals("0")) {

                                            /* change password in local DB */
                                            changePasswordInLokalDB(input_password2);
                                        }

                                        /* old password not correct */
                                    } catch (Exception e) {
                                        Toast.makeText(getContext(), getResources().getString(R.string.tErrorOldPassword), Toast.LENGTH_LONG).show();
                                        /* set btn enable */
                                        btnSave.setEnabled(true);
                                        btnSave.setBackgroundColor(getResources().getColor(R.color.colorGreenAccent));
                                    }
                                }

                                /* no internet connection */
                                @Override
                                public void onFailure(Call<ResponseBody> call, Throwable t) {

                                    /* change password in local DB */
                                    if (input_currentPassword.equals(currentUser.getPassword())) {
                                        changePasswordInLokalDB(input_password2);
                                    } else {
                                        /* old password not correct */
                                        Toast.makeText(getContext(), getResources().getString(R.string.tErrorOldPassword), Toast.LENGTH_LONG).show();
                                        /* set btn enable */
                                        btnSave.setEnabled(true);
                                        btnSave.setBackgroundColor(getResources().getColor(R.color.colorGreenAccent));
                                    }

                                    call.cancel();
                                }
                            });
                        }
                    } else {
                        Toast.makeText(getContext(), getResources().getString(R.string.tErrorPasswordNotIdentical), Toast.LENGTH_LONG).show();
                        /* set btn enable */
                        btnSave.setEnabled(true);
                        btnSave.setBackgroundColor(getResources().getColor(R.color.colorGreenAccent));
                    }
                } else {
                    if (MainActivity.getHints()) {
                        Toast.makeText(getContext(), getResources().getString(R.string.tFillAllFields), Toast.LENGTH_LONG).show();
                    }

                    /* set btn enable */
                    btnSave.setEnabled(true);
                    btnSave.setBackgroundColor(getResources().getColor(R.color.colorGreenAccent));
                }
                break;
        }
    }

    /* function to change password in DB */
    private void changePasswordInLokalDB(String password) {
        currentUser.setPassword(password);
        userDAO.update(currentUser.getId(), currentUser);

        /* set btn enable */
        btnSave.setEnabled(true);
        btnSave.setBackgroundColor(getResources().getColor(R.color.colorGreenAccent));

        /* UI-Meldung */
        if (MainActivity.getHints()) {
            Toast.makeText(getContext(), getResources().getString(R.string.tSuccessChangePassword), Toast.LENGTH_LONG).show();
        }
    }

    /* function to validate */
    public boolean validate() {
        boolean valid = true;

        /* read inputs */
        String input_password = password1.getText().toString();

        /* validate password */
        String regrex = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[^a-zA-Z0-9])(?!.*\\s).{8,15}$";
        Pattern pattern = Pattern.compile(regrex);
        Matcher matcher = pattern.matcher(input_password);

        if (!matcher.matches()) {
            password1.setError(getResources().getString(R.string.errorMsgPassword));
            password2.setError(getResources().getString(R.string.errorMsgPassword));
            Toast.makeText(MainActivity.getInstance().getApplicationContext(), getResources().getString(R.string.tErrorPassword), Toast.LENGTH_SHORT).show();
            valid = false;
        }

        return valid;
    }
}

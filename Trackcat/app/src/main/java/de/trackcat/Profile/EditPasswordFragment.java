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
                setButtonDisable();

                /* Inputfelder auslesen */
                String input_currentPassword = currentPassword.getText().toString();
                String input_password1 = password1.getText().toString();
                String input_password2 = password2.getText().toString();

                /* validate password */
                boolean checkCurrentPassword = GlobalFunctions.validatePassword(currentPassword, MainActivity.getInstance());
                boolean checkPassword1 = GlobalFunctions.validatePassword(password1, MainActivity.getInstance());
                boolean checkPassword2 = GlobalFunctions.validatePassword(password2, MainActivity.getInstance());
                if (checkCurrentPassword && checkPassword1 && checkPassword2) {

                    /* check if passwords are equals */
                    if (input_password1.equals(input_password2)) {

                        currentUser = userDAO.read(MainActivity.getActiveUser());

                        /* send inputs to server */
                        Retrofit retrofit = APIConnector.getRetrofit();
                        APIClient apiInterface = retrofit.create(APIClient.class);
                        String base = currentUser.getMail() + ":" + input_currentPassword;
                        HashMap<String, String> map = new HashMap<>();
                        String password = GlobalFunctions.hashPassword(input_password2);
                        map.put("newPw",password);
                        map.put("timeStamp", "" + GlobalFunctions.getTimeStamp());

                        // TODO hashsalt Password
                        /* start a call */
                        String authString = "Basic " + Base64.encodeToString(base.getBytes(), Base64.NO_WRAP);
                        Call<ResponseBody> call = apiInterface.changeUserPassword(authString, map);

                        call.enqueue(new Callback<ResponseBody>() {

                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                try {

                                    if (response.code() == 401) {
                                        MainActivity.getInstance().showNotAuthorizedModal(10);
                                    } else {
                                        /* get jsonString from API */
                                        String jsonString = response.body().string();

                                        /* parse json */
                                        JSONObject mainObject = new JSONObject(jsonString);
                                        /*  if change password success*/
                                        if (mainObject.getString("success").equals("0")) {

                                            /* change password in local DB */
                                            changePasswordInLokalDB(password);
                                        } else if (mainObject.getString("success").equals("1")) {

                                            if (MainActivity.getHints()) {
                                                Toast.makeText(getContext(), getResources().getString(R.string.tChangePasswortUnknownError), Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    }
                                    /* old password not correct */
                                } catch (Exception e) {

                                    /* set btn enable */
                                    setButtonEnable();
                                }
                            }

                            /* no internet connection */
                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {

                                Toast.makeText(getContext(), getResources().getString(R.string.tNoConnectionPassword), Toast.LENGTH_LONG).show();

                                /* set btn enable */
                                setButtonEnable();

                                call.cancel();
                            }
                        });

                    } else {
                        if (MainActivity.getHints()) {
                            Toast.makeText(getContext(), getResources().getString(R.string.tErrorPasswordNotIdentical), Toast.LENGTH_LONG).show();
                        }
                        /* set btn enable */
                        setButtonEnable();
                    }
                } else {

                    /* set btn enable */
                    setButtonEnable();
                }
                break;
        }
    }

    /* function to change password in DB */
    private void changePasswordInLokalDB(String password) {
        currentUser.setPassword(password);
        currentUser.setTimeStamp(GlobalFunctions.getTimeStamp());
        userDAO.update(currentUser.getId(), currentUser);

        /* set btn enable */
        setButtonEnable();

        /* UI-Meldung */
        if (MainActivity.getHints()) {
            Toast.makeText(getContext(), getResources().getString(R.string.tSuccessChangePassword), Toast.LENGTH_LONG).show();
        }
    }

    /* functions to enable/disable button */
    private void setButtonEnable() {
        btnSave.setEnabled(true);
        btnSave.setBackgroundColor(getResources().getColor(R.color.colorGreenAccent));
    }

    private void setButtonDisable() {
        btnSave.setBackgroundColor(getResources().getColor(R.color.colorAccentDisable));
        btnSave.setEnabled(false);
    }
}

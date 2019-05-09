package de.trackcat.LogIn;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONObject;

import de.trackcat.APIClient;
import de.trackcat.APIConnector;
import de.trackcat.Database.DAO.UserDAO;
import de.trackcat.Database.Models.User;
import de.trackcat.GlobalFunctions;
import de.trackcat.MainActivity;
import de.trackcat.R;
import de.trackcat.SignIn.SignInFragment_1;
import de.trackcat.StartActivity;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class LogInFragment extends Fragment implements View.OnClickListener {

    private FragmentTransaction fragTransaction;
    private UserDAO userDAO;
    /* UI references */
    private EditText emailTextView, passwordTextView;
    private Button btnLogin;
    private TextView signInLink, messageBox, messageBoxInfo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_login, container, false);

        emailTextView = view.findViewById(R.id.input_email);
        passwordTextView = view.findViewById(R.id.input_password);
        btnLogin = view.findViewById(R.id.btn_login);
        signInLink = view.findViewById(R.id.link_signup);
        messageBox = view.findViewById(R.id.messageBox);
        messageBoxInfo = view.findViewById(R.id.messageBoxInfo);

        /* set on click-Listener */
        btnLogin.setOnClickListener(this);
        signInLink.setOnClickListener(this);

        /* set user dao */
        userDAO = new UserDAO(StartActivity.getInstance());

        /* set info after correct regist */
        if (getArguments() != null) {
            setErrorMessage("Anmeldung", getResources().getString(R.string.messageAfterCorrectRegist));
        }

        return view;
    }

    /* onClick Listener */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btn_login:
                login();
                break;
            case R.id.link_signup:
                fragTransaction = getFragmentManager().beginTransaction();
                fragTransaction.replace(R.id.mainFrame, new SignInFragment_1(),
                        getResources().getString(R.string.fSignIn_1));
                fragTransaction.commit();
                break;
        }
    }

    /* Function to Login */
    public void login() {

        /* validate the inputs */
        boolean emailCorrect = GlobalFunctions.validateEMail(emailTextView, StartActivity.getInstance());
        boolean passwordCorrect = GlobalFunctions.validatePassword(passwordTextView, StartActivity.getInstance());
        if (emailCorrect && passwordCorrect) {

            setButtonDisable();

            /* read the inputs to send */
            String email = emailTextView.getText().toString();
            String password = passwordTextView.getText().toString();

            /* set wait field */
            final ProgressDialog progressDialog = new ProgressDialog(getContext(),
                    R.style.AppTheme_Dark_Dialog);
            StartActivity.getInstance().progressDialog = progressDialog;
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage(getResources().getString(R.string.login));
            progressDialog.show();

            /* send inputs to server */
            Retrofit retrofit = APIConnector.getRetrofit();
            APIClient apiInterface = retrofit.create(APIClient.class);
            String base = email + ":" + password;

            // TODO hashsalt Password
            /* start a call */
            String authString = "Basic " + Base64.encodeToString(base.getBytes(), Base64.NO_WRAP);
            Call<ResponseBody> call = apiInterface.getUser(authString);

            call.enqueue(new Callback<ResponseBody>() {

                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        progressDialog.dismiss();

                        /* user not authorized */
                        if (response.code() == 401) {
                            /* show server error message to user */
                            Log.d(getResources().getString(R.string.app_name) + "-LoginConnection", "Server Error: " + response.raw().message());
                            setErrorMessage("Daten nicht korrekt", getResources().getString(R.string.eDataNotCorrect));
                        } else {

                            /* get jsonString from API */
                            String jsonString = response.body().string();

                            /* parse json */
                            JSONObject mainObject = new JSONObject(jsonString);

                            /* open activity if login success*/
                            if (mainObject.getString("success").equals("0")) {

                                /* get userObject from Json */
                                JSONObject userObject = mainObject.getJSONObject("userData");

                                /* save logged user in db */
                                User loggedUser = new User();
                                loggedUser.setId(userObject.getInt("id"));
                                loggedUser.setIdUsers(userObject.getInt("id"));
                                loggedUser.setMail(userObject.getString("email"));
                                loggedUser.setFirstName(userObject.getString("firstName"));
                                loggedUser.setLastName(userObject.getString("lastName"));
                                if (userObject.getString("image") != "null") {
                                    loggedUser.setImage(GlobalFunctions.getBytesFromBase64(userObject.getString("image")));
                                }
                                loggedUser.setGender(userObject.getInt("gender"));
                                if (userObject.getInt("darkTheme") == 0) {
                                    loggedUser.setDarkThemeActive(false);
                                } else {
                                    loggedUser.setDarkThemeActive(true);
                                }

                                if (userObject.getInt("hints") == 0) {
                                    loggedUser.setHintsActive(false);
                                } else {
                                    loggedUser.setHintsActive(true);
                                }

                                try {
                                    loggedUser.setDateOfRegistration(userObject.getLong("dateOfRegistration"));
                                } catch (Exception e) {
                                }

                                try {
                                    loggedUser.setLastLogin(userObject.getLong("lastLogin"));
                                } catch (Exception e) {
                                }

                                try {
                                    loggedUser.setWeight((float) userObject.getDouble("weight"));
                                } catch (Exception e) {
                                }

                                try {
                                    loggedUser.setSize((float) userObject.getDouble("size"));
                                } catch (Exception e) {
                                }
                                try {
                                    loggedUser.setDateOfBirth(userObject.getLong("dateOfBirth"));
                                } catch (Exception e) {
                                }

                                loggedUser.setPassword(userObject.getString("password"));
                                loggedUser.setTimeStamp(userObject.getLong("timeStamp"));
                                loggedUser.isSynchronised(true);
                                userDAO.create(loggedUser);

                                /* open MainActivity */
                                Intent intent = new Intent(getContext(), MainActivity.class);
                                startActivity(intent);
                                getActivity().finish();

                            } else if (mainObject.getString("success").equals("2")) {

                                /* show server error message to user */
                                Log.d(getResources().getString(R.string.app_name) + "-LoginConnection", "Server Error: " + response.raw().message());
                                setErrorMessage("Fehlende Verifizierung", getResources().getString(R.string.eEMailNotVerified));
                            }
                        }
                    } catch (Exception e) {
                        progressDialog.dismiss();

                        /* show server error message to user */
                        Log.d(getResources().getString(R.string.app_name) + "-LoginConnection", "Server Error: " + response.raw().message());
                        setErrorMessage(response.raw().message(), getResources().getString(R.string.eServer));
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {

                    progressDialog.dismiss();

                    /* show server error message to user */
                    Log.d(getResources().getString(R.string.app_name) + "-LoginConnection", "Server Error: " + t.getMessage());
                    setErrorMessage(t.getMessage(), getResources().getString(R.string.eCheckConnection));
                    call.cancel();
                }
            });
        }
    }

    /* function to set Error */
    private void setErrorMessage(String messageBoxText, String messageInfoText) {

        messageBoxInfo.setVisibility(View.VISIBLE);
        messageBoxInfo.setText(messageInfoText);

        messageBox.setVisibility(View.VISIBLE);
        messageBox.setText(messageBoxText);
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        messageBox.setVisibility(View.GONE);
                        messageBoxInfo.setVisibility(View.GONE);
                    }
                }, 10000);
        setButtonEnable();
    }

    /* functions to enable/disable button */
    private void setButtonEnable() {
        btnLogin.setEnabled(true);
        btnLogin.setBackgroundColor(getResources().getColor(R.color.colorGreenAccent));
    }

    private void setButtonDisable() {
        btnLogin.setBackgroundColor(getResources().getColor(R.color.colorAccentDisable));
        btnLogin.setEnabled(false);
    }
}

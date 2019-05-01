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
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.trackcat.APIClient;
import de.trackcat.APIConnector;
import de.trackcat.Database.DAO.UserDAO;
import de.trackcat.Database.Models.User;
import de.trackcat.GlobalFunctions;
import de.trackcat.MainActivity;
import de.trackcat.R;
import de.trackcat.StartActivity;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

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
        if (!validate()) {
            return;
        }
        btnLogin.setEnabled(false);
        btnLogin.setBackgroundColor(getResources().getColor(R.color.colorAccentDisable));

        /* read the inputs to send */
        String email = emailTextView.getText().toString();
        String password = passwordTextView.getText().toString();

        /* set wait field */
        final ProgressDialog progressDialog = new ProgressDialog(getContext(),
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Anmeldung...");
        progressDialog.show();

        /* set waiting handler */
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        progressDialog.dismiss();

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
                                    /* get jsonString from API */
                                    String jsonString = response.body().string();

                                    if (jsonString.equals("\"1\"")) {
                                        /* set errror message */
                                        Log.d(getResources().getString(R.string.app_name) + "-LoginConnection", "Anmeldedaten nicht korrekt");
                                        messageBox.setVisibility(View.VISIBLE);
                                        messageBox.setText("Anmeldedaten sind nicht");
                                        messageBoxInfo.setText("Bitte überprüfen Sie Ihre Anmeldedaten.");
                                        messageBoxInfo.setVisibility(View.VISIBLE);
                                        new android.os.Handler().postDelayed(
                                                new Runnable() {
                                                    public void run() {
                                                        messageBox.setVisibility(View.GONE);
                                                        messageBox.setText("");
                                                        messageBoxInfo.setVisibility(View.GONE);
                                                        messageBoxInfo.setText("");
                                                    }
                                                }, 10000);
                                        btnLogin.setEnabled(true);
                                        btnLogin.setBackgroundColor(getResources().getColor(R.color.colorGreenAccent));
                                    } else {

                                        /* parse json */
                                        JSONObject mainObject = new JSONObject(jsonString);
                                        /* open activity if login success*/
                                        if (mainObject.getString("success").equals("0")) {

                                            /* get userObject from Json */
                                            JSONObject userObject = mainObject.getJSONObject("userData");

                                            /* save logged user in db */
                                            User loggedUser = new User();
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
                                            loggedUser.isSynchronised(true);
                                            userDAO.create(loggedUser);

                                          Intent intent = new Intent(getContext(), MainActivity.class);
                                          startActivity(intent);

                                          getActivity().finish();
                                      }
                                  }
                                } catch (Exception e) {

                                    /* show server error message to user */
                                    Log.d(getResources().getString(R.string.app_name) + "-LoginConnection", "Server Error: " + response.raw().message());
                                    messageBoxInfo.setVisibility(View.VISIBLE);
                                    messageBoxInfo.setText(" Es tut uns leid, leider ist ein Server Fehler aufgetreten. Versuchen Sie es später nochmal...");

                                    messageBox.setVisibility(View.VISIBLE);
                                    messageBox.setText(response.raw().message());
                                    new android.os.Handler().postDelayed(
                                            new Runnable() {
                                                public void run() {
                                                    messageBox.setVisibility(View.GONE);
                                                    messageBox.setText("");
                                                    messageBoxInfo.setVisibility(View.GONE);
                                                    messageBoxInfo.setText("");
                                                }
                                            }, 10000);
                                    btnLogin.setEnabled(true);
                                    btnLogin.setBackgroundColor(getResources().getColor(R.color.colorGreenAccent));
                                }
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                Log.d(getResources().getString(R.string.app_name) + "-LoginConnection", "Server Error: " + t.getMessage());
                                messageBox.setVisibility(View.VISIBLE);
                                messageBox.setText("Keine Serververbindung!");
                                messageBoxInfo.setText("Bitte überprüfen Sie Ihre Internetverbindung.");
                                messageBoxInfo.setVisibility(View.VISIBLE);
                                new android.os.Handler().postDelayed(
                                        new Runnable() {
                                            public void run() {
                                                messageBox.setVisibility(View.GONE);
                                                messageBox.setText("");
                                                messageBoxInfo.setVisibility(View.GONE);
                                                messageBoxInfo.setText("");
                                            }
                                        }, 10000);
                                btnLogin.setEnabled(true);
                                btnLogin.setBackgroundColor(getResources().getColor(R.color.colorGreenAccent));
                                call.cancel();
                            }
                        });
                    }
                }, 3000);
    }

    /* Function to validate user input */
    public boolean validate() {
        boolean valid = true;

        String email = emailTextView.getText().toString();
        String password = passwordTextView.getText().toString();

        /* validate email */
        Pattern pattern = Pattern.compile(getResources().getString(R.string.rEmail));
        Matcher matcher = pattern.matcher(email);

        if (!matcher.matches()) {
            emailTextView.setError(getResources().getString(R.string.errorMsgEMail));
            Toast.makeText(StartActivity.getInstance().getApplicationContext(), getResources().getString(R.string.tErrorEmail), Toast.LENGTH_SHORT).show();
            valid = false;
        } else {
            emailTextView.setError(null);
        }

        /* validate password */
        Pattern pattern2 = Pattern.compile(getResources().getString(R.string.rPassword));
        Matcher matcher2 = pattern2.matcher(password);

        if (!matcher2.matches()) {
            passwordTextView.setError(getResources().getString(R.string.errorMsgPassword));
            Toast.makeText(StartActivity.getInstance().getApplicationContext(), getResources().getString(R.string.tErrorPassword), Toast.LENGTH_SHORT).show();
            valid = false;
        } else {
            passwordTextView.setError(null);
        }
        return valid;
    }
}

package de.trackcat.LogIn;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import de.trackcat.APIClient;
import de.trackcat.APIConnector;
import de.trackcat.Database.DAO.RouteDAO;
import de.trackcat.Database.DAO.UserDAO;
import de.trackcat.Database.Models.Route;
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

import static okhttp3.internal.Util.UTF_8;

public class LogInFragment extends Fragment implements View.OnClickListener {

    /* variables */
    private FragmentTransaction fragTransaction;
    private UserDAO userDAO;
    private EditText emailTextView, passwordTextView;
    private Button btnLogin;
    private TextView signInLink, messageBox, messageBoxInfo, resetPassword;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        /* Views */
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        emailTextView = view.findViewById(R.id.input_email);
        passwordTextView = view.findViewById(R.id.input_password);
        btnLogin = view.findViewById(R.id.btn_login);
        signInLink = view.findViewById(R.id.link_signup);
        messageBox = view.findViewById(R.id.messageBox);
        messageBoxInfo = view.findViewById(R.id.messageBoxInfo);
        resetPassword = view.findViewById(R.id.link_resetPassword);

        /* Set on click-Listener */
        btnLogin.setOnClickListener(this);
        signInLink.setOnClickListener(this);

        /* set link */
        resetPassword.setClickable(true);
        resetPassword.setMovementMethod(LinkMovementMethod.getInstance());
        String text1 = "Passwort vergessen? Setzen Sie es <a href='" + getString(R.string.link_resetPassword) + "'>hier</a>  zurück.";
        resetPassword.setText(Html.fromHtml(text1));



        /* Set user dao */
        userDAO = new UserDAO(StartActivity.getInstance());

        /* Set info after correct regist */
        if (getArguments() != null) {
            setErrorMessage("Anmeldung", getResources().getString(R.string.messageAfterCorrectRegist));
        }
        return view;
    }

    /* OnClick Listener */
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

        /* Validate the inputs */
        boolean emailCorrect = GlobalFunctions.validateEMail(emailTextView, StartActivity.getInstance());
        boolean passwordCorrect = GlobalFunctions.validatePassword(passwordTextView, StartActivity.getInstance());
        if (emailCorrect && passwordCorrect) {

            setButtonDisable();

            /* Read the inputs to send */
            String email = emailTextView.getText().toString();
            String password = GlobalFunctions.hashPassword(passwordTextView.getText().toString());

            /* Wet wait field */
            final ProgressDialog progressDialog = new ProgressDialog(getContext(),
                    R.style.AppTheme_Dark_Dialog);
            StartActivity.getInstance().progressDialog = progressDialog;
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage(getResources().getString(R.string.login));
            progressDialog.show();

            /* Start a call */
            Retrofit retrofit = APIConnector.getRetrofit();
            APIClient apiInterface = retrofit.create(APIClient.class);
            String base = email + ":" + password;
            String authString = "Basic " + Base64.encodeToString(base.getBytes(), Base64.NO_WRAP);
            Call<ResponseBody> call = apiInterface.getUser(authString);
            call.enqueue(new Callback<ResponseBody>() {

                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {

                        /* User not authorized */
                        if (response.code() == 401) {

                            /* Show server error message to user */
                            Log.d(getResources().getString(R.string.app_name) + "-LoginConnection", "Server Error: " + response.raw().message());
                            setErrorMessage("Daten nicht korrekt", getResources().getString(R.string.eDataNotCorrect));
                        } else {

                            /* Get jsonString from API */
                            String jsonString = response.body().string();

                            /* Parse json */
                            JSONObject mainObject = new JSONObject(jsonString);

                            /* Open activity if login success*/
                            if (mainObject.getString("success").equals("0")) {

                                /* Get userObject from Json */
                                JSONObject userObject = mainObject.getJSONObject("userData");
                                userObject.put("password", password);

                                /* Save logged user in db */
                                userDAO.create(GlobalFunctions.createUser(userObject,false, true));

                                /* Create routes */
                                if(mainObject.getJSONArray("records").length()>0&& mainObject.getJSONArray("records")!=null) {
                                    JSONArray recordsArray = mainObject.getJSONArray("records");
                                    GlobalFunctions.createRecords(recordsArray, StartActivity.getInstance());
                                }

                                /* Open MainActivity */
                                Intent intent = new Intent(getContext(), MainActivity.class);
                                startActivity(intent);
                                getActivity().finish();

                            } else if (mainObject.getString("success").equals("2")) {

                                /* Show server error message to user */
                                Log.d(getResources().getString(R.string.app_name) + "-LoginConnection", "Server Error: " + response.raw().message());
                                setErrorMessage("Fehlende Verifizierung", getResources().getString(R.string.eEMailNotVerified));
                            }
                        }
                        progressDialog.dismiss();
                    } catch (Exception e) {
                        progressDialog.dismiss();

                        /* Show server error message to user */
                        Log.d(getResources().getString(R.string.app_name) + "-LoginConnection", "Server Error: " + response.raw().message());
                        setErrorMessage(e.toString(), getResources().getString(R.string.eServer));
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

    /* Function to set Error */
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

    /* Functions to enable/disable button */
    private void setButtonEnable() {
        btnLogin.setEnabled(true);
        btnLogin.setBackgroundColor(getResources().getColor(R.color.colorGreenAccent));
    }

    private void setButtonDisable() {
        btnLogin.setBackgroundColor(getResources().getColor(R.color.colorAccentDisable));
        btnLogin.setEnabled(false);
    }
}

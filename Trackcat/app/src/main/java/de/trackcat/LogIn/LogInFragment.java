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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.trackcat.APIClient;
import de.trackcat.APIConnector;
import de.trackcat.Database.Models.User;
import de.trackcat.MainActivity;
import de.trackcat.R;
import de.trackcat.StartActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LogInFragment extends Fragment implements View.OnClickListener {

    private FragmentTransaction fragTransaction;
    /* UI references */
    private EditText emailTextView;
    private EditText passwordTextView;
    private Button btnLogin;
    private TextView signInLink;
    private TextView messageBox;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_login, container, false);

        emailTextView = view.findViewById(R.id.input_email);
        passwordTextView = view.findViewById(R.id.input_password);
        btnLogin = view.findViewById(R.id.btn_login);
        signInLink = view.findViewById(R.id.link_signup);
        messageBox = view.findViewById(R.id.messageBox);

        /* set on click-Listener */
        btnLogin.setOnClickListener(this);
        signInLink.setOnClickListener(this);

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
                fragTransaction.replace(R.id.mainFrame, new SignInFragment(),
                        getResources().getString(R.string.fSignIn));
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
                        Call<String> call = apiInterface.getUser(authString);

                        call.enqueue(new Callback<String>() {

                            @Override
                            public void onResponse(Call<String> call, Response<String> response) {
                                Log.d(getResources().getString(R.string.app_name) + "-LoginConnection", response.body());

                                /* open activity if login success*/
                                if (response.body().equals("0")) {
                                    Intent intent = new Intent(getContext(), MainActivity.class);
                                    startActivity(intent);
                                } else {

                                    /* set errror message */
                                    messageBox.setVisibility(View.VISIBLE);
                                    messageBox.setText("FEHLER!");
                                    new android.os.Handler().postDelayed(
                                            new Runnable() {
                                                public void run() {
                                                    messageBox.setVisibility(View.GONE);
                                                }
                                            }, 7000);
                                    btnLogin.setEnabled(true);
                                }
                            }

                            @Override
                            public void onFailure(Call<String> call, Throwable t) {
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

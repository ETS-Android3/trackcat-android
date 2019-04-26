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
import de.trackcat.MainActivity;
import de.trackcat.R;
import de.trackcat.StartActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SignInFragment_3 extends Fragment implements View.OnClickListener {

    private FragmentTransaction fragTransaction;
    /* UI references */
    EditText password1, password2;
    Button btnSignIn, btnBack;
    TextView logInInLink;
    String firstName, lastName, email;
    private com.shuhart.stepview.StepView stepView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_signin_3, container, false);

        btnBack = view.findViewById(R.id.btn_back);
        btnSignIn = view.findViewById(R.id.btn_signin);
        logInInLink = view.findViewById(R.id.link_login);
        password1 = view.findViewById(R.id.input_password1);
        password2 = view.findViewById(R.id.input_password2);

        /* get bundle */
        if (getArguments() != null) {
            firstName = getArguments().getString("firstName");
            lastName = getArguments().getString("lastName");
            email = getArguments().getString("email");
            if (getArguments().getString("password1") != null) {
                password1.setText(getArguments().getString("password1"));
            }
            if (getArguments().getString("password2") != null) {
                password2.setText(getArguments().getString("password2"));
            }
        }

        /* step view */
        stepView = view.findViewById(R.id.step_view);
        stepView.setStepsNumber(3);
        stepView.go(2, false);

        /* set on click-Listener */
        btnBack.setOnClickListener(this);
        btnSignIn.setOnClickListener(this);
        logInInLink.setOnClickListener(this);

        return view;
    }

    /* onClick Listener */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                /* read inputs */
                String input_password1 = password1.getText().toString();
                String input_password2 = password2.getText().toString();
                /*create bundle*/
                Bundle bundleSignIn_1_and_2_and_3 = new Bundle();
                bundleSignIn_1_and_2_and_3.putString("firstName", firstName);
                bundleSignIn_1_and_2_and_3.putString("lastName", lastName);
                bundleSignIn_1_and_2_and_3.putString("email", email);
                bundleSignIn_1_and_2_and_3.putString("password1", input_password1);
                bundleSignIn_1_and_2_and_3.putString("password2", input_password2);

                SignInFragment_2 signInFragment_2 = new SignInFragment_2();
                signInFragment_2.setArguments(bundleSignIn_1_and_2_and_3);

                /* show next page */
                fragTransaction = getFragmentManager().beginTransaction();
                fragTransaction.replace(R.id.mainFrame, signInFragment_2,
                        getResources().getString(R.string.fSignIn_2));
                fragTransaction.commit();

                break;
            case R.id.btn_signin:
                signin();
                break;
            case R.id.link_login:
                fragTransaction = getFragmentManager().beginTransaction();
                fragTransaction.replace(R.id.mainFrame, new LogInFragment(),
                        getResources().getString(R.string.fLogIn));
                fragTransaction.commit();
                break;
        }
    }

    public void signin() {

        /* read inputs */
        String input_password1 = password1.getText().toString();
        String input_password2 = password2.getText().toString();
        btnSignIn.setEnabled(false);


        /* if passwords are the same and inputs validate */
        if (input_password1.equals(input_password2)) {
            if (validate()) {
                btnSignIn.setEnabled(false);

                final ProgressDialog progressDialog = new ProgressDialog(getContext(),
                        R.style.AppTheme_Dark_Dialog);
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Account wird erstellt...");
                progressDialog.show();


                // TODO: Implement your own signup logic here.

                new android.os.Handler().postDelayed(
                        new Runnable() {
                            public void run() {
                                progressDialog.dismiss();

                                /* send inputs to server */
                                Retrofit retrofit = APIConnector.getRetrofit();
                                APIClient apiInterface = retrofit.create(APIClient.class);
                                String base = email + ":" + password1;

                                // TODO hashsalt Password
                                /* start a call */
                                String authString = "Basic " + Base64.encodeToString(base.getBytes(), Base64.NO_WRAP);
                                Call<String> call = apiInterface.getUser(authString);

                                call.enqueue(new Callback<String>() {

                                    @Override
                                    public void onResponse(Call<String> call, Response<String> response) {
                                        try {
                                            Log.d(getResources().getString(R.string.app_name) + "-SigninConnection", response.body());

                                            /* open activity if login success*/
                                            if (response.body().equals("0")) {
                                                Intent intent = new Intent(getContext(), MainActivity.class);
                                                startActivity(intent);
                                            } else {

                                                /* set errror message */
                                                //  messageBox.setVisibility(View.VISIBLE);
                                                //  messageBox.setText("FEHLER!");
                                                new android.os.Handler().postDelayed(
                                                        new Runnable() {
                                                            public void run() {
                                                                // messageBox.setVisibility(View.GONE);
                                                            }
                                                        }, 7000);
                                                btnSignIn.setEnabled(true);
                                            }
                                        } catch (Exception e) {

                                            /* show server error message to user */
                                            Log.d(getResources().getString(R.string.app_name) + "-SiginConnection", "Server Error: " + response.raw().message());
                                            //  messageBoxInfo.setVisibility(View.VISIBLE);
                                            // messageBoxInfo.setText(" Es tut uns leid, leider ist ein Server Fehler aufgetreten. Versuchen Sie es später nochmal...");

                                            // messageBox.setVisibility(View.VISIBLE);
                                            //  messageBox.setText(response.raw().message());
                                            new android.os.Handler().postDelayed(
                                                    new Runnable() {
                                                        public void run() {
                                                            //  messageBox.setVisibility(View.GONE);
                                                            //  messageBoxInfo.setVisibility(View.GONE);
                                                        }
                                                    }, 10000);
                                            btnSignIn.setEnabled(true);
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<String> call, Throwable t) {
                                        call.cancel();
                                    }
                                });
                            }
                        }, 3000);
            } else {
                return;
            }

        } else {
            Toast.makeText(MainActivity.getInstance().getApplicationContext(), getResources().getString(R.string.tErrorPasswordNotIdentical), Toast.LENGTH_SHORT).show();
        }
    }

    public boolean validate() {
        boolean valid = true;

        /* read inputs */
        String input_password1 = password1.getText().toString();

        /* validate password */
        Pattern pattern2 = Pattern.compile(getResources().getString(R.string.rPassword));
        Matcher matcher2 = pattern2.matcher(input_password1);

        if (!matcher2.matches()) {
            password1.setError(getResources().getString(R.string.errorMsgPassword));
            password2.setError(getResources().getString(R.string.errorMsgPassword));
            Toast.makeText(StartActivity.getInstance().getApplicationContext(), getResources().getString(R.string.tErrorPassword), Toast.LENGTH_SHORT).show();
            valid = false;
        } else {
            password1.setError(null);
            password2.setError(null);
        }
        return valid;
    }
}

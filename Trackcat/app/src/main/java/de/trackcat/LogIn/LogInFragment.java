package de.trackcat.LogIn;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.trackcat.MainActivity;
import de.trackcat.R;
import de.trackcat.StartActivity;

public class LogInFragment extends Fragment {

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

        emailTextView =  view.findViewById(R.id.input_email);
        passwordTextView = view.findViewById(R.id.input_password);
        btnLogin= view.findViewById(R.id.btn_login);
        signInLink= view.findViewById(R.id.link_signup);
        messageBox= view.findViewById(R.id.messageBox);

        /* Login EventListener */
        btnLogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });

        /* SignIn Link EventListener */
        signInLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                fragTransaction = getFragmentManager().beginTransaction();
                fragTransaction.replace(R.id.mainFrame, new SignInFragment(),
                        getResources().getString(R.string.fSignIn));
                fragTransaction.commit();
            }
        });

        return view;
    }

    /* Function to Login */
    public void login() {

        if (!validate()) {
            onLoginFailed();
            return;
        }

        btnLogin.setEnabled(false);

        /* set wait field */
        final ProgressDialog progressDialog = new ProgressDialog(getContext(),
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Anmeldung...");
        progressDialog.show();

        /* read the inputs */
        String email = emailTextView.getText().toString();
        String password = passwordTextView.getText().toString();

        // TODO: Implement your own authentication logic here.
        /* open activity*/
        Intent intent = new Intent(getContext(), MainActivity.class);
        startActivity(intent);

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onLoginSuccess or onLoginFailed
                        onLoginSuccess();
                        // onLoginFailed();
                        progressDialog.dismiss();
                    }
                }, 3000);
    }

    /* @Override
   protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {

                // TODO: Implement successful signup logic here
                // By default we just finish the Activity and log them in automatically
                this.finish();
            }
        }
    }*/

    /* Function, if login success */
    public void onLoginSuccess() {
        btnLogin.setEnabled(true);

    }

    /* Function, if login failed */
    public void onLoginFailed() {
        // TODO: Implement correct message
        messageBox.setText("FEHLER!");
        btnLogin.setEnabled(true);
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
        }else{
            emailTextView.setError(null);
        }

        /* validate password */
        Pattern pattern2 = Pattern.compile(getResources().getString(R.string.rPassword));
        Matcher matcher2 = pattern2.matcher(password);

        if (!matcher2.matches()) {
            passwordTextView.setError(getResources().getString(R.string.errorMsgPassword));
            Toast.makeText(StartActivity.getInstance().getApplicationContext(),  getResources().getString(R.string.tErrorPassword), Toast.LENGTH_SHORT).show();
            valid = false;
        }else{
            passwordTextView.setError(null);
        }

        return valid;
    }
}
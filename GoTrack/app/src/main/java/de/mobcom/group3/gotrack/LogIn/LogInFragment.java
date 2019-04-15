package de.mobcom.group3.gotrack.LogIn;

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
import de.mobcom.group3.gotrack.MainActivity;
import de.mobcom.group3.gotrack.R;

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
           /*     fragTransaction = getChildFragmentManager().beginTransaction();
                fragTransaction.replace(R.id.mainFrame, new SignInFragment(),
                        getResources().getString(R.string.fSignIn));
                fragTransaction.commit();*/
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

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailTextView.setError("Geben Sie eine gültige E-mail Adresse ein");
            valid = false;
        } else {
            emailTextView.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            passwordTextView.setError("Passwort muss zwischen 4 und 10 alphanumeric characters");
            valid = false;
        } else {
            passwordTextView.setError(null);
        }

        return valid;
    }
}

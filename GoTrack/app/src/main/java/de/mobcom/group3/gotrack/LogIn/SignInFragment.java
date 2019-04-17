package de.mobcom.group3.gotrack.LogIn;

import android.app.ProgressDialog;
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

import de.mobcom.group3.gotrack.MainActivity;
import de.mobcom.group3.gotrack.R;

public class SignInFragment extends Fragment {

    private FragmentTransaction fragTransaction;
    /* UI references */
    EditText email, firstName, lastName, password1, password2;
    Button btnSignIn;
    TextView logInInLink;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_signin, container, false);

        btnSignIn = view.findViewById(R.id.btn_signin);
        logInInLink = view.findViewById(R.id.link_login);
        email = view.findViewById(R.id.input_email);
        firstName = view.findViewById(R.id.input_firstName);
        lastName = view.findViewById(R.id.input_lastName);
        password1 = view.findViewById(R.id.input_password1);
        password2 = view.findViewById(R.id.input_password2);

        /* SignIn EventListener */
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signin();
            }
        });

        /* LogIn Link EventListener */
        logInInLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragTransaction = getFragmentManager().beginTransaction();
                fragTransaction.replace(R.id.mainFrame, new LogInFragment(),
                        getResources().getString(R.string.fLogIn));
                fragTransaction.commit();
            }
        });

        return view;
    }


    public void signin() {

        /* read inputs */
        String input_firstName = firstName.getText().toString();
        String input_lastName = lastName.getText().toString();
        String input_email = email.getText().toString();
        String input_password1 = password1.getText().toString();
        String input_password2 = password2.getText().toString();

        /* check if all fields filled */
        if (!input_firstName.isEmpty() && !input_lastName.isEmpty() && !input_email.isEmpty() && !input_password1.isEmpty() && !input_password2.isEmpty()) {

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
                                    // On complete call either onSignupSuccess or onSignupFailed
                                    // depending on success
                                    //   onSignupSuccess();
                                    // onSignupFailed();
                                    progressDialog.dismiss();
                                }
                            }, 3000);
                }

            } else {
                Toast.makeText(MainActivity.getInstance().getApplicationContext(), "Ihre Passwörter stimmen nicht überein.", Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(MainActivity.getInstance().getApplicationContext(), "Bitte füllen Sie alle Felder aus.", Toast.LENGTH_SHORT).show();
        }


    }

    public boolean validate() {
        boolean valid = true;
        int nameLength = 15;

        /* read inputs */
        String input_firstName = firstName.getText().toString();
        String input_lastName = lastName.getText().toString();
        String input_email = email.getText().toString();
        String input_password1 = password1.getText().toString();

        /* validate name */
        if ((input_firstName + " " + input_lastName).length() > nameLength) {
            firstName.setError("Ihr kompletter Name darf nicht länger als max. " + nameLength + " + Zeichen sein.");
            lastName.setError("Ihr kompletter Name darf nicht länger als max. " + nameLength + " + Zeichen sein.");
            Toast.makeText(MainActivity.getInstance().getApplicationContext(), "Ihr Name ist zu lang.", Toast.LENGTH_SHORT).show();
            valid = false;
        }

        /* validate email */
        String regrex = "^\\w+([\\.-]?\\w+)*@\\w+([\\.-]?\\w+)*(\\.\\w{2,3})+$";
        Pattern pattern = Pattern.compile(regrex);
        Matcher matcher = pattern.matcher(input_email);

        if (!matcher.matches()) {
            email.setError("Ihre Email Adresse entsrpicht nicht dem Standard-Email Format.");
            Toast.makeText(MainActivity.getInstance().getApplicationContext(), "Ihre E-Mail Adresse ist nicht konform!", Toast.LENGTH_SHORT).show();
            valid = false;
        }

        /* validate password */
        String passwordErrorMsg = "Das Passwort muss zwischen 8 und 15 Zeichen lang sein und mindestens folgende Parameter enthalten: 1x Groß- und Kleinbuchstabe, 1x Zahl und 1x Sonderzeichen";
        String regrex2 = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[^a-zA-Z0-9])(?!.*\\s).{8,15}$";
        Pattern pattern2 = Pattern.compile(regrex2);
        Matcher matcher2 = pattern2.matcher(input_password1);

        if (!matcher2.matches()) {
            password1.setError(passwordErrorMsg);
            password2.setError(passwordErrorMsg);
            Toast.makeText(MainActivity.getInstance().getApplicationContext(), "Ihr Passwort ist nicht konform!", Toast.LENGTH_SHORT).show();
            valid = false;
        }

        return valid;
    }
}

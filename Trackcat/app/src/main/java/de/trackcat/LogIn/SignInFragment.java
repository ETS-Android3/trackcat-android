package de.trackcat.LogIn;

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

import de.trackcat.MainActivity;
import de.trackcat.R;
import de.trackcat.StartActivity;

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
                }else{
                    return;
                }

            } else {
                Toast.makeText(MainActivity.getInstance().getApplicationContext(), getResources().getString(R.string.tErrorPasswordNotIdentical), Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(MainActivity.getInstance().getApplicationContext(), getResources().getString(R.string.tFillAllFields), Toast.LENGTH_SHORT).show();
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

        /* validate firstName */
        Pattern pattern3 = Pattern.compile(getResources().getString(R.string.rName));
        Matcher matcher3 = pattern3.matcher(input_firstName);
        if (!matcher3.matches()) {
            firstName.setError(getResources().getString(R.string.errorMsgName));
            Toast.makeText(StartActivity.getInstance().getApplicationContext(), getResources().getString(R.string.tErrorName), Toast.LENGTH_SHORT).show();
            valid = false;
        }else{
            firstName.setError(null);
        }

        /* validate lastName */
        Pattern pattern4 = Pattern.compile(getResources().getString(R.string.rName));
        Matcher matcher4 = pattern4.matcher(input_lastName);
        if (!matcher4.matches()) {
            lastName.setError(getResources().getString(R.string.errorMsgName));
            Toast.makeText(StartActivity.getInstance().getApplicationContext(), getResources().getString(R.string.tErrorName), Toast.LENGTH_SHORT).show();
            valid = false;
        }else{
            lastName.setError(null);
        }

        /* validate email */
        Pattern pattern = Pattern.compile(getResources().getString(R.string.rEmail));
        Matcher matcher = pattern.matcher(input_email);

        if (!matcher.matches()) {
            email.setError(getResources().getString(R.string.errorMsgEMail));
            Toast.makeText(StartActivity.getInstance().getApplicationContext(), getResources().getString(R.string.tErrorEmail), Toast.LENGTH_SHORT).show();
            valid = false;
        }else{
            email.setError(null);
        }

        /* validate password */
        Pattern pattern2 = Pattern.compile(getResources().getString(R.string.rPassword));
        Matcher matcher2 = pattern2.matcher(input_password1);

        if (!matcher2.matches()) {
            password1.setError(getResources().getString(R.string.errorMsgPassword));
            password2.setError(getResources().getString(R.string.errorMsgPassword));
            Toast.makeText(StartActivity.getInstance().getApplicationContext(),  getResources().getString(R.string.tErrorPassword), Toast.LENGTH_SHORT).show();
            valid = false;
        }else{
            password1.setError(null);
            password2.setError(null);
        }
        return valid;
    }
}

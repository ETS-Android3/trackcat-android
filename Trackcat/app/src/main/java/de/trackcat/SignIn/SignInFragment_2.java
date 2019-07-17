package de.trackcat.SignIn;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import de.trackcat.GlobalFunctions;
import de.trackcat.LogIn.LogInFragment;
import de.trackcat.R;
import de.trackcat.StartActivity;

public class SignInFragment_2 extends Fragment implements View.OnClickListener {

    private FragmentTransaction fragTransaction;
    /* UI references */
    EditText email;
    ImageView btnBack, btnNext;
    TextView logInInLink;
    String firstName, lastName, password1, password2;
    Boolean generalTerm, dataProtection;
    private com.shuhart.stepview.StepView stepView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_signin_2, container, false);

        /* get references */
        btnBack = view.findViewById(R.id.btn_back);
        btnNext = view.findViewById(R.id.btn_next);
        logInInLink = view.findViewById(R.id.link_login);
        email = view.findViewById(R.id.input_email);

        /* get bundle */
        if (getArguments() != null) {
            firstName = getArguments().getString("firstName");
            lastName = getArguments().getString("lastName");
            password1 = getArguments().getString("password1");
            password2 = getArguments().getString("password2");
            generalTerm = getArguments().getBoolean("generalTerms");
            dataProtection = getArguments().getBoolean("dataProtection");
            if (getArguments().getString("email") != null) {
                email.setText(getArguments().getString("email"));
            }
        }

        /* stepview */
        stepView = view.findViewById(R.id.step_view);
        stepView.go(1, false);
        btnBack.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        logInInLink.setOnClickListener(this);

        return view;
    }

    /* onClick Listener */
    @Override
    public void onClick(View v) {
        Bundle bundleSignIn_1_and_2;
        switch (v.getId()) {

            case R.id.btn_back:
                /* read inputs */
                String input_email_back = email.getText().toString();
                /*create bundle*/
                bundleSignIn_1_and_2 = new Bundle();
                bundleSignIn_1_and_2.putString("firstName", firstName);
                bundleSignIn_1_and_2.putString("lastName", lastName);
                bundleSignIn_1_and_2.putString("email", input_email_back);
                bundleSignIn_1_and_2.putString("password1", password1);
                bundleSignIn_1_and_2.putString("password2", password2);
                bundleSignIn_1_and_2.putBoolean("generalTerms", generalTerm);
                bundleSignIn_1_and_2.putBoolean("dataProtection", dataProtection);

                SignInFragment_1 signInFragment_1 = new SignInFragment_1();
                signInFragment_1.setArguments(bundleSignIn_1_and_2);

                /* show next page */
                fragTransaction = getFragmentManager().beginTransaction();
                fragTransaction.replace(R.id.mainFrame, signInFragment_1,
                        getResources().getString(R.string.fSignIn_1));
                fragTransaction.commit();
                break;
            case R.id.btn_next:
                /* read inputs */
                String input_email_next = email.getText().toString();
                if (GlobalFunctions.validateEMail(email, StartActivity.getInstance())) {
                    /*create bundle*/
                    bundleSignIn_1_and_2 = new Bundle();
                    bundleSignIn_1_and_2.putString("firstName", firstName);
                    bundleSignIn_1_and_2.putString("lastName", lastName);
                    bundleSignIn_1_and_2.putString("email", input_email_next);
                    bundleSignIn_1_and_2.putString("password1", password1);
                    bundleSignIn_1_and_2.putString("password2", password2);
                    bundleSignIn_1_and_2.putBoolean("generalTerms", generalTerm);
                    bundleSignIn_1_and_2.putBoolean("dataProtection", dataProtection);

                    SignInFragment_3 signInFragment_3 = new SignInFragment_3();
                    signInFragment_3.setArguments(bundleSignIn_1_and_2);

                    /* show next page */
                    fragTransaction = getFragmentManager().beginTransaction();
                    fragTransaction.replace(R.id.mainFrame, signInFragment_3,
                            getResources().getString(R.string.fSignIn_3));
                    fragTransaction.commit();
                }

                break;
            case R.id.link_login:
                fragTransaction = getFragmentManager().beginTransaction();
                fragTransaction.replace(R.id.mainFrame, new LogInFragment(),
                        getResources().getString(R.string.fLogIn));
                fragTransaction.commit();
                break;
        }
    }
}

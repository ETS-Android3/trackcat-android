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
import android.widget.Toast;

import de.trackcat.GlobalFunctions;
import de.trackcat.LogIn.LogInFragment;
import de.trackcat.MainActivity;
import de.trackcat.R;
import de.trackcat.StartActivity;

public class SignInFragment_3 extends Fragment implements View.OnClickListener {

    private FragmentTransaction fragTransaction;
    /* UI references */
    EditText password1, password2;
    ImageView btnBack, btnNext;
    TextView logInInLink, messageBox, messageBoxInfo;
    String firstName, lastName, email;
    Boolean generalTerm, dataProtection;
    int gender;
    private com.shuhart.stepview.StepView stepView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_signin_3, container, false);

        btnBack = view.findViewById(R.id.btn_back);
        btnNext = view.findViewById(R.id.btn_next);
        logInInLink = view.findViewById(R.id.link_login);
        password1 = view.findViewById(R.id.input_password1);
        password2 = view.findViewById(R.id.input_password2);
        messageBox = view.findViewById(R.id.messageBox);
        messageBoxInfo = view.findViewById(R.id.messageBoxInfo);

        /* get bundle */
        if (getArguments() != null) {
            firstName = getArguments().getString("firstName");
            lastName = getArguments().getString("lastName");
            gender= getArguments().getInt("gender");
            email = getArguments().getString("email");
            generalTerm = getArguments().getBoolean("generalTerms");
            dataProtection = getArguments().getBoolean("dataProtection");

            if (getArguments().getString("password1") != null) {
                password1.setText(getArguments().getString("password1"));
            }
            if (getArguments().getString("password2") != null) {
                password2.setText(getArguments().getString("password2"));
            }
        }

        /* step view */
        stepView = view.findViewById(R.id.step_view);
        stepView.go(2, false);

        /* set on click-Listener */
        btnBack.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        logInInLink.setOnClickListener(this);

        return view;
    }

    /* onClick Listener */
    @Override
    public void onClick(View v) {
        Bundle bundleSignIn_1_and_2_and_3 = new Bundle();
        switch (v.getId()) {
            case R.id.btn_back:

                /*create bundle*/
                bundleSignIn_1_and_2_and_3.putString("firstName", firstName);
                bundleSignIn_1_and_2_and_3.putString("lastName", lastName);
                bundleSignIn_1_and_2_and_3.putInt("gender", gender);
                bundleSignIn_1_and_2_and_3.putString("email", email);
                bundleSignIn_1_and_2_and_3.putString("password1", password1.getText().toString());
                bundleSignIn_1_and_2_and_3.putString("password2", password2.getText().toString());
                bundleSignIn_1_and_2_and_3.putBoolean("generalTerms", generalTerm);
                bundleSignIn_1_and_2_and_3.putBoolean("dataProtection", dataProtection);

                SignInFragment_2 signInFragment_2 = new SignInFragment_2();
                signInFragment_2.setArguments(bundleSignIn_1_and_2_and_3);

                /* show next page */
                fragTransaction = getFragmentManager().beginTransaction();
                fragTransaction.replace(R.id.mainFrame, signInFragment_2,
                        getResources().getString(R.string.fSignIn_2));
                fragTransaction.commit();
                break;

            case R.id.btn_next:

                /* read inputs */
                String input_password1 = password1.getText().toString();
                String input_password2 = password2.getText().toString();
                if (input_password1.equals(input_password2)) {
                    if (GlobalFunctions.validateTwoPassword(password1, password2, StartActivity.getInstance())) {

                        /*create bundle*/
                        bundleSignIn_1_and_2_and_3.putString("firstName", firstName);
                        bundleSignIn_1_and_2_and_3.putString("lastName", lastName);
                        bundleSignIn_1_and_2_and_3.putInt("gender", gender);
                        bundleSignIn_1_and_2_and_3.putString("email", email);
                        bundleSignIn_1_and_2_and_3.putString("password1", input_password1);
                        bundleSignIn_1_and_2_and_3.putString("password2", input_password2);
                        bundleSignIn_1_and_2_and_3.putBoolean("generalTerms", generalTerm);
                        bundleSignIn_1_and_2_and_3.putBoolean("dataProtection", dataProtection);

                        SignInFragment_4 signInFragment_4 = new SignInFragment_4();
                        signInFragment_4.setArguments(bundleSignIn_1_and_2_and_3);

                        /* show next page */
                        fragTransaction = getFragmentManager().beginTransaction();
                        fragTransaction.replace(R.id.mainFrame, signInFragment_4,
                                getResources().getString(R.string.fSignIn_4));
                        fragTransaction.commit();
                    }
                } else {
                    Toast.makeText(StartActivity.getInstance().getApplicationContext(), getResources().getString(R.string.tErrorPasswordNotIdentical), Toast.LENGTH_SHORT).show();
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

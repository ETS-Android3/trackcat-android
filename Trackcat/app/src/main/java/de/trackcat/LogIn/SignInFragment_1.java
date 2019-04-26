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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.trackcat.APIClient;
import de.trackcat.APIConnector;
import de.trackcat.Charts.BarChartFragment;
import de.trackcat.MainActivity;
import de.trackcat.R;
import de.trackcat.StartActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SignInFragment_1 extends Fragment implements View.OnClickListener {

    private FragmentTransaction fragTransaction;
    /* UI references */
    EditText  firstName, lastName;
    ImageView btnNext;
    TextView logInInLink;
    String first_Name, last_Name, email, password1, password2;
    com.shuhart.stepview.StepView stepView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_signin_1, container, false);

        /*get references */
        btnNext = view.findViewById(R.id.btn_next);
        logInInLink = view.findViewById(R.id.link_login);
        firstName = view.findViewById(R.id.input_firstName);
        lastName = view.findViewById(R.id.input_lastName);

        /* get bundle */
        if (getArguments() != null) {
            first_Name = getArguments().getString("firstName");
            firstName.setText(first_Name);
            last_Name = getArguments().getString("lastName");
            lastName.setText(last_Name);
            email = getArguments().getString("email");
            password1 = getArguments().getString("password1");
            password2 = getArguments().getString("password2");
        }

        stepView=view.findViewById(R.id.step_view);
        stepView.setStepsNumber(3);

        /* set on click-Listener */
        btnNext.setOnClickListener(this);
        logInInLink.setOnClickListener(this);

        return view;
    }

    /* onClick Listener */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_next:
                /* read inputs */
                String input_firstName = firstName.getText().toString();
                String input_lastName = lastName.getText().toString();
                if (validate()) {
                    /*create bundle*/
                    Bundle bundleSignIn_1 = new Bundle();
                    bundleSignIn_1.putString("firstName",input_firstName);
                    bundleSignIn_1.putString("lastName", input_lastName);
                    bundleSignIn_1.putString("email", email);
                    bundleSignIn_1.putString("password1", password1);
                    bundleSignIn_1.putString("password2", password2);

                    SignInFragment_2 signInFragment_2 = new SignInFragment_2();
                    signInFragment_2.setArguments(bundleSignIn_1);

                    /* show next page */
                    fragTransaction = getFragmentManager().beginTransaction();
                    fragTransaction.replace(R.id.mainFrame, signInFragment_2,
                            getResources().getString(R.string.fSignIn_2));
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

    public boolean validate() {
        boolean valid = true;

        /* read inputs */
        String input_firstName = firstName.getText().toString();
        String input_lastName = lastName.getText().toString();

        /* validate firstName */
        Pattern pattern3 = Pattern.compile(getResources().getString(R.string.rName));
        Matcher matcher3 = pattern3.matcher(input_firstName);
        if (!matcher3.matches()) {
            firstName.setError(getResources().getString(R.string.errorMsgName));
            Toast.makeText(StartActivity.getInstance().getApplicationContext(), getResources().getString(R.string.tErrorName), Toast.LENGTH_SHORT).show();
            valid = false;
        } else {
            firstName.setError(null);
        }

        /* validate lastName */
        Pattern pattern4 = Pattern.compile(getResources().getString(R.string.rName));
        Matcher matcher4 = pattern4.matcher(input_lastName);
        if (!matcher4.matches()) {
            lastName.setError(getResources().getString(R.string.errorMsgName));
            Toast.makeText(StartActivity.getInstance().getApplicationContext(), getResources().getString(R.string.tErrorName), Toast.LENGTH_SHORT).show();
            valid = false;
        } else {
            lastName.setError(null);
        }
        return valid;
    }
}

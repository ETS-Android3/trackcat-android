package de.trackcat.SignIn;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

import de.trackcat.GlobalFunctions;
import de.trackcat.LogIn.LogInFragment;
import de.trackcat.MainActivity;
import de.trackcat.R;
import de.trackcat.StartActivity;

public class SignInFragment_2 extends Fragment implements View.OnClickListener {

    private FragmentTransaction fragTransaction;
    /* UI references */
    EditText email, day_Of_Birth;
    ImageView btnBack, btnNext;
    TextView logInInLink;
    String firstName, lastName, password1, password2, dayOfBirth;
    Boolean generalTerm, dataProtection;
    int gender;
    private com.shuhart.stepview.StepView stepView;
    DatePickerDialog picker;
    AlertDialog.Builder alert;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_signin_2, container, false);

        /* get references */
        btnBack = view.findViewById(R.id.btn_back);
        btnNext = view.findViewById(R.id.btn_next);
        logInInLink = view.findViewById(R.id.link_login);
        email = view.findViewById(R.id.input_email);
        day_Of_Birth=view.findViewById(R.id.input_dayOfBirth);
        day_Of_Birth.setKeyListener(null);

        /* get bundle */
        if (getArguments() != null) {
            firstName = getArguments().getString("firstName");
            lastName = getArguments().getString("lastName");
            gender= getArguments().getInt("gender");
            password1 = getArguments().getString("password1");
            password2 = getArguments().getString("password2");
            generalTerm = getArguments().getBoolean("generalTerms");
            dataProtection = getArguments().getBoolean("dataProtection");
            if (getArguments().getString("email") != null) {
                email.setText(getArguments().getString("email"));
            }
            if (getArguments().getString("dayOfBirth") != null) {
                day_Of_Birth.setText(getArguments().getString("dayOfBirth"));
            }
        }

        /* stepview */
        stepView = view.findViewById(R.id.step_view);
        stepView.go(1, false);
        btnBack.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        logInInLink.setOnClickListener(this);
        day_Of_Birth.setOnClickListener(this);

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
                String input_dayOfBirth_back= day_Of_Birth.getText().toString();
                /*create bundle*/
                bundleSignIn_1_and_2 = new Bundle();
                bundleSignIn_1_and_2.putString("firstName", firstName);
                bundleSignIn_1_and_2.putString("lastName", lastName);
                bundleSignIn_1_and_2.putInt("gender", gender);
                bundleSignIn_1_and_2.putString("email", input_email_back);
                bundleSignIn_1_and_2.putString("dayOfBirth", input_dayOfBirth_back);
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
                String input_dayOfBirth_next= day_Of_Birth.getText().toString();
                if (GlobalFunctions.validateEMail(email, StartActivity.getInstance())) {
                    /*create bundle*/
                    bundleSignIn_1_and_2 = new Bundle();
                    bundleSignIn_1_and_2.putString("firstName", firstName);
                    bundleSignIn_1_and_2.putString("lastName", lastName);
                    bundleSignIn_1_and_2.putInt("gender", gender);
                    bundleSignIn_1_and_2.putString("email", input_email_next);
                    bundleSignIn_1_and_2.putString("dayOfBirth", input_dayOfBirth_next);
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
            case R.id.input_dayOfBirth:
                int day, month, year;
                /*if no inputs, choose current date*/
                if (day_Of_Birth.getText().toString().equals(getResources().getString(R.string.noInformation))) {
                    final Calendar cldr = Calendar.getInstance();
                    day = cldr.get(Calendar.DAY_OF_MONTH);
                    month = cldr.get(Calendar.MONTH);
                    year = cldr.get(Calendar.YEAR);
                } else {
                    /* get old values */
                    String[] dayOfBirthValues = day_Of_Birth.getText().toString().split("\\.");

                    /* set datepicker an set value in field */
                    day = Integer.parseInt(dayOfBirthValues[0]);
                    month = Integer.parseInt(dayOfBirthValues[1]) - 1;
                    year = Integer.parseInt(dayOfBirthValues[2]);
                }

                picker = new DatePickerDialog(getContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                /* get current date */
                                final Calendar cldr = Calendar.getInstance();
                                int currentDay = cldr.get(Calendar.DAY_OF_MONTH);
                                int currentMonth = cldr.get(Calendar.MONTH) + 1;
                                int currentYear = cldr.get(Calendar.YEAR);

                                /* check if dayOfBirth in future */
                                if (year > currentYear) {
                                    if (MainActivity.getHints()) {
                                        Toast.makeText(getContext(), getResources().getString(R.string.birthdayInFuture), Toast.LENGTH_SHORT).show();
                                    }
                                    return;
                                }
                                if (currentMonth == (monthOfYear + 1) && currentYear == year) {
                                    if (dayOfMonth > currentDay) {
                                        if (MainActivity.getHints()) {
                                            Toast.makeText(getContext(), getResources().getString(R.string.birthdayInFuture), Toast.LENGTH_SHORT).show();
                                        }
                                        return;
                                    }
                                }

                                String month = "" + (monthOfYear + 1);
                                String day = "" + dayOfMonth;
                                if (monthOfYear + 1 < 10) {
                                    month = "0" + month;
                                }
                                if (dayOfMonth < 10) {
                                    day = "0" + day;
                                }
                                day_Of_Birth.setText(day + "." + month + "." + year);
                            }
                        }, year, month, day);
                picker.show();
                break;
        }
    }
}

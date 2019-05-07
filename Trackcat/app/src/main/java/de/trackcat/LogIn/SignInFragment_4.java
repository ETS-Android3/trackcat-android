package de.trackcat.LogIn;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONObject;
import java.util.HashMap;
import de.trackcat.APIClient;
import de.trackcat.APIConnector;
import de.trackcat.R;
import de.trackcat.StartActivity;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SignInFragment_4 extends Fragment implements View.OnClickListener {

    private FragmentTransaction fragTransaction;

    /* UI references */
    CheckBox generalTerm, dataProtection;
    ImageView btnBack;
    Button btnSignIn;
    TextView logInInLink, messageBox, messageBoxInfo, link_termsOfService, link_dataProtection, text_dataProtection;
    String firstName, lastName, email, password1, password2;
    Boolean checkGeneralTerm, checkDataProtection = false;
    private com.shuhart.stepview.StepView stepView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        /* get views */
        View view = inflater.inflate(R.layout.fragment_signin_4, container, false);
        generalTerm = view.findViewById(R.id.checkBox_generalTerm);
        link_termsOfService= view.findViewById(R.id.link_termsOfService);
        dataProtection = view.findViewById(R.id.checkBox_dataProtection);
        link_dataProtection= view.findViewById(R.id.link_dataProtection);
        btnBack = view.findViewById(R.id.btn_back);
        btnSignIn = view.findViewById(R.id.btn_signin);
        logInInLink = view.findViewById(R.id.link_login);
        messageBox = view.findViewById(R.id.messageBox);
        messageBoxInfo = view.findViewById(R.id.messageBoxInfo);

        /* get bundle */
        if (getArguments() != null) {
            firstName = getArguments().getString("firstName");
            lastName = getArguments().getString("lastName");
            email = getArguments().getString("email");
            password1 = getArguments().getString("password1");
            password2 = getArguments().getString("password2");
            generalTerm.setChecked(getArguments().getBoolean("generalTerms"));
            dataProtection.setChecked(getArguments().getBoolean("dataProtection"));
            checkGeneralTerm = getArguments().getBoolean("generalTerms");
            checkDataProtection = getArguments().getBoolean("dataProtection");
        }

        /* step view */
        stepView = view.findViewById(R.id.step_view);
        stepView.go(3, false);

        /* set on click-Listener */
        btnBack.setOnClickListener(this);
        btnSignIn.setOnClickListener(this);
        logInInLink.setOnClickListener(this);
        generalTerm.setOnClickListener(this);
        dataProtection.setOnClickListener(this);

        /* set button enable if necessary */
        if (checkGeneralTerm && checkDataProtection) {
            btnSignIn.setEnabled(true);
            btnSignIn.setBackgroundColor(getResources().getColor(R.color.colorGreenAccent));
        }

        /* set link */
        link_termsOfService.setClickable(true);
        link_termsOfService.setMovementMethod(LinkMovementMethod.getInstance());
        String text1 = "<a href='"+getString(R.string.link_termsOfService)+"'> AGBs</a>  gelesen und akzeptiert";
        link_termsOfService.setText(Html.fromHtml(text1));

        link_dataProtection.setClickable(true);
        link_dataProtection.setMovementMethod(LinkMovementMethod.getInstance());
        String text2 = "<a href='"+getString(R.string.link_dataProtection)+"'> Datenschutzerklärung</a>  gelesen und akzeptiert";
        link_dataProtection.setText(Html.fromHtml(text2));

        return view;
    }

    /* onClick Listener */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:

                /*create bundle*/
                Bundle bundleSignIn_1_and_2_and_3_and_4 = new Bundle();
                bundleSignIn_1_and_2_and_3_and_4.putString("firstName", firstName);
                bundleSignIn_1_and_2_and_3_and_4.putString("lastName", lastName);
                bundleSignIn_1_and_2_and_3_and_4.putString("email", email);
                bundleSignIn_1_and_2_and_3_and_4.putBoolean("generalTerms", generalTerm.isChecked());
                bundleSignIn_1_and_2_and_3_and_4.putBoolean("dataProtection", dataProtection.isChecked());

                SignInFragment_3 signInFragment_3 = new SignInFragment_3();
                signInFragment_3.setArguments(bundleSignIn_1_and_2_and_3_and_4);

                /* show next page */
                fragTransaction = getFragmentManager().beginTransaction();
                fragTransaction.replace(R.id.mainFrame, signInFragment_3,
                        getResources().getString(R.string.fSignIn_3));
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
            case R.id.checkBox_generalTerm:

                checkGeneralTerm = generalTerm.isChecked();

                /* change button */
                if (checkGeneralTerm && checkDataProtection) {
                    btnSignIn.setEnabled(true);
                    btnSignIn.setBackgroundColor(getResources().getColor(R.color.colorGreenAccent));
                } else {
                    btnSignIn.setEnabled(false);
                    btnSignIn.setBackgroundColor(getResources().getColor(R.color.colorAccentDisable));
                }
                break;
            case R.id.checkBox_dataProtection:

                checkDataProtection = dataProtection.isChecked();

                /* change button */
                if (checkGeneralTerm && checkDataProtection) {
                    btnSignIn.setEnabled(true);
                    btnSignIn.setBackgroundColor(getResources().getColor(R.color.colorGreenAccent));
                } else {
                    btnSignIn.setEnabled(false);
                    btnSignIn.setBackgroundColor(getResources().getColor(R.color.colorAccentDisable));
                }
                break;
        }
    }

    public void signin() {

        btnSignIn.setEnabled(false);
        btnSignIn.setBackgroundColor(getResources().getColor(R.color.colorAccentDisable));
        generalTerm.setError(null);
        dataProtection.setError(null);

        /* if generalTerm and dataProtection is checked*/
        if (generalTerm.isChecked() && dataProtection.isChecked()) {

            btnSignIn.setEnabled(true);

            final ProgressDialog progressDialog = new ProgressDialog(getContext(),
                    R.style.AppTheme_Dark_Dialog);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Account wird erstellt...");
            progressDialog.show();

            /* send inputs to server */
            Retrofit retrofit = APIConnector.getRetrofit();
            APIClient apiInterface = retrofit.create(APIClient.class);

            HashMap<String, String> map = new HashMap<>();
            map.put("firstName", firstName);
            map.put("lastName", lastName);
            map.put("email", email);
            map.put("password", password1);


            // TODO hashsalt Password
            /* start a call */
            Call<ResponseBody> call = apiInterface.registerUser(map);

            call.enqueue(new Callback<ResponseBody>() {

                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {

                        progressDialog.dismiss();
                        String jsonString = response.body().string();

                        Log.d(getResources().getString(R.string.app_name) + "-SigninConnection", jsonString);

                        JSONObject json = new JSONObject(jsonString);

                        /* open activity if login success*/
                        if (json.getString("success").equals("0")) {

                            /* load LogInFragment */
                            Bundle bundle = new Bundle();
                            bundle.putBoolean("singIn", true);
                            LogInFragment login = new LogInFragment();
                            login.setArguments(bundle);

                            fragTransaction = getFragmentManager().beginTransaction();
                            fragTransaction.replace(R.id.mainFrame, login,
                                    getResources().getString(R.string.fLogIn));
                            fragTransaction.commit();
                        } else if (json.getString("success").equals("1")) {

                            /* set errror message */
                            messageBoxInfo.setVisibility(View.VISIBLE);
                            messageBoxInfo.setText(" Es tut uns leid, leider ist ein Server Fehler aufgetreten. Versuchen Sie es später nochmal...");

                            messageBox.setVisibility(View.VISIBLE);
                            messageBox.setText("Serverfehler");
                            new android.os.Handler().postDelayed(
                                    new Runnable() {
                                        public void run() {
                                            messageBox.setVisibility(View.GONE);
                                            messageBox.setText("");
                                            messageBoxInfo.setVisibility(View.GONE);
                                            messageBoxInfo.setText("");
                                        }
                                    }, 10000);
                            btnSignIn.setBackgroundColor(getResources().getColor(R.color.colorGreenAccent));
                            btnSignIn.setEnabled(true);
                        } else if (json.getString("success").equals("3")) {

                            messageBoxInfo.setVisibility(View.VISIBLE);
                            messageBoxInfo.setText("Leider ist Ihre E-mail Adresse schon vergeben. Bitte wählen Sie eine andere.");

                            messageBox.setVisibility(View.VISIBLE);
                            messageBox.setText("Entschuldigung");
                            new android.os.Handler().postDelayed(
                                    new Runnable() {
                                        public void run() {
                                            messageBox.setVisibility(View.GONE);
                                            messageBox.setText("");
                                            messageBoxInfo.setVisibility(View.GONE);
                                            messageBoxInfo.setText("");
                                        }
                                    }, 10000);

                            btnSignIn.setBackgroundColor(getResources().getColor(R.color.colorGreenAccent));
                            btnSignIn.setEnabled(true);
                        }
                    } catch (Exception e) {

                        /* show server error message to user */
                        Log.d(getResources().getString(R.string.app_name) + "-SiginConnection", "Server Error: " + response.raw().message());
                        messageBoxInfo.setVisibility(View.VISIBLE);
                        messageBoxInfo.setText(" Es tut uns leid, leider ist ein Server Fehler aufgetreten. Versuchen Sie es später nochmal...");

                        messageBox.setVisibility(View.VISIBLE);
                        messageBox.setText(response.raw().message());
                        new android.os.Handler().postDelayed(
                                new Runnable() {
                                    public void run() {
                                        messageBox.setVisibility(View.GONE);
                                        messageBoxInfo.setVisibility(View.GONE);
                                    }
                                }, 10000);
                        btnSignIn.setEnabled(true);
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    call.cancel();

                    /* show server error message to user */
                    Log.d(getResources().getString(R.string.app_name) + "-SiginConnection", "Server Error: " + t.getMessage());
                    messageBoxInfo.setVisibility(View.VISIBLE);
                    messageBoxInfo.setText(" Es tut uns leid, leider ist ein Server Fehler aufgetreten. Versuchen Sie es später nochmal...");

                    messageBox.setVisibility(View.VISIBLE);
                    messageBox.setText(t.getMessage());
                    new android.os.Handler().postDelayed(
                            new Runnable() {
                                public void run() {
                                    messageBox.setVisibility(View.GONE);
                                    messageBoxInfo.setVisibility(View.GONE);
                                }
                            }, 10000);
                    btnSignIn.setEnabled(true);
                }
            });
            //  }
            //  }, 3000);


        } else {

            if (!generalTerm.isChecked()) {
                generalTerm.setError("Sie müssen die AGBs akzeptieren.");
                Toast.makeText(StartActivity.getInstance().getApplicationContext(), getResources().getString(R.string.tErrorHookGeneralTerm), Toast.LENGTH_SHORT).show();
            } else if (!dataProtection.isChecked()) {
                dataProtection.setError("Sie müssen die Datenschutzrichtlinien akzeptieren.");
                Toast.makeText(StartActivity.getInstance().getApplicationContext(), getResources().getString(R.string.tErrorHookDataProtection), Toast.LENGTH_SHORT).show();
            } else if (!generalTerm.isChecked() && !dataProtection.isChecked()) {
                generalTerm.setError("Sie müssen die AGBs akzeptieren.");
                dataProtection.setError("Sie müssen die Datenschutzrichtlinien akzeptieren.");
                Toast.makeText(StartActivity.getInstance().getApplicationContext(), getResources().getString(R.string.tErrorHook), Toast.LENGTH_SHORT).show();
            }


        }
    }
}

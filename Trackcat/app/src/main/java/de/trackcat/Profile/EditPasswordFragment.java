package de.trackcat.Profile;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.trackcat.MainActivity;
import de.trackcat.R;

public class EditPasswordFragment extends Fragment implements View.OnClickListener {

    EditText currentPassword, password1, password2;
    Button btnSave;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_edit_password, container, false);

        /* get fields */
        currentPassword = view.findViewById(R.id.input_currentPassword);
        password1 = view.findViewById(R.id.input_password1);
        password2 = view.findViewById(R.id.input_password2);
        btnSave = view.findViewById(R.id.btn_save);

        btnSave.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_save:

                /* Inputfelder auslesen */
                String input_currentPassword = currentPassword.getText().toString();
                String input_password1 = password1.getText().toString();
                String input_password2 = password2.getText().toString();

                /* check if all fields are filled and validate inputs*/
                if (!input_currentPassword.equals("") && !input_password1.equals("") && !input_password2.equals("")) {

                    /* check if passwords are equals */
                    if (input_password1.equals(input_password2)) {

                        /* validate password */
                        if (validate()) {
                            //TODO Nutzerandereungen an DB senden

                            /* UI-Meldung */
                            if (MainActivity.getHints()) {
                                Toast.makeText(getContext(),  getResources().getString(R.string.tSuccessChangePassword), Toast.LENGTH_LONG).show();
                            }
                        }
                    } else {
                        Toast.makeText(getContext(), getResources().getString(R.string.tErrorPasswordNotIdentical), Toast.LENGTH_LONG).show();
                    }


                } else {
                    if (MainActivity.getHints()) {
                        Toast.makeText(getContext(),  getResources().getString(R.string.tFillAllFields), Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }

    public boolean validate() {
        boolean valid = true;

        /* read inputs */
        String input_password = password1.getText().toString();

        /* validate password */
        String regrex="^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[^a-zA-Z0-9])(?!.*\\s).{8,15}$";
        Pattern pattern = Pattern.compile(regrex);
        Matcher matcher = pattern.matcher(input_password);

       if (!matcher.matches()) {
           password1.setError(getResources().getString(R.string.errorMsgPassword));
           password2.setError(getResources().getString(R.string.errorMsgPassword));
            Toast.makeText(MainActivity.getInstance().getApplicationContext(), getResources().getString(R.string.tErrorPassword), Toast.LENGTH_SHORT).show();
            valid = false;
        }

        return valid;
    }
}

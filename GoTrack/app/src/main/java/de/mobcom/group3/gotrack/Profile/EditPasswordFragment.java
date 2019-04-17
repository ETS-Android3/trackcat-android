package de.mobcom.group3.gotrack.Profile;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.mobcom.group3.gotrack.MainActivity;
import de.mobcom.group3.gotrack.R;

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

                /* check if all fields are filled and  validate inputs*/
                if (!input_currentPassword.equals("") && !input_password1.equals("") && !input_password2.equals("")) {

                    if (input_password1.equals(input_password2)) {
                        if (validate()) {
                            //TODO Nutzerandereungen an DB senden

                            /* UI-Meldung */
                            if (MainActivity.getHints()) {
                                Toast.makeText(getContext(), "Passwort wurde erfolgreich geändert!", Toast.LENGTH_LONG).show();
                            }
                        }
                    } else {
                        Toast.makeText(getContext(), "Passwörter stimmen nicht überein!", Toast.LENGTH_LONG).show();
                    }


                } else {
                    if (MainActivity.getHints()) {
                        Toast.makeText(getContext(), "Bitte alle Felder ausfüllen!", Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }

    public boolean validate() {
        boolean valid = true;
        String passwordErrorMsg="Das Passwort muss zwischen 8 und 15 Zeichen lang sein und mindestens folgende Parameter enthalten: 1x Groß- und Kleinbuchstabe, 1x Zahl und 1x Sonderzeichen";
        int nameLength = 15;
        /* read inputs */
        String input_password = password1.getText().toString();

        /* validate email */
        String regrex="^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[^a-zA-Z0-9])(?!.*\\s).{8,15}$";
        Pattern pattern = Pattern.compile(regrex);
        Matcher matcher = pattern.matcher(input_password);

        //String regrex2="^\\w+([\\.-]?\\w+)*@\\w+([\\.-]?\\w+)*(\\.\\w{2,3})+$";

       if (!matcher.matches()) {
           password1.setError(passwordErrorMsg);
           password2.setError(passwordErrorMsg);
            Toast.makeText(MainActivity.getInstance().getApplicationContext(), "Ihr Passwort ist nicht konform!", Toast.LENGTH_SHORT).show();
            valid = false;
        }

        return valid;
    }
}

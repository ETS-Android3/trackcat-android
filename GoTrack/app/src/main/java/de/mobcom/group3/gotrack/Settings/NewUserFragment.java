package de.mobcom.group3.gotrack.Settings;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import de.mobcom.group3.gotrack.Database.DAO.UserDAO;
import de.mobcom.group3.gotrack.Database.Models.User;
import de.mobcom.group3.gotrack.R;

public class NewUserFragment extends Fragment implements View.OnClickListener {

    EditText fieldFirstName;
    EditText fieldLastName;
    EditText fieldEmail;
    TextView fieldTitle;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_user, container, false);

        /*je nach Seite Title und Buttontext ändern*/
        String title =  getArguments().getString("title");
        String btnText =  getArguments().getString("btnText");
        fieldTitle = view.findViewById(R.id.user_settings_title);
        fieldTitle.setText(""+title);

        fieldFirstName = view.findViewById(R.id.user_first_name);
        fieldLastName = view.findViewById(R.id.user_last_name);
        fieldEmail = view.findViewById(R.id.user_email);

        Button actionBtn = view.findViewById(R.id.create_new_user);
        actionBtn.setText(""+btnText);
        actionBtn.setOnClickListener(this);

        de.hdodenhof.circleimageview.CircleImageView imageUpload = view.findViewById(R.id.profile_image_upload);
        imageUpload.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.create_new_user:
                // Inputfelder auslesen
                String firstName = fieldFirstName.getText().toString();
                String lastName = fieldLastName.getText().toString();
                String email = fieldEmail.getText().toString();

                if (!firstName.equals("") && !lastName.equals("") && !email.equals("")) {
                    // User-Instanz erzeugen
                    User user = new User();
                    user.setName(firstName + " " + lastName);
                    user.setMail(email);

                    // An Datenbank senden
                    UserDAO dao = new UserDAO(getContext());
                    dao.create(user);

                    Toast.makeText(getContext(), "Benutzer \"" + user.getName() + "\" wurde erstellt!", Toast.LENGTH_LONG).show();
                    fieldFirstName.setText("");
                    fieldLastName.setText("");
                    fieldEmail.setText("");
                } else {
                    Toast.makeText(getContext(), "Bitte alle Felder ausfüllen", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.profile_image_upload:
                Toast.makeText(getContext(), "Bild ausgewählt!", Toast.LENGTH_LONG).show();
                break;
        }
    }
}

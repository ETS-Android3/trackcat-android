package de.mobcom.group3.gotrack.Settings;

import android.app.Activity;
import android.content.ContentProvider;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;

import de.mobcom.group3.gotrack.Database.DAO.UserDAO;
import de.mobcom.group3.gotrack.Database.Models.User;
import de.mobcom.group3.gotrack.MainActivity;
import de.mobcom.group3.gotrack.R;

public class NewUserFragment extends Fragment implements View.OnClickListener {

    private static final int READ_REQUEST_CODE = 20;
    EditText fieldFirstName;
    EditText fieldLastName;
    EditText fieldEmail;
    TextView fieldTitle;
    Button actionBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_user, container, false);

        /*je nach Seite Title und Buttontext ändern*/
        String title = getArguments().getString("title");
        String btnText = getArguments().getString("btnText");
        fieldTitle = view.findViewById(R.id.user_settings_title);
        fieldTitle.setText("" + title);

        fieldFirstName = view.findViewById(R.id.user_first_name);
        fieldLastName = view.findViewById(R.id.user_last_name);
        fieldEmail = view.findViewById(R.id.user_email);

        if (btnText == "speichern") {
            fieldFirstName.setText(getArguments().getString("etitFistName"));
            fieldLastName.setText(getArguments().getString("etitLastName"));
            fieldEmail.setText(getArguments().getString("etitEmail"));
        }

        actionBtn = view.findViewById(R.id.create_new_user);
        actionBtn.setText("" + btnText);
        actionBtn.setOnClickListener(this);

        de.hdodenhof.circleimageview.CircleImageView imageUpload = view.findViewById(R.id.profile_image_upload);
        imageUpload.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.create_new_user:
                // Inputfelder auslesen
                String firstName = fieldFirstName.getText().toString();
                String lastName = fieldLastName.getText().toString();
                String email = fieldEmail.getText().toString();

                if (!firstName.equals("") && !lastName.equals("") && !email.equals("")) {
                    // User-Instanz erzeugen
                    User user = new User();
                    user.setFirstName(firstName);
                    user.setLastName(lastName);
                    user.setMail(email);
                    user.setActive(1);
                    UserDAO dao = new UserDAO(getContext());

                    String fullName = user.getFirstName() + " " + user.getLastName();
                    if (actionBtn.getText().equals("erstellen")) {
                        // An Datenbank senden
                        dao.create(user);

                        /*Alten Nutzer deaktivieren*/
                        User oldUser = dao.read(MainActivity.getActiveUser());
                        oldUser.setActive(0);
                        dao.update(MainActivity.getActiveUser(), oldUser);

                        Toast.makeText(getContext(), "Benutzer \"" + fullName + "\" wurde erstellt!", Toast.LENGTH_LONG).show();
                        fieldFirstName.setText("");
                        fieldLastName.setText("");
                        fieldEmail.setText("");
                    } else {
                        // An Datenbank senden
                        dao.update(MainActivity.getActiveUser(), user);
                        Toast.makeText(getContext(), "Benutzer \"" + fullName + "\" wurde bearbeitet!", Toast.LENGTH_LONG).show();
                    }

                   MainActivity.getInstance().addItemsToSpinner();
                } else {
                    Toast.makeText(getContext(), "Bitte alle Felder ausfüllen", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.profile_image_upload:
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                startActivityForResult(intent, READ_REQUEST_CODE);

                Toast.makeText(getContext(), "Wählen Sie Ihr Profilbild aus!", Toast.LENGTH_LONG).show();
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        Toast.makeText(getContext(), "onActivityResult() Methode", Toast.LENGTH_SHORT).show();
        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Toast.makeText(getContext(), "Valid result", Toast.LENGTH_SHORT).show();
            if (resultData != null) {
                Toast.makeText(getContext(), "Not empty data", Toast.LENGTH_SHORT).show();
                Log.i("NEW_USER", "URI: " + resultData.getData());
                //((de.hdodenhof.circleimageview.CircleImageView)getView().findViewById(R.id.profile_image)).setImageURI(resultData.getData());
            }
        }
    }
}

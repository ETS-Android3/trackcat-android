package de.trackcat.Profile;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.BulletSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.Objects;

import de.trackcat.MainActivity;
import de.trackcat.R;

public class DeleteAccountFragment extends Fragment implements View.OnClickListener{

    Button btnSave;
    CheckBox accept;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_delete_account, container, false);

        /* get fields */
        btnSave = view.findViewById(R.id.btn_delete_account);
        accept = view.findViewById(R.id.checkBoxAccept);

        btnSave.setOnClickListener(this);
        accept.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.checkBoxAccept:

                /* change button */
                if(btnSave.isEnabled()) {

                    btnSave.setEnabled(false);
                    btnSave.setBackgroundColor(getResources().getColor(R.color.colorAccentDisable));
                }else{
                    btnSave.setEnabled(true);
                    btnSave.setBackgroundColor(getResources().getColor(R.color.colorGreenAccent));
                }

                break;
            case R.id.btn_delete_account:

                /* create AlertBox */
                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                alert.setTitle("Account löschen?");
                alert.setMessage("Sind Sie sich sicher, dass Sie Ihren Account löschen wollen?");


                alert.setPositiveButton("Ja", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {


                    }
                });

                alert.setNegativeButton("Nein", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
                alert.show();


                break;
        }
    }
}

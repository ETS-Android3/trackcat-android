package de.trackcat.Profile;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;

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
            case R.id.btn_save:



                break;
        }
    }
}

package de.mobcom.group3.gotrack.Settings;

import java.util.ArrayList;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import de.mobcom.group3.gotrack.R;

// Custom Adapter for Spinner
public class CustomSpinnerAdapter extends ArrayAdapter<String> {

    private Context context1;
    private ArrayList<Integer> listImages;
    private ArrayList<String> listNames;
    private ArrayList<String> listEmails;
    public Resources res;
    LayoutInflater inflater;

    public CustomSpinnerAdapter(Context context, ArrayList<Integer> profileImages, ArrayList<String> profileNames, ArrayList<String> profileEmails) {
        super(context, R.layout.spinner_profile_selected, profileNames);

        context1 = context;
        this.listImages = profileImages;
        this.listNames = profileNames;
        this.listEmails = profileEmails;

        inflater = (LayoutInflater) context1
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getProfileList(position, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getSelectedProfile(position, parent);
    }

    // This funtion called for each row ( Called data.size() times )
    public View getProfileList(int position, ViewGroup parent) {
        View view = inflater.inflate(R.layout.spinner_profile_list, parent, false);

        LinearLayout profileList = view.findViewById(R.id.profile_layout_list);
        ImageView profileImage = profileList.findViewById(R.id.profile_image);
        profileImage.setImageResource(listImages.get(position));

        TextView profileName = profileList.findViewById(R.id.profile_name);
        profileName.setText(listNames.get(position));

        TextView profileEmail = profileList.findViewById(R.id.profile_email);
        profileEmail.setText(listEmails.get(position));

        return view;
    }

    // This funtion called for each row ( Called data.size() times )
    public View getSelectedProfile(int position, ViewGroup parent) {
        View view = inflater.inflate(R.layout.spinner_profile_selected, parent, false);

        LinearLayout selectedProfile = view.findViewById(R.id.profile_layout_selected);
        TextView profileName = selectedProfile.findViewById(R.id.profile_name_selected);
        profileName.setText(listNames.get(position));

        TextView profileEmail = selectedProfile.findViewById(R.id.profile_email_selected);
        profileEmail.setText(listEmails.get(position));
        return view;
    }

}
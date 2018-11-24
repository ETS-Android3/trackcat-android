package de.mobcom.group3.gotrack.Settings;

import java.util.ArrayList;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import de.mobcom.group3.gotrack.R;

// Custom Adapter for Spinner
public class CustomSpinnerAdapter extends ArrayAdapter<String> {

    private Context context1;
    private ArrayList<String> dataIcons;
    private ArrayList<String> dataText;
    private ArrayList<String> dataEmail;
    private ArrayList<String> dataName;
    public Resources res;
    LayoutInflater inflater;

    public CustomSpinnerAdapter(Context context, ArrayList<String> objectsIcons, ArrayList<String> objectsText, ArrayList<String> objectsEmail, ArrayList<String> objectsNames) {
        super(context, R.layout.spinner_profile_selected, objectsIcons);

        context1 = context;
        dataIcons = objectsIcons;
        dataText = objectsText;
        dataEmail = objectsEmail;
        dataName = objectsNames;

        inflater = (LayoutInflater) context1
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //return getCustomView(position, convertView, parent);
        return getFirstView(position, convertView, parent);
    }

    // This funtion called for each row ( Called data.size() times )
    public View getCustomView(int position, View convertView, ViewGroup parent) {
        View row = inflater.inflate(R.layout.spinner_profile_list, parent, false);

        LinearLayout tvCategory = (LinearLayout) row.findViewById(R.id.profile_layout_list);
        TextView profileListText = tvCategory.findViewById(R.id.profile_list_text);
        profileListText.setText(dataText.get(position));

        TextView profileListIcon = tvCategory.findViewById(R.id.profile_list_icon);
        profileListIcon.setText(dataIcons.get(position));

        return row;
    }

    // This funtion called for each row ( Called data.size() times )
    public View getFirstView(int position, View convertView, ViewGroup parent) {
        View row = inflater.inflate(R.layout.spinner_profile_selected, parent, false);

        LinearLayout tvCategory = (LinearLayout) row.findViewById(R.id.profile_layout_selected);
        TextView profileName = tvCategory.findViewById(R.id.profile_name_selected);
        profileName.setText(dataName.get(position));

        TextView profileEmail = tvCategory.findViewById(R.id.profile_email_selected);
        profileEmail.setText(dataEmail.get(position));
        return row;
    }
}
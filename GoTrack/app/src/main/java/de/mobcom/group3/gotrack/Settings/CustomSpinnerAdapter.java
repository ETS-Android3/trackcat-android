package de.mobcom.group3.gotrack.Settings;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import de.mobcom.group3.gotrack.Database.DAO.UserDAO;
import de.mobcom.group3.gotrack.Database.Models.User;
import de.mobcom.group3.gotrack.MainActivity;
import de.mobcom.group3.gotrack.R;

// Custom Adapter for Spinner
public class CustomSpinnerAdapter extends ArrayAdapter<String> {

    private Context context1;
    private ArrayList<Integer> listImages;
    private ArrayList<String> listNames;
    private ArrayList<String> listEmails;
    public Resources res;
    LayoutInflater inflater;
    TextView title;

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

    // Fügt für jedes Listenelement dem Spinner einen Eintrag hinzu
    public View getProfileList(int position, ViewGroup parent) {
        View view = inflater.inflate(R.layout.spinner_profile_list, parent, false);

        if (position == listNames.size() - 1) {
            view = inflater.inflate(R.layout.spinner_footer, parent, false);

            /*Anzeigen des Profilbearbeitungsfragment*/
            LinearLayout editUserLayout = (LinearLayout )view.findViewById(R.id.profile_edit_user);
            editUserLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    /*Titel und BtnText bearbeiten*/
                    Bundle bundle = new Bundle();
                    bundle.putString("title", "Profil bearbeiten");
                    bundle.putString("btnText", "speichern");

                    // TODO Splitten des Ersten und Zweiten Namens
                    /*Aktiven Nutzer ermitteln und Text ausgeben*/
                    UserDAO dao = new UserDAO(MainActivity.getInstance());
                    User user = dao.read(MainActivity.getActiveUser());
                    bundle.putString("etitFistName", user.getName());
                    bundle.putString("etitLastName", user.getName());
                    bundle.putString("etitEmail", user.getMail());

                    NewUserFragment newUserFragment=new NewUserFragment();
                    newUserFragment.setArguments(bundle);

                    FragmentTransaction fragTransaction = MainActivity.getInstance().getSupportFragmentManager().beginTransaction();
                    fragTransaction.replace(R.id.mainFrame, newUserFragment, "EDITUSER");
                    fragTransaction.commit();

                    /*Ausblenden des Spinners*/
                    DrawerLayout mainDrawer = MainActivity.getInstance().findViewById(R.id.drawer_layout);
                    mainDrawer.closeDrawer(GravityCompat.START);
                    try {
                        Method method = Spinner.class.getDeclaredMethod("onDetachedFromWindow");
                        method.setAccessible(true);
                        method.invoke(MainActivity.getInstance().getSpinner());
                    }catch(Exception e){
                    }
                }
            });

            /*Anzeigen des Profilerstellungssfragment*/
            LinearLayout addUserLayout = (LinearLayout )view.findViewById(R.id.profile_add_user);
            addUserLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    /*Titel und BtnText bearbeiten*/
                    Bundle bundle = new Bundle();
                    bundle.putString("title", "Profil erstellen");
                    bundle.putString("btnText", "erstellen");
                    NewUserFragment newUserFragment=new NewUserFragment();
                    newUserFragment.setArguments(bundle);

                    FragmentTransaction fragTransaction = MainActivity.getInstance().getSupportFragmentManager().beginTransaction();
                    fragTransaction.replace(R.id.mainFrame, newUserFragment, "NEWUSER");
                    fragTransaction.commit();

                    /*Ausblenden des Spinners*/
                    DrawerLayout mainDrawer = MainActivity.getInstance().findViewById(R.id.drawer_layout);
                    mainDrawer.closeDrawer(GravityCompat.START);
                    try {
                        Method method = Spinner.class.getDeclaredMethod("onDetachedFromWindow");
                        method.setAccessible(true);
                        method.invoke(MainActivity.getInstance().getSpinner());
                    }catch(Exception e){
                    }
                }
            });
        }

        LinearLayout profileItem = view.findViewById(R.id.profile_layout_list);
        ImageView profileImage = profileItem.findViewById(R.id.profile_image);
        profileImage.setImageResource(listImages.get(position));

        TextView profileName = profileItem.findViewById(R.id.profile_name);
        profileName.setText(listNames.get(position));

        TextView profileEmail = profileItem.findViewById(R.id.profile_email);
        profileEmail.setText(listEmails.get(position));

        return view;
    }

    // Anzeige
    public View getSelectedProfile(int position, ViewGroup parent) {
        View view = inflater.inflate(R.layout.spinner_profile_selected, parent, false);

        LinearLayout selectedProfile = view.findViewById(R.id.profile_layout_selected);
        TextView profileName = selectedProfile.findViewById(R.id.profile_name);
        profileName.setText(listNames.get(position));

        TextView profileEmail = selectedProfile.findViewById(R.id.profile_email);
        profileEmail.setText(listEmails.get(position));

        return view;
    }

}
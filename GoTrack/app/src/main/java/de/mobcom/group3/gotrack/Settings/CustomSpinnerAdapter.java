package de.mobcom.group3.gotrack.Settings;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import de.mobcom.group3.gotrack.Database.DAO.UserDAO;
import de.mobcom.group3.gotrack.Database.Models.User;
import de.mobcom.group3.gotrack.MainActivity;
import de.mobcom.group3.gotrack.R;

/* Custom Adapter for Spinner */
public class CustomSpinnerAdapter extends ArrayAdapter<String> {

    private Context context1;
    private ArrayList<byte[]> listImages;
    private ArrayList<String> listNames;
    private ArrayList<String> listEmails;
    public Resources res;
    LayoutInflater inflater;
    private UserDAO userDAO;

    public CustomSpinnerAdapter(Context context, ArrayList<byte[]> profileImages, ArrayList<String> profileNames, ArrayList<String> profileEmails) {
        super(context, R.layout.spinner_profile_selected, profileNames);

        context1 = context;
        this.listImages = profileImages;
        this.listNames = profileNames;
        this.listEmails = profileEmails;
        this.userDAO = new UserDAO(MainActivity.getInstance());

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

    /* Fügt für jedes Listenelement dem Spinner einen Eintrag hinzu */
    public View getProfileList(int position, ViewGroup parent) {
        View view = inflater.inflate(R.layout.spinner_profile_list, parent, false);

        if (position == listNames.size() - 1) {
            view = inflater.inflate(R.layout.spinner_footer, parent, false);

            /* Anzeigen des Profilbearbeitungsfragment */
            LinearLayout editUserLayout = (LinearLayout) view.findViewById(R.id.profile_edit_user);
            editUserLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /* Titel und BtnText bearbeiten */
                    Bundle bundle = new Bundle();
                    bundle.putString("title", "Profil bearbeiten");
                    bundle.putString("btnText", "Speichern");

                    /* Aktiven Nutzer ermitteln und Text ausgeben */
                    User user = userDAO.read(MainActivity.getActiveUser());
                    bundle.putString("etitFistName", user.getFirstName());
                    bundle.putString("etitLastName", user.getLastName());
                    bundle.putString("etitEmail", user.getMail());
                    bundle.putByteArray("currentImage", user.getImage());

                    NewUserFragment newUserFragment = new NewUserFragment();
                    newUserFragment.setArguments(bundle);

                    FragmentTransaction fragTransaction = MainActivity.getInstance().getSupportFragmentManager().beginTransaction();
                    fragTransaction.replace(R.id.mainFrame, newUserFragment, "EDITUSER");
                    fragTransaction.commit();

                    /* Ausblenden des Spinners */
                    DrawerLayout mainDrawer = MainActivity.getInstance().findViewById(R.id.drawer_layout);
                    mainDrawer.closeDrawer(GravityCompat.START);
                    try {
                        Method method = Spinner.class.getDeclaredMethod("onDetachedFromWindow");
                        method.setAccessible(true);
                        method.invoke(MainActivity.getInstance().getSpinner());
                    } catch (Exception e) {
                    }
                }
            });

            /* Anzeigen des Profilerstellungssfragment */
            LinearLayout addUserLayout = (LinearLayout) view.findViewById(R.id.profile_add_user);
            addUserLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /* Titel und BtnText bearbeiten */
                    Bundle bundle = new Bundle();
                    bundle.putString("title", "Profil erstellen");
                    bundle.putString("btnText", "Erstellen");
                    NewUserFragment newUserFragment = new NewUserFragment();
                    newUserFragment.setArguments(bundle);

                    FragmentTransaction fragTransaction = MainActivity.getInstance().getSupportFragmentManager().beginTransaction();
                    fragTransaction.replace(R.id.mainFrame, newUserFragment, "NEWUSER");
                    fragTransaction.commit();

                    /* Ausblenden des Spinners */
                    DrawerLayout mainDrawer = MainActivity.getInstance().findViewById(R.id.drawer_layout);
                    mainDrawer.closeDrawer(GravityCompat.START);
                    try {
                        Method method = Spinner.class.getDeclaredMethod("onDetachedFromWindow");
                        method.setAccessible(true);
                        method.invoke(MainActivity.getInstance().getSpinner());
                    } catch (Exception e) {
                    }
                }
            });

            /* Profil löschen */
            LinearLayout deleteUserLayout = (LinearLayout) view.findViewById(R.id.profile_delete_user);
            deleteUserLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    List<User> users = userDAO.readAll();
                    User user = userDAO.read(MainActivity.getActiveUser());
                    if (users.size() > 1) {
                        userDAO.delete(user);
                        if (MainActivity.getHints()) {
                            Toast.makeText(MainActivity.getInstance().getApplicationContext(), "Löschen von: " + user.getFirstName() + " " + user.getLastName(), Toast.LENGTH_LONG).show();
                        }
                    } else {
                        if (MainActivity.getHints()) {
                            Toast.makeText(MainActivity.getInstance().getApplicationContext(), "" + user.getFirstName() + " " + user.getLastName() + " konnte nicht gelöscht werden, da sonst keine Nutzer mehr existieren.", Toast.LENGTH_LONG).show();
                        }
                    }
                    /* Ausblenden des Spinners */
                    DrawerLayout mainDrawer = MainActivity.getInstance().findViewById(R.id.drawer_layout);
                    mainDrawer.closeDrawer(GravityCompat.START);
                    try {
                        Method method = Spinner.class.getDeclaredMethod("onDetachedFromWindow");
                        method.setAccessible(true);
                        method.invoke(MainActivity.getInstance().getSpinner());
                    } catch (Exception e) {
                    }
                    /*Aktualisieren des Spinners*/
                    MainActivity.getInstance().addItemsToSpinner();
                }
            });
        }


        LinearLayout profileItem = view.findViewById(R.id.profile_layout_list);

        /* Padding beim ersten Element setzen */
        if (position == 0) {
            int padding_in_dp = 20;
            final float scale = MainActivity.getInstance().getResources().getDisplayMetrics().density;
            int padding_in_px = (int) (padding_in_dp * scale + 0.5f);
            profileItem.setPadding(padding_in_px,padding_in_px/2,padding_in_px,padding_in_px/2);
        }
        /* Profilbilder anzeigen */
        ImageView profileImage = profileItem.findViewById(R.id.profile_image);
        byte[] imgRessource = listImages.get(position);
        Bitmap bitmap = BitmapFactory.decodeResource(getContext().getResources(), R.raw.default_profile);
        if (imgRessource != null && imgRessource.length > 0) {
            bitmap = BitmapFactory.decodeByteArray(imgRessource, 0, imgRessource.length);
        }
        profileImage.setImageBitmap(bitmap);

        /* Name des Profils */
        TextView profileName = profileItem.findViewById(R.id.profile_name);
        profileName.setText(listNames.get(position));

        /* Email-Adresse des Profils */
        TextView profileEmail = profileItem.findViewById(R.id.profile_email);
        profileEmail.setText(listEmails.get(position));

        return view;
    }

    /* Anzeige des ausgewählten Profils (hier ohne Profilbild) */
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
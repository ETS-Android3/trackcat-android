package de.trackcat.FriendsSystem.FriendShowOptions;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;
import de.trackcat.MainActivity;
import de.trackcat.R;

public class FriendProfileFragment extends Fragment implements View.OnClickListener {

    RelativeLayout loadProfile;
    Button btn_delete_friend;
    TextView name, email, dayOfBirth, gender, amountRecords, amountTime, amountDistance, dayOfRegistration;
    CircleImageView image, state;
    ImageView birthday, user_gender_image;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_friends_profile, container, false);

        /* get profil fields */
        loadProfile = view.findViewById(R.id.loadScreen);
        image = view.findViewById(R.id.profile_image);
        state = view.findViewById(R.id.profile_state);
        name = view.findViewById(R.id.user_name);
        email = view.findViewById(R.id.user_email);
        dayOfBirth = view.findViewById(R.id.user_dayOfBirth);
        birthday = view.findViewById(R.id.user_birthday);
        gender = view.findViewById(R.id.user_gender);
        user_gender_image = view.findViewById(R.id.user_gender_image);
        amountRecords = view.findViewById(R.id.user_amount_records);
        amountTime = view.findViewById(R.id.user_amount_time_records);
        amountDistance = view.findViewById(R.id.user_amount_distance_records);
        dayOfRegistration = view.findViewById(R.id.user_dayOfRegistration);
        btn_delete_friend = view.findViewById(R.id.btn_delete_friend);

        /* set loadscreen invisible */
        loadProfile.setVisibility(View.GONE);

        /* set on click listener */
        btn_delete_friend.setOnClickListener(this);

        /* set values */
        //TODO values aus der Datenbank anfragen
        // image.setImageURI();
        //  state = view.findViewById(R.id.profile_state);
        name.setText("Anna Webstar");
        email.setText("anna@web.de");
        dayOfBirth.setText("12.12.1998");
        gender.setText("weiblich");
        //  user_gender_image = view.findViewById(R.id.user_gender_image);
        amountRecords.setText("20");
        amountTime.setText("12:20:12");
        amountDistance.setText("20km");
        dayOfRegistration.setText("13.09.2014");

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_delete_friend:
                /* create AlertBox */
                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                alert.setTitle("Freund wirklich entfernen?");
                alert.setMessage(MainActivity.getInstance().getResources().getString(R.string.friendsDelete));

                alert.setPositiveButton("Freund entfernen", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        //TODO Freund entfernen
                    }
                });

                alert.setNegativeButton("Abbruch", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });

                alert.show();
                break;
        }
    }
}

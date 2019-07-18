package de.trackcat.FriendsSystem.FriendShowOptions;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

public class PublicPersonProfileFragment extends Fragment{

    RelativeLayout loadProfile;
    TextView name, dayOfBirth, gender, dayOfRegistration;
    CircleImageView image, state;
    ImageView birthday, user_gender_image;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_friends_public_person_profile, container, false);

        /* get profil fields */
        loadProfile = view.findViewById(R.id.loadScreen);
        image = view.findViewById(R.id.profile_image);
        state = view.findViewById(R.id.profile_state);
        name = view.findViewById(R.id.user_name);
        dayOfBirth = view.findViewById(R.id.user_dayOfBirth);
        birthday = view.findViewById(R.id.user_birthday);
        gender = view.findViewById(R.id.user_gender);
        user_gender_image = view.findViewById(R.id.user_gender_image);
        dayOfRegistration = view.findViewById(R.id.user_dayOfRegistration);

        /* set loadscreen invisible */
        loadProfile.setVisibility(View.GONE);

        /* set values */
        //TODO values aus der Datenbank anfragen
        // image.setImageURI();
        //  state = view.findViewById(R.id.profile_state);
        name.setText("Anna Webstar");
        dayOfBirth.setText("12.12");
        gender.setText("weiblich");
        //user_gender_image
        dayOfRegistration.setText("13.09.2014");

        return view;
    }
}

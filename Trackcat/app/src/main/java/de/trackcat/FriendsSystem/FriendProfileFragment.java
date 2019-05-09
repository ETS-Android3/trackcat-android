package de.trackcat.FriendsSystem;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import de.trackcat.MainActivity;
import de.trackcat.R;

public class FriendProfileFragment extends Fragment implements View.OnClickListener {

    RelativeLayout loadProfile;
    Button btn_delete_friend;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_friends_profile, container, false);

        /* get profil fields */
        loadProfile = view.findViewById(R.id.loadScreen);
        loadProfile.setVisibility(View.GONE);

        btn_delete_friend = view.findViewById(R.id.btn_delete_friend);

        /* set on click listener */
        btn_delete_friend.setOnClickListener(this);

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
                        FragmentTransaction fragTransaction = MainActivity.getInstance().getSupportFragmentManager().beginTransaction();
                        fragTransaction.replace(R.id.mainFrame, new FriendLiveFragment(),
                                MainActivity.getInstance().getResources().getString(R.string.fFriendLiveView));
                        fragTransaction.commit();
                    }
                });

                alert.setNegativeButton("Abbruch", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        FragmentTransaction fragTransaction = MainActivity.getInstance().getSupportFragmentManager().beginTransaction();
                        fragTransaction.replace(R.id.mainFrame, new FriendProfileFragment(),
                                MainActivity.getInstance().getResources().getString(R.string.fFriendProfile));
                        fragTransaction.commit();
                    }
                });

                alert.show();
                break;
        }
    }
}

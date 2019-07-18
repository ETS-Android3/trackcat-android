package de.trackcat.FriendsSystem;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import de.trackcat.CustomElements.CustomFriend;
import de.trackcat.FriendsSystem.FriendShowOptions.FriendLiveFragment;
import de.trackcat.FriendsSystem.FriendShowOptions.FriendProfileFragment;
import de.trackcat.GlobalFunctions;
import de.trackcat.MainActivity;
import de.trackcat.R;

public class FriendListAdapter extends ArrayAdapter<String> {

    private List<CustomFriend> friends;
    public TextView name, email, registSince;
    LayoutInflater inflater;
    CircleImageView image, state;
    boolean newFriend;

    public FriendListAdapter(Activity context, List<CustomFriend> friends, boolean type) {
        super(context, R.layout.friend_list_item);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.friends = friends;
        this.newFriend = type;
    }

    @Override
    public int getCount() {
        return friends.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = null;
        if (!newFriend) {

            /* Variablen erstellen */
            view = inflater.inflate(R.layout.friend_list_item, parent, false);
            name = view.findViewById(R.id.friend_name);
            email = view.findViewById(R.id.friend_email);
            image = view.findViewById(R.id.profile_image);
            state = view.findViewById(R.id.profile_state);
            name.setText(friends.get(position).getFirstName() + " " + friends.get(position).getLastName());
           // email.setText(friends.get(position).getEmail());

            /* Shows details of routes */
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    /* create AlertBox */
                    AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                    alert.setTitle("Was möchten Sie anzeigen?");
                    alert.setMessage(MainActivity.getInstance().getResources().getString(R.string.friendsLive));

                    alert.setPositiveButton("LIVEÜBERTRAGUNG", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            FragmentTransaction fragTransaction = MainActivity.getInstance().getSupportFragmentManager().beginTransaction();
                            fragTransaction.replace(R.id.mainFrame, new FriendLiveFragment(),
                                    MainActivity.getInstance().getResources().getString(R.string.fFriendLiveView));
                            fragTransaction.commit();
                        }
                    });

                    alert.setNeutralButton("PROFIL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            FragmentTransaction fragTransaction = MainActivity.getInstance().getSupportFragmentManager().beginTransaction();
                            fragTransaction.replace(R.id.mainFrame, new FriendProfileFragment(),
                                    MainActivity.getInstance().getResources().getString(R.string.fFriendProfile));
                            fragTransaction.commit();
                        }
                    });

                    alert.show();
                }
            });
        } else {
            /* find views */
            view = inflater.inflate(R.layout.new_friend_list_item, parent, false);
            name = view.findViewById(R.id.friend_name);
            registSince = view.findViewById(R.id.friend_regist_since);
            image = view.findViewById(R.id.profile_image);
            state = view.findViewById(R.id.profile_state);

            /* add values */
            name.setText(friends.get(position).getFirstName() + " " + friends.get(position).getLastName());
            registSince.setText(GlobalFunctions.getDateWithTimeFromSeconds(friends.get(position).getDateOfRegistration(), "dd.MM.yyyy HH:MM"));
            state.setImageBitmap(GlobalFunctions.findLevel(friends.get(position).getTotalDistance()));

            ImageView addFriend = view.findViewById(R.id.add_friend);
            addFriend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    /* create AlertBox */
                    AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                    alert.setTitle("Freund hinzufügen?");
                    alert.setMessage("Möchten Sie '" + name.getText().toString() + "' zu Ihren Freunden hinzufügen, um seine Liveübertragunen und sein Profil anzeigen zu können?");

                    alert.setPositiveButton("Freund hinzufügen", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            //TODO add user als freund
                        }
                    });

                    alert.setNegativeButton("Abruch", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });

                    alert.show();
                }
            });

        }
        // ShowRecord.show(records, position, MainActivity.getInstance().getResources().getString(R.string.fRecordDetailsDashbaord), recordId, recordType, importState, recordName, recordDistance, recordTime, recordItem, recordDate);
        return view;
    }
}

package de.trackcat.FriendsSystem;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Base64;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import de.trackcat.APIClient;
import de.trackcat.APIConnector;
import de.trackcat.CustomElements.CustomFriend;
import de.trackcat.CustomElements.CustomLocation;
import de.trackcat.Database.DAO.UserDAO;
import de.trackcat.Database.Models.User;
import de.trackcat.GlobalFunctions;
import de.trackcat.MainActivity;
import de.trackcat.R;
import de.trackcat.RecordList.RecordDetailsFragment;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class FriendListAdapter extends ArrayAdapter<String> {

    private List<CustomFriend> friends;
    public TextView name, email;
    LayoutInflater inflater;

    public FriendListAdapter(Activity context, List<CustomFriend> friends) {
        super(context, R.layout.friend_list_item);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.friends = friends;
    }

    @Override
    public int getCount() {
        return friends.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        /* Variablen erstellen */
        View view = inflater.inflate(R.layout.friend_list_item, parent, false);
        name = view.findViewById(R.id.friend_name);
        email = view.findViewById(R.id.friend_email);
        name.setText(friends.get(position).getFirstName() + " " + friends.get(position).getLastName());
        email.setText(friends.get(position).getEmail());

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

        // ShowRecord.show(records, position, MainActivity.getInstance().getResources().getString(R.string.fRecordDetailsDashbaord), recordId, recordType, importState, recordName, recordDistance, recordTime, recordItem, recordDate);
        return view;
    }
}

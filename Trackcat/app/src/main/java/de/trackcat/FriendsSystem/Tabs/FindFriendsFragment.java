package de.trackcat.FriendsSystem.Tabs;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import de.trackcat.CustomElements.CustomFriend;
import de.trackcat.FriendsSystem.FriendListAdapter;
import de.trackcat.MainActivity;
import de.trackcat.R;

public class FindFriendsFragment extends Fragment implements View.OnKeyListener {

    EditText findFriend;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_friends_find, container, false);

        /* find view */
        findFriend = view.findViewById(R.id.findFriend);
        findFriend.setOnKeyListener(this);

        List<CustomFriend> friendList= new ArrayList<>();

        CustomFriend friend1= new CustomFriend();
        friend1.setFirstName("neuer Max");
        friend1.setLastName("Mustermann");
        friend1.setEmail("max@mustermann.de");
        friendList.add(friend1);

        CustomFriend friend2= new CustomFriend();
        friend2.setFirstName("neue Mimi");
        friend2.setLastName("Mensch");
        friend2.setEmail("mimi@mensch.de");
        friendList.add(friend2);


        FriendListAdapter adapter = new FriendListAdapter(MainActivity.getInstance(),friendList, true);
        ListView friendListView = view.findViewById(R.id.friend_list);
        friendListView.setAdapter(adapter);

        return view;
    }


    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
           String find=  findFriend.getText().toString();
            Toast.makeText(getContext(), "Suche nach '"+find+"' gestartet.", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }
}

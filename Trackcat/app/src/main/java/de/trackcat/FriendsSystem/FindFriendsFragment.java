package de.trackcat.FriendsSystem;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

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

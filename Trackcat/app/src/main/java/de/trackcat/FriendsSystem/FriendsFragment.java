package de.trackcat.FriendsSystem;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import de.trackcat.CustomElements.CustomFriend;
import de.trackcat.MainActivity;
import de.trackcat.R;
import de.trackcat.RecordList.SwipeControll.RecordListAdapter;

public class FriendsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_friends, container, false);

        List<CustomFriend> friendList= new ArrayList<>();

        CustomFriend friend1= new CustomFriend();
        friend1.setFirstName("Max");
        friend1.setLastName("Mustermann");
        friend1.setEmail("max@mustermann.de");
        friendList.add(friend1);

        CustomFriend friend2= new CustomFriend();
        friend2.setFirstName("Mimi");
        friend2.setLastName("Mensch");
        friend2.setEmail("mimi@mensch.de");
        friendList.add(friend2);

        CustomFriend friend3= new CustomFriend();
        friend3.setFirstName("Manfred");
        friend3.setLastName("Walter");
        friend3.setEmail("manfred@walter.de");
        friendList.add(friend3);

        FriendListAdapter adapter = new FriendListAdapter(MainActivity.getInstance(),friendList);
        ListView friendListView = view.findViewById(R.id.friend_list);
        friendListView.setAdapter(adapter);

        /* RecyclerView mit Inhalten aus Adapter füllen
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext().getApplicationContext());
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this.getContext(), DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(mAdapter);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(mAdapter);*/

        return view;
    }


}

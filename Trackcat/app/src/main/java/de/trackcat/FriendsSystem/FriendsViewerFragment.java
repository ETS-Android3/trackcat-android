package de.trackcat.FriendsSystem;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rahimlis.badgedtablayout.BadgedTabLayout;

import de.trackcat.FriendsSystem.Tabs.FindFriendsFragment;
import de.trackcat.FriendsSystem.Tabs.FriendQuestionsFragment;
import de.trackcat.FriendsSystem.Tabs.FriendSendQuestionsFragment;
import de.trackcat.FriendsSystem.Tabs.FriendsFragment;
import de.trackcat.FriendsSystem.Tabs.SharingFriendsFragment;
import de.trackcat.R;

public class FriendsViewerFragment extends Fragment {
    private int activeSite;
    private String searchTerm;
    BadgedTabLayout tabLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_friends_viewer, container, false);

        /* get active site */
        activeSite = getArguments().getInt("activeSite");
        if (getArguments().getString("searchTerm") != null) {
            searchTerm = getArguments().getString("searchTerm");
        }

        /* create tabView Pager */
        ViewPager viewPager = view.findViewById(R.id.viewpager);
        viewPager.setOffscreenPageLimit(3);
        setupViewPager(viewPager);
        tabLayout = view.findViewById(R.id.tablayout);
        tabLayout.setupWithViewPager(viewPager);


        tabLayout.setIcon(0,R.drawable.ic_search_black_24dp);
        tabLayout.setIcon(1,R.drawable.ic_friends);
        tabLayout.setIcon(2,R.drawable.ic_live_friends);
        tabLayout.setIcon(3,R.drawable.ic_friend_questions);
        tabLayout.setIcon(4,R.drawable.ic_send_friend_questions);


        return view;
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        FriendsFragment friendsFragment = new FriendsFragment();
        FindFriendsFragment findFriendsFragment = new FindFriendsFragment();
        SharingFriendsFragment sharingFriendsFragment = new SharingFriendsFragment();
        FriendQuestionsFragment friendQuestionsFragment = new FriendQuestionsFragment();
        FriendSendQuestionsFragment friendSendQuestionsFragment = new FriendSendQuestionsFragment();
        adapter.addFragment(findFriendsFragment, "");
        adapter.addFragment(friendsFragment, "");
        adapter.addFragment(sharingFriendsFragment, "");
        adapter.addFragment(friendQuestionsFragment, "");
        adapter.addFragment(friendSendQuestionsFragment, "");



        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(activeSite);
    }

    /* function to change BadgeText */
    public void setBadgeText(int index, String text) {
        tabLayout.setBadgeText(index, text);
    }
}

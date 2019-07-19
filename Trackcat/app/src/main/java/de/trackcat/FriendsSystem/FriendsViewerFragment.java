package de.trackcat.FriendsSystem;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.trackcat.FriendsSystem.Tabs.FindFriendsFragment;
import de.trackcat.FriendsSystem.Tabs.FriendsFragment;
import de.trackcat.FriendsSystem.Tabs.SharingFriendsFragment;
import de.trackcat.R;

public class FriendsViewerFragment extends Fragment {
    private int activeSite;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_friends_viewer, container, false);

        /* get active site */
        activeSite = getArguments().getInt("activeSite");

        ViewPager viewPager =  view.findViewById(R.id.viewpager);
        viewPager.setOffscreenPageLimit(3);
        setupViewPager(viewPager);
        TabLayout  tabLayout =  view.findViewById(R.id.tablayout);
        tabLayout.setupWithViewPager(viewPager);
        return view;
    }

    private void setupViewPager(ViewPager viewPager)
    {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        FriendsFragment friendsFragment=new FriendsFragment();
        FindFriendsFragment findFriendsFragment=new FindFriendsFragment();
        SharingFriendsFragment sharingFriendsFragment= new SharingFriendsFragment();
        adapter.addFragment(findFriendsFragment,"SUCHEN");
        adapter.addFragment(friendsFragment,"FREUNDE");
        adapter.addFragment(sharingFriendsFragment,"LIVE");
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(activeSite);
    }
}

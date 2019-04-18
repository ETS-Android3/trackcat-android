package de.trackcat.Recording.Recording_UI;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import de.trackcat.MainActivity;
import de.trackcat.R;

import java.util.ArrayList;
import java.util.List;


public class PageViewer extends Fragment {
    private List<Fragment> listFragments = new ArrayList<>();

    public PageViewer() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_page_viewer, container, false);

        /* create Fragments for ViewPager */
        KMH_View_Fragment kmhFrag = new KMH_View_Fragment();
        TimeTotal_View_Fragment timeFrag = new TimeTotal_View_Fragment();

        listFragments.add(kmhFrag);
        listFragments.add(timeFrag);

        // Instantiate a ViewPager and a PagerAdapter.
        ViewPager mPager = view.findViewById(R.id.pager);
        PagerAdapter mPagerAdapter = new PageViewer.ScreenSlidePagerAdapter(MainActivity.getInstance().getSupportFragmentManager());

        mPager.setAdapter(mPagerAdapter);

        LinearLayout mLinearLayout = view.findViewById(R.id.indicator);

        /* create Indicator (little buttons) */
        if (Build.VERSION.SDK_INT > 21) {
            CurrentPageIndicator mIndicator = new CurrentPageIndicator(MainActivity.getInstance(), mLinearLayout, mPager, R.drawable.indicator_circle);
            mIndicator.setPageCount(listFragments.size());
            mIndicator.show();
        } else {
            CurrentPageIndicator mIndicator = new CurrentPageIndicator(MainActivity.getInstance(), mLinearLayout, mPager, R.drawable.indicator_circle_v21);
            mIndicator.setPageCount(listFragments.size());
            mIndicator.show();
        }

        return view;
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        private ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        /* called on Swipe */
        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            return listFragments.get(position);
        }

        @Override
        public int getCount() {
            return listFragments.size();
        }
    }
}

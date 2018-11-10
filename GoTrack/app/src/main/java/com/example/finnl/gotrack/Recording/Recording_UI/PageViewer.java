package com.example.finnl.gotrack.Recording.Recording_UI;

import android.content.Context;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
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

import com.example.finnl.gotrack.MainActivity;
import com.example.finnl.gotrack.R;
import com.example.finnl.gotrack.Recording.RecordFragment;

import java.util.ArrayList;
import java.util.List;


public class PageViewer extends Fragment {


    private List<Fragment> listFragments = new ArrayList<>();

    public PageViewer() {
        // Required empty public constructor
    }


    public static PageViewer newInstance(String param1, String param2) {
        PageViewer fragment = new PageViewer();
        return fragment;
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

        KMH_View_Fragment kmhFrag = new KMH_View_Fragment();
        TimeTotal_View_Fragment timeFrag = new TimeTotal_View_Fragment();

        //listFragments = new ArrayList<>();
        listFragments.add(kmhFrag);
        listFragments.add(timeFrag);

        // Instantiate a ViewPager and a PagerAdapter.
        ViewPager mPager = (ViewPager) view.findViewById(R.id.pager);

        PagerAdapter mPagerAdapter = new PageViewer.ScreenSlidePagerAdapter(MainActivity.getInstance().getSupportFragmentManager());
        // instance.getChildFragmentManager());//MainActivity.getInstance().getSupportFragmentManager());

        mPager.setAdapter(mPagerAdapter);





        LinearLayout mLinearLayout = view.findViewById(R.id.indicator);

        CurrentPageIndicator mIndicator = new CurrentPageIndicator(MainActivity.getInstance(), mLinearLayout, mPager, R.drawable.indicator_circle);
        mIndicator.setPageCount(listFragments.size());
        mIndicator.show();

        return view;
    }

    /**
     * A simple pager adapter that represents 5 ScreenSlidePageFragment objects, in
     * sequence.
     */
    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

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

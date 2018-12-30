package de.mobcom.group3.gotrack.RecordList;

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

import de.mobcom.group3.gotrack.MainActivity;
import de.mobcom.group3.gotrack.R;
import de.mobcom.group3.gotrack.Recording.Recording_UI.CurrentPageIndicator;

import java.util.ArrayList;
import java.util.List;

public class RecordListDetailsPageViewer extends Fragment {


    private List<Fragment> listFragments = new ArrayList<>();

    public RecordListDetailsPageViewer() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        /* Layout inflaten */
        View view = inflater.inflate(R.layout.fragment_page_viewer_charts, container, false);

        // TODO muss funktionstüchtig eingebaut werden
        /* Anzeige der allgemeinen Informationen */
        int id = getArguments().getInt("id");
        Bundle bundleInformation = new Bundle();
        bundleInformation.putInt("id", id);

        RecordDetailsInformationFragment recordDetailsInformationFragment = new RecordDetailsInformationFragment();
        recordDetailsInformationFragment.setArguments(bundleInformation);
        listFragments.add(recordDetailsInformationFragment);

        /* Anzeige der Charts */
        double[] speedValues = getArguments().getDoubleArray("speedArray");
        double[] altitudeValues = getArguments().getDoubleArray("altitudeArray");
        Bundle bundleCharts = new Bundle();
        bundleCharts.putDoubleArray("speedArray", speedValues);
        bundleCharts.putDoubleArray("altitudeArray", altitudeValues);
        RecordDetailsChartsFragment recordDetailsCharts = new RecordDetailsChartsFragment();
        recordDetailsCharts.setArguments(bundleCharts);
        listFragments.add(recordDetailsCharts);

        /* Instanziieren des ViewPagers */
        ViewPager mPager = view.findViewById(R.id.pager);
        PagerAdapter mPagerAdapter = new RecordListDetailsPageViewer.ScreenSlidePagerAdapter(MainActivity.getInstance().getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        LinearLayout mLinearLayout = view.findViewById(R.id.indicator);

        /* create Indicator (little buttons) */
        CurrentPageIndicator mIndicator = new CurrentPageIndicator(MainActivity.getInstance(), mLinearLayout, mPager, R.drawable.indicator_circle);
        mIndicator.setPageCount(listFragments.size());
        mIndicator.show();
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

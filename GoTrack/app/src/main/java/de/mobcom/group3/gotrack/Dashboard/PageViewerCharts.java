package de.mobcom.group3.gotrack.Dashboard;

import android.graphics.Color;
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
import de.mobcom.group3.gotrack.Charts.BarChartFragment;
import de.mobcom.group3.gotrack.MainActivity;
import de.mobcom.group3.gotrack.R;
import de.mobcom.group3.gotrack.Recording.Recording_UI.CurrentPageIndicator;
import java.util.ArrayList;
import java.util.List;

public class PageViewerCharts extends Fragment {


    private List<Fragment> listFragments = new ArrayList<>();

    public PageViewerCharts() {
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
        View view = inflater.inflate(R.layout.fragment_page_viewer_charts, container, false);

        //TODO funktionstüchtig einbauen
        /* Daten aus Datenbank auslesen
        RouteDAO dao = new RouteDAO(MainActivity.getInstance());
        List<Route> records = dao.readLastSevenDays(MainActivity.getActiveUser());
        double[] distanceArray = {0, 1, 2, 3, 5, 5, 6, 7, 0};
        double[] timeArray = {0, 1, 2, 3, 5, 5, 6, 7, 0};
        double distance = 0;
        double time = 0;
        long prevDate=0;
        int position=1;
        for (int i = 0; i < records.size(); i++) {
            long curDate = records.get(i).getLocations().get(0).getTime();
            double curDistance = records.get(i).getDistance();
            double curTime= records.get(i).getTime();

            if (curDate == prevDate) {
                distance = distance + curDistance;
                time = time + curTime;
            }else{
                if(i!=0) {
                    distanceArray[position] = distance;
                    timeArray[position] = time;
                    position++;
                }
                prevDate = curDate;
                distance=curDistance;
                time= curTime;
            }
        }*/
        double[] distanceArray = {0, 1, 7, 3, 5, 5, 6, 7, 0};
        double[] timeArray = {0, 1, 2, 1, 5, 3, 6, 7, 0};

        /* Distanz der Woche */
        Bundle bundleDistance = new Bundle();
        bundleDistance.putDoubleArray("array", distanceArray);
        bundleDistance.putString("title", "Distanz der Woche");
        bundleDistance.putInt("color", Color.RED);
        bundleDistance.putString("rangeTitle", "km/h");
        BarChartFragment barFragDistance = new BarChartFragment();
        barFragDistance.setArguments(bundleDistance);

        /* Laufzeit der Woche */
        Bundle bundleTime = new Bundle();
        bundleTime.putDoubleArray("array", timeArray);
        bundleTime.putString("title", "Laufzeit der Woche");
        bundleTime.putInt("color", Color.GREEN);
        bundleTime.putString("rangeTitle", "Stunden");
        BarChartFragment barFragTime = new BarChartFragment();
        barFragTime.setArguments(bundleTime);

        listFragments.add(barFragDistance);
        listFragments.add(barFragTime);

        // Instantiate a ViewPager and a PagerAdapter.
        ViewPager mPager = view.findViewById(R.id.pager);
        PagerAdapter mPagerAdapter = new PageViewerCharts.ScreenSlidePagerAdapter(MainActivity.getInstance().getSupportFragmentManager());

        mPager.setAdapter(mPagerAdapter);

        LinearLayout mLinearLayout = view.findViewById(R.id.indicator);

        /* create Indicator (little buttons) */
        CurrentPageIndicator mIndicator = new CurrentPageIndicator(MainActivity.getInstance(), mLinearLayout, mPager, R.drawable.indicator_circle_theme_color);
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

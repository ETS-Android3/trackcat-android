package de.mobcom.group3.gotrack.Dashboard;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import de.mobcom.group3.gotrack.Charts.BarChartFragment;
import de.mobcom.group3.gotrack.Database.DAO.RouteDAO;
import de.mobcom.group3.gotrack.Database.Models.Route;
import de.mobcom.group3.gotrack.MainActivity;
import de.mobcom.group3.gotrack.R;
import de.mobcom.group3.gotrack.Recording.Recording_UI.CurrentPageIndicator;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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

        // TODO: verschiedene Tage Testen (Strecken eines einzelnen Tages funktionieren)
        //Daten aus Datenbank auslesen
        RouteDAO dao = new RouteDAO(MainActivity.getInstance());
        List<Route> records = dao.readLastSevenDays(MainActivity.getActiveUser());
        double[] distanceArray = {0, 0, 0, 0, 0, 0, 0, 0, 0};
        double[] timeArray = {0, 0, 0, 0, 0, 0, 0, 0, 0};
        double[] timeArrayMinutes = {0, 0, 0, 0, 0, 0, 0, 0, 0};
        double[] timeArrayHours = {0, 0, 0, 0, 0, 0, 0, 0, 0};
        double distance = 0;
        double time = 0;
        String prevDateString = getDate(records.get(0).getLocations().get(0).getTime(), "dd/MM/yyyy");
        int maxDistance = 0;
        int maxTime = 0;
        for (int i = 0; i < records.size(); i++) {
            long curDate = records.get(i).getLocations().get(0).getTime();
            double curDistance = records.get(i).getDistance();
            double curTime= records.get(i).getTime();
            String curDateString = getDate(curDate, "dd/MM/yyyy");
            int dayOfWeek = getWeekDay(curDate);

            if (curDateString.equals(prevDateString)) {
                distance = distance + curDistance;
                time = time + curTime;

                if(maxDistance < distance){
                    maxDistance = (int)distance;
                }
                if(maxTime < time){
                    maxTime = (int)time;
                }

                distanceArray[dayOfWeek] = distance;
                timeArray[dayOfWeek] = time;
                timeArrayMinutes[dayOfWeek] = time / 60;
                timeArrayHours[dayOfWeek] = time / (60 * 60);
            }else{
                prevDateString = curDateString;
                distance = curDistance;
                time = curTime;
            }

        }

        /* Distanz der Woche */
        Bundle bundleDistance = new Bundle();
        bundleDistance.putDoubleArray("array", distanceArray);
        bundleDistance.putString("title", "Distanz der Woche");
        bundleDistance.putInt("color", Color.RED);
        bundleDistance.putString("rangeTitle", "Meter");

        if(maxDistance <= 100){
            bundleDistance.putDouble("stepsY", 10);
        }else if(maxDistance <= 1000){
            bundleDistance.putDouble("stepsY", 100);
        }else if(maxDistance <= 10000){
            bundleDistance.putDouble("stepsY", 1000);
        }

        BarChartFragment barFragDistance = new BarChartFragment();
        barFragDistance.setArguments(bundleDistance);

        /* Laufzeit der Woche */
        Bundle bundleTime = new Bundle();
        bundleTime.putString("title", "Laufzeit der Woche");
        bundleTime.putInt("color", Color.GREEN);

        if(maxTime < 60){
            bundleTime.putDouble("stepsY", 10);
            bundleTime.putString("rangeTitle", "Sekunden");
            bundleTime.putDoubleArray("array", timeArray);
        }else if(maxTime < 300){
            bundleTime.putDouble("stepsY", 0.5);
            bundleTime.putString("rangeTitle", "Minuten");
            bundleTime.putDoubleArray("array", timeArrayMinutes);
        }else if(maxTime < 600){
            bundleTime.putDouble("stepsY", 1);
            bundleTime.putString("rangeTitle", "Minuten");
            bundleTime.putDoubleArray("array", timeArrayMinutes);
        }else if(maxTime < 1200){
            bundleTime.putDouble("stepsY", 2);
            bundleTime.putString("rangeTitle", "Minuten");
            bundleTime.putDoubleArray("array", timeArrayMinutes);
        }else if(maxTime < 1800){
            bundleTime.putDouble("stepsY", 3);
            bundleTime.putString("rangeTitle", "Minuten");
            bundleTime.putDoubleArray("array", timeArrayMinutes);
        }else if(maxTime < 2400){
            bundleTime.putDouble("stepsY", 4);
            bundleTime.putString("rangeTitle", "Minuten");
            bundleTime.putDoubleArray("array", timeArrayMinutes);
        }else if(maxTime < 3000){
            bundleTime.putDouble("stepsY", 5);
            bundleTime.putString("rangeTitle", "Minuten");
            bundleTime.putDoubleArray("array", timeArrayMinutes);
        }else if(maxTime < 3600){
            bundleTime.putDouble("stepsY", 6);
            bundleTime.putString("rangeTitle", "Minuten");
            bundleTime.putDoubleArray("array", timeArrayMinutes);
        }else if(maxTime < 18000){
            bundleTime.putDouble("stepsY", 0.5);
            bundleTime.putString("rangeTitle", "Stunden");
            bundleTime.putDoubleArray("array", timeArrayHours);
        }else if(maxTime >= 18000){
            bundleTime.putDouble("stepsY", 1);
            bundleTime.putString("rangeTitle", "Stunden");
            bundleTime.putDoubleArray("array", timeArrayHours);
        }

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

    private String getDate(long millis, String dateFormat){
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);
        return formatter.format(calendar.getTime());
    }

    private int getWeekDay(long millis){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

        return dayOfWeek;
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

package de.mobcom.group3.gotrack.Dashboard;

import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
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
    private static final String PREF_DARK_THEME = "dark_theme";
    private int colorAccent;
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

        if(PreferenceManager.getDefaultSharedPreferences(getContext()).getBoolean(PREF_DARK_THEME, false)){
            colorAccent = getResources().getColor(R.color.colorGreyAccent);
        }else{
            colorAccent = getResources().getColor(R.color.colorGreenAccent);
        }

        //Daten aus Datenbank auslesen
        RouteDAO dao = new RouteDAO(MainActivity.getInstance());
        List<Route> records = dao.readLastSevenDays(MainActivity.getActiveUser());
        double[] distanceArray = {0, 0, 0, 0, 0, 0, 0, 0, 0},
                distanceArrayKm = {0, 0, 0, 0, 0, 0, 0, 0, 0},
                timeArray = {0, 0, 0, 0, 0, 0, 0, 0, 0},
                timeArrayMinutes = {0, 0, 0, 0, 0, 0, 0, 0, 0},
                timeArrayHours = {0, 0, 0, 0, 0, 0, 0, 0, 0};
        double distance = 0,
                time = 0,
                maxDistance = 0,
                maxDistanceKm = 0,
                maxTime = 0,
                maxTimeMinutes = 0,
                maxTimeHours = 0;
        int prevDay = 0;

        // Der Code wird nur ausgeführt wenn es Strecken gibt. Sonst bleibt das Array bei null und somit ein leerer Graph
        if(records.size() > 0) {
            for (int i = 0; i < records.size(); i++) {
                long curDate = records.get(i).getDate();
                double curDistance = records.get(i).getDistance();
                double curTime = records.get(i).getTime();
                int dayOfWeek = getWeekDay(curDate);

                if(dayOfWeek == prevDay){
                    // Wenn es sich bei einem Datensatz ums selbe Datum handelt werden die Variablen aufsummiert
                    distance = distance + curDistance;
                    time = time + curTime;
                } else {
                    // Wenn ein neues Datum erreicht wurde, werden die Variablen mit dem ersten Datensatz erstellt
                    prevDay = dayOfWeek;
                    distance = curDistance;
                    time = curTime;
                }
                // Wenn die neue Zeit oder Distanz größer ist als die alte max werden die variablen überschrieben
                if (maxDistance < distance) {
                    maxDistance = distance;
                    maxDistanceKm = distance / 1000;
                }
                if (maxTime < time) {
                    maxTime = time;
                    maxTimeMinutes = time /(60);
                    maxTimeHours = time /(60 * 60);
                }
                // Die für die Plots notwendigen Arrays werden erstellt
                distanceArray[dayOfWeek] = distance;
                distanceArrayKm[dayOfWeek] = distance / 1000;

                timeArray[dayOfWeek] = time;
                timeArrayMinutes[dayOfWeek] = time / 60;
                timeArrayHours[dayOfWeek] = time / (60 * 60);
            }
        }

        /* Distanz der Woche */
        Bundle bundleDistance = new Bundle();
        bundleDistance.putString("title", "Distanz der Woche");
        bundleDistance.putInt("color", colorAccent);

        if(maxDistance < 1000){
            bundleDistance.putDoubleArray("array", distanceArray);
            bundleDistance.putString("rangeTitle", "Meter");
            bundleDistance.putDouble("stepsY", maxDistance / 5);
        }else{
            bundleDistance.putDoubleArray("array", distanceArrayKm);
            bundleDistance.putString("rangeTitle", "Km");
            bundleDistance.putDouble("stepsY", (maxDistanceKm) / 5);
        }

        BarChartFragment barFragDistance = new BarChartFragment();
        barFragDistance.setArguments(bundleDistance);

        /* Laufzeit der Woche */
        Bundle bundleTime = new Bundle();
        bundleTime.putString("title", "Laufzeit der Woche");
        bundleTime.putInt("color", colorAccent);


        // Die Schrittweise der Plot Range wird an den höchsten Time Wert angepasst
        // Dies Verhindert eine überladene UI
        if (maxTime < 60) {
            bundleTime.putDouble("stepsY", 10);
            bundleTime.putString("rangeTitle", "Sekunden");
            bundleTime.putDoubleArray("array", timeArray);
        }else if(maxDistance < 3600){
            bundleTime.putDouble("stepsY", maxTimeMinutes / 5);
            bundleTime.putString("rangeTitle", "Minuten");
            bundleTime.putDoubleArray("array", timeArrayMinutes);
        }else{
            bundleTime.putDouble("stepsY", maxTimeHours / 5);
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
        if (Build.VERSION.SDK_INT > 21) {
            CurrentPageIndicator mIndicator = new CurrentPageIndicator(MainActivity.getInstance(), mLinearLayout, mPager, R.drawable.indicator_circle_theme_color);
            mIndicator.setPageCount(listFragments.size());
            mIndicator.show();
        } else {
            CurrentPageIndicator mIndicator = new CurrentPageIndicator(MainActivity.getInstance(), mLinearLayout, mPager, R.drawable.indicator_circle_v21);
            mIndicator.setPageCount(listFragments.size());
            mIndicator.show();
        }

        return view;
    }
    // Der Wochentag der Aktuellen Strecke wird als int zurückgegeben
    private int getWeekDay(long millis) {
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

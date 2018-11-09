package com.example.finnl.gotrack.Recording;

import android.annotation.SuppressLint;
import android.support.v4.app.Fragment;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.finnl.gotrack.MainActivity;
import com.example.finnl.gotrack.R;
import com.example.finnl.gotrack.Recording.Recording_UI.KMH_View_Fragment;
import com.example.finnl.gotrack.Recording.Recording_UI.CurrentPageIndicator;
import com.example.finnl.gotrack.Recording.Recording_UI.TimeTotal_View_Fragment;
import com.example.finnl.gotrack.Statistics.mCounter;
import com.example.finnl.gotrack.Statistics.SpeedAverager;

import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;

import java.util.ArrayList;
import java.util.List;

/*
 * Fragment for Track recording. includes GPS Locator and Statistics
 * Results displayed in this Fragment view
 * */

public class RecordFragment extends Fragment {
    private mCounter kmCounter;

    private Timer timer;
    private Timer rideTimer;
    private SpeedAverager kmhAverager;

    private MapView mMapView;
    private MapController mMapController;
    private Marker startMarker;

    private Polyline mPath;
    public static Handler handler;

    private ArrayList<GeoPoint> GPSData = new ArrayList<>();

    private KMH_View_Fragment kmhFrag;
    private TimeTotal_View_Fragment timeFrag;

    private List<android.support.v4.app.Fragment> listFragments = new ArrayList<>();


    private TextView kmh_TextView;
    private TextView time_TextView;
    private TextView distance_TextView;
    private TextView average_speed_TextView;
    private TextView altimeter_TextView;

    private boolean isTracking = false;

    CurrentPageIndicator mIndicator;

    private View view;

    @SuppressLint("HandlerLeak")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        /* ----------------------------------------------------------------------------------handler
         * recieves messages from another thread
         *
         * handler recieves data from Timer Thread
         */
        handler = new Handler() {
            @Override
            public void handleMessage(android.os.Message msg) {
                if (msg.what == 0) {
                    /*
                     * set Time in TextView
                     * */
                    try {
                        time_TextView = view.findViewById(R.id.time_TextView);
                        time_TextView.setText(msg.obj + "");
                    } catch (NullPointerException e) {
                    }
                    try {
                        average_speed_TextView = view.findViewById(R.id.average_speed_TextView);
                        average_speed_TextView.setText(Math.round((kmhAverager.getAvgSpeed() * 60 * 60) / 100) / 10.0 + " km/h");
                    } catch (NullPointerException e) {
                    }
                } else if (msg.what == 1) {
                    //setRideTime((String) msg.obj);
                }
            }
        };
        /*
         * #########################################################################################
         */

        /*
         *------------------------------------------------------------------------------------------
         *Inflate the layout for this fragment
         *
         * */
        view = inflater.inflate(R.layout.record_fragment_main, container, false);

        mMapView = (MapView) view.findViewById(R.id.mapview);
        mMapView.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE);

        // kackhässliche ZoomControls--> für abgabe AUS!!!!!!todo
        mMapView.setBuiltInZoomControls(true);

        mMapView.setMultiTouchControls(true);
        mMapController = (MapController) mMapView.getController();
        startMarker = new Marker(mMapView);
        mPath = new Polyline(mMapView);


        mMapView.getOverlays().add(mPath);
        mMapView.getOverlays().add(startMarker);


        kmhFrag = new KMH_View_Fragment();
        timeFrag = new TimeTotal_View_Fragment();

        listFragments.add(kmhFrag);
        listFragments.add(timeFrag);

        /*
         * Swipe view of kmh/Time
         * */
        ViewPager mPager;
        PagerAdapter mPagerAdapter;

        // Instantiate a ViewPager and a PagerAdapter.
        mPager = (ViewPager) view.findViewById(R.id.pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(MainActivity.getInstance().getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);

        /*
         * indicatior (little dots)
         * */
        LinearLayout mLinearLayout = view.findViewById(R.id.indicator);

        mIndicator = new CurrentPageIndicator(MainActivity.getInstance(), mLinearLayout, mPager, R.drawable.indicator_circle);
        mIndicator.setPageCount(listFragments.size());
        mIndicator.show();


        /*start Tracking*/// TODO: 02.11.2018
        //startTracking();
        return view;
    }

    /* starts GPS Tracker and recording Objects */
    public void startTracking() {
        // start Locator
        new Locator(MainActivity.getInstance(), this);

        kmCounter = new mCounter();

        // timer
        timer = new Timer(0);

        // ride Time if kmh > 0
        rideTimer = new Timer(1);

        // average Kmh
        kmhAverager = new SpeedAverager(MainActivity.getInstance(), kmCounter, timer, 1);

        isTracking = true;
    }

    public boolean isTracking(){
        return isTracking;
    }

    //##############################################################################################


    /*
     * get Location Update in this class
     * Draw Position and Track in OSM
     * Store data in Arraylist
     * Calculate Statistics
     *----------------------------------------------------------------------------------------------
     */
    public void updateLocation(Location location) {
        GeoPoint gPt = new GeoPoint(location.getLatitude(), location.getLongitude());

        // add to List
        GPSData.add(gPt);

        /*
         * move Map and Zoom
         * */
        mMapController.setZoom(18);
        mMapController.setCenter(gPt);

        /*
         * set Polyline
         * */
        mPath.setPoints(GPSData);
        mPath.setColor(Color.RED);
        mPath.setWidth(4);

        /*
         * set Marker for current Position
         * */
        startMarker.setPosition(gPt);
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);

        /*
         * updatde OSM Map
         * */
        mMapView.invalidate();


        /*
         * add Location to Statistics
         * */

        // add Distance
        kmCounter.addKm(location);

        // count ridetime
        if (!rideTimer.getActive() && location.getSpeed() > 0) {
            rideTimer.startTimer();
        } else if (rideTimer.getActive() && location.getSpeed() == 0) {
            rideTimer.stopTimer();
        }

        kmhAverager.calcAvgSpeed();


        /*
         *
         * Set Values in Satistics
         *
         *
         * */

        /* set Speed value in TextView */
        try {
            kmh_TextView = view.findViewById(R.id.kmh_TextView);
            kmh_TextView.setText((Math.round(location.getSpeed() * 60 * 60) / 100) / 10.0 + " km/h");
        } catch (NullPointerException e) {
        }
        try {
            distance_TextView = view.findViewById(R.id.distance_TextView);
            distance_TextView.setText(Math.round(kmCounter.getAmount()) / 1000.0 + " km");
        } catch (NullPointerException e) {
        }
        try {
            altimeter_TextView = view.findViewById(R.id.altimeter_TextView);
            altimeter_TextView.setText(location.getAltitude() + " m");
        } catch (NullPointerException e) {
        }


    }
    /*
     *##############################################################################################
     */


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

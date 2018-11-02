package com.example.finnl.gotrack.Recording;

import android.app.Fragment;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.finnl.gotrack.MainActivity;
import com.example.finnl.gotrack.R;
import com.example.finnl.gotrack.Statistics.mCounter;
import com.example.finnl.gotrack.Statistics.SpeedAverager;

import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;

import java.util.ArrayList;

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
                    //setTime((String) msg.obj);
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
        View view = inflater.inflate(R.layout.record_fragment, container, false);

        mMapView = (MapView) view.findViewById(R.id.mapview);
        mMapView.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE);

        // kackhässliche ZoomControls--> für abgabe AUS!!!!!!todo
        mMapView.setBuiltInZoomControls(true);

        mMapView.setMultiTouchControls(true);
        mMapController = (MapController) mMapView.getController();
        startMarker = new Marker(mMapView);
        mPath = new Polyline(mMapView);


        /*start Tracking*/// TODO: 02.11.2018
        startTracking();

        mMapView.getOverlays().add(mPath);
        mMapView.getOverlays().add(startMarker);


        return view;
    }

    /* starts GPS Tracker and recording Objects */
    private void startTracking() {
        // start Locator
        new Locator(MainActivity.getInstance(), this);

        kmCounter = new mCounter();

        // timer
        timer = new Timer(0);

        // ride Time if kmh > 0
        rideTimer = new Timer(1);

        // average Kmh
        kmhAverager = new SpeedAverager(MainActivity.getInstance(), kmCounter, timer, 1);
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


    }
    /*
     *##############################################################################################
     */
}

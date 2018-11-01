package com.example.finnl.gotrack.Recording;

import android.app.Fragment;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.finnl.gotrack.MainActivity;
import com.example.finnl.gotrack.R;
import com.example.finnl.gotrack.Statistics.KmCounter;
import com.example.finnl.gotrack.Statistics.KmhAverager;

import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;

import java.util.ArrayList;

public class RecordFragment extends Fragment {
    private KmCounter kmCounter;

    private Timer timer;
    private Timer rideTimer;
    private KmhAverager kmhAverager;

    private MapView mMapView;
    private MapController mMapController;
    private Marker startMarker;

    private Polyline mPath;

    private ArrayList<GeoPoint> GPSData = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.record_fragment, container, false);

        mMapView = (MapView) view.findViewById(R.id.mapview);
        mMapView.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE);

        // kackhässliche ZoomControls--> für abgabe AUS!!!!!!
        mMapView.setBuiltInZoomControls(true);

        mMapView.setMultiTouchControls(true);
        mMapController = (MapController) mMapView.getController();
        startMarker = new Marker(mMapView);
        mPath = new Polyline(mMapView);


        // start Tracking
        startTracking();



        return view;
    }

    private void startTracking() {


        // start Locator
        new Locator(MainActivity.getInstance(), this);

        kmCounter = new KmCounter(MainActivity.getInstance());

        // timer
        timer = new Timer(MainActivity.getInstance(), 0);

        // ride Time if kmh > 0
        rideTimer = new Timer(MainActivity.getInstance(), 1);

        // average Kmh
        kmhAverager = new KmhAverager(MainActivity.getInstance(), kmCounter, timer, 1);
    }

    //##############################################################################################


    // get Location Update in this class
    //----------------------------------------------------------------------------------------------

    public void updateLocation(Location location) {
        // test View
        GPSData.add(new GeoPoint(location.getLatitude(), location.getLongitude()));


        startMarker.remove(mMapView);
        mMapView.getOverlays().remove(mPath);



        mMapController.setZoom(18);
        GeoPoint gPt = new GeoPoint(location.getLatitude(), location.getLongitude());
        mMapController.setCenter(gPt);




        mPath.setPoints(GPSData);
        mPath.setColor(Color.RED);
        mPath.setWidth(4);


        mMapView.getOverlays().add(mPath);




        startMarker.setPosition(gPt);
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        mMapView.getOverlays().add(startMarker);
        mMapView.invalidate();

        // add Distance
        kmCounter.addKm(location);

        // count ridetime
        if (!rideTimer.getActive() && location.getSpeed() > 0) {
            rideTimer.startTimer();
        } else if (rideTimer.getActive() && location.getSpeed() == 0) {
            rideTimer.killTimer();
        }

        kmhAverager.calcAvgSpeed();


    }
    //##############################################################################################

}

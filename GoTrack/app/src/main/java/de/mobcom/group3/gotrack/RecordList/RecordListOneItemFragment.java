package de.mobcom.group3.gotrack.RecordList;

import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;

import java.util.ArrayList;

import de.mobcom.group3.gotrack.Database.DAO.RouteDAO;
import de.mobcom.group3.gotrack.Database.Models.Route;
import de.mobcom.group3.gotrack.MainActivity;
import de.mobcom.group3.gotrack.R;
import de.mobcom.group3.gotrack.Recording.Timer;


public class RecordListOneItemFragment extends Fragment {

    // DataModel
    private Route model;

    // view Element
    private View fragmentView;

    private double height = 0;
    private double maxSpeed = 0;

    private ArrayList<GeoPoint> GPSData = new ArrayList<>();


    /*
     * Model reciever
     * */
    public void setModel(Route model) {
        this.model = model;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        fragmentView = inflater.inflate(R.layout.fragment_record_list_one_item, container, false);
        setButtons();
        for (int i = 0; i < model.getLocations().size(); i++) {
            Location location = model.getLocations().get(i);

            GeoPoint gPt = new GeoPoint(location.getLatitude(), location.getLongitude());
            GPSData.add(gPt);

            if (i > 1) {
                double distance = model.getLocations().get(i - 1).getAltitude() - location.getAltitude();
                if (distance < 0) {
                    distance = distance * -1;
                }
                height = height + distance;

                if(location.getSpeed()>maxSpeed){
                    maxSpeed = location.getSpeed();
                }
            }

        }
        drawRoute();

        setData();

        return fragmentView;
    }

    private void setData() {

        /*
         * Set Average Speed
         * */
        TextView average_speed_TextView = fragmentView.findViewById(R.id.average_speed_TextView);
        String toSet = Math.round(((model.getDistance() / model.getTime()) * 60 * 60) / 100) / 10.0 + " km/h";
        average_speed_TextView.setText(toSet);

        /*
         * Set real Average Speed
         * */
        TextView real_average_speed_TextView = fragmentView.findViewById(R.id.real_average_speed_TextView);
        toSet = Math.round(((model.getDistance() / model.getRideTime()) * 60 * 60) / 100) / 10.0 + " km/h";
        real_average_speed_TextView.setText(toSet);


        /*
         * Set Distance
         * */
        TextView distance_TextView = fragmentView.findViewById(R.id.distance_TextView);
        toSet = Math.round(model.getDistance()) / 1000.0 + " km";
        distance_TextView.setText(toSet);

        /*
         * Set altimeter
         * */
        TextView altimeter_TextView = fragmentView.findViewById(R.id.altimeter_TextView);
        toSet = "± " + Math.round(height) + " m";
        altimeter_TextView.setText(toSet);

        /*
         * Set total Time
         * */
        TextView total_time_TextView = fragmentView.findViewById(R.id.total_time_TextView);
        Timer timerForCalc = new Timer();
        toSet = timerForCalc.secToString(model.getTime());
        total_time_TextView.setText(toSet);

        /*
         * Set ride Time
         * */
        TextView real_time_TextView = fragmentView.findViewById(R.id.real_time_TextView);
        toSet = timerForCalc.secToString(model.getRideTime());
        real_time_TextView.setText(toSet);

        /*
         * Set Max Speed
         * */
        TextView max_speed_TextView = fragmentView.findViewById(R.id.max_speed_TextView);
        toSet = (Math.round(maxSpeed * 60 * 60) / 100) / 10.0 + " km/h";
        max_speed_TextView.setText(toSet);


    }

    private void setButtons() {

        Button saveButton = fragmentView.findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO Save Model in DB
                RouteDAO dao = new RouteDAO(MainActivity.getInstance());
                dao.create(model);

                switchBack();
            }
        });

        Button discardButton = fragmentView.findViewById(R.id.discardButton);
        discardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO Discard Model
                switchBack();
            }
        });

    }

    // switch
    private void switchBack() {
        FragmentTransaction fragTransaction = MainActivity.getInstance().getSupportFragmentManager().beginTransaction();
        fragTransaction.replace(R.id.mainFrame, MainActivity.getInstance().getRecordFragment(), getResources().getString(R.string.fRecord));
        fragTransaction.commit();
    }

    private void drawRoute() {
        MapView mMapView = fragmentView.findViewById(R.id.mapview);
        mMapView.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE);
        mMapView.setBuiltInZoomControls(false);
        mMapView.setMultiTouchControls(true);
        MapController mMapController = (MapController) mMapView.getController();
        mMapController.setZoom(19);

        /*
         * add Marker and Polyline
         * */

        GeoPoint gPt = new GeoPoint(model.getLocations().get(0).getLatitude(), model.getLocations().get(0).getLongitude());
        Marker startMarker = new Marker(mMapView);
        startMarker.setPosition(gPt);
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        startMarker.setIcon(MainActivity.getInstance().getResources().getDrawable(R.drawable.ic_map_record_start));


        gPt = new GeoPoint(model.getLocations().get(model.getLocations().size() - 1).getLatitude(), model.getLocations().get(model.getLocations().size() - 1).getLongitude());
        Marker stopMarker = new Marker(mMapView);
        stopMarker.setPosition(gPt);
        stopMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        stopMarker.setIcon(MainActivity.getInstance().getResources().getDrawable(R.drawable.ic_map_record_end));

        Polyline mPath = new Polyline(mMapView);

        mMapView.getOverlays().add(mPath);
        mMapView.getOverlays().add(startMarker);
        mMapView.getOverlays().add(stopMarker);

        mPath.setPoints(GPSData);
        mPath.setColor(Color.RED);
        mPath.setWidth(4);

        gPt = new GeoPoint(model.getLocations().get(model.getLocations().size() / 2).getLatitude(), model.getLocations().get(model.getLocations().size() / 2).getLongitude());
        mMapController.setCenter(gPt);
    }
}

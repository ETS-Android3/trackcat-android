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


public class RecordListOneItemFragment extends Fragment {

    // DataModel
    private Route model;

    // view Element
    private View fragmentView;

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

        }


        drawRoute();


        return fragmentView;
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
        fragTransaction.replace(R.id.mainFrame, MainActivity.getInstance().getRecordFragment(), "RECORD");
        fragTransaction.commit();
    }

    private void drawRoute() {

        MapView mMapView = fragmentView.findViewById(R.id.mapview);
        mMapView.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE);

        // kackhässliche ZoomControls----------------------------------------------------------------> für abgabe AUS!!!!!!todo
        mMapView.setBuiltInZoomControls(true);

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
        // TODO Timo icon aussuchen
        startMarker.setIcon(MainActivity.getInstance().getResources().getDrawable(R.drawable.ic_map_record_start));


        gPt = new GeoPoint(model.getLocations().get(model.getLocations().size() - 1).getLatitude(), model.getLocations().get(model.getLocations().size() - 1).getLongitude());
        Marker stopMarker = new Marker(mMapView);
        stopMarker.setPosition(gPt);
        stopMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        // TODO Timo icon aussuchen

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

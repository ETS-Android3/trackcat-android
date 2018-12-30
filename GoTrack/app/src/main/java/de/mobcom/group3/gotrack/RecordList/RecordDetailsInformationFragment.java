package de.mobcom.group3.gotrack.RecordList;

import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;
import de.mobcom.group3.gotrack.Database.DAO.RouteDAO;
import de.mobcom.group3.gotrack.Database.Models.Route;
import de.mobcom.group3.gotrack.MainActivity;
import de.mobcom.group3.gotrack.R;

public class RecordDetailsInformationFragment extends Fragment {
    public RecordDetailsInformationFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    View view;
    ArrayList<GeoPoint> GPSData = new ArrayList<>();
    private double height = 0;
    private double altitudeUp = 0;
    private double altitudeDown = 0;
    private double maxSpeed = 0;
    ArrayList<Location> locations;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        int id = getArguments().getInt("id");
        view = inflater.inflate(R.layout.fragment_record_details_information, container, false);

        /*Auslesen der werte aus der Datenbank*/
        RouteDAO dao = new RouteDAO(MainActivity.getInstance());
        Route record = dao.read(id);
        locations = record.getLocations();
        double prevDistance=0;

        /*Auslesen der Locations und ermitteln der Höhe und der maximalen Geschwindigkeit*/
        for (int i = 0; i < locations.size(); i++) {
            Location location = record.getLocations().get(i);

            GeoPoint gPt = new GeoPoint(location.getLatitude(), location.getLongitude());
            GPSData.add(gPt);

            double distance = record.getLocations().get(i - 1).getAltitude() - location.getAltitude();

            if (i > 1) {
                if (distance < 0) {
                    distance = distance * -1;
                }

                /* Berechnung der Höhenmeter */
                if (distance>prevDistance){
                    altitudeUp=altitudeUp+distance;
                }else{
                    altitudeDown=altitudeDown+distance;
                }
                height = height + distance;

                if (location.getSpeed() > maxSpeed) {
                    maxSpeed = location.getSpeed();
                }
            }
            prevDistance=distance;
        }

        /*DateFormat setzen*/
        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
        TimeZone tz = TimeZone.getTimeZone("UTC");
        df.setTimeZone(tz);

        /*Name setzen*/
        TextView name_TextView = view.findViewById(R.id.name_TextView);
        String toSet = record.getName();
        name_TextView.setText(toSet);

        /*Average Speed setzen*/
        TextView average_speed_TextView = view.findViewById(R.id.average_speed_TextView);
        toSet = Math.round(((record.getDistance() / record.getTime()) * 60 * 60) / 100) / 10.0 + " km/h";
        average_speed_TextView.setText(toSet);

        /*Real Average Speed setzen*/
        TextView real_average_speed_TextView = view.findViewById(R.id.real_average_speed_TextView);
        toSet = Math.round(((record.getDistance() / record.getRideTime()) * 60 * 60) / 100) / 10.0 + " km/h";
        real_average_speed_TextView.setText(toSet);

        /*Distance setzen*/
        TextView distance_TextView = view.findViewById(R.id.distance_TextView);
        toSet = Math.round(record.getDistance()) / 1000.0 + " km";
        distance_TextView.setText(toSet);

        /*Altimeter setzen*/
        TextView altimeter_TextView = view.findViewById(R.id.altimeter_TextView);
        toSet = "± " + Math.round(height) + " m";
        altimeter_TextView.setText(toSet);

        /*TotalTime setzen*/
        TextView total_time_TextView = view.findViewById(R.id.total_time_TextView);
        String time = df.format(new Date(record.getTime() * 1000));
        total_time_TextView.setText(time);

        /*RideTime setzen*/
        TextView real_time_TextView = view.findViewById(R.id.real_time_TextView);
        time = df.format(new Date(record.getRideTime() * 1000));
        real_time_TextView.setText(time);

        /*MaxSpeed setzen*/
        TextView max_speed_TextView = view.findViewById(R.id.max_speed_TextView);
        toSet = (Math.round(maxSpeed * 60 * 60) / 100) / 10.0 + " km/h";
        max_speed_TextView.setText(toSet);

        /*Route anzeigen*/
        drawRoute();

        return view;
    }

    private void drawRoute() {
        MapView mMapView = view.findViewById(R.id.mapview);
        mMapView.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE);
        mMapView.setBuiltInZoomControls(false);
        mMapView.setMultiTouchControls(true);
        MapController mMapController = (MapController) mMapView.getController();
        mMapController.setZoom(19);

        /*Poliline und Marker erstellen*/
        GeoPoint gPt = new GeoPoint(locations.get(0).getLatitude(), locations.get(0).getLongitude());
        Marker startMarker = new Marker(mMapView);
        startMarker.setPosition(gPt);
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        startMarker.setIcon(MainActivity.getInstance().getResources().getDrawable(R.drawable.ic_map_record_start));


        gPt = new GeoPoint(locations.get(locations.size() - 1).getLatitude(), locations.get(locations.size() - 1).getLongitude());
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

        gPt = new GeoPoint(locations.get(locations.size() / 2).getLatitude(), locations.get(locations.size() / 2).getLongitude());
        mMapController.setCenter(gPt);
    }
}

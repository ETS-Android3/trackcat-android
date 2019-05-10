package de.trackcat.RecordList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import de.trackcat.CustomElements.CustomLocation;
import de.trackcat.Database.DAO.LocationDAO;
import de.trackcat.Database.DAO.RouteDAO;
import de.trackcat.Database.Models.Location;
import de.trackcat.Database.Models.Route;
import de.trackcat.MainActivity;
import de.trackcat.R;

public class RecordDetailsInformationFragment extends Fragment implements View.OnClickListener {
    public RecordDetailsInformationFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    View view;
    ArrayList<GeoPoint> GPSData = new ArrayList<>();
    private double altitudeUp = 0;
    private double altitudeDown = 0;
    private double maxSpeed = 0;
    private MapView mMapView = null;
    List<Location> locations;
    Route record;
    RouteDAO dao;
    LocationDAO locationDao;
    TextView recordName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        int id = getArguments().getInt("id");
        view = inflater.inflate(R.layout.fragment_record_details_information, container, false);

        /* Auslesen der Werte aus der Datenbank */
        dao = new RouteDAO(MainActivity.getInstance());
        record = dao.read(id);

        locationDao = new LocationDAO(MainActivity.getInstance());
        locations = locationDao.readAll(record.getId());
        /* Auslesen der Locations und Ermitteln der Höhe und der maximalen Geschwindigkeit */
        for (int i = 0; i < locations.size(); i++) {
            Log.v("iiiiiii------------", i + "");
            Location location = locations.get(i);

            GeoPoint gPt = new GeoPoint(location.getLatitude(), location.getLongitude());
            GPSData.add(gPt);

            if (i > 0) {
                double difference = location.getAltitude() - locations.get(i - 1).getAltitude();

                /* Berechnung der Höhenmeter */
                if (difference > 0) {
                    altitudeUp += Math.abs(difference);
                } else if (difference < 0) {
                    altitudeDown += Math.abs(difference);
                }

                /* Maximalgeschwindigkeit ausrechnen */
                if (location.getSpeed() > maxSpeed) {
                    maxSpeed = location.getSpeed();
                }
            }

        }

        /* DateFormat setzen */
        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
        TimeZone tz = TimeZone.getTimeZone("UTC");
        df.setTimeZone(tz);

        /* Name setzen */
        recordName = view.findViewById(R.id.record_name);
        String toSet = record.getName();
        recordName.setText(toSet);

        /* Average Speed setzen */
        TextView averageSpeed = view.findViewById(R.id.average_speed_value);
        toSet = Math.round(((record.getDistance() / record.getRideTime()) * 60 * 60) / 100) / 10.0 + " km/h";
        averageSpeed.setText(toSet);

        /* Distanz setzen */
        TextView distance = view.findViewById(R.id.distance_value);
        toSet = Math.round(record.getDistance()) / 1000.0 + " km";
        distance.setText(toSet);

        /* Positive Altimeter setzen */
        TextView positiveAltimeter = view.findViewById(R.id.altimeter_pos_value);
        toSet = Math.round(altitudeUp) + " m";
        positiveAltimeter.setText(toSet);

        /* Negative Altimeter setzen */
        TextView negativeAltimeter = view.findViewById(R.id.altimeter_neg_value);
        toSet = Math.round(altitudeDown) + " m";
        negativeAltimeter.setText(toSet);

        /* TotalTime setzen */
        TextView totalTime = view.findViewById(R.id.total_time_value);
        String time = df.format(new Date(record.getTime() * 1000));
        totalTime.setText(time);

        /* RideTime setzen */
        TextView pauseTime = view.findViewById(R.id.pause_time_value);
        time = df.format(new Date((record.getTime() - record.getRideTime()) * 1000));
        pauseTime.setText(time);

        /* MaxSpeed setzen */
        TextView maxSpeed = view.findViewById(R.id.max_speed_value);
        toSet = (Math.round(this.maxSpeed * 60 * 60) / 100) / 10.0 + " km/h";
        maxSpeed.setText(toSet);

        /* Datum setzen */
        TextView recordDate = view.findViewById(R.id.date_value);
        long curDate = record.getDate();
        String curDateString = getDate(curDate, "dd.MM.yyyy");
        recordDate.setText(curDateString);

        /* Route anzeigen */
        drawRoute();

        /* Button zur Bearbeitung von Routen */
        ImageView editRouteName = view.findViewById(R.id.editRoute);
        editRouteName.setOnClickListener(this);


        view.setTag(view.getVisibility());
        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                double minLat = Double.MAX_VALUE;
                double maxLat = Double.MIN_VALUE;
                double minLong = Double.MAX_VALUE;
                double maxLong = Double.MIN_VALUE;


                for (GeoPoint point : GPSData) {
                    if (point.getLatitude() < minLat)
                        minLat = point.getLatitude();
                    if (point.getLatitude() > maxLat)
                        maxLat = point.getLatitude();
                    if (point.getLongitude() < minLong)
                        minLong = point.getLongitude();
                    if (point.getLongitude() > maxLong)
                        maxLong = point.getLongitude();
                }

                maxLat += 0.001;
                maxLong += 0.001;
                minLat -= 0.001;
                minLong -= 0.001;

                BoundingBox box = new BoundingBox();
                box.set(maxLat, maxLong, minLat, minLong);

                mMapView.zoomToBoundingBox(box, false);

                double zoomLvl = mMapView.getZoomLevelDouble();

                mMapView.getController().setZoom(zoomLvl - 0.3);
            }
        });

        return view;
    }

    private void drawRoute() {
        mMapView = view.findViewById(R.id.mapview);
        mMapView.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE);
        mMapView.setBuiltInZoomControls(false);
        mMapView.setMultiTouchControls(true);
        MapController mMapController = (MapController) mMapView.getController();
        mMapController.setZoom(19);

        /* Poliline und Marker erstellen */
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

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.editRoute:

                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                alert.setTitle("Routenname bearbeiten?");

                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View alertView = inflater.inflate(R.layout.fragment_record_list_edit_route, null, true);
                TextView edit_record_name = alertView.findViewById(R.id.edit_record_name);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    alert.setView(alertView);
                    edit_record_name.setText(record.getName());

                } else {
                    // TODO Implementation für Nutzer mit API <= 16
                }

                alert.setPositiveButton("Speichern", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String newName = edit_record_name.getText().toString();

                        /* Aktualisieren der Route */
                        Route newRecord = record;
                        newRecord.setName(newName);
                        dao.update(record.getId(), newRecord);

                        recordName.setText(newName);
                        if (MainActivity.getHints()) {
                            Toast.makeText(MainActivity.getInstance().getApplicationContext(), "Name erfolgreich bearbeitet!", Toast.LENGTH_LONG).show();
                        }

                    }
                });

                alert.setNegativeButton("Verwerfen", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                alert.show();

                break;
        }
    }

    /* Das Datum wird von Millisekunden als Formatiertes Datum zurückgegeben */
    private static String getDate(long millis, String dateFormat) {
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);
        return formatter.format(calendar.getTime());
    }
}

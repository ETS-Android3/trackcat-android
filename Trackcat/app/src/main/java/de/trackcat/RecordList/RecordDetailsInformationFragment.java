package de.trackcat.RecordList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.config.Configuration;
import org.osmdroid.config.IConfigurationProvider;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import de.trackcat.APIClient;
import de.trackcat.APIConnector;
import de.trackcat.BuildConfig;
import de.trackcat.Database.DAO.RecordTempDAO;
import de.trackcat.Database.DAO.RouteDAO;
import de.trackcat.Database.DAO.UserDAO;
import de.trackcat.Database.Models.Location;
import de.trackcat.Database.Models.Route;
import de.trackcat.Database.Models.User;
import de.trackcat.GlobalFunctions;
import de.trackcat.MainActivity;
import de.trackcat.R;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static org.osmdroid.tileprovider.util.StorageUtils.getStorage;

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
    RecordTempDAO tempDAO;
    TextView recordName;
    boolean temp;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        int id = getArguments().getInt("id");
        temp = getArguments().getBoolean("temp");
        String locationsAsString = getArguments().getString("locations");
        view = inflater.inflate(R.layout.fragment_record_details_information, container, false);

        /* Auslesen der Werte aus der Datenbank */
        locations = Arrays.asList(new Gson().fromJson(locationsAsString, Location[].class));
        dao = new RouteDAO(MainActivity.getInstance());
        tempDAO = new RecordTempDAO(MainActivity.getInstance());
        if (temp) {
            record = tempDAO.read(id);
        } else {
            record = dao.read(id);
        }

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
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            startMarker.setIcon(MainActivity.getInstance().getResources().getDrawable(R.drawable.ic_map_record_start));
        }

        gPt = new GeoPoint(locations.get(locations.size() - 1).getLatitude(), locations.get(locations.size() - 1).getLongitude());
        Marker stopMarker = new Marker(mMapView);
        stopMarker.setPosition(gPt);
        stopMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            stopMarker.setIcon(MainActivity.getInstance().getResources().getDrawable(R.drawable.ic_map_record_end));
        }

        Polyline mPath = new Polyline(mMapView);

        mMapView.getOverlays().add(mPath);
        mMapView.getOverlays().add(startMarker);
        mMapView.getOverlays().add(stopMarker);

        mPath.setPoints(GPSData);
        mPath.setColor(Color.RED);
        mPath.setWidth(4);

        /* load map by big routes */
        IConfigurationProvider provider = Configuration.getInstance();
        provider.setUserAgentValue(BuildConfig.APPLICATION_ID);
        provider.setOsmdroidBasePath(getStorage());
        provider.setOsmdroidTileCache(getStorage());

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
                        newRecord.setTimeStamp(GlobalFunctions.getTimeStamp());
                        if (temp) {
                            tempDAO.update(record.getId(), newRecord);
                            if (MainActivity.getHints()) {
                                Toast.makeText(getContext(), getResources().getString(R.string.teditRecordName), Toast.LENGTH_LONG).show();
                            }
                        } else {
                            dao.update(record.getId(), newRecord);


                            /* get current user */
                            UserDAO userDAO = new UserDAO(MainActivity.getInstance());
                            User currentUser = userDAO.read(MainActivity.getActiveUser());

                            /* read profile values from global db */
                            HashMap<String, String> map = new HashMap<>();
                            map.put("recordId", "" + record.getId());
                            map.put("recordName", "" + newName);
                            map.put("timestamp", "" + newRecord.getTimeStamp());

                            Retrofit retrofit = APIConnector.getRetrofit();
                            APIClient apiInterface = retrofit.create(APIClient.class);

                            /* start a call */
                            String base = currentUser.getMail() + ":" + currentUser.getPassword();
                            String authString = "Basic " + Base64.encodeToString(base.getBytes(), Base64.NO_WRAP);
                            Call<ResponseBody> call = apiInterface.updateRecordName(authString, map);

                            call.enqueue(new Callback<ResponseBody>() {

                                @Override
                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                    try {

                                        if (response.code() == 401) {
                                             MainActivity.getInstance().showNotAuthorizedModal(4);
                                        } else {
                                            /* get jsonString from API */
                                            String jsonString = response.body().string();

                                            /* parse json */
                                            JSONObject mainObject = new JSONObject(jsonString);
                                            if (mainObject.getString("success").equals("0")) {

                                                if (MainActivity.getHints()) {
                                                    Toast.makeText(getContext(), getResources().getString(R.string.teditRecordName), Toast.LENGTH_LONG).show();
                                                }
                                            } else if (mainObject.getString("success").equals("1")) {

                                                if (MainActivity.getHints()) {
                                                    Toast.makeText(getContext(), getResources().getString(R.string.teditRecordNameUnknownError), Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        }
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onFailure(Call<ResponseBody> call, Throwable t) {
                                    call.cancel();
                                }
                            });
                        }
                        recordName.setText(newName);
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

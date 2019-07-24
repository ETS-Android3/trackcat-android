package de.trackcat.FriendsSystem.FriendShowOptions;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.config.Configuration;
import org.osmdroid.config.IConfigurationProvider;
import org.osmdroid.events.DelayedMapListener;
import org.osmdroid.events.MapListener;
import org.osmdroid.events.ScrollEvent;
import org.osmdroid.events.ZoomEvent;
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
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

import de.trackcat.APIClient;
import de.trackcat.APIConnector;
import de.trackcat.BuildConfig;
import de.trackcat.Database.DAO.UserDAO;
import de.trackcat.Database.Models.Location;
import de.trackcat.Database.Models.User;
import de.trackcat.MainActivity;
import de.trackcat.R;
import de.trackcat.Statistics.SpeedAverager;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static android.view.View.*;
import static org.osmdroid.tileprovider.util.StorageUtils.getStorage;

public class FriendLiveFragment extends Fragment implements OnClickListener {

    protected static final int DEFAULT_INACTIVITY_DELAY_IN_MILLISECS = 200;
    private MapView mMapView = null;
    ArrayList<GeoPoint> GPSData = new ArrayList<>();
    GeoPoint gPt;
    ArrayList<Location> locations = new ArrayList<>();
    Marker stopMarker;
    MapController mMapController;
    View view;
    private UserDAO userDAO;
    private static User currentUser;
    float lastSpeed;
    double lastAltimeter;
    TextView userTitle, averageSpeed, distance, altimeter, time;
    FloatingActionButton type, goToMarker, showCompleteRecord;
    String titleStart = "Übertragung von ";
    int runCounter, index, recordId;
    boolean userScroll, showAll;
    private static Handler handler;

    @SuppressLint("RestrictedApi")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        /* Get views */
        view = inflater.inflate(R.layout.fragment_friend_live_view, container, false);
        mMapView = view.findViewById(R.id.mapview);
        userTitle = view.findViewById(R.id.sharing_user_title);
        averageSpeed = view.findViewById(R.id.average_speed_TextView);
        distance = view.findViewById(R.id.distance_TextView);
        altimeter = view.findViewById(R.id.altimeter_TextView);
        time = view.findViewById(R.id.time_TextView);
        type = view.findViewById(R.id.fabButton);
        goToMarker = view.findViewById(R.id.goToMarkerBtn);
        showCompleteRecord = view.findViewById(R.id.showCompleteRecordBtn);

        /* Set on Click listener */
        goToMarker.setOnClickListener(this);
        showCompleteRecord.setOnClickListener(this);

        /* Create user DAO and get current user */
        userDAO = new UserDAO(MainActivity.getInstance());
        currentUser = userDAO.read(MainActivity.getActiveUser());

        /* Get friend id from bundle*/
        int friendId = getArguments().getInt("friendId");
        index = 0;

        /* Set controll variables */
        runCounter = 1;
        userScroll = false;
        showAll = true;
        recordId = 0;

        /* DateFormat setzen */
        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
        TimeZone tz = TimeZone.getTimeZone("UTC");
        df.setTimeZone(tz);

        /* Check zoom and scroll */
        mMapView.addMapListener(new DelayedMapListener(new MapListener() {
            @Override
            public boolean onScroll(ScrollEvent event) {

                /* App Scroll */
                if (event.getX() == 0 && event.getY() == 0) {
                    userScroll = false;

                    /* User Scroll */
                } else {
                    userScroll = true;
                    goToMarker.show();
                }
                return true;
            }

            @Override
            public boolean onZoom(ZoomEvent event) {
                return true;
            }
        }, DEFAULT_INACTIVITY_DELAY_IN_MILLISECS));

        /* Create handler and runnable */
        handler = new Handler();
        int delay = 4000; //milliseconds
        Runnable runnable = new Runnable() {
            public void run() {

                /* Create hashmap */
                HashMap<String, String> map = new HashMap<>();
                map.put("friendId", "" + friendId);
                map.put("index", "" + index);

                /* Start a call */
                Retrofit retrofit = APIConnector.getRetrofit();
                APIClient apiInterface = retrofit.create(APIClient.class);
                User currentUser = userDAO.read(MainActivity.getActiveUser());
                String base = currentUser.getMail() + ":" + currentUser.getPassword();
                String authString = "Basic " + Base64.encodeToString(base.getBytes(), Base64.NO_WRAP);
                Call<ResponseBody> call = apiInterface.getLiveRecord(authString, map);
                call.enqueue(new Callback<ResponseBody>() {

                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                        try {
                            if (response.code() == 401) {
                                MainActivity.getInstance().showNotAuthorizedModal(9);
                            } else {

                                /* Get jsonString from API */
                                String jsonString = response.body().string();

                                /* Parse json */
                                JSONObject mainObject = new JSONObject(jsonString);

                                /* check if liverecord exists */
                                if (mainObject.has("id")) {

                                    /* Start new live record */
                                    if (recordId != mainObject.getInt("id")) {
                                        locations.clear();
                                        GPSData.clear();
                                        index = 0;
                                        runCounter = 1;

                                        if (MainActivity.getHints()) {
                                            Toast.makeText(MainActivity.getInstance().getApplicationContext(), MainActivity.getInstance().getResources().getString(R.string.friendNewLive), Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    /* Get location data */
                                    JSONArray locationArray = mainObject.getJSONArray("locations");
                                    for (int i = 0; i < locationArray.length(); i++) {
                                        Location location = new Location();
                                        location.setAltitude(((JSONObject) locationArray.get(i)).getDouble("altitude"));
                                        location.setLatitude(((JSONObject) locationArray.get(i)).getDouble("latitude"));
                                        location.setLongitude(((JSONObject) locationArray.get(i)).getDouble("longitude"));
                                        location.setSpeed(((JSONObject) locationArray.get(i)).getLong("speed"));
                                        location.setTime(((JSONObject) locationArray.get(i)).getLong("time"));
                                        index = ((JSONObject) locationArray.get(i)).getInt("id");
                                        lastSpeed = location.getSpeed();
                                        lastAltimeter = location.getAltitude();
                                        locations.add(location);
                                        GeoPoint gPt = new GeoPoint(location.getLatitude(), location.getLongitude());
                                        GPSData.add(gPt);
                                    }
                                    if (locations.size() > 0) {

                                        /* Draw route */
                                        if (runCounter == 1) {
                                            createMap();

                                            /* Set recordId */
                                            recordId = mainObject.getInt("id");
                                        } else {
                                            drawRoute();
                                        }
                                    }
                                    /* Set values */
                                    userTitle.setText(titleStart + mainObject.getString("firstName") + " " + mainObject.getString("lastName"));

                                    String speedStr = (Math.round(lastSpeed * 60 * 60) / 100) / 10.0 + " km/h";
                                    averageSpeed.setText(speedStr);

                                    distance.setText(Math.round(mainObject.getLong("distance")) / 1000.0 + " km");

                                    String altimeterStr = Math.round(lastAltimeter) + " m";
                                    altimeter.setText(altimeterStr);

                                    type.setImageResource(SpeedAverager.getTypeIcon(mainObject.getInt("type"), false));

                                    String timeStr = df.format(new Date(mainObject.getLong("time") * 1000));
                                    time.setText(timeStr);

                                    /* Show complete record */
                                    if (showAll) {
                                        view.setTag(view.getVisibility());
                                        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                                            @Override
                                            public void onGlobalLayout() {

                                                if (showAll) {
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
                                            }
                                        });
                                    }
                                    runCounter++;

                                } else {

                                    /* stop runnable */
                                    handler.removeCallbacksAndMessages(null);
                                    if (MainActivity.getHints()) {
                                        Toast.makeText(MainActivity.getInstance().getApplicationContext(), MainActivity.getInstance().getResources().getString(R.string.friendNoLive), Toast.LENGTH_SHORT).show();
                                    }
                                    MainActivity.getInstance().loadFriendSystem(2);
                                }
                            }

                        } catch (
                                JSONException e1) {
                            e1.printStackTrace();
                        } catch (
                                IOException e1) {
                            e1.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        call.cancel();
                    }
                });

                /* restart runnable */
                handler.postDelayed(this, delay);
            }
        };

        /* start runnable */
        handler.postDelayed(runnable, delay);

        return view;
    }

    private void createMap() {

        /* Set map */
        mMapView.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE);
        mMapView.setBuiltInZoomControls(false);
        mMapView.setMultiTouchControls(true);
        mMapController = (MapController) mMapView.getController();
        mMapController.setZoom(19);

        /* create Polyline and marker */
        gPt = new GeoPoint(locations.get(0).getLatitude(), locations.get(0).getLongitude());
        Marker startMarker = new Marker(mMapView);
        startMarker.setPosition(gPt);
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        startMarker.setIcon(MainActivity.getInstance().getResources().getDrawable(R.drawable.ic_map_record_start));

        stopMarker = new Marker(mMapView);
        gPt = new GeoPoint(locations.get(locations.size() - 1).getLatitude(), locations.get(locations.size() - 1).getLongitude());
        stopMarker.setPosition(gPt);
        stopMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        stopMarker.setIcon(MainActivity.getInstance().getResources().getDrawable(R.drawable.ic_logo_marker));

        Polyline mPath = new Polyline(mMapView);

        mMapView.getOverlays().add(mPath);
        mMapView.getOverlays().add(startMarker);
        mMapView.getOverlays().add(stopMarker);

        mPath.setPoints(GPSData);
        mPath.setColor(Color.RED);
        mPath.setWidth(4);

        /* Load map by big routes */
        IConfigurationProvider provider = Configuration.getInstance();
        provider.setUserAgentValue(BuildConfig.APPLICATION_ID);
        provider.setOsmdroidBasePath(getStorage());
        provider.setOsmdroidTileCache(getStorage());

        Configuration.getInstance().setUserAgentValue(MainActivity.getInstance().getPackageName());
    }

    private void drawRoute() {

        /* Update end marker */
        gPt = new GeoPoint(locations.get(locations.size() - 1).getLatitude(), locations.get(locations.size() - 1).getLongitude());
        stopMarker.setPosition(gPt);

        /* check if userScroll true  and scroll to marker*/
        if (!userScroll && !showAll) {
            mMapController.setCenter(gPt);
        }

        /* Create Polyline */
        Polyline mPath = new Polyline(mMapView);
        mMapView.getOverlays().add(mPath);
        mMapView.getOverlays().add(stopMarker);

        mPath.setPoints(GPSData);
        mPath.setColor(Color.RED);
        mPath.setWidth(4);

        /* Load map by big routes */
        IConfigurationProvider provider = Configuration.getInstance();
        provider.setUserAgentValue(BuildConfig.APPLICATION_ID);
        provider.setOsmdroidBasePath(getStorage());
        provider.setOsmdroidTileCache(getStorage());
        mMapView.invalidate();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.goToMarkerBtn:
                /* Go to marker */
                goToMarker();
                Toast.makeText(MainActivity.getInstance().getApplicationContext(), MainActivity.getInstance().getResources().getString(R.string.friendLiveViewZoomToUser), Toast.LENGTH_SHORT).show();
                goToMarker.hide();
                break;
            case R.id.showCompleteRecordBtn:
                /* Show zoomed view */
                if (showAll) {
                    goToMarker();
                    showCompleteRecord.setImageResource(R.drawable.ic_switch_one);
                    Toast.makeText(MainActivity.getInstance().getApplicationContext(), MainActivity.getInstance().getResources().getString(R.string.friendLiveViewZoom), Toast.LENGTH_SHORT).show();

                    /* show fullTrack view */
                } else {
                    showAll = true;
                    showCompleteRecord.setImageResource(R.drawable.ic_switch_all);
                    Toast.makeText(MainActivity.getInstance().getApplicationContext(), MainActivity.getInstance().getResources().getString(R.string.friendLiveViewFullTrack), Toast.LENGTH_SHORT).show();
                    goToMarker.hide();
                }
                break;
        }
    }

    /* Function to zoom to marker */
    private void goToMarker() {
        mMapController.setCenter(gPt);
        userScroll = false;
        showAll = false;
        mMapView.invalidate();
    }

    public static void resetHandler() {
        handler.removeCallbacksAndMessages(null);
    }
}


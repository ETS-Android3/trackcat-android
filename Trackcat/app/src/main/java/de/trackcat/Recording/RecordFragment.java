package de.trackcat.Recording;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ScaleDrawable;
import android.hardware.GeomagneticField;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.GpsStatus;
import android.location.Location;
import android.os.*;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Base64;
import android.util.Log;
import android.view.*;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.karan.churi.PermissionManager.PermissionManager;

import de.trackcat.APIClient;
import de.trackcat.APIConnector;
import de.trackcat.CustomElements.CustomLocation;
import de.trackcat.CustomElements.RecordModelForServer;
import de.trackcat.Database.DAO.LocationTempDAO;
import de.trackcat.Database.DAO.RecordTempDAO;
import de.trackcat.Database.DAO.RouteDAO;
import de.trackcat.Database.DAO.UserDAO;
import de.trackcat.Database.Models.Route;
import de.trackcat.Database.Models.User;
import de.trackcat.FriendsSystem.FriendShowOptions.FriendLiveFragment;
import de.trackcat.FriendsSystem.FriendShowOptions.FriendProfileFragment;
import de.trackcat.FriendsSystem.Tabs.FriendQuestionsFragment;
import de.trackcat.FriendsSystem.Tabs.FriendSendQuestionsFragment;
import de.trackcat.FriendsSystem.Tabs.FriendsFragment;
import de.trackcat.GlobalFunctions;
import de.trackcat.MainActivity;
import de.trackcat.NotificationActionReciever;
import de.trackcat.R;
import de.trackcat.Recording.Recording_UI.PageViewer;
import de.trackcat.StartActivity;
import de.trackcat.Statistics.SpeedAverager;
import de.trackcat.Statistics.mCounter;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.TimerTask;


/*
 * Fragment for Track recording. includes GPS Locator and Statistics
 * Results displayed in this Fragment view
 * */

public class RecordFragment extends Fragment implements SensorEventListener {

    /*
     * Statistics
     * */
    private Locator locatorGPS;
    private mCounter kmCounter;
    private Timer timer;
    private Timer rideTimer;
    private SpeedAverager kmhAverager;

    /*
     * Maps Attributes
     * */
    private MapView mMapView;
    private MapController mMapController;
    private Marker startMarker;
    private Polyline mPath;

    /*
     * All recorded GeoPoints
     * */
    private ArrayList<GeoPoint> GPSData = new ArrayList<>();

    /*
     * Message Handler
     * */
    static Handler handler;


    /*
     * Statistics TextViews
     * */
    private TextView kmh_TextView;
    private TextView time_TextView;
    private TextView average_speed_TextView;

    private static boolean isTracking = false;

    public static boolean isTracking() {
        return isTracking;
    }

    private View view;

    /*
     * Notification stuff
     * */
    private NotificationManagerCompat notificationManager;
    private static final String CHANNEL_ID = "Trackcat_Notification_Channel_ID";

    /*
     * Play/Pause Button + ProgressBar on hold
     * */
    private ImageView playPause;
    private Vibrator vibe;
    private CountDownTimer countdownTimer;
    private ProgressBar progressBar;
    private int progressTime = 1000;
    private long startTime;

    private String notificationContent = "";

    /*
     + declaring necessary variables for mapOrientation
     + and assign initial values
     */

    private boolean northUp = false;
    private int deviceOrientation = 0;
    private float lat = 0f;
    private float lon = 0f;
    private float alt = 0f;
    private long timeOfFix = 0;
    private float gpsBearing = 0f;

    private CompassOverlay mCompassOverlay;
    private SensorManager sensorManager;
    private Sensor magnetometer;

    /*
     * Model of Route
     * */
    private Route model;
    private int newRecordId;
    private int liveRecordId;
    private boolean liveRecording;

    /*
     * Daos
     * */
    private RecordTempDAO recordTempDAO;
    private LocationTempDAO locationTempDAO;
    private RouteDAO recordDAO;
    private UserDAO userDAO;

    /* live record values */
    boolean recordingRuns;

    private java.util.Timer sendLiveTimer;

    @Override
    public void onPause() {
        super.onPause();

        if (!isTracking) {
            stopGPS();
        }

        /*
         + stop sensor listening when app will be paused
         */
        sensorManager.unregisterListener(this);
        /*
         + stop compass overlay when app will be paused
         */
        mCompassOverlay.disableCompass();
        mCompassOverlay.getOrientationProvider().stopOrientationProvider();
    }

    @Override
    public void onResume() {
        super.onResume();
        /*
         + start sensor listening when app will be resumed
         */
        sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_UI);
        /*
         + start compass overlay when app will be resumed
         */
        mCompassOverlay.enableCompass();
        mCompassOverlay.getOrientationProvider().startOrientationProvider(mCompassOverlay);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        /*
         + stop sensor listening when app will be closed
         */
        sensorManager.unregisterListener(this);
        /*
         + stop compass overlay when app will be closed
         */
        mCompassOverlay.onDetach(mMapView);
    }

    private PermissionManager permissionManager = new PermissionManager() {
    };

    @TargetApi(Build.VERSION_CODES.N)
    @SuppressLint({"HandlerLeak", "ClickableViewAccessibility"})
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        /* Fragt nach noch nicht erteilten Permissions */
        permissionManager.checkAndRequestPermissions(MainActivity.getInstance());

        /* set DAOS */
        recordTempDAO = new RecordTempDAO(MainActivity.getInstance());
        locationTempDAO = new LocationTempDAO(MainActivity.getInstance());
        userDAO = new UserDAO(MainActivity.getInstance());

        /* ----------------------------------------------------------------------------------handler
         * recieves messages from another thread
         *
         * handler recieves data from Timer Thread
         */
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 0) {
                    /*
                     * set Time in TextView
                     * */
                    String toSetTime = msg.obj + "";

                    String toSetDistance = Math.round(kmCounter.getAmount()) / 1000.0 + " km";

                    try {
                        time_TextView = view.findViewById(R.id.time_TextView);
                        time_TextView.setText(toSetTime);

                        TextView distance_TextView = view.findViewById(R.id.distance_TextView);
                        distance_TextView.setText(toSetDistance);


                    } catch (NullPointerException e) {
                        Log.v(getResources().getString(R.string.app_name), e.toString());
                    }

                    try {
                        notificationContent = toSetTime + "   " + toSetDistance;

                        issueNotification(notificationContent);
                    } catch (Exception e) {
                        Log.v(getResources().getString(R.string.app_name), e.toString());
                    }

                    /*
                     * recalculate average Speed
                     * */
                    try {
                        average_speed_TextView = view.findViewById(R.id.average_speed_TextView);
                        String toSet = Math.round((kmhAverager.getAvgSpeed() * 60 * 60) / 100) / 10.0 + " km/h";
                        average_speed_TextView.setText(toSet);
                    } catch (NullPointerException e) {
                        Log.v(getResources().getString(R.string.app_name), e.toString());

                    }
                } else if (msg.what == 2) {

                    /*
                     * recieved Lcoation
                     * */
                    updateLocation((Location) msg.obj);
                }
            }
        };

        if (Build.VERSION.SDK_INT > 21) {
            view = inflater.inflate(R.layout.fragment_record_main, container, false);
        } else {
            view = inflater.inflate(R.layout.fragment_record_main_api_less_21, container, false);
        }

        /*
         * set Map Attributes
         */
        mMapView = view.findViewById(R.id.mapview);
        mMapView.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE);


        mMapView.setMultiTouchControls(true);
        mMapController = (MapController) mMapView.getController();
        mMapController.setZoom(18);

        mMapView.setBuiltInZoomControls(false);

        /*
         * add Marker and Polyline
         * */
        startMarker = new Marker(mMapView);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            startMarker.setIcon(MainActivity.getInstance().getResources().getDrawable(R.drawable.ic_logo_marker));
        }

        mPath = new Polyline(mMapView);
        mMapView.getOverlays().add(mPath);
        mMapView.getOverlays().add(startMarker);


        /*
         + add compass element
         + !!! needs a device with the compass-functionality to work properly !!!
         */

        /*
         + Lock the device in current screen orientation
         */
        if (!"Android-x86".equalsIgnoreCase(Build.BRAND)) {
            int orientation = Objects.requireNonNull(getActivity()).getRequestedOrientation();
            int rotation = ((WindowManager) Objects.requireNonNull(
                    Objects.requireNonNull(getActivity()).getSystemService(
                            Context.WINDOW_SERVICE))).getDefaultDisplay().getRotation();
            switch (rotation) {
                case Surface.ROTATION_0:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    deviceOrientation = 0;
                    break;
                case Surface.ROTATION_90:
                    deviceOrientation = 90;
                    orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                    break;
                case Surface.ROTATION_180:
                    deviceOrientation = 180;
                    orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
                    break;
                case Surface.ROTATION_270:
                    deviceOrientation = 270;
                    orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                    break;
            }

            getActivity().setRequestedOrientation(orientation);
        }

        /*
         + create new compass element
         */
        mCompassOverlay = new CompassOverlay(Objects.requireNonNull(getContext()),
                new InternalCompassOrientationProvider(Objects.requireNonNull(getActivity())),
                mMapView);

        /*
         + add compass overlay to the mapView
         */
        mMapView.getOverlays().add(mCompassOverlay);

        /*
         + Button for aligning the map to north
         */
        view.findViewById(R.id.compBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!northUp) {
                    /*
                     + resets map orientation to north and sets repetition
                     */
                    northUp = true;
                    mMapView.setVerticalMapRepetitionEnabled(true);
                    mMapView.setMapOrientation((float) 0.0);
                } else {
                    /*
                     + unset map repetition
                     */
                    northUp = false;
                    mMapView.setVerticalMapRepetitionEnabled(false);
                }
            }
        });

        /*
         + !!! needs a device with a magnetometer sensor to work properly !!!
         + make this fragment listening to changes of magnetic sensor
         */
        sensorManager = (SensorManager) MainActivity.getInstance().getSystemService(
                Context.SENSOR_SERVICE);
        assert sensorManager != null;
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_UI);

        /*
         * Initialize for Notification
         */
        createNotificationChannel();
        notificationManager = NotificationManagerCompat.from(MainActivity.getInstance());

        /*
         * Play Button
         * */
        playPause = view.findViewById(R.id.play_imageView);

        // for haptic feedback
        vibe = (Vibrator) MainActivity.getInstance().getSystemService(Context.VIBRATOR_SERVICE);
        progressBar = view.findViewById(R.id.progressBar);
        startTime = 0;

        /*
         * onTouchListener makes hold down possible for ending Tracking
         * */
        playPause.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                /*
                 * when Button is pushed
                 * */
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    vibe.vibrate(10);
                    /* change state of Tracking */
                    if (isTracking) {
                        stopTracking();
                    } else {
                        Log.d("TEEEST", "Starten");

                        /* start recording */
                        if (!recordingRuns && MainActivity.getConnection()) {


                            /* check if live sharing or not */
                            AlertDialog.Builder alertdialogbuilder = new AlertDialog.Builder(getContext());
                            alertdialogbuilder.setTitle(getContext().getResources().getString(R.string.recordsOptionsTitle));

                            alertdialogbuilder.setItems(getContext().getResources().getStringArray(R.array.recordsOptions), new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int id) {

                                    if (id == 0) {
                                        Toast.makeText(MainActivity.getInstance().getApplicationContext(), "Live-Übertragung gestartet.", Toast.LENGTH_LONG).show();

                                        /* start a call */
                                        Retrofit retrofit = APIConnector.getRetrofit();
                                        APIClient apiInterface = retrofit.create(APIClient.class);
                                        User currentUser = userDAO.read(MainActivity.getActiveUser());
                                        String base = currentUser.getMail() + ":" + currentUser.getPassword();
                                        String authString = "Basic " + Base64.encodeToString(base.getBytes(), Base64.NO_WRAP);

                                        Call<ResponseBody> call = apiInterface.requestLiveRecord(authString);
                                        call.enqueue(new Callback<ResponseBody>() {

                                            @Override
                                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                                                try {
                                                    /* get jsonString from API */
                                                    String jsonString = response.body().string();

                                                    /* parse json */
                                                    JSONObject mainObject = new JSONObject(jsonString);

                                                    /* friendship question okay */
                                                    liveRecordId = mainObject.getInt("liveRecordId");

                                                    /* Set boolean on true */
                                                    liveRecording = true;
                                                    recordingRuns = true;

                                                    startTracking();


                                                } catch (JSONException e1) {
                                                    e1.printStackTrace();
                                                } catch (IOException e1) {
                                                    e1.printStackTrace();
                                                }
                                            }

                                            @Override
                                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                                call.cancel();
                                            }
                                        });


                                    }
                                    if (id == 1) {
                                        Toast.makeText(MainActivity.getInstance().getApplicationContext(), "Private Aufzeichnung gestartet.", Toast.LENGTH_LONG).show();
                                        liveRecording = false;
                                        recordingRuns = true;
                                        startTracking();

                                    }
                                }
                            });

                            AlertDialog dialog = alertdialogbuilder.create();
                            dialog.show();
                            dialog.setCanceledOnTouchOutside(false);
                        }
                        //    startTracking();

                    }

                    /* get location data */
                    List<de.trackcat.Database.Models.Location> locations = new ArrayList<>();
                    if (recordingRuns) {
                        locations = locationTempDAO.readAll(newRecordId);



                        /* if there is any data collectet Tracked Route is saveable */
                        if (timer.getTime() > 0 && locations != null && locations.size() > 2) {
                            /* store starttime for holdDown */
                            startTime = event.getEventTime();
                            /* timer for Progressbar */
                            startTimer();

                            if (MainActivity.getHints()) {
                                Toast toast = Toast.makeText(MainActivity.getInstance().getApplicationContext(), "Halten für speichern", Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.TOP, 0, 0);
                                toast.show();
                            }
                        }
                    }
                    /*
                     * when Button is released
                     * */
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getEventTime() - startTime < progressTime) {
                        /*
                         * stops hold down Progress if holding down was too short
                         * */
                        killTimer();
                        progressBar.setProgress(0);
                    } else if (startTime != 0) {

                        endTracking();

                    }
                }
                return true;
            }
        });

        /*
         * recreate status of Play/Pause Button
         * */
        if (isTracking) {
            playPause.setImageResource(R.drawable.record_pausebtn_white);
        } else {
            playPause.setImageResource(R.drawable.record_playbtn_white);
            if (locatorGPS != null) {
                locatorGPS.startTracking();
            }
        }


        /*
         * set ViewPager(km/h<->Time Swiper)
         * */
        FragmentTransaction fragTransaction = getChildFragmentManager().beginTransaction();
        fragTransaction.replace(R.id.pageViewerContainer, new PageViewer(), "PageViewer");
        fragTransaction.commit();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            stopGPS();
            MainActivity.getInstance().startForegroundService(new Intent(MainActivity.getInstance(), Locator.class));
        } else {
            if (locatorGPS == null) {
                locatorGPS = new Locator();
            }
        }

        //startTracking();
        if (timer != null) {
            timer.sendTime();
        }

        MainActivity.getInstance().setRecordFragment(this);

        return view;
    }

    /*
     * end Tracking and switch to statistics for dismiss or save
     * */
    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void endTracking() {
        stopTracking();
        notificationManager.cancel(MainActivity.getInstance().getNOTIFICATION_ID());

        model.setTime(timer.getTime());
        model.setRideTime(rideTimer.getTime());
        model.setDistance(kmCounter.getAmount());
        int type = SpeedAverager.getRouteType(kmhAverager.getAvgSpeed());
        model.setType(type);
        model.setUserID(MainActivity.getActiveUser());
        model.setDate(System.currentTimeMillis());
        model.setTemp(true);
        Date currentTime = Calendar.getInstance().getTime();
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy_HH:mm:ss");
        String dateStr = simpleDateFormat.format(currentTime);
        String defaultName = "Rec_" + dateStr;

        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        alert.setTitle("Aufnahme speichern?");

        LayoutInflater inflater = (LayoutInflater) Objects.requireNonNull(getContext()).getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        MapView mapViewZoom = null;
        ImageView typeIcon;

        @SuppressLint("InflateParams")
        View alertView = inflater != null ? inflater.inflate(R.layout.fragment_record_list_one_item, null, true) : null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            alert.setView(alertView);

            /* Route auf Karte zeichnen */
            assert alertView != null;
            drawRoute(alertView);

            mapViewZoom = alertView.findViewById(R.id.mapview);

            /* Typ festlegen */
            typeIcon = alertView.findViewById(R.id.fabButton);
            typeIcon.setImageResource(SpeedAverager.getTypeIcon(type, false));

            /* Placeholder festlegen */
            TextView recordName = alertView.findViewById(R.id.record_name);
            recordName.setHint(defaultName);

            /* Setzt die aufgezeichneten Kilometer */
            TextView distance_TextView = alertView.findViewById(R.id.distance_TextView);
            double distance = Math.round(kmCounter.getAmount());
            if (distance >= 1000) {
                String d = "" + distance / 1000L;
                distance_TextView.setText(d.replace('.', ',') + " km");
            } else {
                distance_TextView.setText((int) distance + " m");
            }

            /* Setzt die Zeit */
            TextView total_time_TextView = alertView.findViewById(R.id.total_time_TextView);
            Timer timerForCalc = new Timer();
            total_time_TextView.setText(timerForCalc.secToString(timer.getTime()));
        }


        alert.setPositiveButton("Speichern", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                TextView recordName = alertView != null ? alertView.findViewById(R.id.record_name) : null;
                if (recordName != null && recordName.getText().toString().equals("")) {
                    model.setName(defaultName);
                } else {
                    model.setName(recordName != null ? recordName.getText().toString() : null);
                }

                model.setTimeStamp(GlobalFunctions.getTimeStamp());
             /*   RouteDAO dao = new RouteDAO(MainActivity.getInstance());
                dao.create(model);*/

                recordTempDAO.update(newRecordId, model);

                /* send route full to server */

                /* get current user */
                UserDAO userDAO = new UserDAO(MainActivity.getInstance());
                User currentUser = userDAO.read(MainActivity.getActiveUser());

                Retrofit retrofit = APIConnector.getRetrofit();
                APIClient apiInterface = retrofit.create(APIClient.class);
                String base = currentUser.getMail() + ":" + currentUser.getPassword();

                RecordModelForServer m = new RecordModelForServer();
                m.setId(newRecordId);
                m.setUserID(MainActivity.getActiveUser());
                m.setName(model.getName());
                m.setType(model.getType());
                m.setTime(model.getTime());
                m.setDate(model.getDate());
                m.setRideTime(model.getRideTime());
                m.setDistance(model.getDistance());
                m.setTimeStamp(model.getTimeStamp());
                m.setLocations(locationTempDAO.readAll(newRecordId));

                /* start a call */
                String authString = "Basic " + Base64.encodeToString(base.getBytes(), Base64.NO_WRAP);
                Call<ResponseBody> call = apiInterface.uploadFullTrack(authString, m);

                call.enqueue(new Callback<ResponseBody>() {

                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                        /* get jsonString from API */
                        String jsonString = null;

                        try {
                            jsonString = response.body().string();

                            /* parse json */
                            JSONObject mainObject = new JSONObject(jsonString);

                            if (mainObject.getString("success").equals("0")) {

                                MainActivity.getInstance().endTracking();
                                Toast.makeText(getActivity(), getResources().getString(R.string.saveRouteOnServer),
                                        Toast.LENGTH_LONG).show();

                                /* save in DB*/
                                recordDAO = new RouteDAO(MainActivity.getInstance());

                                if (mainObject.getJSONObject("record") != null) {
                                    JSONObject recordJSON = mainObject.getJSONObject("record");

                                    Route record = new Route();
                                    record.setId(recordJSON.getInt("id"));
                                    record.setName(recordJSON.getString("name"));
                                    record.setTime(recordJSON.getLong("time"));
                                    record.setDate(recordJSON.getLong("date"));
                                    record.setType(recordJSON.getInt("type"));
                                    record.setRideTime(recordJSON.getInt("ridetime"));
                                    record.setDistance(recordJSON.getDouble("distance"));
                                    record.setTimeStamp(recordJSON.getLong("timestamp"));
                                    record.setTemp(false);
                                    record.setLocations(recordJSON.getString("locations"));
                                    recordDAO.create(record);

                                    /* delete old record */
                                    int tempRecordId = mainObject.getInt("oldId");
                                    record.setId(tempRecordId);

                                    /*remove from temp*/
                                    recordTempDAO.delete(record);
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
                        MainActivity.getInstance().endTracking();
                        Toast.makeText(getActivity(), getResources().getString(R.string.saveRouteOffline),
                                Toast.LENGTH_LONG).show();

                    }
                });

            }
        });

        alert.setNegativeButton("Verwerfen", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                /*remove from temp*/
                recordTempDAO.delete(model);

                /* Send request to server if its live recording */
                if(liveRecording) {
                    /* get current user */
                    UserDAO userDAO = new UserDAO(MainActivity.getInstance());
                    User currentUser = userDAO.read(MainActivity.getActiveUser());

                    /* Start a call */
                    Retrofit retrofit = APIConnector.getRetrofit();
                    APIClient apiInterface = retrofit.create(APIClient.class);
                    String base = currentUser.getMail() + ":" + currentUser.getPassword();
                    String authString = "Basic " + Base64.encodeToString(base.getBytes(), Base64.NO_WRAP);
                    Call<ResponseBody> call = apiInterface.abortLiveRecord(authString);

                    call.enqueue(new Callback<ResponseBody>() {

                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                            /* get jsonString from API */
                            String jsonString = null;

                            try {
                                jsonString = response.body().string();

                                /* parse json */
                                JSONObject mainObject = new JSONObject(jsonString);

                                if (mainObject.getString("success").equals("0")) {
                                    Toast.makeText(MainActivity.getInstance(), getResources().getString(R.string.abortLiveRecord),
                                            Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(MainActivity.getInstance(), getResources().getString(R.string.abortLiveRecordError),
                                            Toast.LENGTH_LONG).show();
                                }

                                /* end tracking and delete temp record */
                                MainActivity.getInstance().endTracking();
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            call.cancel();

                            Toast.makeText(MainActivity.getInstance(), getResources().getString(R.string.abortLiveRecordError),
                                    Toast.LENGTH_LONG).show();

                            /* end tracking and delete temp record */
                            MainActivity.getInstance().endTracking();
                        }
                    });

                }else{
                    Toast.makeText(MainActivity.getInstance(), getResources().getString(R.string.abortLiveRecord),
                            Toast.LENGTH_LONG).show();

                    /* end tracking and delete temp record */
                    MainActivity.getInstance().endTracking();
                }
            }
        });

        final MapView zomable = mapViewZoom;

        final AlertDialog alertDialog = alert.create();

        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
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

                assert zomable != null;
                zomable.zoomToBoundingBox(box, false);

                double zoomLvl = zomable.getZoomLevelDouble();

                zomable.getController().setZoom(zoomLvl - 0.3);
            }
        });

        alertDialog.show();
    }

    private void drawRoute(View alertView) {
        MapView mMapView = alertView.findViewById(R.id.mapview);
        mMapView.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE);
        mMapView.setBuiltInZoomControls(false);
        mMapView.setMultiTouchControls(true);

        /* get location */
        List<de.trackcat.Database.Models.Location> locations = locationTempDAO.readAll(newRecordId);

        /* Marker und Polyline zeichnen */
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
        stopMarker.setIcon(MainActivity.getInstance().getResources().getDrawable(R.drawable.ic_map_record_end));

        Polyline mPath = new Polyline(mMapView);

        mMapView.getOverlays().add(mPath);
        mMapView.getOverlays().add(startMarker);
        mMapView.getOverlays().add(stopMarker);

        mPath.setPoints(GPSData);
        mPath.setColor(Color.RED);
        mPath.setWidth(4);

    }

    /*
     * start Timer for stop/save Tracking Progressbar
     * */
    private void startTimer() {
        countdownTimer = new CountDownTimer(progressTime, 10) {
            @Override
            public void onTick(long toGo) {
                updateProgress(toGo);
            }

            @Override
            public void onFinish() {
                killTimer();
                progressBar.setProgress(0);
                vibe.vibrate(20);
            }
        };
        countdownTimer.start();
    }

    /*
     * update Timer progress in progressbar
     * */
    private void updateProgress(long toGo) {
        if (toGo > 0) {
            double hunderedst = (double) progressTime / 100;
            double percentage = (double) (progressTime - toGo) / hunderedst;
            progressBar.setProgress((int) percentage);
        } else {
            progressBar.setProgress(100);
        }
    }

    /*
     * stop/kill Timer progress
     * */
    private void killTimer() {
        countdownTimer.cancel();
        countdownTimer = null;
    }

    /* starts GPS Tracker and recording Objects */
    public void startTracking() {
        /* instantiate if null */
        if (kmCounter == null) {

            if (liveRecording){
                sendLiveTimer = new java.util.Timer();
                /* start Timer on 1 sec */
                sendLiveTimer.scheduleAtFixedRate(new sendLive(), 10000, 10000);
            }

            // instatiate new ModelRoute ann add to DB
            model = new Route();
            newRecordId = recordTempDAO.create(model);
            model.setId(newRecordId);

            // start Locator
            //locatorGPS = new Locator(MainActivity.getInstance(), this);

            // counts Distance
            kmCounter = new mCounter();

            // timer
            timer = new Timer(0);

            // ride Time if kmh > 0
            rideTimer = new Timer(1);

            // average Kmh
            kmhAverager = new SpeedAverager(MainActivity.getInstance(), kmCounter, rideTimer, 1);

            isTracking = true;
        } else {
            /* restart if already instantiated */
            timer.startTimer();
            rideTimer.startTimer();
            //locatorGPS.startTracking();

            isTracking = true;
        }

        playPause.setImageResource(R.drawable.record_pausebtn_white);
        issueNotification(notificationContent);
    }

    /*
     * creates or updates Notification
     * */
    private void issueNotification(String content) {
        /* returns to Record Page when Notification is clicked */
        Intent notificationIntent = MainActivity.getInstance().getIntent();// new Intent(MainActivity.getInstance().getApplicationContext(), MainActivity.class);
        notificationIntent.putExtra("action", MainActivity.getInstance().getResources().getString(R.string.fRecord));
        PendingIntent intent = PendingIntent.getActivity(MainActivity.getInstance().getApplicationContext(), 0,
                notificationIntent, 0);

        /* calls Broadcastreciever when Button "pause" is clicked and pauses Tracking + opens Record page */
        Intent pauseTrackingIntent = new Intent(MainActivity.getInstance(), NotificationActionReciever.class);
        pauseTrackingIntent.setAction("ACTION_PAUSE");
        PendingIntent intentPause = PendingIntent.getBroadcast(MainActivity.getInstance().getApplicationContext(), 0,
                pauseTrackingIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        /* calls Broadcastreciever when Button "play" is clicked and starts Tracking + opens Record page */
        Intent playTrackingIntent = new Intent(MainActivity.getInstance(), NotificationActionReciever.class);
        playTrackingIntent.setAction("ACTION_PLAY");
        PendingIntent intentPlay = PendingIntent.getBroadcast(MainActivity.getInstance().getApplicationContext(), 0,
                playTrackingIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        /*
         * create Notification
         * */
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(MainActivity.getInstance(), CHANNEL_ID)
                .setContentTitle("Laufende Aufzeichnung")
                .setContentText(content)
                .setSound(null)
                .setOngoing(false)
                .setContentIntent(intent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        /* Nur in neueren Versionen */
        if (Build.VERSION.SDK_INT >= 21) {
            mBuilder.setSmallIcon(R.drawable.ic_icon)
                    .setLargeIcon(BitmapFactory.decodeResource(MainActivity.getInstance().getResources(), R.mipmap.ic_launcher));
        }

        if (isTracking) {
            mBuilder.addAction(R.drawable.ic_pause_circle_filled_white_24dp, "Pause",
                    intentPause);
        } else {
            mBuilder.addAction(R.drawable.ic_play_circle_filled_black_24dp, "Play",
                    intentPlay);
        }

        // start Notification
        notificationManager.notify(MainActivity.getInstance().getNOTIFICATION_ID(), mBuilder.build());
    }


    /*
     * create Channel for Notification
     * */
    private void createNotificationChannel() {
        // create Notification Channel. needed from API 26
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getResources().getString(R.string.app_name);
            String description = "Shows Tracking";
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            // Register Channel to System
            NotificationManager notificationManager = MainActivity.getInstance().getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    /*
     * get Location Update in this class
     * Draw Position and Track in OSM
     * Store data in ArrayList
     * Calculate Statistics
     *----------------------------------------------------------------------------------------------
     */

    int locationCounter;
    int locationGetRound = 0;

    void updateLocation(Location location) {


        GeoPoint gPt = new GeoPoint(location.getLatitude(), location.getLongitude());

        /*
         + assign current location values to  necessary variables for map orientation
         */
        gpsBearing = location.getBearing();
        lat = (float) location.getLatitude();
        lon = (float) location.getLongitude();
        alt = (float) location.getAltitude();
        timeOfFix = location.getTime();



        /*
         * move Map
         * */
        mMapController.setCenter(gPt);

        /*
         * set Marker for current Position
         * */
        startMarker.setPosition(gPt);
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);

        try {
            FloatingActionButton fab = view.findViewById(R.id.fabButton);
            fab.setImageResource(SpeedAverager.getTypeIcon(SpeedAverager.getRouteType(kmhAverager.getAvgSpeed()), false));
        } catch (Exception e) {
            Log.v(getResources().getString(R.string.app_name), e.toString());
        }

        try {
            TextView acc_TextView = view.findViewById(R.id.accuracy_TextView);
            String toSet = location.getAccuracy() + "m";
            acc_TextView.setText(toSet);

            ImageView iconAcc = view.findViewById(R.id.accuracy_icon);
            if (location.getAccuracy() < 5) {
                iconAcc.setImageResource(R.drawable.ic_signal_cellular_4_bar_black_24dp);
            } else if (location.getAccuracy() < 10) {
                iconAcc.setImageResource(R.drawable.ic_signal_cellular_3_bar_black_24dp);
            } else if (location.getAccuracy() < 20) {
                iconAcc.setImageResource(R.drawable.ic_signal_cellular_2_bar_black_24dp);
            } else if (location.getAccuracy() > 30) {
                iconAcc.setImageResource(R.drawable.ic_signal_cellular_1_bar_black_24dp);
            }

        } catch (NullPointerException e) {
            Log.v(getResources().getString(R.string.app_name), e.toString());
        }

        try {
            if (isTracking) {
                locationCounter++;

            /*    CustomLocation toSave = new CustomLocation();
                toSave.setAltitude(location.getAltitude());
                toSave.setLatitude(location.getLatitude());
                toSave.setLongitude(location.getLongitude());
                toSave.setSpeed(location.getSpeed());
                toSave.setTime(location.getTime());
                model.addLocation(toSave);
                */


                // add Location to Model
                de.trackcat.Database.Models.Location newLocation = new de.trackcat.Database.Models.Location();
                newLocation.setAltitude(location.getAltitude());
                newLocation.setLatitude(location.getLatitude());
                newLocation.setLongitude(location.getLongitude());
                newLocation.setSpeed(location.getSpeed());
                newLocation.setTime(location.getTime());
                newLocation.setRecordId(newRecordId);
                locationTempDAO.create(newLocation);


                // add to List
                GPSData.add(gPt);

                // add Distance
                kmCounter.addKm(location);

                try {
                    /*
                     * set Polyline
                     * */
                    mPath.setPoints(GPSData);
                    mPath.setColor(Color.RED);
                    mPath.setWidth(4);
                } catch (NullPointerException e) {
                    Log.v(getResources().getString(R.string.app_name), e.toString());
                }


                /*
                 + sets the desired map rotation based on location heading if movement is detected
                 + and map is not fixed in north direction
                 */
                if ((gpsBearing >= 0.1f) && !northUp) {
                    mMapView.setMapOrientation(-gpsBearing);
                }

                /*
                 * updatde OSM Map
                 * */
                mMapView.invalidate();

                /*
                 * add Location to Statistics
                 * */

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
                    String toSet = (Math.round(location.getSpeed() * 60 * 60) / 100) / 10.0 + " km/h";
                    kmh_TextView.setText(toSet);
                } catch (NullPointerException e) {
                    Log.v(getResources().getString(R.string.app_name), e.toString());
                }
                try {
                    TextView distance_TextView = view.findViewById(R.id.distance_TextView);
                    String toSet = Math.round(kmCounter.getAmount()) / 1000.0 + " km";
                    distance_TextView.setText(toSet);
                } catch (NullPointerException e) {
                    Log.v(getResources().getString(R.string.app_name), e.toString());
                }
                try {
                    TextView altimeter_TextView = view.findViewById(R.id.altimeter_TextView);
                    String toSet = Math.round(location.getAltitude()) + " m";
                    altimeter_TextView.setText(toSet);
                } catch (NullPointerException e) {
                    Log.v(getResources().getString(R.string.app_name), e.toString());
                }

                /* send every ... loacation to server */

                if (locationCounter % 10 == 0 && liveRecording) {

                    /* Ger last ___ locations */
                    List<de.trackcat.Database.Models.Location> l = locationTempDAO.readAllWithLimit(newRecordId, 10, locationGetRound);
                    locationCounter = 0;
                    locationGetRound = locationGetRound + 10;

                    /* Get current user */
                    UserDAO userDAO = new UserDAO(MainActivity.getInstance());
                    User currentUser = userDAO.read(MainActivity.getActiveUser());

                    /* Create model */
                    RecordModelForServer m = new RecordModelForServer();
                    m.setId(liveRecordId);
                    m.setType(SpeedAverager.getRouteType(kmhAverager.getAvgSpeed()));
                    m.setTime(timer.getTime());
                    m.setRideTime(rideTimer.getTime());
                    m.setDistance(kmCounter.getAmount());
                    m.setLocations(l);

                    /* Start a call */
                    Retrofit retrofit = APIConnector.getRetrofit();
                    APIClient apiInterface = retrofit.create(APIClient.class);
                    String base = currentUser.getMail() + ":" + currentUser.getPassword();
                    String authString = "Basic " + Base64.encodeToString(base.getBytes(), Base64.NO_WRAP);
                    Call<ResponseBody> call = apiInterface.updateLiveRecord(authString, m);
                    call.enqueue(new Callback<ResponseBody>() {

                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                            /* Get jsonString from API */
                            String jsonString = null;

                            try {
                                jsonString = response.body().string();

                                /* parse json */
                                JSONObject mainObject = new JSONObject(jsonString);

                                if (mainObject.getString("success").equals("0")) {

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
                            MainActivity.getInstance().endTracking();
                            Toast.makeText(getActivity(), getResources().getString(R.string.saveRouteOffline),
                                    Toast.LENGTH_LONG).show();

                        }
                    });
                }


            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    /*
     * Stop/Pause Tracking
     * */
    public void stopTracking() {
        timer.stopTimer();
        rideTimer.stopTimer();

        stopGPS();

        isTracking = false;

        try {
            String toSet = "0.0 km/h";
            kmh_TextView.setText(toSet);
        } catch (NullPointerException e) {
            Log.v(getResources().getString(R.string.app_name), e.toString());
        }

        playPause.setImageResource(R.drawable.record_playbtn_white);
        issueNotification(notificationContent);

    }

    private void stopGPS() {
        if (locatorGPS != null) {
            locatorGPS.stopTracking();
        } else {
            //MainActivity.getInstance().stopService(new Intent(MainActivity.getInstance(), Locator.class));
        }
    }


    public void stopTimer() {
        timer.stopTimer();
        timer = null;
        rideTimer.stopTimer();
        rideTimer = null;
    }

    /*
     + !!! needs a device with the compass-functionality (magnetometer sensor) to work properly !!!
     + sets the desired map rotation based on sensor heading if no movement is detected and
     + map is not fixed in north direction
     + is called when sensor registers change of alignment
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor == magnetometer) {
            if (!northUp && gpsBearing < 0.1f) {
                mMapView.setMapOrientation(360 - mCompassOverlay.getOrientation());
            }
        }
    }

    /*
     + sets offset to the compass to show true north instead of magnetic north
     + is called when accuracy of the sensor has changed to update alignment of the compass
     */
    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        float[] rotationMatrix = new float[9];
        float[] orientation = new float[3];
        GeomagneticField gf = new GeomagneticField(lat, lon, alt, timeOfFix);

        SensorManager.getOrientation(rotationMatrix, orientation);

        float trueNorth = orientation[0] + gf.getDeclination();
        if (trueNorth > 360.0f)
            trueNorth = trueNorth - 360.0f;
        float offset = (360 - trueNorth - this.deviceOrientation);
        if (offset < 0)
            offset += 360;
        if (offset > 360)
            offset -= 360;
        mCompassOverlay.setAzimuthOffset(offset);
    }

    private class sendLive extends TimerTask {

        @Override
        public void run() {
            // für timer stoppen
            // sendLiveTimer.cancel();

        }
    }
}

package de.mobcom.group3.gotrack.Recording;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.hardware.GeomagneticField;
import android.location.Location;
import android.os.*;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.view.*;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import de.mobcom.group3.gotrack.MainActivity;
import de.mobcom.group3.gotrack.NotificationActionReciever;
import de.mobcom.group3.gotrack.R;
import de.mobcom.group3.gotrack.Recording.Recording_UI.PageViewer;
import de.mobcom.group3.gotrack.Statistics.SpeedAverager;
import de.mobcom.group3.gotrack.Statistics.mCounter;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.compass.IOrientationConsumer;
import org.osmdroid.views.overlay.compass.IOrientationProvider;
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider;

import java.util.ArrayList;
import java.util.Objects;

/*
 * Fragment for Track recording. includes GPS Locator and Statistics
 * Results displayed in this Fragment view
 * */

public class RecordFragment extends Fragment implements IOrientationConsumer {

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

    private boolean isTracking = false;

    private View view;

    /*
     * Notification stuff
     * */
    private NotificationManagerCompat notificationManager;
    private static final String CHANNEL_ID = "GoTrack_Notification_Channel_ID";

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
     + necessary variables for mapOrientation
     */

    private int deviceOrientation = 0;
    private CompassOverlay mCompassOverlay;
    private float gpsSpeed = 0f;
    private float lat = 0f;
    private float lon = 0f;
    private float alt = 0f;
    private long timeOfFix = 0;
    private float trueNorth;

    @Override
    public void onPause() {
        if (!isTracking) {
            locatorGPS.stopTracking();
        }
        super.onPause();
        mCompassOverlay.disableCompass();
        mCompassOverlay.getOrientationProvider().stopOrientationProvider();
    }

    @Override
    public void onResume() {
        super.onResume();
        mCompassOverlay.enableCompass();
        mCompassOverlay.getOrientationProvider().startOrientationProvider(mCompassOverlay);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mCompassOverlay.onDetach(mMapView);
    }


    @TargetApi(Build.VERSION_CODES.N)
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @SuppressLint({"HandlerLeak", "ClickableViewAccessibility"})
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
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
                        String toSetTime = msg.obj + "";
                        time_TextView.setText(toSetTime);

                        TextView distance_TextView = view.findViewById(R.id.distance_TextView);
                        String toSetDistance = Math.round(kmCounter.getAmount()) / 1000.0 + " km";
                        distance_TextView.setText(toSetDistance);

                        notificationContent = toSetTime + "   " + toSetDistance;

                        // todo Noftification Time/Distance String editable
                        issueNotification(notificationContent);

                    } catch (NullPointerException e) {
                        Log.v("GOREACK", e.toString());
                    }
                    /*
                     * recalculate average Speed
                     * */
                    try {
                        average_speed_TextView = view.findViewById(R.id.average_speed_TextView);
                        String toSet = Math.round((kmhAverager.getAvgSpeed() * 60 * 60) / 100) / 10.0 + " km/h";
                        average_speed_TextView.setText(toSet);
                    } catch (NullPointerException e) {
                        Log.v("GOREACK", e.toString());

                    }
                } else if (msg.what == 1) {

                    /*
                    TODO
                    setRideTime((String) msg.obj);
                    */
                }
            }
        };

        /*
         *------------------------------------------------------------------------------------------
         *Inflate the layout for this fragment
         *
         * */
        view = inflater.inflate(R.layout.fragment_record_main, container, false);

        /*
         * set Map Attributes
         */
        mMapView = view.findViewById(R.id.mapview);
        mMapView.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE);

        // kackhässliche ZoomControls----------------------------------------------------------------> für abgabe AUS!!!!!!todo
        mMapView.setBuiltInZoomControls(true);

        mMapView.setMultiTouchControls(true);
        mMapController = (MapController) mMapView.getController();
        mMapController.setZoom(18);

        /*
         * add Marker and Polyline
         * */
        startMarker = new Marker(mMapView);
        mPath = new Polyline(mMapView);


        mMapView.getOverlays().add(mPath);
        mMapView.getOverlays().add(startMarker);

        /*
         + add compass element
         + toDo: figure out if compass orientation works on real device <- not sure if it works on AVD
         + !!! needs a device with the compass-functionality to work properly !!!
         */
        if (!"Android-x86".equalsIgnoreCase(Build.BRAND)) {
            //Lock the device in current screen orientation
            int orientation = Objects.requireNonNull(getActivity()).getRequestedOrientation();
            int rotation = ((WindowManager) Objects.requireNonNull(Objects.requireNonNull(getActivity()).getSystemService(
                    Context.WINDOW_SERVICE))).getDefaultDisplay().getRotation();
            switch (rotation) {
                case Surface.ROTATION_0:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    deviceOrientation = 0;
                    break;
                case Surface.ROTATION_90:
                    deviceOrientation =90;
                    orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                    break;
                case Surface.ROTATION_180:
                    deviceOrientation =180;
                    orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
                    break;
                case Surface.ROTATION_270:
                    deviceOrientation =270;
                    orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                    break;
            }

            getActivity().setRequestedOrientation(orientation);
        }

        mCompassOverlay = new CompassOverlay(Objects.requireNonNull(getContext()),
                new InternalCompassOrientationProvider(Objects.requireNonNull(getActivity())), mMapView);

        /*
         + Option to switch between pointer and compass mode. Default is compass mode.
         + To switch on pointer mode uncomment statement below.
         */
        mCompassOverlay.setPointerMode(true);

        mMapView.getOverlays().add(mCompassOverlay);

        view.findViewById(R.id.compBtn).setOnClickListener(v -> {
            mMapView.setMapOrientation(trueNorth);
            mCompassOverlay.setAzimuthOffset(0);
        });

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
                        startTracking();
                    }
                    /* if there is any data collectet Tracked Route is saveable */
                    if (timer.getTime() > 0) {
                        /* store starttime for holdDown */
                        startTime = event.getEventTime();
                        /* timer for Progressbar */
                        startTimer();

                        /*
                         * todo
                         * Toast for user help
                         * */
                        Toast toast = Toast.makeText(MainActivity.getInstance().getApplicationContext(), "Halten für speichern", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.TOP, 0, 0);
                        toast.show();
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
        }


        /*
         * set ViewPager(km/h<->Time Swiper)
         * */
        FragmentTransaction fragTransaction = getChildFragmentManager().beginTransaction();
        fragTransaction.replace(R.id.pageViewerContainer, new PageViewer(), "PageViewer");
        fragTransaction.commit();


        /*start Tracking*/// TODO: 02.11.2018
        //startTracking();
        if (timer != null)

        {
            timer.sendTime();
        }

        if (locatorGPS == null) {
            // start Locator
            locatorGPS = new Locator(MainActivity.getInstance(), this);
        }

        locatorGPS.startTracking();

        return view;
    }

    /*
     * end Tracking ans switch to Statistics for dismisss or save
     * */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void endTracking() {

        stopTracking();
        notificationManager.cancel(MainActivity.getInstance().getNOTIFICATION_ID());

        /*
         * kill this instance and create new Fragment in Main
         * */
        MainActivity.getInstance().endTracking();
        /*
         * TODO open statistics page from here
         * */

    }


    /*   public String setTime() {
     *//* rectrate status of Timer *//*
        if (timer != null) {
            return timer.getTime();
        }
    }*/

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

                // todo start show statistics
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

            // start Locator
            //locatorGPS = new Locator(MainActivity.getInstance(), this);

            // counts Distance
            kmCounter = new mCounter();

            // timer
            timer = new Timer(0);

            // ride Time if kmh > 0
            rideTimer = new Timer(1);

            // average Kmh
            kmhAverager = new SpeedAverager(MainActivity.getInstance(), kmCounter, timer, 1);

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
        notificationIntent.putExtra("action", "RECORD");
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
                .setSmallIcon(R.drawable.ic_icon)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher))
                .setContentTitle("Laufende Aufzeichnung")
                .setContentText(content)
                .setSound(null)
                .setOngoing(false) // TODO vielleich komisch weil Notification kann gelöscht werden
                .setContentIntent(intent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

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
            CharSequence name = "GoTrack";
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
    void updateLocation(Location location) {
        GeoPoint gPt = new GeoPoint(location.getLatitude(), location.getLongitude());

        /*
         + declare necessary variables for map orientation
         */
        float gpsBearing = location.getBearing();
        gpsSpeed = location.getSpeed();
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
            Log.v("GOREACK", e.toString());

        }


        if (isTracking) {
            // add to List
            GPSData.add(gPt);

            try {
                /*
                 * set Polyline
                 * */
                mPath.setPoints(GPSData);
                mPath.setColor(Color.RED);
                mPath.setWidth(4);
            } catch (NullPointerException e) {
                Log.v("GOREACK", e.toString());
            }

            //this part adjusts the desired map and compass rotation based on device orientation and location heading
            if (gpsSpeed >= 0.01)
                setOrientation(gpsBearing);

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
                String toSet = (Math.round(location.getSpeed() * 60 * 60) / 100) / 10.0 + " km/h";
                kmh_TextView.setText(toSet);
            } catch (NullPointerException e) {
                Log.v("GOREACK", e.toString());

            }
            try {
                TextView distance_TextView = view.findViewById(R.id.distance_TextView);
                String toSet = Math.round(kmCounter.getAmount()) / 1000.0 + " km";
                distance_TextView.setText(toSet);
            } catch (NullPointerException e) {
                Log.v("GOREACK", e.toString());

            }
            try {
                TextView altimeter_TextView = view.findViewById(R.id.altimeter_TextView);
                String toSet = location.getAltitude() + " m";
                altimeter_TextView.setText(toSet);
            } catch (NullPointerException e) {
                Log.v("GOREACK", e.toString());

            }
        }
    }

    /*
     * Stop/Pause Tracking
     * */
    public void stopTracking() {
        timer.stopTimer();
        rideTimer.stopTimer();
        //locatorGPS.stopTracking();
        isTracking = false;

        try {
            String toSet = "0.0 km/h";
            kmh_TextView.setText(toSet);
        } catch (NullPointerException e) {
            Log.v("GOREACK", e.toString());
        }

        playPause.setImageResource(R.drawable.record_playbtn_white);
        issueNotification(notificationContent);

    }

    /*
     + method to provide map orientation without movement activity
     + !!! needs a device with the compass-functionality to work properly !!!
     */
    @Override
    public void onOrientationChanged(final float orientationToMagneticNorth, IOrientationProvider source) {
        if (gpsSpeed < 0.01) {
            GeomagneticField gf = new GeomagneticField(lat, lon, alt, timeOfFix);
            trueNorth = orientationToMagneticNorth + gf.getDeclination();
            gf = null;
            if (trueNorth > 360.0f)
                trueNorth = trueNorth - 360.0f;
            //this part adjusts the desired map and compass rotation based on device orientation and compass heading
            setOrientation(trueNorth);
        }
    }

    private void setOrientation(Float orientation) {
        float t = (360 - orientation - this.deviceOrientation);
        if (t < 0)
            t += 360;
        if (t > 360)
            t -= 360;
        t = (int) t;
        t /= 5;
        t = (int) t;
        t = t * 5;

        mMapView.setMapOrientation(t);
        mCompassOverlay.setAzimuthOffset(-t);
    }

}

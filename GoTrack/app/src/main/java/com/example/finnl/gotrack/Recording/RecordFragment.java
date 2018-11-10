package com.example.finnl.gotrack.Recording;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.finnl.gotrack.MainActivity;
import com.example.finnl.gotrack.NotificationActionReciever;
import com.example.finnl.gotrack.R;
import com.example.finnl.gotrack.Recording.Recording_UI.KMH_View_Fragment;
import com.example.finnl.gotrack.Recording.Recording_UI.CurrentPageIndicator;
import com.example.finnl.gotrack.Recording.Recording_UI.PageViewer;
import com.example.finnl.gotrack.Recording.Recording_UI.TimeTotal_View_Fragment;
import com.example.finnl.gotrack.Statistics.mCounter;
import com.example.finnl.gotrack.Statistics.SpeedAverager;

import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/*
 * Fragment for Track recording. includes GPS Locator and Statistics
 * Results displayed in this Fragment view
 * */

public class RecordFragment extends Fragment {
    private static final String CHANNEL_ID = "GoTrack_Notification_Channel_ID";
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

    private static KMH_View_Fragment kmhFrag;
    private static TimeTotal_View_Fragment timeFrag;

    private List<android.support.v4.app.Fragment> listFragments = new ArrayList<>();


    private TextView kmh_TextView;
    private TextView time_TextView;
    private TextView distance_TextView;
    private TextView average_speed_TextView;
    private TextView altimeter_TextView;

    private boolean isTracking = false;

    CurrentPageIndicator mIndicator;

    private View view;
    private Locator locatorGPS;

    private NotificationCompat.Builder mBuilder;
    private NotificationManagerCompat notificationManager;
    private ImageView playPause = null;
    private Vibrator vibe;
    private long startTime;
    private CountDownTimer countdownTimer;

    private ProgressBar progressBar;
    private int progressTime = 1000;
    private static ViewPager mPager;
    private static PagerAdapter mPagerAdapter;

    private static RecordFragment instance;

    public RecordFragment() {

        kmhFrag = new KMH_View_Fragment();
        timeFrag = new TimeTotal_View_Fragment();

        listFragments = new ArrayList<>();
        listFragments.add(kmhFrag);
        listFragments.add(timeFrag);

        instance = this;


    }

    @SuppressLint("HandlerLeak")
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
                    /*
                     * set Time in TextView
                     * */
                    try {
                        time_TextView = view.findViewById(R.id.time_TextView);
                        time_TextView.setText(msg.obj + "");
                    } catch (NullPointerException e) {
                    }
                    try {
                        average_speed_TextView = view.findViewById(R.id.average_speed_TextView);
                        average_speed_TextView.setText(Math.round((kmhAverager.getAvgSpeed() * 60 * 60) / 100) / 10.0 + " km/h");
                    } catch (NullPointerException e) {
                    }
                } else if (msg.what == 1) {
                    //setRideTime((String) msg.obj);
                }
            }
        };
        /*
         * #########################################################################################
         */
        createNotificationChannel();
        notificationManager = NotificationManagerCompat.from(MainActivity.getInstance());

        /*
         *------------------------------------------------------------------------------------------
         *Inflate the layout for this fragment
         *
         * */
        view = inflater.inflate(R.layout.fragment_record_main, container, false);

        mMapView = (MapView) view.findViewById(R.id.mapview);
        mMapView.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE);

        // kackhässliche ZoomControls--> für abgabe AUS!!!!!!todo
        mMapView.setBuiltInZoomControls(true);

        mMapView.setMultiTouchControls(true);
        mMapController = (MapController) mMapView.getController();
        startMarker = new Marker(mMapView);
        mPath = new Polyline(mMapView);


        mMapView.getOverlays().add(mPath);
        mMapView.getOverlays().add(startMarker);


        /*
         * Swipe view of kmh/Time
         * */


        /*// Instantiate a ViewPager and a PagerAdapter.
        mPager = (ViewPager) view.findViewById(R.id.pager);
        if (mPagerAdapter == null) {
            mPagerAdapter = new ScreenSlidePagerAdapter(MainActivity.getInstance().getSupportFragmentManager());
                    // instance.getChildFragmentManager());//MainActivity.getInstance().getSupportFragmentManager());
        }
        mPager.setAdapter(mPagerAdapter);*/

       /* listFragments = new ArrayList<>();
        listFragments.add(kmhFrag);
        listFragments.add(timeFrag);*/

        /*
         * indicatior (little dots)
         * */
        LinearLayout mLinearLayout = view.findViewById(R.id.indicator);

        //view.findViewById(R.id.pager);

       /* mIndicator = new CurrentPageIndicator(MainActivity.getInstance(), mLinearLayout, mPager, R.drawable.indicator_circle);
        mIndicator.setPageCount(listFragments.size());
        mIndicator.show();*/

        progressBar = view.findViewById(R.id.progressBar);

        vibe = (Vibrator) MainActivity.getInstance().getSystemService(Context.VIBRATOR_SERVICE);
        /*
         * Play Button
         * */

        playPause = (ImageView) view.findViewById(R.id.play_imageView);

        startTime = 0;

        playPause.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (isTracking) {
                        stopTracking();
                    } else {
                        startTracking();
                    }
                    if (timer.getTime() > 0) {
                        startTime = event.getEventTime();
                        startTimer();
                        Toast toast = Toast.makeText(MainActivity.getInstance().getApplicationContext(), "Halten für speichern", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.TOP, 0, 0);
                        toast.show();
                    }
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getEventTime() - startTime > progressTime) {
                        Log.v("huhuh", "testetstetst");


                    } else {
                        killTimer();
                        progressBar.setProgress(0);
                    }
                }
                return true;
            }
        });
        FragmentTransaction fragTransaction = getChildFragmentManager().beginTransaction();
        //getFragmentManager().beginTransaction();

        fragTransaction.replace(R.id.pageViewerContainer, new PageViewer(), "PageViewer");
        fragTransaction.commit();



       /* playPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isTracking) {
                    stopTracking();
                } else {
                    startTracking();
                }
            }
        });*/
        time_TextView = view.findViewById(R.id.time_TextView);



        /*start Tracking*/// TODO: 02.11.2018
        //startTracking();
        return view;
    }

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

    private void updateProgress(long toGo) {
        if (toGo > 0) {
            double hunderedst = (double) progressTime / 100;
            double percentage = (double) (progressTime - toGo) / hunderedst;

            progressBar.setProgress((int) percentage);
        } else {
            progressBar.setProgress(100);
        }
    }

    private void killTimer() {
        countdownTimer.cancel();
        countdownTimer = null;
    }

    /* starts GPS Tracker and recording Objects */
    public void startTracking() {

        if (locatorGPS == null) {

            // start Locator
            locatorGPS = new Locator(MainActivity.getInstance(), this);

            kmCounter = new mCounter();

            // timer
            timer = new Timer(0);

            // ride Time if kmh > 0
            rideTimer = new Timer(1);

            // average Kmh
            kmhAverager = new SpeedAverager(MainActivity.getInstance(), kmCounter, timer, 1);

            isTracking = true;
        } else {
            timer.startTimer();
            rideTimer.startTimer();
            locatorGPS.startTracking();

            isTracking = true;
        }
        vibe.vibrate(10);

        playPause.setImageResource(R.drawable.record_pausebtn_white);


        Intent notificationIntent = MainActivity.getInstance().getIntent();// new Intent(MainActivity.getInstance().getApplicationContext(), MainActivity.class);
        notificationIntent.putExtra("action", "RECORD");
        PendingIntent intent = PendingIntent.getActivity(MainActivity.getInstance().getApplicationContext(), 0,
                notificationIntent, 0);


        //Intent pauseTrackingIntent = MainActivity.getInstance().getIntent();
        //pauseTrackingIntent.putExtra("action", "pause");

        Intent pauseTrackingIntent = new Intent(MainActivity.getInstance(), NotificationActionReciever.class);
        pauseTrackingIntent.setAction("ACTION_PAUSE");
        //pauseTrackingIntent.putExtra("action", "pause");

        PendingIntent intentPause = PendingIntent.getBroadcast(MainActivity.getInstance().getApplicationContext(), 0,
                pauseTrackingIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                /*.getActivity(MainActivity.getInstance().getApplicationContext(), 0,
                pauseTrackingIntent, 0);
*/
        mBuilder = new NotificationCompat.Builder(MainActivity.getInstance(), CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_icon)
                /* .setLargeIcon(BitmapFactory.decodeResource(MainActivity.getInstance().getApplicationContext().getResources(),
                         R.drawable.ic_launcher))*/
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher))
                .setContentTitle("Laufende Aufzeichnung")
                .setContentText("TestContent")
                .setSound(null)
                .setOngoing(true)
                .setContentIntent(intent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .addAction(R.drawable.ic_pause_circle_filled_white_24dp, "Pause",
                        intentPause);


        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(00, mBuilder.build());

    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "GoTrack";
            String description = "Shows Tracking";
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = MainActivity.getInstance().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public boolean isTracking() {
        return isTracking;
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

        try {
            /*
             * set Polyline
             * */
            mPath.setPoints(GPSData);
            mPath.setColor(Color.RED);
            mPath.setWidth(4);
        } catch (NullPointerException e) {
        }
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


        /*
         *
         * Set Values in Satistics
         *
         *
         * */

        /* set Speed value in TextView */
        try {
            listFragments.get(0);
            kmh_TextView = view.findViewById(R.id.kmh_TextView);

            ViewPager pager = view.findViewById(R.id.pager);


            int count = pager.getChildCount();

            kmh_TextView.setText((Math.round(location.getSpeed() * 60 * 60) / 100) / 10.0 + " km/h");
        } catch (NullPointerException e) {
        }
        try {
            distance_TextView = view.findViewById(R.id.distance_TextView);
            distance_TextView.setText(Math.round(kmCounter.getAmount()) / 1000.0 + " km");
        } catch (NullPointerException e) {
        }
        try {
            altimeter_TextView = view.findViewById(R.id.altimeter_TextView);
            altimeter_TextView.setText(location.getAltitude() + " m");
        } catch (NullPointerException e) {
        }


    }

    public void stopTracking() {
        timer.stopTimer();
        rideTimer.stopTimer();
        locatorGPS.stopTracking();
        isTracking = false;

        try {
            kmh_TextView.setText("0.0 km/h");
        } catch (NullPointerException e) {

        }

        vibe.vibrate(10);
        playPause.setImageResource(R.drawable.record_playbtn_white);

        notificationManager.cancel(00);

    }
    /*
     *##############################################################################################
     */


    /**
     * A simple pager adapter that represents 5 ScreenSlidePageFragment objects, in
     * sequence.
     */
    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {

            return listFragments.get(position);
        }

        @Override
        public int getCount() {

            return listFragments.size();
        }
    }

}

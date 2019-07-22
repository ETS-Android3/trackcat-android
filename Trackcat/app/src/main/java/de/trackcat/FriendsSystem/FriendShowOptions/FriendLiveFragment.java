package de.trackcat.FriendsSystem.FriendShowOptions;

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
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.trackcat.APIClient;
import de.trackcat.APIConnector;
import de.trackcat.CustomElements.CustomFriend;
import de.trackcat.CustomElements.CustomLocation;
import de.trackcat.Database.DAO.UserDAO;
import de.trackcat.Database.Models.User;
import de.trackcat.FriendsSystem.Tabs.FindFriendsFragment;
import de.trackcat.FriendsSystem.Tabs.FriendQuestionsFragment;
import de.trackcat.FriendsSystem.Tabs.FriendSendQuestionsFragment;
import de.trackcat.FriendsSystem.Tabs.FriendsFragment;
import de.trackcat.MainActivity;
import de.trackcat.R;
import de.trackcat.Statistics.SpeedAverager;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class FriendLiveFragment extends Fragment {

    private MapView mMapView = null;
    ArrayList<GeoPoint> GPSData = new ArrayList<>();
    ArrayList<CustomLocation> locations;
    View view;
    private UserDAO userDAO;
    private static User currentUser;
    int index;
    TextView userTitle, averageSpeed, distance, altimeter;
    FloatingActionButton type;
    String titleStart = "Übertragung von ";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        /* Get views */
        view = inflater.inflate(R.layout.fragment_friend_live_view, container, false);
        userTitle = view.findViewById(R.id.sharing_user_title);
        averageSpeed = view.findViewById(R.id.average_speed_TextView);
        distance = view.findViewById(R.id.distance_TextView);
        altimeter = view.findViewById(R.id.altimeter_TextView);
        type = view.findViewById(R.id.fabButton);

        /* Create user DAO and get current user */
        userDAO = new UserDAO(MainActivity.getInstance());
        currentUser = userDAO.read(MainActivity.getActiveUser());

        /* Get friend id from bundle*/
        int friendId = getArguments().getInt("friendId");
        index = 0;

        Handler handler = new Handler();
        int delay = 1000; //milliseconds

        handler.postDelayed(new Runnable() {
            public void run() {
                //do something
                handler.postDelayed(this, delay);
                Log.d("HALLO", "RUN!");



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
                                //   MainActivity.getInstance().showNotAuthorizedModal(type);
                            } else {

                                /* Get jsonString from API */
                                String jsonString = response.body().string();

                                /* Parse json */
                                JSONObject mainObject = new JSONObject(jsonString);

                                /* set values */
                                userTitle.setText(titleStart + "Max Mustermann");
                               // String toSet = (Math.round(location.getSpeed() * 60 * 60) / 100) / 10.0 + " km/h";
                                averageSpeed.setText("12");
                               // String toSet = Math.round(kmCounter.getAmount()) / 1000.0 + " km";
                                distance.setText("123");
                                //String toSet = Math.round(location.getAltitude()) + " m";
                                altimeter.setText("34");
                                // average Kmh
                              //  SpeedAverager kmhAverager = new SpeedAverager(MainActivity.getInstance(), kmCounter, rideTimer, 1);
                               // type.setImageResource(SpeedAverager.getTypeIcon(SpeedAverager.getRouteType(kmhAverager.getAvgSpeed()), false));
                            }
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
        }, delay);

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
}

package de.trackcat.FriendsSystem.Tabs;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.trackcat.APIClient;
import de.trackcat.APIConnector;
import de.trackcat.CustomElements.CustomFriend;
import de.trackcat.Database.DAO.UserDAO;
import de.trackcat.Database.Models.User;
import de.trackcat.FriendsSystem.FriendListAdapter;
import de.trackcat.GlobalFunctions;
import de.trackcat.MainActivity;
import de.trackcat.R;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SharingFriendsFragment extends Fragment implements View.OnClickListener {

    /* Variables */
    EditText findFriend;
    private static TextView noEntrys;
    private UserDAO userDAO;
    private static View view;
    private static User currentUser;
    private static FriendListAdapter adapter;
    private static SwipeRefreshLayout swipeContainer;
    private static int page, maxPage;
    private static boolean backPress;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        /* Get views */
        view = inflater.inflate(R.layout.fragment_friends_sharing, container, false);
        noEntrys = view.findViewById(R.id.no_entrys);

        /* Create user DAO and get current user */
        userDAO = new UserDAO(MainActivity.getInstance());
        currentUser = userDAO.read(MainActivity.getActiveUser());

        /* Update page with SwipeRefresh */
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                adapter.clear();
                loadPage();
                swipeContainer.setRefreshing(false);
            }
        });

        /* Set page */
        if (MainActivity.getSearchFriendPage() != 0) {
            maxPage = MainActivity.getSearchFriendPage();
            backPress = true;
        } else {
            backPress = false;
        }
        page = 1;


        /* Load page */
        loadPage();

        return view;
    }

    /* Function to load friend page */
    public static void loadPage() {
        showLiveFriends();
    }


    /* Function to show all friends */
    public static void showLiveFriends() {


        /* Start a call */
        Retrofit retrofit = APIConnector.getRetrofit();
        APIClient apiInterface = retrofit.create(APIClient.class);
        String base = currentUser.getMail() + ":" + currentUser.getPassword();
        String authString = "Basic " + Base64.encodeToString(base.getBytes(), Base64.NO_WRAP);
        Call<ResponseBody> call = apiInterface.getLiveFriends(authString);
        call.enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {
                    if (response.code() == 401) {
                      //  MainActivity.getInstance().showNotAuthorizedModal(5);
                    } else {

                        /* Get jsonString from API */
                        String jsonString = response.body().string();

                        /* Parse json */
                        JSONArray friends = new JSONArray(jsonString);

                        List<CustomFriend> friendList=new ArrayList<>();

                        /* Show friend questions if they exists */
                        for (int i = 0; i < friends.length(); i++) {
                            CustomFriend friend = new CustomFriend();
                            friend.setFirstName(((JSONObject) friends.get(i)).getString("firstName"));
                            friend.setLastName(((JSONObject) friends.get(i)).getString("lastName"));
                            friend.setDateOfRegistration(((JSONObject) friends.get(i)).getLong("dateOfRegistration"));
                            friend.setImage(GlobalFunctions.getBytesFromBase64(((JSONObject) friends.get(i)).getString("image")));
                            friend.setTotalDistance(((JSONObject) friends.get(i)).getLong("totalDistance"));
                            friend.setId(((JSONObject) friends.get(i)).getInt("id"));
                            friend.setEmail(((JSONObject) friends.get(i)).getString("email"));
                            friendList.add(friend);
                        }

                        /* Add entrys to view */
                        adapter = new FriendListAdapter(MainActivity.getInstance(), friendList, false, false, false);
                        ListView friendListView = view.findViewById(R.id.friend_list);
                        friendListView.setAdapter(adapter);


                        noEntrys.setVisibility(View.GONE);
                        /* Message no friends */
                        if (friendList.size() == 0 && MainActivity.getSearchFriendTerm() == "") {
                            noEntrys.setVisibility(View.VISIBLE);
                            noEntrys.setText(MainActivity.getInstance().getResources().getString(R.string.friendNoEntry));
                        } else
                            /* Message no friends found*/
                            if (MainActivity.getSearchFriendTerm() != "") {
                                noEntrys.setVisibility(View.VISIBLE);
                                noEntrys.setText(MainActivity.getInstance().getResources().getString(R.string.friendSearchNoEntry));
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
                adapter.clear();
                Toast.makeText(MainActivity.getInstance().getApplicationContext(), MainActivity.getInstance().getResources().getString(R.string.friendNoConnection), Toast.LENGTH_SHORT).show();
                noEntrys.setVisibility(View.VISIBLE);
                noEntrys.setText(MainActivity.getInstance().getResources().getString(R.string.friendNoConnection));

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {


        }
    }
}

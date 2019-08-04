package de.trackcat.FriendsSystem.Tabs;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Base64;
import android.util.Log;
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

public class FriendsFragment extends Fragment implements View.OnKeyListener, View.OnClickListener {

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
    private static String searchTerm;
    ImageView resetSearch;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        /* Get views */
        view = inflater.inflate(R.layout.fragment_friends, container, false);
        resetSearch = view.findViewById(R.id.resetSearch);
        resetSearch.setOnClickListener(this);
        noEntrys = view.findViewById(R.id.no_entrys);

        /* Create user DAO and get current user */
        userDAO = new UserDAO(MainActivity.getInstance());
        currentUser = userDAO.read(MainActivity.getActiveUser());

        /* Update page with SwipeRefresh */
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (adapter != null) {
                   // maxPage = MainActivity.getSearchFriendPage();
                    noEntrys.setVisibility(View.GONE);
                    page = 1;
                    adapter.clear();
                    loadPage();
                }
            }
        });

        /* start refreshing by loading */
        swipeContainer.setColorSchemeColors(Color.RED, Color.BLUE, Color.YELLOW);
        swipeContainer.setRefreshing(true);
        swipeContainer.bringToFront();

        Log.d("TEEEST", "" + MainActivity.getSearchFriendPage());

        /* Set page */
        if (MainActivity.getSearchFriendPage() != 0) {
            backPress = true;
            maxPage = MainActivity.getSearchFriendPage();
        } else {
            backPress = false;
            maxPage =1;
        }
        page = 1;


        /* Get searchedTerm */
        searchTerm = MainActivity.getSearchFriendTerm();

        /* Find search field */
        findFriend = view.findViewById(R.id.findFriend);
        VectorDrawableCompat drawableCompat = VectorDrawableCompat.create(getActivity().getResources(), R.drawable.ic_search, findFriend.getContext().getTheme());
        findFriend.setCompoundDrawablesWithIntrinsicBounds(drawableCompat, null, null, null);

        //Drawable myDrawable = getResources().getDrawable(R.drawable.ic_search);
        // Drawable  d= new Drawable()
        // Drawable      drawableCompat= VectorDrawableCompat.create(getActivity().getResources(), R.drawable.ic_search, null);
        //  findFriend.setCompoundDrawablesWithIntrinsicBounds(myDrawable, null, null, null);
    /*    Drawable image = getResources().getDrawable( R.drawable.ic_search );
        int h = image.getIntrinsicHeight();
        int w = image.getIntrinsicWidth();
        image.setBounds( 0, 0, w, h );
        findFriend.setCompoundDrawables( image, null, null, null );*/
        findFriend.setOnKeyListener(this);

        /* Load page */
        loadPage();

        return view;
    }

    /* Function to load friend page */
    public static void loadPage() {
        List<CustomFriend> friendList = new ArrayList<>();
        showFriends(searchTerm, false, friendList);
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {

            /* Set variables */
            searchTerm = findFriend.getText().toString();
            MainActivity.setSearchFriendTerm(searchTerm);
            page = 1;
            maxPage=1;
            MainActivity.setSearchFriendPage(0);
            Toast.makeText(getContext(), "Suche nach Freund '" + searchTerm + "' gestartet.", Toast.LENGTH_SHORT).show();

            /* Close keyboard */
            InputMethodManager imm = (InputMethodManager) MainActivity.getInstance().getSystemService(Context.INPUT_METHOD_SERVICE);
            View view = MainActivity.getInstance().getCurrentFocus();
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

            /* Search term */
            List<CustomFriend> friendList = new ArrayList<>();
            showFriends(searchTerm, false, friendList);

            return true;
        }
        return false;
    }

    /* Function to show all friends */
    public static void showFriends(String find, boolean loadMore, List<CustomFriend> friendList) {

        /* Check if load more */
        if (loadMore) {
            page++;
            maxPage=page;
            //if (!backPress) {
            MainActivity.setSearchFriendPage(page);
            //  }
        }

        /* Create map */
        HashMap<String, String> map = new HashMap<>();
        map.put("search", find);
        map.put("page", "" + page);

        /* Start a call */
        Retrofit retrofit = APIConnector.getRetrofit();
        APIClient apiInterface = retrofit.create(APIClient.class);
        String base = currentUser.getMail() + ":" + currentUser.getPassword();
        String authString = "Basic " + Base64.encodeToString(base.getBytes(), Base64.NO_WRAP);
        Call<ResponseBody> call = apiInterface.searchMyFriends(authString, map);
        call.enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {
                    if (response.code() == 401) {
                        MainActivity.getInstance().showNotAuthorizedModal(5);
                    } else {

                        /* Get jsonString from API */
                        String jsonString = response.body().string();

                        /* Parse json */
                        JSONArray friends = new JSONArray(jsonString);

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
                            friend.setIsLive(((JSONObject) friends.get(i)).getInt("isLive"));
                            friendList.add(friend);
                        }

                        /* Add entrys to view */
                        adapter = new FriendListAdapter(MainActivity.getInstance(), friendList, false, false, false, false, false);
                        ListView friendListView = view.findViewById(R.id.friend_list);
                        friendListView.setAdapter(adapter);

                        /* Load more if backpress */
                        if (page != maxPage) {
                            showFriends(find, true, friendList);
                        } else {

                            if (!backPress &&loadMore) {
                                friendListView.setSelection((page - 1) * 10);
                            } else {
                                friendListView.setSelection(MainActivity.getSearchFriendPageIndex());
                            }
                        }

                        noEntrys.setVisibility(View.GONE);
                        /* Message no friends */
                        if (friendList.size() == 0 && MainActivity.getSearchFriendTerm() == "") {
                            noEntrys.setVisibility(View.VISIBLE);
                            noEntrys.setText(MainActivity.getInstance().getResources().getString(R.string.friendNoEntry));
                        } else
                            /* Message no friends found*/
                            if (friendList.size() == 0 && MainActivity.getSearchFriendTerm() != "") {
                                noEntrys.setVisibility(View.VISIBLE);
                                noEntrys.setText(MainActivity.getInstance().getResources().getString(R.string.friendSearchNoEntry));
                            }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                swipeContainer.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                call.cancel();
                try {
                    adapter.clear();
                } catch (Exception e) {
                }
                Toast.makeText(MainActivity.getInstance().getApplicationContext(), MainActivity.getInstance().getResources().getString(R.string.friendNoConnection), Toast.LENGTH_SHORT).show();
                noEntrys.setVisibility(View.VISIBLE);
                noEntrys.setText(MainActivity.getInstance().getResources().getString(R.string.friendNoConnection));
                swipeContainer.setRefreshing(false);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.resetSearch:
                List<CustomFriend> friendList = new ArrayList<>();
                MainActivity.setSearchFriendTerm("");
                page=1;
                maxPage=1;
                MainActivity.setSearchFriendPage(0);
                searchTerm = "";
                findFriend.setText("");
                showFriends(searchTerm, false, friendList);
                Toast.makeText(getContext(), "Suche zurückgesetzt.", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}

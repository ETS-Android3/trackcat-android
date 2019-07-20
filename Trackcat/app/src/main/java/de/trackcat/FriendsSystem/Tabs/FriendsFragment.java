package de.trackcat.FriendsSystem.Tabs;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.rahimlis.badgedtablayout.BadgedTabLayout;

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
import de.trackcat.FriendsSystem.FriendsViewerFragment;
import de.trackcat.GlobalFunctions;
import de.trackcat.MainActivity;
import de.trackcat.R;
import de.trackcat.RecordList.SwipeControll.RecordListAdapter;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class FriendsFragment extends Fragment implements View.OnKeyListener {

    EditText findFriend;
    private UserDAO userDAO;
    private static View view;
    private static User currentUser;
    private static FriendListAdapter adapter;
    private SwipeRefreshLayout swipeContainer;
    private static int page, maxPage;
    private static boolean backPress;
    private static String searchTerm;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_friends, container, false);

        /* create user DAO and get current user */
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

        /* set page */
        if (MainActivity.getSearchFriendPage() != 0) {
            maxPage = MainActivity.getSearchFriendPage();
            backPress = true;
        } else {
            backPress = false;
        }
        page = 1;

        /* get searchedTerm */
        searchTerm = MainActivity.getSearchFriendTerm();

        /* find search field */
        findFriend = view.findViewById(R.id.findFriend);
        findFriend.setOnKeyListener(this);

        /* load page */
        loadPage();

        return view;
    }

    public static void loadPage() {
        List<CustomFriend> friendList = new ArrayList<>();
        showFriends(searchTerm, false, friendList);
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
            searchTerm = findFriend.getText().toString();
            MainActivity.setSearchFriendTerm(searchTerm);
            page = 1;
            Toast.makeText(getContext(), "Suche nach Freund '" + searchTerm + "' gestartet.", Toast.LENGTH_SHORT).show();

            InputMethodManager imm = (InputMethodManager) MainActivity.getInstance().getSystemService(Context.INPUT_METHOD_SERVICE);
            View view = MainActivity.getInstance().getCurrentFocus();
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

            /* search term */
            List<CustomFriend> friendList = new ArrayList<>();
            showFriends(searchTerm, false, friendList);

            return true;
        }
        return false;
    }

    public static void showFriends(String find, boolean loadMore, List<CustomFriend> friendList) {

        /* check if load more */
        if (loadMore) {
            page++;
            if (!backPress) {
                MainActivity.setSearchFriendPage(page);
            }
        }

        /* create map */
        HashMap<String, String> map = new HashMap<>();
        map.put("search", find);
        map.put("page", "" + page);

        /* check of friends*/
        Retrofit retrofit = APIConnector.getRetrofit();
        APIClient apiInterface = retrofit.create(APIClient.class);

        /* start a call */
        String base = currentUser.getMail() + ":" + currentUser.getPassword();
        String authString = "Basic " + Base64.encodeToString(base.getBytes(), Base64.NO_WRAP);

        Call<ResponseBody> call = apiInterface.searchMyFriends(authString, map);

        call.enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                /* get jsonString from API */
                String jsonString = null;

                try {
                    jsonString = response.body().string();

                    /* parse json */
                    JSONArray friends = new JSONArray(jsonString);


                    /* show friend questions if they exists */
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

                    /* add entrys to view */
                    adapter = new FriendListAdapter(MainActivity.getInstance(), friendList, false, false);
                    ListView friendListView = view.findViewById(R.id.friend_list);
                    friendListView.setAdapter(adapter);


                    /* load more if backpress */
                    if (page != maxPage && backPress) {
                        showFriends(find, true, friendList);
                    } else {
                        if (backPress) {
                            friendListView.setSelection(MainActivity.getSearchFriendPageIndex());
                        } else {
                            friendListView.setSelection((page - 1) * 10);
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
}

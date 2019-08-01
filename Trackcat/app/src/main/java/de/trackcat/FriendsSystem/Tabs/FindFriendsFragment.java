package de.trackcat.FriendsSystem.Tabs;

import android.content.Context;
import android.os.Bundle;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
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

public class FindFriendsFragment extends Fragment implements View.OnKeyListener {

    EditText findFriend;
    private static TextView noEntrys;
    private static FriendListAdapter adapter;
    private static ProgressBar progressBar;
    private UserDAO userDAO;
    private static View view;
    String searchTerm;
    private static User currentUser;
    private static boolean backPress;
    private static int page, maxPage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_friends_find, container, false);
        noEntrys = view.findViewById(R.id.no_entrys);
        progressBar = view.findViewById(R.id.progressBar);

        /* Create user DAO */
        userDAO = new UserDAO(MainActivity.getInstance());
        currentUser = userDAO.read(MainActivity.getActiveUser());

        /* Set page */
        int t = MainActivity.getSearchForeignPageIndex();
        if (MainActivity.getSearchForeignPage() != 0) {
            maxPage = MainActivity.getSearchForeignPage();
            backPress = true;
        } else {
            backPress = false;
        }
        page = 1;

        /* Get searchedTerm */
        searchTerm = MainActivity.getSearchForeignTerm();

        /* Find search field */
        findFriend = view.findViewById(R.id.findFriend);
        VectorDrawableCompat drawableCompat = VectorDrawableCompat.create(getActivity().getResources(), R.drawable.ic_search, findFriend.getContext().getTheme());
        findFriend.setCompoundDrawablesWithIntrinsicBounds(drawableCompat, null, null, null);
        findFriend.setOnKeyListener(this);

        /* Set last search */
        if (searchTerm != null) {
            findFriend.setText(searchTerm);

            /* Search term */
            List<CustomFriend> friendList = new ArrayList<>();
            search(searchTerm, false, friendList);
        }
        return view;
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {

            /* Set variables */
            searchTerm = findFriend.getText().toString();
            MainActivity.setSearchForeignTerm(searchTerm);
            page = 1;
            Toast.makeText(getContext(), "Suche nach '" + searchTerm + "' gestartet.", Toast.LENGTH_SHORT).show();

            /* Close keyboard */
            InputMethodManager imm = (InputMethodManager) MainActivity.getInstance().getSystemService(Context.INPUT_METHOD_SERVICE);
            View view = MainActivity.getInstance().getCurrentFocus();
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

            /* Search term */
            List<CustomFriend> friendList = new ArrayList<>();
            search(searchTerm, false, friendList);

            /* show progressbar */
            progressBar.setVisibility(View.VISIBLE);

            return true;
        }
        return false;
    }

    public static void search(String find, boolean loadMore, List<CustomFriend> friendList) {

        /* Check if load more */
        if (loadMore) {
            page++;
            if (!backPress) {
                MainActivity.setSearchForeignPage(page);
            }
        }

        /* Create map */
        HashMap<String, String> map = new HashMap<>();
        map.put("search", "" + find);
        map.put("page", "" + page);

        /* Start a call */
        Retrofit retrofit = APIConnector.getRetrofit();
        APIClient apiInterface = retrofit.create(APIClient.class);
        String base = currentUser.getMail() + ":" + currentUser.getPassword();
        String authString = "Basic " + Base64.encodeToString(base.getBytes(), Base64.NO_WRAP);
        Call<ResponseBody> call = apiInterface.findFriend(authString, map);

        call.enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {
                    if (response.code() == 401) {
                        MainActivity.getInstance().showNotAuthorizedModal(8);
                    } else {

                        /* Get jsonString from API */
                        String jsonString = response.body().string();

                        /* Parse json */
                        JSONArray friends = new JSONArray(jsonString);

                        /* Show search entrys exists */
                        for (int i = 0; i < friends.length(); i++) {
                            CustomFriend friend = new CustomFriend();
                            friend.setFirstName(((JSONObject) friends.get(i)).getString("firstName"));
                            friend.setLastName(((JSONObject) friends.get(i)).getString("lastName"));
                            friend.setDateOfRegistration(((JSONObject) friends.get(i)).getLong("dateOfRegistration"));
                            friend.setImage(GlobalFunctions.getBytesFromBase64(((JSONObject) friends.get(i)).getString("image")));
                            friend.setTotalDistance(((JSONObject) friends.get(i)).getLong("totalDistance"));
                            friend.setId(((JSONObject) friends.get(i)).getInt("id"));
                            friendList.add(friend);
                        }

                        /* Add entrys to view */
                        adapter = new FriendListAdapter(MainActivity.getInstance(), friendList, true, false, false, false);
                        ListView friendListView = view.findViewById(R.id.friend_list);
                        friendListView.setAdapter(adapter);


                        /* Load more if backpress */
                        if (page != maxPage && backPress) {
                            search(find, true, friendList);
                        } else {

                            friendListView.setSelection(MainActivity.getSearchForeignPageIndex());

                         /*   if (backPress) {
                               } else {
                                friendListView.setSelection((page - 1) * 10);
                            }*/
                        }

                        /* delete progressbar */
                        progressBar.setVisibility(View.GONE);

                        noEntrys.setVisibility(View.GONE);
                        /* Message no friends */
                        if (friendList.size() == 0 && MainActivity.getSearchForeignTerm() != null) {
                            noEntrys.setVisibility(View.VISIBLE);
                            noEntrys.setText(MainActivity.getInstance().getResources().getString(R.string.friendSearchNoEntry));
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.getInstance().getApplicationContext(), MainActivity.getInstance().getResources().getString(R.string.friendSearchError), Toast.LENGTH_SHORT).show();

                    /* delete progressbar */
                    progressBar.setVisibility(View.GONE);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.getInstance().getApplicationContext(), MainActivity.getInstance().getResources().getString(R.string.friendSearchError), Toast.LENGTH_SHORT).show();

                    /* delete progressbar */
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                call.cancel();
                try {
                    adapter.clear();
                } catch (Exception e) {
                }
                Toast.makeText(MainActivity.getInstance().getApplicationContext(), MainActivity.getInstance().getResources().getString(R.string.friendSearchNoConnection), Toast.LENGTH_SHORT).show();
                noEntrys.setVisibility(View.VISIBLE);
                noEntrys.setText(MainActivity.getInstance().getResources().getString(R.string.friendSearchNoConnection));

                /* delete progressbar */
                progressBar.setVisibility(View.GONE);
            }
        });
    }
}

package de.trackcat.FriendsSystem;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import de.trackcat.APIClient;
import de.trackcat.APIConnector;
import de.trackcat.CustomElements.CustomFriend;
import de.trackcat.Database.DAO.UserDAO;
import de.trackcat.Database.Models.User;
import de.trackcat.FriendsSystem.FriendShowOptions.FriendLiveFragment;
import de.trackcat.FriendsSystem.FriendShowOptions.FriendProfileFragment;
import de.trackcat.FriendsSystem.FriendShowOptions.PublicPersonProfileFragment;
import de.trackcat.FriendsSystem.Tabs.FindFriendsFragment;
import de.trackcat.FriendsSystem.Tabs.FriendQuestionsFragment;
import de.trackcat.FriendsSystem.Tabs.FriendSendQuestionsFragment;
import de.trackcat.FriendsSystem.Tabs.FriendsFragment;
import de.trackcat.GlobalFunctions;
import de.trackcat.MainActivity;
import de.trackcat.R;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class FriendListAdapter extends ArrayAdapter<String> implements View.OnClickListener {

    private List<CustomFriend> friends;
    public TextView name, email, registSince;
    LayoutInflater inflater;
    CircleImageView image, state;
    boolean newFriend, friendQuestion, sendFriendQuestion, liveFriend;
    UserDAO userDAO;

    public FriendListAdapter(Activity context, List<CustomFriend> friends, boolean type, boolean friendQuestion, boolean sendFriendQuestion, boolean liveFriend) {
        super(context, R.layout.friend_list_item);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.friends = friends;
        this.newFriend = type;
        this.friendQuestion = friendQuestion;
        this.sendFriendQuestion = sendFriendQuestion;
        this.liveFriend = liveFriend;

        /* Create userDAO */
        userDAO = new UserDAO(MainActivity.getInstance());
    }

    @Override
    public int getCount() {
        return friends.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = null;
        /* Friend list */
        if (!newFriend) {

            /* Last item */
            if (friends.size() % 10 == 0 && position == friends.size() - 1 && !liveFriend) {
                view = inflater.inflate(R.layout.friend_list_last_item, parent, false);
                Button loadMore = view.findViewById(R.id.loadMore);
                loadMore.setOnClickListener(this);

                /* Item between */
            } else {
                view = inflater.inflate(R.layout.friend_list_item, parent, false);
            }

            /* Set variables */
            name = view.findViewById(R.id.friend_name);
            email = view.findViewById(R.id.friend_email);
            image = view.findViewById(R.id.profile_image);
            state = view.findViewById(R.id.profile_state);

            /* Add name and regist since */
            name.setText(friends.get(position).getFirstName() + " " + friends.get(position).getLastName());
            email.setText(friends.get(position).getEmail());

            /* Find level */
            double distance = Math.round(friends.get(position).getTotalDistance());
            double levelDistance;
            if (distance >= 1000) {
                levelDistance = distance / 1000L;
            } else {
                levelDistance = distance / 1000;
            }
            state.setImageBitmap(GlobalFunctions.findLevel(levelDistance));

            /* Set profile image */
            byte[] imgRessource = friends.get(position).getImage();
            Bitmap bitmap = BitmapFactory.decodeResource(MainActivity.getInstance().getResources(), R.raw.default_profile);
            if (imgRessource != null && imgRessource.length > 3) {
                bitmap = BitmapFactory.decodeByteArray(imgRessource, 0, imgRessource.length);
            }
            image.setImageBitmap(bitmap);

            if(friends.get(position).getIsLive()==1){
                image.setBorderColor(MainActivity.getInstance().getResources().getColor(R.color.live));
                image.setBorderWidth(9);
            }

            /* Shows details of routes */
            view.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    AlertDialog.Builder alertdialogbuilder = new AlertDialog.Builder(getContext());
                    alertdialogbuilder.setTitle(getContext().getResources().getString(R.string.friendsOptionTitle));

                    /* liveFriendOptions */
                    if (liveFriend|| friends.get(position).getIsLive()==1) {
                        alertdialogbuilder.setItems(getContext().getResources().getStringArray(R.array.friendLiveOptions), new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int id) {

                                /* Show profile */
                                if (id == 0) {
                                    FriendProfileFragment friendProfileFragment = new FriendProfileFragment();
                                    Bundle bundle = new Bundle();
                                    bundle.putInt("friendId", friends.get(position).getId());
                                    bundle.putInt("authorizationType", 9);
                                    friendProfileFragment.setArguments(bundle);
                                    FragmentTransaction fragTransaction = MainActivity.getInstance().getSupportFragmentManager().beginTransaction();
                                    fragTransaction.replace(R.id.mainFrame, friendProfileFragment,
                                            MainActivity.getInstance().getResources().getString(R.string.fFriendLiveProfile));
                                    fragTransaction.commit();
                                }

                                /* Show friend live view */
                                if (id == 1) {
                                    FriendLiveFragment friendLiveFragment = new FriendLiveFragment();
                                    Bundle bundle = new Bundle();
                                    bundle.putInt("friendId", friends.get(position).getId());
                                    friendLiveFragment.setArguments(bundle);
                                    FragmentTransaction fragTransaction = MainActivity.getInstance().getSupportFragmentManager().beginTransaction();

                                    /* LiveFriendList */
                                    if(liveFriend){
                                        fragTransaction.replace(R.id.mainFrame, friendLiveFragment,
                                                MainActivity.getInstance().getResources().getString(R.string.fFriendLiveViewList));


                                                /* Live friend from profile */
                                    }else{
                                        fragTransaction.replace(R.id.mainFrame, friendLiveFragment,
                                                MainActivity.getInstance().getResources().getString(R.string.fFriendLiveView));
                                    }

                                    fragTransaction.commit();
                                }

                                /* Delete friend */
                                if (id == 2) {
                                    deleteFriend(friends.get(position).getId());
                                }
                            }
                        });
                        /* FriendOptions */
                    } else {
                        alertdialogbuilder.setItems(getContext().getResources().getStringArray(R.array.friendOptions), new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int id) {

                                /* Show profile */
                                if (id == 0) {
                                    FriendProfileFragment friendProfileFragment = new FriendProfileFragment();
                                    Bundle bundle = new Bundle();
                                    bundle.putInt("friendId", friends.get(position).getId());
                                    bundle.putInt("authorizationType", 5);
                                    friendProfileFragment.setArguments(bundle);
                                    FragmentTransaction fragTransaction = MainActivity.getInstance().getSupportFragmentManager().beginTransaction();
                                    fragTransaction.replace(R.id.mainFrame, friendProfileFragment,
                                            MainActivity.getInstance().getResources().getString(R.string.fFriendProfile));
                                    fragTransaction.commit();

                                    /* Set id for backPress */
                                    MainActivity.setSearchFriendPageIndex(position);
                                }

                                /* Delete friend */
                                if (id == 1) {
                                    deleteFriend(friends.get(position).getId());
                                }
                            }
                        });
                    }
                    AlertDialog dialog = alertdialogbuilder.create();
                    dialog.show();
                }
            });
            /* Stranger list */
        } else {

            /* Last item */
            if (friends.size() % 10 == 0 && position == friends.size() - 1 && !friendQuestion && !sendFriendQuestion) {
                view = inflater.inflate(R.layout.new_friend_list_last_item, parent, false);
                Button loadMore = view.findViewById(R.id.loadMore);
                loadMore.setOnClickListener(this);
                /* Items between */
            } else {
                if (friendQuestion && !sendFriendQuestion) {
                    view = inflater.inflate(R.layout.friend_list_item, parent, false);
                } else {
                    view = inflater.inflate(R.layout.new_friend_list_item, parent, false);
                }
            }

            /* Set name and register since OR email */
            name = view.findViewById(R.id.friend_name);
            name.setText(friends.get(position).getFirstName() + " " + friends.get(position).getLastName());
            if (!friendQuestion || sendFriendQuestion) {
                registSince = view.findViewById(R.id.friend_regist_since);
                registSince.setText(GlobalFunctions.getDateWithTimeFromSeconds(friends.get(position).getDateOfRegistration(), "dd.MM.yyyy"));
            } else {
                email = view.findViewById(R.id.friend_email);
                email.setText(friends.get(position).getEmail());
            }

            /* Set level */
            state = view.findViewById(R.id.profile_state);
            double distance = Math.round(friends.get(position).getTotalDistance());
            double levelDistance;
            if (distance >= 1000) {
                levelDistance = distance / 1000L;
            } else {
                levelDistance = distance / 1000;
            }
            state.setImageBitmap(GlobalFunctions.findLevel(levelDistance));

            /* Set profile image */
            image = view.findViewById(R.id.profile_image);
            byte[] imgRessource = friends.get(position).getImage();
            Bitmap bitmap = BitmapFactory.decodeResource(MainActivity.getInstance().getResources(), R.raw.default_profile);
            if (imgRessource != null && imgRessource.length > 3) {
                bitmap = BitmapFactory.decodeByteArray(imgRessource, 0, imgRessource.length);
            }
            image.setImageBitmap(bitmap);

            /* Add on click listener */
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder alertdialogbuilder = new AlertDialog.Builder(getContext());
                    alertdialogbuilder.setTitle(getContext().getResources().getString(R.string.friendsOptionTitle));

                    /* Stranger list (search page) */
                    if (!friendQuestion) {
                        alertdialogbuilder.setItems(getContext().getResources().getStringArray(R.array.foreignOptions), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {

                                /* Show public profile */
                                if (id == 0) {
                                    PublicPersonProfileFragment publicPersonProfileFragment = new PublicPersonProfileFragment();
                                    Bundle bundle = new Bundle();
                                    bundle.putInt("friendId", friends.get(position).getId());
                                    bundle.putInt("authorizationType", 8);
                                    publicPersonProfileFragment.setArguments(bundle);
                                    FragmentTransaction fragTransaction = MainActivity.getInstance().getSupportFragmentManager().beginTransaction();
                                    fragTransaction.replace(R.id.mainFrame, publicPersonProfileFragment,
                                            MainActivity.getInstance().getResources().getString(R.string.fPublicPersonProfile));
                                    fragTransaction.commit();
                                    MainActivity.setSearchForeignPageIndex(position);
                                }

                                /* AddFriend */
                                if (id == 1) {
                                    addFriend(friends.get(position).getId());
                                }
                            }
                        });
                        /* Friend list by friend request */
                    } else {

                        /* Send friend request */
                        if (sendFriendQuestion) {
                            alertdialogbuilder.setItems(getContext().getResources().getStringArray(R.array.sendFriendQuestionOptions), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {

                                    /* Show public profile */
                                    if (id == 0) {
                                        PublicPersonProfileFragment publicPersonProfileFragment = new PublicPersonProfileFragment();
                                        Bundle bundle = new Bundle();
                                        bundle.putInt("friendId", friends.get(position).getId());
                                        bundle.putInt("authorizationType", 7);
                                        publicPersonProfileFragment.setArguments(bundle);
                                        FragmentTransaction fragTransaction = MainActivity.getInstance().getSupportFragmentManager().beginTransaction();
                                        fragTransaction.replace(R.id.mainFrame, publicPersonProfileFragment,
                                                MainActivity.getInstance().getResources().getString(R.string.fPublicPersonProfileSendQuestion));
                                        fragTransaction.commit();

                                        /* Set index */
                                        MainActivity.setSendFriendQuestionIndex(position);
                                    }

                                    /* Delete friend */
                                    if (id == 1) {
                                        deleteFriend(friends.get(position).getId());
                                    }
                                }
                            });

                            /* Received friend requests*/
                        } else {
                            alertdialogbuilder.setItems(getContext().getResources().getStringArray(R.array.friendQuestionOptions), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {

                                    /* Show public profile */
                                    if (id == 0) {
                                        PublicPersonProfileFragment publicPersonProfileFragment = new PublicPersonProfileFragment();
                                        Bundle bundle = new Bundle();
                                        bundle.putInt("friendId", friends.get(position).getId());
                                        bundle.putInt("authorizationType", 6);
                                        publicPersonProfileFragment.setArguments(bundle);
                                        FragmentTransaction fragTransaction = MainActivity.getInstance().getSupportFragmentManager().beginTransaction();
                                        fragTransaction.replace(R.id.mainFrame, publicPersonProfileFragment,
                                                MainActivity.getInstance().getResources().getString(R.string.fPublicPersonProfileQuestion));
                                        fragTransaction.commit();

                                        /* Set index */
                                        MainActivity.setFriendQuestionIndex(position);
                                    }

                                    /* AddFriend */
                                    if (id == 1) {
                                        addFriend(friends.get(position).getId());
                                    }

                                    /* Delete friend */
                                    if (id == 2) {
                                        deleteFriend(friends.get(position).getId());
                                    }
                                }
                            });
                        }
                    }
                    AlertDialog dialog = alertdialogbuilder.create();
                    dialog.show();
                }
            });
        }
        return view;
    }

    /* Function to clear list */
    public void clear() {
        friends.clear();
        notifyDataSetChanged();
    }

    /* Function to add a friend */
    private void addFriend(int friendId) {

        /* Check type */
        int type;
        if (friendQuestion) {
            type = 6;
        } else {
            type = 8;
        }

        /* Create hashmap */
        HashMap<String, String> map = new HashMap<>();
        map.put("friendId", "" + friendId);

        /* Start a call */
        Retrofit retrofit = APIConnector.getRetrofit();
        APIClient apiInterface = retrofit.create(APIClient.class);
        User currentUser = userDAO.read(MainActivity.getActiveUser());
        String base = currentUser.getMail() + ":" + currentUser.getPassword();
        String authString = "Basic " + Base64.encodeToString(base.getBytes(), Base64.NO_WRAP);
        Call<ResponseBody> call = apiInterface.requestFriend(authString, map);
        call.enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {
                    if (response.code() == 401) {
                        MainActivity.getInstance().showNotAuthorizedModal(type);
                    } else {

                        /* Get jsonString from API */
                        String jsonString = response.body().string();

                        /* Parse json */
                        JSONObject mainObject = new JSONObject(jsonString);

                        /* Friendship question okay */
                        if (mainObject.getString("success").equals("0")) {
                            Toast.makeText(getContext(), getContext().getResources().getString(R.string.friendQuestionOkay), Toast.LENGTH_SHORT).show();

                            /* Reload search page */
                            FriendSendQuestionsFragment.loadPage();
                            if (MainActivity.getSearchForeignPage() > 1) {
                                FindFriendsFragment.search(MainActivity.getSearchForeignTerm(), false, friends);
                            } else {
                                List<CustomFriend> f = new ArrayList<>();
                                FindFriendsFragment.search(MainActivity.getSearchForeignTerm(), false, f);
                            }

                            /* Friendship question error */
                        } else if (mainObject.getString("success").equals("1")) {
                            Toast.makeText(getContext(), getContext().getResources().getString(R.string.friendQuestionError), Toast.LENGTH_SHORT).show();

                            /* Friendship question check */
                        } else if (mainObject.getString("success").equals("2")) {
                            Toast.makeText(getContext(), getContext().getResources().getString(R.string.friendQuestionCheck), Toast.LENGTH_SHORT).show();

                            /* Load page new if friendship accepted */
                            if (newFriend && friendQuestion) {
                                FriendQuestionsFragment.loadPage();
                                FriendsFragment.loadPage();
                            }
                        }
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


    /* Function to add a friend */
    private void deleteFriend(int friendId) {

        /* Create AlertBox */
        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        String positivText = "";
        int type;

        /* Show delete text */
        if (newFriend) {
            if (sendFriendQuestion) {
                alert.setTitle("Anfrage wirklich zurückziehen?");
                alert.setMessage(MainActivity.getInstance().getResources().getString(R.string.friendsSendQuestionDelete));
                positivText = "zurückziehen";
                type = 7;
            } else {
                alert.setTitle("Anfrage wirklich ablehen?");
                alert.setMessage(MainActivity.getInstance().getResources().getString(R.string.friendsQuestionDelete));
                positivText = "ablehen";
                type = 6;
            }
        } else {
            alert.setTitle("Freund wirklich entfernen?");
            alert.setMessage(MainActivity.getInstance().getResources().getString(R.string.friendsDelete));
            positivText = "entfernen";
            type = 5;
        }

        alert.setPositiveButton(positivText, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                /* Create hashmap */
                HashMap<String, String> map = new HashMap<>();
                map.put("friendId", "" + friendId);

                /* Start a call */
                Retrofit retrofit = APIConnector.getRetrofit();
                APIClient apiInterface = retrofit.create(APIClient.class);
                User currentUser = userDAO.read(MainActivity.getActiveUser());
                String base = currentUser.getMail() + ":" + currentUser.getPassword();
                String authString = "Basic " + Base64.encodeToString(base.getBytes(), Base64.NO_WRAP);
                Call<ResponseBody> call = apiInterface.deleteFriend(authString, map);
                call.enqueue(new Callback<ResponseBody>() {

                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                        try {
                            if (response.code() == 401) {
                                MainActivity.getInstance().showNotAuthorizedModal(type);
                            } else {

                                /* Get jsonString from API */
                                String jsonString = response.body().string();

                                /* Parse json */
                                JSONObject mainObject = new JSONObject(jsonString);

                                /* Delete friend okay */
                                if (mainObject.getString("success").equals("0")) {

                                    /* Delete friend questions */
                                    if (newFriend) {
                                        if (!sendFriendQuestion) {
                                            Toast.makeText(getContext(), getContext().getResources().getString(R.string.friendDeleteQuestionOkay), Toast.LENGTH_SHORT).show();
                                            FriendQuestionsFragment.loadPage();
                                        } else {
                                            Toast.makeText(getContext(), getContext().getResources().getString(R.string.friendDeleteSendQuestionOkay), Toast.LENGTH_SHORT).show();
                                            FriendSendQuestionsFragment.loadPage();
                                        }

                                        /* Delete friends */
                                    } else {
                                        Toast.makeText(getContext(), getContext().getResources().getString(R.string.friendDeleteFriendOkay), Toast.LENGTH_SHORT).show();
                                        FriendsFragment.loadPage();
                                    }

                                    /* Delete friend error */
                                } else if (mainObject.getString("success").equals("1")) {
                                    Toast.makeText(getContext(), getContext().getResources().getString(R.string.friendDeleteError), Toast.LENGTH_SHORT).show();
                                }
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
        });

        alert.setNegativeButton("Abbruch", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });

        alert.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            /* Load more event */
            case R.id.loadMore:

                /* Check if is a newFriend list or a friend list */
                if (newFriend) {
                    FindFriendsFragment.search(MainActivity.getSearchForeignTerm(), true, friends);
                } else {
                    FriendsFragment.showFriends(MainActivity.getSearchFriendTerm(), true, friends);
                }
                break;
        }
    }
}

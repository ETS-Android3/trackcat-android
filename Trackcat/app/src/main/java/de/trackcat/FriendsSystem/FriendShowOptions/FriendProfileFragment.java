package de.trackcat.FriendsSystem.FriendShowOptions;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;
import de.trackcat.APIClient;
import de.trackcat.APIConnector;
import de.trackcat.Database.DAO.UserDAO;
import de.trackcat.Database.Models.User;
import de.trackcat.GlobalFunctions;
import de.trackcat.MainActivity;
import de.trackcat.R;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class FriendProfileFragment extends Fragment {

    RelativeLayout loadProfile;
    TextView name, email, dayOfBirth, gender, amountRecords, totalTime, totalDistance, dayOfRegistration, lastLogIn;
    CircleImageView image, state;
    ImageView birthday, user_gender_image;
    UserDAO userDAO;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_friends_profile, container, false);

        /* get profil fields */
        loadProfile = view.findViewById(R.id.loadScreen);
        image = view.findViewById(R.id.profile_image);
        state = view.findViewById(R.id.profile_state);
        name = view.findViewById(R.id.user_name);
        email = view.findViewById(R.id.user_email);
        dayOfBirth = view.findViewById(R.id.user_dayOfBirth);
        birthday = view.findViewById(R.id.user_birthday);
        gender = view.findViewById(R.id.user_gender);
        user_gender_image = view.findViewById(R.id.user_gender_image);
        amountRecords = view.findViewById(R.id.user_amount_records);
        totalTime = view.findViewById(R.id.user_amount_time_records);
        totalDistance = view.findViewById(R.id.user_amount_distance_records);
        lastLogIn = view.findViewById(R.id.user_lastLogIn);
        dayOfRegistration = view.findViewById(R.id.user_dayOfRegistration);

        /* set loadscreen invisible */
        loadProfile.setVisibility(View.GONE);

        /*create userDAO*/
        userDAO = new UserDAO(MainActivity.getInstance());

        /*get friend id from bundle*/
        int friendId = getArguments().getInt("friendId");

        /* create hashmap */
        HashMap<String, String> map = new HashMap<>();
        map.put("friendId", "" + friendId);

        Retrofit retrofit = APIConnector.getRetrofit();
        APIClient apiInterface = retrofit.create(APIClient.class);

        /* start a call */
        User currentUser = userDAO.read(MainActivity.getActiveUser());
        String base = currentUser.getMail() + ":" + currentUser.getPassword();
        String authString = "Basic " + Base64.encodeToString(base.getBytes(), Base64.NO_WRAP);

        Call<ResponseBody> call = apiInterface.showFriendProfile(authString, map);

        call.enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {
                    /* get jsonString from API */
                    String jsonString = response.body().string();

                    /* parse json */
                    JSONObject mainObject = new JSONObject(jsonString);

                    /* set values */
                    name.setText(mainObject.getString("firstName") + " " + mainObject.getString("lastName"));
                    dayOfBirth.setText(GlobalFunctions.getDateFromSeconds(mainObject.getLong("dateOfBirth"), "dd.MM.yyyy"));

                    /* set gender */
                    if (mainObject.getInt("gender") != 2) {
                        InputStream imageStream;
                        if (mainObject.getInt("gender") == 0) {
                            gender.setText("weiblich");
                            gender.setTextColor(getResources().getColor(R.color.colorFemale));
                            imageStream = getContext().getResources().openRawResource(R.raw.female);
                        } else {
                            gender.setText("männlich");
                            imageStream = getContext().getResources().openRawResource(R.raw.male);
                            gender.setTextColor(getResources().getColor(R.color.colorMale));
                        }

                        Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
                        user_gender_image.setImageBitmap(bitmap);
                        user_gender_image.setVisibility(View.VISIBLE);
                    } else {
                        GlobalFunctions.setNoInformationStyle(gender);
                        user_gender_image.setVisibility(View.GONE);
                    }

                    /* set profile image */
                    byte[] imgRessource = GlobalFunctions.getBytesFromBase64(mainObject.getString("image"));
                    Bitmap bitmap = BitmapFactory.decodeResource(MainActivity.getInstance().getResources(), R.raw.default_profile);
                    if (imgRessource != null && imgRessource.length > 0) {
                        bitmap = BitmapFactory.decodeByteArray(imgRessource, 0, imgRessource.length);
                    }
                    image.setImageBitmap(bitmap);

                    /* set dateOfRegistration*/
                    String curdayIfRegistrationString = GlobalFunctions.getDateWithTimeFromSeconds(mainObject.getLong("dateOfRegistration"), "dd.MM.yyyy HH:mm");
                    dayOfRegistration.setText(curdayIfRegistrationString);

                    /* set lastLogin*/
                    String curLastLoginString = GlobalFunctions.getDateWithTimeFromSeconds(mainObject.getLong("lastLogin"), "dd.MM.yyyy HH:mm");
                    lastLogIn.setText(curLastLoginString);

                    /*set amount records*/
                    amountRecords.setText(""+mainObject.getLong("amountRecords"));

                    /* set total distance */
                    double distance = Math.round(mainObject.getLong("totalDistance"));
                    double levelDistance;
                    if (distance >= 1000) {
                        String d = "" + Math.round((distance / 1000L)*100)/100.0;
                        totalDistance.setText(d.replace('.', ',') + " km");
                        levelDistance=distance / 1000L;
                    } else {
                        levelDistance= distance/1000;
                        totalDistance.setText((int) distance + " m");
                    }

                    /* set total time */
                    SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
                    TimeZone tz = TimeZone.getTimeZone("UTC");
                    df.setTimeZone(tz);
                    String time = df.format(new Date(mainObject.getLong("totalTime") * 1000));
                    totalTime.setText(time);

                    /* set level */
                    state.setImageBitmap(GlobalFunctions.findLevel(levelDistance));

                    /* set email */
                    email.setText(""+mainObject.getString("email"));

                    /* remove loadscreen */
                    loadProfile.setVisibility(View.GONE);

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

        return view;
    }
}

package de.trackcat.FriendsSystem.FriendShowOptions;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

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

public class PublicPersonProfileFragment extends Fragment {

    RelativeLayout loadProfile;
    TextView name, age, totalDistance, dayOfRegistration;
    CircleImageView image, state;
    ImageView user_gender_image;
    UserDAO userDAO;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_friends_public_person_profile, container, false);

        /* Get profile fields */
        loadProfile = view.findViewById(R.id.loadScreen);
        image = view.findViewById(R.id.profile_image);
        state = view.findViewById(R.id.profile_state);
        name = view.findViewById(R.id.user_name);
        age = view.findViewById(R.id.user_age);
        totalDistance = view.findViewById(R.id.user_amount_distance_records);
        user_gender_image = view.findViewById(R.id.user_gender_image);
        dayOfRegistration = view.findViewById(R.id.user_dayOfRegistration);

        /* Create userDAO*/
        userDAO = new UserDAO(MainActivity.getInstance());

        /* Get friendId and authorizationType from bundle */
        int friendId = getArguments().getInt("friendId");
        int authorizationType = getArguments().getInt("authorizationType");

        /* Create hashMap */
        HashMap<String, String> map = new HashMap<>();
        map.put("strangerId", "" + friendId);

        /* Start a call */
        Retrofit retrofit = APIConnector.getRetrofit();
        APIClient apiInterface = retrofit.create(APIClient.class);
        User currentUser = userDAO.read(MainActivity.getActiveUser());
        String base = currentUser.getMail() + ":" + currentUser.getPassword();
        String authString = "Basic " + Base64.encodeToString(base.getBytes(), Base64.NO_WRAP);
        Call<ResponseBody> call = apiInterface.showStrangerProfile(authString, map);
        call.enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {
                    if (response.code() == 401) {
                        MainActivity.getInstance().showNotAuthorizedModal(authorizationType);
                    } else {

                        /* Get jsonString from API */
                        String jsonString = response.body().string();

                        /* Parse json */
                        JSONObject mainObject = new JSONObject(jsonString);

                        /* Set values */
                        name.setText(mainObject.getString("firstName") + " " + mainObject.getString("lastName"));

                        int ageNumber = mainObject.getInt("age");

                        if (ageNumber == 1) {
                            age.setText("" + ageNumber + " Jahr");
                        } else {
                            age.setText("" + ageNumber + " Jahre");
                        }

                        dayOfRegistration.setText(GlobalFunctions.getDateFromSeconds(mainObject.getLong("dateOfRegistration"), "dd.MM.yyyy"));

                        /* set total distance */
                        double distance = Math.round(mainObject.getLong("totalDistance"));
                        double levelDistance;
                        if (distance >= 1000) {
                            String d = "" + Math.round((distance / 1000L) * 100) / 100.0;
                            totalDistance.setText(d.replace('.', ',') + " km");
                            levelDistance = distance / 1000L;
                        } else {
                            levelDistance = distance / 1000;
                            totalDistance.setText((int) distance + " m");
                        }

                        /* Set profile image */
                        byte[] imgRessource = GlobalFunctions.getBytesFromBase64(mainObject.getString("image"));
                        Bitmap bitmap = BitmapFactory.decodeResource(MainActivity.getInstance().getResources(), R.raw.default_profile);
                        if (imgRessource != null && imgRessource.length > 0) {
                            bitmap = BitmapFactory.decodeByteArray(imgRessource, 0, imgRessource.length);
                        }
                        image.setImageBitmap(bitmap);

                        /* Set level */
                        state.setImageBitmap(GlobalFunctions.findLevel(levelDistance));

                        /* Remove loadScreen */
                        loadProfile.setVisibility(View.GONE);
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
        return view;
    }
}

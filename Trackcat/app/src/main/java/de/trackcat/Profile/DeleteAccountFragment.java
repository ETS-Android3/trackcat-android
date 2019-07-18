package de.trackcat.Profile;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import de.trackcat.APIClient;
import de.trackcat.APIConnector;
import de.trackcat.Database.DAO.UserDAO;
import de.trackcat.Database.Models.User;
import de.trackcat.MainActivity;
import de.trackcat.R;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class DeleteAccountFragment extends Fragment implements View.OnClickListener {

    Button btnSave;
    CheckBox accept;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_delete_account, container, false);

        /* get fields */
        btnSave = view.findViewById(R.id.btn_delete_account);
        accept = view.findViewById(R.id.checkBoxAccept);

        btnSave.setOnClickListener(this);
        accept.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.checkBoxAccept:

                /* change button */
                if (btnSave.isEnabled()) {

                    btnSave.setEnabled(false);
                    btnSave.setBackgroundColor(getResources().getColor(R.color.colorAccentDisable));
                } else {
                    btnSave.setEnabled(true);
                    btnSave.setBackgroundColor(getResources().getColor(R.color.colorGreenAccent));
                }

                break;
            case R.id.btn_delete_account:

                /* create AlertBox */
                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                alert.setTitle(getResources().getString(R.string.deleteAccountModalWindowTitle));
                alert.setMessage(getResources().getString(R.string.deleteAccountModalWindow));


                alert.setPositiveButton("Account löschen", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        /* get current user */
                        UserDAO userDAO = new UserDAO(MainActivity.getInstance());
                        User currentUser = userDAO.read(MainActivity.getActiveUser());

                        /* read profile values from global db */
                        HashMap<String, String> map = new HashMap<>();
                        map.put("id", "" + currentUser.getId());

                        Retrofit retrofit = APIConnector.getRetrofit();
                        APIClient apiInterface = retrofit.create(APIClient.class);

                        /* start a call */
                        String base = currentUser.getMail() + ":" + currentUser.getPassword();
                        String authString = "Basic " + Base64.encodeToString(base.getBytes(), Base64.NO_WRAP);
                        Call<ResponseBody> call = apiInterface.deleteUser(authString, map);

                        call.enqueue(new Callback<ResponseBody>() {

                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                try {

                                    if (response.code() == 401) {
                                        MainActivity.getInstance().showNotAuthorizedModal(3);
                                    } else {
                                        /* get jsonString from API */
                                        String jsonString = response.body().string();

                                        /* parse json */
                                        JSONObject mainObject = new JSONObject(jsonString);
                                        if (mainObject.getString("success").equals("0")) {
                                            MainActivity.getInstance().logout();
                                            if (MainActivity.getHints()) {
                                                Toast.makeText(getContext(), getResources().getString(R.string.deleteAccountSuccess), Toast.LENGTH_LONG).show();
                                            }
                                        }else if (mainObject.getString("success").equals("1")) {
                                            MainActivity.getInstance().logout();
                                            if (MainActivity.getHints()) {
                                                Toast.makeText(getContext(), getResources().getString(R.string.deleteAccountUnknownError), Toast.LENGTH_LONG).show();
                                            }
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
                                if (MainActivity.getHints()) {
                                    Toast.makeText(getContext(), getResources().getString(R.string.eDeleteAccountNoConnection), Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                });

                alert.setNegativeButton("Abruch", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
                alert.show();
                break;
        }
    }
}

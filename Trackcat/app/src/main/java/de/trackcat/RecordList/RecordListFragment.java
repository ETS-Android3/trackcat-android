package de.trackcat.RecordList;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import de.trackcat.APIClient;
import de.trackcat.APIConnector;
import de.trackcat.Database.DAO.LocationTempDAO;
import de.trackcat.Database.DAO.RecordTempDAO;
import de.trackcat.Database.DAO.RouteDAO;
import de.trackcat.Database.DAO.UserDAO;
import de.trackcat.Database.Models.Route;
import de.trackcat.Database.Models.User;
import de.trackcat.MainActivity;
import de.trackcat.R;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class RecordListFragment extends Fragment {

    private RecyclerView recyclerView;
    private List<Route> records;
    private RecordListAdapter mAdapter;
    private LinearLayout mainLayout;

    private SwipeRefreshLayout swipeContainer;

    private boolean restore = false;
    private RouteDAO recordDAO;
    private RecordTempDAO recordTempDAO;
    private LocationTempDAO locationTempDAO;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_record_list, container, false);

        /* create daos */
        recordDAO = new RouteDAO(MainActivity.getInstance());
        recordTempDAO = new RecordTempDAO(MainActivity.getInstance());
        locationTempDAO = new LocationTempDAO(MainActivity.getInstance());

        /* get temp routes and add to routeList */
        records = recordDAO.readAll();
        List<Route> tempRecords = recordTempDAO.readAll();

        for (Route route : tempRecords) {
            records.add(route);
        }

        /* Elemente aus View holen und Adapter definieren */
        recyclerView = view.findViewById(R.id.recycler_view);
        mainLayout = view.findViewById(R.id.main_layout);
        mAdapter = new RecordListAdapter(this.getContext(), records);

        /* RecyclerView mit Inhalten aus Adapter füllen */
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext().getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this.getContext(), DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(mAdapter);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(mAdapter);

        /* set swipe view */
        setUpItemTouchHelper();

        /* Aktualisieren der Seite durch SwipeRefresh */
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mAdapter.clear();
                MainActivity.getInstance().synchronizeRecords(true);
                swipeContainer.setRefreshing(false);
            }
        });

        /* Farbschema SwipeRefresh festlegen */
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        return view;
    }

    /* function to restore item */
    private void restoreItem(Route deletedItem, int deletedIndex) {
        mAdapter.restoreItem(deletedItem, deletedIndex);
        recordDAO.create(deletedItem);
    }

    /* function  to swipe*/
    private void setUpItemTouchHelper() {
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {

                int position = viewHolder.getAdapterPosition();

                /* Entfernten Item-Namen zwischenspeichern */
                String name = records.get(position).getName();

                /* Speichern für Rückgängig machen sichern */
                final Route deletedItem = records.get(position);
                final int deletedIndex = position;

                /* Item aus Recycler View und Datenbank entfernen */
                mAdapter.removeItem(position);
                if (!deletedItem.isTemp()) {
                    recordDAO.delete(deletedItem);
                }

                /* Snackbar mit 'Rückgängig' Funnktion anzeigen */
                Snackbar snackbar = Snackbar.make(mainLayout, "Aufzeichnung \"" + name + "\" wurde entfernt!", Snackbar.LENGTH_LONG);
                TextView snackbarText = snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
                snackbarText.setTextColor(Color.WHITE);
                snackbarText.setTextColor(Color.WHITE);
                snackbar.setAction("Rückgängig", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        /* restore item */
                        if (!deletedItem.isTemp()) {
                            restoreItem(deletedItem, deletedIndex);
                        }
                        restore = true;
                    }
                });
                snackbar.setActionTextColor(Color.YELLOW);
                snackbar.show();
                snackbar.addCallback(new Snackbar.Callback() {

                    @Override
                    public void onDismissed(Snackbar snackbar, int event) {
                        /* check if route resotred or not */
                        if (!restore && !deletedItem.isTemp()) {
                            Retrofit retrofit = APIConnector.getRetrofit();
                            APIClient apiInterface = retrofit.create(APIClient.class);

                            /* start a call */
                            UserDAO userDAO = new UserDAO(MainActivity.getInstance());
                            User currentUser = userDAO.read(MainActivity.getActiveUser());
                            String base = currentUser.getMail() + ":" + currentUser.getPassword();
                            String authString = "Basic " + Base64.encodeToString(base.getBytes(), Base64.NO_WRAP);

                            /* get record id */
                            HashMap<String, String> map = new HashMap<>();
                            map.put("recordId", "" + deletedItem.getId());

                            Call<ResponseBody> call = apiInterface.deleteRecord(authString, map);

                            call.enqueue(new Callback<ResponseBody>() {

                                @Override
                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                                    /* get jsonString from API */
                                    String jsonString = null;

                                    try {
                                        jsonString = response.body().string();

                                        /* parse json */
                                        JSONObject mainObject = new JSONObject(jsonString);
                                        if (mainObject.getString("success").equals("0")) {
                                            Toast.makeText(MainActivity.getInstance(), "Aufnahme '" + deletedItem.getName() + "' erfolgreich gelöscht.",
                                                    Toast.LENGTH_LONG).show();
                                        } else {
                                            Toast.makeText(MainActivity.getInstance(), "Aufnahme '" + deletedItem.getName() + "' konnte nicht gelöscht werden.",
                                                    Toast.LENGTH_LONG).show();
                                            /* restore item */
                                            restoreItem(deletedItem, deletedIndex);
                                        }


                                    } catch (IOException e) {
                                        e.printStackTrace();
                                        /* restore item */
                                        Toast.makeText(MainActivity.getInstance(), "Aufnahme '" + deletedItem.getName() + "' konnte nicht gelöscht werden.",
                                                Toast.LENGTH_LONG).show();
                                        restoreItem(deletedItem, deletedIndex);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        /* restore item */
                                        Toast.makeText(MainActivity.getInstance(), "Aufnahme '" + deletedItem.getName() + "' konnte nicht gelöscht werden.",
                                                Toast.LENGTH_LONG).show();
                                        restoreItem(deletedItem, deletedIndex);

                                    }
                                }

                                @Override
                                public void onFailure(Call<ResponseBody> call, Throwable t) {
                                    call.cancel();
                                    Toast.makeText(MainActivity.getInstance(), "Aufnahme '" + deletedItem.getName() + "' konnte nicht gelöscht werden.\nBitte überprüfen Sie Ihre Internetverbindung!",
                                            Toast.LENGTH_LONG).show();
                                    /* restore item */
                                    restoreItem(deletedItem, deletedIndex);
                                }
                            });
                        } else if (deletedItem.isTemp()) {
                            recordTempDAO.delete(deletedItem);
                        }
                    }
                });
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

                View itemView = viewHolder.itemView;

                /* set icon and color */
                final GradientDrawable background = new GradientDrawable();
                background.setCornerRadius(15);
                background.setColor(Color.RED);
                Drawable icon = ContextCompat.getDrawable(getContext(), R.drawable.ic_delete);
                int backgroundCornerOffset = 20;

                int iconMargin = (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
                int iconTop = itemView.getTop() + (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
                int iconBottom = iconTop + icon.getIntrinsicHeight();

                /* swipe left */
                if (dX > 0) {
                    /* swipe right */
                } else if (dX < 0) {

                    /* set icon  and background position */
                    int iconLeft = itemView.getRight() - iconMargin - icon.getIntrinsicWidth();
                    int iconRight = itemView.getRight() - iconMargin;
                    icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);

                    background.setBounds(itemView.getRight() + ((int) dX) - backgroundCornerOffset,
                            itemView.getTop(), itemView.getRight(), itemView.getBottom());
                }

                /* draw elements */
                background.draw(c);
                icon.draw(c);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }
}

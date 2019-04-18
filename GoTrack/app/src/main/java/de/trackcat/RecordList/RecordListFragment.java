package de.trackcat.RecordList;

import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import de.trackcat.Database.DAO.RouteDAO;
import de.trackcat.Database.Models.Route;
import de.trackcat.InExport.Export;
import de.trackcat.MainActivity;
import de.trackcat.R;
import de.trackcat.RecordList.SwipeControll.RecordListAdapter;
import de.trackcat.RecordList.SwipeControll.SwipeControllerActions;
import de.trackcat.RecordList.SwipeControll.SwipeController;

public class RecordListFragment extends Fragment {

    private RecyclerView recyclerView;
    private List<Route> records;
    private RecordListAdapter mAdapter;
    private LinearLayout mainLayout;

    SwipeController swipeController = null;
    private SwipeRefreshLayout swipeContainer;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_record_list, container, false);

        /* Routen Aufzeichnungen von Datenbank abfragen */
        RouteDAO dao = new RouteDAO(MainActivity.getInstance());
        records = dao.readAll(MainActivity.getActiveUser());

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

        swipeController = new SwipeController(new SwipeControllerActions() {
            @Override
            public void onRemoveClick(int position) {
                /* Entfernten Item-Namen zwischenspeichern */
                String name = records.get(position).getName();

                /* Speichern für Rückgängig machen sichern */
                final Route deletedItem = records.get(position);
                final int deletedIndex = position;

                /* Item aus Recycler View und Datenbank entfernen */
                mAdapter.removeItem(position);
                RouteDAO dao = new RouteDAO(MainActivity.getInstance());
                dao.delete(deletedItem);

                /* Snackbar mit 'Rückgängig' Funnktion anzeigen */
                Snackbar snackbar = Snackbar.make(mainLayout, "Aufzeichnung \"" + name + "\" wurde entfernt!", Snackbar.LENGTH_LONG);
                TextView snackbarText = snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
                snackbarText.setTextColor(Color.WHITE);
                snackbar.setAction("Rückgängig", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        /* Gelöschtes Item wiederherstellen */
                        mAdapter.restoreItem(deletedItem, deletedIndex);
                        dao.create(deletedItem);
                    }
                });
                snackbar.setActionTextColor(Color.YELLOW);
                snackbar.show();
            }

            /* Versenden einer Route */
            @Override
            public void onShareClick(int position) {
                String fileName = Export.getExport().exportRoute(getContext(),
                        records.get(position).getId(), true);
                Log.i("GoTrack-Export", "Die Datei: " + fileName +
                        " wurde erstellt.");
            }
        });

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeController);
        itemTouchhelper.attachToRecyclerView(recyclerView);

        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
                swipeController.onDraw(c);
            }
        });


        /* Aktualisieren der Seite durch SwipeRefresh */
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mAdapter.clear();
                MainActivity.getInstance().loadRecordList();
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
}

package de.mobcom.group3.gotrack.RecordList;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.List;
import de.mobcom.group3.gotrack.Database.DAO.RouteDAO;
import de.mobcom.group3.gotrack.Database.Models.Route;
import de.mobcom.group3.gotrack.MainActivity;
import de.mobcom.group3.gotrack.R;
import de.mobcom.group3.gotrack.RecordList.RecyclerView.RecordListAdapter;
import de.mobcom.group3.gotrack.RecordList.RecyclerView.RecyclerItemTouchHelper;

public class RecordListFragment extends Fragment implements RecyclerItemTouchHelper.RecyclerItemTouchHelperListener {

    private RecyclerView recyclerView;
    private List<Route> records;
    private RecordListAdapter mAdapter;
    private LinearLayout mainLayout;

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

        /* EventListener definieren */
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);

        return view;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof RecordListAdapter.MyViewHolder) {
            /* Entfernten Item-Namen zwischenspeichern */
            String name = records.get(viewHolder.getAdapterPosition()).getName();

            /* Speichern für Rückgängig machen sichern */
            final Route deletedItem = records.get(viewHolder.getAdapterPosition());
            final int deletedIndex = viewHolder.getAdapterPosition();

            /* Item aus Recycler View und Datenbank entfernen */
            mAdapter.removeItem(viewHolder.getAdapterPosition());
            RouteDAO dao = new RouteDAO(MainActivity.getInstance());
            dao.delete(deletedItem);

            /* Snackbar mit 'Rückgängig' Funbktion anzeigen */
            Snackbar snackbar = Snackbar.make(mainLayout, "Aufzeichnung \"" + name + "\" wurde entfernt!", Snackbar.LENGTH_LONG);
            TextView snackbarText = snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
            snackbarText.setTextColor(Color.WHITE);
            snackbar.setAction("Rückgängig", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    /* Gelöschtes Item wiederherstellen */
                    mAdapter.restoreItem(deletedItem, deletedIndex);
                    // TODO: Route sollte wieder an vorher genutzem Index eingefügt werden können
                    dao.create(deletedItem);
                }
            });
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();
        }
    }
}

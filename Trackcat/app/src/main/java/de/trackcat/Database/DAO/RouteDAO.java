package de.trackcat.Database.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.google.gson.Gson;

import de.trackcat.Database.Models.Route;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static de.trackcat.Database.DAO.DbContract.RouteEntry.*;

/**
 * Data access object for routes.
 * Alters database content via CRUD methods.
 * Creates im- and exportable routes as JSON.
 */
public class RouteDAO {
    /*
     + attribute to store activity context global
     + the context is needed by the DbHelper and will be handed over by creating an instance
     */
    private final Context context;

    private Gson gson = new Gson();
    private Type exImportType = Route.class;

    /**
     * Constructor to create instance of data access object.
     *
     * @param context of type context from calling activity
     */
    public RouteDAO(Context context) {
        this.context = context;
    }

    /**
     * Inserts a new route into the database.
     *
     * @param route of type route to be stored in the database
     *
     *              <p>
     *              Sets the database id to the model.
     *              </p>
     */
    public void create(Route route) {
        DbHelper dbHelper = new DbHelper(context);
        try {
            dbHelper.getWritableDatabase().insert(TABLE_NAME, null,
                    valueGenerator(route));
        } finally {
            dbHelper.close();
        }
    }

    /**
     * Method to generate content values.
     *
     * @param route of type route
     * @return content values to be inserted into database
     *
     * <p>
     * Maps the attributes of the route model to content values based on columns
     * where they have to be inserted. This type of prepared statement should prevent SQL
     * injections.
     * </p>
     */
    private ContentValues valueGenerator(Route route) {
        ContentValues values = new ContentValues();
        if (route.getId() != 0 && this.read(route.getId()).getId() == 0) {
            values.put(COL_ID, route.getId());
        }
        values.put(COL_NAME, route.getName());
        values.put(COL_TIME, route.getTime());
        values.put(COL_DATE, route.getDate());
        values.put(COL_TYPE, route.getType());
        values.put(COL_RIDETIME, route.getRideTime());
        values.put(COL_DISTANCE, route.getDistance());
        values.put(COL_TIMESTAMP, route.getTimeStamp());
        values.put(COL_ISTEMP, route.isTempDB());
        values.put(COL_LOCATIONS, route.getLocations());

        return values;
    }

    /**
     * Reads a specific route from database, which has matching id.
     *
     * @param id of type integer of which route has to be selected
     * @return route from database, if matching id was found else an empty route object
     */
    public Route read(int id) {
        Route result = new Route();
        DbHelper dbHelper = new DbHelper(context);
        try {
            String selection = COL_ID + " = ?";
            String[] selectionArgs = {String.valueOf(id)};
            String[] projection = {
                    COL_ID,
                    COL_NAME,
                    COL_TIME,
                    COL_DATE,
                    COL_TYPE,
                    COL_RIDETIME,
                    COL_DISTANCE,
                    COL_TIMESTAMP,
                    COL_ISTEMP,
                    COL_LOCATIONS};
            try (Cursor cursor = dbHelper.getReadableDatabase().query(
                    TABLE_NAME,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    null)) {
                if (cursor.moveToFirst()) {
                    result.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)));
                    result.setName(cursor.getString(cursor.getColumnIndexOrThrow(COL_NAME)));
                    result.setTime(cursor.getLong(cursor.getColumnIndexOrThrow(COL_TIME)));
                    result.setDate(cursor.getLong(cursor.getColumnIndexOrThrow(COL_DATE)));
                    result.setType(cursor.getInt(cursor.getColumnIndexOrThrow(COL_TYPE)));
                    result.setRideTime(cursor.getLong(cursor.getColumnIndexOrThrow(COL_RIDETIME)));
                    result.setTimeStamp(cursor.getLong(cursor.getColumnIndexOrThrow(COL_TIMESTAMP)));
                    result.setDistance(cursor.getDouble(cursor.getColumnIndexOrThrow(COL_DISTANCE)));
                    result.setTempDB(cursor.getInt(cursor.getColumnIndexOrThrow(COL_ISTEMP)));
                    result.setLocations(cursor.getString(cursor.getColumnIndexOrThrow(COL_LOCATIONS)));
                }
            }
        } finally {
            dbHelper.close();
        }
        return result;
    }

    /**
     * Reads all routes of a specific user.
     *
     * @return List of all routes belong to specific user in database sorted ascending after id,
     * if routes with matching userId was found else an empty List
     */
    public List<Route> readAll() {
        return this.readAll(new String[]{"id", "DESC"});
    }

    /**
     * Reads all routes of a specific user.
     *
     * @param orderArgs String[] { column to sort, ASC / DESC }
     *                  use COL_ID, COL_NAME, COL_TIME or COL_DISTANCE etc from DbContract
     *                  as columns and ASC for ascending or DESC for descending order
     * @return List of all routes belong to specific user in database sorted after
     * orderArgs, if routes with matching userId was found else an empty List
     */
    private List<Route> readAll(String[] orderArgs) {
        DbHelper dbHelper = new DbHelper(context);
        ArrayList<Route> result = new ArrayList<>();
        try {

            String[] projection = {
                    COL_ID,
                    COL_NAME,
                    COL_TIME,
                    COL_DATE,
                    COL_TYPE,
                    COL_RIDETIME,
                    COL_DISTANCE,
                    COL_TIMESTAMP,
                    COL_ISTEMP,
                    COL_LOCATIONS};
            try (Cursor cursor = dbHelper.getReadableDatabase().query(
                    TABLE_NAME,
                    projection,
                    null,
                    null,
                    null,
                    null,
                    orderArgs[0] + " " + orderArgs[1])) {
                if (cursor.moveToFirst())
                    do {
                        result.add(new Route(
                                cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)),
                                cursor.getString(cursor.getColumnIndexOrThrow(COL_NAME)),
                                cursor.getLong(cursor.getColumnIndexOrThrow(COL_TIME)),
                                cursor.getLong(cursor.getColumnIndexOrThrow(COL_RIDETIME)),
                                cursor.getDouble(cursor.getColumnIndexOrThrow(COL_DISTANCE)),
                                cursor.getInt(cursor.getColumnIndexOrThrow(COL_TYPE)),
                                cursor.getLong(cursor.getColumnIndexOrThrow(COL_DATE)),
                                cursor.getLong(cursor.getColumnIndexOrThrow(COL_TIMESTAMP)),
                                cursor.getInt(cursor.getColumnIndexOrThrow(COL_ISTEMP)),
                                cursor.getString(cursor.getColumnIndexOrThrow(COL_LOCATIONS))));

                    } while (cursor.moveToNext());
            }
        } finally {
            dbHelper.close();
        }
        return result;
    }

    /**
     * Reads all routes of a specific user which are recorded within the last seven days.
     *
     * @return List of all routes belong to specific user in database sorted descending after
     * date, if routes with matching userId was found else an empty List
     */
    public List<Route> readLastSevenDays() {
        DbHelper dbHelper = new DbHelper(context);
        List<Route> result = new ArrayList<>();
        try {
            String[] projection = {
                    COL_ID,
                    COL_NAME,
                    COL_TIME,
                    COL_DATE,
                    COL_TYPE,
                    COL_RIDETIME,
                    COL_DISTANCE,
                    COL_TIMESTAMP,
                    COL_ISTEMP,
                    COL_LOCATIONS};
            long sevenDaysInMillis = 604800000;
            String having = COL_DATE + " >= " + (System.currentTimeMillis() - sevenDaysInMillis);
            try (Cursor cursor = dbHelper.getWritableDatabase().query(
                    TABLE_NAME,
                    projection,
                    null,
                    null,
                    COL_DATE,
                    having,
                    COL_DATE + " DESC")) {
                if (cursor.moveToFirst())
                    do {

                            result.add(new Route(
                                    cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)),
                                    cursor.getString(cursor.getColumnIndexOrThrow(COL_NAME)),
                                    cursor.getLong(cursor.getColumnIndexOrThrow(COL_TIME)),
                                    cursor.getLong(cursor.getColumnIndexOrThrow(COL_RIDETIME)),
                                    cursor.getDouble(cursor.getColumnIndexOrThrow(COL_DISTANCE)),
                                    cursor.getInt(cursor.getColumnIndexOrThrow(COL_TYPE)),
                                    cursor.getLong(cursor.getColumnIndexOrThrow(COL_DATE)),
                                    cursor.getLong(cursor.getColumnIndexOrThrow(COL_TIMESTAMP)),
                                    cursor.getInt(cursor.getColumnIndexOrThrow(COL_ISTEMP)),
                                    cursor.getString(cursor.getColumnIndexOrThrow(COL_LOCATIONS))));


                    } while (cursor.moveToNext());
            }
        } finally {
            dbHelper.close();
        }
        return result;
    }

    /**
     * Updates a specific route in database with handed over route, which has matching id.
     *
     * @param id    of type integer of which route has to be updated
     * @param route of type route which would override route with defined id in database
     */
    public void update(int id, Route route) {
        DbHelper dbHelper = new DbHelper(context);
        String selection = COL_ID + " = ?";
        String[] selectionArgs = {String.valueOf(id)};
        try {
            dbHelper.getWritableDatabase().update(TABLE_NAME, valueGenerator(route), selection, selectionArgs);
        } finally {
            dbHelper.close();
        }
    }

    /**
     * Deletes a specific route from database, which has matching id.
     *
     * @param id of type integer of which route has to be deleted
     */
    void delete(int id) {
        DbHelper dbHelper = new DbHelper(context);
        String selection = COL_ID + " LIKE ?";
        String[] selectionArgs = {String.valueOf(id)};
        try {
            dbHelper.getWritableDatabase().delete(TABLE_NAME, selection, selectionArgs);
        } finally {
            dbHelper.close();
        }
    }

    /**
     * Deletes a specific route from database, which has matching id.
     *
     * @param route of type route which has to be deleted
     */
    public void delete(Route route) {
        this.delete(route.getId());
    }
}
package de.trackcat.Database.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import de.trackcat.CustomElements.CustomLocation;
import de.trackcat.Database.Models.Location;
import de.trackcat.Database.Models.Route;

import static de.trackcat.Database.DAO.DbContract.RecordTempEntry.COL_DATE;
import static de.trackcat.Database.DAO.DbContract.RecordTempEntry.COL_DISTANCE;
import static de.trackcat.Database.DAO.DbContract.RecordTempEntry.COL_ID;
import static de.trackcat.Database.DAO.DbContract.RecordTempEntry.COL_ISIMPORTED;
import static de.trackcat.Database.DAO.DbContract.RecordTempEntry.COL_ISTEMP;
import static de.trackcat.Database.DAO.DbContract.RecordTempEntry.COL_LOCATIONS;
import static de.trackcat.Database.DAO.DbContract.RecordTempEntry.COL_NAME;
import static de.trackcat.Database.DAO.DbContract.RecordTempEntry.COL_RIDETIME;
import static de.trackcat.Database.DAO.DbContract.RecordTempEntry.COL_TIME;
import static de.trackcat.Database.DAO.DbContract.RecordTempEntry.COL_TIMESTAMP;
import static de.trackcat.Database.DAO.DbContract.RecordTempEntry.COL_TYPE;
import static de.trackcat.Database.DAO.DbContract.RecordTempEntry.COL_USER;
import static de.trackcat.Database.DAO.DbContract.RecordTempEntry.TABLE_NAME;

/**
 * Data access object for routes.
 * Alters database content via CRUD methods.
 * Creates im- and exportable routes as JSON.
 */
public class RecordTempDAO {
    /*
     + attribute to store activity context global
     + the context is needed by the DbHelper and will be handed over by creating an instance
     */
    private final Context context;

    private Gson gson = new Gson();
    private Type listType = new TypeToken<ArrayList<CustomLocation>>() {
    }.getType();
    private Type exImportType = Route.class;

    /**
     * Constructor to create instance of data access object.
     *
     * @param context of type context from calling activity
     */
    public RecordTempDAO(Context context) {
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
    public int create(Route route) {
        DbHelper dbHelper = new DbHelper(context);
        long id = 0;
        try {
            //  route.setId((int) dbHelper.getWritableDatabase().insert(TABLE_NAME, null, valueGenerator(route)));
            id = dbHelper.getWritableDatabase().insert(TABLE_NAME, null, valueGenerator(route));
        } finally {
            dbHelper.close();
        }
        return (int) id;
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
        values.put(COL_USER, route.getUserId());
        values.put(COL_NAME, route.getName());
        values.put(COL_TIME, route.getTime());
        values.put(COL_DATE, route.getDate());
        values.put(COL_TYPE, route.getType());
        values.put(COL_RIDETIME, route.getRideTime());
        values.put(COL_DISTANCE, route.getDistance());
        values.put(COL_TIMESTAMP, route.getTimeStamp());
        values.put(COL_ISIMPORTED, route.isImportedDB());
        values.put(COL_ISTEMP, route.isTempDB());

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
                    COL_USER,
                    COL_NAME,
                    COL_TIME,
                    COL_DATE,
                    COL_TYPE,
                    COL_RIDETIME,
                    COL_DISTANCE,
                    COL_TIMESTAMP,
                    COL_ISIMPORTED,
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
                    result.setUserID(cursor.getInt(cursor.getColumnIndexOrThrow(COL_USER)));
                    result.setName(cursor.getString(cursor.getColumnIndexOrThrow(COL_NAME)));
                    result.setTime(cursor.getLong(cursor.getColumnIndexOrThrow(COL_TIME)));
                    result.setDate(cursor.getLong(cursor.getColumnIndexOrThrow(COL_DATE)));
                    result.setType(cursor.getInt(cursor.getColumnIndexOrThrow(COL_TYPE)));
                    result.setRideTime(cursor.getLong(cursor.getColumnIndexOrThrow(COL_RIDETIME)));
                    result.setTimeStamp(cursor.getLong(cursor.getColumnIndexOrThrow(COL_TIMESTAMP)));
                    result.setDistance(cursor.getDouble(cursor.getColumnIndexOrThrow(COL_DISTANCE)));
                    result.setImportedDB(cursor.getInt(cursor.getColumnIndexOrThrow(COL_ISIMPORTED)));
                    result.setTempDB(cursor.getInt(cursor.getColumnIndexOrThrow(COL_ISTEMP)));
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
                    COL_USER,
                    COL_NAME,
                    COL_TIME,
                    COL_DATE,
                    COL_TYPE,
                    COL_RIDETIME,
                    COL_DISTANCE,
                    COL_TIMESTAMP,
                    COL_ISIMPORTED,
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
                                cursor.getInt(cursor.getColumnIndexOrThrow(COL_USER)),
                                cursor.getString(cursor.getColumnIndexOrThrow(COL_NAME)),
                                cursor.getLong(cursor.getColumnIndexOrThrow(COL_TIME)),
                                cursor.getLong(cursor.getColumnIndexOrThrow(COL_RIDETIME)),
                                cursor.getDouble(cursor.getColumnIndexOrThrow(COL_DISTANCE)),
                                cursor.getInt(cursor.getColumnIndexOrThrow(COL_TYPE)),
                                cursor.getLong(cursor.getColumnIndexOrThrow(COL_DATE)),
                                cursor.getLong(cursor.getColumnIndexOrThrow(COL_TIMESTAMP)),
                                cursor.getInt(cursor.getColumnIndexOrThrow(COL_ISIMPORTED)),
                                cursor.getInt(cursor.getColumnIndexOrThrow(COL_ISTEMP))));

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
     * @param userId id of specific user of whom routes has to be selected
     * @return List of all routes belong to specific user in database sorted descending after
     * date, if routes with matching userId was found else an empty List
     */
    public List<Route> readLastSevenDays(int userId) {
        DbHelper dbHelper = new DbHelper(context);
        List<Route> result = new ArrayList<>();
        try {
            String selection = COL_USER + " = ?";
            String[] selectionArgs = {String.valueOf(userId)};
            String[] projection = {
                    COL_ID,
                    COL_USER,
                    COL_NAME,
                    COL_TIME,
                    COL_DATE,
                    COL_TYPE,
                    COL_RIDETIME,
                    COL_DISTANCE,
                    COL_TIMESTAMP,
                    COL_ISIMPORTED,
                    COL_ISTEMP,
                    COL_LOCATIONS};
            long sevenDaysInMillis = 604800000;
            String having = COL_DATE + " >= " + (System.currentTimeMillis() - sevenDaysInMillis) +
                    " AND " + COL_ISIMPORTED + " == 0";
            try (Cursor cursor = dbHelper.getWritableDatabase().query(
                    TABLE_NAME,
                    projection,
                    selection,
                    selectionArgs,
                    COL_DATE,
                    having,
                    COL_DATE + " DESC")) {
                if (cursor.moveToFirst())
                    do {
                        if (cursor.getInt(cursor.getColumnIndexOrThrow(COL_ISIMPORTED)) == 0) {
                            result.add(new Route(
                                    cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)),
                                    cursor.getInt(cursor.getColumnIndexOrThrow(COL_USER)),
                                    cursor.getString(cursor.getColumnIndexOrThrow(COL_NAME)),
                                    cursor.getLong(cursor.getColumnIndexOrThrow(COL_TIME)),
                                    cursor.getLong(cursor.getColumnIndexOrThrow(COL_RIDETIME)),
                                    cursor.getDouble(cursor.getColumnIndexOrThrow(COL_DISTANCE)),
                                    cursor.getInt(cursor.getColumnIndexOrThrow(COL_TYPE)),
                                    cursor.getLong(cursor.getColumnIndexOrThrow(COL_DATE)),
                                    cursor.getLong(cursor.getColumnIndexOrThrow(COL_TIMESTAMP)),
                                    cursor.getInt(cursor.getColumnIndexOrThrow(COL_ISIMPORTED)),
                                    cursor.getInt(cursor.getColumnIndexOrThrow(COL_ISTEMP))));
                        }
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
        LocationTempDAO locationDAO = new LocationTempDAO(context);
        try {
            /*delete locations*/
            for (Location location : locationDAO.readAll(id)) {
                locationDAO.delete(location.getId());
            }
            String selection = COL_ID + " LIKE ?";
            String[] selectionArgs = {String.valueOf(id)};
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

    /**
     * Deletes all not finished records.
     */
    public void deleteAllNotFinished() {

        LocationTempDAO locationDAO = new LocationTempDAO(context);
        ArrayList<Integer> result = new ArrayList<>();
        DbHelper dbHelper = new DbHelper(context);
        try {
            String selection = COL_DATE + " LIKE 0";

            String[] projection = {
                    COL_ID};
            try (Cursor cursor = dbHelper.getReadableDatabase().query(
                    TABLE_NAME,
                    projection,
                    selection,
                    null,
                    null,
                    null,
                    null)) {
                if (cursor.moveToFirst())
                    do {
                        result.add(cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)));
                    } while (cursor.moveToNext());

            }
            int id;
            Log.d("GESCHLOSSEN"," zu löschende Routen: "+result.size());

            for (int i = 0; i < result.size(); i++) {
                id = result.get(i);

                /*delete locations*/
                for (Location location : locationDAO.readAll(id)) {
                    locationDAO.delete(location.getId());
                }
                selection = COL_ID + " LIKE ?";
                String[] selectionArgs = {String.valueOf(id)};
                dbHelper.getWritableDatabase().delete(TABLE_NAME, selection, selectionArgs);
            }
        } finally {
            dbHelper.close();

            Log.d("GESCHLOSSEN"," DB GESCHLOSSEN");
        }

    }
}
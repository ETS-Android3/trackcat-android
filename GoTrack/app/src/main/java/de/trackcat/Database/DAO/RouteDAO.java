package de.trackcat.Database.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import de.trackcat.CustomElements.CustomLocation;
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
    private Type listType = new TypeToken<ArrayList<CustomLocation>>() {
    }.getType();
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
            route.setId((int) dbHelper.getWritableDatabase().insert(TABLE_NAME, null,
                    valueGenerator(route)));
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
        values.put(COL_USER, route.getUserId());
        values.put(COL_NAME, route.getName());
        values.put(COL_TIME, route.getTime());
        values.put(COL_DATE, route.getDate());
        values.put(COL_TYPE, route.getType());
        values.put(COL_RIDETIME, route.getRideTime());
        values.put(COL_DISTANCE, route.getDistance());
        values.put(COL_ISIMPORTED, route.isImportedDB());
        values.put(COL_LOCATIONS, gson.toJson(route.getLocations(), listType));

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
                    COL_ISIMPORTED,
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
                    result.setDistance(cursor.getDouble(cursor.getColumnIndexOrThrow(COL_DISTANCE)));
                    result.setImportedDB(cursor.getInt(cursor.getColumnIndexOrThrow(COL_ISIMPORTED)));
                    result.setLocations(gson.fromJson(cursor.getString(
                            cursor.getColumnIndexOrThrow(COL_LOCATIONS)), listType));
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
     * @param userId id of specific user of whom routes has to be selected
     * @return List of all routes belong to specific user in database sorted ascending after id,
     * if routes with matching userId was found else an empty List
     */
    public List<Route> readAll(int userId) {
        return this.readAll(userId, new String[]{"id", "ASC"});
    }

    /**
     * Reads all routes of a specific user.
     *
     * @param userId    id of specific user of whom routes has to be selected
     * @param orderArgs String[] { column to sort, ASC / DESC }
     *                  use COL_ID, COL_NAME, COL_TIME or COL_DISTANCE etc from DbContract
     *                  as columns and ASC for ascending or DESC for descending order
     * @return List of all routes belong to specific user in database sorted after
     * orderArgs, if routes with matching userId was found else an empty List
     */
    private List<Route> readAll(int userId, String[] orderArgs) {
        DbHelper dbHelper = new DbHelper(context);
        List<Route> result = new ArrayList<>();
        String selection = COL_USER + " = ?";
        try {
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
                    COL_ISIMPORTED,
                    COL_LOCATIONS};
            try (Cursor cursor = dbHelper.getReadableDatabase().query(
                    TABLE_NAME,
                    projection,
                    selection,
                    selectionArgs,
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
                                cursor.getInt(cursor.getColumnIndexOrThrow(COL_ISIMPORTED)),
                                gson.fromJson(cursor.getString(
                                        cursor.getColumnIndexOrThrow(COL_LOCATIONS)), listType)));
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
                    COL_ISIMPORTED,
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
                                    cursor.getInt(cursor.getColumnIndexOrThrow(COL_ISIMPORTED)),
                                    gson.fromJson(cursor.getString(
                                            cursor.getColumnIndexOrThrow(COL_LOCATIONS)), listType)));
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

    /**
     * Imports a single route from handed over JSON.
     *
     * @param jsonString of type string which defines the route to be imported
     * @param userId     of type integer to define the user to whom the route would be associated
     * @param isImported of type boolean to define if the route is a restore from a backup
     *                   (case false) or if it is an imported route received by an other user
     *                   (case true)
     *
     *                   <p>
     *                   Creates a route with the attributes which were defined in JSON
     *                   </p>
     */
    public void importRouteFromJson(String jsonString, int userId, boolean isImported) {
        Route route = gson.fromJson(jsonString, exImportType);
        if (!route.isImported()) {
            route.setImported(isImported);
        }
        route.setUserID(userId);
        this.create(route);
    }

    /**
     * Imports all routes from handed over JSON List.
     *
     * @param jsonStrings of type List<String> which inherits the routes to be imported
     * @param userId      of type integer to define the user to whom the route would be associated
     * @param isImported  of type boolean to define if the route is a restore from a backup
     *                    (case false) or if it is an imported route received by an other user
     *                    (case true)
     *
     *                    <p>
     *                    Creates a route for each entry with the attributes which were defined in JSON
     *                    </p>
     */
    public void importRoutesFromJson(List<String> jsonStrings, int userId, boolean isImported) {
        for (String jsonString : jsonStrings) {
            this.importRouteFromJson(jsonString, userId, isImported);
        }
    }

    /**
     * Creates a JSON string which defines a route object and its attributes.
     *
     * @param id of type integer of which route has to be exported
     * @return a JSON string
     */
    public String exportRouteToJson(int id) {
        return gson.toJson(this.read(id), exImportType);
    }

    /**
     * Creates a List of JSON strings which defines all route objects and its attributes
     * of a specific user.
     *
     * @param userId of type integer of which user routes has to be exported
     * @return a List of JSON strings
     */
    public List<String> exportRoutesToJson(int userId) {
        List<String> result = new ArrayList<>();
        for (Route route : readAll(userId)) {
            result.add(exportRouteToJson(route.getId()));
        }
        return result;
    }
}
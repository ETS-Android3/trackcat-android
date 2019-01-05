package de.mobcom.group3.gotrack.Database.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.location.Location;
import android.os.Parcel;

import com.google.gson.Gson;

import de.mobcom.group3.gotrack.Database.Models.Route;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static android.os.Parcel.obtain;
import static de.mobcom.group3.gotrack.Database.DAO.DbContract.RouteEntry.*;

// toDo: write javaDoc and comments

/*      Parcel ArrayList<Locations> instead of using GSON

        write into a byte[] parceled:

        final Parcel p1 = obtain();
        p1.writeTypedList(route.getLocations());
        final byte[] parceled = p1.marshall();
        p1.recycle();

        read from a byte[] parceled and write into an ArrayList<Location> unParceled:

        final Parcel p2 = obtain();
        p2.unmarshall(parceled, 0, parceled.length);
        p2.setDataPosition(0);
        ArrayList unParceled = p2.createTypedArrayList(Location.CREATOR);
 */

/* public String[] getInfo(int id) {
    String[] result = new String[]{};
    String selection = COL_ID + " = ?";
    String[] selectionArgs = { String.valueOf(id) };
    String[] projection = { COL_DATE, COL_TIME, COL_RIDETIME, COL_DISTANCE };
    Cursor cursor = writableDb.query(
            TABLE_NAME,
            projection,
            selection,
            selectionArgs,
            null,
            null,
            null
    );
    if(cursor.moveToFirst()) {
        result[0] = String.valueOf(cursor.getLong(cursor.getColumnIndexOrThrow(COL_DATE)));
        result[1] = String.valueOf(cursor.getLong(cursor.getColumnIndexOrThrow(COL_TIME)));
        result[2] = String.valueOf(cursor.getLong(cursor.getColumnIndexOrThrow(COL_RIDETIME)));
        result[3] = String.valueOf(cursor.getDouble(cursor.getColumnIndexOrThrow(COL_DISTANCE)));
    }
    return result;
}*/


public class RouteDAO {
    private final Context context;

    public RouteDAO(Context context) {
        this.context = context;
    }

    public void create(Route route) {
        DbHelper dbHelper = new DbHelper(context);
        try {
            route.setId((int) dbHelper.getWritableDatabase().insert(TABLE_NAME, null,
                    valueGenerator(route)));
        } finally {
            dbHelper.close();
        }
    }

    private ContentValues valueGenerator(Route route) {
        final Parcel parcel = obtain();
        parcel.writeTypedList(route.getLocations());
        final byte[] parceled = parcel.marshall();

        ContentValues values = new ContentValues();
        values.put(COL_USER, route.getUserId());
        values.put(COL_NAME, route.getName());
        values.put(COL_TIME, route.getTime());
        values.put(COL_DATE, route.getDate());
        values.put(COL_TYPE, route.getType());
        values.put(COL_RIDETIME, route.getRideTime());
        values.put(COL_DISTANCE, route.getDistance());
        values.put(COL_ISIMPORTED, route.isImportedDB());
        values.put(COL_LOCATIONS, parceled);

        parcel.recycle();

        return values;
    }

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
                    COL_LOCATIONS };
            try (Cursor cursor = dbHelper.getReadableDatabase().query(
                    TABLE_NAME,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    null )) {
                if (cursor.moveToFirst()) {
                    final Parcel parcel = obtain();
                    result.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)));
                    result.setUserID(cursor.getInt(cursor.getColumnIndexOrThrow(COL_USER)));
                    result.setName(cursor.getString(cursor.getColumnIndexOrThrow(COL_NAME)));
                    result.setTime(cursor.getLong(cursor.getColumnIndexOrThrow(COL_TIME)));
                    result.setDate(cursor.getLong(cursor.getColumnIndexOrThrow(COL_DATE)));
                    result.setType(cursor.getInt(cursor.getColumnIndexOrThrow(COL_TYPE)));
                    result.setRideTime(cursor.getLong(cursor.getColumnIndexOrThrow(COL_RIDETIME)));
                    result.setDistance(cursor.getDouble(cursor.getColumnIndexOrThrow(COL_DISTANCE)));
                    result.setImportedDB(cursor.getInt(cursor.getColumnIndexOrThrow(COL_ISIMPORTED)));

                    final byte[] parceled = cursor.getBlob(cursor.getColumnIndexOrThrow(COL_LOCATIONS));
                    parcel.unmarshall(parceled, 0, parceled.length);
                    parcel.setDataPosition(0);

                    result.setLocations(parcel.createTypedArrayList(Location.CREATOR));

                    parcel.recycle();
                }
            }
        } finally {
            dbHelper.close();
        }
        return result;
    }

    /**
     * @param userId id of specific user of whom routes have to be selected
     * @return List of all routes belong to specific user in database sorted descending after id
     */
    public List<Route> readAll(int userId) {
        return this.readAll(userId, new String[]{"id", "ASC"});
    }

    /**
     * @param userId    id of specific user of whom routes have to be selected
     * @param orderArgs String[] { column to sort, ASC / DESC }
     *                  use COL_ID, COL_NAME, COL_TIME or COL_DISTANCE as columns
     * @return List of all users in database
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
                    COL_LOCATIONS };
            try (Cursor cursor = dbHelper.getReadableDatabase().query(
                    TABLE_NAME,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    orderArgs[0] + " " + orderArgs[1] )) {
                if (cursor.moveToFirst())
                    do {
                        final Parcel parcel = obtain();
                        final byte[] parceled = cursor.getBlob(cursor.getColumnIndexOrThrow(COL_LOCATIONS));
                        parcel.unmarshall(parceled, 0, parceled.length);
                        parcel.setDataPosition(0);

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
                                parcel.createTypedArrayList(Location.CREATOR)));

                        parcel.recycle();
                    } while (cursor.moveToNext());
            }
        } finally {
            dbHelper.close();
        }
        return result;
    }

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
                    COL_LOCATIONS };
            long sevenDaysInMillis = 604800000;
            String having = COL_DATE + " >= " + (System.currentTimeMillis() - sevenDaysInMillis);
            try (Cursor cursor = dbHelper.getWritableDatabase().query(
                    TABLE_NAME,
                    projection,
                    selection,
                    selectionArgs,
                    COL_DATE,
                    having,
                    "id  ASC" )) {
                if (cursor.moveToFirst())
                    do {
                        final Parcel parcel = obtain();
                        final byte[] parceled = cursor.getBlob(cursor.getColumnIndexOrThrow(COL_LOCATIONS));
                        parcel.unmarshall(parceled, 0, parceled.length);
                        parcel.setDataPosition(0);

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
                            parcel.createTypedArrayList(Location.CREATOR)));
                        parcel.recycle();
                    } while (cursor.moveToNext());
            }
        } finally {
            dbHelper.close();
        }
        return result;
    }

    public void update(int id, Route route) {
        DbHelper dbHelper = new DbHelper(context);
        String selection = COL_ID + " = ?";
        String[] selectionArgs = { String.valueOf(route.getUserId()) };
        try {
            dbHelper.getWritableDatabase().update(TABLE_NAME, valueGenerator(route), selection, selectionArgs);
        } finally {
            dbHelper.close();
        }
    }

    public void delete(int id) {
        DbHelper dbHelper = new DbHelper(context);
        String selection = COL_ID + " LIKE ?";
        String[] selectionArgs = { String.valueOf(id) };
        try {
            dbHelper.getWritableDatabase().delete(TABLE_NAME, selection, selectionArgs);
        } finally {
            dbHelper.close();
        }
    }

    public void delete(Route route) {
        this.delete(route.getId());
    }

    public void importRouteFromJSON(String jsonString) {
        Gson gson = new Gson();
        byte[] parceled = gson.fromJson(jsonString, byte[].class);
        final Parcel p2 = obtain();

        p2.unmarshall(parceled, 0, parceled.length);
        p2.setDataPosition(0);
        Route route = p2.readParcelable(Route.class.getClassLoader());
        route.setImported(true);
        this.create(route);
        p2.recycle();
    }

    public void importRoutesFromJson(List<String> jsonStrings) {
        for (String jsonString : jsonStrings) {
            this.importRouteFromJSON(jsonString);
        }
    }

    public String exportRouteToJson(int id) {
        Gson gson = new Gson();
        final Parcel p1 = obtain();
        p1.writeParcelable(this.read(id), 0);
        final byte[] parceled = p1.marshall();
        p1.recycle();

        return gson.toJson(parceled, byte[].class);
    }

    public List<String> exportRoutesToJson(int userId) {
        List<String> result = new ArrayList<>();
        for (Route route : readAll(userId)) {
            result.add(exportRouteToJson(route.getId()));
        }
        return result;
    }
}
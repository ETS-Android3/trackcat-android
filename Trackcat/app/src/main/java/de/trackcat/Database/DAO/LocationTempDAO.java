package de.trackcat.Database.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import de.trackcat.Database.Models.Location;

import static de.trackcat.Database.DAO.DbContract.LocationTempEntry.COL_ALTITUDE;
import static de.trackcat.Database.DAO.DbContract.LocationTempEntry.COL_ID;
import static de.trackcat.Database.DAO.DbContract.LocationTempEntry.COL_LATITUDE;
import static de.trackcat.Database.DAO.DbContract.LocationTempEntry.COL_LONGITUDE;
import static de.trackcat.Database.DAO.DbContract.LocationTempEntry.COL_RECORD_ID;
import static de.trackcat.Database.DAO.DbContract.LocationTempEntry.COL_SPEED;
import static de.trackcat.Database.DAO.DbContract.LocationTempEntry.COL_TIME;
import static de.trackcat.Database.DAO.DbContract.LocationTempEntry.TABLE_NAME;

/**
 * Data access object for routes.
 * Alters database content via CRUD methods.
 * Creates im- and exportable routes as JSON.
 */
public class LocationTempDAO {
    /*
     + attribute to store activity context global
     + the context is needed by the DbHelper and will be handed over by creating an instance
     */
    private final Context context;


    /**
     * Constructor to create instance of data access object.
     *
     * @param context of type context from calling activity
     */
    public LocationTempDAO(Context context) {
        this.context = context;
    }

    /**
     * Inserts a new location into the database.
     *
     * @param location of type location to be stored in the database
     *
     *                 <p>
     *                 Sets the database id to the model.
     *                 </p>
     */
    public void create(Location location) {
        DbHelper dbHelper = new DbHelper(context);
        try {
            location.setId((int) dbHelper.getWritableDatabase().insert(TABLE_NAME, null,
                    valueGenerator(location)));
        } finally {
            dbHelper.close();
        }
    }

    /**
     * Method to generate content values.
     *
     * @param location of type location
     * @return content values to be inserted into database
     *
     * <p>
     * Maps the attributes of the route model to content values based on columns
     * where they have to be inserted. This type of prepared statement should prevent SQL
     * injections.
     * </p>
     */
    private ContentValues valueGenerator(Location location) {
        ContentValues values = new ContentValues();
        if (location.getId() != 0 && this.read(location.getId()).getId() == 0) {
            values.put(COL_ID, location.getId());
        }
        values.put(COL_RECORD_ID, location.getRecordId());
        values.put(COL_LATITUDE, location.getLatitude());
        values.put(COL_LONGITUDE, location.getLongitude());
        values.put(COL_ALTITUDE, location.getAltitude());
        values.put(COL_TIME, location.getTime());
        values.put(COL_SPEED, location.getSpeed());

        return values;
    }

    /**
     * Reads a specific location from database, which has matching id.
     *
     * @param id of type integer of which location has to be selected
     * @return location from database, if matching id was found else an empty route object
     */
    public Location read(int id) {
        Location result = new Location();
        DbHelper dbHelper = new DbHelper(context);
        try {
            String selection = COL_ID + " = ?";
            String[] selectionArgs = {String.valueOf(id)};
            String[] projection = {
                    COL_ID,
                    COL_RECORD_ID,
                    COL_LATITUDE,
                    COL_LONGITUDE,
                    COL_ALTITUDE,
                    COL_TIME,
                    COL_SPEED};
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
                    result.setRecordId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_RECORD_ID)));
                    result.setLatitude(cursor.getDouble(cursor.getColumnIndexOrThrow(COL_LATITUDE)));
                    result.setLongitude(cursor.getDouble(cursor.getColumnIndexOrThrow(COL_LONGITUDE)));
                    result.setAltitude(cursor.getDouble(cursor.getColumnIndexOrThrow(COL_ALTITUDE)));
                    result.setTime(cursor.getLong(cursor.getColumnIndexOrThrow(COL_TIME)));
                    result.setSpeed(cursor.getFloat(cursor.getColumnIndexOrThrow(COL_SPEED)));
                }
            }
        } finally {
            dbHelper.close();
        }
        return result;
    }

    /**
     * Reads all locations of a specific record.
     *
     * @param recordId id of specific record of whom location has to be selected
     * @return List of all locations belong to specific record in database sorted ascending after id,
     * if locations with matching recordId was found else an empty List
     */
    public List<Location> readAll(int recordId) {
        return this.readAll(recordId, new String[]{"id", "ASC"});
    }

    /**
     * Reads all locations of a specific record.
     *
     * @param recordId  id of specific record of whom locations has to be selected
     * @param orderArgs String[] { column to sort, ASC / DESC }
     *                  use COL_ID, COL_ALTITUDE, COL_TIME or COL_SPEED etc from DbContract
     *                  as columns and ASC for ascending or DESC for descending order
     * @return List of all locations belong to specific record in database sorted after
     * orderArgs, if locations with matching recordId was found else an empty List
     */
    private List<Location> readAll(int recordId, String[] orderArgs) {
        DbHelper dbHelper = new DbHelper(context);
        List<Location> result = new ArrayList<>();
        String selection = COL_RECORD_ID + " = ?";
        try {
            String[] selectionArgs = {String.valueOf(recordId)};
            String[] projection = {
                    COL_ID,
                    COL_RECORD_ID,
                    COL_LATITUDE,
                    COL_LONGITUDE,
                    COL_ALTITUDE,
                    COL_TIME,
                    COL_SPEED};
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
                        result.add(new Location(
                                cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)),
                                cursor.getInt(cursor.getColumnIndexOrThrow(COL_RECORD_ID)),
                                cursor.getDouble(cursor.getColumnIndexOrThrow(COL_LATITUDE)),
                                cursor.getDouble(cursor.getColumnIndexOrThrow(COL_LONGITUDE)),
                                cursor.getDouble(cursor.getColumnIndexOrThrow(COL_ALTITUDE)),
                                cursor.getLong(cursor.getColumnIndexOrThrow(COL_TIME)),
                                cursor.getFloat(cursor.getColumnIndexOrThrow(COL_SPEED))));

                    } while (cursor.moveToNext());
            }
        } finally {
            dbHelper.close();
        }
        return result;
    }

    /**
     * Reads last locations of a specific record.
     *
     * @param recordId id of specific record of whom location has to be selected
     * @param limit is the number of limit
     * @param round is the page to show
     * @return List of all locations belong to specific record in database sorted ascending after id,
     * if locations with matching recordId was found else an empty List
     */
    public List<Location> readAllWithLimit(int recordId, int limit, int round) {
        return this.readAllWithLimit(recordId, new String[]{"id", "ASC"}, limit, round);
    }

    /**
     * Reads all locations of a specific record.
     *
     * @param recordId  id of specific record of whom locations has to be selected
     * @param orderArgs String[] { column to sort, ASC / DESC }
     *                  use COL_ID, COL_ALTITUDE, COL_TIME or COL_SPEED etc from DbContract
     *                  as columns and ASC for ascending or DESC for descending order
     * @return List of all locations belong to specific record in database sorted after
     * orderArgs, if locations with matching recordId was found else an empty List
     */
    private List<Location> readAllWithLimit(int recordId, String[] orderArgs, int limit, int round) {
        DbHelper dbHelper = new DbHelper(context);
        List<Location> result = new ArrayList<>();
        String selection = COL_RECORD_ID + " = ?";
        try {
            String[] selectionArgs = {String.valueOf(recordId)};
            String[] projection = {
                    COL_ID,
                    COL_RECORD_ID,
                    COL_LATITUDE,
                    COL_LONGITUDE,
                    COL_ALTITUDE,
                    COL_TIME,
                    COL_SPEED};
            try (Cursor cursor = dbHelper.getReadableDatabase().query(
                    TABLE_NAME,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    orderArgs[0] + " " + orderArgs[1],
                    round + "," + limit)) {
                if (cursor.moveToFirst())
                    do {
                        result.add(new Location(
                                cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)),
                                cursor.getInt(cursor.getColumnIndexOrThrow(COL_RECORD_ID)),
                                cursor.getDouble(cursor.getColumnIndexOrThrow(COL_LATITUDE)),
                                cursor.getDouble(cursor.getColumnIndexOrThrow(COL_LONGITUDE)),
                                cursor.getDouble(cursor.getColumnIndexOrThrow(COL_ALTITUDE)),
                                cursor.getLong(cursor.getColumnIndexOrThrow(COL_TIME)),
                                cursor.getFloat(cursor.getColumnIndexOrThrow(COL_SPEED))));

                    } while (cursor.moveToNext());
            }
        } finally {
            dbHelper.close();
        }
        return result;
    }

    /**
     * Updates a specific location in database with handed over location, which has matching id.
     *
     * @param id       of type integer of which route has to be updated
     * @param location of type location which would override location with defined id in database
     */
    public void update(int id, Location location) {
        DbHelper dbHelper = new DbHelper(context);
        String selection = COL_ID + " = ?";
        String[] selectionArgs = {String.valueOf(id)};
        try {
            dbHelper.getWritableDatabase().update(TABLE_NAME, valueGenerator(location), selection, selectionArgs);
        } finally {
            dbHelper.close();
        }
    }

    /**
     * Deletes a specific location from database, which has matching id.
     *
     * @param id of type integer of which location has to be deleted
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
     * Deletes a specific location from database, which has matching id.
     *
     * @param location of type location which has to be deleted
     */
    public void delete(Location location) {
        this.delete(location.getId());
    }

}
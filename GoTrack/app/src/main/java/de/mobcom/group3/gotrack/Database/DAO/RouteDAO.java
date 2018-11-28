package de.mobcom.group3.gotrack.Database.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.Build;
import android.support.annotation.RequiresApi;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import de.mobcom.group3.gotrack.Database.Models.Route;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static de.mobcom.group3.gotrack.Database.DAO.DbContract.RouteEntry.*;

// toDo: write javaDoc and comments


@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
public class RouteDAO implements IDAO<Route> {
    private SQLiteDatabase writableDb;
    private SQLiteDatabase readableDb;
    private Gson gson = new Gson();
    private Type listType = new TypeToken<ArrayList<Location>>() {}.getType();
    private Type exImportType = Route.class;

    public RouteDAO(Context context) {
        DbHelper dbHelper = new DbHelper(context);
        writableDb = dbHelper.getInstance(true);
        readableDb = dbHelper.getInstance(false);
    }

    @Override
    public void create(Route route) {
        route.setId((int) writableDb.insert(TABLE_NAME, null, valueGenerator(route)));
    }

    private ContentValues valueGenerator(Route route) {
        ContentValues values = new ContentValues();
        values.put(COL_USER, route.getUserId());
        values.put(COL_NAME, route.getName());
        values.put(COL_TIME, route.getTime());
        values.put(COL_DISTANCE, route.getDistance());
        values.put(COL_LOCATIONS, gson.toJson(route.getLocations())); // alternative toJsonTree().getAsString()
        return values;
    }

    @Override
    public Route read(int id) {
        Route result = new Route();
        String selection = COL_ID + " = ?";
        String[] selectionArgs = { String.valueOf(id) };
        String[] projection = {
                COL_ID,
                COL_USER,
                COL_NAME,
                COL_TIME,
                COL_DISTANCE,
                COL_LOCATIONS
        };
        Cursor cursor = readableDb.query(
                TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );
        if (cursor.moveToFirst()) {
            result.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)));
            result.setUserID(cursor.getInt(cursor.getColumnIndexOrThrow(COL_USER)));
            result.setName(cursor.getString(cursor.getColumnIndexOrThrow(COL_NAME)));
            result.setTime(cursor.getDouble(cursor.getColumnIndexOrThrow(COL_TIME)));
            result.setDistance(cursor.getDouble(cursor.getColumnIndexOrThrow(COL_DISTANCE)));
            result.setLocations(gson.fromJson(cursor.getString(
                    cursor.getColumnIndexOrThrow(COL_LOCATIONS)), listType));
        }
        cursor.close();
        return result;
    }

    @Override
    public List<Route> readAll(int userId) {
        return this.readAll(userId, new String[]{"id", "DESC"});
    }

    public List<Route> readAll(int userId, String[] orderArgs) {
        String selection = COL_USER + " = ?";
        String[] selectionArgs = { String.valueOf(userId) };
        String[] projection = {
                COL_ID,
                COL_USER,
                COL_NAME,
                COL_TIME,
                COL_DISTANCE,
                COL_LOCATIONS
        };
        List<Route> result = new ArrayList<>();
        Cursor cursor = readableDb.query(
                TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                orderArgs[0] + " " + orderArgs[1]
        );
        if(cursor.moveToFirst())
            do {
                result.add(new Route(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_USER)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_NAME)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(COL_TIME)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(COL_DISTANCE)),
                        gson.fromJson(cursor.getString(
                                cursor.getColumnIndexOrThrow(COL_LOCATIONS)), listType)));
            } while (cursor.moveToNext());
        cursor.close();
        return result;
    }

    @Override
    public void update(int id, Route route) {
        String selection = COL_ID + " = ?";
        String[] selectionArgs = { String.valueOf(route.getUserId()) };
        writableDb.update(TABLE_NAME, valueGenerator(route), selection, selectionArgs);
    }

    private void delete(int id) {
        String selection = COL_ID + " LIKE ?";
        String[] selectionArgs = { String.valueOf(id) };
        writableDb.delete(TABLE_NAME, selection, selectionArgs);
    }

    @Override
    public void delete(Route route) {
        this.delete(route.getId());
    }

    public void importRouteFromJSON(String jsonString) {
        this.create(gson.fromJson(jsonString, exImportType));
    }

    public void importRoutesFromJson(List<String> jsonStrings) {
        for (String jsonString : jsonStrings) {
            this.importRouteFromJSON(jsonString);
        }
    }

    public String exportRouteToJson(int id) {
        return gson.toJson(this.read(id));
    }

    public List<String> exportRoutesToJson(int userId) {
        List<String> result = new ArrayList<>();
        for (Route route : readAll(userId)) {
            result.add(gson.toJson(route));
        }
        return result;
    }
}
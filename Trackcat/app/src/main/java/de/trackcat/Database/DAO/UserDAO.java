package de.trackcat.Database.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.google.gson.Gson;

import de.trackcat.Database.Models.Location;
import de.trackcat.Database.Models.Route;
import de.trackcat.Database.Models.User;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static de.trackcat.Database.DAO.DbContract.UserEntry.*;

/**
 * Data access object for users.
 * Alters database content via CRUD methods.
 * Creates im- and exportable users as JSON.
 */
public class UserDAO {
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
    public UserDAO(Context context) {
        this.context = context;
    }

    /**
     * Inserts a new user into the database.
     *
     * @param user of type user to be stored in the database
     *
     *             <p>
     *             Sets the database id to the model.
     *             </p>
     */
    public void create(User user) {
        DbHelper dbHelper = new DbHelper(context);
        try {
            dbHelper.getWritableDatabase().insert(TABLE_NAME, null, valueGenerator(user));
        } finally {
            dbHelper.close();
        }
    }

    /**
     * Method to generate content values.
     *
     * @param user of type user
     * @return content values to be inserted into database
     *
     * <p>
     * Maps the attributes of the user model to content values based on columns
     * where they have to be inserted. This type of prepared statement should prevent SQL
     * injections.
     * </p>
     */
    private ContentValues valueGenerator(User user) {
        ContentValues values = new ContentValues();
        values.put(COL_ID, user.getId());
        values.put(COL_FIRSTNAME, user.getFirstName());
        values.put(COL_LASTNAME, user.getLastName());
        values.put(COL_MAIL, user.getMail());
        values.put(COL_PASSWORD, user.getPassword());
        values.put(COL_WEIGHT, user.getWeight());
        values.put(COL_SIZE, user.getSize());
        values.put(COL_GENDER, user.getGender());
        values.put(COL_DATEOFBIRTH, user.getDateOfBirth());
        values.put(COL_DATEOFREGISTRATION, user.getDateOfRegistration());
        values.put(COL_LASTLOGIN, user.getLastLogin());
        values.put(COL_AMOUNTRECORD, user.getAmountRecord());
        values.put(COL_TOTALTIME, user.getTotalTime());
        values.put(COL_TOTALDISTANCE, user.getTotalDistance());
        values.put(COL_TIMESTAMP, user.getTimeStamp());
        values.put(COL_HINT, user.isHintsActiveDB());
        values.put(COL_THEME, user.isDarkThemeActiveDB());
        values.put(COL_ISSYNCHRONIZED, user.isSynchronizedDB());
        values.put(COL_IMAGE, user.getImage());
        return values;
    }

    /**
     * Reads a specific user from database, which has matching id.
     *
     * @param id of type integer of which user has to be selected
     * @return user from database, if matching id was found else an empty user object
     */
    public User read(int id) {
        DbHelper dbHelper = new DbHelper(context);
        User result = new User();
        try {
            String selection = COL_ID + " = ?";
            String[] selectionArgs = {String.valueOf(id)};
            String[] projection = {
                    COL_ID,
                    COL_FIRSTNAME,
                    COL_LASTNAME,
                    COL_MAIL,
                    COL_PASSWORD,
                    COL_WEIGHT,
                    COL_SIZE,
                    COL_GENDER,
                    COL_DATEOFBIRTH,
                    COL_DATEOFREGISTRATION,
                    COL_LASTLOGIN,
                    COL_AMOUNTRECORD,
                    COL_TOTALTIME,
                    COL_TOTALDISTANCE,
                    COL_TIMESTAMP,
                    COL_THEME,
                    COL_HINT,
                    COL_IMAGE,
                    COL_ISSYNCHRONIZED};
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
                    result.setFirstName(cursor.getString(cursor.getColumnIndexOrThrow(COL_FIRSTNAME)));
                    result.setLastName(cursor.getString(cursor.getColumnIndexOrThrow(COL_LASTNAME)));
                    result.setHintsActiveDB(cursor.getInt(cursor.getColumnIndexOrThrow(COL_HINT)));
                    result.setDarkThemeActiveDB(cursor.getInt(cursor.getColumnIndexOrThrow(COL_THEME)));
                    result.setMail(cursor.getString(cursor.getColumnIndexOrThrow(COL_MAIL)));
                    result.setPassword(cursor.getString(cursor.getColumnIndexOrThrow(COL_PASSWORD)));
                    result.setWeight(cursor.getFloat(cursor.getColumnIndexOrThrow(COL_WEIGHT)));
                    result.setSize(cursor.getFloat(cursor.getColumnIndexOrThrow(COL_SIZE)));
                    result.setGender(cursor.getInt(cursor.getColumnIndexOrThrow(COL_GENDER)));
                    result.setDateOfBirth(cursor.getLong(cursor.getColumnIndexOrThrow(COL_DATEOFBIRTH)));
                    result.setDateOfRegistration(cursor.getLong(cursor.getColumnIndexOrThrow(COL_DATEOFREGISTRATION)));
                    result.setLastLogin(cursor.getLong(cursor.getColumnIndexOrThrow(COL_LASTLOGIN)));
                    result.setAmountRecord(cursor.getLong(cursor.getColumnIndexOrThrow(COL_AMOUNTRECORD)));
                    result.setTotalTime(cursor.getLong(cursor.getColumnIndexOrThrow(COL_TOTALTIME)));
                    result.setTotalDistance(cursor.getLong(cursor.getColumnIndexOrThrow(COL_TOTALDISTANCE)));
                    result.setTimeStamp(cursor.getLong(cursor.getColumnIndexOrThrow(COL_TIMESTAMP)));
                    result.setImage(cursor.getBlob(cursor.getColumnIndexOrThrow(COL_IMAGE)));
                    result.setIsSynchronizedDB(cursor.getInt(cursor.getColumnIndexOrThrow(COL_ISSYNCHRONIZED)));
                }
            }
        } finally {
            dbHelper.close();
        }
        return result;
    }

    /**
     * Reads all users from database.
     *
     * @return List of all users in database sorted descending after id,
     */
    public List<User> readAll() {
        return this.readAll(new String[]{COL_ID, "DESC"});
    }

    /**
     * Reads all users from database.
     *
     * @param orderArgs String[] { column to sort, ASC / DESC } use COL_ID or COL_NAME from
     *                  DbContract as columns and ASC for ascending or DESC for descending order
     * @return List of all users in database
     */
    private List<User> readAll(String[] orderArgs) {
        DbHelper dbHelper = new DbHelper(context);
        ArrayList<User> result = new ArrayList<>();
        try {
            String[] projection = {
                    COL_ID,
                    COL_FIRSTNAME,
                    COL_LASTNAME,
                    COL_MAIL,
                    COL_PASSWORD,
                    COL_IMAGE,
                    COL_WEIGHT,
                    COL_SIZE,
                    COL_GENDER,
                    COL_DATEOFBIRTH,
                    COL_DATEOFREGISTRATION,
                    COL_LASTLOGIN,
                    COL_AMOUNTRECORD,
                    COL_TOTALTIME,
                    COL_TOTALDISTANCE,
                    COL_TIMESTAMP,
                    COL_HINT,
                    COL_THEME,
                    COL_ISSYNCHRONIZED};
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
                        result.add(new User(
                                cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)),
                                cursor.getString(cursor.getColumnIndexOrThrow(COL_FIRSTNAME)),
                                cursor.getString(cursor.getColumnIndexOrThrow(COL_LASTNAME)),
                                cursor.getInt(cursor.getColumnIndexOrThrow(COL_HINT)),
                                cursor.getInt(cursor.getColumnIndexOrThrow(COL_THEME)),
                                cursor.getString(cursor.getColumnIndexOrThrow(COL_MAIL)),
                                cursor.getString(cursor.getColumnIndexOrThrow(COL_PASSWORD)),
                                cursor.getFloat(cursor.getColumnIndexOrThrow(COL_WEIGHT)),
                                cursor.getFloat(cursor.getColumnIndexOrThrow(COL_SIZE)),
                                cursor.getInt(cursor.getColumnIndexOrThrow(COL_GENDER)),
                                cursor.getLong(cursor.getColumnIndexOrThrow(COL_DATEOFBIRTH)),
                                cursor.getLong(cursor.getColumnIndexOrThrow(COL_DATEOFREGISTRATION)),
                                cursor.getLong(cursor.getColumnIndexOrThrow(COL_LASTLOGIN)),
                                cursor.getLong(cursor.getColumnIndexOrThrow(COL_AMOUNTRECORD)),
                                cursor.getLong(cursor.getColumnIndexOrThrow(COL_TOTALTIME)),
                                cursor.getLong(cursor.getColumnIndexOrThrow(COL_TOTALDISTANCE)),
                                cursor.getLong(cursor.getColumnIndexOrThrow(COL_TIMESTAMP)),
                                cursor.getBlob(cursor.getColumnIndexOrThrow(COL_IMAGE)),
                                cursor.getInt(cursor.getColumnIndexOrThrow(COL_ISSYNCHRONIZED))));
                    } while (cursor.moveToNext());
            }
        } finally {
            dbHelper.close();
        }
        return result;
    }

    /**
     * Updates a specific user in database with handed over user, which has matching id.
     *
     * @param id   of type integer of which route has to be updated
     * @param user of type user which would override user with defined id in database
     */
    public void update(int id, User user) {
        DbHelper dbHelper = new DbHelper(context);
        try {
            String selection = COL_ID + " = ?";
            String[] selectionArgs = {String.valueOf(id)};
            user.setId(dbHelper.getWritableDatabase().update(TABLE_NAME, valueGenerator(user), selection, selectionArgs));
        } finally {
            dbHelper.close();
        }
    }

    /**
     * Deletes a specific user and all of its routes from database, which has matching id.
     *
     * @param user of type user which has to be deleted
     */
    public void delete(User user) {
        DbHelper dbHelper = new DbHelper(context);
        RouteDAO routeDAO = new RouteDAO(context);

        try {
            for (Route route : routeDAO.readAll()) {
                routeDAO.delete(route.getId());
            }

            dbHelper.getWritableDatabase().execSQL("delete from " + DbContract.RecordTempEntry.TABLE_NAME);
            dbHelper.getWritableDatabase().execSQL("delete from " + DbContract.LocationTempEntry.TABLE_NAME);
            String selection = COL_ID + " LIKE ?";
            String[] selectionArgs = {String.valueOf(user.getId())};
            dbHelper.getWritableDatabase().delete(TABLE_NAME, selection, selectionArgs);
        } finally {
            dbHelper.close();
        }
    }

    /**
     * Count the entrys of the userTable
     *
     * @return amount of users in table
     */
    public int userInDB() {
        DbHelper dbHelper = new DbHelper(context);
        int result;
        final String SQL_COUNT_USER_ENTRYS = "SELECT COUNT(*) FROM " + TABLE_NAME;

        try {

            Cursor cursor = dbHelper.getWritableDatabase().rawQuery(SQL_COUNT_USER_ENTRYS, null);
            cursor.moveToFirst();
            result = cursor.getInt(0);

        } finally {
            dbHelper.close();
        }
        return result;
    }

    /**
     * Reads current users from database.
     *
     * @return List of all users in database
     */
    public User readCurrentUser() {
        String[] orderArgs = new String[]{COL_ID, "ASC"};
        DbHelper dbHelper = new DbHelper(context);
        User result = new User();
        try {
            String[] projection = {
                    COL_ID,
                    COL_HINT,
                    COL_THEME
            };
            try (Cursor cursor = dbHelper.getReadableDatabase().query(
                    TABLE_NAME,
                    projection,
                    null,
                    null,
                    null,
                    null,
                    orderArgs[0] + " " + orderArgs[1],
                    "1")) {
                if (cursor.moveToFirst()) {
                    result.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)));
                    result.setHintsActiveDB(cursor.getInt(cursor.getColumnIndexOrThrow(COL_HINT)));
                    result.setDarkThemeActiveDB(cursor.getInt(cursor.getColumnIndexOrThrow(COL_THEME)));
                }
            }
        } finally {
            dbHelper.close();
        }
        return result;
    }
}
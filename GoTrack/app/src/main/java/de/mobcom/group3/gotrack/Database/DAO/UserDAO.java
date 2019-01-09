package de.mobcom.group3.gotrack.Database.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import com.google.gson.Gson;

import de.mobcom.group3.gotrack.Database.Models.Route;
import de.mobcom.group3.gotrack.Database.Models.User;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import static de.mobcom.group3.gotrack.Database.DAO.DbContract.UserEntry.*;

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
    // global GSON object to convert user object into JSON
    private Gson gson = new Gson();
    // type to define in or from which object would be converted by GSON
    private Type imExportType = User.class;

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
     * <p>
     *      Sets the database id to the model.
     * </p>
     */
    public void create(User user) {
        DbHelper dbHelper = new DbHelper(context);
        try {
            user.setId((int) dbHelper.getWritableDatabase().insert(TABLE_NAME, null, valueGenerator(user)));
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
     *     Maps the attributes of the user model to content values based on columns
     *     where they have to be inserted. This type of prepared statement should prevent SQL
     *     injections.
     * </p>
     */
    private ContentValues valueGenerator(User user) {
        ContentValues values = new ContentValues();
        values.put(COL_FIRSTNAME, user.getFirstName());
        values.put(COL_LASTNAME, user.getLastName());
        values.put(COL_MAIL, user.getMail());
        values.put(COL_ISACTIVE, user.isActiveForDB());
        values.put(COL_HINT, user.isHintsActiveDB());
        values.put(COL_THEME, user.isDarkThemeActiveDB());
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
                    COL_ISACTIVE,
                    COL_THEME,
                    COL_HINT,
                    COL_IMAGE };
            try (Cursor cursor = dbHelper.getReadableDatabase().query(
                    TABLE_NAME,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    null )) {
                if (cursor.moveToFirst()) {
                    result.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)));
                    result.setFirstName(cursor.getString(cursor.getColumnIndexOrThrow(COL_FIRSTNAME)));
                    result.setLastName(cursor.getString(cursor.getColumnIndexOrThrow(COL_LASTNAME)));
                    result.setActiveForDB(cursor.getInt(cursor.getColumnIndexOrThrow(COL_ISACTIVE)));
                    result.setHintsActiveDB(cursor.getInt(cursor.getColumnIndexOrThrow(COL_HINT)));
                    result.setDarkThemeActiveDB(cursor.getInt(cursor.getColumnIndexOrThrow(COL_THEME)));
                    result.setMail(cursor.getString(cursor.getColumnIndexOrThrow(COL_MAIL)));
                    result.setImage(cursor.getBlob(cursor.getColumnIndexOrThrow(COL_IMAGE)));
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
                    COL_ISACTIVE,
                    COL_IMAGE,
                    COL_HINT,
                    COL_THEME };
            try (Cursor cursor = dbHelper.getReadableDatabase().query(
                    TABLE_NAME,
                    projection,
                    null,
                    null,
                    null,
                    null,
                    orderArgs[0] + " " + orderArgs[1] )) {
                if (cursor.moveToFirst())
                    do {
                        result.add(new User(
                                cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)),
                                cursor.getString(cursor.getColumnIndexOrThrow(COL_FIRSTNAME)),
                                cursor.getString(cursor.getColumnIndexOrThrow(COL_LASTNAME)),
                                cursor.getInt(cursor.getColumnIndexOrThrow(COL_ISACTIVE)),
                                cursor.getInt(cursor.getColumnIndexOrThrow(COL_HINT)),
                                cursor.getInt(cursor.getColumnIndexOrThrow(COL_THEME)),
                                cursor.getString(cursor.getColumnIndexOrThrow(COL_MAIL)),
                                cursor.getBlob(cursor.getColumnIndexOrThrow(COL_IMAGE))));
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
     * @param id of type integer of which route has to be updated
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
            for (Route route : routeDAO.readAll(user.getId())) {
                routeDAO.delete(route.getId());
            }
            String selection = COL_ID + " LIKE ?";
            String[] selectionArgs = {String.valueOf(user.getId())};
            dbHelper.getWritableDatabase().delete(TABLE_NAME, selection, selectionArgs);
        } finally {
            dbHelper.close();
        }
    }

    /**
     * Imports a single user from handed over JSON.
     *
     * @param jsonString of type string which defines the route to be imported
     *
     * <p>
     *      Creates a user with the attributes which were defined in JSON
     * </p>
     */
    public void importUserFromJson(String jsonString) {
        this.create(gson.fromJson(jsonString, imExportType));
    }

    /**
     * Imports all users from handed over JSON List.
     *
     * @param jsonStrings of type List<String> which inherits the routes to be imported
     *
     * <p>
     *      Creates a user for each entry with the attributes which were defined in JSON
     * </p>
     */
    public void importUsersFromJson(ArrayList<String> jsonStrings) {
        for (String jsonString : jsonStrings) {
            this.importUserFromJson(jsonString);
        }
    }

    /**
     * Creates a JSON string which defines a user object and its attributes.
     *
     * @param id of type integer of which user has to be exported
     * @return a JSON string
     */
    public String exportUserToJson(int id) {
        return gson.toJson(this.read(id));
    }

    /**
     * Creates a List of JSON strings which defines all user objects and its attributes.
     *
     * @return a List of JSON strings
     */
    public ArrayList<String> exportUsersToJson() {
        ArrayList<String> result = new ArrayList<>();
        for (User user : this.readAll(new String[]{COL_ID, "DESC"})) {
            result.add(gson.toJson(user));
        }
        return result;
    }
}
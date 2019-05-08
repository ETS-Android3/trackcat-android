package de.trackcat.Database.DAO;

import android.provider.BaseColumns;

/*
 + class to provide database configuration
 */
final class DbContract {

    /*
     + private constructor to prevent instantiating this class
     */
    private DbContract() {
    }

    /*
     + defining standard database constants for creation
     */
    static final int DATABASE_VERSION = 1;
    static final String DATABASE_NAME = "GoTrack.db";

    /*
     + string to create table where routes where stored in
     */
    static final String SQL_CREATE_ROUTE_TABLE =
            "CREATE TABLE IF NOT EXISTS " + RouteEntry.TABLE_NAME + " ( " +
                    RouteEntry.COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    RouteEntry.COL_USER + " INTEGER, " +
                    RouteEntry.COL_NAME + " TEXT, " +
                    RouteEntry.COL_TIME + " LONG, " +
                    RouteEntry.COL_DATE + " LONG, " +
                    RouteEntry.COL_TYPE + " INTEGER, " +
                    RouteEntry.COL_RIDETIME + " LONG, " +
                    RouteEntry.COL_DISTANCE + " DOUBLE, " +
                    RouteEntry.COL_TIMESTAMP + " LONG, " +
                    RouteEntry.COL_ISIMPORTED + " BOOLEAN, " +
                    RouteEntry.COL_LOCATIONS + " TEXT) ";

    /*
     + string to delete route table
     */
    static final String SQL_DELETE_ROUTE_TABLE = "DROP TABLE IF EXISTS " + RouteEntry.TABLE_NAME;

    /*
     + string to create table where users where stored in
     */
    static final String SQL_CREATE_USER_TABLE =
            "CREATE TABLE IF NOT EXISTS " + UserEntry.TABLE_NAME + " ( " +
                    UserEntry.COL_ID + " INTEGER PRIMARY KEY, " +
                    UserEntry.COL_FIRSTNAME + " TEXT, " +
                    UserEntry.COL_LASTNAME + " TEXT, " +
                    UserEntry.COL_ISACTIVE + " BOOLEAN, " +
                    UserEntry.COL_THEME + " BOOLEAN, " +
                    UserEntry.COL_HINT + " BOOLEAN, " +
                    UserEntry.COL_MAIL + " TEXT, " +
                    UserEntry.COL_PASSWORD + " TEXT, " +
                    UserEntry.COL_WEIGHT + " FLOAT, " +
                    UserEntry.COL_SIZE + " FLOAT, " +
                    UserEntry.COL_GENDER + " INTEGER, " +
                    UserEntry.COL_DATEOFBIRTH + " LONG, " +
                    UserEntry.COL_DATEOFREGISTRATION + " LONG, " +
                    UserEntry.COL_LASTLOGIN + " LONG, " +
                    UserEntry.COL_TIMESTAMP + " LONG, " +
                    UserEntry.COL_IDUSERS + " INTEGER, " +
                    UserEntry.COL_ISSYNCHRONIZED + " BOOLEAN, " +
                    UserEntry.COL_IMAGE + " BLOB) ";

    /*
     + string to delete user table
     */
    static final String SQL_DELETE_USER_TABLE = "DROP TABLE IF EXISTS " + UserEntry.TABLE_NAME;

    /*
     + class to declare columns for route table
     */
    static final class RouteEntry implements BaseColumns {
        private RouteEntry() {}
        static final String TABLE_NAME = "route_table";
        static final String COL_ID = "id";
        static final String COL_USER = "fk_user_id";
        static final String COL_NAME = "name";
        static final String COL_TIME = "time";
        static final String COL_DATE = "date";
        static final String COL_TYPE = "type";
        static final String COL_RIDETIME = "rideTime";
        static final String COL_DISTANCE = "distance";
        static final String COL_LOCATIONS = "locations";
        static final String COL_TIMESTAMP = "timestamp";
        static final String COL_ISIMPORTED = "imported";
    }

    /*
     + class to declare columns for user table
     */
    static final class UserEntry implements BaseColumns {
        private UserEntry() {}
        static final String TABLE_NAME = "user_table";
        static final String COL_ID = "id";
        static final String COL_LASTNAME = "lastname";
        static final String COL_FIRSTNAME = "firstname";
        static final String COL_ISACTIVE = "active";
        static final String COL_HINT = "hint";
        static final String COL_THEME = "theme";
        static final String COL_MAIL = "mail";
        static final String COL_PASSWORD = "password";
        static final String COL_WEIGHT ="weight";
        static final String COL_SIZE ="size";
        static final String COL_GENDER ="gender";
        static final String COL_DATEOFBIRTH ="dateOfBirth";
        static final String COL_DATEOFREGISTRATION ="dateOfRegistration";
        static final String COL_LASTLOGIN ="lastLogin";
        static final String COL_TIMESTAMP ="timeStamp";
        static final String COL_IDUSERS ="idUsers";
        static final String COL_ISSYNCHRONIZED ="isSynchronized";
        static final String COL_IMAGE = "image";
    }

}
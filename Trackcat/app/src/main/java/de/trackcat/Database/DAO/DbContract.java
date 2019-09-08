package de.trackcat.Database.DAO;

import android.provider.BaseColumns;

/* Class to provide database configuration */
final class DbContract {

    private DbContract() {
    }

    /* Defining standard database constants for creation */
    static final int DATABASE_VERSION = 1;
    static final String DATABASE_NAME = "Trackcat.db";


    /* Strings to define record tables */
    static final String SQL_CREATE_ROUTE_TABLE =
            "CREATE TABLE IF NOT EXISTS " + RouteEntry.TABLE_NAME + " ( " +
                    RouteEntry.COL_ID + " INTEGER PRIMARY KEY, " +
                    RouteEntry.COL_NAME + " TEXT, " +
                    RouteEntry.COL_TIME + " LONG, " +
                    RouteEntry.COL_DATE + " LONG, " +
                    RouteEntry.COL_TYPE + " INTEGER, " +
                    RouteEntry.COL_RIDETIME + " LONG, " +
                    RouteEntry.COL_DISTANCE + " DOUBLE, " +
                    RouteEntry.COL_TIMESTAMP + " LONG, " +
                    RouteEntry.COL_ISTEMP + " BOOLEAN, " +
                    RouteEntry.COL_LOCATIONS + " TEXT) ";

    static final String SQL_DELETE_ROUTE_TABLE = "DROP TABLE IF EXISTS " + RouteEntry.TABLE_NAME;

    /* Strings to define temp record tables */
    static final String SQL_CREATE_RECORD_TEMP_TABLE =
            "CREATE TABLE IF NOT EXISTS " + RecordTempEntry.TABLE_NAME + " ( " +
                    RecordTempEntry.COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    RecordTempEntry.COL_NAME + " TEXT, " +
                    RecordTempEntry.COL_TIME + " LONG, " +
                    RecordTempEntry.COL_DATE + " LONG, " +
                    RecordTempEntry.COL_TYPE + " INTEGER, " +
                    RecordTempEntry.COL_RIDETIME + " LONG, " +
                    RecordTempEntry.COL_DISTANCE + " DOUBLE, " +
                    RecordTempEntry.COL_TIMESTAMP + " LONG, " +
                    RecordTempEntry.COL_ISTEMP + " BOOLEAN, " +
                    RecordTempEntry.COL_LOCATIONS + " TEXT) ";

    static final String SQL_DELETE_RECORD_TEMP_TABLE = "DROP TABLE IF EXISTS " + RecordTempEntry.TABLE_NAME;

    /* Strings to define temp location tables */
    static final String SQL_CREATE_LOCATION_TEMP_TABLE =
            "CREATE TABLE IF NOT EXISTS " + LocationTempEntry.TABLE_NAME + " ( " +
                    LocationTempEntry.COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    LocationTempEntry.COL_LATITUDE + " DOUBLE, " +
                    LocationTempEntry.COL_LONGITUDE + " DOUBLE, " +
                    LocationTempEntry.COL_ALTITUDE + " DOUBLE, " +
                    LocationTempEntry.COL_TIME + " LONG, " +
                    LocationTempEntry.COL_SPEED + " FLOAT, " +
                    LocationTempEntry.COL_RECORD_ID + " INTEGER, " +
                    "FOREIGN KEY(" + LocationTempEntry.COL_RECORD_ID + ") REFERENCES " + RecordTempEntry.TABLE_NAME + "(" + RecordTempEntry.COL_ID + "))";

    static final String SQL_DELETE_LOCATION_TEMP_TABLE = "DROP TABLE IF EXISTS " + LocationTempEntry.TABLE_NAME;

    /* Strings to define user tables */
    static final String SQL_CREATE_USER_TABLE =
            "CREATE TABLE IF NOT EXISTS " + UserEntry.TABLE_NAME + " ( " +
                    UserEntry.COL_ID + " INTEGER PRIMARY KEY, " +
                    UserEntry.COL_FIRSTNAME + " TEXT, " +
                    UserEntry.COL_LASTNAME + " TEXT, " +
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
                    UserEntry.COL_AMOUNTRECORD + " LONG, " +
                    UserEntry.COL_TOTALTIME + " LONG, " +
                    UserEntry.COL_TOTALDISTANCE + " LONG, " +
                    UserEntry.COL_TIMESTAMP + " LONG, " +
                    UserEntry.COL_IMAGE + " BLOB) ";

    static final String SQL_DELETE_USER_TABLE = "DROP TABLE IF EXISTS " + UserEntry.TABLE_NAME;

    /* Class to declare record table */
    static final class RouteEntry implements BaseColumns {
        private RouteEntry() {
        }

        static final String TABLE_NAME = "record_table";
        static final String COL_ID = "id";
        static final String COL_NAME = "name";
        static final String COL_TIME = "time";
        static final String COL_DATE = "date";
        static final String COL_TYPE = "type";
        static final String COL_RIDETIME = "rideTime";
        static final String COL_DISTANCE = "distance";
        static final String COL_LOCATIONS = "locations";
        static final String COL_TIMESTAMP = "timestamp";
        static final String COL_ISTEMP = "temp";
    }

    /* Class to declare temp record table */
    static final class RecordTempEntry implements BaseColumns {
        private RecordTempEntry() {
        }

        static final String TABLE_NAME = "record_temp_table";
        static final String COL_ID = "id";
        static final String COL_NAME = "name";
        static final String COL_TIME = "time";
        static final String COL_DATE = "date";
        static final String COL_TYPE = "type";
        static final String COL_RIDETIME = "rideTime";
        static final String COL_DISTANCE = "distance";
        static final String COL_LOCATIONS = "locations";
        static final String COL_TIMESTAMP = "timestamp";
        static final String COL_ISTEMP = "temp";
    }

    /* Class to declare temp location table */
    static final class LocationTempEntry implements BaseColumns {
        private LocationTempEntry() {
        }

        static final String TABLE_NAME = "location_temp_table";
        static final String COL_ID = "id";
        static final String COL_LATITUDE = "latitude";
        static final String COL_LONGITUDE = "longitude";
        static final String COL_ALTITUDE = "altitude";
        static final String COL_TIME = "time";
        static final String COL_SPEED = "speed";
        static final String COL_RECORD_ID = "fk_record_id";
    }

    /* Class to declare user table */
    static final class UserEntry implements BaseColumns {
        private UserEntry() {
        }

        static final String TABLE_NAME = "user_table";
        static final String COL_ID = "id";
        static final String COL_LASTNAME = "lastname";
        static final String COL_FIRSTNAME = "firstname";
        static final String COL_HINT = "hint";
        static final String COL_THEME = "theme";
        static final String COL_MAIL = "mail";
        static final String COL_PASSWORD = "password";
        static final String COL_WEIGHT = "weight";
        static final String COL_SIZE = "size";
        static final String COL_GENDER = "gender";
        static final String COL_DATEOFBIRTH = "dateOfBirth";
        static final String COL_DATEOFREGISTRATION = "dateOfRegistration";
        static final String COL_LASTLOGIN = "lastLogin";
        static final String COL_AMOUNTRECORD = "amountRecord";
        static final String COL_TOTALTIME = "totalTime";
        static final String COL_TOTALDISTANCE = "totalDistance";
        static final String COL_TIMESTAMP = "timeStamp";
        static final String COL_IMAGE = "image";
    }
}
package de.mobcom.group3.gotrack.Database.DAO;

import android.provider.BaseColumns;

// toDo: better change to enum?
final class DbContract {
    private DbContract() {}

    static final int DATABASE_VERSION = 1;
    static final String DATABASE_NAME = "GoTrack.db";

    static final String SQL_CREATE_ROUTE_TABLE =
            "CREATE TABLE IF NOT EXISTS " + RouteEntry.TABLE_NAME + " (" +
                    RouteEntry.COL_ID + " LONG PRIMARY KEY," +
                    RouteEntry.COL_USER + " LONG, " + //toDo: implement foreign key constraint for user.id
                    RouteEntry.COL_NAME + " TEXT," +
                    RouteEntry.COL_TIME + " DOUBLE," +
                    RouteEntry.COL_DISTANCE + " DOUBLE," +
                    RouteEntry.COL_LOCATIONS + " TEXT)";

    static final String SQL_DELETE_ROUTE_TABLE =
            "DROP TABLE IF EXISTS " + RouteEntry.TABLE_NAME;

    static final String SQL_CREATE_USER_TABLE =
            "CREATE TABLE IF NOT EXISTS " + UserEntry.TABLE_NAME + " (" +
                    UserEntry.COL_ID + " LONG PRIMARY KEY," +
                    UserEntry.COL_NAME + " TEXT)";

    static final String SQL_DELETE_USER_TABLE =
            "DROP TABLE IF EXISTS " + UserEntry.TABLE_NAME;

    // toDo: better change to enum?
    static final class RouteEntry implements BaseColumns {
        static final String TABLE_NAME = "routes_table";
        static final String COL_ID = "id";
        static final String COL_USER = "user_id";
        static final String COL_NAME = "name";
        static final String COL_TIME = "time";
        static final String COL_DISTANCE = "distance";
        static final String COL_LOCATIONS = "locations";
    }

    // toDo: better change to enum?
    static final class UserEntry implements BaseColumns {
        static final String TABLE_NAME = "user_table";
        static final String COL_ID = "id";
        static final String COL_NAME = "name";
    }

}

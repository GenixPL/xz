package pw.xz.xdd.xz;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Cezary Borowski on 2018-01-13.
 */

public class SQLiteDbHelper extends SQLiteOpenHelper
{
    public SQLiteDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "lectures_database.db";

    public static class Entry implements BaseColumns {
        public static final String TABLENAME = "Lectures";
        public static final String NAME = "name";
        public static final String START_TIME_HH = "start_time_hh";
        public static final String START_TIME_MM = "start_time_mm";
        public static final String END_TIME_HH = "end_time_hh";
        public static final String END_TIME_MM = "end_time_mm";
        public static final String DAY = "day";
        public static final String ROOM_ID = "room_id";
        public static final String DESCRIPTION = "description";
    }

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + Entry.TABLENAME + " (" +
                    Entry._ID + " INTEGER PRIMARY KEY," +
                    Entry.NAME + " TEXT," +
                    Entry.START_TIME_HH + " INTEGER," +
                    Entry.START_TIME_MM + " INTEGER," +
                    Entry.END_TIME_HH + " INTEGER," +
                    Entry.END_TIME_MM + " INTEGER," +
                    Entry.DAY + " TEXT," +
                    Entry.ROOM_ID + " TEXT," +
                    Entry.DESCRIPTION + " TEXT)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + Entry.TABLENAME;


    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_DELETE_ENTRIES);
        db.execSQL(SQL_CREATE_ENTRIES);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public void addToDb(String name, int start_time_hh, int start_time_mm,
                        int end_time_hh, int end_time_mm, String day,
                        String room_id, String description)
    {
        // Gets the data repository in write mode
        SQLiteDatabase db = getWritableDatabase();

        // Create a new map of values, where column names are the key
        ContentValues values = new ContentValues();

        values.put(Entry.NAME, name);
        values.put(Entry.START_TIME_HH, start_time_hh);
        values.put(Entry.START_TIME_MM, start_time_mm);
        values.put(Entry.END_TIME_HH, end_time_hh);
        values.put(Entry.END_TIME_MM, end_time_mm);
        values.put(Entry.DAY, day);
        values.put(Entry.ROOM_ID, room_id);
        values.put(Entry.DESCRIPTION, description);
    }

    public List<Lecture> getByRoomAndTime(String room_id,
                                          int start_time_hh, int start_time_mm, String day)
    {
        SQLiteDatabase db = getReadableDatabase();
        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                //Entry._ID,
                Entry.NAME,
                //Entry.START_TIME_HH,
                //Entry.START_TIME_MM,
                Entry.END_TIME_HH,
                Entry.END_TIME_MM,
                //Entry.DAY,
                //Entry.ROOM_ID,
                Entry.DESCRIPTION,
        };

        // Filter results WHERE "title" = 'My Title'
        String selection = Entry.ROOM_ID + " = ? AND " + Entry.START_TIME_HH + " >= ? AND "
                + Entry.START_TIME_MM + " != ? AND " + Entry.DAY + " = ?";

        String[] selectionArgs = {
                room_id,
                Integer.toString(start_time_hh),
                Integer.toString(start_time_mm),
                day
        };

        // How you want the results sorted in the resulting Cursor
        String sortOrder =
                Entry.START_TIME_HH + " ASC, " + Entry.START_TIME_MM + " ASC";

        Cursor cursor = db.query(
                Entry.TABLENAME,                     // The table to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder//null                                 // The sort order
        );

        List<Lecture> wyniki = new ArrayList<>();

        while(cursor.moveToNext()) {
            String name = cursor.getString(
                    cursor.getColumnIndexOrThrow(Entry.NAME));
            //This is already known
            //int start_time_hh = cursor.getInt(
            //        cursor.getColumnIndexOrThrow(Entry.START_TIME_HH));
            //This is already known
            //int start_time_mm = cursor.getInt(
            //        cursor.getColumnIndexOrThrow(Entry.START_TIME_MM));
            int end_time_hh = cursor.getInt(
                    cursor.getColumnIndexOrThrow(Entry.END_TIME_HH));
            int end_time_mm = cursor.getInt(
                    cursor.getColumnIndexOrThrow(Entry.END_TIME_MM));
            //This is already known
            //String day = cursor.getString(
            //        cursor.getColumnIndexOrThrow(Entry.DAY));
            //This is already known
            //String room_id = cursor.getString(
            //        cursor.getColumnIndexOrThrow(Entry.ROOM_ID));
            String description = cursor.getString(
                    cursor.getColumnIndexOrThrow(Entry.DESCRIPTION));
            wyniki.add(new Lecture(name, start_time_hh, start_time_mm, end_time_hh,
                    end_time_mm, day, room_id, description));
        }

        cursor.close();
        return wyniki;
    }

}
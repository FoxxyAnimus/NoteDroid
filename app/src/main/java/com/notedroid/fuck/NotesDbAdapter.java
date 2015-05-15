package com.notedroid.fuck;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class NotesDbAdapter {
    // Define constant variables with the names of the data fields
    public static final String KEY_TITLE = "title";
    public static final String KEY_BODY = "body";
    public static final String KEY_ROWID = "_id";
    public static final String KEY_CREATION_DATE = "creation_date";
    public static final String KEY_MODIFICATION_DATE = "modification_date";

    //variables to set up the database to store the notes
    public static final String TAG = "NotesDbAdapter";
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;

    private static final String DATABASE_NAME = "NOTEDROID";
    private static final String DATABASE_TABLE = "NOTES";
    private static final int DATABASE_VERSION = 1;

    //Variable containing the code to create a database
    private static final String DATABASE_CREATE = "CREATE TABLE " + DATABASE_TABLE + " (" +
            KEY_ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            KEY_CREATION_DATE + " TEXT, " +
            KEY_MODIFICATION_DATE + " TEXT, " +
            KEY_TITLE + " TEXT NOT NULL, " +
            KEY_BODY + " TEXT);";

    private final Context mCtx;
    private static class DatabaseHelper extends SQLiteOpenHelper {

        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            // Create the database to store the notes
            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            //React to a database upgrade to a new version
            Log.w(TAG, "Upgrading database to a new version " + oldVersion + " to " +
                newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
            onCreate(db);
        }
    }

    public NotesDbAdapter(Context ctx) {
        this.mCtx = ctx;
    }

    public NotesDbAdapter open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        mDbHelper.close();
    }

    // Create a new note
    public long createNote(String title, String body) {
        //Create a variable to store the values of the note
        ContentValues initialValues = new ContentValues();

        // Get the current time
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd\'T\'HH:mm:ss.SSS");
        String current_time = sdf.format(c.getTime());

        // Add the values to prepare for creating the note
        initialValues.put(KEY_CREATION_DATE, current_time);
        initialValues.put(KEY_MODIFICATION_DATE, current_time);
        initialValues.put(KEY_TITLE, title);
        initialValues.put(KEY_BODY, body);

        // Create the note by inserting it into the database
        return mDb.insert(DATABASE_TABLE, null, initialValues);
    }

    // Updates an existing note with a new title and body. This
    // also updates the modification date
    // @param rowId The rowId of the note to be updated
    // @param title The new title of the existing note
    // @param body The new body of the existing note
    // @return The result of the call to update the database record
    public boolean updateNote(long rowId, String title, String body) {
        // Create a variable to store the values of the note
        ContentValues args = new ContentValues();

        // Get the current time
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd\'T\'HH:mm:ss.SSS");
        String current_time = sdf.format(c.getTime());

        // Add the values to prepare for creating the note
        args.put(KEY_TITLE, title);
        args.put(KEY_BODY, body);
        args.put(KEY_MODIFICATION_DATE, current_time);

        // Updates the existing note database record based on the rowId field
        return mDb.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
    }

    // Return a cursor over the list of all notes in the database
    // @return cursor over all notes
    public Cursor fetchAllNotes() {
        return mDb.query(DATABASE_TABLE, new String[] {KEY_ROWID, KEY_TITLE,
            KEY_BODY}, null, null, null, null, null);
    }

    // Retrieves information about a specific note
    // @param rowId The rowId of the note to be loaded
    // @return A Cursor for an individual note
    public Cursor fetchNote(long rowId) throws SQLException {

        Cursor mCursor = mDb.query(true, DATABASE_TABLE, new String[] {KEY_ROWID,
            KEY_TITLE, KEY_BODY}, KEY_ROWID + '=' + rowId, null,
            null, null, null, null);

        if (mCursor != null) {
            mCursor.moveToFirst();
        }

        return mCursor;
    }

    // Deletes a specific note based on the id
    // @param rowId The rowId of the note to be deleted
    // @return The result of the delete database call
    public boolean deleteNote(long rowId) {
        return mDb.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
    }

    // Gets a note
    // @param rowId
    //@return The note object
    public Note getNoteById(long rowId) {
        // Search the database for the note record we are looking for
        Cursor note = mDb.query(true, DATABASE_TABLE, new String[] {KEY_ROWID, KEY_CREATION_DATE,
            KEY_MODIFICATION_DATE, KEY_TITLE, KEY_BODY},
            KEY_ROWID + "=" + rowId, null, null, null, null, null);

        if (note != null) {
            // Get the first result which in this case, is what we are looking for
            note.moveToFirst();

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd\'T\'HH:mm:ss.SSS");
            Date creationDate;
            Date modificationDate;

            // Get the creation date in the correct format
            try {
                creationDate = sdf.parse(note.getString(note.getColumnIndexOrThrow(KEY_CREATION_DATE)));
            } catch (ParseException e) {
                creationDate = new Date();
            }

            // Get the modification date in teh correct format
            try {
                modificationDate = sdf.parse(note.getString(note.getColumnIndexOrThrow(KEY_MODIFICATION_DATE)));
            } catch (ParseException e) {
                modificationDate = new Date();
            }

            // Create a new Note from the data in the database
            Note result = new Note(note.getLong(note.getColumnIndexOrThrow(KEY_ROWID)),
                    creationDate,
                    modificationDate,
                    note.getString(note.getColumnIndexOrThrow(KEY_TITLE)),
                    note.getString(note.getColumnIndexOrThrow(KEY_BODY)));

            // Close the cursor
            note.close();

            // Return the note object
            return result;

        } else {
            return null;
        }
    }
}

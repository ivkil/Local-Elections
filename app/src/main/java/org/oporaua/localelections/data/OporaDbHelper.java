package org.oporaua.localelections.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static org.oporaua.localelections.data.OporaContract.*;


public class OporaDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    public static final String DATABASE_NAME = "opora.db";

    public OporaDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_ACCIDENTS_TYPES_TABLE = "CREATE TABLE " + AccidentTypeEntry.TABLE_NAME + " (" +
                AccidentTypeEntry._ID + " INTEGER PRIMARY KEY, " +
                AccidentTypeEntry.COLUMN_TITLE + " TEXT NOT NULL);";

        final String SQL_CREATE_ACCIDENTS_SUBTYPES_TABLE = "CREATE TABLE " + AccidentSubtypeEntry.TABLE_NAME + " (" +
                AccidentSubtypeEntry._ID + " INTEGER PRIMARY KEY, " +
                AccidentSubtypeEntry.COLUMN_TITLE + " TEXT NOT NULL);";

        final String SQL_CREATE_REGIONS_TABLE = "CREATE TABLE " + RegionEntry.TABLE_NAME + " (" +
                RegionEntry._ID + " INTEGER PRIMARY KEY, " +
                RegionEntry.COLUMN_TITLE + " TEXT NOT NULL);";

        final String SQL_CREATE_LOCALITIES_TABLE = "CREATE TABLE " + LocalityEntry.TABLE_NAME + " (" +
                LocalityEntry._ID + " INTEGER PRIMARY KEY, " +
                LocalityEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                LocalityEntry.COLUMN_REGION_ID + " INTEGER NOT NULL, " +
                LocalityEntry.COLUMN_DISTRICT + " TEXT, " +
                "FOREIGN KEY (" + LocalityEntry.COLUMN_REGION_ID + ") REFERENCES " +
                RegionEntry.TABLE_NAME + " (" + RegionEntry._ID + "));";

        final String SQL_CREATE_PARTIES_TABLE = "CREATE TABLE " + PartyEntry.TABLE_NAME + " (" +
                PartyEntry._ID + " INTEGER PRIMARY KEY, " +
                PartyEntry.COLUMN_TITLE + " TEXT NOT NULL);";

        final String SQL_CREATE_ELECTIONS_TYPES_TABLE = "CREATE TABLE " + ElectionTypeEntry.TABLE_NAME + " (" +
                ElectionTypeEntry._ID + " INTEGER PRIMARY KEY, " +
                ElectionTypeEntry.COLUMN_TITLE + " TEXT NOT NULL);";

        final String SQL_CREATE_ACCIDENTS_TABLE = "CREATE TABLE " + AccidentEntry.TABLE_NAME + " (" +
                AccidentEntry._ID + " INTEGER PRIMARY KEY, " +
                AccidentEntry.COLUMN_DATE_TEXT + " TEXT NOT NULL, " +
                AccidentEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                AccidentEntry.COLUMN_SOURCE + " TEXT NOT NULL, " +
                AccidentEntry.COLUMN_EVIDENCE_URL + " TEXT, " +
                AccidentEntry.COLUMN_LAT + " REAL NOT NULL, " +
                AccidentEntry.COLUMN_LNG + " REAL NOT NULL, " +
                AccidentEntry.COLUMN_REGION_ID + " INTEGER NOT NULL, " +
                AccidentEntry.COLUMN_LOCALITY_ID + " INTEGER NOT NULL, " +
                AccidentEntry.COLUMN_ELECTIONS_ID + " INTEGER NOT NULL, " +
                AccidentEntry.COLUMN_OFFENDER_PARTY_ID + " INTEGER NOT NULL, " +
                "FOREIGN KEY (" + AccidentEntry.COLUMN_REGION_ID + ") REFERENCES " +
                RegionEntry.TABLE_NAME + " (" + RegionEntry._ID + "), " +
                "FOREIGN KEY (" + AccidentEntry.COLUMN_LOCALITY_ID + ") REFERENCES " +
                LocalityEntry.TABLE_NAME + " (" + LocalityEntry._ID + "), " +
                "FOREIGN KEY (" + AccidentEntry.COLUMN_ELECTIONS_ID + ") REFERENCES " +
                ElectionTypeEntry.TABLE_NAME + " (" + ElectionTypeEntry._ID + "), " +
                "FOREIGN KEY (" + AccidentEntry.COLUMN_OFFENDER_PARTY_ID + ") REFERENCES " +
                PartyEntry.TABLE_NAME + " (" + PartyEntry._ID + "));";

        sqLiteDatabase.execSQL(SQL_CREATE_ACCIDENTS_TYPES_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_ACCIDENTS_SUBTYPES_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_REGIONS_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_LOCALITIES_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_PARTIES_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_ELECTIONS_TYPES_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_ACCIDENTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + AccidentTypeEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + AccidentSubtypeEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + RegionEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + LocalityEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + PartyEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ElectionTypeEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + AccidentEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
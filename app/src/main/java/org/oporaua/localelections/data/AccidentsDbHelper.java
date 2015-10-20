/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.oporaua.localelections.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static org.oporaua.localelections.data.AccidentsContract.AccidentEntry;


public class AccidentsDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    public static final String DATABASE_NAME = "accidents.db";

    public AccidentsDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_ACCIDENTS_TABLE = "CREATE TABLE " + AccidentEntry.TABLE_NAME + " (" +
                AccidentEntry._ID + " INTEGER PRIMARY KEY," +
                AccidentEntry.COLUMN_DATE_TEXT + " TEXT NOT NULL, " +
                AccidentEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                AccidentEntry.COLUMN_SOURCE + " TEXT NOT NULL, " +
                AccidentEntry.COLUMN_EVIDENCE_URL + " TEXT, " +
                AccidentEntry.COLUMN_LAT + " REAL NOT NULL, " +
                AccidentEntry.COLUMN_LNG + " REAL NOT NULL, " +
                AccidentEntry.COLUMN_REGION_ID + " INTEGER NOT NULL, " +
                AccidentEntry.COLUMN_LOCALITY_ID + " INTEGER NOT NULL, " +
                AccidentEntry.COLUMN_ELECTIONS_ID + " INTEGER NOT NULL, " +
                AccidentEntry.COLUMN_OFFENDER_PARTY_ID + " INTEGER NOT NULL);";

        sqLiteDatabase.execSQL(SQL_CREATE_ACCIDENTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + AccidentEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
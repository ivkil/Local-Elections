package org.oporaua.localelections.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;

import org.oporaua.localelections.data.OporaContract.AccidentEntry;
import org.oporaua.localelections.data.OporaContract.AccidentSubtypeEntry;
import org.oporaua.localelections.data.OporaContract.AccidentTypeEntry;
import org.oporaua.localelections.data.OporaContract.ElectionTypeEntry;
import org.oporaua.localelections.data.OporaContract.LocalityEntry;
import org.oporaua.localelections.data.OporaContract.PartyEntry;
import org.oporaua.localelections.data.OporaContract.RegionEntry;

@SuppressWarnings("ConstantConditions")
public class OporaProvider extends ContentProvider {

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private OporaDbHelper mOpenHelper;

    private static final int ACCIDENTS_TYPES = 100;
    private static final int ACCIDENTS_SUBTYPES = 200;
    private static final int REGIONS = 300;
    private static final int LOCALITIES = 400;
    private static final int PARTIES = 500;
    private static final int ELECTIONS_TYPES = 600;
    private static final int ACCIDENTS = 700;
    private static final int ACCIDENT_WITH_DATA = 701;
    private static final int ACCIDENT_WITH_TYPE = 702;

    private static final SQLiteQueryBuilder sAccidentsWithDataQueryBuilder;
    private static final SQLiteQueryBuilder sAccidentsWithTypeQueryBuilder;

    static {
        sAccidentsWithDataQueryBuilder = new SQLiteQueryBuilder();
        sAccidentsWithDataQueryBuilder.setTables(
                AccidentEntry.TABLE_NAME + " LEFT OUTER JOIN " +
                        RegionEntry.TABLE_NAME + " ON " +
                        AccidentEntry.TABLE_NAME + "." + AccidentEntry.COLUMN_REGION_ID +
                        " = " + RegionEntry.TABLE_NAME + "." + RegionEntry._ID +
                        " LEFT OUTER JOIN " +
                        LocalityEntry.TABLE_NAME + " ON " +
                        AccidentEntry.TABLE_NAME + "." + AccidentEntry.COLUMN_LOCALITY_ID +
                        " = " + LocalityEntry.TABLE_NAME + "." + LocalityEntry._ID +
                        " LEFT OUTER JOIN " +
                        ElectionTypeEntry.TABLE_NAME + " ON " +
                        AccidentEntry.TABLE_NAME + "." + AccidentEntry.COLUMN_ELECTIONS_ID +
                        " = " + ElectionTypeEntry.TABLE_NAME + "." + ElectionTypeEntry._ID +
                        " LEFT OUTER JOIN " +
                        PartyEntry.TABLE_NAME + " ON " +
                        AccidentEntry.TABLE_NAME + "." + AccidentEntry.COLUMN_OFFENDER_PARTY_ID +
                        " = " + PartyEntry.TABLE_NAME + "." + PartyEntry._ID);
    }

    static {
        sAccidentsWithTypeQueryBuilder = new SQLiteQueryBuilder();
        sAccidentsWithTypeQueryBuilder.setTables(
                AccidentEntry.TABLE_NAME + " LEFT OUTER JOIN " +
                        AccidentSubtypeEntry.TABLE_NAME + " ON " +
                        AccidentEntry.TABLE_NAME + "." + AccidentEntry.COLUMN_ACCIDENT_SUBTYPE +
                        " = " + AccidentSubtypeEntry.TABLE_NAME + "." + AccidentSubtypeEntry._ID);
    }

    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = OporaContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, OporaContract.PATH_ACCIDENTS_TYPES, ACCIDENTS_TYPES);
        matcher.addURI(authority, OporaContract.PATH_ACCIDENTS_SUBTYPES, ACCIDENTS_SUBTYPES);
        matcher.addURI(authority, OporaContract.PATH_REGIONS, REGIONS);
        matcher.addURI(authority, OporaContract.PATH_LOCALITIES, LOCALITIES);
        matcher.addURI(authority, OporaContract.PATH_PARTIES, PARTIES);
        matcher.addURI(authority, OporaContract.PATH_ELECTIONS_TYPES, ELECTIONS_TYPES);

        matcher.addURI(authority, OporaContract.PATH_ACCIDENTS, ACCIDENTS);
        matcher.addURI(authority, OporaContract.PATH_ACCIDENTS + "/#", ACCIDENT_WITH_DATA);
        matcher.addURI(authority, OporaContract.PATH_ACCIDENTS + "/*", ACCIDENT_WITH_TYPE);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new OporaDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            case ACCIDENTS: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        AccidentEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case ACCIDENT_WITH_DATA: {
                retCursor = sAccidentsWithDataQueryBuilder.query(
                        mOpenHelper.getReadableDatabase(),
                        projection,
                        AccidentEntry.TABLE_NAME + "." + AccidentEntry._ID + " = '" + ContentUris.parseId(uri) + "'",
                        null,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case ACCIDENT_WITH_TYPE: {
                retCursor = sAccidentsWithTypeQueryBuilder.query(
                        mOpenHelper.getReadableDatabase(),
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case REGIONS: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        RegionEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case ACCIDENTS_TYPES: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        AccidentTypeEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public String getType(@NonNull Uri uri) {

        final int match = sUriMatcher.match(uri);

        switch (match) {
            case ACCIDENTS:
                return AccidentEntry.CONTENT_TYPE;
            case ACCIDENT_WITH_TYPE:
                return AccidentEntry.CONTENT_TYPE;
            case ACCIDENT_WITH_DATA:
                return AccidentEntry.CONTENT_ITEM_TYPE;
            case REGIONS:
                return RegionEntry.CONTENT_TYPE;
            case ACCIDENTS_TYPES:
                return AccidentTypeEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case ACCIDENTS_TYPES: {
                long _id = db.insert(AccidentTypeEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = AccidentTypeEntry.buildAccidentTypeUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case ACCIDENTS_SUBTYPES: {
                long _id = db.insert(AccidentSubtypeEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = AccidentSubtypeEntry.buildAccidentSubtypeUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case REGIONS: {
                long _id = db.insert(RegionEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = RegionEntry.buildRegionUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case LOCALITIES: {
                long _id = db.insert(LocalityEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = LocalityEntry.buildLocalityUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case PARTIES: {
                long _id = db.insert(PartyEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = PartyEntry.buildPartyUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case ELECTIONS_TYPES: {
                long _id = db.insert(ElectionTypeEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = ElectionTypeEntry.buildElectionsTypeUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case ACCIDENTS: {
                long _id = db.insert(AccidentEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = AccidentEntry.buildAccidentUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        switch (match) {
            case ACCIDENTS:
                rowsDeleted = db.delete(
                        AccidentEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (selection == null || rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(
            @NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case ACCIDENTS:
                rowsUpdated = db.update(AccidentEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        String tableName;
        switch (match) {
            case ACCIDENTS_TYPES:
                tableName = AccidentTypeEntry.TABLE_NAME;
                break;
            case ACCIDENTS_SUBTYPES:
                tableName = AccidentSubtypeEntry.TABLE_NAME;
                break;
            case REGIONS:
                tableName = RegionEntry.TABLE_NAME;
                break;
            case LOCALITIES:
                tableName = LocalityEntry.TABLE_NAME;
                break;
            case PARTIES:
                tableName = PartyEntry.TABLE_NAME;
                break;
            case ELECTIONS_TYPES:
                tableName = ElectionTypeEntry.TABLE_NAME;
                break;
            case ACCIDENTS:
                tableName = AccidentEntry.TABLE_NAME;
                break;
            default:
                return super.bulkInsert(uri, values);
        }

        db.beginTransaction();
        int returnCount = 0;
        try {
            for (ContentValues value : values) {
                long _id = db.insert(tableName, null, value);
                if (_id != -1) {
                    returnCount++;
                }
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnCount;

    }

}

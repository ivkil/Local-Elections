package org.oporaua.localelections.data;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class OporaContract {

    public static final String CONTENT_AUTHORITY = "org.oporaua.localelections";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_ACCIDENTS_TYPES = "accident_types";
    public static final String PATH_ACCIDENTS_SUBTYPES = "accident_subtypes";
    public static final String PATH_REGIONS = "regions";
    public static final String PATH_LOCALITIES = "localities";
    public static final String PATH_PARTIES = "parties";
    public static final String PATH_ELECTIONS_TYPES = "elections_types";
    public static final String PATH_ACCIDENTS = "accidents";

    public static final String DATE_FORMAT = "yyyyMMdd";

    public static String getDbDateString(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, new Locale("ua"));
        return sdf.format(date);
    }

    public static Date getDateFromDb(String dateText) {
        SimpleDateFormat dbDateFormat = new SimpleDateFormat(DATE_FORMAT, new Locale("ua"));
        try {
            return dbDateFormat.parse(dateText);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static final class AccidentTypeEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_ACCIDENTS_TYPES).build();

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_ACCIDENTS_TYPES;
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_ACCIDENTS_TYPES;

        public static final String TABLE_NAME = "accident_types";

        public static final String COLUMN_TITLE = "title";

        public static Uri buildAccidentTypeUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

    }

    public static final class AccidentSubtypeEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_ACCIDENTS_SUBTYPES).build();

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_ACCIDENTS_SUBTYPES;
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_ACCIDENTS_SUBTYPES;

        public static final String TABLE_NAME = "accident_subtypes";

        public static final String COLUMN_TITLE = "title";

        public static Uri buildAccidentSubtypeUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

    }

    public static final class RegionEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_REGIONS).build();

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_REGIONS;
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_REGIONS;

        public static final String TABLE_NAME = "regions";

        public static final String COLUMN_TITLE = "title";

        public static Uri buildRegionUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

    }

    public static final class LocalityEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_LOCALITIES).build();

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_LOCALITIES;
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_LOCALITIES;

        public static final String TABLE_NAME = "localities";

        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_REGION_ID = "region_id";
        public static final String COLUMN_DISTRICT = "district";

        public static Uri buildLocalityUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

    }

    public static final class PartyEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_PARTIES).build();

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_PARTIES;
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_PARTIES;

        public static final String TABLE_NAME = "parties";

        public static final String COLUMN_TITLE = "title";

        public static Uri buildPartyUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class ElectionTypeEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_ELECTIONS_TYPES).build();

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_ELECTIONS_TYPES;
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_ELECTIONS_TYPES;

        public static final String TABLE_NAME = "elections_types";

        public static final String COLUMN_TITLE = "title";

        public static Uri buildElectionsTypeUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

    }

    public static final class AccidentEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_ACCIDENTS).build();

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_ACCIDENTS;
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_ACCIDENTS;

        public static final String TABLE_NAME = "accidents";

        public static final String COLUMN_DATE_TEXT = "date";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_SOURCE = "source";
        public static final String COLUMN_EVIDENCE_URL = "evidence_url";
        public static final String COLUMN_LAT = "latitude";
        public static final String COLUMN_LNG = "longitude";
        public static final String COLUMN_REGION_ID = "region_id";
        public static final String COLUMN_LOCALITY_ID = "locality_id";
        public static final String COLUMN_ELECTIONS_ID = "elections_id";
        public static final String COLUMN_OFFENDER_PARTY_ID = "offender_party_id";

        public static Uri buildAccidentUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

}
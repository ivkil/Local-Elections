package org.oporaua.localelections.data;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class AccidentsContract {

    public static final String CONTENT_AUTHORITY = "org.oporaua.localelections";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_ACCIDENTS = "accidents";
    public static final String PATH_PARTIES = "parties";

    public static final String DATE_FORMAT = "yyyyMMdd";


    public static String getDbDateString(Date date){
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, new Locale("ua"));
        return sdf.format(date);
    }

    public static Date getDateFromDb(String dateText) {
        SimpleDateFormat dbDateFormat = new SimpleDateFormat(DATE_FORMAT, new Locale("ua"));
        try {
            return dbDateFormat.parse(dateText);
        } catch ( ParseException e ) {
            e.printStackTrace();
            return null;
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
}
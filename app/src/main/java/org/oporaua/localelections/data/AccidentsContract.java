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
        public static final String COLUMN_SOURCE = "source";


        public static Uri buildAccidentUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}
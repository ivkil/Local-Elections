package org.oporaua.localelections;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.support.annotation.Nullable;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.widget.ThemedSpinnerAdapter;


public class FilterSpinnerAdapter extends SimpleCursorAdapter implements ThemedSpinnerAdapter {

    private final ThemedSpinnerAdapter.Helper mDropDownHelper;

    public FilterSpinnerAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
        mDropDownHelper = new ThemedSpinnerAdapter.Helper(context);
    }

    @Override
    public void setDropDownViewTheme(Resources.Theme theme) {
        mDropDownHelper.setDropDownViewTheme(theme);
    }

    @Nullable
    @Override
    public Resources.Theme getDropDownViewTheme() {
        return mDropDownHelper.getDropDownViewTheme();
    }

}

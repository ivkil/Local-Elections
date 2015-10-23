package org.oporaua.localelections.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.StringRes;

import org.oporaua.localelections.R;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public final class PrefUtil {

    private static final String SAVED_ACCIDENTS_TYPES = "accidents_types";
    private static final String SAVED_ACCIDENTS_SUBTYPES = "accidents_subtypes";
    private static final String SAVED_REGIONS = "regions";
    private static final String SAVED_LOCALITIES = "localities";
    private static final String SAVED_PARTIES = "parties";
    private static final String SAVED_ELECTIONS_TYPES = "elections_types";

    private static PrefUtil sInstance;

    private Context mContext;

    private PrefUtil(Context context) {
        this.mContext = context;
    }

    public static void initialize(Context context) {
        if (sInstance != null) {
            throw new IllegalStateException("AppPrefs have already been initialized");
        }
        sInstance = new PrefUtil(context);
    }

    public static PrefUtil getInstance() {
        if (sInstance == null) {
            throw new IllegalStateException("AppPrefs should be initialized first");
        }
        return sInstance;
    }

    private static String getString(@StringRes int resourceId) {
        return getInstance().mContext.getString(resourceId);
    }

    private SharedPreferences getPrefs() {
        return PreferenceManager.getDefaultSharedPreferences(mContext);
    }

    public boolean isAccidentsTypes() {
        return getPrefs().getBoolean(SAVED_ACCIDENTS_TYPES, false);
    }

    public void setAccidentsTypes(boolean accidentsTypes) {
        getPrefs().edit().putBoolean(SAVED_ACCIDENTS_TYPES, accidentsTypes).apply();
    }

    public boolean isAccidentsSubtypes() {
        return getPrefs().getBoolean(SAVED_ACCIDENTS_SUBTYPES, false);
    }

    public void setAccidentsSubtypes(boolean accidentsSubtypes) {
        getPrefs().edit().putBoolean(SAVED_ACCIDENTS_SUBTYPES, accidentsSubtypes).apply();
    }

    public boolean isRegions() {
        return getPrefs().getBoolean(SAVED_REGIONS, false);
    }

    public void setRegions(boolean regions) {
        getPrefs().edit().putBoolean(SAVED_REGIONS, regions).apply();
    }

    public boolean isLocalities() {
        return getPrefs().getBoolean(SAVED_LOCALITIES, false);
    }

    public void setLocalities(boolean localities) {
        getPrefs().edit().putBoolean(SAVED_LOCALITIES, localities).apply();
    }

    public boolean isParties() {
        return getPrefs().getBoolean(SAVED_PARTIES, false);
    }

    public void setParties(boolean parties) {
        getPrefs().edit().putBoolean(SAVED_PARTIES, parties).apply();
    }

    public boolean isElectionsTypes() {
        return getPrefs().getBoolean(SAVED_ELECTIONS_TYPES, false);
    }

    public void setElectionsTypes(boolean electionsTypes) {
        getPrefs().edit().putBoolean(SAVED_ELECTIONS_TYPES, electionsTypes).apply();
    }

    public static Set<String> getRegionSubscribeIds() {
        return getInstance().getPersistentObjectSet(getString(R.string.pref_regions_key));
    }

    private Set<String> getPersistentObjectSet(String key) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            return getPrefs().getStringSet(key, null);
        } else {
            String s = getPrefs().getString(key, null);
            if (s != null) return new HashSet<>(Arrays.asList(s.split(",")));
            else return null;
        }
    }

}
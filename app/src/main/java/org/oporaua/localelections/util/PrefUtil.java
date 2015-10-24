package org.oporaua.localelections.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
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

    private SharedPreferences mSharedPreferences;

    private PrefUtil(Context context) {
        this.mContext = context;
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
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

    private static SharedPreferences getPrefs() {
        return getInstance().mSharedPreferences;
    }

    private static SharedPreferences.Editor getEditor() {
        return getPrefs().edit();
    }

    public static boolean isAccidentsTypes() {
        return getPrefs().getBoolean(SAVED_ACCIDENTS_TYPES, false);
    }

    public static void setAccidentsTypes(boolean accidentsTypes) {
        getEditor().putBoolean(SAVED_ACCIDENTS_TYPES, accidentsTypes).apply();
    }

    public static boolean isAccidentsSubtypes() {
        return getPrefs().getBoolean(SAVED_ACCIDENTS_SUBTYPES, false);
    }

    public static void setAccidentsSubtypes(boolean accidentsSubtypes) {
        getEditor().putBoolean(SAVED_ACCIDENTS_SUBTYPES, accidentsSubtypes).apply();
    }

    public static boolean isRegions() {
        return getPrefs().getBoolean(SAVED_REGIONS, false);
    }

    public static void setRegions(boolean regions) {
        getEditor().putBoolean(SAVED_REGIONS, regions).apply();
    }

    public static boolean isLocalities() {
        return getPrefs().getBoolean(SAVED_LOCALITIES, false);
    }

    public static void setLocalities(boolean localities) {
        getEditor().putBoolean(SAVED_LOCALITIES, localities).apply();
    }

    public static boolean isParties() {
        return getPrefs().getBoolean(SAVED_PARTIES, false);
    }

    public static void setParties(boolean parties) {
        getEditor().putBoolean(SAVED_PARTIES, parties).apply();
    }

    public static boolean isElectionsTypes() {
        return getPrefs().getBoolean(SAVED_ELECTIONS_TYPES, false);
    }

    public static void setElectionsTypes(boolean electionsTypes) {
        getEditor().putBoolean(SAVED_ELECTIONS_TYPES, electionsTypes).apply();
    }

    public static Set<String> getRegionSubscribeIds() {
        return getInstance().getPersistentObjectSet(getString(R.string.pref_regions_key));
    }

    @Nullable
    private Set<String> getPersistentObjectSet(String key) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            return mSharedPreferences.getStringSet(key, null);
        } else {
            String s = mSharedPreferences.getString(key, null);
            if (s != null) return new HashSet<>(Arrays.asList(s.split(",")));
            else return null;
        }
    }

}
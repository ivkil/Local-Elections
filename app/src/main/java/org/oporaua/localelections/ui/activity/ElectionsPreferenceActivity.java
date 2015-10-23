package org.oporaua.localelections.ui.activity;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.util.Log;

import org.oporaua.localelections.R;
import org.oporaua.localelections.util.PrefUtil;

import java.util.Set;


public class ElectionsPreferenceActivity extends PreferenceActivity {
//        implements Preference.OnPreferenceChangeListener {

//    private boolean mBindingPreference;

    @SuppressWarnings("deprecation")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
//        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_location_key)));
//        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_units_key)));

        Set<String> selections = PrefUtil.getRegionSubscribeIds();

        String[] selected = new String[0];
        if (selections != null) {
            selected = selections.toArray(new String[selections.size()]);
        }
        for (String s : selected) {
            Log.d("TAG", s);
        }
//        Toast.makeText(this, selected[all], Toast.LENGTH_LONG).show();
    }

//
//    private void bindPreferenceSummaryToValue(Preference preference) {
//
//        mBindingPreference = true;
//
//        preference.setOnPreferenceChangeListener(this);
//
//        onPreferenceChange(preference,
//                PreferenceManager
//                        .getDefaultSharedPreferences(preference.getContext())
//                        .getString(preference.getKey(), ""));
//
//        mBindingPreference = false;
//    }
//
//    @Override
//    public boolean onPreferenceChange(Preference preference, Object value) {
//        String stringValue = value.toString();
//
//        if ( !mBindingPreference ) {
//            if (preference.getKey().equals(getString(R.string.pref_location_key))) {
//                SunshineSyncAdapter.syncImmediately(this);
//            } else {
//                getContentResolver().notifyChange(WeatherContract.WeatherEntry.CONTENT_URI, null);
//            }
//        }
//
//        if (preference instanceof ListPreference) {
//            ListPreference listPreference = (ListPreference) preference;
//            int prefIndex = listPreference.findIndexOfValue(stringValue);
//            if (prefIndex >= 0) {
//                preference.setSummary(listPreference.getEntries()[prefIndex]);
//            }
//        } else {
//            preference.setSummary(stringValue);
//        }
//        return true;
//    }

}

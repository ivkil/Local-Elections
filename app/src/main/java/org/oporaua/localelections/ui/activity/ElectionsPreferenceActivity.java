package org.oporaua.localelections.ui.activity;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import org.oporaua.localelections.R;


public class ElectionsPreferenceActivity extends PreferenceActivity {

    @SuppressWarnings("deprecation")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

//        Set<String> selections = PrefUtil.getRegionSubscribeIds();

//        String[] selected = new String[0];
//        if (selections != null) {
//            selected = selections.toArray(new String[selections.size()]);
//        }
//        for (String s : selected) {
//            Log.d("TAG", s);
//        }
    }

}

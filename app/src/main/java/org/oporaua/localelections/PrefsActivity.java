package org.oporaua.localelections;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class PrefsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prefs);
        getSupportFragmentManager().beginTransaction().
                replace(R.id.container, new PrefsFragment()).commit();
    }
}

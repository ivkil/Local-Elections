package org.oporaua.localelections;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import org.oporaua.localelections.interfaces.SetToolbarListener;

public class WebViewActivity extends AppCompatActivity implements SetToolbarListener {

    private static final String ARG_FILE_URI = "file_uri";

    public static Intent getCallingIntent(Context context, String fileUri) {
        Intent intent = new Intent(context, WebViewActivity.class);
        intent.putExtra(ARG_FILE_URI, fileUri);
        return intent;
    }

    private String mFileUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        processIntent();
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.container,
                    WebViewFragment.newInstance(mFileUri, false)).commit();
        }
    }

    private void processIntent() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.containsKey(ARG_FILE_URI)) {
            mFileUri = bundle.getString(ARG_FILE_URI);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @SuppressWarnings("ConstantConditions")
    @Override
    public void onSetToolbar(Toolbar toolbar) {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

}
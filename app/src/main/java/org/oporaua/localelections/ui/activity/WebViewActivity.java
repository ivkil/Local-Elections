package org.oporaua.localelections.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import org.oporaua.localelections.R;
import org.oporaua.localelections.interfaces.SetToolbarListener;
import org.oporaua.localelections.ui.fragment.WebViewFragment;

public class WebViewActivity extends AppCompatActivity implements SetToolbarListener {

    public static final String ARG_FILE_URL = "file_url";
    public static final String ARG_PRINT_ENABLED = "print_url";

    public static Intent getCallingIntent(Context context, String fileUri, boolean printEnabled) {
        Intent intent = new Intent(context, WebViewActivity.class);
        intent.putExtra(ARG_FILE_URL, fileUri);
        intent.putExtra(ARG_PRINT_ENABLED, printEnabled);
        return intent;
    }

    private String mFileUrl;
    private boolean mPrintEnabled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        processIntent();
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.container,
                    WebViewFragment.newInstance(mFileUrl, false, mPrintEnabled)).commit();
        }
    }

    private void processIntent() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.containsKey(ARG_FILE_URL)) {
            mFileUrl = bundle.getString(ARG_FILE_URL);
            mPrintEnabled = bundle.getBoolean(ARG_PRINT_ENABLED);
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

package org.oporaua.localelections;


import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView.OnCloseListener;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import org.oporaua.localelections.interfaces.SetToolbarListener;
import org.oporaua.localelections.util.Constants;
import org.oporaua.localelections.util.GeneralUtil;

import butterknife.Bind;
import butterknife.ButterKnife;


public class WebViewFragment extends Fragment implements OnQueryTextListener, OnCloseListener,
        OnClickListener, OnEditorActionListener {

    private static final int INIT_POSITION = 0;

    private static final String ARG_PATH = "path";
    private static final String ARG_SEARCH_ENABLE = "enable";
    private static final String ARG_PRINT_ENABLE = "print";

    @Bind(R.id.webView)
    WebView mWebView;

    @Bind(R.id.pb_progress)
    ProgressBar mProgressBar;

    @Bind(R.id.app_toolbar)
    Toolbar mToolbar;

    private String mPath;
    private boolean mSearchEnabled;
    private boolean mPrintEnable;

    private EditText mSearchEditText;
    private TextView mIndicatorTextView;

    public static WebViewFragment newInstance(String path, boolean searchEnabled, boolean printEnable) {
        WebViewFragment fragment = new WebViewFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PATH, path);
        args.putBoolean(ARG_SEARCH_ENABLE, searchEnabled);
        args.putBoolean(ARG_PRINT_ENABLE, printEnable);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mPath = getArguments().getString(ARG_PATH);
        mSearchEnabled = getArguments().getBoolean(ARG_SEARCH_ENABLE);
        mPrintEnable = getArguments().getBoolean(ARG_PRINT_ENABLE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_web_view, container, false);
        ButterKnife.bind(this, view);
        if (savedInstanceState == null) {
            mWebView.loadUrl(mPath);
        } else {
            mWebView.restoreState(savedInstanceState);
        }
        mWebView.setWebViewClient(new MyWebViewClient());
        mWebView.getSettings().setBuiltInZoomControls(true);

        if (mSearchEnabled) {
            configureSearch();
        } else {
            mToolbar.findViewById(R.id.root_search_custom).setVisibility(View.GONE);
            mToolbar.findViewById(R.id.root_search_normal).setVisibility(View.GONE);
        }

        if (getActivity() instanceof SetToolbarListener) {
            ((SetToolbarListener) getActivity()).onSetToolbar(mToolbar);
        }

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (mPrintEnable) {
            inflater.inflate(R.menu.menu_web_view_print, menu);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_print:
                createWebPrintJob();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void createWebPrintJob() {
        if (isKitkat()) {
            PrintManager printManager = (PrintManager) getActivity().getSystemService(Context.PRINT_SERVICE);
            PrintDocumentAdapter printAdapter = mWebView.createPrintDocumentAdapter();
            String jobName = getString(R.string.app_name) + " Document";
            printManager.print(jobName, printAdapter, new PrintAttributes.Builder().build());
        }
    }

    private boolean isKitkat() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem item = menu.findItem(R.id.action_print);
        if (!isKitkat() && item != null) {
            item.setVisible(false);
        }
    }

    private void configureSearch() {
        ButterKnife.findById(mToolbar, R.id.ib_close).setOnClickListener(this);
        ButterKnife.findById(mToolbar, R.id.ib_down).setOnClickListener(this);
        ButterKnife.findById(mToolbar, R.id.ib_up).setOnClickListener(this);
        ButterKnife.findById(mToolbar, R.id.ib_scroll_up).setOnClickListener(this);
        ButterKnife.findById(mToolbar, R.id.ib_search).setOnClickListener(this);

        mSearchEditText = ButterKnife.findById(mToolbar, R.id.et_search);
        mSearchEditText.addTextChangedListener(new SearchTextWatcher());
        mSearchEditText.setOnEditorActionListener(this);

        mIndicatorTextView = ButterKnife.findById(mToolbar, R.id.tv_indicator);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            mWebView.setFindListener(new WebView.FindListener() {
                @Override
                public void onFindResultReceived(int activeMatchOrdinal, int numberOfMatches, boolean isDoneCounting) {
                    if (!isDoneCounting) return;
                    String result;
                    if (numberOfMatches != 0) {
                        mIndicatorTextView.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
                        result = String.format("%d/%d", ++activeMatchOrdinal,
                                numberOfMatches);

                    } else {
                        if (!TextUtils.isEmpty(mSearchEditText.getText())) {
                            mIndicatorTextView.setTextColor(ContextCompat.getColor(getActivity(), R.color.accent));
                            result = getString(R.string.find_indicator_empty);
                        } else {
                            result = "";
                        }
                    }
                    if (numberOfMatches > 1) {
                        enabledNavigation(true);
                    } else {
                        enabledNavigation(false);
                    }
                    mIndicatorTextView.setText(result);
                }
            });
        } else {
            mIndicatorTextView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ib_close:
                GeneralUtil.hideKeyBoard(getActivity(), mSearchEditText);
                mWebView.clearMatches();
                showSearchToolbar(false);
                enabledNavigation(false);
                break;
            case R.id.ib_search:
                showSearchToolbar(true);
                GeneralUtil.showKeyBoard(getActivity(), mSearchEditText);
                mSearchEditText.selectAll();
                mIndicatorTextView.setText("");
                enabledNavigation(false);
                break;
            case R.id.ib_scroll_up:
                GeneralUtil.hideKeyBoard(getActivity(), mSearchEditText);
                scrollUp();
                break;
            case R.id.ib_up:
                GeneralUtil.hideKeyBoard(getActivity(), mSearchEditText);
                mWebView.findNext(false);
                break;
            case R.id.ib_down:
                GeneralUtil.hideKeyBoard(getActivity(), mSearchEditText);
                mWebView.findNext(true);
                break;
        }
    }

    @SuppressWarnings("ConstantConditions")
    private void showSearchToolbar(boolean show) {
        mToolbar.findViewById(R.id.root_search_custom).setVisibility(show ? View.VISIBLE : View.GONE);
        mToolbar.findViewById(R.id.root_search_normal).setVisibility(show ? View.GONE : View.VISIBLE);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(!show);
    }

    private void enabledNavigation(boolean enabled) {
        ImageButton buttonDown = ButterKnife.findById(mToolbar, R.id.ib_down);
        buttonDown.setClickable(enabled);
        buttonDown.setEnabled(enabled);
        buttonDown.setColorFilter(ContextCompat.getColor(getActivity(),
                enabled ? R.color.white : R.color.gray));
        ImageButton buttonUp = ButterKnife.findById(mToolbar, R.id.ib_up);
        buttonUp.setClickable(enabled);
        buttonUp.setEnabled(enabled);
        buttonUp.setColorFilter(ContextCompat.getColor(getActivity(),
                enabled ? R.color.white : R.color.gray));
    }

    @SuppressWarnings("deprecation")
    private void findInPage(String s) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            mWebView.findAllAsync(s);
        } else {
            mWebView.findAll(s);
        }
    }

    private class SearchTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            findInPage(s.toString());
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Uri uri = Uri.parse(url);
            if (uri.getScheme().equalsIgnoreCase(Constants.LAW_SCHEME)) {
                String fragment = uri.getAuthority();
                Intent intent = new Intent(getActivity(), WebViewActivity.class);
                url = Uri.parse(Constants.LAW_PATH)
                        .buildUpon()
                        .encodedFragment(fragment)
                        .build().toString();
                intent.putExtra(WebViewActivity.ARG_FILE_URL, url);
                startActivity(intent);
            }
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            mProgressBar.setVisibility(View.GONE);
        }
    }

    private void scrollUp() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
            ObjectAnimator anim = ObjectAnimator.ofInt(mWebView, "scrollY",
                    mWebView.getScrollY(), INIT_POSITION);
            anim.setDuration(400);
            anim.start();
        } else {
            mWebView.scrollTo(INIT_POSITION, INIT_POSITION);
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return true;
    }

    @Override
    public boolean onClose() {
        return false;
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            findInPage(mSearchEditText.getText().toString());
            GeneralUtil.hideKeyBoard(getActivity(), mSearchEditText);
        }
        return false;
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        mWebView.saveState(outState);
        super.onSaveInstanceState(outState);
    }

}

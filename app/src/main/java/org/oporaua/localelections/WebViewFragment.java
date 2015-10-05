package org.oporaua.localelections;


import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView.OnCloseListener;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.oporaua.localelections.interfaces.SetToolbarListener;
import org.oporaua.localelections.util.GeneralUtil;

import butterknife.Bind;
import butterknife.ButterKnife;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class WebViewFragment extends Fragment implements OnQueryTextListener, OnCloseListener,
        OnClickListener, WebView.FindListener {

    private static final int INIT_POSITION = 0;

    private static final String ARG_PATH = "path";
    private static final String ARG_SEARCH_ENABLE = "enable";

    @Bind(R.id.webView)
    WebView mWebView;

    @Bind(R.id.pb_progress)
    ProgressBar mProgressBar;

    @Bind(R.id.app_toolbar)
    Toolbar mToolbar;

    private String mPath;
    private boolean mSearchEnabled;

    private EditText mSearchEditText;
    private TextView mIndicatorTextView;

    public static WebViewFragment newInstance(String path, boolean searchEnabled) {
        WebViewFragment fragment = new WebViewFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PATH, path);
        args.putBoolean(ARG_SEARCH_ENABLE, searchEnabled);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPath = getArguments().getString(ARG_PATH);
        mSearchEnabled = getArguments().getBoolean(ARG_SEARCH_ENABLE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_web_view, container, false);
        ButterKnife.bind(this, view);
        mWebView.loadUrl(mPath);
        mWebView.setWebViewClient(new MyWebViewClient());

        if (mSearchEnabled) {

            ButterKnife.findById(mToolbar, R.id.ib_close).setOnClickListener(this);
            ButterKnife.findById(mToolbar, R.id.ib_down).setOnClickListener(this);
            ButterKnife.findById(mToolbar, R.id.ib_up).setOnClickListener(this);
            ButterKnife.findById(mToolbar, R.id.ib_scroll_up).setOnClickListener(this);
            ButterKnife.findById(mToolbar, R.id.ib_search).setOnClickListener(this);

            mSearchEditText = ButterKnife.findById(mToolbar, R.id.et_search);
            mSearchEditText.addTextChangedListener(new SearchTextWatcher());

            mIndicatorTextView = ButterKnife.findById(mToolbar, R.id.tv_indicator);

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                mWebView.setFindListener(this);
            } else {
                mIndicatorTextView.setVisibility(View.GONE);
            }
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ib_close:
                onCloseClick();
                break;
            case R.id.ib_search:
                showSearchToolbar(true);
                GeneralUtil.showKeyBoard(getActivity(), mSearchEditText);
                mSearchEditText.selectAll();
                break;
            case R.id.ib_scroll_up:
                scrollUp();
                break;
            case R.id.ib_up:
                mWebView.findNext(false);
                break;
            case R.id.ib_down:
                mWebView.findNext(true);
                break;
        }
    }

    private void onCloseClick() {
        mWebView.clearMatches();
        GeneralUtil.hideKeyBoard(getActivity(), mSearchEditText);
        showSearchToolbar(false);
    }

    @SuppressWarnings("ConstantConditions")
    private void showSearchToolbar(boolean show) {
        mToolbar.findViewById(R.id.root_search_custom).setVisibility(show ? View.VISIBLE : View.GONE);
        mToolbar.findViewById(R.id.root_search_normal).setVisibility(show ? View.GONE : View.VISIBLE);
        mIndicatorTextView.setText(getString(R.string.find_indicator_default));
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(!show);
    }

    @Override
    public void onFindResultReceived(int activeMatchOrdinal, int numberOfMatches,
                                     boolean isDoneCounting) {
        if (!isDoneCounting) return;
        String result;
        if (numberOfMatches == 0) {
            result = getString(R.string.find_indicator_default);
        } else {
            result = String.format("%d/%d", ++activeMatchOrdinal,
                    numberOfMatches);
        }
        mIndicatorTextView.setText(result);
    }

    private class SearchTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @SuppressWarnings("deprecation")
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                mWebView.findAllAsync(s.toString());
            } else {
                mWebView.findAll(s.toString());
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return false;
        }

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

}

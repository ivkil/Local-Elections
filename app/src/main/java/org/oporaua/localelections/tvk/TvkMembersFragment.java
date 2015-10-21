package org.oporaua.localelections.tvk;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.okhttp.OkHttpClient;

import org.oporaua.localelections.R;
import org.oporaua.localelections.interfaces.SetToolbarListener;
import org.oporaua.localelections.util.GeneralUtil;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class TvkMembersFragment extends Fragment implements SearchView.OnQueryTextListener, SearchView.OnCloseListener {

    private static final String TVK_OPORA_BASE_URL = "http://tvk.oporaua.org/";

    @Bind(R.id.rv_tvk_members)
    RecyclerView mTvkMembersRecyclerView;

    private TvkService mTvkService;
    private TvkMemberAdapter mTvkMemberAdapter;
    private Subscription mMembersSubscription;
    private SearchView mSearchView;

    public static TvkMembersFragment newInstance() {
        return new TvkMembersFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tvk_members, container, false);
        ButterKnife.bind(this, view);
        if (getActivity() instanceof SetToolbarListener) {
            Toolbar toolbar = ButterKnife.findById(view, R.id.app_toolbar);
            ((SetToolbarListener) getActivity()).onSetToolbar(toolbar);
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(TVK_OPORA_BASE_URL)
                .client(new OkHttpClient())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();

        mTvkService = retrofit.create(TvkService.class);

        mTvkMembersRecyclerView.setHasFixedSize(true);
        mTvkMembersRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mTvkMemberAdapter = new TvkMemberAdapter(getActivity());
        mTvkMembersRecyclerView.setAdapter(mTvkMemberAdapter);

        loadMembers("Боярин Юрій");

        return view;
    }

    private void loadMembers(String query) {
        GeneralUtil.unsubscribeSubscription(mMembersSubscription);
        mMembersSubscription = mTvkService.loadTvkMembers(query)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(new TvkMembersSubscriber());
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        loadMembers(query);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    @Override
    public boolean onClose() {
        return true;
    }

    private class TvkMembersSubscriber extends Subscriber<List<TvkMember>> {
        @Override
        public void onCompleted() {
        }

        @Override
        public void onError(Throwable e) {
            e.printStackTrace();
        }

        @Override
        public void onNext(List<TvkMember> tvkMembers) {
            mTvkMemberAdapter.swapData(tvkMembers);
        }

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.tvk_members, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        mSearchView = (SearchView) MenuItemCompat.getActionView(searchItem);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        mSearchView.setIconified(false);
        mSearchView.setOnQueryTextListener(this);
        mSearchView.setOnCloseListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        GeneralUtil.unsubscribeSubscription(mMembersSubscription);
    }

}
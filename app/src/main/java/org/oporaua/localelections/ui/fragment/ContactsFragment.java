package org.oporaua.localelections.ui.fragment;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.oporaua.localelections.R;
import org.oporaua.localelections.interfaces.SetToolbarListener;

import butterknife.Bind;
import butterknife.ButterKnife;


public class ContactsFragment extends Fragment implements View.OnClickListener {

    @Bind(R.id.contacts_mail_view)
    View mViewMail;

    @Bind(R.id.contacts_phone_view)
    View mViewPhone;

    @Bind(R.id.contacts_address_view)
    View mViewAddress;

    public static ContactsFragment newInstance() {
        return new ContactsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contacts, container, false);
        ButterKnife.bind(this, view);
        if (getActivity() instanceof SetToolbarListener) {
            Toolbar toolbar = ButterKnife.findById(view, R.id.app_toolbar);
            ((SetToolbarListener) getActivity()).onSetToolbar(toolbar);
        }
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewMail.setOnClickListener(this);
        mViewPhone.setOnClickListener(this);
        mViewAddress.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent();
        switch (view.getId()) {
            case R.id.contacts_mail_view:
                intent.setAction(Intent.ACTION_SENDTO);
                Uri mail = Uri.parse("mailto:" + getString(R.string.contacts_mail));
                intent.setData(mail);
                break;
            case R.id.contacts_phone_view:
                intent.setAction(Intent.ACTION_DIAL);
                Uri phoneNumber = Uri.parse("tel:" + getString(R.string.contacts_phone));
                intent.setData(phoneNumber);
                break;
            case R.id.contacts_address_view:
                intent.setAction(Intent.ACTION_VIEW);
                Uri geoLocation = Uri.parse("geo:50.45,30.523333")
                        .buildUpon()
                        .appendQueryParameter("q", "вул. Професора Підвисоцького, 10/10")
                        .build();
                intent.setData(geoLocation);
                intent.setPackage("com.google.android.apps.maps");
                break;
        }
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(intent);
        }
    }

}

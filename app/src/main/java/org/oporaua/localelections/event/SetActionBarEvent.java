package org.oporaua.localelections.event;

import android.support.v7.widget.Toolbar;

public class SetActionBarEvent {

    private Toolbar mToolbar;

    public SetActionBarEvent(Toolbar toolbar) {
        mToolbar = toolbar;
    }

    public Toolbar getToolbar() {
        return mToolbar;
    }
}

package pl.nkg.biblospk.ui;

import de.greenrobot.event.EventBus;

public abstract class AbstractActivity extends AbstractNoEventActivity {

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }
}

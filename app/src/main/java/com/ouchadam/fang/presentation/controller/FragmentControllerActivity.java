package com.ouchadam.fang.presentation.controller;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import com.github.frankiesardo.icepick.annotation.Icicle;
import com.github.frankiesardo.icepick.bundle.Bundles;
import com.novoda.notils.android.Fragments;
import com.ouchadam.bookkeeper.Downloader;
import com.ouchadam.bookkeeper.RestoreableBookKeeper;
import com.ouchadam.bookkeeper.delegate.IdManager;
import com.ouchadam.bookkeeper.domain.DownloadId;
import com.ouchadam.bookkeeper.domain.Downloadable;
import com.ouchadam.bookkeeper.domain.ProgressValues;
import com.ouchadam.bookkeeper.watcher.DownloadWatcher;
import com.ouchadam.bookkeeper.watcher.LazyWatcher;
import com.ouchadam.fang.R;
import com.ouchadam.fang.debug.DebugActivity;
import com.ouchadam.fang.presentation.item.LatestFragment;

public class FragmentControllerActivity extends FangActivity {

    @Icicle
    public String activityTitle;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.debug, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.ab_debug) {
            startActivity(new Intent(this, DebugActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onFangCreate(Bundle savedInstanceState) {
        Bundles.restoreInstanceState(this, savedInstanceState);
        if (activityTitle != null) {
            getActionBar().setTitle(activityTitle);
        }
        invalidateOptionsMenu();
        if (hasEmptyContent()) {
            showDefaultFragment();
        }
    }

    private boolean hasEmptyContent() {
        return Fragments.findFragmentById(getSupportFragmentManager(), R.id.content_frame) == null;
    }

    private void showDefaultFragment() {
        getActionBar().setTitle("Latest");
        invalidateOptionsMenu();
        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new LatestFragment()).commit();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (getActionBar() != null && getActionBar().getTitle() != null) {
            activityTitle = getActionBar().getTitle().toString();
        }
        Bundles.saveInstanceState(this, outState);
        super.onSaveInstanceState(outState);
    }

}

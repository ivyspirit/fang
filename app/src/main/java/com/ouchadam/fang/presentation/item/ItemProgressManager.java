package com.ouchadam.fang.presentation.item;

import android.view.View;
import com.ouchadam.bookkeeper.domain.ProgressValues;
import com.ouchadam.bookkeeper.watcher.adapter.ListItemProgress;
import com.ouchadam.bookkeeper.watcher.adapter.TypedBaseAdapter;
import com.ouchadam.fang.domain.item.Item;

class ItemProgressManager extends ListItemProgress<Item, ExampleListAdapter.ViewHolder> {

    public ItemProgressManager(TypedBaseAdapter<Item> baseAdapter) {
        super(baseAdapter);
    }

    @Override
    protected void onStart(Item what, ExampleListAdapter.ViewHolder viewHolder) {
        viewHolder.textView.setText("Download about to start");
        viewHolder.progressBar.setProgress(0);
        viewHolder.progressBar.setMax(100);
        viewHolder.progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onUpdate(Item what, ExampleListAdapter.ViewHolder viewHolder, ProgressValues progressValues) {
        viewHolder.textView.setText("Downloading...");
        viewHolder.progressBar.setVisibility(View.VISIBLE);
        viewHolder.progressBar.setMax(100);
        viewHolder.progressBar.setProgress(progressValues.getPercentage());
    }

    @Override
    protected void onStop(Item what, ExampleListAdapter.ViewHolder viewHolder) {
        viewHolder.textView.setText("Download complete");
        viewHolder.progressBar.setVisibility(View.GONE);
    }

}
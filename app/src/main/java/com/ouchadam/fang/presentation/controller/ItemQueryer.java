package com.ouchadam.fang.presentation.controller;

import android.content.Context;
import android.support.v4.app.LoaderManager;
import com.ouchadam.fang.domain.item.Item;
import com.ouchadam.fang.persistance.DataUpdater;
import com.ouchadam.fang.persistance.FangProvider;
import com.ouchadam.fang.persistance.Query;
import com.ouchadam.fang.persistance.database.Tables;
import com.ouchadam.fang.persistance.database.Uris;
import com.ouchadam.fang.presentation.item.DataQueryer;
import novoda.android.typewriter.cursor.CursorMarshaller;

import java.util.List;

class ItemQueryer implements DataUpdater.DataUpdatedListener<Item> {

    private final int itemColumnId;
    private final OnItemListener onItemListener;
    private final DataQueryer<Item> itemQueryer;

    interface OnItemListener {
        void onItem(Item item);
    }

    public ItemQueryer(Context context, int itemColumnId, LoaderManager loaderManager, OnItemListener onItemListener) {
        this.itemColumnId = itemColumnId;
        this.onItemListener = onItemListener;
        this.itemQueryer = new DataQueryer<Item>(context, getQueryValues(), getMarshaller(), loaderManager, this);
    }

    public void query() {
        itemQueryer.queryForData();
    }

    private CursorMarshaller<Item> getMarshaller() {
        return getItemMarshaller();
    }

    private CursorMarshaller<Item> getItemMarshaller() {
        return new ItemMarshaller();
    }

    public Query getQueryValues() {
        return new Query.Builder().
                withUri(FangProvider.getUri(Uris.PLAYLIST_ITEM)).
                withSelection(Tables.Item._id.name() + "=?").
                withSelectionArgs(new String[]{String.valueOf(itemColumnId)}).build();
    }

    @Override
    public void onDataUpdated(List<Item> data) {
        if (!data.isEmpty() && data.size() == 1) {
            onItemListener.onItem(data.get(0));
        }
    }

    public void stop() {
        itemQueryer.stop();
    }

}

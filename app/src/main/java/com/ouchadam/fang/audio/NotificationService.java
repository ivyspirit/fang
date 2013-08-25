package com.ouchadam.fang.audio;

import android.R;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;

import com.ouchadam.fang.domain.FullItem;
import com.ouchadam.fang.persistance.FangProvider;
import com.ouchadam.fang.persistance.Query;
import com.ouchadam.fang.persistance.database.Tables;
import com.ouchadam.fang.persistance.database.Uris;
import com.ouchadam.fang.presentation.controller.FangNotification;
import com.ouchadam.fang.presentation.controller.FullItemMarshaller;
import com.squareup.picasso.Picasso;

import java.io.IOException;

public class NotificationService extends IntentService {

    private FangNotification fangNotification;

    public static void start(Context context, long itemId) {
        Intent intent = new Intent(context, NotificationService.class);
        intent.putExtra("test", itemId);
        context.startService(intent);
    }

    public NotificationService() {
        super(NotificationService.class.getSimpleName());
    }

    @Override
    public void onCreate() {
        super.onCreate();
        fangNotification = FangNotification.from(this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (isValid(intent)) {
            long itemId = intent.getLongExtra("test", -1L);
            FullItem item = getFullItem(itemId);
            if (item != null) {
                try {
                    int imageWidth = getResources().getDimensionPixelSize(R.dimen.notification_large_icon_width);
                    int imageHeight = getResources().getDimensionPixelSize(R.dimen.notification_large_icon_height);
                    Bitmap channelImage = Picasso.with(this).load(item.getImageUrl()).resize(imageWidth, imageHeight).get();
                    fangNotification.show(channelImage, item);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private FullItem getFullItem(long itemId) {
        Query query = getQueryValues(itemId);
        Cursor cursor = getContentResolver().query(query.uri, query.projection, query.selection, query.selectionArgs, query.sortOrder);

        FullItem item = null;
        if   (cursor != null && cursor.moveToFirst()) {
            item = new FullItemMarshaller().marshall(cursor);
        }
        return item;
    }

    private boolean isValid(Intent intent) {
        return intent != null && intent.hasExtra("test") && intent.getLongExtra("test", -1L) != -1L;
    }

    private Query getQueryValues(long itemId) {
        return new Query.Builder().
                withUri(FangProvider.getUri(Uris.FULL_ITEM)).
                withSelection(Tables.Item._id.name() + "=?").
                withSelectionArgs(new String[]{String.valueOf(itemId)}).build();
    }

}

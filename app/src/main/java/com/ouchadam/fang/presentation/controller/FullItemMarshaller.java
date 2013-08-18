package com.ouchadam.fang.presentation.controller;

import android.database.Cursor;
import com.ouchadam.fang.domain.FullItem;
import com.ouchadam.fang.domain.channel.Image;
import com.ouchadam.fang.domain.item.Item;
import com.ouchadam.fang.persistance.database.Tables;
import novoda.android.typewriter.cursor.CursorMarshaller;

public class FullItemMarshaller implements CursorMarshaller<FullItem> {

    @Override
    public FullItem marshall(Cursor cursor) {
        CursorMarshallerUtil cursorUtil = new CursorMarshallerUtil(cursor);
        Item item = new ItemMarshaller().marshall(cursor);
        Image image = new ImageCursorMarshaller().marshall(cursor);
        String channelTitle = cursorUtil.getString(Tables.Channel.CHANNEL_TITLE);
        long downloadId = cursorUtil.getLong(Tables.Playlist.DOWNLOAD_ID);
        return new FullItem(item, channelTitle, image, downloadId);
    }

    private static class ImageCursorMarshaller implements CursorMarshaller<Image> {
        @Override
        public Image marshall(Cursor cursor) {
            CursorMarshallerUtil cursorUtil = new CursorMarshallerUtil(cursor);
            String url = cursorUtil.getString(Tables.ChannelImage.URL);
            String link = cursorUtil.getString(Tables.ChannelImage.LINK);
            String title = cursorUtil.getString(Tables.ChannelImage.TITLE);
            int width = cursorUtil.getInt(Tables.ChannelImage.WIDTH);
            int height = cursorUtil.getInt(Tables.ChannelImage.HEIGHT);
            return new Image(url, link, title, width, height);
        }
    }

    private static class CursorMarshallerUtil {

        private final Cursor cursor;

        private CursorMarshallerUtil(Cursor cursor) {
            this.cursor = cursor;
        }

        public String getString(Enum column) {
            return cursor.getString(getColumnIndex(column));
        }

        public int getInt(Enum column) {
            return cursor.getInt(getColumnIndex(column));
        }

        public long getLong(Enum column) {
            return cursor.getLong(getColumnIndex(column));
        }

        private int getColumnIndex(Enum column) {
            return cursor.getColumnIndexOrThrow(column.name());
        }

    }

}
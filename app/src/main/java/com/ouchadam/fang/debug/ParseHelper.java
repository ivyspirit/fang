package com.ouchadam.fang.debug;

import android.app.Activity;

import android.content.ContentResolver;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import com.novoda.sexp.parser.ParseFinishWatcher;
import com.ouchadam.fang.domain.channel.Channel;
import com.ouchadam.fang.parsing.ChannelFinder;
import com.ouchadam.fang.parsing.PodcastParser;
import com.ouchadam.fang.persistance.ChannelPersister;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class ParseHelper {

    private final static Handler HANDLER = new Handler(Looper.getMainLooper());
    private final ContentResolver contentResolver;
    private final OnParseFinishedListener listener;
    private PodcastParser podcastParser;

    public interface OnParseFinishedListener {
        void onParseFinished(Channel channel);
    }

    public ParseHelper(ContentResolver contentResolver, OnParseFinishedListener listener) {
        this.contentResolver = contentResolver;
        this.listener = listener;
    }

    public void parse(Activity activity, String fileName) {
        podcastParser = PodcastParser.newInstance(ChannelFinder.newInstance(), parseFinishWatcher);
        try {
            podcastParser.parse(activity.getAssets().open(fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private final ParseFinishWatcher parseFinishWatcher = new ParseFinishWatcher() {
        @Override
        public void onFinish() {
            new ChannelPersister(contentResolver).persist(podcastParser.getResult());
            onCallback(podcastParser.getResult());
        }
    };

    public void parse(String... urls) {
        for (final String url : urls) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    PodcastParser podcastParser = PodcastParser.newInstance(ChannelFinder.newInstance(), new ParseFinishWatcher() {
                        @Override
                        public void onFinish() {
                        }
                    });
                    podcastParser.parse(getInputStream(url));
                    new ChannelPersister(contentResolver).persist(podcastParser.getResult());
                    onCallback(podcastParser.getResult());
                }
            }).start();
        }
    }

    private synchronized void onCallback(final Channel channel) {
        HANDLER.post(new Runnable() {
            @Override
            public void run() {
                listener.onParseFinished(channel);
            }
        });
    }

    private InputStream getInputStream(String url) {
        URL oracle = null;
        Log.e("!!!", "Fetching stream for : " + url);
        try {
            oracle = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        InputStream in = null;
        try {
            return oracle.openStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}

package com.ouchadam.fang.debug;

import android.app.Activity;

import com.novoda.sexp.parser.ParseFinishWatcher;
import com.ouchadam.fang.domain.channel.Channel;
import com.ouchadam.fang.parsing.ChannelFinder;
import com.ouchadam.fang.parsing.PodcastParser;

import java.io.IOException;

public class ParseHelper implements ParseFinishWatcher {

    private PodcastParser podcastParser;

    private final OnParseFinishedListener listener;

    public interface OnParseFinishedListener {
        void onParseFinished(Channel channel);
    }

    public ParseHelper(OnParseFinishedListener listener) {
        this.listener = listener;
    }

    public void parse(Activity activity, String fileName) {
        podcastParser = PodcastParser.newInstance(ChannelFinder.newInstance(), this);
        try {
            podcastParser.parse(activity.getAssets().open(fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFinish() {
        listener.onParseFinished(podcastParser.getResult());
    }
}
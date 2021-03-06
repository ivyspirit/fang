package com.ouchadam.fang.audio;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;

import com.ouchadam.fang.domain.PodcastPosition;
import com.ouchadam.fang.notification.FangNotification;
import com.ouchadam.fang.presentation.controller.AudioFocusManager;
import com.ouchadam.fang.presentation.controller.PlayerEvent;

import java.io.IOException;

class PlayerHandler implements PlayerEventReceiver.PlayerEventCallbacks {

    static final long MISSING_ID = -1L;

    private final PodcastPlayer podcastPlayer;
    private final AudioFocusManager audioFocusManager;
    private final AudioSync audioSync;
    private final PlayingItemStateManager itemStateManager;
    private final FangNotification notification;
    private final ServiceManipulator serviceManipulator;

    private long playingItemId;

    interface AudioSync {
        void onSync(long itemId, PlayerEvent playerEvent);
    }

    static PlayerHandler from(Context context, AudioSync audioSync, MediaPlayer.OnCompletionListener onComplete, ServiceManipulator serviceManipulator) {
        PodcastPlayer player = new PodcastPlayer(context, new PodcastPositionBroadcaster(context), onComplete);
        AudioFocusManager focusManager = new AudioFocusManager((AudioManager) context.getSystemService(Context.AUDIO_SERVICE));
        PlayingItemStateManager itemStateManager = PlayingItemStateManager.from(context);
        FangNotification notification = FangNotification.from(context);
        return new PlayerHandler(player, focusManager, audioSync, itemStateManager, notification, serviceManipulator);
    }

    PlayerHandler(PodcastPlayer podcastPlayer, AudioFocusManager audioFocusManager, AudioSync audioSync, PlayingItemStateManager itemStateManager, FangNotification notification, ServiceManipulator serviceManipulator) {
        this.podcastPlayer = podcastPlayer;
        this.audioFocusManager = audioFocusManager;
        this.audioSync = audioSync;
        this.itemStateManager = itemStateManager;
        this.notification = notification;
        this.serviceManipulator = serviceManipulator;
        this.playingItemId = itemStateManager.getStoredPlayingId();
    }

    @Override
    public void onNewSource(long itemId, Uri source) {
        this.playingItemId = itemId;
        setAudioSource(source);
        sync(new PlayerEvent.Factory().newSource(itemId, source));
    }

    private void setAudioSource(Uri uri) {
        try {
            podcastPlayer.setSource(uri);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("couldn't find : " + uri);
        }
    }

    @Override
    public void onPlay(PodcastPosition position) {
        play(position);
        sync(new PlayerEvent.Factory().play(position));
    }

    private void play(PodcastPosition position) {
        audioFocusManager.requestFocus();
        podcastPlayer.play(position);
    }

    @Override
    public void onPause() {
        pauseAudio();
        itemStateManager.persist(playingItemId, podcastPlayer.getPosition());
        sync(new PlayerEvent.Factory().pause());
    }

    private void pauseAudio() {
        podcastPlayer.pause();
        audioFocusManager.abandonFocus();
    }

    @Override
    public void onStop() {
        if (podcastPlayer.isPrepared()) {
            itemStateManager.persist(playingItemId, podcastPlayer.getPosition());
        }
        stopAudio();
        notification.dismiss();
        serviceManipulator.stop();
    }


    private void stopAudio() {
        audioFocusManager.abandonFocus();
        podcastPlayer.release();
    }

    @Override
    public void gotoPosition(PodcastPosition position) {
        podcastPlayer.goTo(position.value());
        sync(new PlayerEvent.Factory().goTo(position));
    }

    private void sync(PlayerEvent playerEvent) {
        audioSync.onSync(playingItemId, playerEvent);
    }

    public SyncEvent asSyncEvent() {
        return currentItemIsValid() ? createValidSyncEvent() : SyncEvent.fresh();
    }

    private boolean currentItemIsValid() {
        return playingItemId != MISSING_ID;
    }

    private SyncEvent createValidSyncEvent() {
        return podcastPlayer.isNotPrepared() ? SyncEvent.idle(playingItemId) : new SyncEvent(podcastPlayer.isPlaying(), podcastPlayer.getPosition(), playingItemId);
    }

}

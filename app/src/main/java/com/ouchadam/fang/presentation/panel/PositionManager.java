package com.ouchadam.fang.presentation.panel;

import android.content.Context;
import android.widget.SeekBar;

import com.ouchadam.fang.audio.SeekbarReceiver;
import com.ouchadam.fang.domain.PodcastPosition;

class PositionManager {

    private final SlidingPanelViewManipulator.OnSeekChanged onSeekChanged;
    private final SeekbarReceiver seekbarReceiver;
    private final PositionController positionController;

    private boolean positionChanging = false;
    private PodcastPosition position;

    public PositionManager(SlidingPanelViewManipulator.OnSeekChanged onSeekChanged, SeekbarReceiver seekbarReceiver, PositionController positionController) {
        this.onSeekChanged = onSeekChanged;
        this.seekbarReceiver = seekbarReceiver;
        this.positionController = positionController;
        positionController.setSeekChangedListener(seekListener);
    }

    private final SeekBar.OnSeekBarChangeListener seekListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            PodcastPosition position = new PodcastPosition(seekBar.getProgress(), seekBar.getMax());
            positionController.update(positionChanging, position);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            positionChanging = true;
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            onSeekChanged.onSeekChanged(new PodcastPosition(seekBar.getProgress(), seekBar.getMax()));
            positionChanging = false;
        }
    };


    public void registerForUpdates(Context context) {
        seekbarReceiver.register(context);
    }

    public void unregisterForUpdates(Context context) {
        seekbarReceiver.unregister(context);
    }

    public void update(PodcastPosition position) {
        this.position = position;
        positionController.update(positionChanging, position);
    }

    public PodcastPosition getLatestPosition() {
        return position;
    }

}

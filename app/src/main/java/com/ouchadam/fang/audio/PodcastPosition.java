package com.ouchadam.fang.audio;

import java.io.Serializable;

public class PodcastPosition implements Serializable {

    private int currentPosition;
    private int duration;

    public PodcastPosition(int currentPosition, int duration) {
        this.currentPosition = currentPosition;
        this.duration = duration;
    }

    public int value() {
        return currentPosition;
    }

    public int asPercentage() {
        float percentCoeff = (float) currentPosition / (float) duration;
        return (int) (percentCoeff * 100);
    }

    public int getDuration() {
        return duration;
    }
}

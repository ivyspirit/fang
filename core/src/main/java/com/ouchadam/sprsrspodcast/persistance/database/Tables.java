package com.ouchadam.sprsrspodcast.persistance.database;

public class Tables {

    private Tables() {
        throw new RuntimeException("This class should not be instantiated");
    }

    public enum Item {
        TITLE,
        PUBDATE,
        AUDIO_URL,
        AUDIO_TYPE,
        LINK,
        SUBTITLE,
        SUMMARY;
    }

}
package com.ouchadam.fang.persistance.database.bridge;

public interface ContentProviderOperationValues {
    void withValue(String key, Object value);
    void withSelection(String selection, String[] selectionArgs);
}

package me.infuzion.chess.web.record;

import java.util.HashMap;
import java.util.Map;

public class RecordManager<T extends Record> {
    private final Map<Class<T>, RecordSource<T>> classRecordSourceMap = new HashMap<>();

    public void register(Class<T> recordClass, RecordSource<T> source) {
        classRecordSourceMap.put(recordClass, source);
    }

    public RecordSource<T> getRecordSource(Class<T> record) {
        return classRecordSourceMap.get(record);
    }
}

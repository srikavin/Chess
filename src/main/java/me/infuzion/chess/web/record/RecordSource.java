package me.infuzion.chess.web.record;

import me.infuzion.chess.util.Identifier;

import java.util.List;

public interface RecordSource<T extends Record> {
    List<T> getRecords(int limit);


    T getRecord(Identifier id);
}

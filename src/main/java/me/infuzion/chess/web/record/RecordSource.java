package me.infuzion.chess.web.record;

import me.infuzion.chess.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface RecordSource<T extends Record> {
    List<T> getRecords(int limit);

    @Nullable
    T getRecord(Identifier id);
}

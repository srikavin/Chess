package me.infuzion.chess.web.record;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import me.infuzion.chess.util.Identifier;
import me.infuzion.chess.web.record.filter.Filter;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class RecordSet<T extends Record> {
    private final String resourceName;
    private final RecordSource<T> recordSource;

    public RecordSet(String resourceName, RecordSource<T> recordSource) {
        this.resourceName = resourceName;
        this.recordSource = recordSource;
    }

    @Nullable
    public T get(Identifier id) {
        return recordSource.getRecord(id);
    }

    public List<T> get() {
        return get(10);
    }

    public List<T> get(int limit) {
        return recordSource.getRecords(limit);
    }

    public List<T> get(Filter<T> filter) {
        return null;
    }

    public JsonObject toJson() {
        return toJson(10);
    }

    public JsonObject toJson(int limit) {
        JsonObject root = new JsonObject();
        JsonArray array = new JsonArray();
        List<T> records = get(limit);

        for (T record : records) {
            array.add(record.toJson());
        }

        root.add(resourceName, array);
        return root;
    }
}

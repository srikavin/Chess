package me.infuzion.chess.web.record;

import com.google.gson.JsonObject;

public interface JsonSerializable {
    JsonObject toJson();
}

package me.infuzion.chess.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;

import java.lang.reflect.Modifier;

public class ChessUtilities {
    public final static Gson gson;

    static {
        GsonBuilder builder = new GsonBuilder();
        gson = builder.enableComplexMapKeySerialization().setPrettyPrinting().serializeNulls().create();
    }
}

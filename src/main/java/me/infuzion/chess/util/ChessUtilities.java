package me.infuzion.chess.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;

import java.lang.reflect.Modifier;

public class ChessUtilities {
    public final static Gson gson;
    public final static JsonParser parser;
    public final static Gson gsonWithStatic;

    static {
        GsonBuilder builder = new GsonBuilder();
        gson = builder.enableComplexMapKeySerialization().create();

        parser = new JsonParser();
        builder = new GsonBuilder();
        gsonWithStatic = builder.excludeFieldsWithModifiers(Modifier.TRANSIENT).create();
    }
}

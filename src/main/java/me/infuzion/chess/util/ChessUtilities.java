package me.infuzion.chess.util;

import com.google.gson.*;
import me.infuzion.chess.board.ChessPosition;

public class ChessUtilities {
    public final static Gson gson;

    static {
        GsonBuilder builder = new GsonBuilder();
        gson = builder.enableComplexMapKeySerialization().setPrettyPrinting()
                .registerTypeAdapter(Identifier.class,
                        (JsonSerializer<Identifier>) (src, typeOfSrc, context) -> new JsonPrimitive(src.toString())
                ).registerTypeAdapter(Identifier.class,
                        (JsonDeserializer<Identifier>) (json, type, context) -> new Identifier(json.getAsString())
                ).registerTypeAdapter(ChessPosition.class,
                        (JsonSerializer<ChessPosition>) (src, typeOfSrc, context) -> new JsonPrimitive(src.getPosition())
                ).registerTypeAdapter(ChessPosition.class,
                        (JsonDeserializer<ChessPosition>) (json, type, context) -> new ChessPosition(json.getAsString())
                ).create();
    }
}

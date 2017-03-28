package me.infuzion.chess.web.listener;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.infuzion.chess.ChessPosition;
import me.infuzion.chess.util.ChessUtilities;
import me.infuzion.chess.util.MatchDatabase;
import me.infuzion.chess.web.Game;
import me.infuzion.chess.web.event.ChessWebEvent;
import me.infuzion.chess.web.event.ChessWebListener;
import me.infuzion.chess.web.event.EndPointURL;
import me.infuzion.web.server.event.EventCondition;
import me.infuzion.web.server.event.EventHandler;

import java.util.Map;
import java.util.UUID;

public class ChessMoveListener implements ChessWebListener {

    private final MatchDatabase matchDatabase;

    public ChessMoveListener(MatchDatabase database) {
        this.matchDatabase = database;
    }

    @EventCondition(eventMethod = "")
    private boolean shouldExecute(String pageName) {
        return pageName.equals(EndPointURL.SUBMIT_URL);
    }

    @EventHandler
    private void onEvent(ChessWebEvent event) {
        if (!shouldExecute(event.getEvent().getPage())) {
            return;
        }

        UUID publicUUID = event.getPublicUUID();
        Map<UUID, Game> uuidGameMap = event.getUuidGameMap();

        Game game = uuidGameMap.get(publicUUID);
        String requestData = event.getEvent().getRequestData();
        if (game == null || requestData == null) {
            return;
        }
        JsonElement element = ChessUtilities.parser.parse(event.getEvent().getRequestData());
        JsonObject action = element.getAsJsonObject();
        JsonElement sRow = action.get("startrow");
        JsonElement sCol = action.get("startcol");
        JsonElement eRow = action.get("endrow");
        JsonElement eCol = action.get("endcol");
        if (sRow == null || sCol == null || eRow == null || eCol == null) {
            setResponseJson(event.getEvent(), new JsonObject(), 400);
            return;
        }
        int startRow = sRow.getAsInt();
        int startCol = sCol.getAsInt();
        int endRow = eRow.getAsInt();
        int endCol = eCol.getAsInt();

        int status = 200;
        if (!(startRow > 7 || startRow < 0 || endRow > 7 || endCol < 0)) {
            game.move(publicUUID, new ChessPosition(startRow, startCol),
                    new ChessPosition(endRow, endCol));
            matchDatabase.addMatch(game);
        } else {
            status = 400;
        }

        JsonObject obj = new JsonObject();
        obj.addProperty("status", status);

        setResponseJson(event.getEvent(), obj, status);
    }
}

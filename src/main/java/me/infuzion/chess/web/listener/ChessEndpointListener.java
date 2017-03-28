package me.infuzion.chess.web.listener;

import me.infuzion.chess.util.ChessUtilities;
import me.infuzion.chess.web.event.ChessWebEvent;
import me.infuzion.chess.web.event.ChessWebListener;
import me.infuzion.chess.web.event.EndPointURL;
import me.infuzion.web.server.event.EventHandler;

public class ChessEndpointListener implements ChessWebListener {

    @EventHandler
    public void onEvent(ChessWebEvent event) {
        if (event.getEvent().getPage().equals(EndPointURL.ENDPOINT_LIST_URL)) {
            setResponseJson(event.getEvent(),
                    ChessUtilities.gsonWithStatic.toJsonTree(EndPointURL.instance));
        }
    }
}

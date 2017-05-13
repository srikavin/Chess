package me.infuzion.chess.web.event;

import com.google.gson.JsonObject;
import me.infuzion.chess.util.ChessUtilities;
import me.infuzion.web.server.EventListener;
import me.infuzion.web.server.event.def.PageRequestEvent;
import me.infuzion.web.server.util.Utilities;

import java.io.InputStream;


public interface ChessWebListener extends EventListener {

    default void setResponseJson(PageRequestEvent event, String json) {
        setResponseJson(event, json, 200);
    }

    default void setResponseJson(PageRequestEvent event, String json, int status) {
        setResponse(event, json, status, "json");
    }

    default void setResponseJson(PageRequestEvent event, Object element) {
        setResponseJson(event, ChessUtilities.gson.toJson(element));
    }

    default void setResponseJsonWrapped(PageRequestEvent event, Object element, String root) {
        JsonObject object = new JsonObject();
        object.add(root, ChessUtilities.gson.toJsonTree(element));
        setResponseJson(event, object);
    }

    default void setResponseJson(PageRequestEvent event, Object element, int status) {
        setResponseJson(event, ChessUtilities.gson.toJson(element), status);
    }

    default void setResponse(PageRequestEvent event, String output, int status, String fileType) {
        event.setStatusCode(status);
        event.setResponseData(output);
        event.setContentType(fileType);
        event.setHandled(true);
    }

    default void setResponse(PageRequestEvent event, InputStream output, int status, String fileType) {
        setResponse(event, Utilities.convertStreamToString(output), status, fileType);
    }
}

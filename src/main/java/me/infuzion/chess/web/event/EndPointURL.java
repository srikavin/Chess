package me.infuzion.chess.web.event;

public class EndPointURL {
    public final transient static String URL_PREFIX = "/";
    public final transient static String URL_SUFFIX = ".json";
    public final transient static String API_PREFIX = URL_PREFIX + "api/v1/";
    public final transient static String GAME_JOIN_PREFIX = API_PREFIX + "join/";

    public final static String CREATE_GAME_URL = API_PREFIX + "games/create";
    public final static String SUBMIT_URL = API_PREFIX + "submit";
    public final static String JOINABLE_GAMES_URL = API_PREFIX + "games";
    public final static String BASE_GAME_URL = API_PREFIX + "games";
    public final static String ENDPOINT_LIST_URL = API_PREFIX + "chess/endpoints";
    public final static String USER_API_BASE_URL = API_PREFIX + "users/";
    public final static String TOKEN_CHECK = API_PREFIX + "token";
    public final static String GET_USER_IMAGE = API_PREFIX + "user/image";

    public final static String GAME_PREVIEW_URL = BASE_GAME_URL + "/preview";

    public final static String USER_IMAGE_CHANGE = API_PREFIX + "me/image";

    public final static String WEBSOCKET_GAME_URL = URL_PREFIX + "game/";

    public final static String LOGIN_URL = API_PREFIX + "login";
    public final static String REGISTER_URL = API_PREFIX + "register";

    private EndPointURL() {
    }

}

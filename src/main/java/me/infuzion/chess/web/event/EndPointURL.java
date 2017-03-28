package me.infuzion.chess.web.event;

public class EndPointURL {

    public final transient static String URL_PREFIX = "/";
    public final transient static String URL_SUFFIX = ".json";
    public final transient static String API_PREFIX = URL_PREFIX + "api/";
    public final transient static String GAME_JOIN_PREFIX = API_PREFIX + "join/";

    public final static String CREATE_GAME_URL = API_PREFIX + "creategame" + URL_SUFFIX;
    public final static String CHESS_URL = URL_PREFIX + "chess" + URL_SUFFIX;
    public final static String SUBMIT_URL = API_PREFIX + "submit" + URL_SUFFIX;
    public final static String JOINABLE_GAMES_URL = API_PREFIX + "waiting" + URL_SUFFIX;
    public final static String BASE_GAME_URL = URL_PREFIX + "game/";
    public final static String GAME_URL_FORMAT = "{id}";
    public final static String GAME_URL_DATA_SUFFIX = URL_SUFFIX;
    public final static String GAME_JOIN_URL = GAME_JOIN_PREFIX + "{id}" + URL_SUFFIX;
    public final static String ENDPOINT_LIST_URL = API_PREFIX + "chess/endpoints";

    public final static String LOGIN_URL = URL_PREFIX + "login";
    public final static String REGISTER_URL = URL_PREFIX + "register";

    public final static String USER_BASE_URL = URL_PREFIX + "user/";

    public final transient static EndPointURL instance;

    static {
        instance = new EndPointURL();
    }

    private EndPointURL() {
    }

}

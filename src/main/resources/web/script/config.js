"use strict";
var chessConfigLoaded = chessConfigLoaded || false;

String.prototype.format = String.prototype.format ||
    function () {
        "use strict";
        var str = this.toString();
        if (arguments.length) {
            var t = typeof arguments[0];
            var key;
            var args = ("string" === t || "number" === t) ?
                Array.prototype.slice.call(arguments)
                : arguments[0];

            for (key in args) {
                str = str.replace(new RegExp("\\{" + key + "\\}", "gi"), args[key]);
            }
        }

        return str;
    };

$.ajax({
    url: "/chess/endpoints",
    dataType: "json",
    success: function (data) {
        CREATE_GAME_URL = data["CREATE_GAME_URL"];
        CHESS_URL = data["CHESS_URL"];
        SUBMIT_URL = data["SUBMIT_URL"];
        JOINABLE_GAMES_URL = data["JOINABLE_GAMES_URL"];
        BASE_GAME_URL = data["BASE_GAME_URL"];
        GAME_URL_FORMAT = data["GAME_URL_FORMAT"];
        GAME_URL_DATA_SUFFIX = data["GAME_URL_DATA_SUFFIX"];
        GAME_JOIN_URL = data["GAME_JOIN_URL"];

        GAME_URL_DATA = BASE_GAME_URL + GAME_URL_FORMAT + GAME_URL_DATA_SUFFIX;
        GAME_URL_PAGE = BASE_GAME_URL + GAME_URL_FORMAT;
    }
});

var CREATE_GAME_URL;
var CHESS_URL;
var SUBMIT_URL;
var JOINABLE_GAMES_URL;
var BASE_GAME_URL;
var GAME_URL_FORMAT;
var GAME_URL_DATA_SUFFIX;
var GAME_JOIN_URL;

var GAME_URL_DATA;
var GAME_URL_PAGE;


var imgBaseURL = "/";
var imgSources = {
    wRook: imgBaseURL + "sprite/wRook.svg",
    wBishop: imgBaseURL + "sprite/wBishop.svg",
    wKnight: imgBaseURL + "sprite/wKnight.svg",
    wKing: imgBaseURL + "sprite/wKing.svg",
    wQueen: imgBaseURL + "sprite/wQueen.svg",
    wPawn: imgBaseURL + "sprite/wPawn.svg",
    bRook: imgBaseURL + "sprite/bRook.svg",
    bBishop: imgBaseURL + "sprite/bBishop.svg",
    bKnight: imgBaseURL + "sprite/bKnight.svg",
    bKing: imgBaseURL + "sprite/bKing.svg",
    bQueen: imgBaseURL + "sprite/bQueen.svg",
    bPawn: imgBaseURL + "sprite/bPawn.svg"
};

function formatAndGet(url, id) {
    return url.format({id: id});
}

chessConfigLoaded = true;
if (main) {
    $(main);
}
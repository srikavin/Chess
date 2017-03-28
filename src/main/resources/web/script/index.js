"use strict";
$.getScript("/script/config.js");

var main = function () {
    $("#refresh").on("click", updateData);
    $("#newGameDiv").on("click", function () {
        $.ajax(CREATE_GAME_URL, {
            dataType: "json",
            success: function (data) {
                if ($.isEmptyObject(data)) {
                    $("#newGame").prop("disabled", "true").addClass("");
                    $("#newGameDiv").tooltip().tooltip('show');
                } else {
                    $("#newGame").prop("disabled", "false");
                    $("#newGameDiv").tooltip('hide').tooltip('destroy');
                }
            }
        });
    });

    updateData();

    $('body').on("click", "[data-game]", function () {
        window.chess = {id: $(this).data("game")};
        loadPage(formatAndGet(GAME_URL_PAGE, $(this).data("game")));
    });

    function loadPage(url) {
        $("#content").load(url, function () {
            $("#loading-screen").css("opacity", 0);
            setTimeout(function () {
                $("#loading-screen").css("visibility", "hidden");
            }, 750)
        });
        $("#loading-screen").css("visibility", "visible").css("opacity", 100);
    }

    function updateData() {
        $.ajax(JOINABLE_GAMES_URL, {
            dataType: "json",
            success: function (data) {
                $(".added").remove();
                for (var i = 0; i < data.length; i++) {
                    addGameElement(data[i]["gameID"]["id"], "Standard", data[i]["status"]);
                }
            }
        });
    }

    function addGameElement(id, mode, status) {
        var toAppend = $("#game-entry-format").clone().removeAttr("id").html().format({
            id: id,
            mode: mode,
            status: status
        });
        $("#game-holder").append("<tr class='added'>" + toAppend + "</tr>");
    }
};
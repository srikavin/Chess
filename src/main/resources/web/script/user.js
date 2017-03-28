"use strict";
$.getScript("/script/config.js");

$(function () {
    $("#loading-screen").css("visibility", "visible").css("opacity", .75);
});

var main = function () {
    function populateHTML(data) {
        console.log(data);
        $("#bio").text(data.bio);
        $("#username").text(data.username);
        $("#last-seen").text(moment(data.lastSeen).fromNow());
        $("#loading-screen").css("opacity", 0);
        setTimeout(function () {
            $("#loading-screen").css("visibility", "hidden");
        }, 750);
    }

    var name = location.pathname.substr(location.pathname.lastIndexOf('/') + 1);
    $.ajax({
        url: '/user/',
        data: {format: "json", name: name},
        success: populateHTML,
        dataType: "json"
    });
};

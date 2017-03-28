// $.getScript("/script/config.js");

main = function () {
    $("#loginButton").on('click', function (e) {
        e.preventDefault();
        submit('/login', $("#username").val(), $("#password").val(), function (data) {
            window.location.assign("/user/" + data.username);
            console.log(data);
        });
        return false;
    });

    $("#signupButton").on('click', function (e) {
        e.preventDefault();
        submit('/register', $("#username").val(), $("#password").val(), function (data) {
            window.location.assign("/user/" + data.username);
            console.log(data);
        });
        return false;
    });

    function submit(url, username, password, callback) {
        $("#loading-screen").css("visibility", "visible").css("opacity", .75);
        $.post(url, {username: username, password: password}, function (data) {
            callback(data);
            $("#loading-screen").css("opacity", 0);
            setTimeout(function () {
                $("#loading-screen").css("visibility", "hidden");
            }, 750);
        });
    }
};
$(main);
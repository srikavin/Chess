"use strict";
const preferredTileSize = 80;
var tileSize = 80;

$.getScript("/script/config.js");

function Action(startrow, startcol, endrow, endcol, id) {
    this.startrow = startrow;
    this.startcol = startcol;
    this.endrow = endrow;
    this.endcol = endcol;
    this.send = function send() {
        if (this.startrow != null && this.startcol != null && this.endrow != null && this.endcol != null) {
            $.post(formatAndGet(SUBMIT_URL, id), JSON.stringify(this));
        }
    }
}

String.prototype.capitalizeFirstLetter = function () {
    return this.charAt(0).toUpperCase() + this.slice(1);
};

var image;

var main = function () {
    var canvas = document.getElementById('canvas');
    var clickStart;
    var clickEnd;
    var lastAjax;

    $(window).on("resize", updateCanvasSize);

    function updateCanvasSize() {
        var gameSelector = $("#game");
        var width = gameSelector.width();
        var height = gameSelector.height();
        var size = Math.min(width, height);

        tileSize = size / 8;
        canvas.width = size;
        canvas.height = size;
        draw(true);
    }

    var draggingX;
    var draggingY;
    var lastDragX;
    var lastDragY;
    var isDragging = false;
    var isHovering = true;

    function handleMouseDown() {
        isDragging = true;
    }

    function handleMouseOver() {
        isHovering = true;
    }

    function handleHover(event) {
        if (event.type == "mouseout") {
            isHovering = false;
        }
        if (isDragging) {
            isDragging = false;
            setUpGrid(ctx);
            loadBoard(lastAjax);
            new Action(draggingY, draggingX, Math.floor(lastDragY), Math.floor(lastDragX), id).send();
            draggingX = undefined;
            draggingY = undefined;
            $(canvas).css("cursor", "auto");
        }
    }

    function handleMouseMove(event) {
        var rect = canvas.getBoundingClientRect();
        if (isHovering) {
            var tileX = Math.floor((event.clientX - rect.left) / tileSize);
            var tileY = Math.floor((event.clientY - rect.top) / tileSize);
            var pieceData = getPieceData(tileX, tileY);
            if (!pieceData.empty) {
                $(canvas).css("cursor", "move");
            } else if (!isDragging) {
                $(canvas).css("cursor", "auto");
            }
        }
        if (isDragging) {
            $(canvas).css("cursor", "move");
            var x = event.clientX - rect.left;
            var y = event.clientY - rect.top;
            if (!draggingX || !draggingY) {
                draggingX = Math.floor(x / tileSize);
                draggingY = Math.floor(y / tileSize)
            }
            lastDragX = x / tileSize;
            lastDragY = y / tileSize;
            var data = getPieceData(draggingX, draggingY);
            setUpGrid(ctx);
            loadBoard(lastAjax);
            drawTile(ctx, lastDragX, lastDragY, true, data.image, data.color);
        }
    }

    function handleMouseClick(event) {
        var rect = canvas.getBoundingClientRect();
        var col = event.clientX - rect.left;
        var row = event.clientY - rect.top;
        if (!id) {
            return;
        }
        if (clickStart) {
            clickEnd = {
                row: Math.floor(row / tileSize),
                col: Math.floor(col / tileSize)
            };
            var action = new Action(clickStart.row, clickStart.col, clickEnd.row,
                clickEnd.col, id);
            action.send();
            clickStart = undefined;
            clickEnd = undefined;
        } else {
            clickStart = {
                row: Math.floor(row / tileSize),
                col: Math.floor(col / tileSize)
            };
            setUpGrid(ctx);
            ctx.globalAlpha = 0.25;
            setUpGridPoint(ctx, clickStart.col, clickStart.row, "red");
            ctx.globalAlpha = 1.0;
            loadBoard(lastAjax);

        }
    }

    $("canvas")
        .on("mousedown", handleMouseDown)
        .on("mouseover", handleMouseOver)
        .on("mouseup mouseout", handleHover)
        .on("mousemove", handleMouseMove)
        .on("click", handleMouseClick);

    var ctx;
    var id;
    if (canvas.getContext) {
        ctx = canvas.getContext('2d');
        updateCanvasSize();
        loadImages(imgSources, getAjax);
    } else {
        alert("Your browser is not supported.");
    }

    var errorLast = false;

    function getAjax(force) {
        var url;
        if (window.chess && window.chess.id) {
            url = formatAndGet(GAME_URL_DATA, window.chess.id)
        } else if (id) {
            url = formatAndGet(GAME_URL_DATA, id);
        } else {
            url = window.location.href + ".json";
        }
        $.ajax(url, {
                dataType: "json",
                success: function (data) {
                    if (_.isEqual(lastAjax, data) && !force) {
                        return;
                    }
                    lastAjax = data;
                    if (!force) {
                        notify();
                    }

                    if (errorLast) {
                        $.notify("A connection has been made.", {
                            animate: {
                                enter: 'animated bounceIn',
                                exit: 'animated bounceOut'
                            },
                            type: 'success',
                            timer: 3500
                        });
                        errorLast = false;
                    }

                    id = data["gameID"]["id"];
                    history.pushState(undefined, "Chess", formatAndGet(GAME_URL_PAGE, id));
                    setUpGrid(ctx);
                    loadBoard(data);
                },
                error: function () {
                    if (errorLast) {
                        return;
                    }
                    $.notify("An error has occurred and connection has been lost. Click to go back.", {
                        animate: {
                            enter: 'animated bounceIn',
                            exit: 'animated bounceOut'
                        },
                        url: '/play.html',
                        target: '_self',
                        type: 'danger',
                        timer: 3500
                    });
                    errorLast = true;
                }
            }
        );
    }

    setInterval(draw, 1000);

    function loadBoard(data) {
        var board = data["board"];
        if (board["lastMove"] && board["lastMove"]["start"] && board["lastMove"]["end"]) {
            ctx.globalAlpha = 0.25;
            setUpGridPoint(ctx, board["lastMove"]["start"]["col"], board["lastMove"]["start"]["row"], "yellow");
            setUpGridPoint(ctx, board["lastMove"]["end"]["col"], board["lastMove"]["end"]["row"], "blue");
            ctx.globalAlpha = 1.0;
        }

        var pieces = data["board"]["data"]["pieces"];
        for (var i = 0; i < 8; i++) {
            for (var j = 0; j < 8; j++) {
                if (pieces[j][i] != undefined) {
                    setPiece(ctx, j, i,
                        getImage(pieces[j][i]["type"], pieces[j][i]["color"]));
                }
            }
        }
    }

    function draw(force) {
        if (force === undefined) {
            force = false;
        }
        getAjax(force);
    }

    function getImage(type, color) {
        var colorPrefix;
        if (color === "WHITE") {
            colorPrefix = 'w';
        } else {
            colorPrefix = 'b';
        }
        return image[colorPrefix + type.toLowerCase().capitalizeFirstLetter()];
    }


    function setPiece(ctx, x, y, img) {
        var h = img.height * (tileSize / preferredTileSize);
        var w = img.width * (tileSize / preferredTileSize);
        ctx.drawImage(img,
            tileSize * x,
            tileSize * y,
            w,
            h);
    }

    function drawTile(ctx, x, y, transparent, img, color) {
        if (img === undefined && color === undefined) {
            var data = getPieceData(Math.floor(x), Math.floor(y));
            if (data == undefined) {
                return;
            }
            if (data.empty) {
                color = data.boardColor;
            } else {
                img = data.image;
                color = data.boardColor;
            }
        }
        if (!transparent) {
            if (img == undefined) {
                setUpGridPoint(ctx, x, y, color);
            }
        } else if (img != undefined) {
            setPiece(ctx, x, y, img);
        }
    }

    function getPieceData(row, col) {
        if (lastAjax === undefined) {
            return undefined;
        }
        var piece = lastAjax["board"]["data"]["pieces"][row][col];
        var boardColor = (lastAjax["board"]["boardColors"][row][col] == "BLACK") ? "darkgray" : "lightgray";
        if (piece == undefined || piece == "null") {
            return {
                empty: true,
                boardColor: boardColor
            };
        }
        return {
            empty: false,
            boardColor: boardColor,
            color: piece["color"],
            type: piece["type"],
            image: getImage(piece["type"], piece["color"])
        }

    }

    function invertColor(color) {
        if (color === "darkgray") {
            return "lightgray";
        }
        return "darkgray";
    }

    function setUpGridPoint(ctx, x, y, color) {
        ctx.fillStyle = color;
        ctx.fillRect(tileSize * x, tileSize * y, tileSize, tileSize);
    }

    function setUpGrid(ctx) {
        var color = "lightgray";
        for (var i = 0; i < 8; i++) {
            for (var j = 0; j < 8; j++) {
                setUpGridPoint(ctx, j, i, color);
                color = invertColor(color);
            }
            color = invertColor(color);
        }
    }

    function loadImages(sources, callback) {
        image = {};
        var loadedImages = 0;
        var numImages = 0;
        // get num of sources
        for (var source in sources) {
            numImages++;
        }
        for (var src in sources) {
            image[src] = new Image();
            image[src].onload = function () {
                if (++loadedImages >= numImages) {
                    if (callback) {
                        callback(image);
                    }
                }
            };
            image[src].src = sources[src];
        }
    }

    function notify() {
        try {
            $.notify("Your opponent has made a move!", {
                animate: {
                    enter: 'animated bounceIn',
                    exit: 'animated bounceOut'
                },
                type: 'info',
                timer: 3500
            });
        } catch (e) {

        }
        if (!document.hasFocus() || !("Notification" in window)) {
            return;
        }
        Notification.requestPermission().then(function (result) {
            if (result === "granted") {
                new Notification("Your opponent has made a move!");
            }
        });
    }
};


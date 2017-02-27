const tileHeight = 80;
const tileWidth = 80;

const imgBaseURL = "/";
const imgSources = {
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

function Action(startX, startY, endX, endY) {
  this.startX = startX;
  this.startY = startY;
  this.endX = endX;
  this.endY = endY;
  this.send = function send() {
    $.post("/submit.json", JSON.stringify(this));
  }
}

String.prototype.capitalizeFirstLetter = function () {
  return this.charAt(0).toUpperCase() + this.slice(1);
};

$(function () {
  window.navigator.vibrate([200, 100, 200]);
  var canvas = document.getElementById('canvas');
  var clickStart;
  var clickEnd;
  var lastAjax;

  $("canvas").on("click", function () {
    var rect = canvas.getBoundingClientRect();
    var x = event.clientX - rect.left;
    var y = event.clientY - rect.top;
    if (clickStart) {
      clickEnd = {
        x: Math.floor(x / tileWidth),
        y: Math.floor(y / tileHeight)
      };
      var action = new Action(clickStart.x, clickStart.y, clickEnd.y,
          clickEnd.x);
      action.send();
      clickStart = undefined;
      clickEnd = undefined;
    } else {
      clickStart = {
        x: Math.floor(x / tileWidth),
        y: Math.floor(y / tileHeight)
      };
    }
  });

  var ctx;
  if (canvas.getContext) {
    ctx = canvas.getContext('2d');
    ctx.canvas.width = window.innerWidth;
    ctx.canvas.height = window.innerHeight;
    game(ctx);
  } else {
    alert("Your browser is not supported.");
  }

  var errorLast = false;

  function getAjax() {
    var curUrl = window.location.href;
    var url;
    if (curUrl.endsWith("/play.html")) {
      url = "/chess.json";
    } else {
      url = curUrl + ".json";
    }
    $.ajax(url, {
      dataType: "json",
      success: function (data) {
        if (_.isEqual(lastAjax, data)) {
          return;
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
        var id = data["gameID"]["id"];
        history.pushState(undefined, "Chess", "/game/" + id);
        notify();
        lastAjax = data;
        setUpGrid(ctx);
        loadBoard(data);
      },
      error: function () {
        if (errorLast) {
          return;
        }
        $.notify("An error has occurred and connection has been lost.", {
          animate: {
            enter: 'animated bounceIn',
            exit: 'animated bounceOut'
          },
          type: 'danger',
          timer: 3500
        });
        errorLast = true;
      }

    })
  }

  setInterval(draw, 1000);

  function loadBoard(jsonObject) {
    var pieces = jsonObject["board"]["pieces"];
    for (var i = 0; i < 8; i++) {
      for (var j = 0; j < 8; j++) {
        if (pieces[i][j] != undefined) {
          setPiece(ctx, i, j,
              getImage(pieces[i][j]["type"], pieces[i][j]["color"]));
        }
      }
    }
  }

  function draw() {
    getAjax();
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

  var image;

  function game(ctx) {
    setUpGrid(ctx);
    loadImages(imgSources);
  }

  function setPiece(ctx, x, y, img) {
    ctx.drawImage(img, tileHeight * x, tileWidth * y);
  }

  function invertColor(color) {
    if (color === "darkgray") {
      return "lightgray";
    }
    return "darkgray";
  }

  function setUpGridPoint(ctx, x, y, color) {
    ctx.fillStyle = color;
    ctx.fillRect(tileHeight * x, tileWidth * y, tileWidth, tileHeight);
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
    $.notify("Your opponent has made a move!", {
      animate: {
        enter: 'animated bounceIn',
        exit: 'animated bounceOut'
      },
      type: 'info',
      timer: 3500
    });
    if (!document.hasFocus() || !("Notification" in window)) {
      return;
    }
    Notification.requestPermission().then(function (result) {
      if (result === "granted") {
        var notification = new Notification("Your opponent has made a move!");
      }
    });
  }
});

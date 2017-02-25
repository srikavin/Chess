const tileHeight = 80;
const tileWidth = 80;

const imgSources = {
  wRook: "sprite/wRook.svg",
  wBishop: "sprite/wBishop.svg",
  wKnight: "sprite/wKnight.svg",
  wKing: "sprite/wKing.svg",
  wQueen: "sprite/wQueen.svg",
  wPawn: "sprite/wPawn.svg",
  bRook: "sprite/bRook.svg",
  bBishop: "sprite/bBishop.svg",
  bKnight: "sprite/bKnight.svg",
  bKing: "sprite/bKing.svg",
  bQueen: "sprite/bQueen.svg",
  bPawn: "sprite/bPawn.svg"
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
    console.log("x: " + x + " y: " + y);
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

  function getAjax() {
    $.ajax("/chess.json", {
      dataType: "json",
      success: function (data) {
        if (_.isEqual(lastAjax, data)) {
          return;
        }
        notify();
        lastAjax = data;
        setUpGrid(ctx);
        loadBoard(data);
      }
    })
  }

  setInterval(draw, 750);

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
    if (color == "WHITE") {
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
    // Let's check if the browser supports notifications
    if (!("Notification" in window)) {
    }

    // Let's check whether notification permissions have already been granted
    else if (Notification.permission === "granted") {
      // If it's okay let's create a notification
      var notification = new Notification("Your opponent has made a move!");
    }

    // Otherwise, we need to ask the user for permission
    else if (Notification.permission !== 'denied') {
      Notification.requestPermission(function (permission) {
        // If the user accepts, let's create a notification
        if (permission === "granted") {
          var notification = new Notification("Your opponent has made a move!");
        }
      });
    }
  }
});

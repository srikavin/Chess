package me.infuzion.chess.web.listener;

import com.google.gson.JsonObject;
import me.infuzion.chess.game.util.Identifier;
import me.infuzion.chess.game.util.RandomStringGenerator;
import me.infuzion.chess.web.dao.impl.UserDatabase;
import me.infuzion.chess.web.domain.User;
import me.infuzion.chess.web.event.helper.RequestUser;
import me.infuzion.chess.web.event.helper.RequiresAuthentication;
import me.infuzion.web.server.EventListener;
import me.infuzion.web.server.event.def.PageRequestEvent;
import me.infuzion.web.server.event.reflect.EventHandler;
import me.infuzion.web.server.event.reflect.Route;
import me.infuzion.web.server.event.reflect.param.mapper.impl.BodyParam;
import me.infuzion.web.server.event.reflect.param.mapper.impl.Response;
import me.infuzion.web.server.event.reflect.param.mapper.impl.UrlParam;
import me.infuzion.web.server.http.HttpResponse;
import me.infuzion.web.server.router.RouteMethod;
import org.apache.commons.io.output.ByteArrayOutputStream;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ChessUserProfileListener implements EventListener {
    private final static RandomStringGenerator randomStringGenerator = new RandomStringGenerator(32);
    private final static Font font;
    private final static Rectangle errorRect = new Rectangle(0, 206, 640, 228);

    static {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        Font tmp;
        try {
            Font rFont = Font.createFont(Font.TRUETYPE_FONT, ChessUserProfileListener.class
                    .getResourceAsStream("/fonts/PTSans55F.ttf"));
            ge.registerFont(rFont);
            tmp = rFont;

        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
            tmp = Font.getFont(Font.SANS_SERIF);
        }
        font = tmp.deriveFont(95f);
    }

    private final UserDatabase database;

    public ChessUserProfileListener(UserDatabase database) {
        this.database = database;
    }

    private static byte[] drawErrorString(String text) throws IOException {
        BufferedImage img = ImageIO.read(new Object() {
        }.getClass().getEnclosingClass().getResourceAsStream("/images/error/user-image-not-found.png"));
        Graphics2D g = (Graphics2D) img.getGraphics();

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        FontMetrics metrics = g.getFontMetrics(font);
        int x = errorRect.x + (errorRect.width - metrics.stringWidth(text)) / 2;
        int y = errorRect.y + ((errorRect.height - metrics.getHeight()) / 2) + metrics.getAscent();
        g.setFont(font);
        g.drawString(text, x, y);
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream(10000)) {
            ImageIO.write(img, "png", baos);
            return baos.toByteArray();
        }
    }

    @EventHandler
    @Route("/api/v1/users/:user_id")
    @Response("application/json")
    public JsonObject getUserById(PageRequestEvent event, @UrlParam("user_id") String id) {
        User user = database.getUser(new Identifier(id));

        if (user == null) {
            event.getResponse().setStatusCode(404);
            return new JsonObject();
        }

        return user.toJson();
    }

    @EventHandler
    @Route("/api/v1/users/")
    @Response("application/json")
    public JsonObject getUserByUsername(PageRequestEvent event, @UrlParam("username") String username) {
        User user = database.getUser(username);

        if (user == null) {
            event.getResponse().setStatusCode(404);
            return new JsonObject();
        }

        return user.toJson();
    }

    @EventHandler
    @Route("/api/v1/users/:user_id/preview")
    @Response(value = "image/png", raw = true)
    public byte[] onImageGet(PageRequestEvent event, @UrlParam("user_id") String userId) throws IOException {
        HttpResponse response = event.getResponse();
        User user = database.getUser(new Identifier(userId));
        if (user != null) {
            Path pathToImage = Paths.get(user.getImagePath());
            byte[] image;

            if (pathToImage.equals(Path.of("/images/unknown.png"))) {
                image = drawErrorString(user.getUsername());
            } else {
                image = Files.readAllBytes(pathToImage);
            }
            return image;
        } else {
            response.setStatusCode(404);
            return drawErrorString("user not found");
        }
    }

    @EventHandler(PageRequestEvent.class)
    @Route(value = "/api/v1/me/image", methods = RouteMethod.POST)
    @Response("application/json")
    @RequiresAuthentication
    public JsonObject onImageUpdate(@RequestUser User user, @BodyParam(raw = true) ByteBuffer image) {
        JsonObject object = new JsonObject();
        try {
            Path path = Paths.get(".", "images");
            Files.createDirectories(path);

            String imagePath = randomStringGenerator.nextString() + ".png";

            path = path.resolve(imagePath);

            RandomAccessFile file = new RandomAccessFile(path.toFile(), "rw");
            FileChannel channel = file.getChannel();

            channel.write(image);
            channel.close();
            file.close();

            database.updateImagePath(user.getIdentifier(), path.toString());
            object.addProperty("success", true);
        } catch (IOException e) {
            e.printStackTrace();
            object.addProperty("error", "invalid image");
        }
        return object;
    }
}

package me.infuzion.chess.web.listener;

import com.google.gson.JsonObject;
import me.infuzion.chess.util.ChessUtilities;
import me.infuzion.chess.util.Identifier;
import me.infuzion.chess.util.RandomString;
import me.infuzion.chess.web.event.AuthenticatedPageRequestEvent;
import me.infuzion.chess.web.event.EndPointURL;
import me.infuzion.chess.web.game.User;
import me.infuzion.chess.web.record.source.UserDatabase;
import me.infuzion.web.server.EventListener;
import me.infuzion.web.server.event.def.PageRequestEvent;
import me.infuzion.web.server.event.reflect.EventHandler;
import me.infuzion.web.server.event.reflect.Route;
import me.infuzion.web.server.util.HTTPMethod;
import org.apache.commons.io.output.ByteArrayOutputStream;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

public class ChessUserListener implements EventListener {
    private final static RandomString randomStrings = new RandomString(32);
    private final static Font font;
    private final static Rectangle errorRect = new Rectangle(0, 206, 640, 228);

    static {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        Font tmp;
        try {
            Font rFont = Font.createFont(Font.TRUETYPE_FONT,
                    new Object() {
                    }.getClass().getEnclosingClass().getResourceAsStream("/fonts/PTSans55F.ttf"));
            ge.registerFont(rFont);
            tmp = rFont;

        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
            tmp = Font.getFont(Font.SANS_SERIF);
        }
        font = tmp.deriveFont(95f);
    }

    private final UserDatabase database;

    public ChessUserListener(UserDatabase database) {
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
    @Route(path = "/api/v1/users/:user_id/preview")
    public void onImageGet(PageRequestEvent event, Map<String, String> map) {
        User user = database.getUser(new Identifier(map.get("user_id")));
        if (user != null) {
            try {
                Path pathToImage = Paths.get(user.getImagePath());
                System.out.println(pathToImage.toString());
                byte[] image;

                if (pathToImage.toString().equals("/images/unknown.png")) {
                    image = drawErrorString(user.getUsername());

                } else {
                    image = Files.readAllBytes(pathToImage);
                }
                event.setResponseData(image);
                event.setContentType("image/png");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            event.setStatusCode(404);
            try {
                event.setResponseData(drawErrorString("user not found"));
                event.setContentType("image/png");
            } catch (IOException e) {
                JsonObject object = new JsonObject();
                object.addProperty("error", "user not found");
                event.setResponseData(ChessUtilities.gson.toJson(object));
            }
        }
    }

    @EventHandler
    public void onImageUpdate(AuthenticatedPageRequestEvent event) {
        if (event.getEvent().getPage().equalsIgnoreCase(EndPointURL.USER_IMAGE_CHANGE) && event.getEvent().getMethod() == HTTPMethod.POST) {
            JsonObject object = new JsonObject();
            try {
                byte[] image = event.getEvent().getRawMultipartFormData().get("file");
                if (ImageIO.read(new ByteArrayInputStream(image)) == null) {
                    throw new IOException("invalid image");
                }
                Path path = Paths.get(".", "images");
                Files.createDirectories(path);
                String imagePath = randomStrings.nextString();

                path = path.resolve(imagePath + ".png");
                Files.createFile(path);
                Files.write(path, image);
                database.updateImagePath(event.getId(), path.toString());
                object.addProperty("success", true);
            } catch (IOException e) {
                e.printStackTrace();
                object.addProperty("error", "invaid image");
            }
            event.getEvent().setResponseData(ChessUtilities.gson.toJson(object));
        }
    }
}

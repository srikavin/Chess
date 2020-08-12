package me.infuzion.chess.web;

import me.infuzion.chess.util.ChessUtilities;
import me.infuzion.web.server.Server;
import me.infuzion.web.server.event.reflect.param.DefaultTypeConverter;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URISyntaxException;

public class Main {

    public static void main(String[] args) throws IOException, URISyntaxException {
        String path = Main.class
                .getClassLoader()
                .getResource("logging.properties")
                .getFile();
        System.setProperty("java.util.logging.config.file", path);

        int port = 37629;

        if (args.length == 1) {
            port = Integer.parseInt(args[0]);
        }

        Server server = new Server(new InetSocketAddress("0.0.0.0", port), new DefaultTypeConverter(ChessUtilities.gson));

        new Chess(server);
    }
}

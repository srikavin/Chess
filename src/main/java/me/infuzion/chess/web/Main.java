package me.infuzion.chess.web;

import me.infuzion.web.server.Server;

import java.io.IOException;
import java.net.InetSocketAddress;

public class Main {

    public static void main(String[] args) throws IOException {
        String path = Main.class
                .getClassLoader()
                .getResource("logging.properties")
                .getFile();
        System.setProperty("java.util.logging.config.file", path);

        Server server = new Server(new InetSocketAddress("0.0.0.0", 37629));
        new Chess(server);
    }
}

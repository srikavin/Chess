package me.infuzion.chess;

import java.io.IOException;
import java.net.ServerSocket;
import me.infuzion.web.server.Server;

public class Main {

    public static void main(String[] args) throws IOException {
        ServerSocket socket = new ServerSocket(80);
        Server server = new Server(socket);
        Thread serverThread = new Thread(server);
        serverThread.start();
        new Chess(server);
    }
}

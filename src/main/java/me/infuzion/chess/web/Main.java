package me.infuzion.chess.web;

import me.infuzion.web.server.Server;

import java.io.IOException;
import java.net.ServerSocket;

public class Main {

    public static void main(String[] args) throws IOException {
        ServerSocket socket = new ServerSocket(37628);
        Server server = new Server(socket);
        Thread serverThread = new Thread(server);
        serverThread.start();
        new Chess(server);
    }
}

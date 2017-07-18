package me.infuzion.chess.util;

public abstract class Database {
    static {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("FAILED TO LOAD SQLITE JDBC");
            System.exit(-1);
        }
    }
}

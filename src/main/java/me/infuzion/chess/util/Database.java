package me.infuzion.chess.util;

public abstract class Database {
    static {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to load Postgres JDBC");
            System.exit(-1);
        }
    }
}

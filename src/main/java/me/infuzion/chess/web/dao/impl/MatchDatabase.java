package me.infuzion.chess.web.dao.impl;

import me.infuzion.chess.game.board.ChessMove;
import me.infuzion.chess.game.board.ChessPosition;
import me.infuzion.chess.game.piece.PieceType;
import me.infuzion.chess.game.util.Identifier;
import me.infuzion.chess.web.dao.MatchDao;
import me.infuzion.chess.web.domain.Game;
import me.infuzion.chess.web.domain.GameStatus;
import org.jetbrains.annotations.NotNull;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class MatchDatabase extends Database implements MatchDao {
    private final DataSource source;

    public MatchDatabase(DataSource source) {
        this.source = source;

        DBHelper.executeStatement(source, "CREATE TABLE IF NOT EXISTS MATCH_STATUS" +
                "(ID        SERIAL       PRIMARY KEY  NOT NULL," +
                "NAME       VARCHAR(32)   UNIQUE)");

        DBHelper.prepareStatement(source, "INSERT INTO MATCH_STATUS (NAME) VALUES (?) ON CONFLICT DO NOTHING ", ps -> {
            for (GameStatus e : GameStatus.values) {
                ps.setString(1, e.name());
                ps.addBatch();
            }
            ps.executeBatch();

        });

        DBHelper.executeStatement(source, "CREATE TABLE IF NOT EXISTS MATCHES"
                + "(ID              VARCHAR(16)  PRIMARY KEY NOT NULL, "
                + "PLAYER_WHITE     VARCHAR(36),"
                + "PLAYER_BLACK     VARCHAR(36),"
                + "STATUS           INTEGER,"
                + "INITIAL_FEN      VARCHAR(128),"
                + "CURRENT_FEN      VARCHAR(128),"
                + "FOREIGN KEY (STATUS)          REFERENCES MATCH_STATUS(ID),"
                + "FOREIGN KEY (PLAYER_WHITE)    REFERENCES USERS(ID),"
                + "FOREIGN KEY (PLAYER_BLACK)    REFERENCES USERS(ID))");

        DBHelper.executeStatement(source,
                "CREATE TABLE IF NOT EXISTS MOVES"
                        + "(ID            SERIAL         PRIMARY KEY NOT NULL, "
                        + "MATCH_ID       VARCHAR(16),"
                        + "FOREIGN KEY (MATCH_ID)        REFERENCES MATCHES(ID),"
                        + "PLY            INTEGER,"
                        + "FROM_SQUARE    VARCHAR(2),"
                        + "TO_SQUARE      VARCHAR(2),"
                        + "PROMOTION      VARCHAR(16)    DEFAULT NULL)");
    }

    private ResultSetMapper<Game> generateFirstElementMapper(List<ChessMove> moves) {
        return (rs) -> {
            Identifier id = new Identifier(rs.getString("ID"));
            String initialFen = rs.getString("INITIAL_FEN");
            String whiteIdString = rs.getString("PLAYER_WHITE");
            String blackIdString = rs.getString("PLAYER_BLACK");
            String statusName = rs.getString("STATUS_NAME");

            Identifier whitePlayer = whiteIdString == null ? null : new Identifier(whiteIdString);
            Identifier blackPlayer = blackIdString == null ? null : new Identifier(blackIdString);

            return new Game(id, initialFen, moves, whitePlayer, blackPlayer, GameStatus.valueOf(statusName));
        };
    }

    private Game mapResultSetWithoutMoves(ResultSet rs) throws SQLException {
        Identifier id = new Identifier(rs.getString("ID"));
        String initialFen = rs.getString("INITIAL_FEN");
        String whiteIdString = rs.getString("PLAYER_WHITE");
        String blackIdString = rs.getString("PLAYER_BLACK");
        String statusName = rs.getString("STATUS_NAME");
        String currentFen = rs.getString("CURRENT_FEN");

        Identifier whitePlayer = whiteIdString == null ? null : new Identifier(whiteIdString);
        Identifier blackPlayer = blackIdString == null ? null : new Identifier(blackIdString);

        return new Game(id, initialFen, currentFen, whitePlayer, blackPlayer, GameStatus.valueOf(statusName));
    }

    public List<Game> getMatches(int limit) {
        return DBHelper.transaction(source, connection -> {
            return DBHelper.prepareStatement(source,
                    "SELECT matches.ID, PLAYER_WHITE, PLAYER_BLACK, INITIAL_FEN, CURRENT_FEN, match_status.name AS STATUS_NAME " +
                            "FROM MATCHES " +
                            "JOIN match_status " +
                            "ON matches.STATUS = match_status.ID " +
                            "LIMIT ?", ps -> {
                        ps.setInt(1, limit);
                        ps.executeQuery();
                        return DBHelper.mapElements(ps.executeQuery(), this::mapResultSetWithoutMoves);
                    });
        });
    }

    @Override
    public List<Game> getRecentMatchesForUser(Identifier user, int limit) {
        return DBHelper.prepareStatement(source,
                "SELECT matches.ID, PLAYER_WHITE, PLAYER_BLACK, INITIAL_FEN, CURRENT_FEN, match_status.name AS STATUS_NAME " +
                        "FROM MATCHES " +
                        "JOIN match_status " +
                        "ON matches.STATUS = match_status.ID " +
                        "WHERE matches.player_white = ? OR matches.player_black = ? " +
                        "LIMIT ?", ps -> {
                    ps.setString(1, user.getId());
                    ps.setString(2, user.getId());
                    ps.setInt(3, limit);
                    return DBHelper.mapElements(ps.executeQuery(), this::mapResultSetWithoutMoves);
                });
    }

    @Override
    public Game updateAndAddMove(Game game, ChessMove move) {
        DBHelper.transaction(source, connection -> {
            DBHelper.prepareStatement(connection,
                    "INSERT INTO moves(id, match_id, ply, from_square, to_square, promotion) VALUES (DEFAULT, ?, ?, ?, ? ,?);",
                    ps -> {
                        ps.setString(1, game.getId().getId());
                        ps.setInt(2, game.getBoard().getCurrentPly());
                        ps.setString(3, move.getSource().getPosition());
                        ps.setString(4, move.getEnd().getPosition());
                        ps.setString(5, move.getPromotion() == null ? null : move.getPromotion().name());
                        ps.execute();
                    });
            updateMatch(connection, game);
        });

        return getMatch(game.getId());
    }

    private List<ChessMove> getMovesForMatch(Connection connection, @NotNull Identifier matchId) throws SQLException {
        return DBHelper.prepareStatement(connection,
                "SELECT id, match_id, ply, from_square, to_square, promotion FROM moves WHERE match_id = ? ORDER BY ply",
                ps -> {
                    ps.setString(1, matchId.getId());

                    return DBHelper.mapElements(ps.executeQuery(), this::mapChessMove);
                });
    }

    public Game getMatch(@NotNull Identifier id) {
        return DBHelper.transaction(source, connection -> {
            connection.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);

            List<ChessMove> moves = getMovesForMatch(connection, id);

            return DBHelper.prepareStatement(connection,
                    "SELECT matches.ID, PLAYER_WHITE, PLAYER_BLACK, INITIAL_FEN, match_status.name AS STATUS_NAME FROM MATCHES " +
                            "JOIN match_status " +
                            "ON matches.STATUS = match_status.ID " +
                            "WHERE matches.id = ?", ps -> {

                        ps.setString(1, id.getId());

                        return DBHelper.mapFirstElement(ps.executeQuery(), generateFirstElementMapper(moves));
                    }
            );
        });
    }

    private ChessMove mapChessMove(ResultSet rs) throws SQLException {
        String from = rs.getString("from_square");
        String to = rs.getString("to_square");
        String pieceType = rs.getString("promotion");

        PieceType type = pieceType == null ? null : PieceType.valueOf(pieceType);

        return new ChessMove(new ChessPosition(from), new ChessPosition(to), type);
    }

    private void updateMatch(Connection connection, Game game) throws SQLException {
        DBHelper.prepareStatement(connection, "UPDATE matches " +
                "SET player_white = ?, player_black = ?, status = (SELECT match_status.ID FROM match_status WHERE match_status.name = ?), current_fen = ?" +
                "WHERE id = ?", ps -> {

            ps.setString(1, game.getPlayerWhite() == null ? null : game.getPlayerWhite().getId());
            ps.setString(2, game.getPlayerBlack() == null ? null : game.getPlayerBlack().getId());
            ps.setString(3, game.getStatus().name());
            ps.setString(4, game.getCurrentFen());
            ps.setString(5, game.getId().getId());

            ps.execute();
        });
    }

    public Game updateMatch(Game game) {
        DBHelper.transaction(source, connection -> {
            updateMatch(connection, game);
        });

        return getMatch(game.getId());
    }

    @Override
    public Game newMatch(Game game) {
        DBHelper.transaction(source, connection -> {
            DBHelper.prepareStatement(connection, "INSERT INTO matches(ID, PLAYER_WHITE, PLAYER_BLACK, STATUS, INITIAL_FEN, CURRENT_FEN) " +
                            "VALUES (?, ?, ?, (SELECT match_status.ID FROM match_status WHERE match_status.name = ?), ?, ?)",
                    ps -> {
                        ps.setString(1, game.getId().getId());
                        ps.setString(2, game.getPlayerWhite() == null ? null : game.getPlayerWhite().getId());
                        ps.setString(3, game.getPlayerBlack() == null ? null : game.getPlayerBlack().getId());
                        ps.setString(4, game.getStatus().name());
                        ps.setString(5, game.getInitialFen());
                        ps.setString(6, game.getCurrentFen());

                        ps.execute();
                    });
        });

        return getMatch(game.getId());
    }
}

/*
 * Copyright 2020 Srikavin Ramkumar
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.infuzion.chess.web.domain.service;

import me.infuzion.chess.board.ChessMove;
import me.infuzion.chess.piece.Color;
import me.infuzion.chess.util.Identifier;
import me.infuzion.chess.web.dao.MatchDao;
import me.infuzion.chess.web.domain.Game;
import me.infuzion.chess.web.domain.GameStatus;
import org.jetbrains.annotations.NotNull;

import java.security.SecureRandom;
import java.util.List;

public class GameService {
    private final MatchDao matchDao;
    private final ThreadLocal<SecureRandom> randomThreadLocal = ThreadLocal.withInitial(SecureRandom::new);

    public GameService(MatchDao matchDao) {
        this.matchDao = matchDao;
    }

    /**
     * Creates a game in the {@link me.infuzion.chess.web.domain.GameStatus#WAITING} state with the specified FEN. The
     * given player will be assigned to white/black player depending on the parameter.
     *
     * @param initialFen  The starting FEN of the board
     * @param player      The player who created this game
     * @param playerColor The color the player wishes to play
     * @return A game in the {@link me.infuzion.chess.web.domain.GameStatus#WAITING} state
     */
    public Game createGame(@NotNull String initialFen, @NotNull Identifier player, @NotNull Color playerColor) {
        return matchDao.newMatch(new Game(new Identifier(), initialFen, player, playerColor, initialFen));
    }

    public Game getGame(@NotNull Identifier identifier) {
        return matchDao.getMatch(identifier);
    }

    public List<Game> getRecentGameForUser(@NotNull Identifier user, int limit) {
        return matchDao.getRecentMatchesForUser(user, limit);
    }

    public List<Game> getActiveGames(int limit) {
        return matchDao.getMatches(limit);
    }

    /**
     * Attempts to add the given player to the game. This method assumes that a player with the given id exists.
     *
     * @param gameId The id of the game to add the player to
     * @param player The id of the player to add.
     * @return True if the player was successfully added, false otherwise
     */
    public boolean addPlayerToGame(@NotNull Identifier gameId, @NotNull Identifier player) {
        Game game = matchDao.getMatch(gameId);

        if (game == null) {
            return false;
        }

        Identifier whiteSide = game.getPlayerWhite();
        Identifier blackSide = game.getPlayerBlack();

        if (game.getStatus() != GameStatus.WAITING || (whiteSide != null && blackSide != null)) {
            return false;
        }

        if (player.equals(whiteSide) || player.equals(blackSide)) {
            return false;
        }

        if (whiteSide == null && blackSide == null) {
            if (randomThreadLocal.get().nextBoolean()) {
                game.setPlayerWhite(player);
            } else {
                game.setPlayerBlack(player);
            }
        } else if (blackSide == null) {
            game.setPlayerBlack(player);
        } else {
            game.setPlayerWhite(player);
        }

        if (game.getPlayerWhite() != null && game.getPlayerBlack() != null) {
            game.setStatus(GameStatus.IN_PROGRESS_WHITE);
        }

        matchDao.updateMatch(game);

        return true;
    }

    public boolean addMove(@NotNull Identifier matchId, @NotNull Identifier playerId, @NotNull ChessMove move) {
        long start3 = System.currentTimeMillis();
        Game game = matchDao.getMatch(matchId);
        long start = System.currentTimeMillis();

        if (game == null || game.getStatus() == GameStatus.WAITING) {
            return false;
        }

        if ((game.getStatus() == GameStatus.IN_PROGRESS_WHITE && !game.getPlayerWhite().equals(playerId)) ||
                (game.getStatus() == GameStatus.IN_PROGRESS_BLACK && !game.getPlayerBlack().equals(playerId))) {
            System.out.println(1);
            return false;
        }

        boolean moveAllowed = game.getBoard().move(move);

        if (!moveAllowed) {
            System.out.println(2);
            return false;
        }

        if (game.getStatus() == GameStatus.IN_PROGRESS_WHITE) {
            game.setStatus(GameStatus.IN_PROGRESS_BLACK);
        } else if (game.getStatus() == GameStatus.IN_PROGRESS_BLACK) {
            game.setStatus(GameStatus.IN_PROGRESS_WHITE);
        }

        game.setCurrentFen(game.getBoard().toFen());
        long start2 = System.currentTimeMillis();
        matchDao.updateAndAddMove(game, move);

        long end = System.currentTimeMillis();
        System.out.println("verify: " + (start2 - start) + " fetch: " + (start - start3) + " persist: " + (end - start2) + " total: " + (end - start));

        return true;
    }
}

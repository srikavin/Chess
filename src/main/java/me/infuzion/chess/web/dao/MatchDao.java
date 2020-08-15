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

package me.infuzion.chess.web.dao;

import me.infuzion.chess.game.board.ChessMove;
import me.infuzion.chess.game.util.Identifier;
import me.infuzion.chess.web.domain.Game;

import java.util.List;

public interface MatchDao {
    Game getMatch(Identifier identifier);

    List<Game> getMatches(int limit);

    List<Game> getRecentMatchesForUser(Identifier user, int limit);

    /**
     * Updates the persisted copy of the game as well as persisting the given move using the current information in the
     * game object. This is equivalent to calling {@link #updateMatch(Game)} and also persisting the move information.
     *
     * @param game The updated game object to persist
     * @param move The move object to persist
     * @return A copy of the game object as it was persisted
     */
    Game updateAndAddMove(Game game, ChessMove move);

    /**
     * Persists a copy of the given object and updating any conflicting information (this does not persist any move information).
     *
     * @param game The updated game object to persist
     * @return A copy of the game object as it was persisted
     */
    Game updateMatch(Game game);

    /**
     * Persists a copy of the given Game object without storing any move information (only information within the game
     * object itself is stored).
     *
     * @param game The game object to persist
     * @return A copy of the game object as it was persisted
     */
    Game newMatch(Game game);
}

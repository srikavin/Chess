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

package me.infuzion.chess.web.domain;

import me.infuzion.chess.game.board.ChessBoard;
import me.infuzion.chess.game.board.ChessMove;
import me.infuzion.chess.game.piece.Color;
import me.infuzion.chess.game.util.Identifier;
import me.infuzion.chess.web.game.Visibility;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class Game {
    private transient final ChessBoard board;

    private final Identifier id;
    private final String initialFen;
    private final Visibility visibility;

    @Nullable
    private final List<@NotNull ChessMove> moves;

    private String currentFen;
    private Identifier playerWhite;
    private Identifier playerBlack;
    private GameStatus status;
    public Game(@NotNull Identifier id, @NotNull String initialFen, @NotNull List<@NotNull ChessMove> moves,
                @Nullable Identifier playerWhite, @Nullable Identifier playerBlack, @NotNull GameStatus status) {
        this.id = id;
        this.playerWhite = playerWhite;
        this.playerBlack = playerBlack;
        this.status = status;
        this.initialFen = initialFen;

        visibility = Visibility.PUBLIC;
        this.board = ChessBoard.fromInitialFen(initialFen, moves);
        this.currentFen = this.board.toFen();
        this.moves = moves;
    }

    public Game(@NotNull Identifier id, @NotNull String initialFen, @NotNull String currentFen,
                @Nullable Identifier playerWhite, @Nullable Identifier playerBlack, @NotNull GameStatus status) {
        this.id = id;
        this.playerWhite = playerWhite;
        this.playerBlack = playerBlack;
        this.status = status;
        this.initialFen = initialFen;

        visibility = Visibility.PUBLIC;
        this.board = ChessBoard.fromFen(currentFen);
        this.currentFen = currentFen;
        this.moves = null;
    }

    public Game(@NotNull Identifier id, @NotNull String initialFen, @NotNull Identifier player, @NotNull Color playerColor,
                String currentFen) {
        this(id,
                initialFen,
                Collections.emptyList(),
                playerColor == Color.WHITE ? player : null,
                playerColor == Color.BLACK ? player : null,
                GameStatus.WAITING);

        this.currentFen = currentFen;
    }

    public String getCurrentFen() {
        return currentFen;
    }

    public @Nullable List<ChessMove> getMoves() {
        return moves;
    }

    public void setCurrentFen(String currentFen) {
        this.currentFen = currentFen;
    }

    public String getInitialFen() {
        return initialFen;
    }

    public ChessBoard getBoard() {
        return board;
    }

    public Identifier getId() {
        return id;
    }

    public Identifier getPlayerBlack() {
        return playerBlack;
    }

    public void setPlayerBlack(Identifier playerBlack) {
        this.playerBlack = playerBlack;
    }

    public Identifier getPlayerWhite() {
        return playerWhite;
    }

    public void setPlayerWhite(Identifier playerWhite) {
        this.playerWhite = playerWhite;
    }

    public GameStatus getStatus() {
        return status;
    }

    public void setStatus(GameStatus status) {
        this.status = status;
    }

    public Visibility getVisibility() {
        return visibility;
    }
}

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

import me.infuzion.chess.board.BoardData;
import me.infuzion.chess.board.ChessBoard;
import me.infuzion.chess.piece.ChessPiece;
import me.infuzion.chess.piece.PieceType;
import org.apache.commons.io.output.ByteArrayOutputStream;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static me.infuzion.chess.piece.Color.BLACK;
import static me.infuzion.chess.piece.Color.WHITE;

public class GamePreviewGenerator {
    private static final Map<String, BufferedImage> pieceToImageMap = new HashMap<>();
    private static final BufferedImage defaultBoard = new BufferedImage(640, 640, BufferedImage.TYPE_INT_ARGB);
    private static final int tileSize = 80;

    static {
        try {
            String color = "w";
            for (int i = 0; i < 2; i++) {
                for (PieceType e : PieceType.values()) {
                    String name = color + e.getAbbreviation();
                    BufferedImage image = ImageIO.read(Objects.requireNonNull(Game.class.getClassLoader().getResourceAsStream("images/pieces/" + name + ".png")));
                    pieceToImageMap.put(name, image);
                }
                color = "b";
            }

            ChessBoard board = ChessBoard.getDefaultBoard();
            final java.awt.Color white = new java.awt.Color(240, 217, 181);
            final java.awt.Color black = new java.awt.Color(181, 136, 99);
            Graphics graphics = defaultBoard.getGraphics();


            me.infuzion.chess.piece.Color[][] boardColor = {
                    {WHITE, BLACK, WHITE, BLACK, WHITE, BLACK, WHITE, BLACK},
                    {BLACK, WHITE, BLACK, WHITE, BLACK, WHITE, BLACK, WHITE},
                    {WHITE, BLACK, WHITE, BLACK, WHITE, BLACK, WHITE, BLACK},
                    {BLACK, WHITE, BLACK, WHITE, BLACK, WHITE, BLACK, WHITE},
                    {WHITE, BLACK, WHITE, BLACK, WHITE, BLACK, WHITE, BLACK},
                    {BLACK, WHITE, BLACK, WHITE, BLACK, WHITE, BLACK, WHITE},
                    {WHITE, BLACK, WHITE, BLACK, WHITE, BLACK, WHITE, BLACK},
                    {BLACK, WHITE, BLACK, WHITE, BLACK, WHITE, BLACK, WHITE},
            };

            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    graphics.setColor(boardColor[i][j] == me.infuzion.chess.piece.Color.WHITE ? white : black);
                    graphics.fillRect(i * tileSize, j * tileSize, (i + 1) * tileSize, (j + 1) * tileSize);
                }
            }
        } catch (IOException e1) {
            e1.printStackTrace();
            System.exit(-1);
        }
    }

    public static byte[] generateThumbnail(Game game) {
        BufferedImage image = new BufferedImage(640, 640, BufferedImage.TYPE_INT_ARGB);
        Graphics graphics = image.getGraphics();
        graphics.drawImage(defaultBoard, 0, 0, null);
        BoardData data = game.getBoard().getData();
        ChessPiece[][] pieces = data.getPieces();

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                ChessPiece current = pieces[j][i];
                if (current == null) {
                    continue;
                }
                String name = current.getColor() == me.infuzion.chess.piece.Color.WHITE ? "w" : "b";
                name += current.getType().getAbbreviation();
                BufferedImage pieceImage = pieceToImageMap.get(name);
                graphics.drawImage(pieceImage, i * tileSize, (7 - j) * tileSize, null);
            }
        }

        byte[] img;
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream(10000)) {
            ImageIO.write(image, "png", baos);
            img = baos.toByteArray();

        } catch (IOException e) {
            e.printStackTrace();
            return new byte[1];
        }


        return img;
    }

}

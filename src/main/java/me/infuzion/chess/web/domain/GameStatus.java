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

public enum GameStatus {
    IN_PROGRESS_WHITE(0),
    IN_PROGRESS_BLACK(1),
    ENDED_DRAW(2),
    ENDED_WHITE_WINS(3),
    ENDED_BLACK_WINS(4),
    WAITING(5);
    public static final GameStatus[] values = values();
    private final int value;

    GameStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public GameStatus valueOf(int value) {
        for (GameStatus e : values) {
            if (e.value == value) {
                return e;
            }
        }
        return null;
    }
}

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

package me.infuzion.chess.clock;

import me.infuzion.chess.game.piece.Color;

public class Clock {
    private final int whiteTimeDeciSeconds;
    private final int blackTimeDeciSeconds;
    private final Color currentActive;

    public Clock(int whiteTimeDeciSeconds, int blackTimeDeciSeconds, Color currentActive) {
        this.whiteTimeDeciSeconds = whiteTimeDeciSeconds;
        this.blackTimeDeciSeconds = blackTimeDeciSeconds;
        this.currentActive = currentActive;
    }

    @Override
    public String toString() {
        return "Clock{" +
                "whiteTimeDeciSeconds=" + whiteTimeDeciSeconds +
                ", blackTimeDeciSeconds=" + blackTimeDeciSeconds +
                '}';
    }

    public int getWhiteTimeDeciSeconds() {
        return whiteTimeDeciSeconds;
    }

    public int getBlackTimeDeciSeconds() {
        return blackTimeDeciSeconds;
    }

    public Color getCurrentActive() {
        return currentActive;
    }
}

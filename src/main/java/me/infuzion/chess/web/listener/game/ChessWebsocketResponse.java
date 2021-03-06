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

package me.infuzion.chess.web.listener.game;

public abstract class ChessWebsocketResponse {
    final String type;
    final String error;
    final boolean success;

    protected ChessWebsocketResponse(String type) {
        this.type = type;
        this.error = null;
        this.success = true;
    }

    protected ChessWebsocketResponse(String type, String error) {
        this.type = type;
        this.error = error;
        this.success = false;
    }
}

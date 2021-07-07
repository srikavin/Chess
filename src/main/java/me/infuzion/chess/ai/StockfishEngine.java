/*
 * Copyright 2021 Srikavin Ramkumar
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

package me.infuzion.chess.ai;

import me.infuzion.chess.game.util.Identifier;

import java.io.*;

public class StockfishEngine extends UciEngine {
    public static final Identifier STOCKFISH_ID = new Identifier("STOCKFISH_nZ98HP");

    private final Process process;
    private final BufferedReader reader;
    private final BufferedWriter writer;

    public StockfishEngine(String path) throws IOException {
        process = new ProcessBuilder(path).start();

        reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));

        sendUciCommand("uci");
        readUntil("uciok");
    }

    @Override
    protected void disconnect() {
        process.destroy();
    }

    @Override
    protected void sendUciCommand(String command) {
        try {
            writer.write(command);
            writer.write('\n');
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected String readUciResponse() {
        try {
            return reader.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

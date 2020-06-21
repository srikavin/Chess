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

package me.infuzion.chess.web;

import com.intuit.karate.junit5.Karate;
import me.infuzion.web.server.Server;
import org.junit.jupiter.api.BeforeAll;

import java.io.IOException;
import java.net.InetSocketAddress;

public class KarateIT {
    static Thread t;

    @BeforeAll
    static void before() {
        if (t != null) {
            t.stop();
        }

        t = new Thread(() -> {
            try {
                Server server = new Server(new InetSocketAddress("0.0.0.0", 37628));
                new Chess(server);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        //ensure that the server is stopped once tests are finished
        t.setDaemon(true);
        t.start();

    }

    @Karate.Test
    Karate testSample() {
        return Karate.run("Games").relativeTo(getClass());
    }
}

package me.infuzion.chess.util;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RandomStringTest {
    @Test
    void constructor() {
        assertThrows(IllegalArgumentException.class, () -> new RandomString(-1));
        new RandomString(2);
    }

    @Test
    void nextString() {
        int matches = 0;
        RandomString randomString = new RandomString(12);
        ArrayList<String> previous = new ArrayList<>();
        for (int i = 0; i < 10000; i++) {
            String str = randomString.nextString();
            if (previous.contains(str)) {
                matches++;
            }
            previous.add(str);
        }
        assertTrue(matches < 5);
    }

}
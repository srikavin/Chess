package me.infuzion.chess.game.util;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RandomStringGeneratorTest {
    @Test
    void constructor() {
        assertThrows(IllegalArgumentException.class, () -> new RandomStringGenerator(-1));
        assertEquals(2, new RandomStringGenerator(2).nextString().length());
        assertEquals(26, new RandomStringGenerator(26).nextString().length());
    }

    @Test
    void nextString() {
        int matches = 0;
        RandomStringGenerator randomStringGenerator = new RandomStringGenerator(12);
        ArrayList<String> previous = new ArrayList<>();
        for (int i = 0; i < 10000; i++) {
            String str = randomStringGenerator.nextString();
            if (previous.contains(str)) {
                matches++;
            }
            previous.add(str);
        }
        assertEquals(matches, 0);
    }

}
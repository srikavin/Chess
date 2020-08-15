package me.infuzion.chess.game.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class IdentifierTest {
    @Test
    void constructor() {
        new Identifier("abc");
        new Identifier();
    }

    @Test
    void getId() {
        assertEquals("abc", new Identifier("abc").getId());
        assertEquals("dcvasdwqe123", new Identifier("dcvasdwqe123").getId());
        assertEquals("a123--=.,!@#^*()!)!AKSDLWm,wbtec", new Identifier("a123--=.,!@#^*()!)!AKSDLWm,wbtec").getId());

        assertNotEquals("abc", new Identifier("abc a").getId());
        assertNotEquals("abc", new Identifier("abc ").getId());
        assertNotEquals("abc", new Identifier("abc 1230").getId());
        assertNotEquals("abc", new Identifier("abc 1230").getId());
    }

    @Test
    void equals() {
        assertEquals(new Identifier("abc"), new Identifier("abc"));
        assertEquals(new Identifier("abc123123123"), new Identifier("abc123123123"));
        assertEquals(new Identifier("123!@#"), new Identifier("123!@#"));

        assertNotEquals(new Object(), new Identifier("abc"));
        assertNotEquals(new Identifier("123"), new Identifier("abc"));
        assertNotEquals(new Identifier(""), new Identifier("abc"));
        assertNotEquals(new Identifier("ab c"), new Identifier("abc"));
    }

    @Test
    void hashCodeTest() {
        assertEquals(new Identifier("abc").hashCode(), "abc".hashCode());
        assertEquals(new Identifier("abc123123123").hashCode(), "abc123123123".hashCode());
        assertEquals(new Identifier("123!@#").hashCode(), "123!@#".hashCode());

        assertNotEquals(new Identifier("abc").hashCode(), "123".hashCode());
        assertNotEquals(new Identifier("abc").hashCode(), "".hashCode());
        assertNotEquals(new Identifier("abc").hashCode(), "ab c".hashCode());
    }

    @Test
    void toStringTest() {
        assertEquals(new Identifier("abc").toString(), "abc");
        assertEquals(new Identifier("abc123123123").toString(), "abc123123123");
        assertEquals(new Identifier("123!@#").toString(), "123!@#");

        assertNotEquals(new Identifier("abc").toString(), null);
        assertNotEquals(new Identifier("abc").toString(), "123");
        assertNotEquals(new Identifier("abc").toString(), "");
        assertNotEquals(new Identifier("abc").toString(), "ab c");
    }

}
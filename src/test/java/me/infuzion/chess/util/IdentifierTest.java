package me.infuzion.chess.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IdentifierTest {
    @Test
    void constructor() {
        assertThrows(IllegalArgumentException.class, () -> new Identifier(null));
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
        assertTrue(new Identifier("abc").equals(new Identifier("abc")));
        assertTrue(new Identifier("abc123123123").equals(new Identifier("abc123123123")));
        assertTrue(new Identifier("123!@#").equals(new Identifier("123!@#")));

        assertFalse(new Identifier("abc").equals(new Object()));
        assertFalse(new Identifier("abc").equals(new Identifier("123")));
        assertFalse(new Identifier("abc").equals(new Identifier("")));
        assertFalse(new Identifier("abc").equals(new Identifier("ab c")));
    }

    @Test
    void hashCodeTest() {
        assertEquals(new Identifier("abc").hashCode(), "abc".hashCode());
        assertEquals(new Identifier("abc123123123").hashCode(), "abc123123123".hashCode());
        assertEquals(new Identifier("123!@#").hashCode(), "123!@#".hashCode());

        assertNotEquals(new Identifier("abc").hashCode(), null);
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
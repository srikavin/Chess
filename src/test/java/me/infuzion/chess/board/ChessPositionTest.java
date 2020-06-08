package me.infuzion.chess.board;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ChessPositionTest {
    @Test
    void colCharToInt() {
        char[] inputs = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'};
        int[] values = {0, 1, 2, 3, 4, 5, 6, 7};

        for (int i = 0; i < inputs.length; i++) {
            assertEquals(values[i], ChessPosition.colCharToInt(inputs[i]));
        }

        assertThrows(IllegalArgumentException.class, () -> ChessPosition.colCharToInt('s'));
        assertThrows(IllegalArgumentException.class, () -> ChessPosition.colCharToInt('1'));
        assertThrows(IllegalArgumentException.class, () -> ChessPosition.colCharToInt('z'));
        assertThrows(IllegalArgumentException.class, () -> ChessPosition.colCharToInt('.'));

    }

    @Test
    void constructorExceptions() {
        assertThrows(IllegalArgumentException.class, () -> new ChessPosition(-1, 0));
        assertThrows(IllegalArgumentException.class, () -> new ChessPosition(8, 0));
        assertThrows(IllegalArgumentException.class, () -> new ChessPosition(0, -1));
        assertThrows(IllegalArgumentException.class, () -> new ChessPosition(0, 8));
        assertThrows(IllegalArgumentException.class, () -> new ChessPosition(-1, -1));
        assertThrows(IllegalArgumentException.class, () -> new ChessPosition(8, -1));
        assertThrows(IllegalArgumentException.class, () -> new ChessPosition(8, 8));
    }

    @Test
    void getRow() {
        int[] values = {0, 1, 2, 3, 4, 5, 6, 7};
        int[] inputs = {0, 1, 2, 3, 4, 5, 6, 7};

        for (int i = 0; i < values.length; i++) {
            ChessPosition position = new ChessPosition(inputs[i], 1);
            assertEquals(values[i], position.getRow());
        }
    }

    @Test
    void getColChar() {
        char[] values = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'};
        int[] inputs = {0, 1, 2, 3, 4, 5, 6, 7};

        for (int i = 0; i < values.length; i++) {
            ChessPosition position = new ChessPosition(1, inputs[i]);
            assertEquals(values[i], position.getColChar());
        }
    }

    @Test
    void getPosition() {
        ChessPosition position = new ChessPosition("a8");
        ChessPosition position1 = new ChessPosition("b6");
        ChessPosition position2 = new ChessPosition("e2");
        ChessPosition position3 = new ChessPosition("h1");

        assertEquals(position.getPosition(), "a8");
        assertEquals(position1.getPosition(), "b6");
        assertEquals(position2.getPosition(), "e2");
        assertEquals(position3.getPosition(), "h1");
    }

    @Test
    void getCol() {
        char[] inputs = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'};
        int[] values = {0, 1, 2, 3, 4, 5, 6, 7};

        for (int i = 0; i < values.length; i++) {
            ChessPosition position = new ChessPosition(1, inputs[i]);
            assertEquals(values[i], position.getCol());
        }
    }

    @Test
    void equals() {
        ChessPosition position = new ChessPosition("a8");
        ChessPosition position1 = new ChessPosition(0, 0);
        ChessPosition position2 = new ChessPosition(0, 1);

        assertEquals(position1, position, "Equals equivalent position object");
        assertEquals(position, position1, "Equals equivalent position object");

        assertEquals(position, position, "Equals itself");
        assertEquals(position1, position1, "Equals itself");
        assertEquals(position2, position2, "Equals itself");

        assertNotEquals(new Object(), position, "Should not be equal to `new Object()`");

        assertNotEquals(position2, position);
        assertNotEquals(position2, position1);

        assertNotEquals(position1, position2);
        assertNotEquals(position, position2);
    }

    @Test
    void toStringTest() {
        assertNotNull(new ChessPosition("a2").toString());
        assertNotNull(new ChessPosition("c7").toString());
        assertNotNull(new ChessPosition("e6").toString());
        assertNotNull(new ChessPosition("h2").toString());
    }

}
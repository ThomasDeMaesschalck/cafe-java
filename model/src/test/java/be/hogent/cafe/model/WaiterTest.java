package be.hogent.cafe.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class WaiterTest {


    private Waiter wout;
    private Waiter nathalie;
    private Waiter ilse;


    @BeforeEach
    public void setUp() {
        wout = new Waiter(1, "Peters", "Wout", "password");
        nathalie = new Waiter(2, "Segers", "Nathalie", "password");
        ilse = new Waiter(3, "Vandenbroeck", "Ilse", "password");
    }


    @Test
    public void testEquals() {
        assertNotEquals(wout, nathalie, "wout and nathalie shouldn't be equals");
        assertEquals(wout, wout, "wout and wout should be equals");
    }

    @Test
    public void testToString() {
        assertEquals("Wout Peters", wout.toString(), "Test toString() 01 failed");
        assertEquals("Ilse Vandenbroeck", ilse.toString(), "Test toString() 02 failed");

    }

}
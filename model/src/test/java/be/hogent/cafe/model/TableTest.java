package be.hogent.cafe.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TableTest {
    Table one;
    Table oneone;
    Table ten;
    private Cafe cafe;
    private Waiter wout;
    private Beverage cola;

    @BeforeEach
    public void setUp() {
        cafe = new Cafe("Cafe Thomas", 12);
        wout = new Waiter(1,"Peters", "Wout", "password");
        one = new Table(1);
        oneone = new Table(1);
        ten = new Table(10);
        cola = new Beverage ( 1,"Cola", 2.40);
        cafe.getBeverages().add(cola);
    }


    @Test
    public void testEquals() {
        assertNotEquals(one, ten, "These tables shouldn't be equals");
        assertEquals(one, one, "These tables should be equals");
        assertEquals(one, oneone, "These tables should be equals");
        assertEquals(one.hashCode(), oneone.hashCode(), "These tables should be equals");
    }

    @Test
    public void testToString(){
        cafe.addWaiter(wout);
        cafe.logIn("Wout Peters","password");
        cafe.setActiveTable(1);
        cafe.placeOrder(cola, 5);

        assertEquals("table 1", cafe.getTable(1).toString(), "Test toString() 01 failed");
    }

}

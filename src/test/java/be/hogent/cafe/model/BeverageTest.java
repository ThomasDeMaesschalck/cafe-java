package be.hogent.cafe.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class BeverageTest {

    private Beverage cola;
    private Beverage cola2;
    private Beverage leffe;

    @BeforeEach
    public void setUp() {
        cola = new Beverage ( 1,"Cola", 2.40);
        cola2 = new Beverage ( 1,"Cola", 2.40);
        leffe = new Beverage ( 3,"Leffe", 3.00);

    }

    @Test
    public void testEquals() {
        assertNotEquals(cola, leffe, "cola and leffe shouldn't be equals");
        assertEquals(cola, cola, "cola and cola should be equals");
        assertEquals(cola.hashCode(), cola2.hashCode(), "cola and cola 2 should be equals");

    }

    @Test
    public void testToString() {
        assertEquals("1. Cola 2.4", cola.toString(), "Test toString 01 failed");
        assertEquals("3. Leffe 3.0", leffe.toString(), "Test toString 01 failed");

    }

}
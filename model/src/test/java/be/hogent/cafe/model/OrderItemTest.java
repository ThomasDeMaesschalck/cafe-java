package be.hogent.cafe.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class OrderItemTest {


        private Cafe cafe;
        private Waiter wout;
        private Beverage cola;
        private Beverage leffe;
        private OrderItem order1;

        @BeforeEach
        public void setUp() throws Exception {
            cafe = new Cafe();
            wout = new Waiter(1,"Peters", "Wout", "password");
            cola = new Beverage ( 1,"Cola", 2.40);
            leffe = new Beverage ( 2,"Leffe", 3.00);
            cafe.getBeverages().add(cola);
            cafe.getBeverages().add(leffe);
            order1 = new OrderItem(cola,2);
        }

    @Test
    public void testIncreaseQty(){
        cafe.addWaiter(wout);
        cafe.logIn("Wout Peters","password");
        cafe.setActiveTable(1);
        order1.increaseQty();
        assertEquals(3, order1.getQty(), "Test increaseQty() 01 failed");
    }

    @Test
    public void testIncreaseQtyOverloaded(){
        cafe.addWaiter(wout);
        cafe.logIn("Wout Peters","password");
        cafe.setActiveTable(1);
        order1.increaseQty(5);
        assertEquals(7, order1.getQty(), "Test increaseQtyOverloaded() 01 failed");
    }

    @Test
    public void testDecreaseQty(){
        cafe.addWaiter(wout);
        cafe.logIn("Wout Peters","password");
        cafe.setActiveTable(1);
        order1.decreaseQty();
        assertEquals(1, order1.getQty(), "Test decreaseQty() 01 failed");
    }

    @Test
    public void testToString(){
        cafe.addWaiter(wout);
        cafe.logIn("Wout Peters","password");
        cafe.setActiveTable(1);
        cafe.placeOrder(cola, 5);
        cafe.placeOrder(leffe, 5);
        assertEquals("[beverage: Leffe, quantity: 5, beverage: Cola, quantity: 5]", cafe.getUnpaidOrders().get(cafe.getActiveTable()).getOrderLines().toString(), "Test toString() 01 failed");
    }

    @Test
    public void testEquals() {
        assertNotEquals(cola, leffe, "cola and leffe shouldn't be equals");
        assertEquals(cola, cola, "cola and cola should be equals");
        assertEquals(order1.hashCode(), order1.hashCode(), "order1 and order1 should be equals");

    }

}

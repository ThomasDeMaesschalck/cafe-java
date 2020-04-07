package be.hogent.cafe.model;

import be.hogent.cafe.model.dao.PaidOrderDAOImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class OrderTest {

    private Cafe cafe;
    private Waiter wout;
    private Beverage cola;
    private Beverage leffe;
    private Order order1;
    private Order order2;

    @BeforeEach
    public void setUp() throws Exception {
        cafe = new Cafe("Cafe Thomas", 12);
        wout = new Waiter(1,"Peters", "Wout", "password");
        cola = new Beverage ( 1,"Cola", 2.40);
        leffe = new Beverage ( 2,"Leffe", 3.00);
        cafe.getBeverages().add(cola);
        cafe.getBeverages().add(leffe);
        OrderItem  orderItem = new OrderItem(1, cola, 5);
        order1 = new Order(1,  LocalDate.now(), orderItem,2, 2);
        order2 = new Order(2, LocalDate.now(), orderItem, 4, 1);
    }


        @Test
    public void testToString(){
        cafe.addWaiter(wout);
        cafe.logIn("Wout Peters","password");
        cafe.setActiveTable(1);
        cafe.placeOrder(cola, 5);
        cafe.placeOrder(leffe, 5);

        LocalDate date = LocalDate.now();
        int thisOrderNumber = PaidOrderDAOImpl.getInstance().highestOrderAndIDNumber("orderNumber") + 1; //om het nieuwe ordernummer te verkrijgen voor onderstaande test
        assertEquals("Order: " + thisOrderNumber + ", date: "  + date + ", waiterID: 1, orderItems: [ID: 2, beverage: Leffe, quantity: 5, ID: 1, beverage: Cola, quantity: 5], tableID: 1", cafe.getUnpaidOrders().get(cafe.getActiveTable()).toString(), "Test toString() 01 failed");
    }


    @Test
    public void testEquals() {
        assertNotEquals(order1, order2, "order1 and order2 shouldn't be equals");
        assertEquals(order1, order1, "order1 and order1 should be equals");
        assertEquals(order1.hashCode(), order1.hashCode(), "order1 and order1 should be equals");

    }


}

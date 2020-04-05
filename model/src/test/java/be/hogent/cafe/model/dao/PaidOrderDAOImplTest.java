package be.hogent.cafe.model.dao;

import be.hogent.cafe.model.Beverage;
import be.hogent.cafe.model.Cafe;
import be.hogent.cafe.model.Order;
import be.hogent.cafe.model.OrderItem;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.*;

public class PaidOrderDAOImplTest {
    private OrderItem o1;
    private OrderItem o2;
    private OrderItem o3;
    private OrderItem o4;


    @BeforeEach
    public void setUp() throws Exception {
        Cafe cafe = new Cafe("Thomas", 12);
        Beverage duvel = new Beverage(10, "Duvel", 3.20);
        Beverage koffie = new Beverage(3, "Koffie", 2.40);
        Beverage spa = new Beverage(7, "Spa", 2.40);
        Beverage westmalle = new Beverage(8, "Westmalle", 3.00);
         o1 = new OrderItem(110, duvel, 2);
         o2 = new OrderItem(120, koffie, 2);
         o3 = new OrderItem(140, spa, 3);
         o4 = new OrderItem(130, westmalle, 3);
    }

    @Test
    public void testGetPaidOrders() {
        Set<Order> paidOrdersDAO;

        paidOrdersDAO = PaidOrderDAOImpl.getInstance().getOrders();

        LocalDate date = LocalDate.of (2019, 12, 21);
        Order orderDAOTest = new Order(16, date, o1, 2, -1 );
        orderDAOTest.getOrderLines().add(o2);
        orderDAOTest.getOrderLines().add(o3);
        orderDAOTest.getOrderLines().add(o4);

        Assertions.assertTrue(paidOrdersDAO.contains(orderDAOTest), "testGetPaidOrders 01 failed");
    }


    @Test
    public void testInsertOrder() {
        Set<Order> paidOrdersDAO;
        paidOrdersDAO = PaidOrderDAOImpl.getInstance().getOrders();
        int originalsize = paidOrdersDAO.size();

        LocalDate date = LocalDate.of (2020, 4, 1);
        Order orderDAOTest = new Order(1000, date, o1, 2, 999 );
         orderDAOTest.getOrderLines().add(o2);


        PaidOrderDAOImpl.getInstance().insertOrder(orderDAOTest);


        Assertions.assertTrue(PaidOrderDAOImpl.getInstance().getOrders().contains(orderDAOTest), "testInsertOrder 01 failed");
 //       assertEquals(originalsize, PaidOrderDAOImpl.getInstance().getOrders().size() , "testInsertOrder 02 failed - size not correct");
    }


    @Test
    public void testGetHighestOrderNumber() {
        Set<Order> paidOrdersDAO;
        paidOrdersDAO = PaidOrderDAOImpl.getInstance().getOrders();
        int highestOrderNumber = PaidOrderDAOImpl.getInstance().highestOrderNumber();
        OptionalInt max = paidOrdersDAO.stream().mapToInt(Order::getOrderNumber).max();
        Assertions.assertEquals(max.getAsInt(), highestOrderNumber , "testGetHighestOrderNumber 01 failed - number not correct");
    }

}

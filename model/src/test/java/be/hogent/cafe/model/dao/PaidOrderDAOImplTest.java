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

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PaidOrderDAOImplTest {
    private OrderItem o1;
    private OrderItem o2;
    private OrderItem o3;
    private OrderItem o4;
    private  Set<Order> paidOrdersDAO;
    private Cafe cafe;

    @BeforeEach
    public void setUp()  {
        cafe = new Cafe();
        Beverage duvel = new Beverage(10, "Duvel", 3.20);
        Beverage koffie = new Beverage(3, "Koffie", 2.40);
        Beverage spa = new Beverage(7, "Spa", 2.40);
        Beverage westmalle = new Beverage(8, "Westmalle", 3.00);
         o1 = new OrderItem(110, duvel, 2);
         o2 = new OrderItem(120, koffie, 2);
         o3 = new OrderItem(140, spa, 3);
         o4 = new OrderItem(130, westmalle, 3);
        paidOrdersDAO = PaidOrderDAOImpl.getInstance().getOrders(cafe.getBeverages());
    }

    @Test
    public void testGetPaidOrders() {
        Assertions.assertTrue(paidOrdersDAO.size() > 0, "testGetPaidOrders 01 failed");
    }

    @Test
    public void testInsertOrder() throws DAOException {
        int originalNumberOfHighestOrderNumber = PaidOrderDAOImpl.getInstance().highestOrderNumber();
        int newSize = paidOrdersDAO.size() + 1;

        LocalDate date = LocalDate.of (2020, 4, 8);
        Order orderTest = new Order(1000, date, o1, 2, 999 );
        orderTest.getOrderLines().add(o2);

        boolean isOrderAlreadyInCollection = paidOrdersDAO.stream().anyMatch((o -> o.getOrderNumber() == 1000));

        if(isOrderAlreadyInCollection)
       {
           newSize = newSize - 1;
        }

        Assertions.assertTrue(PaidOrderDAOImpl.getInstance().insertOrder(orderTest), "testInsertOrder 01 failed");
        int sizeCheck = PaidOrderDAOImpl.getInstance().getOrders(cafe.getBeverages()).size();
       assertEquals(newSize, sizeCheck , "testInsertOrder 02 failed - size not correct");

        PaidOrderDAOImpl.getInstance().deleteOrders(originalNumberOfHighestOrderNumber + 1); //gemaakte orders terug deleten
    }


    @Test
    public void testHighestOrderNumber() {
        OptionalInt maxOrderNumber = paidOrdersDAO.stream().mapToInt(Order::getOrderNumber).max();
        int highestOrderNumber = PaidOrderDAOImpl.getInstance().highestOrderNumber();
        assertEquals(maxOrderNumber.getAsInt(), highestOrderNumber , "testGetHighestOrderNumber 01 failed - number not correct");
         }

}

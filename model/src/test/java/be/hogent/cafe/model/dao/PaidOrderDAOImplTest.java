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
    private Set<Order> paidOrdersDAO;

    @BeforeEach
    public void setUp() {
        Cafe cafe = new Cafe();
        Beverage duvel = new Beverage(10, "Duvel", 3.20);
        Beverage koffie = new Beverage(3, "Koffie", 2.40);
        Beverage latte = new Beverage(14, "Latte", 2.80);
        o1 = new OrderItem(duvel, 2);
        o2 = new OrderItem(koffie, 2);
        o3 = new OrderItem(latte, 4);

        paidOrdersDAO = PaidOrderDAOImpl.getInstance().getOrders();
    }

    @Test
    public void testGetPaidOrders() {
        Assertions.assertTrue(paidOrdersDAO.size() > 0, "testGetPaidOrders 01 failed");
    }

    @Test
    public void testInsertOrder() throws DAOException {
        int originalNumberOfHighestOrderNumber = PaidOrderDAOImpl.getInstance().highestOrderNumber();
        int newSize = paidOrdersDAO.size() + 1;

        LocalDate date = LocalDate.of(2020, 4, 8);
        Order orderTest = new Order(999999999, date, 2, 999);
        orderTest.getOrderLines().add(o1);
        orderTest.getOrderLines().add(o2);
        orderTest.getOrderLines().add(o3);


        boolean isOrderAlreadyInCollection = paidOrdersDAO.stream().anyMatch((o -> o.getOrderNumber() == 999999999));

        if (isOrderAlreadyInCollection) {
            newSize = newSize - 1;
        }

        Assertions.assertEquals(3, PaidOrderDAOImpl.getInstance().insertOrder(orderTest), "testInsertOrder 01 failed");
        int sizeCheck = PaidOrderDAOImpl.getInstance().getOrders().size();
        assertEquals(newSize, sizeCheck, "testInsertOrder 02 failed - size not correct");

        PaidOrderDAOImpl.getInstance().deleteOrders(originalNumberOfHighestOrderNumber + 1); //gemaakte orders terug deleten
    }


    @Test
    public void testHighestOrderNumber() {
        OptionalInt maxOrderNumber = paidOrdersDAO.stream().mapToInt(Order::getOrderNumber).max();
        if (maxOrderNumber.isEmpty()) {
            throw new IllegalStateException("There is no maxOrderNumber");
        }
        int highestOrderNumber = PaidOrderDAOImpl.getInstance().highestOrderNumber();
        assertEquals(maxOrderNumber.getAsInt(), highestOrderNumber, "testGetHighestOrderNumber 01 failed - number not correct");
    }

    @Test
    public void testWaiterSalesDates() throws DAOException {
        Assertions.assertTrue(PaidOrderDAOImpl.getInstance().waiterSalesDates(1).size() > 0, "testWaiterSalesDates 01 failed");
    }


}

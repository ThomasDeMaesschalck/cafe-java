package be.hogent.cafe.model.dao;

import be.hogent.cafe.model.Order;

import java.util.Set;

public interface PaidOrderDAO {
    Set<Order> getOrders();

    boolean insertOrder (Order o);

    int highestOrderNumber(String orderOrIDNumber);

}

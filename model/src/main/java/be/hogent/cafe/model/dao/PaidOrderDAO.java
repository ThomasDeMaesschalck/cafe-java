package be.hogent.cafe.model.dao;

import be.hogent.cafe.model.Order;

import java.time.LocalDate;
import java.util.Set;

public interface PaidOrderDAO {
    Set<Order> getOrders();

    int insertOrder(Order o) throws DAOException;

    void deleteOrders(int orderNumbersToDelete) throws DAOException;

    int highestOrderNumber();

    Set<LocalDate> waiterSalesDates(int waiterID) throws DAOException;
}

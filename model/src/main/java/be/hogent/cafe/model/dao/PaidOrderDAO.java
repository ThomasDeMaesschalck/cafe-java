package be.hogent.cafe.model.dao;

import be.hogent.cafe.model.Beverage;
import be.hogent.cafe.model.Order;

import java.time.LocalDate;
import java.util.Set;

public interface PaidOrderDAO {
    Set<Order> getOrders(Set<Beverage> beverages);

    boolean insertOrder (Order o) throws DAOException;

    boolean deleteOrders (int orderNumbersToDelete) throws DAOException;

    int highestOrderNumber();

    Set<LocalDate> waiterSalesDates(int waiterID) throws DAOException;
}

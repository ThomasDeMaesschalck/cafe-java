package be.hogent.cafe.model.reporting;

import be.hogent.cafe.model.Beverage;
import be.hogent.cafe.model.Order;
import be.hogent.cafe.model.OrderItem;
import be.hogent.cafe.model.Waiter;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class AllWaiterSales {

    public static Map<Beverage, Integer> calculate(LocalDate date, Set<Order> paidOrders, Waiter loggedInWaiter) {        //afsplitsen OrderItems en filteren op datum indien nodig
        Map<Beverage, Integer> salesMap = new HashMap<>();
        List<OrderItem> sales;

        if (date != null) {
            sales = paidOrders.stream().filter(o -> o.getWaiterID() == loggedInWaiter.getID()).filter(o -> o.getDate().equals(date))
                    .flatMap(o -> o.getOrderLines().stream()).collect(Collectors.toList());
        } else {
            sales = paidOrders.stream().filter(o -> o.getWaiterID() == loggedInWaiter.getID())
                    .flatMap(o -> o.getOrderLines().stream()).collect(Collectors.toList());
        }

        //orderitems samentellen
        sales.forEach(item -> {
            Beverage beverage = item.getBeverage();
            salesMap.merge(beverage, item.getQty(), Integer::sum);
        });
        return salesMap;
    }


}

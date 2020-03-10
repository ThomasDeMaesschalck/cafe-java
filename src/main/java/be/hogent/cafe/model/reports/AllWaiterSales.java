package be.hogent.cafe.model.reports;

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

    private final LocalDate date;
    private final Set<Order> paidOrders;
    private final Waiter loggedInWaiter;
    final Map<Beverage, Integer> salesMap = new HashMap<>();

    public AllWaiterSales(LocalDate date, Set<Order> paidOrders, Waiter loggedInWaiter) {
        this.date = date;
        this.paidOrders = paidOrders;
        this.loggedInWaiter = loggedInWaiter;
        calculate();
    }

    private void calculate(){        //afsplitsen OrderItems en filteren op datum indien nodig
        List<OrderItem> sales;

        if (date != null)
        {
            sales =   getPaidOrders().stream().filter(o -> o.getWaiterID() == getLoggedInWaiter().getID()).filter(o -> o.getDate() == date)
                    .flatMap(o -> o.getOrderLines().stream()).collect(Collectors.toList());
        }
        else {
            sales =  getPaidOrders().stream().filter(o -> o.getWaiterID() == getLoggedInWaiter().getID())
                    .flatMap(o -> o.getOrderLines().stream()).collect(Collectors.toList());
        }

        //orderitems samentellen
        sales.forEach(item -> { Beverage beverage = item.getBeverage();
            if (!salesMap.containsKey(beverage)) {
                salesMap.put(beverage, item.getQty());
            } else {
                salesMap.put(beverage, salesMap.get(beverage) + item.getQty());
            }
        });
    }

    private Set<Order> getPaidOrders() {
        return paidOrders;
    }

    private Waiter getLoggedInWaiter() {
        return loggedInWaiter;
    }

    public Map<Beverage, Integer> getSalesMap() {
        return salesMap;
    }
}

package be.hogent.cafe.model.reports;

import be.hogent.cafe.model.Order;
import be.hogent.cafe.model.Waiter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class WaitersByRevenue {

    private final Set<Waiter> waiters;
    private final int numberOfHowMany;
    private final HashMap<Waiter, Double> topWaitersMap = new HashMap<>();
    private final Set<Order> paidOrders;

    public WaitersByRevenue(Set<Waiter> waiters, int numberOfHowMany, Set<Order> paidOrders) {
        this.waiters = waiters;
        this.numberOfHowMany = numberOfHowMany;
        this.paidOrders = paidOrders;
        calculate();
    }

    private void calculate(){

    Map<Waiter, Double> topWaitersMapTemp = new HashMap<>();

        getWaiters().forEach((waiter) -> {
            Double revenue = getTotalWaiterRevenue(waiter.getID());
            topWaitersMapTemp.put(waiter, revenue);
        });

    Waiter waiter;
    Double waiterValue;

    for (int i = 0; i < getNumberOfHowMany(); i++) { //waiter zoeken met hoogste omzet zoeken in temp map, toevoegen aan map, verwijderen uit temp, repeat
        waiter = Collections.max(topWaitersMapTemp.entrySet(), Map.Entry.comparingByValue()).getKey();
        waiterValue = Collections.max(topWaitersMapTemp.entrySet(), Map.Entry.comparingByValue()).getValue();
        getTopWaitersMap().put(waiter, waiterValue);
        topWaitersMapTemp.remove(waiter);
    }
}

    private double getTotalWaiterRevenue(int waiterID){
        return getPaidOrders().stream().filter(order -> order.getWaiterID() == waiterID).mapToDouble(Order::getTotalOrderPrice).sum();
    }

    private Set<Waiter> getWaiters() {
        return waiters;
    }

    private int getNumberOfHowMany() {
        return numberOfHowMany;
    }

    public HashMap<Waiter, Double> getTopWaitersMap() {
        return topWaitersMap;
    }

    private Set<Order> getPaidOrders() {
        return paidOrders;
    }
}

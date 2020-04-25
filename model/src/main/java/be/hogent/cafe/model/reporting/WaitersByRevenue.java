package be.hogent.cafe.model.reporting;

import be.hogent.cafe.model.Order;
import be.hogent.cafe.model.Waiter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class WaitersByRevenue {

    public static HashMap<Waiter, Double> calculate(Set<Waiter> waiters, int numberOfHowMany, Set<Order> paidorders) {

        HashMap<Waiter, Double> topWaitersMap = new HashMap<>();

        Map<Waiter, Double> topWaitersMapTemp = new HashMap<>();

        waiters.forEach((waiter) -> {
            Double revenue = getTotalWaiterRevenue(waiter.getID(), paidorders);
            topWaitersMapTemp.put(waiter, revenue);
        });

        Waiter waiter;
        Double waiterValue;

        for (int i = 0; i < numberOfHowMany; i++) { //waiter zoeken met hoogste omzet zoeken in temp map, toevoegen aan map, verwijderen uit temp, repeat
            waiter = Collections.max(topWaitersMapTemp.entrySet(), Map.Entry.comparingByValue()).getKey();
            waiterValue = Collections.max(topWaitersMapTemp.entrySet(), Map.Entry.comparingByValue()).getValue();
            topWaitersMap.put(waiter, waiterValue);
            topWaitersMapTemp.remove(waiter);
        }
        return topWaitersMap;
    }

    private static double getTotalWaiterRevenue(int waiterID, Set<Order> paidOrders) {
        return paidOrders.stream().filter(order -> order.getWaiterID() == waiterID).mapToDouble(Order::getTotalOrderPrice).sum();
    }

}

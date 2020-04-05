package be.hogent.cafe.model;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public class Order {
    private  int orderNumber;
    private  LocalDate date;
    private  int waiterID;
    private Set<OrderItem> orderItems  =  new HashSet<>();
    private  int tableID;
    private static final Logger logger = LogManager.getLogger (Cafe.class.getName ());

    public Order() {
    }

    public Order(int orderNumber, LocalDate date, OrderItem orderItem, int waterID, int tableID) {
        this.orderNumber = orderNumber;
        getOrderLines().add(orderItem);
        this.date = date;
        this.waiterID = waterID;
        this.tableID = tableID;
    }

    public void AddOrUpdateOrderLine(OrderItem orderItem)
    {
        if(getOrderLines().contains(orderItem)) {  //qty van bestaande orderlijn updaten
            Optional<OrderItem> originalOrderItem = getOrderLines().stream().filter((orderItem::equals)).findFirst();
            int newQuantity = originalOrderItem.orElseThrow().getQty() + orderItem.getQty();
            int ID = originalOrderItem.orElseThrow().getID();
            getOrderLines().forEach(o ->  {
                if(o.equals(orderItem))
                {
                    o.setQty(newQuantity);
                }});
            logger.info("orderNumber: " + getOrderNumber() +  " - updated orderline with ID: " + ID  + " to new quantity: " + newQuantity);
          }
        else{ //orderlijn toevoegen aan bestaand order
            getOrderLines().add(orderItem);
            logger.info("orderNumber: " + getOrderNumber() + " -  added orderline " + orderItem.toString());
        }
    }

    public boolean orderLineExists(OrderItem orderItem)
    {
        return getOrderLines().contains(orderItem);
    }

    public int getOrderNumber() {
        return orderNumber;
    }

    public double getTotalOrderPrice(){ //totaal bedrag van een volledig order
        return getOrderLines().stream().mapToDouble(OrderItem::getOrderLinePrice).sum();
    }

    public LocalDate getDate() {
        return date;
    }

    public int getWaiterID() {
        return waiterID;
    }

    public Set<OrderItem> getOrderLines() {
        return orderItems;
    }

    public int getTableID() {
        return tableID;
    }

    public void setOrderNumber(int orderNumber) {
        this.orderNumber = orderNumber;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setWaiterID(int waiterID) {
        this.waiterID = waiterID;
    }

    public void setOrderItems(OrderItem orderItem) {
        this.orderItems.add(orderItem);
    }

    @Override
    public String toString() {
        return "Order: " + getOrderNumber() +
                ", date: " + getDate() +
                ", waiterID: " + getWaiterID() +
                ", orderItems: " + orderItems.toString() +
                ", tableID: " + getTableID();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return orderNumber == order.orderNumber;
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderNumber, waiterID);
    }
}
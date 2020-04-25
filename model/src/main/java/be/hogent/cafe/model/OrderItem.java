package be.hogent.cafe.model;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;

public class OrderItem {

    private final Beverage beverage;
    private int qty;
    private static final Logger logger = LogManager.getLogger(Cafe.class.getName());


    public OrderItem(Beverage beverage, int qty) {
        this.beverage = beverage;
        this.qty = qty;
    }

    public double getOrderLinePrice() {
        return getBeverage().getPrice() * getQty();
    }

    public Beverage getBeverage() {
        return beverage;
    }

    public int getQty() {
        return qty;
    }


    public void increaseQty() {
        qty++;
        logger.info("Quantity increased of " + this.getBeverage().getBeverageName() + " to " + this.getQty());

    }

    public void increaseQty(int increase) {
        qty = qty + increase;
        logger.info("Quantity increased of " + this.getBeverage().getBeverageName() + " to " + this.getQty());

    }


    public void decreaseQty() {
        if (qty > 0) {
            qty--;
            logger.info("Quantity decreased of " + this.getBeverage().getBeverageName());
        }
    }

    @Override
    public String toString() {
        return "beverage: " + getBeverage().getBeverageName() + ", quantity: " + getQty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderItem orderItem = (OrderItem) o;
        return beverage.equals(orderItem.beverage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(beverage);
    }


}

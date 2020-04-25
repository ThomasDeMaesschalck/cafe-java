package be.hogent.cafe.model;

import java.io.Serializable;
import java.util.Objects;

public class Beverage implements Serializable {

    private static final long serialVersionUID = 2834937874439465695L;

    private int beverageID;
    private String beverageName;
    private double price;

    public Beverage(int beverageID, String beverageName, double price) {
        this.beverageID = beverageID;
        this.beverageName = beverageName;
        this.price = price;
    }

    public Beverage() {
    }

    public int getBeverageID() {
        return beverageID;
    }

    public String getBeverageName() {
        return beverageName;
    }

    public double getPrice() {
        return price;
    }

    public void setBeverageID(int beverageID) {
        this.beverageID = beverageID;
    }

    public void setBeverageName(String beverageName) {
        this.beverageName = beverageName;
    }

    public void setPrice(double price) {
        this.price = price;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Beverage beverage = (Beverage) o;
        return beverageID == beverage.beverageID && Double.compare(beverage.price, price) == 0 && Objects.equals(beverageName, beverage.beverageName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(beverageID, beverageName, price);
    }


    @Override
    public String toString() {
        return getBeverageID() + ". " + getBeverageName() + " " + getPrice();
    }

}

package be.hogent.cafe.model;

import java.util.Objects;

public class OrderItem {

    private  Beverage beverage;
    private int qty;
    private  int ID;

    public OrderItem(int ID, Beverage beverage, int qty) {
        this.ID = ID;
        this.beverage = beverage;
        this.qty = qty;
    }

    public OrderItem(Beverage beverage, int qty) {
        this.beverage = beverage;
        this.qty = qty;
    }

     public double getOrderLinePrice(){
        return getBeverage().getPrice() * getQty();
    }

    public Beverage getBeverage() {
        return beverage;
    }

    public int getQty() {
        return qty;
    }

    public int getID() {
        return ID;
    }

    public void increaseQty(){
        qty++;
    }

    public void increaseQty(int increase){
        qty = qty + increase;
    }


    public void decreaseQty(){
        if (qty > 0)
        { qty--; }
    }

    @Override
    public String toString() {
        return "ID: " + getID() + ", beverage: " + getBeverage().getBeverageName() + ", quantity: " + getQty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderItem orderItem = (OrderItem) o;
        return beverage.equals(orderItem.beverage);
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    @Override
    public int hashCode() {
        return Objects.hash(beverage);
    }

    public void setBeverage(Beverage beverage) {
        this.beverage = beverage;
    }

}

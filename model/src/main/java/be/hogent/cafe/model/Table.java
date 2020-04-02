package be.hogent.cafe.model;

import java.util.Objects;

public class Table {
    private int tableID;
    private Waiter belongsToWaiter;
    private boolean activeOrder;

    public Table(int tableID){
        this.tableID = tableID;
        this.belongsToWaiter = null;
        this.activeOrder = false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Table table = (Table) o;
        return tableID == table.tableID;
    }

    @Override
    public int hashCode() {
        return Objects.hash(tableID);
    }

    public Waiter getBelongsToWaiter() {
        return belongsToWaiter;
    }

    public void setBelongsToWaiter(Waiter waiter) {
        this.belongsToWaiter = waiter;
    }

     @Override
    public String toString() {
        return "table " + tableID;
    }

    public int getTableID() {
        return tableID;
    }

    public boolean isActiveOrder() {
        return activeOrder;
    }

    public void setActiveOrder(boolean activeOrder) {
        this.activeOrder = activeOrder;
    }
}


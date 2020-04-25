package be.hogent.cafe.model;

import java.io.Serializable;
import java.util.Objects;

public class Table implements Serializable {
    private final int tableID;
    private Waiter belongsToWaiter;
    private static final long serialVersionUID = -5883989462029327886L;

    public Table(int tableID) {
        this.tableID = tableID;
        this.belongsToWaiter = null;
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
}


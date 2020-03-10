package be.hogent.cafe.model;

import java.util.Objects;

public class Waiter {

    private final String lastName;
    private final String firstName;
    private final String password;
    private final int ID;

    public Waiter(int ID, String lastName, String firstName, String password) {
        this.lastName = lastName;
        this.firstName = firstName;
        this.password = password;
        this.ID = ID;
    }

    public int getID() {
        return ID;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Waiter waiter = (Waiter) o;
        return  lastName.equals(waiter.lastName) &&  firstName.equals(waiter.firstName) &&  password.equals(waiter.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lastName, firstName, password);
    }

    @Override
    public String toString() {
        return firstName + " " + lastName;
    }
}
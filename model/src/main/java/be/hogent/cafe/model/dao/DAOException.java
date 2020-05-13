package be.hogent.cafe.model.dao;

public class DAOException extends Exception {
    private static final long serialVersionUID = 19192L;
    private String message;

    public DAOException() {
        super("Database error");
    }

    public DAOException(String message) {
        this.message = message;
    }

    public String toString() {
        return message;
    }
}
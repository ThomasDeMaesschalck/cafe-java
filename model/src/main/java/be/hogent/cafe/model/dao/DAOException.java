/*
 * Copyright (c) 2017. All code is used during Programmeren 3 module at CVO hogent
 */

package be.hogent.cafe.model.dao;

public class DAOException extends Exception {
    private static final long serialVersionUID = 19192L;
    private String message;

    public DAOException () {
    }

    public DAOException (String message) {
        this.message = message;
    }

    public String getMessage () {
        return message;
    }

    public void setMessage (String message) {
        this.message = message;
    }

    public String toString () {
        return message;
    }
}
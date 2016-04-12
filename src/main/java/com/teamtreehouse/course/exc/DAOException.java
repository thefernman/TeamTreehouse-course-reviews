package com.teamtreehouse.course.exc;

/**
 * Created by Fernando on 4/11/2016.
 */
public class DAOException extends Exception {

    private final Exception originalException;

    public DAOException(Exception originalException, String msg) {
        super(msg);
        this.originalException = originalException;
    }
}

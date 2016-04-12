package com.teamtreehouse.course.exc;

/**
 * Created by Fernando on 4/12/2016.
 */
public class APIError extends RuntimeException {
    private final int status;

    public APIError(int status, String msg) {
        super(msg);
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
}

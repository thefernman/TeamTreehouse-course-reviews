package com.teamtreehouse.testing;

/**
 * Created by Fernando on 4/12/2016.
 */
public class APIResponse {
    private int status;
    private String body;

    public APIResponse(int status, String body) {
        this.status = status;
        this.body = body;
    }

    public int getStatus() {
        return status;
    }

    public String getBody() {
        return body;
    }
}

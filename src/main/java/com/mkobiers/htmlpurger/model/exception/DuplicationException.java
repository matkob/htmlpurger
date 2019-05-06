package com.mkobiers.htmlpurger.model.exception;

public class DuplicationException extends Exception {
    private String message;

    public DuplicationException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return "rule duplication " + message;
    }

}

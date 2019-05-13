package com.mkobiers.htmlpurger.model.exception;

public class DuplicationException extends Exception {
    private String message;
    private int row;
    private int column;

    public DuplicationException(String message, int row, int column) {
        this.message = message;
        this.row = row;
        this.column = column;
    }

    @Override
    public String getMessage() {
        return "rule duplication at " + row + "," + column + " - " + message;
    }

}

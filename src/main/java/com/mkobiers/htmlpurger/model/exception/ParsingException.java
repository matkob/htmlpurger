package com.mkobiers.htmlpurger.model.exception;

public class ParsingException extends Exception {
    private String cause;
    private int col;
    private int row;

    public ParsingException(int row, int col, String cause) {
        this.col = col;
        this.row = row;
        this.cause = cause;
    }

    @Override
    public String getMessage() {
        return "at position " + row + "," + col + " - " + cause;
    }

    public int getCol() {
        return col;
    }

    public int getRow() {
        return row;
    }
}

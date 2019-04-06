package com.mkobiers.htmlpurger.model;

public class GrammarException extends Exception {
    private String message;
    private int col;
    private int row;

    public GrammarException(String message, int col, int row) {
        this.message = message;
        this.col = col;
        this.row = row;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public int getCol() {
        return col;
    }

    public int getRow() {
        return row;
    }
}

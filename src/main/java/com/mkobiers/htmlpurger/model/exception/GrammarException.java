package com.mkobiers.htmlpurger.model.exception;

public class GrammarException extends Exception {
    private String message;
    private int col;
    private int row;

    public GrammarException(int row, int col, String message) {
        this.message = message;
        this.col = col;
        this.row = row;
    }

    @Override
    public String getMessage() {
        return "at position " + row + "," + col + " \"" + message + "\"";
    }

    public int getCol() {
        return col;
    }

    public int getRow() {
        return row;
    }
}

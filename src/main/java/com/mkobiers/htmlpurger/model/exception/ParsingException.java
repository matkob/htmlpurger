package com.mkobiers.htmlpurger.model.exception;

public class ParsingException extends Exception {
    private String message;
    private String information;
    private int col;
    private int row;

    public ParsingException(int row, int col, String message, String information) {
        this.message = message;
        this.col = col;
        this.row = row;
        this.information = information;
    }

    @Override
    public String getMessage() {
        return "at position " + row + "," + col + " \"" + message + "\" - " + information;
    }

    public int getCol() {
        return col;
    }

    public int getRow() {
        return row;
    }
}

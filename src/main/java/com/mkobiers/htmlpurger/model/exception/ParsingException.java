package com.mkobiers.htmlpurger.model.exception;

public class ParsingException extends Exception {
    private String cause;
    private String file;
    private int col;
    private int row;

    public ParsingException(int row, int col, String file, String cause) {
        this.col = col;
        this.row = row;
        this.file = file;
        this.cause = cause;
    }

    @Override
    public String getMessage() {
        return file + " parsing exception at position " + row + "," + col + " - " + cause;
    }
}

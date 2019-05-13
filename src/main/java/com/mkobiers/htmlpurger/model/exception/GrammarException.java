package com.mkobiers.htmlpurger.model.exception;

public class GrammarException extends Exception {
    private String message;
    private String file;
    private int col;
    private int row;

    public GrammarException(int row, int col, String file, String message) {
        this.message = message;
        this.file = file;
        this.col = col;
        this.row = row;
    }

    @Override
    public String getMessage() {
        return file + " grammar exception at position " + row + "," + col + " \"" + message + "\"";
    }
}

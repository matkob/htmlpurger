package com.mkobiers.htmlpurger.model.exception;

public class DuplicationException extends Exception {
    private String message;
    private String file;
    private int row;
    private int column;

    public DuplicationException(int row, int column, String file, String message) {
        this.message = message;
        this.file = file;
        this.row = row;
        this.column = column;
    }

    @Override
    public String getMessage() {
        return file + " rule duplication at " + row + "," + column + " - " + message;
    }

}

package com.mkobiers.htmlpurger.io;

public interface IReader {

    char nextChar();
    char getChar();
    String getErrorMessage();
    int getColumn();
    int getRow();
}

package com.mkobiers.htmlpurger.io;

public interface IReader {

    char nextChar();
    char getChar();
    void rewind();
    String getErrorMessage();
    int getColumn();
    int getRow();
}

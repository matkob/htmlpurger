package com.mkobiers.htmlpurger.model;

public enum TokenType {
    TAGNAME,
    RULE,
    CONTENT,
    TEXT,
    NUM,
    TAGOPEN_NAME,
    TAGCLOSE_NAME,
    ATTR_NAME,
    DOUBLEQUOTED,
    SINGLEQUOTED,
    VALUE,
    TAGOPEN_LEFT,
    TAGOPEN_RIGHT,
    TAGCLOSE_LEFT,
    TAGCLOSE_RIGHT,
    EQUALS,
    LEFT_BRACE,
    RIGHT_BRACE,
    COMMA,

    END_OF_TEXT

}

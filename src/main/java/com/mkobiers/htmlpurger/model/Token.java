package com.mkobiers.htmlpurger.model;

import java.util.Objects;

import static com.mkobiers.htmlpurger.model.TokenType.RULE;

public class Token {
    public final static Token REMOVE = new Token("remove", RULE);
    public final static Token ALL = new Token("all", RULE);

    private String text;
    private TokenType type;
    private int row;
    private int column;

    public Token(String text, TokenType type) {
        this.text = text;
        this.type = type;
    }

    public Token(String text, TokenType type, int row, int column) {
        this.text = text;
        this.type = type;
        this.row = row;
        this.column = column;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public TokenType getType() {
        return type;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Token) {
            return ((Token) obj).getType().equals(type) && ((Token) obj).getText().equalsIgnoreCase(text);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(text.toLowerCase(), type);
    }

    @Override
    public String toString() {
        return "Token{" +
                "text='" + text + '\'' +
                ", type=" + type +
                '}';
    }
}

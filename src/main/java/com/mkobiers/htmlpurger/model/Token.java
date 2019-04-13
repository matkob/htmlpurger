package com.mkobiers.htmlpurger.model;

import java.util.Objects;

public class Token {

    private String text;
    private TokenType type;

    public Token(String text, TokenType type) {
        this.text = text;
        this.type = type;
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

    public void setType(TokenType type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Token) {
            return ((Token) obj).getType().equals(type) && ((Token) obj).getText().equals(text);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(text, type);
    }

    @Override
    public String toString() {
        return "Token{" +
                "text='" + text + '\'' +
                ", type=" + type +
                '}';
    }
}

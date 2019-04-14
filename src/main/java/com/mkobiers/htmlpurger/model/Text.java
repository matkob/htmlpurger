package com.mkobiers.htmlpurger.model;

public class Text extends Content {
    private Token text;

    public Text(Token text) {
        this.text = text;
    }

    public Token getText() {
        return text;
    }

    @Override
    public String toString() {
        return text.getText() + "\n";
    }
}

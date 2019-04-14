package com.mkobiers.htmlpurger.model;

public class Closetag {
    private Token leftBrace;
    private Token name;
    private Token rightBrace;

    public Closetag() {

    }

    public Token getLeftBrace() {
        return leftBrace;
    }

    public void setLeftBrace(Token leftBrace) {
        this.leftBrace = leftBrace;
    }

    public Token getName() {
        return name;
    }

    public void setName(Token name) {
        this.name = name;
    }

    public Token getRightBrace() {
        return rightBrace;
    }

    public void setRightBrace(Token rightBrace) {
        this.rightBrace = rightBrace;
    }

    @Override
    public String toString() {
        return leftBrace.getText() + name.getText() + rightBrace.getText();
    }
}

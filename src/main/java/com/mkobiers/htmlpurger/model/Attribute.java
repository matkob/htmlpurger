package com.mkobiers.htmlpurger.model;

public class Attribute {
    private Token name;
    private boolean hasValue;
    private Token equals;
    private Token value;

    public Attribute() {

    }

    public Token getName() {
        return name;
    }

    public void setName(Token name) {
        this.name = name;
    }

    public boolean hasValue() {
        return hasValue;
    }

    public void setHasValue(boolean hasValue) {
        this.hasValue = hasValue;
    }

    public Token getEquals() {
        return equals;
    }

    public void setEquals(Token equals) {
        this.equals = equals;
    }

    public Token getValue() {
        return value;
    }

    public void setValue(Token value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return name.getText() + (hasValue ? equals.getText() + value.getText() : "");
    }
}

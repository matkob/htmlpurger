package com.mkobiers.htmlpurger.model;

import java.util.ArrayList;
import java.util.List;

public class Opentag {
    private Token leftBrace;
    private Token name;
    private List<Attribute> attributes;
    private Token rightBrace;

    public Opentag() {
        this.attributes = new ArrayList<>();
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

    public List<Attribute> getAttributes() {
        return attributes;
    }

    public boolean addAttribute(Attribute attribute) {
        return this.attributes.add(attribute);
    }

    public Token getRightBrace() {
        return rightBrace;
    }

    public void setRightBrace(Token rightBrace) {
        this.rightBrace = rightBrace;
    }

    @Override
    public String toString() {
        StringBuilder attributesBuilder = new StringBuilder();
        attributes.forEach(a -> attributesBuilder.append(" " + a.toString() + " "));
        return leftBrace.getText() + name.getText() +
                attributesBuilder.toString() + rightBrace.getText();
    }
}

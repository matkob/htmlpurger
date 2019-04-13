package com.mkobiers.htmlpurger.model;

import java.util.ArrayList;
import java.util.List;

public class Tag {
    private Token name;
    private List<Token> attributes;
    private List<Tag> content;

    public Tag(Token name) {
        this.name = name;
        this.attributes = new ArrayList<>();
        this.content = new ArrayList<>();
    }

    public Token getName() {
        return name;
    }

    public List<Token> getAttributes() {
        return attributes;
    }

    public boolean addAttribute(Token token) {
        return attributes.add(token);
    }

    public List<Tag> getContent() {
        return content;
    }

    public boolean addContent(Tag tag) {
        return content.add(tag);
    }
}

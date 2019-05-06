package com.mkobiers.htmlpurger.model;

import java.util.ArrayList;
import java.util.List;

public class ConfigEntry {

    private Token tagname;
    private List<Token> rules;

    public ConfigEntry(Token tagname) {
        this.tagname = tagname;
        this.rules = new ArrayList<>();
    }

    public ConfigEntry(Token tagname, List<Token> rules) {
        this.tagname = tagname;
        this.rules = rules;
    }

    public Token getTagname() {
        return tagname;
    }

    public List<Token> getRules() {
        return rules;
    }

    public boolean addRule(Token token) {
        return rules.add(token);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        rules.forEach(rule -> builder.append(rule.getText() + ", "));
        return tagname.getText() + " { " + builder.toString() + " }";
    }
}

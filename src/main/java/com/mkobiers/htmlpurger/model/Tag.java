package com.mkobiers.htmlpurger.model;

import java.util.ArrayList;
import java.util.List;

import static com.mkobiers.htmlpurger.model.TokenType.TAGNAME;

public class Tag extends TagStandalone {
    private Opentag opentag;
    private List<Content> content;
    private Closetag closetag;

    public Tag() {
        this.content = new ArrayList<>();
    }

    public Opentag getOpentag() {
        return opentag;
    }

    public Token getTagname() {
        return new Token(getOpentag().getName().getText(), TAGNAME);
    }

    public void setOpentag(Opentag opentag) {
        this.opentag = opentag;
    }

    public List<Content> getContent() {
        return content;
    }

    public void addContent(Content content) {
        this.content.add(0, content);
    }

    public Closetag getClosetag() {
        return closetag;
    }

    public void setClosetag(Closetag closetag) {
        this.closetag = closetag;
    }

    public void clear() {
        this.opentag = null;
        this.content = new ArrayList<>();
        this.closetag = null;
    }

    @Override
    public String toString() {
        StringBuilder contentBuilder = new StringBuilder();
        content.forEach(c -> contentBuilder.append(c.toString()));
        return (opentag != null ? opentag.toString() + "\n" : "") +
                contentBuilder.toString() +
                (closetag != null ? closetag.toString() + "\n" : "");
    }
}

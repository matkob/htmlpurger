package com.mkobiers.htmlpurger.model;

import java.util.ArrayList;
import java.util.List;

public class Tag extends Content {
    private Opentag opentag;
    private List<Content> content;
    private Closetag closetag;

    public Tag() {
        this.content = new ArrayList<>();
    }

    public Opentag getOpentag() {
        return opentag;
    }

    public void setOpentag(Opentag opentag) {
        this.opentag = opentag;
    }

    public List<Content> getContent() {
        return content;
    }

    public boolean addContent(Content content) {
        return this.content.add(content);
    }

    public Closetag getClosetag() {
        return closetag;
    }

    public void setClosetag(Closetag closetag) {
        this.closetag = closetag;
    }

    @Override
    public String toString() {
        StringBuilder contentBuilder = new StringBuilder();
        content.forEach(c -> contentBuilder.append(c.toString()));
        return opentag.toString() + "\n" +
                contentBuilder.toString() +
                closetag.toString() + "\n";
    }
}

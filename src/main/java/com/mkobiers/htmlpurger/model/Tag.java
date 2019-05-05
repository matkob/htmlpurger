package com.mkobiers.htmlpurger.model;

import java.util.ArrayList;
import java.util.List;

import static com.mkobiers.htmlpurger.model.TokenType.TAGNAME;

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

    public Token getTagname() {
        return new Token(getOpentag().getName().getText(), TAGNAME);
    }

    public void setOpentag(Opentag opentag) {
        this.opentag = opentag;
    }

    public List<Content> getContent() {
        return content;
    }

    public void setContent(List<Content> content) {
        this.content = content;
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

    @Override
    public String toString() {
        StringBuilder contentBuilder = new StringBuilder();
        content.forEach(c -> contentBuilder.append(c.toString()));
        return (opentag != null ? opentag.toString() + "\n" : "") +
                contentBuilder.toString() +
                (closetag != null ? closetag.toString() + "\n" : "");
    }

    private class TagIterator {
        private int idx = 0;
        private Tag current;

        public Tag next() {
            if (current == null) {
                while (content.get(idx) != null && !(content.get(idx) instanceof Tag)) {
                    idx += 1;
                }
                current = (Tag) content.get(idx);
            } else {

            }

            return null;
        }
    }
}

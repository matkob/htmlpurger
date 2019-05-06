package com.mkobiers.htmlpurger.model;

import static com.mkobiers.htmlpurger.model.TokenType.TAGNAME;

public class TagStandalone extends Content {
    private Opentag opentag;

    public Opentag getOpentag() {
        return opentag;
    }

    public Token getTagname() {
        return new Token(getOpentag().getName().getText(), TAGNAME);
    }

    public void setOpentag(Opentag opentag) {
        this.opentag = opentag;
    }

    public void clear() {
        this.opentag = null;
    }

    @Override
    public String toString() {
        return opentag != null ? opentag.toString() + "\n" : "";
    }
}

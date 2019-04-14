package com.mkobiers.htmlpurger.model;

public class TagStandalone extends Content {
    private Opentag opentag;

    public TagStandalone(Opentag opentag) {
        this.opentag = opentag;
    }

    public Opentag getOpentag() {
        return opentag;
    }

    @Override
    public String toString() {
        return opentag.toString() + "\n";
    }
}

package com.mkobiers.htmlpurger.parser;

import com.mkobiers.htmlpurger.io.IReader;
import com.mkobiers.htmlpurger.lexer.HtmlLexer;
import com.mkobiers.htmlpurger.model.*;
import com.mkobiers.htmlpurger.model.exception.ParsingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import static com.mkobiers.htmlpurger.model.TokenType.*;

public class HtmlParser {

    private Logger logger = LoggerFactory.getLogger(HtmlParser.class);
    private HtmlLexer lexer;

    public HtmlParser(IReader reader) {
        this.lexer = new HtmlLexer(reader);
    }

    public Tag parseHtml() throws Exception {
        logger.info("parsing html file");
        Token t;
        List<Token> tokens = new ArrayList<>();
        while (!(t = lexer.nextToken()).getType().equals(END_OF_TEXT)) {
            tokens.add(t);
        }

        ListIterator<Token> begin = tokens.listIterator();
        return buildTag(begin);
    }

    private Tag buildTag(ListIterator<Token> begin) throws Exception {
        Tag tag = new Tag();
        Opentag opentag = buildOpentag(begin);
        tag.setOpentag(opentag);

        while (begin.hasNext()) {
            Token t = begin.next();
            if (t.getType().equals(TAGOPEN_LEFT)) {
                boolean standalone = isStandalone(begin.next());
                begin.previous();
                begin.previous();
                tag.addContent(standalone ? new TagStandalone(buildOpentag(begin)) : buildTag(begin));
            } else if (t.getType().equals(TEXT)) {
                tag.addContent(new Text(t));
            } else if (t.getType().equals(TAGCLOSE_LEFT)) {
                begin.previous();
                break;
            }
        }

        tag.setClosetag(buildClosetag(begin));
        return tag;
    }

    private Opentag buildOpentag(ListIterator<Token> it) throws ParsingException {
        Opentag opentag = new Opentag();
        Token leftBrace;
        if (!it.hasNext() || !(leftBrace = it.next()).getType().equals(TAGOPEN_LEFT)) {
            throw new ParsingException();
        }
        opentag.setLeftBrace(leftBrace);
        Token name;
        if (!it.hasNext() || !(name = it.next()).getType().equals(TAGOPEN_NAME)) {
            throw new ParsingException();
        }
        opentag.setName(name);
        Attribute attr = null;
        Token t = null;
        while (it.hasNext() && !(t = it.next()).getType().equals(TAGOPEN_RIGHT)) {
            switch (t.getType()) {
                case ATTR_NAME:
                    if (attr != null) {
                        opentag.addAttribute(attr);
                    }
                    attr = new Attribute();
                    attr.setName(t);
                    break;
                case EQUALS:
                    attr.setEquals(t);
                    attr.setHasValue(true);
                    break;
                case SINGLEQUOTED:
                case DOUBLEQUOTED:
                case NUM:
                    attr.setValue(t);
                    opentag.addAttribute(attr);
                    attr = null;
                    break;
            }
        }
        if (t == null || !t.getType().equals(TAGOPEN_RIGHT)) {
            throw new ParsingException();
        }
        opentag.setRightBrace(t);

        return opentag;
    }

    private Closetag buildClosetag(ListIterator<Token> it) throws ParsingException {
        Closetag closetag = new Closetag();
        Token leftBrace;
        if (!it.hasNext() || !(leftBrace = it.next()).getType().equals(TAGCLOSE_LEFT)) {
            throw new ParsingException();
        }
        closetag.setLeftBrace(leftBrace);
        Token name;
        if (!it.hasNext() || !(name = it.next()).getType().equals(TAGCLOSE_NAME)) {
            throw new ParsingException();
        }
        closetag.setName(name);
        Token rightBrace;
        if (!it.hasNext() || !(rightBrace = it.next()).getType().equals(TAGCLOSE_RIGHT)) {
            throw new ParsingException();
        }
        closetag.setRightBrace(rightBrace);

        return closetag;
    }

    private boolean isStandalone(Token t) {
        return t.getText().equalsIgnoreCase("area")
                || t.getText().equalsIgnoreCase("base")
                || t.getText().equalsIgnoreCase("br")
                || t.getText().equalsIgnoreCase("col")
                || t.getText().equalsIgnoreCase("command")
                || t.getText().equalsIgnoreCase("embed")
                || t.getText().equalsIgnoreCase("hr")
                || t.getText().equalsIgnoreCase("img")
                || t.getText().equalsIgnoreCase("input")
                || t.getText().equalsIgnoreCase("keygen")
                || t.getText().equalsIgnoreCase("link")
                || t.getText().equalsIgnoreCase("meta")
                || t.getText().equalsIgnoreCase("param")
                || t.getText().equalsIgnoreCase("source")
                || t.getText().equalsIgnoreCase("track")
                || t.getText().equalsIgnoreCase("wbr");
    }
}

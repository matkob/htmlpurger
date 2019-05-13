package com.mkobiers.htmlpurger.parser;

import com.mkobiers.htmlpurger.io.IReader;
import com.mkobiers.htmlpurger.lexer.HtmlLexer;
import com.mkobiers.htmlpurger.model.*;
import com.mkobiers.htmlpurger.model.exception.GrammarException;
import com.mkobiers.htmlpurger.model.exception.ParsingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import static com.mkobiers.htmlpurger.model.TokenType.*;

public class HtmlParser {
    private final String FILE = "HTML";
    private Logger logger = LoggerFactory.getLogger(HtmlParser.class);
    private final String NO_RIGHT_BRACE_INFO = "no right brace found";
    private final String NO_TAG_NAME_INFO = "no tag name found";
    private final String NO_LEFT_BRACE_INFO = "no left brace found";
    private final String UNKNOWN_TAG = "unknown tag";

    private HtmlLexer lexer;
    private IReader reader;

    public HtmlParser(IReader reader) {
        this.lexer = new HtmlLexer(reader);
        this.reader = reader;
    }

    public Tag parseHtml() throws ParsingException, GrammarException {
        logger.info("parsing html file");
        List<Token> tokens = new ArrayList<>();
        Token t;
        while (!(t = lexer.nextToken()).getType().equals(END_OF_TEXT)) {
            tokens.add(t);
        }

        ListIterator<Token> end = tokens.listIterator(tokens.size());
        return buildTag(end);
    }

    private Tag buildTag(ListIterator<Token> end) throws ParsingException {
        Tag tag = new Tag();
        tag.setClosetag(buildClosetag(end));

        while (end.hasPrevious()) {
            Token t = end.previous();
            if (t.getType().equals(TAGCLOSE_RIGHT)) {
                end.next();
                tag.addContent(buildTag(end));
            } else if (t.getType().equals(TEXT)) {
                tag.addContent(new Text(t));
            } else if (t.getType().equals(TAGOPEN_RIGHT)) {
                end.next();
                Opentag opentag = buildOpentag(end);
                if (opentag.getName().getText().equals(tag.getClosetag().getName().getText())) {
                    tag.setOpentag(opentag);
                    break;
                } else {
                    TagStandalone standalone = new TagStandalone();
                    standalone.setOpentag(opentag);
                    tag.addContent(standalone);
                }
            }
        }
        if (tag.getOpentag() == null) {
            throw new ParsingException(tag.getClosetag().getLeftBrace().getRow(),
                    tag.getClosetag().getLeftBrace().getColumn(), FILE, UNKNOWN_TAG + " \"" + tag.getClosetag().getName().getText() + "\"");
        }
        return tag;
    }

    private Opentag buildOpentag(ListIterator<Token> it) throws ParsingException {
        Opentag opentag = new Opentag();
        Token rightBrace = null;
        if (!it.hasPrevious() || !(rightBrace = it.previous()).getType().equals(TAGOPEN_RIGHT)) {
            if (rightBrace != null) {
                throw new ParsingException(rightBrace.getRow(), rightBrace.getColumn(), FILE, NO_RIGHT_BRACE_INFO);
            } else {
                throw new ParsingException(0, 0, FILE, NO_RIGHT_BRACE_INFO);
            }

        }
        opentag.setRightBrace(rightBrace);
        opentag.setAttributes(buildAttributes(it));
        Token name = null;
        if (!it.hasPrevious() || !(name = it.previous()).getType().equals(TAGOPEN_NAME)) {
            if (name != null) {
                throw new ParsingException(name.getRow(), name.getColumn(), FILE, NO_TAG_NAME_INFO);
            } else {
                throw new ParsingException(0, 0, FILE, NO_TAG_NAME_INFO);
            }
        }
        opentag.setName(name);
        Token leftBrace = null;
        if (!it.hasPrevious() || !(leftBrace = it.previous()).getType().equals(TAGOPEN_LEFT)) {
            if (leftBrace != null) {
                throw new ParsingException(leftBrace.getRow(), leftBrace.getColumn(), FILE, NO_LEFT_BRACE_INFO);
            } else {
                throw new ParsingException(0, 0, FILE, NO_LEFT_BRACE_INFO);
            }

        }
        opentag.setLeftBrace(leftBrace);

        return opentag;
    }

    private Closetag buildClosetag(ListIterator<Token> it) throws ParsingException {
        Closetag closetag = new Closetag();
        Token rightBrace = null;
        if (!it.hasPrevious() || !(rightBrace = it.previous()).getType().equals(TAGCLOSE_RIGHT)) {
            if (rightBrace != null) {
                throw new ParsingException(rightBrace.getRow(), rightBrace.getColumn(), FILE, NO_RIGHT_BRACE_INFO);
            } else {
                throw new ParsingException(0, 0, FILE, NO_RIGHT_BRACE_INFO);
            }        }
        closetag.setRightBrace(rightBrace);
        Token name = null;
        if (!it.hasPrevious() || !(name = it.previous()).getType().equals(TAGCLOSE_NAME)) {
            if (name != null) {
                throw new ParsingException(name.getRow(), name.getColumn(), FILE, NO_TAG_NAME_INFO);
            } else {
                throw new ParsingException(0, 0, FILE, NO_TAG_NAME_INFO);
            }        }
        closetag.setName(name);
        Token leftBrace = null;
        if (!it.hasPrevious() || !(leftBrace = it.previous()).getType().equals(TAGCLOSE_LEFT)) {
            if (leftBrace != null) {
                throw new ParsingException(leftBrace.getRow(), leftBrace.getColumn(), FILE, NO_LEFT_BRACE_INFO);
            } else {
                throw new ParsingException(0, 0, FILE, NO_LEFT_BRACE_INFO);
            }        }
        closetag.setLeftBrace(leftBrace);

        return closetag;
    }

    private List<Attribute> buildAttributes(ListIterator<Token> it) {
        List<Attribute> attributes = new ArrayList<>();

        Token t;
        Token equals = null;
        Token value = null;
        while (it.hasPrevious() && !(t = it.previous()).getType().equals(TAGOPEN_NAME)) {
            switch (t.getType()) {
                case ATTR_NAME:
                    Attribute attribute = new Attribute();
                    if (equals != null && value != null) {
                        attribute.setHasValue(true);
                        attribute.setEquals(equals);
                        attribute.setValue(value);
                    }
                    attribute.setName(t);
                    attributes.add(attribute);
                    break;
                case EQUALS:
                    equals = t;
                    break;
                case SINGLEQUOTED:
                case DOUBLEQUOTED:
                case NUM:
                    value = t;
                    break;
            }
        }
        it.next();
        return attributes;
    }
}

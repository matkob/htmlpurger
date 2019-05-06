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
    private final String NO_RIGHT_BRACE_INFO = "no right brace found";
    private final String NO_TAG_NAME_INFO = "no tag name found";
    private final String NO_LEFT_BRACE_INFO = "no left brace found";

    private HtmlLexer lexer;
    private IReader reader;

    public HtmlParser(IReader reader) {
        this.lexer = new HtmlLexer(reader);
        this.reader = reader;
    }

    public Tag parseHtml() throws Exception {
        logger.info("parsing html file");
        List<Token> tokens = new ArrayList<>();
        Token t;
        while (!(t = lexer.nextToken()).getType().equals(END_OF_TEXT)) {
            tokens.add(t);
        }

        ListIterator<Token> end = tokens.listIterator(tokens.size());
        return buildTag(end);
    }

    private Tag buildTag(ListIterator<Token> end) throws Exception {
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
        return tag;
    }

    private Opentag buildOpentag(ListIterator<Token> it) throws ParsingException {
        Opentag opentag = new Opentag();
        Token rightBrace;
        if (!it.hasPrevious() || !(rightBrace = it.previous()).getType().equals(TAGOPEN_RIGHT)) {
            throw new ParsingException(reader.getRow(), reader.getColumn(), reader.getErrorMessage(), NO_RIGHT_BRACE_INFO);
        }
        opentag.setRightBrace(rightBrace);

        opentag.setAttributes(buildAttributes(it));

        Token name;
        if (!it.hasPrevious() || !(name = it.previous()).getType().equals(TAGOPEN_NAME)) {
            throw new ParsingException(reader.getRow(), reader.getColumn(), reader.getErrorMessage(), NO_TAG_NAME_INFO);
        }
        opentag.setName(name);

        Token leftBrace;
        if (!it.hasPrevious() || !(leftBrace = it.previous()).getType().equals(TAGOPEN_LEFT)) {
            throw new ParsingException(reader.getRow(), reader.getColumn(), reader.getErrorMessage(), NO_LEFT_BRACE_INFO);
        }
        opentag.setLeftBrace(leftBrace);

        return opentag;
    }

    private Closetag buildClosetag(ListIterator<Token> it) throws ParsingException {
        Closetag closetag = new Closetag();
        Token rightBrace;
        if (!it.hasPrevious() || !(rightBrace = it.previous()).getType().equals(TAGCLOSE_RIGHT)) {
            throw new ParsingException(reader.getRow(), reader.getColumn(), reader.getErrorMessage(), NO_RIGHT_BRACE_INFO);
        }
        closetag.setRightBrace(rightBrace);
        Token name;
        if (!it.hasPrevious() || !(name = it.previous()).getType().equals(TAGCLOSE_NAME)) {
            throw new ParsingException(reader.getRow(), reader.getColumn(), reader.getErrorMessage(), NO_TAG_NAME_INFO);
        }
        closetag.setName(name);
        Token leftBrace;
        if (!it.hasPrevious() || !(leftBrace = it.previous()).getType().equals(TAGCLOSE_LEFT)) {
            throw new ParsingException(reader.getRow(), reader.getColumn(), reader.getErrorMessage(), NO_LEFT_BRACE_INFO);
        }
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

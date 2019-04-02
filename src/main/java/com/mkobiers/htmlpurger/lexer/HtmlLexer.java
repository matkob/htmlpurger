package com.mkobiers.htmlpurger.lexer;

import com.mkobiers.htmlpurger.io.IReader;
import com.mkobiers.htmlpurger.model.GrammarException;
import com.mkobiers.htmlpurger.model.Token;
import com.mkobiers.htmlpurger.model.TokenType;

import static com.mkobiers.htmlpurger.model.TokenType.*;

public class HtmlLexer {

    private IReader reader;
    private TokenType next;
    private StringBuilder builder;

    public HtmlLexer(IReader reader) {
        this.reader = reader;
        this.next = TAG;
        this.builder = new StringBuilder();
    }

    public Token nextToken() throws GrammarException {
        char c;
        String text;
        switch (next) {
            case TAG: return buildTag();
            case OPENTAG:
            case CONTENT: return buildContent();
            case CLOSETAG:
            case TAGOPEN_NAME: return buildTagopenName();
            case TAGCLOSE_NAME: return buildTagcloseName();
            case ATTRIBUTES:
            case ATTR:
            case ATTR_NAME: return buildAttrName();
            case EQUALS:
            case VALUE:
            case STRING:
            case SINGLEQUOTED:
            case SINGLEQUOTE:
            case DOUBLEQUOTED:
            case DOUBLEQUOTE:
            case NUM:
            case TEXT:
            case TAGOPEN_LEFT: return buildTagopenLeft();
            case TAGOPEN_RIGHT: return buildTagopenRight();
            case TAGCLOSE_LEFT: return buildTagopenLeft();
            case TAGCLOSE_RIGHT: return buildTagcloseRight();
            case END_OF_TEXT:
        }
        return null;
    }

    private Token buildTag() throws GrammarException {
        char c;
        c = truncate();
        if (c == '<') {
            next = TAGOPEN_NAME;
            builder.append(c);
            return buildTagopenLeft();
        }
        throw new GrammarException();
    }

    private Token buildContent() throws GrammarException {
        char c;
        c = truncate();
        builder.append(c);
        if (c == '<') {
            next = TAGOPEN_LEFT;
            return buildTagopenLeft();
        } else {
            next = TEXT;
            return buildText();
        }
    }

    private Token buildTagopenLeft() throws GrammarException {
        String text = builder.toString();
        char c = reader.nextChar();
        builder.append(c);
        if (c == '/') {
            next = TAGCLOSE_LEFT;
            return buildTagcloseLeft();
        }
        next = TAGOPEN_NAME;
        builder.deleteCharAt(0);
        return new Token(text, TAGOPEN_LEFT);
    }

    private Token buildTagopenRight() throws GrammarException {
        String text = builder.toString();
        builder = new StringBuilder();
        next = CONTENT;
        return new Token(text, TAGOPEN_RIGHT);
    }

    private Token buildTagopenName() throws GrammarException {
        char c;
        String text;
        while ((c = reader.nextChar()) != ' ' && c != '>') {
            builder.append(c);
        }
        if (c == ' ') {
            next = ATTR_NAME;
        } else {
            next = TAGOPEN_RIGHT;
        }
        text = builder.toString();
        builder = new StringBuilder();
        builder.append(c);
        return new Token(text, TAGOPEN_NAME);
    }

    private Token buildTagcloseName() throws GrammarException {
        char c = truncate();
        String text;
        if (c != '>') {
            builder.append(c);
            while ((c = reader.nextChar()) != '>') {
                builder.append(c);
            }
            next = TAGCLOSE_RIGHT;
            text = builder.toString();
            builder = new StringBuilder();
            builder.append(c);
            return new Token(text, TAGCLOSE_NAME);
        }
        throw new GrammarException();
    }

    private Token buildTagcloseLeft() throws GrammarException {
        String text;
        next = TAGCLOSE_NAME;
        text = builder.toString();
        builder = new StringBuilder();
        return new Token(text, CLOSETAG);
    }

    private Token buildTagcloseRight() throws GrammarException {
        String text = builder.toString();
        builder = new StringBuilder();
        next = CONTENT;
        return new Token(text, TAGCLOSE_RIGHT);
    }

    private Token buildAttrName() throws GrammarException {
        char c;
        String text;//TODO truncate
        while ((c = reader.nextChar()) != '=' && c != '>') {
            builder.append(c);
        }
        if (c == '=') {
            next = VALUE;
        } else {
            next = CONTENT;
        }
        text = builder.toString();
        builder = new StringBuilder();
        builder.append(c);
        return new Token(text, ATTR_NAME);
    }

    private Token buildText() throws GrammarException {
        char c;
        String text;
        while ((c = reader.nextChar()) != '<') {
            builder.append(c);
        }
        next = TAGCLOSE_LEFT;
        text = builder.toString();
        builder = new StringBuilder();
        builder.append(c);
        return new Token(text, TEXT);
    }

    private char truncate() {
        char c;
        while ((c = reader.nextChar()) == ' ' || c == '\n' || c == '\t' || c == '\r');
        return c;
    }

}

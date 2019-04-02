package com.mkobiers.htmlpurger.lexer;

import com.mkobiers.htmlpurger.io.IReader;
import com.mkobiers.htmlpurger.model.GrammarException;
import com.mkobiers.htmlpurger.model.Token;
import com.mkobiers.htmlpurger.model.TokenType;

import static com.mkobiers.htmlpurger.model.TokenType.*;

public class HtmlLexer {

    private IReader reader;
    private TokenType current;
    private StringBuilder builder;

    public HtmlLexer(IReader reader) {
        this.reader = reader;
        this.current = TAGOPEN_LEFT;
        this.builder = new StringBuilder();
    }

    public Token nextToken() throws GrammarException {
        char c;
        String text;
        switch (current) {
            case TAG:
            case OPENTAG:
            case CONTENT: return buildContent();

            case CLOSETAG: return buildClosetag();
            case TAGOPEN_NAME: return buildTagopenName();
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
            case TAGCLOSE_LEFT:
            case TAGCLOSE_RIGHT:
            case END_OF_TEXT:

        }
        return null;
    }

    private Token buildContent() throws GrammarException {
        char c;
        c = reader.nextChar();
        builder.append(c);
        if (c == '<') {
            current = TAGOPEN_LEFT;
            return buildTagopenLeft();
        } else {
            current = TEXT;
            return buildText();
        }
    }

    private Token buildTagopenLeft() throws GrammarException {
        char c;
        c = truncate();
        if (c == '<') {
            current = TAGOPEN_NAME;
            return new Token("<", TAGOPEN_LEFT);
        }
        throw new GrammarException();
    }

    private Token buildTagopenRight() throws GrammarException {
        char c;
        c = truncate();
        if (c == '>') {
            current = CONTENT;
            return new Token(">", TAGOPEN_RIGHT);
        }
        throw new GrammarException();
    }

    private Token buildTagopenName() throws GrammarException {
        char c;
        String text;
        while ((c = reader.nextChar()) != ' ' && c != '>') {
            builder.append(c);
        }
        if (c == ' ') {
            current = ATTR_NAME;
        } else {
            current = TAGOPEN_RIGHT;
        }
        text = builder.toString();
        builder = new StringBuilder();
        return new Token(text, TAGOPEN_NAME);
    }

    private Token buildClosetag() throws GrammarException {
        char c;
        String text;
        return null;
    }

    private Token buildAttrName() throws GrammarException {
        char c;
        String text;
        while ((c = reader.nextChar()) != '=' && c != '>') {
            builder.append(c);
        }
        if (c == '=') {
            current = VALUE;
        } else {
            current = CONTENT;
        }
        text = builder.toString();
        builder = new StringBuilder();
        return new Token(text, ATTR_NAME);
    }

    private Token buildText() throws GrammarException {
        char c;
        String text;
        while ((c = reader.nextChar()) != '<') {
            builder.append(c);
        }
        current = CLOSETAG;
        text = builder.toString();
        return new Token(text, TEXT);
    }

    private char truncate() {
        char c;
        while ((c = reader.nextChar()) == ' ' || c == '\n' || c == '\t');
        return c;
    }

}

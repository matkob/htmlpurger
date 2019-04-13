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
        this.next = TAGOPEN_LEFT;
        this.builder = new StringBuilder();
    }

    public Token nextToken() throws Exception {
        switch (next) {
            case TAGOPEN_LEFT: return buildTagopenLeft();
            case TAGOPEN_RIGHT: return buildTagopenRight();
            case TAGOPEN_NAME: return buildTagopenName();

            case TAGCLOSE_LEFT: return buildTagcloseLeft();
            case TAGCLOSE_RIGHT: return buildTagcloseRight();
            case TAGCLOSE_NAME: return buildTagcloseName();

            case ATTR_NAME: return buildAttrName();
            case EQUALS: return buildEquals();
            case VALUE: return buildValue();

            case CONTENT: return buildContent();
            case SINGLEQUOTED: return buildSinglequoted();
            case DOUBLEQUOTED: return buildDoublequoted();
            case NUM: return buildNum();
            case TEXT: return buildText();
        }
        throw new Exception("No matching state found");
    }

    private Token buildTagopenLeft() throws GrammarException {
        char c = truncate();
        if (c != '<') {
            throw new GrammarException(reader.getRow(), reader.getColumn(), reader.getErrorMessage());
        }
        builder.append(c);
        c = reader.nextChar();
        if (c == '/') {
            next = TAGCLOSE_LEFT;
            reader.rewind();
            return buildTagcloseLeft();
        }
        reader.rewind();
        next = TAGOPEN_NAME;
        String text = builder.toString();
        builder = new StringBuilder();
        return new Token(text, TAGOPEN_LEFT);
    }

    private Token buildTagopenRight() throws GrammarException {
        char c = reader.nextChar();
        if (c != '>') {
            throw new GrammarException(reader.getRow(), reader.getColumn(), reader.getErrorMessage());
        }
        builder.append(c);
        next = CONTENT;
        String text = builder.toString();
        builder = new StringBuilder();
        return new Token(text, TAGOPEN_RIGHT);
    }

    private Token buildTagopenName() throws GrammarException {
        String text;
        char c = reader.nextChar();
        if (!isAlpha(c)) {
            throw new GrammarException(reader.getRow(), reader.getColumn(), reader.getErrorMessage());
        }
        builder.append(c);
        while (isAlphaNum(c = reader.nextChar())) {
            builder.append(c);
        }
        if (c == ' ') {
            reader.rewind();
            next = ATTR_NAME;
            text = builder.toString();
            builder = new StringBuilder();
            return new Token(text, TAGOPEN_NAME);
        } else if(c == '>') {
            reader.rewind();
            next = TAGOPEN_RIGHT;
            text = builder.toString();
            builder = new StringBuilder();
            return new Token(text, TAGOPEN_NAME);
        } else {
            throw new GrammarException(reader.getRow(), reader.getColumn(), reader.getErrorMessage());
        }
    }

    private Token buildTagcloseLeft() throws GrammarException {
        char c = reader.nextChar();
        if (c != '/') {
            throw new GrammarException(reader.getRow(), reader.getColumn(), reader.getErrorMessage());
        }
        builder.append(c);
        next = TAGCLOSE_NAME;
        String text = builder.toString();
        builder = new StringBuilder();
        return new Token(text, TAGCLOSE_LEFT);
    }

    private Token buildTagcloseRight() throws GrammarException {
        char c = reader.nextChar();
        if (c != '>') {
            throw new GrammarException(reader.getRow(), reader.getColumn(), reader.getErrorMessage());
        }
        builder.append(c);
        next = CONTENT;
        String text = builder.toString();
        builder = new StringBuilder();
        return new Token(text, TAGCLOSE_RIGHT);
    }

    private Token buildTagcloseName() throws GrammarException {
        String text;
        char c = reader.nextChar();
        if (!isAlpha(c)) {
            throw new GrammarException(reader.getRow(), reader.getColumn(), reader.getErrorMessage());
        }
        builder.append(c);
        while (isAlphaNum(c = reader.nextChar())) {
            builder.append(c);
        }
        if (c == '>') {
            reader.rewind();
            next = TAGCLOSE_RIGHT;
            text = builder.toString();
            builder = new StringBuilder();
            return new Token(text, TAGCLOSE_NAME);
        } else {
            throw new GrammarException(reader.getRow(), reader.getColumn(), reader.getErrorMessage());
        }
    }

    private Token buildAttrName() throws GrammarException {
        char c = truncate();
        String text;
        if (!isAlpha(c)) {
            throw new GrammarException(reader.getRow(), reader.getColumn(), reader.getErrorMessage());
        }
        builder.append(c);
        while (isAlphaNum(c = reader.nextChar())) {
            builder.append(c);
        }
        if (c == '=') {
            next = EQUALS;
        } else if (c == '>') {
            next = TAGOPEN_RIGHT;
        } else {
            throw new GrammarException(reader.getRow(), reader.getColumn(), reader.getErrorMessage());
        }
        reader.rewind();
        text = builder.toString();
        builder = new StringBuilder();
        return new Token(text, ATTR_NAME);
    }

    private Token buildValue() throws GrammarException {
        char c = truncate();
        if (c == '\'') {
            reader.rewind();
            next = SINGLEQUOTED;
            return buildSinglequoted();
        } else if (c == '"') {
            reader.rewind();
            next = DOUBLEQUOTED;
            return buildDoublequoted();
        } else if (c >= 48 && c <= 57) {
            reader.rewind();
            next = NUM;
            return buildNum();
        }
        throw new GrammarException(reader.getRow(), reader.getColumn(), reader.getErrorMessage());
    }

    private Token buildEquals() throws GrammarException {
        char c = reader.nextChar();
        if (c != '=') {
            throw new GrammarException(reader.getRow(), reader.getColumn(), reader.getErrorMessage());
        }
        builder.append(c);
        next = VALUE;
        String text = builder.toString();
        builder = new StringBuilder();
        return new Token(text, EQUALS);
    }

    private Token buildSinglequoted() throws GrammarException {
        String text;
        char c = reader.nextChar();
        if (c != '\'') {
            throw new GrammarException(reader.getRow(), reader.getColumn(), reader.getErrorMessage());
        }
        builder.append(c);
        while (isPrintable(c = reader.nextChar()) && c != '\'') {
            builder.append(c);
        }
        if (c == '\'') {
            builder.append(c);
        } else {
            throw new GrammarException(reader.getRow(), reader.getColumn(), reader.getErrorMessage());
        }
        c = reader.nextChar();
        if (c == '>') {
            next = TAGOPEN_RIGHT;
        } else if (c == ' ') {
            next = ATTR_NAME;
        } else {
            throw new GrammarException(reader.getRow(), reader.getColumn(), reader.getErrorMessage());
        }
        reader.rewind();
        text = builder.toString();
        builder = new StringBuilder();
        return new Token(text, SINGLEQUOTED);
    }

    private Token buildDoublequoted() throws GrammarException {
        String text;
        char c = reader.nextChar();
        if (c != '\"') {
            throw new GrammarException(reader.getRow(), reader.getColumn(), reader.getErrorMessage());
        }
        while (isPrintable(c = reader.nextChar()) && c != '\"') {
            builder.append(c);
        }
        if (c == '\"') {
            builder.append(c);
        } else {
            throw new GrammarException(reader.getRow(), reader.getColumn(), reader.getErrorMessage());
        }
        c = reader.nextChar();
        if (c == '>') {
            next = TAGOPEN_RIGHT;
        } else if (c == ' ') {
            next = ATTR_NAME;
        } else {
            throw new GrammarException(reader.getRow(), reader.getColumn(), reader.getErrorMessage());
        }
        reader.rewind();
        text = builder.toString();
        builder = new StringBuilder();
        return new Token(text, DOUBLEQUOTED);
    }

    private Token buildNum() throws GrammarException {
        char c;
        String text;
        while (isNum(c = reader.nextChar())) {
            builder.append(c);
        }
        if (c == '>') {
            next = TAGOPEN_RIGHT;
        } else if (c == ' ') {
            next = ATTR_NAME;
        } else {
            throw new GrammarException(reader.getRow(), reader.getColumn(), reader.getErrorMessage());
        }
        reader.rewind();
        text = builder.toString();
        builder = new StringBuilder();
        return new Token(text, NUM);
    }

    private Token buildContent() throws GrammarException {
        char c;
        c = reader.nextChar();
        if (c == '<') {
            reader.rewind();
            next = TAGOPEN_LEFT;
            return buildTagopenLeft();
        } else {
            reader.rewind();
            next = TEXT;
            return buildText();
        }
    }

    private Token buildText() throws GrammarException {
        char c;
        String text;
        while ((isPrintable(c = reader.nextChar()) || isWhiteChar(c)) && c != '<') {
            builder.append(c);
        }
        if (c == '<') {
            reader.rewind();
            next = TAGOPEN_LEFT;
        } else if (c == 0) {
            text = builder.toString();
            return new Token(text, END_OF_TEXT);
        } else {
            throw new GrammarException(reader.getRow(), reader.getColumn(), reader.getErrorMessage());
        }
        text = builder.toString();
        builder = new StringBuilder();
        return new Token(text, TEXT);
    }

    private char truncate() {
        char c;
        while ((c = reader.nextChar()) == ' ' || c == '\t');
        return c;
    }

    private boolean isWhiteChar(char c) {
        return c == ' ' || c == '\t';
    }

    private boolean isAlpha(char c) {
        return (c > 64 && c < 91) || (c > 96 && c < 123);
    }

    private boolean isNum(char c) {
        return c > 47 && c < 58;
    }

    private boolean isAlphaNum(char c) {
        return isAlpha(c) || isNum(c);
    }

    private boolean isPrintable(char c) {
        return c > 31 && c < 127;
    }

}

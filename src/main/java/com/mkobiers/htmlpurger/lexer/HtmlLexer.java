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
        switch (next) {
            case TAG: return buildTag();

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
        return new Token(builder.toString(), END_OF_TEXT);
    }

    private Token buildTag() throws GrammarException {
        char c = truncate();
        if (c == '<') {
            next = TAGOPEN_LEFT;
            builder.append(c);
            return buildTagopenLeft();
        }
        throw new GrammarException(reader.getRow(), reader.getColumn(), reader.getErrorMessage());
    }

    private Token buildTagopenLeft() throws GrammarException {
        String text = builder.toString();
        char c = reader.nextChar();
        builder.append(c);
        if (c == '/') {
            next = TAGCLOSE_LEFT;
            return buildTagcloseLeft();
        }
        if (isAlpha(c)) {
            next = TAGOPEN_NAME;
            builder.deleteCharAt(0);
            return new Token(text, TAGOPEN_LEFT);
        }
        throw new GrammarException(reader.getRow(), reader.getColumn(), reader.getErrorMessage());
    }

    private Token buildTagopenRight() throws GrammarException {
        String text = builder.toString();
        builder = new StringBuilder();
        next = CONTENT;
        return new Token(text, TAGOPEN_RIGHT);
    }

    private Token buildTagopenName() throws GrammarException {
        String text;
        char c;
        while (isAlphaNum(c = reader.nextChar())) {
            builder.append(c);
        }
        if (c == ' ') {
            next = ATTR_NAME;
            text = builder.toString();
            builder = new StringBuilder();
            return new Token(text, TAGOPEN_NAME);
        } else if(c == '>') {
            next = TAGOPEN_RIGHT;
            text = builder.toString();
            builder = new StringBuilder();
            builder.append(c);
            return new Token(text, TAGOPEN_NAME);
        } else {
            throw new GrammarException(reader.getRow(), reader.getColumn(), reader.getErrorMessage());
        }
    }

    private Token buildTagcloseLeft() throws GrammarException {
        String text;
        next = TAGCLOSE_NAME;
        text = builder.toString();
        builder = new StringBuilder();
        return new Token(text, TAGCLOSE_LEFT);
    }

    private Token buildTagcloseRight() throws GrammarException {
        String text = builder.toString();
        builder = new StringBuilder();
        next = CONTENT;
        return new Token(text, TAGCLOSE_RIGHT);
    }

    private Token buildTagcloseName() throws GrammarException {
        String text;
        char c = reader.nextChar();
        if (!isAlpha(c)) {
            throw new GrammarException(reader.getRow(), reader.getColumn(), reader.getErrorMessage());
        }
        if (c != '>') {
            builder.append(c);
            while ((c = reader.nextChar()) != '>' && c != '<' && !isWhiteChar(c)) {
                builder.append(c);
            }
            if (isWhiteChar(c) || c == '<') {
                throw new GrammarException(reader.getRow(), reader.getColumn(), reader.getErrorMessage());
            }
            next = TAGCLOSE_RIGHT;
            text = builder.toString();
            builder = new StringBuilder();
            builder.append(c);
            return new Token(text, TAGCLOSE_NAME);
        }
        throw new GrammarException(reader.getRow(), reader.getColumn(), reader.getErrorMessage());
    }

    private Token buildAttrName() throws GrammarException {
        char c = truncate();
        String text;
        builder.append(c);
        while ((c = reader.nextChar()) != '=' && c != '>') {
            builder.append(c);
        }
        if (c == '=') {
            next = EQUALS;
        } else {
            next = TAGOPEN_RIGHT;
        }
        text = builder.toString();
        builder = new StringBuilder();
        builder.append(c);
        return new Token(text, ATTR_NAME);
    }

    private Token buildValue() throws GrammarException {
        char c = truncate();
        builder.append(c);
        if (c == '\'') {
            next = SINGLEQUOTED;
            return buildSinglequoted();
        } else if (c == '"') {
            next = DOUBLEQUOTED;
            return buildDoublequoted();
        } else if (c >= 48 && c <= 57) {
            next = NUM;
            return buildNum();
        }
        throw new GrammarException(reader.getRow(), reader.getColumn(), reader.getErrorMessage());
    }

    private Token buildEquals() {
        String text = builder.toString();
        builder = new StringBuilder();
        next = VALUE;
        return new Token(text, EQUALS);
    }

    private Token buildSinglequoted() throws GrammarException {
        char c;
        String text;
        while ((c = reader.nextChar()) != '\'') {
            builder.append(c);
        }
        builder.append(c);
        c = reader.nextChar();
        if (c == '>') {
            next = TAGOPEN_RIGHT;
        } else if (c == ' ') {
            next = ATTR_NAME;
        } else {
            throw new GrammarException(reader.getRow(), reader.getColumn(), reader.getErrorMessage());
        }
        text = builder.toString();
        builder = new StringBuilder();
        builder.append(c);
        return new Token(text, SINGLEQUOTED);
    }

    private Token buildDoublequoted() throws GrammarException {
        char c;
        String text;
        while ((c = reader.nextChar()) != '"') {
            builder.append(c);
        }
        builder.append(c);
        c = reader.nextChar();
        if (c == '>') {
            next = TAGOPEN_RIGHT;
        } else if (c == ' ') {
            next = ATTR_NAME;
        } else {
            throw new GrammarException(reader.getRow(), reader.getColumn(), reader.getErrorMessage());
        }
        text = builder.toString();
        builder = new StringBuilder();
        builder.append(c);
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
        text = builder.toString();
        builder = new StringBuilder();
        builder.append(c);
        return new Token(text, NUM);
    }

    private Token buildContent() throws GrammarException {
        char c;
        c = truncate();
        builder.append(c);
        if (c == '<') {
            next = TAGOPEN_LEFT;
            return buildTagopenLeft();
        } else if (c != 0){
            next = TEXT;
            return buildText();
        } else {
            return new Token(builder.toString(), END_OF_TEXT);
        }
    }

    private Token buildText() throws GrammarException {
        char c;
        String text;
        while ((c = reader.nextChar()) != '<') {
            if (c != '\n' && c != '\r') {
                builder.append(c);
            }
        }
        next = TAGOPEN_LEFT;
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

    private boolean isWhiteChar(char c) {
        return c == ' ' || c == '\n' || c == '\t' || c == '\r';
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

}

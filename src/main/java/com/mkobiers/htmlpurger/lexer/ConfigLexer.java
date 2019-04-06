package com.mkobiers.htmlpurger.lexer;

import com.mkobiers.htmlpurger.io.IReader;
import com.mkobiers.htmlpurger.model.GrammarException;
import com.mkobiers.htmlpurger.model.Token;
import com.mkobiers.htmlpurger.model.TokenType;

import static com.mkobiers.htmlpurger.model.TokenType.*;

public class ConfigLexer {

    private IReader reader;
    private TokenType current;
    private StringBuilder builder;

    public ConfigLexer(IReader reader) {
        this.reader = reader;
        this.current = TAGNAME;
        this.builder = new StringBuilder();
    }

    public Token nextToken() throws GrammarException {
        switch (current) {
            case TAGNAME:
                Token tagname = buildTagname();
                if (tagname != null) {
                    return tagname;
                }
                break;
            case LEFT_BRACE: return buildLeftbrace();
            case RULE: return buildRule();
            case COMMA: return buildComma();
            case RIGHT_BRACE: return buildRightbrace();
        }
        return buildEnd();
    }

    private Token buildTagname() throws GrammarException {
        String text;
        char c = truncate();
        if (c >= 'A') {
            builder.append(c);
        } else if (c == 0) {
            return null;
        } else {
            throw new GrammarException(reader.getRow(), reader.getColumn(), reader.getErrorMessage());
        }

        while ((c = reader.nextChar()) != '{' && c != 0) {
            if (c != ' ' && c != '\n' && c != '\t' && c != '\r') {
                builder.append(c);
            }
        }
        if (c == '{') {
            text = builder.toString();
            current = LEFT_BRACE;
            builder = new StringBuilder();
            builder.append(c);
            return new Token(text, TAGNAME);
        }
        throw new GrammarException(reader.getRow(), reader.getColumn(), reader.getErrorMessage());
    }

    private Token buildRule() throws GrammarException {
        String text;
        char c = truncate();
        if (c >= 'a') {
            builder.append(c);
        } else {
            throw new GrammarException(reader.getRow(), reader.getColumn(), reader.getErrorMessage());
        }

        while ((c = reader.nextChar()) != '}' && c != ',' && c != 0) {
            if (c != ' ' && c != '\n' && c != '\t' && c != '\r') {
                builder.append(c);
            }
        }
        if (c == '}') {
            current = RIGHT_BRACE;
        } else if (c == ',') {
            current = COMMA;
        } else {
            throw new GrammarException(reader.getRow(), reader.getColumn(), reader.getErrorMessage());
        }

        text = builder.toString();
        if (!isRuleSupported(text)) {
            throw new GrammarException(reader.getRow(), reader.getColumn(), reader.getErrorMessage());
        }
        builder = new StringBuilder();
        builder.append(c);
        return new Token(text, RULE);
    }

    private Token buildComma() {
        String text;
        text = builder.toString();
        current = RULE;
        builder = new StringBuilder();
        return new Token(text, COMMA);
    }

    private Token buildLeftbrace() {
        String text;
        text = builder.toString();
        current = RULE;
        builder = new StringBuilder();
        return new Token(text, LEFT_BRACE);
    }

    private Token buildRightbrace() {
        String text;
        text = builder.toString();
        current = TAGNAME;
        builder = new StringBuilder();
        return new Token(text, RIGHT_BRACE);
    }

    private Token buildEnd() {
        String text;
        text = builder.toString();
        current = END_OF_TEXT;
        return new Token(text, END_OF_TEXT);
    }
    
    private boolean isRuleSupported(String text) {
        return text.equals("remove") || text.equals("all") || text.equals("border") || text.equals("margin")
                || text.equals("color") || text.equals("font") || text.equals("text");
    }

    private char truncate() {
        char c;
        while ((c = reader.nextChar()) == ' ' || c == '\n' || c == '\t' || c == '\r');
        return c;
    }
}

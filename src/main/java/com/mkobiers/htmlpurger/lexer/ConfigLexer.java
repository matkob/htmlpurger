package com.mkobiers.htmlpurger.lexer;

import com.mkobiers.htmlpurger.io.IReader;
import com.mkobiers.htmlpurger.model.Token;
import com.mkobiers.htmlpurger.model.TokenType;
import com.mkobiers.htmlpurger.model.exception.GrammarException;

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

    public Token nextToken() throws Exception {
        switch (current) {
            case TAGNAME: return buildTagname();
            case LEFT_BRACE: return buildLeftbrace();
            case RULE: return buildRule();
            case COMMA: return buildComma();
            case RIGHT_BRACE: return buildRightbrace();
        }
        throw new Exception("No matching state found");
    }

    private Token buildTagname() throws GrammarException {
        String text;
        char c = truncate();
        if (c == 0) {
            text = builder.toString();
            return new Token(text, END_OF_TEXT);
        }
        if (!isAlpha(c)) {
            throw new GrammarException(reader.getRow(), reader.getColumn(), reader.getErrorMessage());
        }
        int startRow = reader.getRow();
        int startColumn = reader.getColumn();
        builder.append(c);
        while (isAlphaNum(c = reader.nextChar())) {
            builder.append(c);
        }
        reader.rewind();
        current = LEFT_BRACE;
        text = builder.toString();
        builder = new StringBuilder();
        return new Token(text, TAGNAME, startRow, startColumn);
    }

    private Token buildRule() throws GrammarException {
        String text;
        char c = truncate();
        int startRow = reader.getRow();
        int startColumn = reader.getColumn();
        if (isAlpha(c)) {
            builder.append(c);
        } else {
            throw new GrammarException(reader.getRow(), reader.getColumn(), reader.getErrorMessage());
        }
        while (isAlpha(c = reader.nextChar())) {
            builder.append(c);
        }
        if (c == ',') {
            reader.rewind();
            current = COMMA;
        } else {
            reader.rewind();
            current = RIGHT_BRACE;
        }
        text = builder.toString();
        if (!isRuleSupported(text)) {
            throw new GrammarException(reader.getRow(), reader.getColumn(), reader.getErrorMessage());
        }
        builder = new StringBuilder();
        return new Token(text, RULE, startRow, startColumn);
    }

    private Token buildComma() throws GrammarException {
        int startRow = reader.getRow();
        int startColumn = reader.getColumn();
        char c = reader.nextChar();
        if (c != ',') {
            throw new GrammarException(reader.getRow(), reader.getColumn(), reader.getErrorMessage());
        }
        builder.append(c);
        current = RULE;
        String text = builder.toString();
        builder = new StringBuilder();
        return new Token(text, COMMA, startRow, startColumn);
    }

    private Token buildLeftbrace() throws GrammarException {
        char c = truncate();
        int startRow = reader.getRow();
        int startColumn = reader.getColumn();
        if (c != '{') {
            throw new GrammarException(reader.getRow(), reader.getColumn(), reader.getErrorMessage());
        }
        builder.append(c);
        current = RULE;
        String text = builder.toString();
        builder = new StringBuilder();
        return new Token(text, LEFT_BRACE, startRow, startColumn);
    }

    private Token buildRightbrace() throws GrammarException {
        char c = truncate();
        int startRow = reader.getRow();
        int startColumn = reader.getColumn();
        if (c != '}') {
            throw new GrammarException(reader.getRow(), reader.getColumn(), reader.getErrorMessage());
        }
        builder.append(c);
        current = TAGNAME;
        String text = builder.toString();
        builder = new StringBuilder();
        return new Token(text, RIGHT_BRACE, startRow, startColumn);
    }
    
    private boolean isRuleSupported(String text) {
        return text.equals("remove") || text.equals("all") || text.equals("border") || text.equals("margin")
                || text.equals("color") || text.equals("font") || text.equals("text");
    }

    private char truncate() {
        char c;
        while ((c = reader.nextChar()) == ' ' || c == '\t');
        return c;
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

package com.mkobiers.htmlpurger.lexer;

import com.mkobiers.htmlpurger.io.IReader;
import com.mkobiers.htmlpurger.model.GrammarException;
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

    public void nextToken() throws GrammarException {
        char c;
        String text;
        switch (current) {
            case TAG:
                current = OPENTAG;
                break;
            case OPENTAG:
                current = TAGOPEN_LEFT;
            case CONTENT:
            case CLOSETAG:
            case NAME:
                while ((c = reader.nextChar()) != ' ' && c != '>') {
                    builder.append(c);
                }
                if (c == ' ') {
                    current = ATTR_NAME;
                } else {
                    current = CLOSETAG;
                }
                text = builder.toString();
                builder = new StringBuilder();
                //return new Token(text, NAME);

            case ATTRIBUTES:
            case ATTR:
            case ATTR_NAME:
            case EQUALS:
            case VALUE:
            case STRING:
            case SINGLEQUOTED:
            case SINGLEQUOTE:
            case DOUBLEQUOTED:
            case DOUBLEQUOTE:
            case NUM:
            case TEXT:
            case TAGOPEN_LEFT:
                while ((c = reader.nextChar()) == ' ' || c == '\n' || c == '\t');
                if (c == '<') {
                    current = NAME;
                    //return new Token("<", TAGOPEN_LEFT);
                }
                throw new GrammarException();

            case TAGCLOSE_LEFT:
            case TAG_RIGHT:
            case END_OF_TEXT:



        }
    }

}

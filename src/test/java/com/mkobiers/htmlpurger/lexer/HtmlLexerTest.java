package com.mkobiers.htmlpurger.lexer;

import com.mkobiers.htmlpurger.io.FileReader;
import com.mkobiers.htmlpurger.io.IReader;
import com.mkobiers.htmlpurger.model.GrammarException;
import com.mkobiers.htmlpurger.model.Token;
import com.mkobiers.htmlpurger.model.TokenType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.mkobiers.htmlpurger.model.TokenType.*;

public class HtmlLexerTest {

    private static final String FILEPATH_PREFIX = "test_files/html/";

    @Test
    public void givenCorrectHtmlFileDoTokensMatch() throws Exception {
        IReader reader = new FileReader(FILEPATH_PREFIX + "correct.txt");
        HtmlLexer htmlLexer = new HtmlLexer(reader);
        Token t;
        List<Token> tokens = new ArrayList<>();
        while (!(t = htmlLexer.nextToken()).getType().equals(END_OF_TEXT)) {
            tokens.add(t);
        }

        List<TokenType> expected = Arrays.asList(
                TAGOPEN_LEFT, TAGOPEN_NAME, TAGOPEN_RIGHT,
                TAGOPEN_LEFT, TAGOPEN_NAME, TAGOPEN_RIGHT,
                TAGOPEN_LEFT, TAGOPEN_NAME, TAGOPEN_RIGHT,
                TEXT,
                TAGCLOSE_LEFT, TAGCLOSE_NAME, TAGCLOSE_RIGHT,
                TAGCLOSE_LEFT, TAGCLOSE_NAME, TAGCLOSE_RIGHT,
                TAGOPEN_LEFT, TAGOPEN_NAME, ATTR_NAME, EQUALS, DOUBLEQUOTED, TAGOPEN_RIGHT,
                TAGOPEN_LEFT, TAGOPEN_NAME, ATTR_NAME, EQUALS, DOUBLEQUOTED, ATTR_NAME, EQUALS, SINGLEQUOTED, TAGOPEN_RIGHT,
                TAGOPEN_LEFT, TAGOPEN_NAME, ATTR_NAME, EQUALS, DOUBLEQUOTED, TAGOPEN_RIGHT,
                TEXT,
                TAGCLOSE_LEFT, TAGCLOSE_NAME, TAGCLOSE_RIGHT,
                TEXT,
                TAGCLOSE_LEFT, TAGCLOSE_NAME, TAGCLOSE_RIGHT,
                TAGCLOSE_LEFT, TAGCLOSE_NAME, TAGCLOSE_RIGHT);
        Assertions.assertEquals(expected, tokens.stream().map(Token::getType).collect(Collectors.toList()));
    }

    @Test
    public void givenHtmlFileWithRandomWhiteCharsDoTokensMatch() throws Exception {
        IReader reader = new FileReader(FILEPATH_PREFIX + "white_chars.txt");
        HtmlLexer htmlLexer = new HtmlLexer(reader);
        Token t;
        List<Token> tokens = new ArrayList<>();
        while (!(t = htmlLexer.nextToken()).getType().equals(END_OF_TEXT)) {
            tokens.add(t);
        }

        List<TokenType> expected = Arrays.asList(
                TAGOPEN_LEFT, TAGOPEN_NAME, TAGOPEN_RIGHT,
                TAGOPEN_LEFT, TAGOPEN_NAME, TAGOPEN_RIGHT,
                TAGOPEN_LEFT, TAGOPEN_NAME, TAGOPEN_RIGHT,
                TEXT,
                TAGCLOSE_LEFT, TAGCLOSE_NAME, TAGCLOSE_RIGHT,
                TAGCLOSE_LEFT, TAGCLOSE_NAME, TAGCLOSE_RIGHT,
                TAGOPEN_LEFT, TAGOPEN_NAME, ATTR_NAME, EQUALS, DOUBLEQUOTED, TAGOPEN_RIGHT,
                TAGOPEN_LEFT, TAGOPEN_NAME, ATTR_NAME, EQUALS, DOUBLEQUOTED, ATTR_NAME, EQUALS, SINGLEQUOTED, TAGOPEN_RIGHT,
                TAGOPEN_LEFT, TAGOPEN_NAME, ATTR_NAME, EQUALS, DOUBLEQUOTED, TAGOPEN_RIGHT,
                TEXT,
                TAGCLOSE_LEFT, TAGCLOSE_NAME, TAGCLOSE_RIGHT,
                TEXT,
                TAGCLOSE_LEFT, TAGCLOSE_NAME, TAGCLOSE_RIGHT,
                TAGCLOSE_LEFT, TAGCLOSE_NAME, TAGCLOSE_RIGHT);
        Assertions.assertEquals(expected, tokens.stream().map(Token::getType).collect(Collectors.toList()));
    }

    @Test
    public void givenMalformedHtmlFileDoesLexerThrow() {
        Assertions.assertThrows(GrammarException.class, () -> {
            IReader reader = new FileReader(FILEPATH_PREFIX + "malformed.txt");
            HtmlLexer htmlLexer = new HtmlLexer(reader);
            Token t;
            List<Token> tokens = new ArrayList<>();
            while (!(t = htmlLexer.nextToken()).getType().equals(END_OF_TEXT)) {
                tokens.add(t);
            }
        });
    }

    @Test
    public void givenHtmlFileSuddenEndDoesLexerThrow() {
        Assertions.assertThrows(GrammarException.class, () -> {
            IReader reader = new FileReader(FILEPATH_PREFIX + "sudden_end.txt");
            HtmlLexer htmlLexer = new HtmlLexer(reader);
            Token t;
            List<Token> tokens = new ArrayList<>();
            while (!(t = htmlLexer.nextToken()).getType().equals(END_OF_TEXT)) {
                tokens.add(t);
            }
        });
    }
}

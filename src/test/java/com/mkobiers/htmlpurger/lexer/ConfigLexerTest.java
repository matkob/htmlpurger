package com.mkobiers.htmlpurger.lexer;

import com.mkobiers.htmlpurger.io.FileReader;
import com.mkobiers.htmlpurger.io.IReader;
import com.mkobiers.htmlpurger.model.Token;
import com.mkobiers.htmlpurger.model.TokenType;
import com.mkobiers.htmlpurger.model.exception.GrammarException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.mkobiers.htmlpurger.model.TokenType.*;

public class ConfigLexerTest {

    private static final String FILEPATH_PREFIX = "test_files/config/";

    @Test
    public void givenCorrectConfigFileDoTokensMatch() throws Exception {
        IReader reader = new FileReader(FILEPATH_PREFIX + "correct.txt");
        ConfigLexer configLexer = new ConfigLexer(reader);
        Token t;
        List<Token> tokens = new ArrayList<>();
        while (!(t = configLexer.nextToken()).getType().equals(END_OF_TEXT)) {
            tokens.add(t);
        }

        List<TokenType> expected = Arrays.asList(TAGNAME, LEFT_BRACE, RULE, COMMA, RULE, RIGHT_BRACE, TAGNAME, LEFT_BRACE, RULE, RIGHT_BRACE);
        Assertions.assertEquals(expected, tokens.stream().map(Token::getType).collect(Collectors.toList()));
    }

    @Test
    public void givenConfigFileWithRandomWhiteCharsDoTokensMatch() throws Exception {
        IReader reader = new FileReader(FILEPATH_PREFIX + "white_chars.txt");
        ConfigLexer configLexer = new ConfigLexer(reader);
        Token t;
        List<Token> tokens = new ArrayList<>();
        while (!(t = configLexer.nextToken()).getType().equals(END_OF_TEXT)) {
            tokens.add(t);
        }

        List<TokenType> expected = Arrays.asList(TAGNAME, LEFT_BRACE, RULE, COMMA, RULE, RIGHT_BRACE, TAGNAME, LEFT_BRACE, RULE, RIGHT_BRACE);
        Assertions.assertEquals(expected, tokens.stream().map(Token::getType).collect(Collectors.toList()));
    }

    @Test
    public void givenConfigFileWithNotSupportedRulesDoesLexerThrow() {
        Assertions.assertThrows(GrammarException.class, () -> {
            IReader reader = new FileReader(FILEPATH_PREFIX + "not_supported.txt");
            ConfigLexer configLexer = new ConfigLexer(reader);
            Token t;
            List<Token> tokens = new ArrayList<>();
            while (!(t = configLexer.nextToken()).getType().equals(END_OF_TEXT)) {
                tokens.add(t);
            }
        });
    }

    @Test
    public void givenConfigFileSuddenEndDoesLexerThrow() {
        Assertions.assertThrows(GrammarException.class, () -> {
            IReader reader = new FileReader(FILEPATH_PREFIX + "sudden_end.txt");
            ConfigLexer configLexer = new ConfigLexer(reader);
            Token t;
            List<Token> tokens = new ArrayList<>();
            while (!(t = configLexer.nextToken()).getType().equals(END_OF_TEXT)) {
                tokens.add(t);
            }
        });
    }


}

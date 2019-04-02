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

    @Test
    public void givenKnownConfigFileIsLexerReturningTokens() throws GrammarException {
        IReader reader = new FileReader("lexertesthtml.txt");
        HtmlLexer htmlLexer = new HtmlLexer(reader);
        Token t;
        List<Token> tokens = new ArrayList<>();
        while (!(t = htmlLexer.nextToken()).getType().equals(END_OF_TEXT)) {
            tokens.add(t);
            System.out.println(t.getType() + "  " + t.getText());
        }

        List<TokenType> expected = Arrays.asList(TAGNAME, LEFT_BRACE, RULE, COMMA, RULE, RIGHT_BRACE, TAGNAME, LEFT_BRACE, RULE, RIGHT_BRACE);
        Assertions.assertEquals(expected, tokens.stream().map(Token::getType).collect(Collectors.toList()));
    }
}

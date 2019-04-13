package com.mkobiers.htmlpurger.parser;

import com.mkobiers.htmlpurger.io.FileReader;
import com.mkobiers.htmlpurger.io.IReader;
import com.mkobiers.htmlpurger.model.Token;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static com.mkobiers.htmlpurger.model.TokenType.TAGNAME;

public class ConfigParserTest {

    private static final String FILEPATH_PREFIX = "test_files/config/";

    @Test
    public void givenCorrectConfigFileDoRulesMatch() throws Exception {
        IReader reader = new FileReader(FILEPATH_PREFIX + "correct.txt");
        ConfigParser parser = new ConfigParser(reader);
        Map<Token, List<Token>> rules = parser.parseConfig();

        Assertions.assertEquals(2, rules.size());
        Assertions.assertNotNull(rules.get(new Token("BODY", TAGNAME)));
        Assertions.assertNotNull(rules.get(new Token("HTML", TAGNAME)));
        Assertions.assertEquals(1, rules.get(new Token("BODY", TAGNAME)).size());
        Assertions.assertEquals(2, rules.get(new Token("HTML", TAGNAME)).size());
    }

    @Test
    public void givenConfigFileWithRandomWhiteCharsDoRulesMatch() throws Exception {
        IReader reader = new FileReader(FILEPATH_PREFIX + "white_chars.txt");
        ConfigParser parser = new ConfigParser(reader);
        Map<Token, List<Token>> rules = parser.parseConfig();

        Assertions.assertEquals(2, rules.size());
        Assertions.assertNotNull(rules.get(new Token("BODY", TAGNAME)));
        Assertions.assertNotNull(rules.get(new Token("HTML", TAGNAME)));
        Assertions.assertEquals(1, rules.get(new Token("BODY", TAGNAME)).size());
        Assertions.assertEquals(2, rules.get(new Token("HTML", TAGNAME)).size());
    }

}

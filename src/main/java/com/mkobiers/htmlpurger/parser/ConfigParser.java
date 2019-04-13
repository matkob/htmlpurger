package com.mkobiers.htmlpurger.parser;

import com.mkobiers.htmlpurger.io.IReader;
import com.mkobiers.htmlpurger.lexer.ConfigLexer;
import com.mkobiers.htmlpurger.model.ConfigEntry;
import com.mkobiers.htmlpurger.model.Token;
import com.mkobiers.htmlpurger.model.TokenType;
import com.mkobiers.htmlpurger.model.exception.ParsingException;
import com.mkobiers.htmlpurger.model.exception.ParsingWarning;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static com.mkobiers.htmlpurger.model.TokenType.*;

public class ConfigParser {

    private Logger logger = LoggerFactory.getLogger(ConfigParser.class);
    private ConfigLexer lexer;
    private Map<Token, List<Token>> rules;

    public ConfigParser(IReader reader) {
        this.lexer = new ConfigLexer(reader);
        this.rules = new HashMap<>();
    }

    public Map<Token, List<Token>> parseConfig() throws Exception {
        logger.info("parsing config file");
        Token t;
        List<Token> tokens = new ArrayList<>();
        while (!(t = lexer.nextToken()).getType().equals(END_OF_TEXT)) {
            tokens.add(t);
        }

        try {
            ListIterator<Token> it = tokens.listIterator();
            while (it.hasNext()) {
                ConfigEntry configEntry = buildConfigEntry(it);
                rules.put(configEntry.getTagname(), configEntry.getRules());
            }
        } catch (ParsingWarning w) {
            logger.warn(w.getMessage());
        }
        return rules;
    }

    private ConfigEntry buildConfigEntry(ListIterator<Token> it) throws Exception {
        ConfigEntry configEntry = new ConfigEntry(getNextToken(it, TAGNAME));
        getNextToken(it, LEFT_BRACE);
        Token rule;
        while ((rule = it.next()).getType().equals(RULE) || rule.getType().equals(COMMA)) {
            if (rule.getType().equals(RULE)) {
                configEntry.addRule(rule);
            }
        }
        if (!rule.getType().equals(RIGHT_BRACE)) {
            throw new ParsingException();
        }
        return configEntry;
    }

    private Token getNextToken(ListIterator<Token> it, TokenType type) throws Exception {
        Token t;
        if (!(t = it.next()).getType().equals(type)) {
            throw new ParsingException();
        }
        return t;
    }
}

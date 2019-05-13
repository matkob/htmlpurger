package com.mkobiers.htmlpurger.parser;

import com.mkobiers.htmlpurger.io.IReader;
import com.mkobiers.htmlpurger.lexer.ConfigLexer;
import com.mkobiers.htmlpurger.model.ConfigEntry;
import com.mkobiers.htmlpurger.model.Token;
import com.mkobiers.htmlpurger.model.exception.DuplicationException;
import com.mkobiers.htmlpurger.model.exception.GrammarException;
import com.mkobiers.htmlpurger.model.exception.ParsingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static com.mkobiers.htmlpurger.model.TokenType.*;

public class ConfigParser {

    private Logger logger = LoggerFactory.getLogger(ConfigParser.class);
    private final String NO_RIGHT_BRACE_INFO = "no right brace found";
    private final String NO_TAG_NAME_INFO = "no tag name found";
    private final String NO_LEFT_BRACE_INFO = "no left brace found";

    private ConfigLexer lexer;
    private IReader reader;
    private Map<Token, List<Token>> rules;

    public ConfigParser(IReader reader) {
        this.lexer = new ConfigLexer(reader);
        this.reader = reader;
        this.rules = new HashMap<>();
    }

    public Map<Token, List<Token>> parseConfig() throws ParsingException, DuplicationException, GrammarException {
        logger.info("parsing config file");
        List<Token> tokens = new ArrayList<>();
        Token t;
        while (!(t = lexer.nextToken()).getType().equals(END_OF_TEXT)) {
            tokens.add(t);
        }

        ListIterator<Token> it = tokens.listIterator();
        while (it.hasNext()) {
            ConfigEntry configEntry = buildConfigEntry(it);
            if (rules.containsKey(configEntry.getTagname())) {
                throw new DuplicationException(configEntry.toString(),
                        configEntry.getTagname().getRow(), configEntry.getTagname().getColumn());
            }
            rules.put(configEntry.getTagname(), configEntry.getRules());
        }
        return rules;
    }

    private ConfigEntry buildConfigEntry(ListIterator<Token> it) throws ParsingException {
        Token tagname;
        if (!(tagname = it.next()).getType().equals(TAGNAME)) {
            throw new ParsingException(tagname.getRow(), tagname.getColumn(), NO_TAG_NAME_INFO);
        }
        ConfigEntry configEntry = new ConfigEntry(tagname);
        Token leftBrace;
        if (!(leftBrace = it.next()).getType().equals(LEFT_BRACE)) {
            throw new ParsingException(leftBrace.getRow(), leftBrace.getColumn(), NO_LEFT_BRACE_INFO);
        }
        Token rule;
        while ((rule = it.next()).getType().equals(RULE) || rule.getType().equals(COMMA)) {
            if (rule.getType().equals(RULE)) {
                configEntry.addRule(rule);
            }
        }
        if (!rule.getType().equals(RIGHT_BRACE)) {
            throw new ParsingException(rule.getRow(), rule.getColumn(), NO_RIGHT_BRACE_INFO);
        }
        return configEntry;
    }

}

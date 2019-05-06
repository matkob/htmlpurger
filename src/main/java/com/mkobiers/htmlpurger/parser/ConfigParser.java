package com.mkobiers.htmlpurger.parser;

import com.mkobiers.htmlpurger.io.IReader;
import com.mkobiers.htmlpurger.lexer.ConfigLexer;
import com.mkobiers.htmlpurger.model.ConfigEntry;
import com.mkobiers.htmlpurger.model.Token;
import com.mkobiers.htmlpurger.model.exception.DuplicationException;
import com.mkobiers.htmlpurger.model.exception.ParsingException;
import com.mkobiers.htmlpurger.model.exception.ParsingWarning;
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

    public Map<Token, List<Token>> parseConfig() throws Exception {
        logger.info("parsing config file");
        List<Token> tokens = new ArrayList<>();
        Token t;
        while (!(t = lexer.nextToken()).getType().equals(END_OF_TEXT)) {
            tokens.add(t);
        }

        try {
            ListIterator<Token> it = tokens.listIterator();
            while (it.hasNext()) {
                ConfigEntry configEntry = buildConfigEntry(it);
                if (rules.containsKey(configEntry.getTagname())) {
                    throw new DuplicationException(configEntry.toString());
                }
                rules.put(configEntry.getTagname(), configEntry.getRules());
            }
        } catch (ParsingWarning w) {
            logger.warn(w.getMessage());
        }
        return rules;
    }

    private ConfigEntry buildConfigEntry(ListIterator<Token> it) throws Exception {
        Token tagname;
        if (!(tagname = it.next()).getType().equals(TAGNAME)) {
            throw new ParsingException(reader.getRow(), reader.getColumn(), reader.getErrorMessage(), NO_TAG_NAME_INFO);
        }
        ConfigEntry configEntry = new ConfigEntry(tagname);
        if (!it.next().getType().equals(LEFT_BRACE)) {
            throw new ParsingException(reader.getRow(), reader.getColumn(), reader.getErrorMessage(), NO_LEFT_BRACE_INFO);
        }
        Token rule;
        while ((rule = it.next()).getType().equals(RULE) || rule.getType().equals(COMMA)) {
            if (rule.getType().equals(RULE)) {
                configEntry.addRule(rule);
            }
        }
        if (!rule.getType().equals(RIGHT_BRACE)) {
            throw new ParsingException(reader.getRow(), reader.getColumn(), reader.getErrorMessage(), NO_RIGHT_BRACE_INFO);
        }
        return configEntry;
    }

}

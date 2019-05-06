package com.mkobiers.htmlpurger.logic;

import com.mkobiers.htmlpurger.io.IReader;
import com.mkobiers.htmlpurger.model.*;
import com.mkobiers.htmlpurger.parser.ConfigParser;
import com.mkobiers.htmlpurger.parser.HtmlParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.mkobiers.htmlpurger.model.TokenType.ATTR_NAME;

public class HtmlPurger {

    private Logger logger = LoggerFactory.getLogger(HtmlPurger.class);
    private ConfigParser configParser;
    private HtmlParser htmlParser;
    private Writer writer;
    private Map<Token, Boolean> used;

    public HtmlPurger(IReader configReader, IReader htmlReader, Writer writer) {
        this.configParser = new ConfigParser(configReader);
        this.htmlParser = new HtmlParser(htmlReader);
        this.writer = writer;
        this.used = new HashMap<>();
    }

    public void purgeHtml() throws Exception {
        Map<Token, List<Token>> config = configParser.parseConfig();
        config.keySet().forEach(token -> used.put(token, false));
        //TODO: info about unused rules
        Tag root = htmlParser.parseHtml();
        logger.info("processing html file");
        applyRules(config, root);
        used.entrySet().stream()
                .filter(tokenUsed -> !tokenUsed.getValue())
                .forEach(notUsed -> {
                    ConfigEntry entry = new ConfigEntry(notUsed.getKey(), config.get(notUsed.getKey()));
                    logger.info("\"{}\" not used", entry);
                });
        logger.info("creating output file");
        writer.write(root.toString());
        writer.close();
        logger.info("output file created");
    }

    private void applyRules(Map<Token, List<Token>> config, TagStandalone tag) {
        if (config.containsKey(tag.getTagname())) {
            logger.info("applying rules to {}", tag.getOpentag().getName().getText());
            List<Token> rules = config.get(tag.getTagname());
            used.put(tag.getTagname(), true);
            if (rules.contains(Token.REMOVE)) {
                removeTag(tag);
            } else if (rules.contains(Token.ALL)) {
                removeAll(tag);
            } else {
                filterDecorators(tag, rules);
            }
        } else {
            logger.info("no rules found for {}", tag.getOpentag().getName().getText());
        }
        if (tag instanceof Tag) {
            Tag withContent = (Tag) tag;
            withContent.getContent().stream()
                    .filter(content -> content instanceof TagStandalone)
                    .forEach(inner -> applyRules(config, (TagStandalone) inner));
        }
    }

    private void removeTag(TagStandalone tag) {
        logger.info("removing {}", tag.getOpentag().getName().getText());
        tag.clear();
    }

    private void removeAll(TagStandalone tag) {
        logger.info("removing all {} styles", tag.getOpentag().getName().getText());
        List<Attribute> attributes = tag.getOpentag().getAttributes();
        attributes = attributes.stream()
                .filter(attr -> !attr.getName().equals(new Token("style", ATTR_NAME)))
                .collect(Collectors.toList());
        tag.getOpentag().setAttributes(attributes);
    }

    private void filterDecorators(TagStandalone tag, List<Token> rules) {
        logger.info("purging {} styles", tag.getOpentag().getName().getText());
        List<Attribute> attributes = tag.getOpentag().getAttributes();
        attributes = attributes.stream()
                .peek(attr -> {
                    if (attr.getName().equals(new Token("style", ATTR_NAME)) && attr.hasValue()) {
                        StringBuilder builder = new StringBuilder();
                        String[] styles = attr.getValue().getText().substring(1, attr.getValue().getText().length()-1).split(";");
                        for (int i = 0; i < styles.length; i++) {
                            String key = styles[i].split(":")[0];
                            String val = styles[i].split(":")[1];
                            if (rules.stream().noneMatch(rule -> key.contains(rule.getText()))) {
                                builder.append(key);
                                builder.append(':');
                                builder.append(val);
                                builder.append(';');
                            }
                        }
                        builder.insert(0, attr.getValue().getText().charAt(0));
                        builder.append(attr.getValue().getText().charAt(attr.getValue().getText().length()-1));
                        String newStyles = builder.toString();
                        attr.setValue(new Token(newStyles, attr.getValue().getType()));
                    }
                }).collect(Collectors.toList());
        tag.getOpentag().setAttributes(attributes);
    }
}

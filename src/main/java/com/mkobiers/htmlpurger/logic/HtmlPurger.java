package com.mkobiers.htmlpurger.logic;

import com.mkobiers.htmlpurger.io.IReader;
import com.mkobiers.htmlpurger.model.Attribute;
import com.mkobiers.htmlpurger.model.Tag;
import com.mkobiers.htmlpurger.model.Token;
import com.mkobiers.htmlpurger.parser.ConfigParser;
import com.mkobiers.htmlpurger.parser.HtmlParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.mkobiers.htmlpurger.model.TokenType.ATTR_NAME;

public class HtmlPurger {

    private Logger logger = LoggerFactory.getLogger(HtmlPurger.class);
    private ConfigParser configParser;
    private HtmlParser htmlParser;
    private Writer writer;

    public HtmlPurger(IReader configReader, IReader htmlReader, Writer writer) {
        this.configParser = new ConfigParser(configReader);
        this.htmlParser = new HtmlParser(htmlReader);
        this.writer = writer;
    }

    public void process() throws Exception {
        Map<Token, List<Token>> config = configParser.parseConfig();
        Tag root = htmlParser.parseHtml();
        applyRules(config, root);
        writer.write(root.toString());
        writer.close();
    }

    private void applyRules(Map<Token, List<Token>> config, Tag tag) {
        if (config.containsKey(tag.getTagname())) {
            List<Token> rules = config.get(tag.getTagname());
            if (rules.contains(Token.REMOVE)) {
                removeTag(tag);
            } else if (rules.contains(Token.ALL)) {
                removeAll(tag);
            } else {
                filterDecorators(tag, rules);
            }
        }
        tag.getContent().stream()
                .filter(content -> content instanceof Tag)
                .forEach(inner -> applyRules(config, (Tag) inner));

    }

    private void removeTag(Tag tag) {
        tag.setOpentag(null);
        tag.setContent(new ArrayList<>());
        tag.setClosetag(null);
    }

    private void removeAll(Tag tag) {
        List<Attribute> attributes = tag.getOpentag().getAttributes();
        attributes = attributes.stream()
                .filter(attr -> !attr.getName().equals(new Token("style", ATTR_NAME)))
                .collect(Collectors.toList());
        tag.getOpentag().setAttributes(attributes);
    }

    private void filterDecorators(Tag tag, List<Token> rules) {
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

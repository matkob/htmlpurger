package com.mkobiers.htmlpurger.parser;

import com.mkobiers.htmlpurger.io.IReader;
import com.mkobiers.htmlpurger.lexer.HtmlLexer;
import com.mkobiers.htmlpurger.model.Tag;
import com.mkobiers.htmlpurger.model.Token;
import com.mkobiers.htmlpurger.model.exception.ParsingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;

import static com.mkobiers.htmlpurger.model.TokenType.END_OF_TEXT;
import static com.mkobiers.htmlpurger.model.TokenType.TAGNAME;

public class HtmlParser {

    private Logger logger = LoggerFactory.getLogger(HtmlParser.class);
    private HtmlLexer lexer;
    private Tag root;

    public HtmlParser(IReader reader) {
        this.lexer = new HtmlLexer(reader);
    }

    public Tag parseHtml() throws Exception {
        logger.info("parsing html file");
        Token t;
        List<Token> tokens = new ArrayList<>();
        while (!(t = lexer.nextToken()).getType().equals(END_OF_TEXT)) {
            tokens.add(t);
        }

        ListIterator<Token> begin = tokens.listIterator();
        ListIterator<Token> end = tokens.listIterator(tokens.size());
        return null;
    }

    private Tag buildTag(ListIterator<Token> begin, ListIterator<Token> end) throws Exception {
        Optional<Token> closeTag = getPreviousTagname(end);
        Optional<Token> openTag = getNextTagname(begin);
        //TODO: support for non closing tags
        if (!openTag.equals(closeTag)) {
            throw new ParsingException();
        }
        return null;
    }

    private Optional<Token> getNextTagname(ListIterator<Token> it) {
        Token t = null;
        while (it.hasNext() && !(t = it.next()).getType().equals(TAGNAME));
        return t != null && t.getType().equals(TAGNAME) ? Optional.of(t) : Optional.empty();
    }

    private Optional<Token> getPreviousTagname(ListIterator<Token> it) {
        Token t = null;
        while (it.hasPrevious() && !(t = it.previous()).getType().equals(TAGNAME));
        return t != null && t.getType().equals(TAGNAME) ? Optional.of(t) : Optional.empty();

    }
}

package com.mkobiers.htmlpurger.parser;

import com.mkobiers.htmlpurger.io.FileReader;
import com.mkobiers.htmlpurger.io.IReader;
import com.mkobiers.htmlpurger.model.Tag;
import org.junit.jupiter.api.Test;

public class HtmlParserTest {

    private static final String FILEPATH_PREFIX = "test_files/html/";

    @Test
    public void givenCorrectHtmlFileDoTokensMatch() throws Exception {
        IReader reader = new FileReader(FILEPATH_PREFIX + "correct.txt");
        HtmlParser parser = new HtmlParser(reader);
        Tag root = parser.parseHtml();
        System.out.println(root);
    }
}

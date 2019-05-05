package com.mkobiers.htmlpurger.parser;

import com.mkobiers.htmlpurger.io.FileReader;
import com.mkobiers.htmlpurger.io.IReader;
import com.mkobiers.htmlpurger.model.Tag;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class HtmlParserTest {

    private static final String FILEPATH_PREFIX = "test_files/html/";

    @Test
    public void givenCorrectHtmlFileDoTokensMatch() throws Exception {
        IReader reader = new FileReader(FILEPATH_PREFIX + "correct.txt");
        HtmlParser parser = new HtmlParser(reader);
        Tag root = parser.parseHtml();

        Assertions.assertEquals(2, root.getContent().size());
        Assertions.assertNotNull(root.getOpentag());
        Assertions.assertNotNull(root.getClosetag());
        Assertions.assertNotNull(root.getContent());
        Assertions.assertEquals(2, root.getContent().size());
        Assertions.assertEquals(1, ((Tag) root.getContent().get(0)).getContent().size());
        Assertions.assertEquals(3, ((Tag) root.getContent().get(1)).getContent().size());
    }

    @Test
    public void givenConfigFileWithRandomWhiteCharsDoRulesMatch() throws Exception {
        IReader reader = new FileReader(FILEPATH_PREFIX + "white_chars.txt");
        HtmlParser parser = new HtmlParser(reader);
        Tag root = parser.parseHtml();

        Assertions.assertEquals(2, root.getContent().size());
        Assertions.assertNotNull(root.getOpentag());
        Assertions.assertNotNull(root.getClosetag());
        Assertions.assertNotNull(root.getContent());
        Assertions.assertEquals(2, root.getContent().size());
        Assertions.assertEquals(1, ((Tag) root.getContent().get(0)).getContent().size());
        Assertions.assertEquals(3, ((Tag) root.getContent().get(1)).getContent().size());
    }

}

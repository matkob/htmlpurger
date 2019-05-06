package com.mkobiers.htmlpurger.logic;

import com.mkobiers.htmlpurger.io.FileReader;
import com.mkobiers.htmlpurger.io.IReader;
import com.mkobiers.htmlpurger.model.exception.DuplicationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.BufferedWriter;
import java.io.FileWriter;

public class HtmlPurgerTest {

    private static final String CONFIG_FILEPATH_PREFIX = "test_files/config/";
    private static final String HTML_FILEPATH_PREFIX = "test_files/html/";
    private static final String OUT_FILEPATH_PREFIX = "test_files/writer/";

    @Test
    public void purgeTest() throws Exception {
        IReader htmlReader = new FileReader(HTML_FILEPATH_PREFIX + "correct.txt");
        IReader configReader = new FileReader(CONFIG_FILEPATH_PREFIX + "correct.txt");
        HtmlPurger purger = new HtmlPurger(configReader, htmlReader,
                new BufferedWriter(new FileWriter(OUT_FILEPATH_PREFIX + "one.txt")));
        purger.purgeHtml();
    }

    @Test
    public void givenCorrectFileOutputFileIsCorrect() throws Exception {
        IReader htmlReader = new FileReader(HTML_FILEPATH_PREFIX + "long.txt");
        IReader configReader = new FileReader(CONFIG_FILEPATH_PREFIX + "correct.txt");
        HtmlPurger purger = new HtmlPurger(configReader, htmlReader,
                new BufferedWriter(new FileWriter(OUT_FILEPATH_PREFIX + "two.txt")));
        purger.purgeHtml();
    }

    @Test
    public void givenConfigFileWithDuplicateShouldThrow() throws Exception {
        Assertions.assertThrows(DuplicationException.class, () -> {
            IReader htmlReader = new FileReader(HTML_FILEPATH_PREFIX + "long.txt");
            IReader configReader = new FileReader(CONFIG_FILEPATH_PREFIX + "duplicate.txt");
            HtmlPurger purger = new HtmlPurger(configReader, htmlReader,
                    new BufferedWriter(new FileWriter(OUT_FILEPATH_PREFIX + "three.txt")));
            purger.purgeHtml();
        });
    }

    @Test
    public void givenNotUsedRuleUserIsInformed() throws Exception{
        IReader htmlReader = new FileReader(HTML_FILEPATH_PREFIX + "long.txt");
        IReader configReader = new FileReader(CONFIG_FILEPATH_PREFIX + "not_used.txt");
        HtmlPurger purger = new HtmlPurger(configReader, htmlReader,
                new BufferedWriter(new FileWriter(OUT_FILEPATH_PREFIX + "four.txt")));
        purger.purgeHtml();
    }
}

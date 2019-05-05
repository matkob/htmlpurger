package com.mkobiers.htmlpurger.logic;

import com.mkobiers.htmlpurger.io.FileReader;
import com.mkobiers.htmlpurger.io.IReader;
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
        HtmlPurger purger = new HtmlPurger(configReader, htmlReader, new BufferedWriter(new FileWriter(OUT_FILEPATH_PREFIX + "out.txt")));
        purger.process();
    }
}

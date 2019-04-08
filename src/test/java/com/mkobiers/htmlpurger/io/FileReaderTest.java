package com.mkobiers.htmlpurger.io;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class FileReaderTest {

    @Test
    void givenKnownFileAreCharsRead() {
        IReader reader = new FileReader("test.txt");

        StringBuilder builder = new StringBuilder();
        char c;
        while ((c = reader.nextChar()) != 0) {
            builder.append(c);
        }
        Assertions.assertEquals("abc abrakadabra", builder.toString());
    }

    @Test
    void givenReadCharIsItSavedInClass() {
        IReader reader = new FileReader("test.txt");
        char c = reader.nextChar();
        Assertions.assertEquals(c, reader.getChar());
        Assertions.assertEquals('b', reader.nextChar());
    }

    @Test
    void rewindTest() {
        IReader reader = new FileReader("lexertestconfig.txt");
        char first = reader.nextChar();
        reader.rewind();
        char second = reader.nextChar();
        Assertions.assertEquals(first, second);
    }
}

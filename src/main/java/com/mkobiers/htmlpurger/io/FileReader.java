package com.mkobiers.htmlpurger.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.Charset;

public class FileReader implements IReader {

    private Logger logger = LoggerFactory.getLogger(FileReader.class);
    private String filename;
    private Reader reader;
    private char current;

    public FileReader(String filename) {
        this.filename = filename;
        try {
            Charset encoding = Charset.defaultCharset();
            InputStream in = new FileInputStream(new File(filename));
            Reader inputReader = new InputStreamReader(in, encoding);
            reader = new BufferedReader(inputReader);
        } catch (FileNotFoundException e) {
            logger.error("File " + filename + "not found");
        }

    }

    @Override
    public char nextChar() {
        try {
            int r = reader.read();
            if (r != -1) {
                char ch = (char) r;
                current = ch;
                return ch;
            } else {
                return 0;
            }
        } catch (IOException e) {
            logger.error("IOException thrown during char reading");
            return 0;
        }
    }

    @Override
    public char getChar() {
        return current;
    }
}

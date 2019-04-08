package com.mkobiers.htmlpurger.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.Charset;

public class FileReader implements IReader {

    private Logger logger = LoggerFactory.getLogger(FileReader.class);
    private Reader reader;
    private char current;
    private String lastTen;

    private int column = 0;
    private int row = 1;

    public FileReader(String filename) {
        this.lastTen = "  ";
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
                if (ch == '\n') {
                    row++;
                    column = 1;
                    return nextChar();
                } else if (ch == '\r') {
                    column = 1;
                    return nextChar();
                } else {
                    column++;
                    lastTen = lastTen.length() < 10 ? lastTen + ch : (lastTen + ch).substring(1);
                }
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

    @Override
    public String getErrorMessage() {
        try {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < 9; i++) {
                int c = reader.read();
                if (c == -1) {
                    break;
                }
                if (c != '\n' && c != '\r' && c != '\t') {
                    builder.append((char) c);
                } else {
                    i--;
                }
            }
            return lastTen + builder.toString();
        } catch (IOException e) {
            logger.error("IOException thrown during char reading");
            return lastTen;
        }
    }

    @Override
    public int getColumn() {
        return column;
    }

    @Override
    public int getRow() {
        return row;
    }
}

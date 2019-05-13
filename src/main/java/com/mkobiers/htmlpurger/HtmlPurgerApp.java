package com.mkobiers.htmlpurger;

import com.mkobiers.htmlpurger.io.FileReader;
import com.mkobiers.htmlpurger.io.IReader;
import com.mkobiers.htmlpurger.logic.HtmlPurger;
import com.mkobiers.htmlpurger.model.exception.DuplicationException;
import com.mkobiers.htmlpurger.model.exception.GrammarException;
import com.mkobiers.htmlpurger.model.exception.ParsingException;
import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class HtmlPurgerApp {
    private static Logger logger = LoggerFactory.getLogger(HtmlPurgerApp.class);

    public static void main(String[] args) {
        Options options = new Options();
        options.addOption("p", "page", true, "source of html file");
        options.addOption("c", "conf", true, "source of config file");
        options.addOption("h", "help", false, "displays this message");

        IReader htmlReader = null;
        IReader configReader = null;

        try {
            CommandLineParser parser = new DefaultParser();
            CommandLine cmd = parser.parse(options, args);

            if (cmd.hasOption("h")){
                String header = "HTMLpurger lets you clean your html\n\n";
                String footer = "\nmkobiers solutions inc 2019";
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp("htmlpurger", header, options, footer, true);
                return;
            }

            if (cmd.hasOption("p") && cmd.hasOption("c")) {
                String htmlPath = cmd.getOptionValue("p");
                String configPath = cmd.getOptionValue("c");
                logger.info("using html file " + htmlPath);
                htmlReader = new FileReader(htmlPath);
                logger.info("using config file " + configPath);
                configReader = new FileReader(configPath);
            } else {
                logger.error("no specified filepaths");
                logger.warn("specify correct html and config filepaths!");
                System.exit(1);
            }

            HtmlPurger purger = new HtmlPurger(configReader, htmlReader,
                    new BufferedWriter(new FileWriter("out.html")));
            purger.purgeHtml();
        } catch (ParseException e) {
            logger.error("wrong arguments");
        } catch (IOException e) {
            logger.error("error creating output file");
        } catch (ParsingException e) {
            logger.error(e.getMessage());
        } catch (GrammarException e) {
            logger.error(e.getMessage());
        } catch (DuplicationException e) {
            logger.error(e.getMessage());
        }
    }
}

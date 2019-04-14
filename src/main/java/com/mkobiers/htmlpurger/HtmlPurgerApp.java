package com.mkobiers.htmlpurger;

import com.mkobiers.htmlpurger.logic.HtmlFilter;
import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HtmlPurgerApp {
    private static Logger logger = LoggerFactory.getLogger(HtmlPurgerApp.class);

    public static void main(String[] args) {
        Options options = new Options();
        options.addOption("p", "page", true, "source of html file");
        options.addOption("c", "conf", true, "source of config file");
        options.addOption("h", "help", false, "displays this message");

        HtmlFilter filter = new HtmlFilter();

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
                logger.info("Using html file " + htmlPath);
                logger.info("Using config file " + configPath);

            } else {
                logger.error("No specified filepaths");
                System.out.println("Specify correct html and config filepaths!");
            }
        } catch (ParseException e) {
            logger.error("Exception thrown during argument parsing");

        }
    }
}

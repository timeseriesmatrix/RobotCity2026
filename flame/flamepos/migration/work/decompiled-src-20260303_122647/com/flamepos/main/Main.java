/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.cli.BasicParser
 *  org.apache.commons.cli.CommandLine
 *  org.apache.commons.cli.Options
 */
package com.floreantpos.main;

import com.floreantpos.config.TerminalConfig;
import com.floreantpos.main.Application;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Locale;
import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;

public class Main {
    private static final String DEVELOPMENT_MODE = "developmentMode";

    public static void main(String[] args) throws Exception {
        Options options = new Options();
        options.addOption(DEVELOPMENT_MODE, true, "State if this is developmentMode");
        BasicParser parser = new BasicParser();
        CommandLine commandLine = parser.parse(options, args);
        String optionValue = commandLine.getOptionValue(DEVELOPMENT_MODE);
        Locale defaultLocale = TerminalConfig.getDefaultLocale();
        if (defaultLocale != null) {
            Locale.setDefault(defaultLocale);
        }
        Application application = Application.getInstance();
        if (optionValue != null) {
            application.setDevelopmentMode(Boolean.valueOf(optionValue));
        }
        application.start();
    }

    public static void restart() throws IOException, InterruptedException, URISyntaxException {
        String javaBin = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
        String classPath = System.getProperty("java.class.path");
        String mainClass = System.getProperty("sun.java.command");
        ArrayList<String> command = new ArrayList<String>();
        command.add(javaBin);
        command.add("-cp");
        command.add(classPath);
        command.add(mainClass);
        ProcessBuilder builder = new ProcessBuilder(command);
        builder.start();
        System.exit(0);
    }
}

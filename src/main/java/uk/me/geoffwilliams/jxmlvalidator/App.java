/*
 * JXMLValidator -- command line XML tool to validate files to schema
 * Copyright (C) 2013  Geoff Williams<geoff@geoffwilliams.me.uk>
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package uk.me.geoffwilliams.jxmlvalidator;

import java.util.List;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

/**
 * Main entry point for program, parsing command line arguments, etc
 *
 */
public class App {
    public static final int STATUS_UNKNOWN = -1;
    public static final int STATUS_OK = 0;
    public static final int STATUS_WARNINGS = 1;
    public static final int STATUS_ERRORS = 2;
    public static final int STATUS_FATAL = 3;
    public static final int STATUS_GUI_MODE = 200;
    public static final int STATUS_EXCEPTION = 255;
    
    private static Logger logger = LoggerFactory.getLogger(App.class);
    private static final String CMD_OPTION_QUIET = "quiet";
    private static final String CMD_OPTION_VERBOSE = "verbose";
    private static final String CMD_OPTION_GUI = "gui";

    
    public Options createOptions() {
        // create Options object
        Options options = new Options();

        // -quiet
        options.addOption(CMD_OPTION_QUIET, false, "only display error messages");

        // -verbose
        options.addOption(CMD_OPTION_VERBOSE, false, "enable debug output");
        
        // -gui
        options.addOption(CMD_OPTION_GUI, false, "enable GUI mode");
   
        return options;
    }
    
    public void logLevel(Level level) {
        LogManager.getRootLogger().setLevel(level);
    }
    
    private int run(String[] args) {
        int status;
        Options options = createOptions();
        try {
            
            CommandLineParser parser = new GnuParser();
            CommandLine cmd = parser.parse( options, args);
            
            // quiet mode...
            if (cmd.hasOption(CMD_OPTION_QUIET)) {
                logLevel(Level.ERROR);
            }
            
            // debug mode
            if (cmd.hasOption(CMD_OPTION_VERBOSE)) {
                logLevel(Level.DEBUG);
            }
            
            // all left over arguments...
            List<String> parameters = cmd.getArgList();

            String uri = null;
            if (parameters.size() == 1) { 
                uri = parameters.get(0);
            }

            
            if (cmd.hasOption(CMD_OPTION_GUI)) {
                // exit status not relevant for GUI operation
                status = STATUS_GUI_MODE;
                guiMode(uri);
            } else if (parameters.size() == 1) { 
                status = commandMode(uri);
            } else {
                // usage error
                status = STATUS_EXCEPTION;
                logger.error("You must specify a file or URL if not using the GUI");
                usage(options);
            }
        } catch (ParseException ex) {
            status = STATUS_EXCEPTION;
            logger.error(ex.getMessage());
            // incorrect invocation -- show help
            usage(options);
        }
        return status;
    }
    
    /**
     * Work in command line mode
     * @param uri File or URL to process
     * @return 
     */
    private int commandMode(String uri) {
        Validator validator = new Validator();
        return validator.validate(uri);
        
    }
    
    private void createAndShowGUI(String uri) {
        logger.debug("Creating sing components...");
        
        ValidatorUI panel = new ValidatorUI(); 
        panel.setInputUri(uri);
        JFrame frame = new JFrame("JXmlValidator GUI");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(panel);
        frame.pack();
        frame.setVisible(true);
    }
    
    private void guiMode(final String uri) {
        logger.debug("starting GUI...");

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI(uri);
            }
        });
    }
    
    private void usage(Options options) {
        // find the name of the jar file containg App.java (this file) 
        String jarFileName = 
                new java.io.File(
                    App.class.getProtectionDomain()
                        .getCodeSource()
                            .getLocation()
                                .getPath()
                ).getName();
        
        HelpFormatter helpFormatter = new HelpFormatter();
        String usageMessage = 
                "java -jar " + jarFileName + " [options] (FILE|URL)\n" +
                "java -jar " + jarFileName + " -gui\n";
        helpFormatter.printHelp(usageMessage, options);

    }
    
    public static void main( String[] args ) {
        App app = new App();
        int status = app.run(args);
        
        // do not to exit immediately in gui mode
        if (status != STATUS_GUI_MODE) {
            logger.debug("Exiting system with status code: {}", status);
            System.exit(status);
        }
    }
}

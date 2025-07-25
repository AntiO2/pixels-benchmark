package io.pixelsdb.benchmark;
/**
 * @time 2023-03-04
 * @version 1.0.0
 * @file CommandProcessor.java
 * @description define options in command line.
 **/

import org.apache.commons.cli.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;

public class CommandProcessor {
    Logger logger = LogManager.getLogger(CommandProcessor.class);
    Options cmdOptions = null;
    String[] cmdLines = null;

    public CommandProcessor(String[] args) {
        this.cmdLines = args;
    }

    // option builder. add new option if needed.
    public void optionBuilder(Options options) {
        options.addOption("h", "help", false, "Print PixelsBench usage information");

        Option testType = Option.builder("t")
                .longOpt("testType")
                .desc("This is for test type. Now we support three types, execSql, gendata and runX.")
                .hasArg()
                .argName("type")
                .build();
        options.addOption(testType);

        Option confFile = Option.builder("c")
                .longOpt("conffile")
                .desc("The path to configuration")
                .hasArg()
                .argName("conf")
                .build();
        options.addOption(confFile);

        Option sqlFile = Option.builder("f")
                .longOpt("sqlfile")
                .desc("The path to sql files")
                .hasArg()
                .argName("file")
                .build();
        options.addOption(sqlFile);

        Option verbose = Option.builder("s")
                .longOpt("silent")
                .desc("Don't print detail test response time histogram")
                .build();
        options.addOption(verbose);

        Option freshness = Option.builder("r")
                .longOpt("freshness")
                .desc("Enable freshness measurement")
                .build();
        options.addOption(freshness);
    }

    // Parse command Line
    public HashMap<String, String> commandPaser(String[] cmdLine) {
        HashMap<String, String> argsList = new HashMap<String, String>();
        CommandLineParser parser = new DefaultParser();
        cmdOptions = new Options();
        optionBuilder(cmdOptions);
        CommandLine argsLine = null;
        try {
            argsLine = parser.parse(cmdOptions, cmdLine);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        processArgs(argsList, argsLine);
        return argsList;
    }

    // Read command line.
    public void processArgs(HashMap<String, String> argsList, CommandLine cmdLine) {
        if (cmdLine.hasOption('h')) {
            printHelp();
            System.exit(0);
        }

        if (cmdLine.hasOption("testType")) {
            argsList.put("t", cmdLine.getOptionValue("t"));
        }

        if (cmdLine.hasOption("c")) {
            argsList.put("c", cmdLine.getOptionValue("c"));
        }

        if (cmdLine.hasOption("f")) {
            argsList.put("f", cmdLine.getOptionValue("f"));
        }

        if (cmdLine.hasOption("s")) {
            argsList.put("s", "true");
        }

        if (cmdLine.hasOption("r")) {
            argsList.put("r", "true");
        }
    }

    // help info
    public void printHelp() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("pixels_bench " + " [options]", cmdOptions);
        System.out.println("Example:");
        System.out.println("Step 1: run sql files for init or cleanup");
        System.out.println("  pixels_bench -t sql -f sql/sqlfile.sql -c conf/db.properties");
        System.out.println("Step 2: generate data and load");
        System.out.println("  pixels_bench -t gendata -c conf/db.properties -f sql/sql_file.sql");
        System.out.println("Step 3: run TP workload");
        System.out.println("  pixels_bench -t runtp -c conf/db.properties -f sql/sql_file.sql");
        System.out.println("Step 4: run AP workload");
        System.out.println("  pixels_bench -t runap -c conf/db.properties -f sql/sql_file.sql");
        System.out.println("Step 5: run XP workload");
        System.out.println("  pixels_bench -t runxp -c conf/db.properties -f sql/sql_file.sql");
        System.out.println("Step 6: run fresh workload");
        System.out.println("  pixels_bench -t runfresh -c conf/db.properties -f sql/sql_file.sql");
        System.out.println("Step 7: run htap workload");
        System.out.println("  pixels_bench -t runhtap -c conf/db.properties -f sql/sql_file.sql");
        System.out.println("Step 8: run all workload");
        System.out.println("  pixels_bench -t runall -c conf/db.properties -f sql/sql_file.sql");
    }
}

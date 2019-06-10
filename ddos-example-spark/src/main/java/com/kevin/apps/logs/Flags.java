package com.kevin.apps.logs;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.spark.streaming.Duration;

import com.kevin.kafka.main.LogFileConsumer;
// this class is used for input JVM arguments
public class Flags {
  private static final Flags THE_INSTANCE = new Flags();

  private boolean isHelp;
  private Duration windowLength;
  private Duration slideInterval;
  private String logsDirectory;
  private String outputHtmlFile;
  private String checkpointDirectory;

  private boolean initialized = false;

  private Flags() {}

  public boolean isHelp() { return isHelp; }

  public Duration getWindowLength() {
    return windowLength;
  }

  public Duration getSlideInterval() {
    return slideInterval;
  }

  public String getLogsDirectory() {
    return logsDirectory;
  }

  public String getOutputHtmlFile() {
    return outputHtmlFile;
  }

  public String getCheckpointDirectory() {
    return checkpointDirectory;
  }

  public static Flags getInstance() {
    if (!THE_INSTANCE.initialized) {
      throw new RuntimeException("Flags have not been initialized");
    }
    return THE_INSTANCE;
  }

  public static void setFromCommandLineArgs(Options options, String[] args) {
    CommandLineParser parser = new PosixParser();
    try {
      CommandLine cl = parser.parse(options, args);
      THE_INSTANCE.isHelp = cl.hasOption(LogFileConsumer.HELP);
      THE_INSTANCE.windowLength = new Duration(Integer.parseInt(
          cl.getOptionValue(LogFileConsumer.WINDOW_LENGTH, "30")) * 1000);
      THE_INSTANCE.slideInterval = new Duration(Integer.parseInt(
          cl.getOptionValue(LogFileConsumer.SLIDE_INTERVAL, "5")) * 1000);
      THE_INSTANCE.logsDirectory = cl.getOptionValue(
          LogFileConsumer.LOGS_DIRECTORY, "/Users/kevinmuga/Documents/workspace/FileStorage/Input");
      THE_INSTANCE.outputHtmlFile = cl.getOptionValue(
    		  LogFileConsumer.OUTPUT_HTML_FILE, "/Users/kevinmuga/Documents/workspace/ddos-example-spark/Output/log_stats.html");
      THE_INSTANCE.checkpointDirectory = cl.getOptionValue(
    		  LogFileConsumer.CHECKPOINT_DIRECTORY, "hdfs://localhost:8020/RddCheckPoint1");

      THE_INSTANCE.initialized = true;
    } catch (ParseException e) {
      THE_INSTANCE.initialized = false;
      System.err.println("Parsing failed.  Reason: " + e.getMessage());
    }
  }
}


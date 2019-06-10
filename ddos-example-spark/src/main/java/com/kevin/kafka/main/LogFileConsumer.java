package com.kevin.kafka.main;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka.KafkaUtils;

import com.kevin.apps.logs.ApacheAccessLog;
import com.kevin.apps.logs.Flags;
import com.kevin.apps.logs.LogAnalyzerTotal;
import com.kevin.apps.logs.LogAnalyzerWindowed;
import com.kevin.apps.logs.Renderer;

import kafka.serializer.StringDecoder;
import scala.Tuple2;

public class LogFileConsumer {
	
	public static final String WINDOW_LENGTH = "w";
	 public static final String SLIDE_INTERVAL = "s";
	 public static final String LOGS_DIRECTORY = "l";
	 public static final String OUTPUT_HTML_FILE = "o";
	 public static final String CHECKPOINT_DIRECTORY = "c";
	 public static final String HELP = "h";

	  private static final Options THE_OPTIONS = createOptions();
	
	private static Options createOptions() {
	    Options options = new Options();
	    options.addOption(LOGS_DIRECTORY, "logs-directory", true, "Directory with input log files");
	    options.addOption(OUTPUT_HTML_FILE, "output-html-file", true, "Output HTML file to write statistics");
	    options.addOption(WINDOW_LENGTH, "window-length", true, "Length of the aggregate window in seconds");
	    options.addOption(SLIDE_INTERVAL, "slide-interval", true, "Slide interval in seconds");
	    options.addOption(CHECKPOINT_DIRECTORY, "checkpoint-directory", true, "Directory for Spark checkpoints");
	    options.addOption(HELP, "help", false, "Print help");
	    return options;
	  }
		    
	private static void helpAndExit(int status) {
	    HelpFormatter helpFormatter = new HelpFormatter();
	    helpFormatter.printHelp("Program [options]", THE_OPTIONS);
	    System.exit(status);
	  }	
/*
 *  main method to start the consumer program.	
 */
	public static void main(String[] args) throws IOException {
		
		if (args.length == 0) {
		      helpAndExit(1);
		    }
		    Flags.setFromCommandLineArgs(THE_OPTIONS, args);
		    if (Flags.getInstance().isHelp()) {
		      helpAndExit(0);
		    }
// set the spark configuration
        SparkConf conf = new SparkConf()
                .setAppName("kafka-sandbox")
                .setMaster("local[*]");

        JavaSparkContext javaSparkContext = new JavaSparkContext(conf);

        JavaStreamingContext javaStreamingContext = new JavaStreamingContext(javaSparkContext,
                Flags.getInstance().getSlideInterval());
     // Checkpointing must be enabled to use the updateStateByKey function.
        javaStreamingContext.checkpoint(Flags.getInstance().getCheckpointDirectory());

        
        Set<String> topics = Collections.singleton("demoSpark");
        Map<String, String> kafkaParams = new HashMap<>();
        kafkaParams.put("metadata.broker.list", "localhost:9092");
		
        JavaPairInputDStream<String, String> directKafkaStream = KafkaUtils.createDirectStream(javaStreamingContext,
                String.class, String.class, StringDecoder.class, StringDecoder.class, kafkaParams, topics);
        
        JavaDStream<ApacheAccessLog> accessLogsDStream = directKafkaStream.map(new Function<Tuple2<String, String>, ApacheAccessLog>() {
            @Override
            public ApacheAccessLog call(Tuple2<String, String> tuple2) throws IOException {
              return ApacheAccessLog.parseFromLogLine(tuple2._2);
            }
          });
        
        LogAnalyzerTotal logAnalyzerTotal = new LogAnalyzerTotal();
        LogAnalyzerWindowed logAnalyzerWindowed = new LogAnalyzerWindowed();

        // Process the DStream which gathers stats for all of time.
        logAnalyzerTotal.processAccessLogs(accessLogsDStream);
        
        logAnalyzerWindowed.processAccessLogs(accessLogsDStream);

//         Render the output each time there is a new RDD in the accessLogsDStream.
        Renderer renderer = new Renderer();

        // Call this to output the stats and create the output file
		accessLogsDStream.foreachRDD(rdd -> 
		{ renderer.render(logAnalyzerTotal.getLogStatistics(), logAnalyzerWindowed.getLogStatistics());
		        });
            
        javaStreamingContext.start();
        try {
			javaStreamingContext.awaitTermination();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }


}

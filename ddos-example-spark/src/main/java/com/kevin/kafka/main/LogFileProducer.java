package com.kevin.kafka.main;
//
//import java.util.concurrent.ExecutionException;
//
//import org.apache.kafka.clients.consumer.Consumer;
//import org.apache.kafka.clients.consumer.ConsumerRecords;
//import org.apache.kafka.clients.producer.Producer;
//import org.apache.kafka.clients.producer.ProducerRecord;
//import org.apache.kafka.clients.producer.RecordMetadata;
//
//import com.kevinm.kafka.constants.IKafkaConstants;
//import com.kevinm.kafka.consumer.ConsumerCreator;
//import com.kevinm.kafka.producer.ProducerCreator;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import com.kevin.apps.logs.ApacheAccessLog;
import com.kevin.kafka.constants.IKafkaConstants;
import com.kevin.kafka.producer.ProducerCreator;




public class LogFileProducer {
	public static void main(String[] args) {
		runProducer();
	}

	static void runProducer() {

		try {
			// Get String Array from file reading
			List<ApacheAccessLog> fileArrayList = readLogFile();
			Producer<String, String> producer = ProducerCreator.createProducer();

			if (fileArrayList != null && !fileArrayList.isEmpty()) {
				
				for (int index = 0; index < fileArrayList.size(); index++) {
					final ProducerRecord<String, String> record = new ProducerRecord<String, String>(
							IKafkaConstants.TOPIC_NAME, fileArrayList.get(index).getIpAddress(), fileArrayList.get(index).getLine());
					//System.out.println("Record:"+record.value());
					
					  try { RecordMetadata metadata = producer.send(record).get();
					  System.out.println("Record sent with key " +  fileArrayList.get(index).getIpAddress() + " to partition " +
					  metadata.partition() + " with offset " + metadata.offset()); } catch
					  (ExecutionException e) { System.out.println("Error in sending record");
					  //System.out.println(e); } catch (InterruptedException e) {
					  //System.out.println("Error in sending record"); System.out.println(e); 
					  }
					 
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}



	private static List<ApacheAccessLog> readLogFile() throws IOException {
		// File file = new
		String fileName = "/Users/kevinmuga/Documents/workspace/FileStorage/Input/apache-access-log-mini.txt";
		Scanner s = new Scanner(new FileReader(fileName));
		ArrayList<ApacheAccessLog> result = new ArrayList<>();

		while (s.hasNext()) {
			String line = s.nextLine();
			//System.out.println("Line:" + line);
			ApacheAccessLog apacheAccessLog = ApacheAccessLog.parseFromLogLine(line);
			apacheAccessLog.setLine(line);
			result.add(apacheAccessLog);
		}
		return result;

	}

}
package com.dcclab.iot.prejob.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeoutException;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import com.dcclab.iot.common.util.PropertyFileReader;
import com.dcclab.iot.prejob.service.PreJobService;
import com.dcclab.iot.prejob.vo.PreJobVO;
import com.google.gson.JsonParser;
import com.ibm.iotf.client.app.ApplicationClient;
import com.ibm.iotf.client.app.Command;
import com.ibm.iotf.client.app.Event;
import com.ibm.iotf.client.app.EventCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

@Controller
public class PreJobController {
	Logger log = Logger.getLogger(this.getClass());
	Date d = new Date();
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

	@Autowired
	private PreJobService preJobService;
	

	@RequestMapping(value = "/")
	public String test() throws Exception {
		return "home";
	}

	@RequestMapping(value = "/dvciot/senseData")
	public String getCpuInfo() throws Exception {
		ApplicationClient myClient = null;
		Properties clientOpt = new Properties();

		/* Bluemix IOT Client-Side Authentification */
		clientOpt = PropertyFileReader.readPropertyFile("mqtt.properties");
		clientOpt.put("org", clientOpt.getProperty("com.mqtt.client.org"));
		clientOpt.put("id", clientOpt.getProperty("com.mqtt.client.id"));
		clientOpt.put("Authentication-Method", clientOpt.getProperty("com.mqtt.client.authenmethod"));
		clientOpt.put("Authentication-Token", clientOpt.getProperty("com.mqtt.client.authentoken"));
		clientOpt.put("API-Key", clientOpt.getProperty("com.mqtt.client.apikey"));

		try {
			myClient = new ApplicationClient(clientOpt);
		} catch (Exception e) {
			e.printStackTrace();
		}

		myClient.connect();

		DeviceEventHandler handler = new DeviceEventHandler();
		handler.setClient(myClient);
		myClient.setEventCallback(handler);
		myClient.subscribeToDeviceEvents();
		// Run the event processing thread
		Thread thread = new Thread(handler);
		thread.start();

		return "home";
	}

	private class DeviceEventHandler implements EventCallback, Runnable {
		JsonParser parser = new JsonParser();
		PreJobVO preJobVO = new PreJobVO();

		private ApplicationClient client;
		private BlockingQueue<Event> evtQueue = new LinkedBlockingQueue<Event>();

		public void processEvent(Event e) {
			try {
				evtQueue.put(e);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		}

		@Override
		public void processCommand(Command cmd) {
			System.out.println("Command received:: " + cmd);
		}

		@Override
		public void run() {
			while (true) {
				Event e = null;
				try {
					e = evtQueue.take();
					// Check count value
					
					String jsonTemp = e.getPayload().substring(5);
					String jsonCpuInfo = jsonTemp.substring(0, jsonTemp.length() - 1);
				
					String cpuInfoKafkaFormat = convertJSONCpuInfo(jsonCpuInfo);
				
					sendRabbitQ(cpuInfoKafkaFormat);				
					
					/* IOT Data(Related Databases) */
					// try {
					// preJobService.putCpuInfo(preJobVO);
					// } catch (Exception e1) {
					// // TODO Auto-generated catch block
					// e1.printStackTrace();
					// }

					/*
					 * (1) Sense Data from raszberrypi using MQTT protocol 
					 * (2) Load IOT Realtime Data to kafka(Distributed Cluster - Using Apache Kafaka)
					 */

//					try {						
//						preJobService.loadRealdataToKafka(cpuInfoKafkaFormat);						
//					} catch (Exception e1) {
//						e1.printStackTrace();
//					}

				} catch (InterruptedException | IOException | TimeoutException e1) {
					continue;
				} catch (KeyManagementException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (NoSuchAlgorithmException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (URISyntaxException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}

		public ApplicationClient getClient() {
			return client;
		}

		public void setClient(ApplicationClient client) {
			this.client = client;
		}
	}

	private static String convertJSONCpuInfo(String cpuInfoJson) {
		JSONParser jsonParser = new JSONParser();
		JSONObject cpuInfoObject = null;
		try {
			cpuInfoObject = (JSONObject) jsonParser.parse(cpuInfoJson);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String cpuInfoKafkaFormat = cpuInfoObject.get("myName") + "," + cpuInfoObject.get("cputemp") + ","
				+ cpuInfoObject.get("cpuload") + "," + cpuInfoObject.get("sine");
		return cpuInfoKafkaFormat;
	}
	
	private static void sendRabbitQ(String cpuInfoKafkaFormat) throws IOException, TimeoutException, KeyManagementException, NoSuchAlgorithmException, URISyntaxException {
		
		ConnectionFactory cf = new ConnectionFactory();

		cf.setHost("HOSTNAME");
		cf.setVirtualHost("VIRTUAL HOSTNAME");
		cf.setUsername("USERNAME");
		cf.setPassword("PASSWORD");
		cf.setPort(5672);
		cf.setConnectionTimeout(30000);		
		
		Connection connection = cf.newConnection();
		Channel channelQueue = connection.createChannel();
				
		channelQueue.basicPublish("EXCHANGE", "ROUTING_KEY", null, cpuInfoKafkaFormat.getBytes());
				
		channelQueue.close();
		connection.close();		
	}
}

package com.dcclab.iot.prejob.service.impl;

import java.util.Properties;
import javax.annotation.Resource;
//import org.apache.kafka.clients.producer.KafkaProducer;
//import org.apache.kafka.clients.producer.ProducerConfig;
//import org.apache.kafka.clients.producer.ProducerRecord;
//import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.log4j.Logger;
import org.bson.Document;
import org.springframework.stereotype.Service;
import com.dcclab.iot.prejob.dao.PreJobDao;
import com.dcclab.iot.prejob.service.PreJobService;
import com.dcclab.iot.prejob.vo.PreJobVO;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoException;
import com.mongodb.client.MongoDatabase;

@Service("preJobService")
public class PreJobServiceImpl implements PreJobService {
	Logger log = Logger.getLogger(PreJobServiceImpl.class);

	@Resource(name = "preJobDao")
	private PreJobDao preJobDao;

	Properties prop = new Properties();

	public void loadCpuInfo(PreJobVO preJobVO) {
		preJobDao.insertCpuInfo(preJobVO);
	}

	public void loadRealdataToKafka(String realData) throws Exception {

//		prop.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "163.239.22.53:9091");
//		prop.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
//		prop.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
//				"org.apache.kafka.common.serialization.StringSerializer");
//		
//
//		KafkaProducer<String, String> producer = new KafkaProducer<>(prop);
//		System.out.println(producer);
//		try {
//			ProducerRecord<String, String> sendData = new ProducerRecord<>("dcclab1", realData);
//			System.out.println(sendData);
//			
//			//@SuppressWarnings("unused")
//			producer.send(sendData);
//			
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			producer.flush();
//			producer.close();
//		}
	}

	public void storeRealdata(String realData) throws Exception {
		try {
			Document documentRealdata = convertDbObject(realData);
			storeRealdataToMongodb(documentRealdata);
		} catch (MongoException me) {
			log.error("storeRealdata - MongoException : " + me.getMessage());
		}
	}

	public void transDataToSpkStreaming(String realData) throws Exception {

	}

	public static void storeRealdataToMongodb(Document document) {
		MongoClientURI uri = new MongoClientURI("mongodb://akssus12:wjddusdn12@ds243728.mlab.com:43728/dcclab");
		MongoClient mongoClient = new MongoClient(uri);
		MongoDatabase mongoDb = mongoClient.getDatabase("dcclab");
		mongoDb.getCollection("tb_cpuinfo").insertOne(document);
	}

	private static Document convertDbObject(String kafkaData) {
		Document document = Document.parse(kafkaData);
		return document;
	}
}
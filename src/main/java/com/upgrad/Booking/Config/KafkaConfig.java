package com.upgrad.Booking.Config;

import org.apache.kafka.clients.producer.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Component
public class KafkaConfig {

    /*
    Reading EC2 Public DNS from application.properties file
     */
    @Value("${url.kafka.server}")
    private String instancePublicDNS;

    public void publish(String topic, String key, String value) {

        /*
        Defining the properties for kafka producer
         */
        Properties properties = new Properties();
        properties.put("bootstrap.servers", instancePublicDNS);
        properties.put("acks", "all");
        properties.put("retries", 0);
        properties.put("linger.ms", 0);
        properties.put("partitioner.class", "org.apache.kafka.clients.producer.internals.DefaultPartitioner");
        properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.put("request.timeout.ms", 30000);
        properties.put("timeout.ms", 30000);
        properties.put("max.in.flight.requests.per.connection", 5);
        properties.put("retry.backoff.ms", 5);

        /*
        Instantiate the producer Object to post the message to the kafka topic
         */
        Producer<String, String> producer = new KafkaProducer<>(properties);
        producer.send(new ProducerRecord<>(topic, key, value));
        producer.close();
    }
}
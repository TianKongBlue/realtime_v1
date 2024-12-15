package com.bw.realtime.util;

import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;
import org.apache.flink.streaming.connectors.kafka.KafkaSerializationSchema;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class KafkaUtils {
    // 从kafka中读取数据
    public static FlinkKafkaConsumer<String> kafkaSource(String topic){
        Properties properties = new Properties();
        properties.setProperty("bootstrap.servers", "hadoop101:9092");
        properties.setProperty("group.id", "test");
        properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,"earliest");

        return new FlinkKafkaConsumer<>(topic, new SimpleStringSchema(), properties);
    }

    // 发送数据到kafka
    public static FlinkKafkaProducer<String> kafkaSink(String topic){

        Properties properties = new Properties();
        properties.setProperty("bootstrap.servers", "hadoop101:9092");

        KafkaSerializationSchema<String> serializationSchema = new KafkaSerializationSchema<String>() {
            @Override
            public ProducerRecord<byte[], byte[]> serialize(String element, Long timestamp) {
                return new ProducerRecord<>(
                        topic, // target topic
                        element.getBytes(StandardCharsets.UTF_8)); // record contents
            }
        };

        FlinkKafkaProducer<String> myProducer = new FlinkKafkaProducer<>(
                topic,             // target topic
                serializationSchema,    // serialization schema
                properties,             // producer config
                FlinkKafkaProducer.Semantic.EXACTLY_ONCE); // fault-tolerance

        return myProducer;
    }

}

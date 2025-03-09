package org.example.source;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.example.config.KafkaConfig;
import org.example.config.SourceConfig;
import org.example.pipeline.DataPipeline;
import org.example.utils.DateTimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class KafkaSource extends ETLDataSource {
    private static final Logger logger = LoggerFactory.getLogger(KafkaSource.class);
    private static final String END_OF_STREAM_MARKER = "__END_OF_STREAM__";
    private final KafkaConfig kafkaConfig;
    private final KafkaConsumer<String, String> consumer;
    private volatile boolean running = true;
    private String[] headers = null; // Store headers dynamically from the first record

    public KafkaSource(SourceConfig sourceConfig) throws Exception {
        super(sourceConfig);
        this.kafkaConfig = sourceConfig.getKafkaConfig();
        if (kafkaConfig == null) {
            throw new IllegalArgumentException("Kafka configuration is required for KafkaSource");
        }
        // Initialize Kafka consumer properties
        Properties props = new Properties();
        props.put("bootstrap.servers", kafkaConfig.getBootstrapServers());
        props.put("group.id", kafkaConfig.getGroupId());
        props.put("auto.offset.reset", kafkaConfig.getAutoOffsetReset());
        props.put("enable.auto.commit", kafkaConfig.isEnableAutoCommit());
        props.put("session.timeout.ms", kafkaConfig.getSessionTimeoutMs());
        props.put("heartbeat.interval.ms", kafkaConfig.getHeartbeatIntervalMs());
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

        // Initialize Kafka consumer
        this.consumer = new KafkaConsumer<>(props);
    }

    @Override
    public InputStream getInputStream() {
        return null;
    }

    @Override
    public void submitTo(DataPipeline pipeline) {
        consumer.subscribe(Collections.singletonList(kafkaConfig.getTopic()));

        long start = System.nanoTime();
        try {
            while (running) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
                for (ConsumerRecord<String, String> record : records) {
                    String value = record.value();
                    if (END_OF_STREAM_MARKER.equals(value)) {
                        logger.info("Received end-of-stream marker. Flushing pipeline and stopping...");
//                        pipeline.flush(); // Flush any remaining records
//                        running = false; // Stop the consumer loop
//                        break;
                    } else {
                        // Parse CSV record dynamically using headers
                        Map<String, Object> data = parseCsvRecordDynamic(value);
                        // Submit to the pipeline
                        pipeline.process(data);
                    }
                }
            }
        } catch (WakeupException e) {
            logger.error("Kafka consumer interrupted: {}", e.getMessage());
        } finally {
            if (running) {
                pipeline.flush(); // Ensure any remaining records are flushed
            }
            consumer.close();
            logger.info("Finished consuming from Kafka in {}",
                    DateTimeUtils.getReadableDuration(start, System.nanoTime()));
        }
    }

    /**
     * Dynamically parse a CSV record into a Map<String, Object> using headers
     * Assumes the first record contains headers, followed by data rows
     */
    private Map<String, Object> parseCsvRecordDynamic(String csvRecord) {
        Map<String, Object> data = new HashMap<>();

        if (csvRecord == null || csvRecord.trim().isEmpty()) {
            logger.warn("Empty or null CSV record encountered");
            return data;
        }

        String[] fields = csvRecord.split(",", -1); // -1 to include trailing empty fields

        // Handle the first record as headers
        if (headers == null) {
            headers = fields;
            logger.info("Detected CSV headers: {}", String.join(", ", headers));
            return data; // Skip processing the header row as data
        }

        // Parse data row using headers
        if (fields.length == headers.length) {
            for (int i = 0; i < headers.length; i++) {
                String header = headers[i].trim();
                String value = fields[i].trim();
                data.put(header, inferType(value)); // Infer type dynamically
            }
        } else {
            logger.error("Mismatch in CSV record format. Expected {} fields, got {}. Record: {}",
                    headers.length, fields.length, csvRecord);
        }
        return data;
    }

    /**
     * Infer the type of a value (String, Integer, Double) based on its format
     */
    private Object inferType(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            if (value.matches("-?\\d+")) { // Integer
                return Integer.parseInt(value);
            } else if (value.matches("-?\\d*\\.?\\d+")) { // Double
                return Double.parseDouble(value);
            }
        } catch (NumberFormatException e) {
            logger.debug("Could not parse '{}' as number, treating as String", value);
        }
        return value; // Default to String
    }

    public void shutdown() {
        running = false;
        if (consumer != null) {
            consumer.wakeup();
        }
    }
}
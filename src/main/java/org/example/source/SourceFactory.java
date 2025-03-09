package org.example.source;

import org.example.config.SourceConfig;

public class SourceFactory {
    public static ETLDataSource createSource(SourceConfig config) throws Exception {
        return switch (config.getType()) {
            case "file" -> new FileSource(config);
            case "s3" -> new AwsS3Source(config);
//            case "hdfs" -> null; //new HDFSSource(config);
            case "kafka" -> new KafkaSource(config);
            case "db" -> new JdbcSource(config);
            default -> throw new UnsupportedOperationException("Unsupported source type: " + config.getType());
        };
    }
}

package org.example.sink;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.config.SinkConfig;

import java.beans.PropertyVetoException;
import java.sql.SQLException;

public class SinkFactory {
    private static final Logger logger = LogManager.getLogger(SinkFactory.class);

    public static ETLDataSink createSink(SinkConfig config) throws PropertyVetoException, SQLException {
        if (config == null) {
            logger.warn("Defaulting to StdoutSink since 'sink' config is missing in yml file");
            return new StdoutSink(null); // Default sink
        }

        if (config.getType() == null) {
            logger.warn("Defaulting to StdoutSink since 'type' is missing from 'sink' config is missing in yml file");
            return new StdoutSink(config); // Default sink
        }

        return switch (config.getType()) {
            case "file" -> null; //new FileSink(config);
            case "db" -> new JdbcSink(config);
            case "s3" -> null; //new S3Sink(config);
            default -> throw new UnsupportedOperationException("Unsupported sink type: " + config.getType());
        };
    }
}


package org.example;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.config.Config;
import org.example.config.ConfigLoader;
import org.example.pipeline.DataPipeline;
import org.example.sink.ETLDataSink;
import org.example.sink.SinkFactory;
import org.example.source.ETLDataSource;
import org.example.source.SourceFactory;

import java.util.List;
import java.util.Objects;

public class ETLFlow {
    private static final Logger logger = LogManager.getLogger(ETLFlow.class);
    private final String configPath;
    private ETLMode mode;

    public ETLFlow(ETLMode mode, String configPath) {
        this.mode = mode;
        this.configPath = configPath;
    }

    private void verifyMode(Config config) {
        try {
            mode = ETLMode.valueOf(config.getEtlMode() == null ? null : config.getEtlMode().toUpperCase());
        } catch (Exception ex) {
            logger.error("Invalid to 'etlMode' - '{}' found in config file '{}'. Details: {}",
                    config.getEtlMode(),
                    configPath,
                    ExceptionUtils.getStackTrace(ex));
        }
    }

    public void start() {
        logger.info("ETL process mode: {}", mode);
        switch (mode) {
            case BATCH:
                runBatch();
                break;
            case STREAMING:
                runStreaming();
                break;
            case HYBRID:
                runBatch();
                runStreaming();
                break;
        }
    }

    private void runBatch() {
        logger.info("Loading config '{}' for etl processing..", configPath);
        Config config;
        try {
            config = new ConfigLoader().loadConfig(configPath);
        } catch (Exception ex) {
            logger.error("Unable to load config file '{}' properly. Details: {}", configPath,
                    ExceptionUtils.getStackTrace(ex));
            return;
        }
        List<ETLDataSource> batchSources;
        try {
            batchSources =
                    List.of(Objects.requireNonNull(SourceFactory.createSource(config.getSource())));
        } catch (Exception ex) {
            logger.error("Unable to register Sources. Details: {}", ExceptionUtils.getStackTrace(ex));
            return;
        }

        List<ETLDataSink> sinks;
        try {
            sinks = List.of(Objects.requireNonNull(SinkFactory.createSink(config.getSink())));
        } catch (Exception e) {
            logger.error("Unable to register Sinks. Details: {}", ExceptionUtils.getStackTrace(e));
            return;
        }

        DataPipeline pipeline = new DataPipeline(sinks.getFirst());
        for (ETLDataSource source : batchSources) {
            source.submitTo(pipeline);
        }
        pipeline.finish();
    }

    private void runStreaming() {
        logger.info("Running streaming ETL...");
//        for (StreamingDataSource source : streamingSources) {
//            source.startListening(pipeline);
//        }
    }
}

package org.example.sink;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.config.SinkConfig;

import java.util.List;
import java.util.Map;

public class StdoutSink extends ETLDataSink {
    private static final Logger logger = LogManager.getLogger(StdoutSink.class);

    public StdoutSink(SinkConfig sinkConfig) {
        super(sinkConfig);
    }

    @Override
    public void save(List<Map<String, Object>> recordList) throws Exception {
        recordList.forEach(logger::info);
    }
}

package org.example.sink;

import org.example.config.SinkConfig;

import java.util.List;
import java.util.Map;

public abstract class ETLDataSink {
    SinkConfig sinkConfig;

    public ETLDataSink(SinkConfig sinkConfig) {
        this.sinkConfig = sinkConfig;
    }

    public abstract void save(List<Map<String, Object>> recordList) throws Exception;

    public void close() {
        
    }
}

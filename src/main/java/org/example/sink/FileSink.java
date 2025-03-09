package org.example.sink;

import org.example.config.SinkConfig;

import java.util.List;
import java.util.Map;

public class FileSink extends ETLDataSink {
    public FileSink(SinkConfig sinkConfig) {
        super(sinkConfig);
    }

    @Override
    public void save(List<Map<String, Object>> recordList) {

    }
}

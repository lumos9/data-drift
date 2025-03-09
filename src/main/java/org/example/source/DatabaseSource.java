package org.example.source;

import org.example.config.SourceConfig;
import org.example.pipeline.DataPipeline;

import java.io.InputStream;

public class DatabaseSource extends ETLDataSource {
    DatabaseSource(SourceConfig sourceConfig) throws Exception {
        super(sourceConfig);
    }

    @Override
    public InputStream getInputStream() {
        return null;
    }

    @Override
    public void submitTo(DataPipeline pipeline) {

    }
}

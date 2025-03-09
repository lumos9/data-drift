package org.example.config;

public class Config {
    private String etlMode; // "batch", "streaming", or "hybrid"
    private SourceConfig source;
    private TransformationConfig transformation;
    private SinkConfig sink;

    // Getters and Setters
    public String getEtlMode() {
        return etlMode;
    }

    public void setEtlMode(String etlMode) {
        this.etlMode = etlMode;
    }

    public SourceConfig getSource() {
        return source;
    }

    public void setSource(SourceConfig source) {
        this.source = source;
    }

    public TransformationConfig getTransformation() {
        return transformation;
    }

    public void setTransformation(TransformationConfig transformation) {
        this.transformation = transformation;
    }

    public SinkConfig getSink() {
        return sink;
    }

    public void setSink(SinkConfig sink) {
        this.sink = sink;
    }
}

package org.example.config;

public class SinkConfig {
    private String type; // "file", "db", "s3", etc.
    private String path; // For file-based sinks, path to the output location (local file, S3, HDFS, etc.)
    private DbConfig dbConfig; // For database sinks, connection details

    // Getters and Setters
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public DbConfig getDbConfig() {
        return dbConfig;
    }

    public void setDbConfig(DbConfig dbConfig) {
        this.dbConfig = dbConfig;
    }
}

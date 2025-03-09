package org.example.config;

import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.IOException;

public class ConfigLoader {
    public Config loadConfig(String configFilePath) throws IOException {
        Yaml yaml = new Yaml();
        try (FileInputStream inputStream = new FileInputStream(configFilePath)) {
            return yaml.loadAs(inputStream, Config.class);
        }
    }
}

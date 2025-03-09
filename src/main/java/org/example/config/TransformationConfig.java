package org.example.config;

import java.util.List;

public class TransformationConfig {
    private String type; // "custom", "filter", "map", "join", etc.
    private List<String> logic; // List of transformation steps, e.g., SQL-like filters or mappings

    // Getters and Setters
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getLogic() {
        return logic;
    }

    public void setLogic(List<String> logic) {
        this.logic = logic;
    }
}

package org.example.source;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.config.SourceConfig;
import org.example.pipeline.DataPipeline;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class JdbcSource extends DatabaseSource {
    private static final Logger logger = LogManager.getLogger(JdbcSource.class);

    public JdbcSource(SourceConfig sourceConfig) throws Exception {
        super(sourceConfig);
        logger.info("Configured JdbcSource with path");
    }

    @Override
    public void submitTo(DataPipeline pipeline) {
        try (Connection conn = DriverManager.getConnection(
                sourceConfig.getDbConfig().getUrl(),
                sourceConfig.getDbConfig().getUsername(),
                sourceConfig.getDbConfig().getPassword())) {
            try (PreparedStatement ps = conn.prepareStatement(sourceConfig.getDbConfig().getQuery());
                 ResultSet rs = ps.executeQuery()) {
                //List<Map<String, Object>> results = new ArrayList<>();
                ResultSetMetaData metaData = rs.getMetaData();
                int columnCount = metaData.getColumnCount();
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    for (int i = 1; i <= columnCount; i++) {
                        String columnName = metaData.getColumnName(i).toLowerCase();
                        row.put(columnName, rs.getObject(i)); // Use getObject to handle various data types
                    }
                    //results.add(row);
                    pipeline.process(row);
                }
                pipeline.flush();
                // Process results or return them
            } catch (SQLException e) {
                logger.error("Error fetching data: {}", ExceptionUtils.getStackTrace(e));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}

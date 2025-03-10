package org.example.sink;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.config.SinkConfig;

import java.beans.PropertyVetoException;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;
import java.time.Instant;

public class JdbcSink extends ETLDataSink {
    private static final Logger logger = LogManager.getLogger(JdbcSink.class);
    private final transient ComboPooledDataSource dataSource;
    private final String tableName;
    private final Set<String> validColumns;

    public JdbcSink(SinkConfig sinkConfig) throws PropertyVetoException, SQLException {
        super(sinkConfig);
        this.tableName = sinkConfig.getDbConfig().getTable();
        String url = sinkConfig.getDbConfig().getUrl();
        String username = sinkConfig.getDbConfig().getUsername();
        String password = sinkConfig.getDbConfig().getPassword();
        try (Connection conn = DriverManager.getConnection(
                url,
                username,
                password)) {

            if (!isTableValid(conn, sinkConfig.getDbConfig().getSchemaName(), tableName)) {
                throw new SQLException("Table '" + tableName + "' does not exist.");
            }

            // Proceed with pool setup if table is valid
            dataSource = new ComboPooledDataSource();
            dataSource.setDriverClass(sinkConfig.getDbConfig().getDriverName());
            dataSource.setJdbcUrl(url);
            dataSource.setUser(username);
            dataSource.setPassword(password);
            dataSource.setMaxPoolSize(20);
            dataSource.setMinPoolSize(1);
            dataSource.setAcquireIncrement(1);
            dataSource.setMaxStatements(100);

            logger.info("Sink connection successful");

            // Fetch the columns for the valid table
            this.validColumns = fetchTableColumns(conn, tableName);
            if (validColumns.isEmpty()) {
                throw new SQLException("Table '" + tableName + "' has no columns.");
            }
        } catch (Exception ex) {
            throw new RuntimeException(
                    "Failed to connect to DB with url: '" + url + "'. Please check sink connection settings in your yml file",
                    ex);
        }
    }

    // Helper method to check if the table exists safely
    private boolean isTableValid(Connection conn, String schemaName, String tableName) throws SQLException {
        String query = "SELECT EXISTS (SELECT 1 FROM pg_tables WHERE schemaname = ? AND tablename = ?)";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, schemaName);
            stmt.setString(2, tableName.toLowerCase()); // Ensure case consistency
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getBoolean(1); // Returns true if table exists
            }
        }
    }

    private Set<String> fetchTableColumns(Connection conn, String tableName) throws SQLException {
        Set<String> columns = new HashSet<>();
        String query = "SELECT column_name FROM information_schema.columns WHERE table_name = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, tableName);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    columns.add(rs.getString("column_name").toLowerCase());
                }
            }
        }
        return columns;
    }

    public void save(List<Map<String, Object>> records) throws Exception {
        if (records == null || records.isEmpty()) return;

        // Determine valid keys that match table columns
        Set<String> recordKeys = records.stream()
                .flatMap(map -> map.keySet().stream())
                .map(String::toLowerCase)
                .filter(validColumns::contains)
                .collect(Collectors.toSet());

        if (recordKeys.isEmpty()) {
            logger.warn("No matching columns found for table: {}. Skipping insert", tableName);
            return;
        }

        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);

            // Get metadata to map column names to SQL types
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet rs = metaData.getColumns(null, null, tableName, null);
            Map<String, Integer> columnTypes = new HashMap<>();
            while (rs.next()) {
                columnTypes.put(rs.getString("COLUMN_NAME"), rs.getInt("DATA_TYPE"));
            }

            String sql = buildInsertSQL(tableName, recordKeys);
            //logger.info(sql);
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                for (Map<String, Object> record : records) {
                    int paramIndex = 1;
                    for (String key : recordKeys) {
                        //logger.info("{} - {}", key, paramIndex);
                        Object value = record.get(key);
                        int sqlType = columnTypes.get(key);

                        if (value == null) {
                            ps.setNull(paramIndex, sqlType);
                        } else {
                            ps.setObject(paramIndex, convertValueToSQLType(value, sqlType));
                        }
                        paramIndex++;
                    }
                    ps.addBatch();
                }
                ps.executeBatch();
                connection.commit();
                logger.info("Saved {} rows successfully", records.size());
            }
        } catch (SQLException e) {
            logger.error("Failed to execute batch insert: {}", ExceptionUtils.getStackTrace(e));
        }
    }

    // Upsert all columns
    private String buildInsertSQL(String tableName, Set<String> columns) {
        // Remove "id" from the insert columns since it's auto-generated
//        Set<String> insertColumns = columns.stream()
//                //.filter(col -> !col.equals("id"))
//                .collect(Collectors.toSet());

        String columnNames = String.join(", ", columns);
        String placeholders = String.join(", ", Collections.nCopies(columns.size(), "?"));

        String updateSet = columns.stream()
                .map(col -> col + " = EXCLUDED." + col)
                .collect(Collectors.joining(", "));

        String query = "INSERT INTO " + tableName + " (" + columnNames + ") VALUES (" + placeholders + ") " +
                "ON CONFLICT (id) DO UPDATE SET " + updateSet;

        //logger.info(query);
        return query;
    }

    private Object convertValueToSQLType(Object value, int sqlType) {
        try {
            return switch (sqlType) {
                case Types.INTEGER, Types.BIGINT, Types.SMALLINT -> Integer.parseInt(value.toString());
                case Types.DECIMAL, Types.NUMERIC, Types.FLOAT, Types.DOUBLE -> Double.parseDouble(value.toString());
                case Types.BOOLEAN -> Boolean.parseBoolean(value.toString());
                case Types.TIMESTAMP, Types.DATE -> Timestamp.from(Instant.parse(value.toString()));
                default -> value; // fallback for VARCHAR, TEXT, etc.
            };
        } catch (Exception e) {
            logger.warn("Failed to convert value '{}' to SQL type '{}'. Inserting as null.", value, sqlType);
            return null;
        }
    }

    public void close() {
        if (dataSource != null) {
            dataSource.close();
            logger.info("Datasource closed successfully");
        }
    }
}


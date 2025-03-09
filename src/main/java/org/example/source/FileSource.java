package org.example.source;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.config.SourceConfig;
import org.example.pipeline.DataPipeline;
import org.example.utils.DateTimeUtils;

import java.io.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class FileSource extends ETLDataSource {
    private static final Logger logger = LogManager.getLogger(FileSource.class);

    public FileSource(SourceConfig sourceConfig) throws Exception {
        super(sourceConfig);
        logger.info("Configured FileSource with path '{}'",
                sourceConfig.getPath());
    }

    private static void streamCsvAsMap(String filePath, DataPipeline dataPipeline)
            throws IOException, CsvValidationException {
        long start = System.nanoTime();
        try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
            String[] headers = reader.readNext(); // Read header row
            if (headers == null) {
                long end = System.nanoTime();
                logger.info("Finished reading file in {}", DateTimeUtils.getReadableDuration(start, end));
                return;
            }
            String[] values;
            while ((values = reader.readNext()) != null) { // Read one row at a time
                Map<String, Object> record = new LinkedHashMap<>();
                for (int i = 0;
                     i < headers.length && i < values.length;
                     i++) {
                    record.put(headers[i].trim(),
                            values[i].trim());
                }
                dataPipeline.process(record); // Process row dynamically
            }
            dataPipeline.flush();
            logger.info("Finished reading file in {}",
                    DateTimeUtils.getReadableDuration(start, System.nanoTime()));
        }
    }

    @Override
    public InputStream getInputStream() throws Exception {
        return new BufferedInputStream(new FileInputStream(sourceConfig.getPath()));
    }

    @Override
    public void submitTo(DataPipeline pipeline) {
        streamAsMap(pipeline);
    }

    @FunctionalInterface
    interface RowProcessor {
        void process(Map<String, Object> row);
    }
}

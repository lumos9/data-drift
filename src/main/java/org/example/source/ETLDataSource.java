package org.example.source;

import com.opencsv.CSVReader;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.compression.CompressionAdapter;
import org.example.compression.CompressionAdapterFactory;
import org.example.config.SourceConfig;
import org.example.encode.EncodingAdapter;
import org.example.encode.EncodingAdapterFactory;
import org.example.pipeline.DataPipeline;
import org.example.security.EncryptionAdapter;
import org.example.security.EncryptionAdapterFactory;
import org.example.utils.DateTimeUtils;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

public abstract class ETLDataSource {
    private static final Logger logger = LogManager.getLogger(ETLDataSource.class);
    protected final CompressionAdapter compressionAdapter;
    protected final EncryptionAdapter encryptionAdapter;
    protected final EncodingAdapter encodingAdapter; // New field
    protected SourceConfig sourceConfig;

    ETLDataSource(SourceConfig sourceConfig) throws Exception {
        this.sourceConfig = sourceConfig;
        this.compressionAdapter = CompressionAdapterFactory.getAdapter(sourceConfig.getCompressionConfig());
        this.encryptionAdapter = EncryptionAdapterFactory.getAdapter(sourceConfig.getEncryptionConfig());
        this.encodingAdapter = EncodingAdapterFactory.getAdapter(sourceConfig.getEncodingConfig());
    }

    protected void streamAsMap(DataPipeline dataPipeline) {
        long start = System.nanoTime();
        try (InputStream rawStream = getInputStream();
             InputStream decompressedStream = compressionAdapter != null ? compressionAdapter.decompress(rawStream) :
                     rawStream;
             InputStream decryptedStream = encryptionAdapter != null ? encryptionAdapter.decrypt(decompressedStream) :
                     decompressedStream;
             InputStream decodedStream = encodingAdapter != null ? encodingAdapter.decode(decryptedStream) :
                     decryptedStream;
             InputStreamReader inputStreamReader = new InputStreamReader(decodedStream, StandardCharsets.UTF_8);
             CSVReader reader = new CSVReader(inputStreamReader);) {
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
        } catch (Exception ex) {
            logger.error("Failed to read source '{}'. Details: {}", sourceConfig.getPath(),
                    ExceptionUtils.getStackTrace(ex));
        }
    }

    public abstract InputStream getInputStream() throws Exception;

    public abstract void submitTo(DataPipeline pipeline);
}

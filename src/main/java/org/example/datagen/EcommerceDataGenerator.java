package org.example.datagen;

import org.apache.commons.lang3.RandomUtils;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.LongStream;

public class EcommerceDataGenerator {
    private static final Logger logger = LogManager.getLogger(EcommerceDataGenerator.class);
    private static final List<String> CATEGORIES = List.of("Electronics", "Clothing", "Books", "Home", "Toys");
    private static final List<String> PAYMENT_METHODS = List.of("Credit Card", "PayPal", "Debit Card", "Cash");
    private static final List<String> REGIONS = List.of("CA", "NY", "TX", "FL", "UK", "DE");
    private static final long MAX_FILE_SIZE_BYTES = 1_073_741_824L; // 1GB
    private static final String KAFKA_BOOTSTRAP_SERVERS = "localhost:9092";
    private static final String KAFKA_TOPIC = "ecommerce-transactions";

    public static void main(String[] args) {
        Config config = parseArgs(args);
        if (config == null) return;
        try {
            switch (config.mode) {
                case "--stream" -> generateDatasetToKafka(config.volume, config.format);
                case "--file" -> generateDatasetToFile(config.volume, config.outputPath, config.format);
                default -> logger.error("Unrecognized mode: {}", config.mode);
            }
        } catch (IOException e) {
            logger.error("Data generation failed: {}", e.getMessage(), e);
            System.exit(1);
        }
    }

    private static Config parseArgs(String[] args) {
        if (args.length < 3 || args.length > 4) {
            printUsage();
            return null;
        }

        long volume;
        try {
            volume = Long.parseLong(args[0]);
            if (volume <= 0) throw new IllegalArgumentException("Volume must be positive");
        } catch (NumberFormatException e) {
            logger.error("Invalid volume: {}. Must be a positive number.", args[1]);
            printUsage();
            return null;
        }

        Format format;
        try {
            format = Format.valueOf(args[1].toUpperCase());
        } catch (IllegalArgumentException e) {
            logger.error("Invalid format: {}. Use CSV, TSV, JSON, or XML.", args[2]);
            printUsage();
            return null;
        }

        String mode = args[2];
        String outputPath = (mode.equals("--file") && args.length == 4) ? args[3] : null;
        if (mode.equals("--file") && outputPath == null) {
            logger.error("--file mode requires an output path");
            printUsage();
            return null;
        }

        if (!mode.equals("--stream") && !mode.equals("--file")) {
            logger.error("Invalid mode: {}. Use --stream or --file.", mode);
            printUsage();
            return null;
        }

        if (mode.equals("--file") && volume * estimateRowSize(format) > MAX_FILE_SIZE_BYTES) {
            logger.warn("Estimated file size ({} GB) exceeds 1GB limit",
                    volume * estimateRowSize(format) / 1_073_741_824.0);
        }

        return new Config(mode, volume, outputPath, format);
    }

    private static long estimateRowSize(Format format) {
        return switch (format) {
            case CSV, TSV -> 100;  // ~100 bytes/row
            case JSON -> 150;      // ~150 bytes/row (more verbose)
            case XML -> 200;       // ~200 bytes/row (most verbose)
        };
    }

    private static void printUsage() {
        System.out.println("""
                Usage: java org.example.datagen.EcommerceDataGenerator <volume> <format> <mode> [output-file]
                  <volume>: Number of transactions (e.g., 1000000)
                  <format>: CSV, TSV, JSON, or XML
                  <mode>: --stream (Kafka) or --file (file output)
                  [output-file]: File path (required for --file)
                Examples:
                  java org.example.datagen.EcommerceDataGenerator 1000000 JSON --stream
                  java org.example.datagen.EcommerceDataGenerator 1000000 CSV --file transactions.csv""");
    }

    private static void generateDatasetToKafka(long volume, Format format) throws IOException {
        Properties props = new Properties();
        props.put("bootstrap.servers", KAFKA_BOOTSTRAP_SERVERS);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        try (var producer = new KafkaProducer<String, String>(props);
             var executor = Executors.newVirtualThreadPerTaskExecutor()) {

            LongStream.range(0, volume).forEach(i -> executor.submit(() -> {
                String row = generateRow(format);
                producer.send(new ProducerRecord<>(KAFKA_TOPIC, null, row));
            }));

            producer.flush();
            executor.shutdown();
            awaitTermination(executor);
            logger.info("Streamed {} transactions to Kafka topic: {} in {}", volume, KAFKA_TOPIC, format);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Interrupted during Kafka streaming", e);
        }
    }

    private static void awaitTermination(ExecutorService executor) throws InterruptedException {
        logger.info("Waiting for all tasks to be finished..");
        if (!executor.awaitTermination(1, TimeUnit.MINUTES)) { // Reduced to 1 minute for example
            logger.warn("Tasks did not complete in time; forcing shutdown");
            executor.shutdownNow(); // Force terminate running tasks
            if (!executor.awaitTermination(10, TimeUnit.SECONDS)) { // Brief wait for cleanup
                logger.error("Executor did not terminate cleanly");
            }
        }
    }

    private static void generateDatasetToFile(long volume, String outputPath, Format format) throws IOException {
        try (var executor = Executors.newVirtualThreadPerTaskExecutor();
             var writer = Files.newBufferedWriter(Path.of(outputPath))) {

            // Write headers or opening structure based on format
            switch (format) {
                case CSV -> writer.write(
                        "transaction_id,timestamp,customer_id,product_id,category,quantity,unit_price,total_amount,payment_method,region\n");
                case TSV -> writer.write(
                        "transaction_id\ttimestamp\tcustomer_id\tproduct_id\tcategory\tquantity\tunit_price\ttotal_amount\tpayment_method\tregion\n");
                case JSON -> writer.write("[\n");  // Start JSON array
                case XML -> writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?><transactions>\n");
            }

            LongStream.range(0, volume).forEach(i -> executor.submit(() -> {
                String row = generateRow(format);
                synchronized (writer) {
                    try {
                        writer.write(row);
                        // Add separators or newlines as needed
                        if (format == Format.JSON && i < volume - 1)
                            writer.write(",\n");  // Comma between JSON objects, except last
                        else if (format != Format.XML) writer.newLine();  // Newline for CSV/TSV
                    } catch (IOException e) {
                        logger.error("File write failed: {}", e.getMessage(), e);
                    }
                }
            }));

            executor.shutdown();
            awaitTermination(executor);

            // Write closing structure if needed
            if (format == Format.JSON) writer.write("\n]");
            else if (format == Format.XML) writer.write("</transactions>");

            logger.info("Wrote {} transactions to {} in {}", volume, outputPath, format);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Interrupted during file writing", e);
        }
    }

    private static String generateRow(Format format) {
        var random = RandomUtils.secureStrong();
        String transactionId = UUID.randomUUID().toString();
        String timestamp = Instant.now()
                .minus(random.randomInt(0, 365), ChronoUnit.DAYS)
                .truncatedTo(ChronoUnit.SECONDS)
                .toString();
        String customerId = "C" + random.randomInt(1, 100_000);
        String productId = "P" + random.randomInt(1, 10_000);
        String category = CATEGORIES.get(random.randomInt(0, CATEGORIES.size()));
        int quantity = random.randomInt(1, 11);
        double unitPrice = random.randomDouble(5.0, 500.0);
        double totalAmount = quantity * unitPrice * (random.randomBoolean() ? 1.0 : 0.9);
        String paymentMethod = PAYMENT_METHODS.get(random.randomInt(0, PAYMENT_METHODS.size()));
        String region = REGIONS.get(random.randomInt(0, REGIONS.size()));

        return switch (format) {
            case CSV -> String.format("%s,%s,%s,%s,%s,%d,%.2f,%.2f,%s,%s",
                    transactionId, timestamp, customerId, productId,
                    category, quantity, unitPrice, totalAmount, paymentMethod, region);
            case TSV -> String.format("%s\t%s\t%s\t%s\t%s\t%d\t%.2f\t%.2f\t%s\t%s",
                    transactionId, timestamp, customerId, productId,
                    category, quantity, unitPrice, totalAmount, paymentMethod, region);
            case JSON -> String.format(
                    "{\"transaction_id\":\"%s\",\"timestamp\":\"%s\",\"customer_id\":\"%s\",\"product_id\":\"%s\",\"category\":\"%s\"," +
                            "\"quantity\":%d,\"unit_price\":%.2f,\"total_amount\":%.2f,\"payment_method\":\"%s\",\"region\":\"%s\"}",
                    transactionId, timestamp, customerId, productId,
                    category, quantity, unitPrice, totalAmount, paymentMethod, region);
            case XML -> String.format(
                    "  <transaction><transaction_id>%s</transaction_id><timestamp>%s</timestamp><customer_id>%s</customer_id>" +
                            "<product_id>%s</product_id><category>%s</category><quantity>%d</quantity><unit_price>%.2f</unit_price>" +
                            "<total_amount>%.2f</total_amount><payment_method>%s</payment_method><region>%s</region></transaction>",
                    transactionId, timestamp, customerId, productId,
                    category, quantity, unitPrice, totalAmount, paymentMethod, region);
        };
    }

    enum Format {CSV, TSV, JSON, XML}

    record Config(String mode, long volume, String outputPath, Format format) {
    }
}
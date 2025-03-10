package org.example.pipeline;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.sink.ETLDataSink;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

public class DataPipeline {

    private static final Logger logger = LogManager.getLogger(DataPipeline.class);
    private static final AtomicReference<ExecutorService> poolRef = new AtomicReference<>();
    private final ETLDataSink etlDataSink;
    private List<Map<String, Object>> list;

    public DataPipeline(ETLDataSink etlDataSink) {
        this.list = new ArrayList<>();
        this.etlDataSink = etlDataSink;
    }

    private static ExecutorService getOrInitPool() {
        if (poolRef.get() == null) {  // First check
            synchronized (poolRef) {
                if (poolRef.get() == null) {
                    ThreadFactory namedThreadFactory = r -> {
                        Thread t = new Thread(r);
                        t.setName("DataPipelineThread-" + t.threadId()); // Fixed prefix
                        return t;
                    };// Double-Checked Locking
                    int numProcessors = Runtime.getRuntime()
                            .availableProcessors();
                    poolRef.set(new ThreadPoolExecutor(
                            numProcessors,
                            // Core threads
                            numProcessors * 2,
                            // Max threads (adjust as needed)
                            60L,
                            TimeUnit.SECONDS,
                            // Keep-alive
                            new LinkedBlockingQueue<>(500),
                            // Large queue size
                            //namedThreadFactory,
                            new ThreadPoolExecutor.CallerRunsPolicy()
                            // Backpressure handling
                    ));
                    //logger.info("Thread pool init success");
                } else {
                    //logger.info("2 - Returning existing thread pool..");
                }
            }
        } else {
            //logger.info("1 - Returning existing thread pool..");
        }
        return poolRef.get();
    }

    private static void shutdownAndAwaitTermination(ExecutorService executor) {
        logger.info("Waiting for submitted tasks to finish...");
        executor.shutdown(); // Disable new tasks

        try {
            if (!executor.awaitTermination(10,
                    TimeUnit.SECONDS)) {
                logger.warn("Forcing shutdown...");
                executor.shutdownNow(); // Force shutdown

                if (!executor.awaitTermination(5,
                        TimeUnit.SECONDS)) {
                    logger.error("Executor did not terminate.");
                } else {
                    logger.info("All tasks finished after additional wait!");
                }
            } else {
                logger.info("All tasks finished!");
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread()
                    .interrupt();
        }
    }

    public void process(Map<String, Object> record) {
        if (list.size() != 10_000) {
            list.add(record);
        } else {
            List<Map<String, Object>> newList = new ArrayList<>(list);
            submitToPool(newList);
            list = new ArrayList<>();
            list.add(record);
        }
    }

    public void flush() {
        if (!list.isEmpty()) {
            List<Map<String, Object>> newList = new ArrayList<>(list);
            submitToPool(newList);
            list = new ArrayList<>();
        }
    }

    public void submitToPool(List<Map<String, Object>> records) {
        ExecutorService poolExecutor = getOrInitPool();
        poolExecutor.execute(() -> {
            try {
                etlDataSink.save(records);
            } catch (Exception ex) {
                logger.error("Saving to sink failed. Details : {}", ExceptionUtils.getStackTrace(ex));
            }
        });
    }

    public void finish() {
        ExecutorService poolExecutor = poolRef.get();
        if (poolExecutor != null) {
            shutdownAndAwaitTermination(poolExecutor);
            poolRef.set(null);
        }
        etlDataSink.close();
    }
}

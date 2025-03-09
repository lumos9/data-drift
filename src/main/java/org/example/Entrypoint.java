package org.example;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.utils.DateTimeUtils;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Entrypoint {
    private static final Logger logger = LogManager.getLogger(Entrypoint.class);

    public static void main(String[] args) {
        long start = System.nanoTime();
        if (args.length == 0) {
            logger.error("Please pass options");
            System.exit(1);
        }
        String configFilePath = args[0];
        new ETLFlow(configFilePath).start();
        logger.info("ETL process took {}", DateTimeUtils.getReadableDuration(start, System.nanoTime()));
    }
}
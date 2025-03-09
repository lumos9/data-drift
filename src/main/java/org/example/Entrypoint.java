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

        ETLMode mode = null;
        String modeArg = args[0].toLowerCase();
        String configFilePath = null;
        switch (modeArg) {
            case "--batch":
                if (args.length < 2) {
                    logger.error("Please pass full config file path");
                    return;
                }
                mode = ETLMode.BATCH;
                configFilePath = args[1];
                break;
            case "--streaming":
                mode = ETLMode.STREAMING;
                break;
//            case "--hybrid":
//                mode = ETLMode.HYBRID;
//                break;
            default:
                logger.error("Invalid mode. Use --batch, --streaming, or --hybrid.");
                return;
        }

        new ETLFlow(mode, configFilePath).start();
        logger.info("ETL process took {}", DateTimeUtils.getReadableDuration(start, System.nanoTime()));
    }
}
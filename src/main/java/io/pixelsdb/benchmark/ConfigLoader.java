package io.pixelsdb.benchmark;
/**
 * @time 2023-03-04
 * @version 1.0.0
 * @file ConfigLoader.java
 * @description Load config and print config when start workload.
 **/

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class ConfigLoader {
    public static String confFile = null;
    public static Properties prop = new Properties();
    Logger logger = LogManager.getLogger(ConfigLoader.class);

    public void loadConfig() {
        try {
            FileInputStream fis = new FileInputStream(ConfigLoader.confFile);
            prop.load(fis);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            logger.error("Read configure failed : " + ConfigLoader.confFile, e);
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            logger.error("Read configure failed : " + ConfigLoader.confFile, e);
            e.printStackTrace();
        }
    }

    public void printConfig() {
        logger.info("===============configuration==================");
        for (Object k : prop.keySet()) {
            logger.info(k.toString() + " = " + prop.getProperty(k.toString()));
        }
        logger.info("===============configuration==================");
        logger.info("");
    }
}

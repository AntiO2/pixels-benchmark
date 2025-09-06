package io.pixelsdb.benchmark.stats;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @version 1.0.0
 * @time 2023-03-04
 * @file Result.java
 * @description record test result and print summary after all workloads are
 *              done.
 **/

public class Result {
    public static Logger logger = LogManager.getLogger(Result.class);
    private String dbType = null;
    private long tpTotal;
    private long apTotal;
    private long atTotal;
    private long iqTotal;
    private double tps;
    private double qps;
    private double xptps;
    private double xpqps;
    private double atps;
    private double iqps;
    private String startTS;
    private String endTs;
    private Histogram hist;
    private double freshness = 0;
    private int apclient;
    private int tpclient;
    private int xapclient;
    private int xtpclient;
    private String riskRate;
    private int apRound;
    private int testDuration;

    public Result() {
        hist = new Histogram();
    }

    public int getApRound() {
        return this.apRound;
    }

    public void setApRound(int round) {
        this.apRound = round;
    }

    public String getRiskRate() {
        return this.riskRate;
    }

    public void setRiskRate(String riskRate) {
        this.riskRate = riskRate;
    }

    public Histogram getHist() {
        return hist;
    }

    public void setHist(Histogram hist) {
        this.hist = hist;
    }

    public int getApclient() {
        return apclient;
    }

    public void setApclient(int apclient) {
        this.apclient = apclient;
    }

    public int getTpclient() {
        return tpclient;
    }

    public void setTpclient(int tpclient) {
        this.tpclient = tpclient;
    }

    public int getXapclient() {
        return xapclient;
    }

    public void setXapclient(int xapclient) {
        this.xapclient = xapclient;
    }

    public int getXtpclient() {
        return xtpclient;
    }

    public void setXtpclient(int xtpclient) {
        this.xtpclient = xtpclient;
    }

    public String getDbType() {
        return dbType;
    }

    public void setDbType(String dbType) {
        this.dbType = dbType;
    }

    public long getTpTotal() {
        return tpTotal;
    }

    public void setTpTotal(long tpTotal) {
        this.tpTotal = tpTotal;
    }

    public long getApTotal() {
        return apTotal;
    }

    public void setApTotal(long apTotal) {
        this.apTotal = apTotal;
    }

    public double getTps() {
        return tps;
    }

    public void setTps(double tps) {
        this.tps = tps;
    }

    public double getQps() {
        return qps;
    }

    public void setQps(double qps) {
        this.qps = qps;
    }

    public String getStartTS() {
        return startTS;
    }

    public void setStartTS(String startTS) {
        this.startTS = startTS;
    }

    public String getEndTs() {
        return endTs;
    }

    public void setEndTs(String endTs) {
        this.endTs = endTs;
    }

    public double getFresh() {
        return freshness;
    }

    public void setFresh(double freshness) {
        this.freshness = freshness;
    }

    public void setTestDuration(int testDuration) {
        this.testDuration = testDuration;
    }

    public void printResult(int type) {
        logger.info("====================Test Summary========================");
        logger.info("Test starts at " + getStartTS());
        logger.info("Test ends at " + getEndTs());
        logger.info("Risk Rate is " + getRiskRate());
        switch (type) {
            case 0:
                logger.info("XP-IQ Concurrency is " + getXapclient());
                logger.info("XP-AT Concurrency is " + getXtpclient());
                logger.info("Total amount of XP-IQ Queries is " + getIqTotal());
                logger.info("Total amount of XP-AT Transaction is " + getAtTotal());
                logger.info("XP-QPS is " + getXpqps());
                logger.info("XP-TPS is " + getXptps());
                break;
            case 1:
                logger.info("AP Concurrency is " + getApclient());
                logger.info("TP Concurrency is " + getTpclient());
                logger.info("Total amount of TP Transaction is " + getTpTotal());
                logger.info("TPS is " + getTps());
                break;
            case 2:
            case 7:
                logger.info("AP Concurrency is " + getApclient());
                logger.info("TP Concurrency is " + getTpclient());
                logger.info("Total amount of AP Queries is " + getApTotal());
                logger.info("QPS is " + getQps());
                break;
            case 3:
                logger.info("AP Concurrency is " + getApclient());
                logger.info("TP Concurrency is " + getTpclient());
                logger.info("XP-IQ Concurrency is " + getXapclient());
                logger.info("XP-AT Concurrency is " + getXtpclient());
                logger.info("Total amount of TP Transaction is " + getTpTotal());
                logger.info("TPS is " + getTps());
                logger.info("Total amount of AP Queries is " + getApTotal());
                logger.info("QPS is " + getQps());
                logger.info("Total amount of XP-IQ Queries is " + getIqTotal());
                logger.info("Total amount of XP-AT Transaction is " + getAtTotal());
                logger.info("XP-QPS is " + getXpqps());
                logger.info("XP-TPS is " + getXptps());
                break;
            case 4:
                logger.info("XP-IQ Concurrency is " + getXapclient());
                logger.info("XP-AT Concurrency is " + getXtpclient());
                logger.info("Total amount of XP-IQ Queries is " + getIqTotal());
                logger.info("Total amount of XP-AT Transaction is " + getAtTotal());
                logger.info("Fresh-XP-QPS is " + getXpqps());
                logger.info("Fresh-XP-TPS is " + getXptps());
                break;
            case 6:
                logger.info("AP Concurrency is " + getApclient());
                logger.info("TP Concurrency is " + getTpclient());
                logger.info("Total amount of TP Transaction is " + getTpTotal());
                logger.info("TPS is " + getTps());
                logger.info("Total amount of AP Queries is " + getApTotal());
                logger.info("QPS is " + getQps());
                logger.info("XP-IQ Concurrency is " + getXapclient());
                logger.info("XP-AT Concurrency is " + getXtpclient());
                logger.info("Total amount of XP-IQ Queries is " + getIqTotal());
                logger.info("Total amount of XP-AT Transaction is " + getAtTotal());
                logger.info("Fresh-XP-QPS is " + getXpqps());
                logger.info("Fresh-XP-TPS is " + getXptps());
                break;
            case 8:
                logger.info("Init freshness table, TP Client Num is " + getTpclient());
                break;
            case 9:
                logger.info("Freshness(ms) : " + getFresh());
        }
        logger.info("Query/Transaction response time(ms) histogram : ");
        if (type == 2 || type == 6 || type == 7) {
            System.out.println("------------AP-------------------");
            for (int apidx = 0; apidx < 13; apidx++) {
                System.out.printf(
                        "AP Query %2d : max rt : %10.2f | min rt : %10.2f | avg rt : %10.2f | 95%% rt : %10.2f | 99%% rt : %10.2f \n",
                        (apidx + 1),
                        hist.getAPItem(apidx).getMax(),
                        hist.getAPItem(apidx).getMin(),
                        hist.getAPItem(apidx).getMean(),
                        hist.getAPItem(apidx).getPercentile(95),
                        hist.getAPItem(apidx).getPercentile(99));
            }
        }

        if (type == 1 || type == 6) {
            int[] readOpsPerTxn = {
                    1, // TP1
                    1, // TP2
                    1, // TP3
                    1, // TP4
                    1, // TP5
                    1, // TP6
                    1, // TP7
                    1, // TP8
                    1, // TP9
                    1, // TP10
                    1, // TP11
                    2, // TP12
                    1, // TP13
                    1, // TP14
                    1, // TP15
                    2, // TP16
                    2, // TP17
                    2 // TP18
            };

            int[] writeOpsPerTxn = {
                    0, // TP1
                    0, // TP2
                    0, // TP3
                    0, // TP4
                    0, // TP5
                    0, // TP6
                    0, // TP7
                    0, // TP8
                    3, // TP9
                    3, // TP10
                    3, // TP11
                    3, // TP12
                    4, // TP13
                    2, // TP14
                    1, // TP15
                    2, // TP16
                    2, // TP17
                    2 // TP18
            };

            long totalReads = 0;
            long totalWrites = 0;
            System.out.println("------------TP-------------------");
            for (int tpidx = 0; tpidx < 18; tpidx++) {
                long count = hist.getTPItem(tpidx).getN();

                totalReads += count * readOpsPerTxn[tpidx];
                totalWrites += count * writeOpsPerTxn[tpidx];
                System.out.printf(
                        "TP Transaction %2d : count: %d|max rt : %10.2f | min rt : %10.2f | avg rt : %10.2f | 95%% rt : %10.2f | 99%% rt : %10.2f \n",
                        (tpidx + 1),
                        hist.getTPItem(tpidx).getN(),
                        hist.getTPItem(tpidx).getMax(),
                        hist.getTPItem(tpidx).getMin(),
                        hist.getTPItem(tpidx).getMean(),
                        hist.getTPItem(tpidx).getPercentile(95),
                        hist.getTPItem(tpidx).getPercentile(99));
            }
            System.out.printf("Total READ Op  = %d\n", totalReads);
            System.out.printf("Total WRITE Op = %d\n", totalWrites);

            double readOPS = totalReads / ((double) testDuration * 60.0);
            double writeOPS = totalWrites / ((double) testDuration * 60.0);

            System.out.printf("READ OP/sec    = %.2f\n", readOPS);
            System.out.printf("WRITE OP/sec   = %.2f\n", writeOPS);
        }

        if (type == 0 || type == 4 || type == 6) {
            System.out.println("-----------XP-IQ--------------------");
            for (int xpidx = 0; xpidx < 6; xpidx++) {
                System.out.printf(
                        "Interative Query %d : max rt : %10.2f | min rt : %10.2f | avg rt : %10.2f | 95%% rt : %10.2f | 99%% rt : %10.2f \n",
                        (xpidx + 1),
                        hist.getXPIQItem(xpidx).getMax(),
                        hist.getXPIQItem(xpidx).getMin(),
                        hist.getXPIQItem(xpidx).getMean(),
                        hist.getXPIQItem(xpidx).getPercentile(95),
                        hist.getXPIQItem(xpidx).getPercentile(99));
            }
        }

        if (type == 0 || type == 4 || type == 6) {
            System.out.println("-----------XP-AT--------------------");
            for (int tpidx = 0; tpidx < 6; tpidx++) {
                System.out.printf(
                        "Analytical Transaction AT%d : max rt : %10.2f | min rt : %10.2f | avg rt : %10.2f | 95%% rt : %10.2f | 99%% rt : %10.2f \n",
                        (tpidx + 1),
                        hist.getXPATItem(tpidx).getMax(),
                        hist.getXPATItem(tpidx).getMin(),
                        hist.getXPATItem(tpidx).getMean(),
                        hist.getXPATItem(tpidx).getPercentile(95),
                        hist.getXPATItem(tpidx).getPercentile(99));
            }
        }

        if (type == 3) {
            logger.info("-----------HTAP-Summary--------------------");
            logger.info("-----------AP-Part--------------------");
            logger.info("QPS : " + qps);
            logger.info("-----------TP-Part--------------------");
            logger.info("TPS : " + tps);
            logger.info("-----------XP-Part--------------------");
            logger.info("XP-QPS : " + xpqps);
            logger.info("XP-TPS : " + xptps);
            logger.info("-----------HTAP-Score--------------------");
            logger.info("Geometric Mean : " + Math.pow(qps * tps * (xpqps + xptps), 1 / 3.0));

        }

        if (type == 6) {
            logger.info("-----------HTAP-Summary--------------------");
            logger.info("-----------AP-Part--------------------");
            logger.info("QPS : " + qps);
            logger.info("-----------TP-Part--------------------");
            logger.info("TPS : " + tps);
            logger.info("-----------XP-Part--------------------");
            logger.info("XP-QPS : " + xpqps);
            logger.info("XP-TPS : " + xptps);
            logger.info("-----------Avg-Freshness-Score--------------------");
            logger.info("Freshness(ms) : " + getFresh());
            logger.info("-----------HTAP-Score--------------------");
            logger.info("Geometric Mean : " + Math.pow(qps * tps * (xpqps + xptps), 1 / 3.0) / (1 + getFresh() / 1000));
        }

        if (type == 4) {
            logger.info("-----------Avg-Freshness-Score--------------------");
            logger.info("Freshness(ms) : " + getFresh());
        }

        if (type == 9) {
            System.out.println("-----------Freshness--------------------");
            System.out.printf(
                    "Freshness : max delay : %10.2f | min delay : %10.2f | avg delay : %10.2f | 95%% delay : %10.2f | 99%% delay : %10.2f \n",
                    hist.getFreshness().getMax(),
                    hist.getFreshness().getMin(),
                    hist.getFreshness().getMean(),
                    hist.getFreshness().getPercentile(95),
                    hist.getFreshness().getPercentile(99));
        }

        logger.info("====================Thank you!========================");
    }

    public double getXptps() {
        return xptps;
    }

    public void setXptps(double xptps) {
        this.xptps = xptps;
    }

    public double getXpqps() {
        return xpqps;
    }

    public void setXpqps(double xpqps) {
        this.xpqps = xpqps;
    }

    public double getAtps() {
        return atps;
    }

    public void setAtps(double atps) {
        this.atps = atps;
    }

    public double getIqps() {
        return iqps;
    }

    public void setIqps(double iqps) {
        this.iqps = iqps;
    }

    public long getAtTotal() {
        return atTotal;
    }

    public void setAtTotal(long atTotal) {
        this.atTotal = atTotal;
    }

    public long getIqTotal() {
        return iqTotal;
    }

    public void setIqTotal(long iqTotal) {
        this.iqTotal = iqTotal;
    }
}

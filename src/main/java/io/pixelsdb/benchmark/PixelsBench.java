package io.pixelsdb.benchmark;
/**
 * @time 2023-03-04
 * @version 1.0.0
 * @file PixelsBench.java
 * @description Here is the main class. Load configuration from conf file and read options from command line.
 * Four different test types are provided, including runAP, runTP, runXP ,runHTAP and runAll.
 **/

import io.pixelsdb.benchmark.dbconn.ConnectionMgr;
import io.pixelsdb.benchmark.load.DataGenerator_RiskControlling;
import io.pixelsdb.benchmark.load.ExecSQL;
import io.pixelsdb.benchmark.stats.Result;
import io.pixelsdb.benchmark.workload.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class PixelsBench {
    public static Logger logger = LogManager.getLogger(PixelsBench.class);
    int taskType = 0;
    Result res = new Result();
    boolean verbose = true;
    boolean freshness = false;
    Sqlstmts sqls = null;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static void main(String[] args) throws SQLException {
        LoggerContext context = (LoggerContext) LogManager.getContext(false);
        File file = new File("conf/log4j2.properties");
        context.setConfigLocation(file.toURI());
        ConfigLoader config = new ConfigLoader();
        PixelsBench pixelsBench = new PixelsBench();

        logger.info("Start Pixels Benchmark");
        CommandProcessor cmdProcessor = new CommandProcessor(args);
        HashMap<String, String> argsList = cmdProcessor.commandPaser(args);
        int type = 0;
        String cmd = null;
        if (!argsList.containsKey("t") || !argsList.containsKey("c")) {
            logger.error("Missing required options -t or -c,please check");
            cmdProcessor.printHelp();
            System.exit(-1);
        }

        if (argsList.containsKey("c")) {
            ConfigLoader.confFile = argsList.get("c");
            config.loadConfig();
            config.printConfig();
        }

        if (argsList.containsKey("s")) {
            pixelsBench.verbose = false;
        }

        if (argsList.containsKey("r")) {
            pixelsBench.freshness = true;
        }

        if (argsList.containsKey("t")) {
            cmd = argsList.get("t");
            if (cmd.equalsIgnoreCase("sql")) {
                if (argsList.containsKey("f")) {
                    ExecSQL execSQL = new ExecSQL(argsList.get("f"));
                    execSQL.execute();
                } else {
                    logger.error("Maybe missing sql file argument -f ,please try to use help to check usage.");
                }
            } else if (cmd.equalsIgnoreCase("gendata")) {
                DataGenerator_RiskControlling new_dg = new DataGenerator_RiskControlling((ConfigLoader.prop.getProperty("sf")));
                new_dg.dataGenerator();
                // add loading code for the target DB here
            } else if (cmd.startsWith("run")) {
                String sqlsPath = argsList.get("f");
                SqlReader sqlStmt = new SqlReader(sqlsPath);
                pixelsBench.setSqls(sqlStmt.loader());

                if (cmd.equalsIgnoreCase("runxp")) {
                    type = 0;
                    pixelsBench.runXP(0);
                } else if (cmd.equalsIgnoreCase("runtp")) {
                    type = 1;
                    pixelsBench.runTP();
                } else if (cmd.equalsIgnoreCase("runap")) {
                    type = 7;
                    pixelsBench.runAP();
                } else if (cmd.equalsIgnoreCase("runappower")) {
                    type = 2;
                    pixelsBench.runAPower();
                } else if (cmd.equalsIgnoreCase("runhtap")) {
                    type = 3;
                    pixelsBench.runAP();
                    pixelsBench.runTP();
                    pixelsBench.runXP(0);
                } else if (cmd.equalsIgnoreCase("runFresh")) {
                    type = 4;
                    pixelsBench.runFreshness(4);
                } else if (cmd.equalsIgnoreCase("runAll")) {
                    type = 6;
                    pixelsBench.runAP();
                    pixelsBench.runTP();
                    pixelsBench.runFreshness(4);
                } else if (cmd.equalsIgnoreCase("runinitfresh")) {
                    if(!pixelsBench.freshness) {
                        logger.error("Try to init freshness table, but freshness is not enabled");
                        System.exit(-1);
                    }
                    type = 8;
                    pixelsBench.runInitFreshness();
                } else if (cmd.equalsIgnoreCase("runpixelsfresh")) {
                    if(!pixelsBench.freshness) {
                        logger.error("Try to run pixels freshness only, but freshness is not enabled");
                        System.exit(-1);
                    }
                    type = 9;
                    pixelsBench.runPixelsFreshness(type);
                } else {
                    logger.error("Run task not found : " + cmd);
                    cmdProcessor.printHelp();
                    System.exit(-1);
                }
                logger.info("Congs~ Test is done! Bye!");
                pixelsBench.getRes().printResult(type);
            } else {
                logger.error("Not a known test type,please check");
                cmdProcessor.printHelp();
                System.exit(-1);
            }

        } else {
            cmdProcessor.printHelp();
            System.exit(-1);
        }
    }

    public Result getRes() {
        return res;
    }

    Sqlstmts getSqls() {
        return sqls;
    }

    void setSqls(Sqlstmts sqlloader) {
        this.sqls = sqlloader;
    }

    // run TP type workload. Spouse nums of threads defined in conf file.
    public void runTP() {
        logger.info("Begin TP Workload");
        taskType = 1;
        res.setStartTS(dateFormat.format(new Date()));
        String tpClient = ConfigLoader.prop.getProperty("tpclient");

        List<Client> tasks = new ArrayList<Client>();
        if (Integer.parseInt(tpClient) > 0) {
            Client job = Client.initTask(ConfigLoader.prop, "TPClient", taskType);
            job.setRet(res);
            job.setVerbose(verbose);
            job.setSqls(sqls);
            tasks.add(job);
        } else {
            logger.warn("There is no an available tp client");
            return;
        }
        ExecutorService es = Executors.newFixedThreadPool(tasks.size());
        List<Future> future = new ArrayList<Future>();
        for (final Client j : tasks) {
            future.add(es.submit(new Runnable() {
                        public void run() {
                            j.startTask();
                        }
                    })
            );
        }
        for (int flength = 0; flength < future.size(); flength++) {
            Future f = future.get(flength);
            if (f != null && !f.isCancelled() && !f.isDone()) {
                try {
                    f.get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        }

        if (!es.isShutdown() || !es.isTerminated()) {
            es.shutdownNow();
        }
        res.setEndTs(dateFormat.format(new Date()));
        logger.info("TP Workload is done.");
    }

    public void runAPower() {
        logger.info("Begin AP Workload");
        taskType = 2;
        res.setStartTS(dateFormat.format(new Date()));
        String apClient = "1";
        Client job = Client.initTask(ConfigLoader.prop, "APClient", taskType);
        job.setRet(res);
        job.setSqls(sqls);
        job.setVerbose(verbose);
        ExecutorService es = Executors.newFixedThreadPool(1);
        Future future = es.submit(new Runnable() {
                                      public void run() {
                                          // TODO Auto-generated method stub
                                          job.startTask();
                                      }
                                  }
        );

        if (future != null && !future.isCancelled() && !future.isDone()) {
            try {
                future.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }

        if (!es.isShutdown() || !es.isTerminated()) {
            es.shutdownNow();
        }
        res.setEndTs(dateFormat.format(new Date()));
        logger.info("AP Workload is done.");
    }

    public void runAP() {
        logger.info("Begin AP Workload");
        taskType = 7;
        res.setStartTS(dateFormat.format(new Date()));
        String apClient = ConfigLoader.prop.getProperty("apclient");

        Client job = Client.initTask(ConfigLoader.prop, "APClient", taskType);
        job.setRet(res);
        job.setSqls(sqls);
        job.setVerbose(verbose);
        ExecutorService es = Executors.newFixedThreadPool(1);
        Future future = es.submit(new Runnable() {
                                      public void run() {
                                          // TODO Auto-generated method stub
                                          job.startTask();
                                      }
                                  }
        );

        if (future != null && !future.isCancelled() && !future.isDone()) {
            try {
                future.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }

        if (!es.isShutdown() || !es.isTerminated()) {
            es.shutdownNow();
        }
        res.setEndTs(dateFormat.format(new Date()));
        logger.info("AP Workload is done.");
    }

    public void runXP(int tt) {
        logger.info("Begin XP Workload");
        taskType = tt;
        res.setStartTS(dateFormat.format(new Date()));
        String tpClient = ConfigLoader.prop.getProperty("xtpclient", "-1");
        if (tpClient.equalsIgnoreCase("-1")) {
            logger.error("Missing configuration xtpclient");
            System.exit(-1);
        }
        String apClient = ConfigLoader.prop.getProperty("xapclient", "-1");
        if (tpClient.equalsIgnoreCase("-1")) {
            logger.error("Missing configuration xapclient");
            System.exit(-1);
        }

        List<Client> tasks = new ArrayList<Client>();
        if (Integer.parseInt(tpClient) > 0) {
            Client job = Client.initTask(ConfigLoader.prop, "TPClient", taskType);
            job.setRet(res);
            job.setVerbose(verbose);
            job.setSqls(sqls);
            tasks.add(job);
        }

        if (Integer.parseInt(apClient) > 0) {
            Client job = Client.initTask(ConfigLoader.prop, "APClient", taskType);
            job.setRet(res);
            job.setVerbose(verbose);
            job.setSqls(sqls);
            tasks.add(job);
        }

        ExecutorService es = Executors.newFixedThreadPool(tasks.size());
        List<Future> future = new ArrayList<Future>();
        for (final Client j : tasks) {
            future.add(es.submit(new Runnable() {
                        public void run() {
                            j.startTask();
                        }
                    })
            );
        }
        Thread freshness = null;
        if (taskType == 4) {
            final long startTs = System.currentTimeMillis();
            //System.out.println("Start time is "+Instant.now());
            //final long startTs = Instant.now().toEpochMilli();
            final int _duration = Integer.parseInt(ConfigLoader.prop.getProperty("xpRunMins"));
            final int _fresh_interval = Integer.parseInt(ConfigLoader.prop.getProperty("fresh_interval", String.valueOf(20)));
            String db = ConfigLoader.prop.getProperty("db");
            int dbType = Constant.getDbType(db);
            freshness = new Thread() {

                public void run() {
                    Connection conn_tp = ConnectionMgr.getConnection(0);
                    Connection conn_ap = ConnectionMgr.getConnection(1);
                    double sum = 0;
                    int cnt = 0;
                    long duration = _duration * 60 * 1000L;
                    long elpased_time = 0L;
                    boolean hasGetFreshness = false;
                    for (int i = 0; i < _fresh_interval; i++) {
                        try {
                            Thread.sleep((long) _duration * 60 * 1000 / _fresh_interval);
                            elpased_time += (long) _duration * 60 * 1000 / _fresh_interval;
                            Freshness2 fresh = new Freshness2(dbType, conn_tp, conn_ap, sqls, startTs);
                            if (fresh.calcFreshness() == 2147483647) {
                                continue;
                            }
                            sum += fresh.calcFreshness();
                            cnt++;
                            res.setFresh(sum / cnt);
                            hasGetFreshness = true;
                        } catch (InterruptedException e) {
                            logger.info("Freshness checker was stopped in force");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    if (!hasGetFreshness) {
                        res.setFresh(2147483647);
                    }
                    try {
                        if (conn_ap != null) {
                            conn_ap.close();
                        }
                        if (conn_tp != null) {
                            conn_tp.close();
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            };
            freshness.start();
        }

        for (int flength = 0; flength < future.size(); flength++) {
            Future f = future.get(flength);
            if (f != null && !f.isCancelled() && !f.isDone()) {
                try {
                    f.get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        }

        if (freshness != null) {
            freshness.interrupt();
        }

        if (!es.isShutdown() || !es.isTerminated()) {
            es.shutdownNow();
        }
        res.setEndTs(dateFormat.format(new Date()));
        logger.info("XP Workload is done");
    }

    public void runFreshness(int tt) {

        logger.info("Begin Freshness Workload");
        runXP(tt);
        logger.info("Freshness Workload is done.");
    }


    public void runPixelsFreshness(int tt) {
        logger.info("Begin Pixels Freshness Workload");
        taskType = tt;
        res.setStartTS(dateFormat.format(new Date()));
        String tpClient = ConfigLoader.prop.getProperty("tpclient");

        List<Client> tasks = new ArrayList<Client>();
        if (Integer.parseInt(tpClient) > 0) {
            Client job = Client.initTask(ConfigLoader.prop, "TPClient", taskType);
            job.setRet(res);
            job.setVerbose(verbose);
            job.setSqls(sqls);
            tasks.add(job);
        } else {
            logger.warn("There is no an available tp client");
            return;
        }
        taskType = 9;

        ExecutorService es = Executors.newFixedThreadPool(tasks.size());
        List<Future> future = new ArrayList<Future>();
        for (final Client j : tasks) {
            future.add(es.submit(new Runnable() {
                        public void run() {
                            j.startTask();
                        }
                    })
            );
        }

        Thread freshness = null;
        final long startTs = System.currentTimeMillis();
        final int _duration = Integer.parseInt(ConfigLoader.prop.getProperty("pixelsFreshRunMins"));
        final int _fresh_warmup = Integer.parseInt(ConfigLoader.prop.getProperty("pixelsFreshWarmupSeconds", String.valueOf(0)));
        final int _fresh_interval = Integer.parseInt(ConfigLoader.prop.getProperty("fresh_interval", String.valueOf(20)));
        String db = ConfigLoader.prop.getProperty("db");
        int dbType = Constant.getDbType(db);
        freshness = new Thread() {
                public void run() {
                    Connection conn_tp = ConnectionMgr.getConnection(0);
                    Connection conn_trino = ConnectionMgr.getConnection(2);
                    double sum = 0;
                    int cnt = 0;
                    long duration = _duration * 60 * 1000L;
                    long elpased_time = 0L;
                    boolean hasGetFreshness = false;

                    try {
                        logger.info("Begin Freshness Warmup, Sleep {} min", _fresh_warmup);
                        Thread.sleep((long) _fresh_warmup * 1000);
                    } catch (InterruptedException e) {
                        logger.info("warm up was stopped in force");
                        return;
                    }

                    for (int i = 0; i < _fresh_interval; i++) {
                        try {
                            Thread.sleep((long) _duration * 60 * 1000 / _fresh_interval);
                            elpased_time += (long) _duration * 60 * 1000 / _fresh_interval;
                            PixelsFreshness fresh = new PixelsFreshness(conn_trino, sqls, startTs);
                            long freshness = fresh.calcFreshness();
                            if (freshness == 2147483647) {
                                continue;
                            }
                            res.getHist().getFreshness().addValue(freshness);
                            sum += freshness;
                            cnt++;
                            res.setFresh(sum / cnt);
                            hasGetFreshness = true;
                        } catch (InterruptedException e) {
                            logger.info("Freshness checker was stopped in force");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    if (!hasGetFreshness) {
                        res.setFresh(2147483647);
                    }
                    try {
                        if (conn_trino != null) {
                            conn_trino.close();
                        }
                        if (conn_tp != null) {
                            conn_tp.close();
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
            }
        };
        freshness.start();


        for (int flength = 0; flength < future.size(); flength++) {
            Future f = future.get(flength);
            if (f != null && !f.isCancelled() && !f.isDone()) {
                try {
                    f.get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        }


        if (freshness != null) {
            freshness.interrupt();
        }

        if (!es.isShutdown() || !es.isTerminated()) {
            es.shutdownNow();
        }
        res.setEndTs(dateFormat.format(new Date()));
        logger.info("Pixels Freshness Workload is done.");
    }

    public void runInitFreshness() {
        Connection conn_tp = ConnectionMgr.getConnection(0);
        logger.info("Begin Init Freshness Table");
        taskType = 8;
        res.setStartTS(dateFormat.format(new Date()));

        String tpClient = ConfigLoader.prop.getProperty("tpclient");
        int tpClientNum = Integer.parseInt(tpClient);
        res.setTpclient(tpClientNum);

        if (tpClientNum <= 0) {
            logger.warn("There is no an available tp client");
            return;
        }

        InitFreshness initFreshness = new InitFreshness(conn_tp, sqls, tpClientNum);
        initFreshness.populateFreshness();
        res.setEndTs(dateFormat.format(new Date()));
    }
}

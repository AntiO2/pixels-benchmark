package io.pixelsdb.benchmark.workload;
/**
 * @time 2023-03-04
 * @version 1.0.0
 * @file Client.java
 * @description abstract class Client
 **/


import io.pixelsdb.benchmark.ConfigLoader;
import io.pixelsdb.benchmark.Constant;
import io.pixelsdb.benchmark.load.ConfigReader;
import io.pixelsdb.benchmark.stats.Histogram;
import io.pixelsdb.benchmark.stats.Result;
import io.pixelsdb.benchmark.util.RandomGenerator;
import io.pixelsdb.benchmark.util.TxRecorder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public abstract class Client {
    public static Logger logger = LogManager.getLogger(Client.class);
    static long tpTotalCount = 0L;
    static long apTotalCount = 0L;
    static long atTotalCount = 0L;
    static long iqTotalCount = 0L;
    static long apTotalTime = 0L;
    static int testid1 = 1;
    static int testid2 = 300001;
    static int testid3 = 300001;
    static HashMap<Integer, Long> delete_map1 = new HashMap<Integer, Long>();
    static HashMap<Integer, Long> delete_map2 = new HashMap<Integer, Long>();
    static ArrayBlockingQueue<Integer> queue_ids = null;
    protected boolean exitFlag = false;
    protected Properties prop = null;
    protected int taskType = 0; // 0 : xp ,1: tp,2 : ap , 3:xp ,4: htap
    CompletionService<ClientResult> cs = null;
    Future<ClientResult>[] fs = null;
    int dbType = 0;
    Sqlstmts sqls = null;
    int threads = 0;
    Lock lock = new ReentrantLock();
    ConfigReader CR = null;
    Result ret = null;
    Histogram hist = null;
    boolean verbose = true;
    int round = 1;
    List<Integer> Related_Blocked_Transfer_ids = null;
    List<Integer> Related_Blocked_Checking_ids = null;
    RandomGenerator rg = new RandomGenerator();
    double risk_rate = 0;
    ExecutorService es = null;//Executors.newFixedThreadPool(5);
    private int testDuration = 0;
    private String clientName = "";

    public static Client initTask(Properties cfg, String name, int taskType) {
        Client client = null;
        try {
            client = (Client) Class.forName("io.pixelsdb.benchmark.workload." + name).getDeclaredConstructor().newInstance();
            client.setClientName(name);
            client.setTask_prop(cfg);
            client.setTaskType(taskType);
            client.doInit_wrapper(name);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return client;
    }

    public void setTestid1(int random_num) {
        testid1 = random_num;
    }

    public void setTestid2(int random_num) {
        testid2 = random_num;
    }

    public void setTestid3(int random_num) {
        testid3 = random_num;
    }

    public boolean isVerbose() {
        return verbose;
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    public void setSqls(Sqlstmts sqls) {
        this.sqls = sqls;
    }

    public Result getRet() {
        return ret;
    }

    public void setRet(Result ret) {
        this.ret = ret;
    }

    public void setDbType(int dbType) {
        this.dbType = dbType;
    }

    public int getTestTime() {
        return testDuration;
    }

    public void setTestTime(int time) {
        testDuration = time;
    }

    public int getTaskType() {
        return taskType;
    }

    public void setTaskType(int type) {
        this.taskType = type;
    }

    public String getClientName() {
        return this.clientName;
    }

    public void setClientName(String name) {
        this.clientName = name;
    }

    protected void setTask_prop(Properties prop) {
        this.prop = prop;
    }

    // parameter handler including string value , int value and bool value
    protected String strParameter(String paraName) {
        return prop.containsKey(paraName) ? prop
                .getProperty(paraName) : null;
    }

    protected String strParameter(String paraName, String defaultValue) {
        return prop.containsKey(paraName) ? prop
                .getProperty(paraName) : defaultValue;
    }

    protected int intParameter(String paraName, int defaultValue) {
        String v = prop
                .getProperty(paraName, String.valueOf(defaultValue));
        return Integer.parseInt(v);
    }

    protected int intParameter(String paraName) {

        return intParameter(paraName, 0);
    }

    protected boolean boolParameter(String paraName, boolean defaultValue) {
        if (prop.containsKey(paraName)) {
            return "true".equalsIgnoreCase(prop.getProperty(paraName));
        }
        return defaultValue;
    }

    protected boolean boolParameter(String paraName) {
        return boolParameter(paraName, false);
    }

    // get db type, only mysql/oracle/pg supported
    public int getDbType(String db) {

        if (db.equalsIgnoreCase("postgreSQL")) {
            return Constant.DB_PG;
        } else if (db.equalsIgnoreCase("mysql")) {
            return Constant.DB_MYSQL;
        } else if (db.equalsIgnoreCase("oracle")) {
            return Constant.DB_ORACLE;
        } else {
            return Constant.DB_UNKNOW;
        }
    }

    // get data size and create thread pool according to client number
    public void doInit_wrapper(String clientName) {
        if (taskType == 0) {
            threads = intParameter("xtpclient");
        } else if (taskType == 4) {
            threads = intParameter("xtpclient") + 1;
        } else if (taskType == 1) {
            threads = intParameter("tpclient");
        }  else if (taskType == 9) {
            threads = intParameter("tpclient"); // TODO(AntiO2): add lakehouse thread?
            TxRecorder.getInstance().init(threads);
        }
        CR = new ConfigReader(strParameter("sf", "1x"));
        String db = strParameter("db");
        setDbType(getDbType(db));
        if (clientName.equals("APClient")) {
            if (taskType == 0 || taskType == 4) {
                threads = intParameter("xapclient");
            } else if (taskType == 7) {
                threads = intParameter("apclient");
            } else if (taskType == 2) {
                threads = 1;
            } else if (taskType == 9) {
                // TODO(AntiO2): add trino query
            } else {
                threads = 0;
            }
        }

        if (threads > 0) {
            es = Executors.newFixedThreadPool(threads);
            cs = new ExecutorCompletionService<ClientResult>(es);
        }
        int contention_num = intParameter("contention_num", 100);
        risk_rate = Double.valueOf(ConfigLoader.prop.getProperty("risk_rate", "0.1"));
        queue_ids = new ArrayBlockingQueue<Integer>(contention_num);
        //set test id
        Long customernumer = CR.customer_number;
        Long companynumber = CR.company_number;
        int customer_no = customernumer.intValue() + 1;
        int company_no = companynumber.intValue();

        int random_num1 = rg.getRandomint(1, customer_no + company_no);
        setTestid1(random_num1);

        int random_num2 = rg.getRandomint(customer_no, customer_no + company_no);
        setTestid2(random_num2);

        int random_num3 = rg.getRandomint(customer_no, customer_no + company_no);
        setTestid3(random_num3);

        if(taskType != 9) {
            try {
                // load the blocking-related transfer accounts
                String DataPath = "Data_" + ConfigLoader.prop.getProperty("sf");
                FileInputStream fi1 = new FileInputStream(new File(DataPath + "/Related_transfer_bids"));
                ObjectInputStream oi1 = new ObjectInputStream(fi1);
                // Read objects
                Related_Blocked_Transfer_ids = (List<Integer>) oi1.readObject();
                oi1.close();
                fi1.close();

                // load the blocking-related checking accounts
                FileInputStream fi2 = new FileInputStream(new File(DataPath + "/Related_checking_bids"));
                ObjectInputStream oi2 = new ObjectInputStream(fi2);
                // Read objects
                Related_Blocked_Checking_ids = (List<Integer>) oi2.readObject();
                oi2.close();
                fi2.close();

            } catch (FileNotFoundException e) {
                logger.error("File not found");
            } catch (IOException | ClassNotFoundException e) {
                logger.error("Error initializing stream");
            }
        }


        doInit();
    }

    // client work from here. A new thread named timer to output response time histogram every 1/10 duration
    public void startTask() {
        int testTime = 0;
        ClientResult _res = null;
        Thread timer = null;
        String db = strParameter("db");
        setDbType(getDbType(db));
        if (ret != null) {
            hist = ret.getHist();
        }

        if (clientName.equalsIgnoreCase("APClient")) {
            if (taskType == 0 || taskType == 4)
                ret.setXapclient(threads);
            else
                ret.setApclient(threads);
        }

        if (clientName.equalsIgnoreCase("tpclient")) {
            if (taskType == 0 || taskType == 4) {
                ret.setXtpclient(threads);
            } else if (taskType == 9) {
                ret.setTpclient(threads);
                ret.setApclient(0); // TODO
            } else
                ret.setTpclient(threads);
        }


        ret.setRiskRate(String.valueOf(risk_rate));


        final int _fresh_interval = intParameter("fresh_interval", 20);

        round = intParameter("apround", 1);

        if (taskType == 7) {
            testTime = intParameter("apRunMins");
        } else if (taskType == 1) {
            testTime = intParameter("tpRunMins");
        } else if (taskType == 0 || taskType == 4) {
            testTime = intParameter("xpRunMins");
        } else if (taskType == 9) {
            testTime = intParameter("pixelsFreshRunMins");
        }
        final int _duration = testTime;
        if (taskType == 2) {
            logger.info("Begin to run :" + clientName + ", Test is " + round + " round");
        } else {
            logger.info("Begin to run :" + clientName + ", Test Duration is " + _duration + " mins");
        }

        setTestTime(testTime);
        if (_duration > 0) {
            timer = new Thread() {
                public void run() {
                    try {
                        long duration = _duration * 60 * 1000L;
                        long elpased_time = 0L;
                        for (int i = 0; i < 10; i++) {
                            Thread.sleep(_duration * 60 * 100L);
                            elpased_time += _duration * 60 * 100L;
                            if (verbose) {
                                if (clientName.equalsIgnoreCase("APClient")) {
                                    for (int apidx = 0; apidx < 13; apidx++) {
                                        if (hist.getAPItem(apidx).getN() == 0)
                                            continue;
                                        logger.info("Query " + (apidx + 1)
                                                + " : max rt : " + hist.getAPItem(apidx).getMax()
                                                + " | min rt :" + hist.getAPItem(apidx).getMin()
                                                + " | avg rt : " + String.format("%.2f", hist.getAPItem(apidx).getMean())
                                                + " | 95% rt : " + String.format("%.2f", hist.getAPItem(apidx).getPercentile(95))
                                                + " | 99% rt : " + String.format("%.2f", hist.getAPItem(apidx).getPercentile(99)));
                                    }
                                    if (taskType == 4 || taskType == 0)
                                        logger.info("Current " + (i + 1) + "/10 time AP QPS is " + String.format("%.2f", iqTotalCount / (elpased_time / 1000.0)));
                                    else
                                        logger.info("Current " + (i + 1) + "/10 time AP QPS is " + String.format("%.2f", apTotalCount / (elpased_time / 1000.0)));
                                }
                                if (clientName.equalsIgnoreCase("TPClient")) {
                                    for (int tpidx = 0; tpidx < 18; tpidx++) {
                                        if (hist.getTPItem(tpidx).getN() == 0)
                                            continue;
                                        logger.info("Transaction " + (tpidx + 1)
                                                + " : max rt : " + hist.getTPItem(tpidx).getMax()
                                                + " | min rt :" + hist.getTPItem(tpidx).getMin()
                                                + " | avg rt : " + String.format("%.2f", hist.getTPItem(tpidx).getMean())
                                                + " | 95% rt : " + String.format("%.2f", hist.getTPItem(tpidx).getPercentile(95))
                                                + " | 99% rt : " + String.format("%.2f", hist.getTPItem(tpidx).getPercentile(99)));
                                    }
                                    if (taskType == 4 || taskType == 0)
                                        logger.info("Current " + (i + 1) + "/10 time TP TPS is " + String.format("%.2f", atTotalCount / (elpased_time / 1000.0)));
                                    else
                                        logger.info("Current " + (i + 1) + "/10 time TP TPS is " + String.format("%.2f", tpTotalCount / (elpased_time / 1000.0)));
                                }
                            }

                        }

                        stopTask();

                    } catch (InterruptedException e) {
                        logger.warn("Test Duration Timer was stopped in force");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            timer.start();

        }
        int _num_thread = threads;

        fs = new Future[_num_thread];
        for (int i = 1; i <= _num_thread; i++) {
            final String threadId = "T" + i;
            Callable<ClientResult> r = new Callable<ClientResult>() {

                public ClientResult call() throws Exception {
                    // TODO Auto-generated method stub
                    Thread.currentThread().setName(threadId);
                    ClientResult result = execute();
                    return result;
                }
            };
            try {
                fs[i - 1] = cs.submit(r);
            } catch (Exception e) {
                logger.error("create thread failed ", e);
            }

        }

        double maxElapsedTime = 0L;
        for (int i = 0; i < _num_thread; i++) {
            try {
                fs[i] = cs.take();
                if (fs[i] != null && !fs[i].isCancelled() && fs[i].isDone()) {
                    _res = fs[i].get();
                    if (_res.getRt() > maxElapsedTime)
                        maxElapsedTime = _res.getRt();
                }
            } catch (Exception e) {
                logger.error("Waiting for load worker", e);
            }
        }

        if (clientName.equalsIgnoreCase("APClient")) {
            if (taskType == 2) {
                ret.setApRound(round);
                ret.setApTotal(apTotalCount);
                ret.setQps(Double.valueOf(String.format("%.2f", apTotalCount / (apTotalTime / 1000.0))));
            } else if (taskType == 0 || taskType == 4) {
                ret.setIqTotal(iqTotalCount);
                ret.setXpqps(Double.valueOf(String.format("%.2f", iqTotalCount / (testDuration * 60.0))));
            } else if (taskType == 7) {
                ret.setApTotal(apTotalCount);
                ret.setApRound(_res.getApRound());
                ret.setQps(Double.valueOf(String.format("%.2f", apTotalCount / (maxElapsedTime / 1000.0))));
            }
        }

        if (clientName.equalsIgnoreCase("TPClient")) {

            if (taskType == 1) {
                ret.setTpTotal(tpTotalCount);
                ret.setTps(Double.valueOf(String.format("%.2f", tpTotalCount / (testDuration * 60.0))));
            } else if (taskType == 0 || taskType == 4) {
                ret.setAtTotal(atTotalCount);
                ret.setXptps(Double.valueOf(String.format("%.2f", atTotalCount / (testDuration * 60.0))));
            }
        }

        if (timer != null) {
            timer.interrupt();
        }

        if (!es.isShutdown() || !es.isTerminated()) {
            es.shutdownNow();
        }
        logger.info("Finished to execute " + clientName);
    }

    public abstract void doInit();

    public abstract ClientResult execute();

    public void stopTask() {
        exitFlag = true;
        int p = 0;
        if (taskType != 7) {
            if (fs != null) {
                for (Future ft : fs) {
                    ft.cancel(true);
                }
            }
        }
    }

    public ArrayList<Integer> getRandomList(int minValue, int maxValue) {
        ArrayList<Integer> rList = new ArrayList<Integer>();
        LinkedList<Integer> source = new LinkedList<Integer>();
        for (int i = minValue; i <= maxValue; i++) {
            source.add(i);
        }
        while (source.size() > 0) {
            int idx = ThreadLocalRandom.current().nextInt(source.size());
            rList.add(source.get(idx));
            source.remove(idx);
        }
        return rList;
    }
}

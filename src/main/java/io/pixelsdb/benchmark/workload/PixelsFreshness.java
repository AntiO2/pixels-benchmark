/*
 * Copyright 2025 PixelsDB.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */


package io.pixelsdb.benchmark.workload;


import io.pixelsdb.benchmark.ConfigLoader;
import io.pixelsdb.benchmark.dbconn.ConnectionMgr;
import io.pixelsdb.benchmark.util.TxRecorder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @package: io.pixelsdb.benchmark.workload
 * @className: PixelsFreshness
 * @author: AntiO2
 * @date: 2025/7/23 12:28
 */
public class PixelsFreshness extends Client
{
    public static final long INVALID_FRESHNESS = 2147483647;
    public static Logger logger = LogManager.getLogger(PixelsFreshness.class);
    int dbType;
    long startTime;
    long curfreshness = 0;
    private TxRecorder txRecorder;
    private Connection conn_trino;

    public Long calcFreshness()
    {
        String query = sqls.fresh_query();

        long maxFreshness = 0;

        try
        {
            PreparedStatement stmt = conn_trino.prepareStatement(query);
            long start_ts = System.currentTimeMillis();
            ResultSet rs = stmt.executeQuery();
            while (rs.next())
            {
                int txnId = rs.getInt(1);
                int clientId = rs.getInt(2);
                long fresh = txRecorder.getFirstUnseenTxn(start_ts, txnId, clientId);
                logger.info("Fresh Query: Cli {}\tTxnID {}\tFresh {}", clientId, txnId, fresh);
                maxFreshness = Math.max(maxFreshness, fresh);
            }

        } catch (SQLException e)
        {
            logger.error("Error executing freshness query", e);
            return INVALID_FRESHNESS;
        }

        this.curfreshness = maxFreshness;
        return maxFreshness;
    }

    @Override
    public void doInit()
    {
        this.startTime = System.currentTimeMillis();
        this.conn_trino = ConnectionMgr.getConnection(2);
        this.txRecorder = TxRecorder.getInstance();
    }

    @Override
    public ClientResult execute()
    {
        ClientResult clientResult = new ClientResult();
        final int _fresh_warmup = Integer.parseInt(ConfigLoader.prop.getProperty("pixelsFreshWarmupSeconds", String.valueOf(0)));
        final int _fresh_interval = Integer.parseInt(ConfigLoader.prop.getProperty("fresh_interval", String.valueOf(200)));
        try
        {
            logger.info("Begin Freshness Warmup, Sleep {} seconds", _fresh_warmup);
            Thread.sleep((long) _fresh_warmup * 1000);
        } catch (InterruptedException e)
        {
            logger.info("warm up was stopped in force");
            return clientResult;
        }
        while (!exitFlag)
        {

            double sum = 0;
            int cnt = 0;
            long elpased_time = 0L;
            boolean hasGetFreshness = false;
            final long startTs = System.currentTimeMillis();
            for (int i = 0; i < _fresh_interval; i++)
            {
                try
                {
                    Thread.sleep(_fresh_interval);
                    elpased_time += _fresh_interval;
                    long freshness = calcFreshness();
                    if (freshness == INVALID_FRESHNESS)
                    {
                        continue;
                    }

                    ret.getHist().getFreshness().addValue(freshness);
                    sum += freshness;
                    cnt++;
                    ret.setFresh(sum / cnt);
                    hasGetFreshness = true;
                } catch (InterruptedException e)
                {
                    logger.info("Freshness checker was stopped in force");
                    break;
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
            if (!hasGetFreshness)
            {
                ret.setFresh(2147483647);
            }
            try
            {
                if (conn_trino != null)
                {
                    conn_trino.close();
                }
            } catch (SQLException e)
            {
                e.printStackTrace();
            }
        }
        return clientResult;
    }
}

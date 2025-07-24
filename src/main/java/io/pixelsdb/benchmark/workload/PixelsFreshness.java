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


import io.pixelsdb.benchmark.pojo.Freshness;
import io.pixelsdb.benchmark.util.TxRecorder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * @package: io.pixelsdb.benchmark.workload
 * @className: PixelsFreshness
 * @author: AntiO2
 * @date: 2025/7/23 12:28
 */
public class PixelsFreshness {
    public static Logger logger = LogManager.getLogger(PixelsFreshness.class);
    int dbType;
    long startTime;
    long curfreshness = 0;
    private final Sqlstmts sqls;
    private final Connection conn;
    private final TxRecorder txRecorder;

    public PixelsFreshness(Connection connAp, Sqlstmts sqls, long startTime) {
        this.sqls = sqls;
        this.conn = connAp;
        this.startTime = startTime;
        this.txRecorder = TxRecorder.getInstance();
    }

    public Long calcFreshness() {
        String query = sqls.fresh_query();

        long maxFreshness = 0;

        try {
             PreparedStatement stmt = conn.prepareStatement(query);
             long start_ts = System.currentTimeMillis();
             ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int txnId = rs.getInt(1);
                int clientId = rs.getInt(2);
                long fresh = txRecorder.getFirstUnseenTxn(start_ts, txnId, clientId);
                logger.info("Fresh Query: Cli {}\tTxnID {}\tFresh {}", clientId, txnId, fresh);
                maxFreshness = Math.max( maxFreshness, fresh);
            }

        } catch (SQLException e) {
            logger.error("Error executing freshness query", e);
            return 0L;
        }

        this.curfreshness = maxFreshness;
        return maxFreshness;
    }
}

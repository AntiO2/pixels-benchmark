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


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @package: io.pixelsdb.benchmark.workload
 * @className: InitFreshness
 * @author: AntiO2
 * @date: 2025/7/23 12:28
 */
public class InitFreshness {

    private final Sqlstmts sqls;
    private final Connection conn;
    private final int tpClient;
    public InitFreshness(Connection connTp, Sqlstmts sqls, int tpClientNum) {
        this.sqls = sqls;
        this.conn = connTp;
        this.tpClient = tpClientNum;
    }


    public int populateFreshness() {
        int cnt = 0;
        PreparedStatement pstmt = null;
        try {
            long currentStarttTs = System.currentTimeMillis();
            // transaction begins
            conn.setAutoCommit(false);
            for(int cli_id = 0; cli_id < tpClient; cli_id++) {
                pstmt = conn.prepareStatement(sqls.fresh_init());
                pstmt.setInt(1, cli_id);
                int i = pstmt.executeUpdate();
                cnt += i;
            }

            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return cnt;
    }
}

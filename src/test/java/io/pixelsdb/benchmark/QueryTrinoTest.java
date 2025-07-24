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


package io.pixelsdb.benchmark;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;

/**
 *
 * @package: io.pixelsdb.benchmark
 * @className: QueryTrinoTest
 * @author: AntiO2
 * @date: 2025/7/24 02:07
 */
public class QueryTrinoTest {
    private static final String JDBC_URL = "jdbc:trino://localhost:18081/paimon/test_simple";
    private static final String TBL_NAME = "test_tbl";

    @Test
    public void testTrinoQuery() {
        String query = "SELECT * FROM " + TBL_NAME;  // 可替换为 SELECT * FROM your_table LIMIT 10;

        Properties properties = new Properties();
        properties.setProperty("user", "test");

        try (Connection conn = DriverManager.getConnection(JDBC_URL, properties)) {

            PreparedStatement pstmt = conn.prepareStatement(query);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                int result = rs.getInt(1);
                System.out.println("Query result: " + result);
            }

        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail("Query failed: " + e.getMessage());
        }
    }
}

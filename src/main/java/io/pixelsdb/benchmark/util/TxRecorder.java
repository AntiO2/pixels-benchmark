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


package io.pixelsdb.benchmark.util;


import java.util.ArrayList;
import java.util.List;

/**
 * @package: io.pixelsdb.benchmark.util
 * @className: TxRecorder
 * @author: AntiO2
 * @date: 2025/7/23 13:36
 */
import java.util.ArrayList;
import java.util.List;

public class TxRecorder {
    private static final TxRecorder INSTANCE = new TxRecorder();

    private final List<TxLinkedList> containers = new ArrayList<>();
    private int[] currentTxIds;

    private TxRecorder() {}

    public static TxRecorder getInstance() {
        return INSTANCE;
    }

    public synchronized void init(int clientCount) {
        containers.clear();
        for (int i = 0; i < clientCount; i++) {
            containers.add(new TxLinkedList());
        }
        currentTxIds = new int[clientCount];
    }

    public void insert(int cliId, long currentStartTs) {
        int txid = currentTxIds[cliId];
        containers.get(cliId).insert(txid, currentStartTs);
    }

    public int getCurrentTxid(int cliId) {
        return currentTxIds[cliId];
    }

    public void incrementTxid(int cliId) {
        currentTxIds[cliId]++;
    }
}

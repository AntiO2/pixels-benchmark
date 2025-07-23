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


/**
 * @package: io.pixelsdb.benchmark.util
 * @className: TxTxLinkedList
 * @author: AntiO2
 * @date: 2025/7/23 13:40
 */
public class TxLinkedList {
    private final TxNode head;
    private final TxNode tail;
    private long firstUnseen = 0;

    public TxLinkedList() {
        int dh = 500;
        long cth = Long.MAX_VALUE;
        int dt = -1;
        long ctt = -1;
        head = new TxNode(dh, cth);
        tail = new TxNode(dt, ctt);
        head.next.set(tail);
    }

    public void insert(int d, long ct) {
        TxNode newTxNode = new TxNode(d, ct);
        while (true) {
            TxNode[] leftRight = search(ct, d);
            TxNode left = leftRight[0];
            TxNode right = leftRight[1];

            newTxNode.next.set(right);
            if (left.next.compareAndSet(right, newTxNode)) {
                break;
            }
        }
    }

    private TxNode[] search(long ct, int d) {
        TxNode left = null, right;
        TxNode leftNext = null;

        while (true) {
            TxNode t = head;
            TxNode tNext = t.next.get();

            while (t.commitTime > ct) {
                left = t;
                leftNext = tNext;
                t = tNext;
                if (t == tail) break;
                tNext = t.next.get();
            }
            right = t;
            if (leftNext == right) {
                return new TxNode[]{left, right};
            }
        }
    }

    public void printList(int client) {
        TxNode t = head;
        TxNode tNext = head.next.get();
        while (true) {
            System.out.println("client: " + client + " txnNum: " + t.data + ", Commit Time: " + t.commitTime);
            if (t == tail) break;
            t = tNext;
            tNext = t.next.get();
        }
    }

    public long getFirstUnseenTxn(long startTime, int txnNum) {
        TxNode t = head;
        TxNode tNext = head.next.get();
        int foundUnseen = 0;
        long ct = -1;
        int tn = -1;
        int option = 0;

        while (true) {
            if (t == tail || (t == head && tNext == tail)) {
                if (foundUnseen == 1) break;
                else {
                    firstUnseen = 0;
                    break;
                }
            } else if (t == head) {
                t = tNext;
                tNext = t.next.get();
            } else if (t.commitTime >= startTime) {
                if (t.data == txnNum) {
                    firstUnseen = 0;
                    option = 1;
                    ct = t.commitTime;
                    tn = t.data;
                    break;
                } else {
                    t = tNext;
                    tNext = t.next.get();
                }
            } else if (t.commitTime < startTime) {
                if (t.data > txnNum) {
                    firstUnseen = (startTime - t.commitTime);
                    ct = t.commitTime;
                    tn = t.data;
                    t = tNext;
                    tNext = t.next.get();
                    foundUnseen = 1;
                } else if (t.data == txnNum) {
                    if (foundUnseen == 1) {
                        option = 2;
                        break;
                    } else {
                        firstUnseen = 0;
                        ct = t.commitTime;
                        tn = t.data;
                        option = 3;
                        break;
                    }
                } else if (t.data < txnNum) {
                    firstUnseen = 0;
                    ct = t.commitTime;
                    tn = t.data;
                    option = 4;
                    break;
                }
            }
        }

        return firstUnseen;
    }
}
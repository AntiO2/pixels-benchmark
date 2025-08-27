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


import java.util.concurrent.atomic.AtomicReference;

/**
 * @package: io.pixelsdb.benchmark.util
 * @className: TxNode
 * @author: AntiO2
 * @date: 2025/7/23 13:39
 */
public class TxNode
{
    public int data;
    public long commitTime;
    public AtomicReference<TxNode> next;

    public TxNode(int data, long commitTime)
    {
        this.data = data;
        this.commitTime = commitTime;
        this.next = new AtomicReference<>(null);
    }
}
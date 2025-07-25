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

package io.pixelsdb.benchmark.pojo;


import java.util.StringJoiner;

/**
 * @package: io.pixelsdb.pojo
 * @className: Freshness
 * @author: AntiO2
 * @date: 2025/7/23 12:03
 */
public record Freshness(
        int f_tx_id,
        int f_cli_id
) {
    @Override
    public String toString() {
        return new StringJoiner(",")
                .add(Integer.toString(f_tx_id))
                .add(Integer.toString(f_cli_id))
                .toString();
    }
}

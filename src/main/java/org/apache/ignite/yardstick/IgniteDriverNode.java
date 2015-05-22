/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.ignite.yardstick;

import org.apache.ignite.*;
import org.apache.ignite.cache.eviction.lru.*;
import org.apache.ignite.configuration.*;
import org.apache.ignite.spi.communication.tcp.*;
import org.apache.ignite.spi.discovery.tcp.*;
import org.yardstickframework.*;

import static org.apache.ignite.cache.CacheMemoryMode.*;

/**
 * Standalone Ignite node.
 */
public class IgniteDriverNode extends IgniteNode {
    /** Client mode. */
    private boolean clientMode;

    /** */
    public IgniteDriverNode(boolean clientMode) {
        this.clientMode = clientMode;
    }

    /** */
    public IgniteDriverNode(boolean clientMode, Ignite ignite) {
        this.clientMode = clientMode;
        this.ignite = ignite;
    }

    /** {@inheritDoc} */
    @Override public void start(BenchmarkConfiguration cfg) throws Exception {
        IgniteBenchmarkArguments args = new IgniteBenchmarkArguments();

        BenchmarkUtils.jcommander(cfg.commandLineArguments(), args, "<ignite-node>");

        IgniteConfiguration c = loadConfiguration(args.configuration());

        assert c != null;

        c.setClientMode(true);

        CacheConfiguration ccfg = null;

        for (CacheConfiguration cc : c.getCacheConfiguration()) {
            // Create cache only
            if (!cc.getName().equals(args.cacheName()))
                continue;

            //cc.setAffinity(new FairAffinityFunction());

            cc.setWriteSynchronizationMode(args.syncMode());

            if (args.orderMode() != null)
                cc.setAtomicWriteOrderMode(args.orderMode());

            cc.setBackups(args.backups());

            if (args.restTcpPort() != 0) {
                ConnectorConfiguration ccc = new ConnectorConfiguration();

                ccc.setPort(args.restTcpPort());

                if (args.restTcpHost() != null)
                    ccc.setHost(args.restTcpHost());

                c.setConnectorConfiguration(ccc);
            }

            if (args.isOffHeap()) {
                cc.setOffHeapMaxMemory(0);

                if (args.isOffheapValues())
                    cc.setMemoryMode(OFFHEAP_VALUES);
                else
                    cc.setEvictionPolicy(new LruEvictionPolicy(50000));
            }

            cc.setWriteBehindEnabled(args.isWriteBehind());

            cc.setReadFromBackup(true);

            ccfg = cc;

            break;
        }

        c.setCacheConfiguration(ccfg);

        TransactionConfiguration tc = c.getTransactionConfiguration();

        tc.setDefaultTxConcurrency(args.txConcurrency());
        tc.setDefaultTxIsolation(args.txIsolation());

        TcpCommunicationSpi commSpi = (TcpCommunicationSpi)c.getCommunicationSpi();

        if (commSpi == null)
            commSpi = new TcpCommunicationSpi();

        TcpDiscoverySpi spi = (TcpDiscoverySpi)c.getDiscoverySpi();

        commSpi.setSocketWriteTimeout(200);
//        spi.setAckTimeout(1000);
//        spi.setNetworkTimeout(5000);
//        spi.setHeartbeatFrequency(1000);
//        spi.setMaxMissedHeartbeats(5);
//        spi.setMaxMissedClientHeartbeats(5);

        ignite = Ignition.start(c);
    }
}

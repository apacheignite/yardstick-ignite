<!--
  Licensed to the Apache Software Foundation (ASF) under one or more
  contributor license agreements.  See the NOTICE file distributed with
  this work for additional information regarding copyright ownership.
  The ASF licenses this file to You under the Apache License, Version 2.0
  (the "License"); you may not use this file except in compliance with
  the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
-->

# Yardstick Apache Ignite Benchmarks
Yardstick Apache Ignite is a set of <a href="http://ignite.incubator.apache.org/">Ignite Grid</a> benchmarks written on top of Yardstick framework.

## Yardstick Framework
Visit <a href="https://github.com/gridgain/yardstick" target="_blank">Yardstick Repository</a> for detailed information on how to run Yardstick benchmarks and how to generate graphs.

The documentation below describes configuration parameters in addition to standard Yardstick parameters.

## Installation
1. Create a local clone of repository
2. Run `mvn package` command for Yardstick Apache Ignite POM

## Provided Benchmarks
The following benchmarks are provided:

1. `GetBenchmark` - benchmarks atomic distributed cache get operation
2. `PutBenchmark` - benchmarks atomic distributed cache put operation
3. `PutGetBenchmark` - benchmarks atomic distributed cache put and get operations together
4. `PutTxBenchmark` - benchmarks transactional distributed cache put operation
5. `PutGetTxBenchmark` - benchmarks transactional distributed cache put and get operations together
6. `SqlQueryBenchmark` - benchmarks distributed SQL query over cached data
7. `SqlQueryJoinBenchmark` - benchmarks distributed SQL query with a Join over cached data
8. `SqlQueryPutBenchmark` - benchmarks distributed SQL query with simultaneous cache updates

## Writing Apache Ignite Benchmarks
All benchmarks extend `AbstractBenchmark` class. A new benchmark should also extend this abstract class and implement `test` method. This is the method that is actually benchmarked.

## Running Apache Ignite Benchmarks
Before running Apache Ignite benchmarks, run `mvn package` command. This command will compile the project and also will unpack scripts from `yardstick-resources.zip` file to `bin` directory.

### Properties And Command Line Arguments
> Note that this section only describes configuration parameters specific to Apache Ignite benchmarks, and not for Yardstick framework. To run Apache Ignite benchmarks and generate graphs, you will need to run them using Yardstick framework scripts in `bin` folder.

> Refer to [Yardstick Documentation](https://github.com/gridgain/yardstick) for common Yardstick properties and command line arguments for running Yardstick scripts.

The following Apache Ignite benchmark properties can be defined in the benchmark configuration:

* `-nn <num>` or `--nodeNumber <num>` - Number of nodes (automatically set in `benchmark.properties`), used to wait for the specified number of nodes to start
* `-b <num>` or `--backups <num>` - Number of backups for every key
* `-cfg <path>` or `--Config <path>` - Path to Apache Ignite configuration file
* `-sm <mode>` or `-syncMode <mode>` - Synchronization mode (defined in `CacheWriteSynchronizationMode`)
* `-dm <mode>` or `--distroMode <mode>` - Distribution mode (defined in `CacheDistributionMode`)
* `-wom <mode>` or `--writeOrderMode <mode>` - Write order mode for ATOMIC caches (defined in `CacheAtomicWriteOrderMode`)
* `-txc <value>` or `--txConcurrency <value>` - Cache transaction concurrency control, either `OPTIMISTIC` or `PESSIMISTIC` (defined in `CacheTxConcurrency`)
* `-txi <value>` or `--txIsolation <value>` - Cache transaction isolation (defined in `CacheTxIsolation`)
* `-ot` or `--offheapTiered` - Flag indicating whether tiered off-heap mode is on
* `-ov` or `--offheapValuesOnly` - Flag indicating whether off-heap mode is on and only cache values are stored off-heap
* `-rtp <num>`  or `--restPort <num>` - REST TCP port, indicates that a Apache Ignite node is ready to process Apache Ignite Clients
* `-rth <host>` or `--restHost <host>` - REST TCP host
* `-ss` or `--syncSend` - Flag indicating whether synchronous send is used in `TcpCommunicationSpi`
* `-r <num>` or `--range` - Range of keys that are randomly generated for cache operations

For example if we need to run 2 `IgniteNode` servers on localhost with `PutBenchmark` benchmark on localhost, with number of backups set to 1, synchronization mode set to `PRIMARY_SYNC`, then the following configuration should be specified in `benchmark.properties` file:

```
SERVER_HOSTS=localhost,localhost
    
# Note that -dn and -sn, which stand for data node and server node, are 
# native Yardstick parameters and are documented in Yardstick framework.
CONFIGS="-b 1 -sm PRIMARY_SYNC -dn PutBenchmark -sn IgniteNode"
```

## Running on Amazon

This repo contains all necessary scripts and properties files for a comparison Apache Ignite with other products.
You can easy run benchmark by using [yardstick-docker](https://github.com/yardstick-benchmarks/yardstick-docker) extension, but it might have an influence on performance.

For running on Amazon EC2 need to perform the following steps:

* Run Amazon EC2 instances. Choose number of instances and hardware according to your requirements.

The following actions need to perform on all instances:

* Install Java, Maven and Git.
* Clone this repository (on all nodes path should be the same) and perform `mvn clean package` command.
* Change `SERVER_HOSTS` and `DRIVER_HOSTS` properties in `config/benchmark.properties` file. 
`SERVER_HOSTS` is comma-separated list of IP addresses where servers should be started, one server per host. 
`DRIVER_HOSTS` is comma-separated list of IP addresses where drivers should be started, one driver per host, if the 
property is not defined then the driver will be run on localhost.
Property file contains many useful information about benchmarks such as `list of benchmarks`, `JVM opts` and etc. More details there
[Properties And Command Line Arguments](https://github.com/gridgain/yardstick#properties-and-command-line-arguments)

* Update IP addresses in `property` section from `config/ignite-localhost-config.xml` file on actual. For example:

```
config/ignite-localhost-config.xml

...
<property name="addresses">
  <list>
    <value>XXX.XXX.XX.1:47500..47509</value>
    <value>XXX.XXX.XX.2:47500..47509</value>
    <value>XXX.XXX.XX.3:47500..47509</value>
  </list>
</property>
...
```

* Perform `./bin/benchmark-run-all.sh` script. For more details about running scripts see [Running Yardstick Benchmarks](https://github.com/gridgain/yardstick#running-yardstick-benchmarks).
* After execution the script in `result` folder will be saved to results of benchmarks. For visualisation of results can be used `bin/jfreechart-graph-gen.sh` script. 
For more details about the script see [JFreeChart Graphs](https://github.com/gridgain/yardstick#jfreechart-graphs).

## Issues
Use Apache Ignite Apache JIRA (https://issues.apache.org/jira/browse/IGNITE) to file bugs.

## License
Yardstick Apache Ignite is available under [Apache 2.0](http://www.apache.org/licenses/LICENSE-2.0.html) Open Source license.

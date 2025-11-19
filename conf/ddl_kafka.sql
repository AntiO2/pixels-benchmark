CREATE TABLE customer_kafka (
                                custid                INT,
                                companyid             INT,
                                gender                CHAR(6),
                                name                  CHAR(15),
                                age                   INT,
                                phone                 CHAR(11),
                                province              CHAR(15),
                                city                  CHAR(15),
                                loan_balance          FLOAT,
                                saving_credit         INT,
                                checking_credit       INT,
                                loan_credit           INT,
                                Isblocked             INT,
                                created_date          DATE,
                                last_update_timestamp BIGINT,
                                PRIMARY KEY (custid) NOT ENFORCED
) WITH (
      'connector' = 'kafka',
      'topic' = 'postgresql.oltp_server.pixels_bench_sf10x.customer',
      'properties.bootstrap.servers' = 'realtime-kafka-2:29092',
      'properties.group.id' = 'flink-pixels-customer',
      'format' = 'debezium-avro-confluent',
      'debezium-avro-confluent.url' = 'http://realtime-kafka-2:8081',
      'scan.startup.mode' = 'earliest-offset'
      );


CREATE TABLE company_kafka (
                               companyid             INT,
                               name                  VARCHAR,
                               category              VARCHAR,
                               staff_size            INT,
                               loan_balance          FLOAT,
                               phone                 CHAR(11),
                               province              CHAR(15),
                               city                  CHAR(15),
                               saving_credit         INT,
                               checking_credit       INT,
                               loan_credit           INT,
                               isblocked             INT,
                               created_date          DATE,
                               last_update_timestamp BIGINT,
                               PRIMARY KEY (companyid) NOT ENFORCED
) WITH (
      'connector' = 'kafka',
      'topic' = 'postgresql.oltp_server.pixels_bench_sf10x.company',
      'properties.bootstrap.servers' = 'realtime-kafka-2:29092',
      'properties.group.id' = 'flink-pixels-company',
      'format' = 'debezium-avro-confluent',
      'debezium-avro-confluent.url' = 'http://realtime-kafka-2:8081',
      'scan.startup.mode' = 'earliest-offset'
      );

CREATE TABLE savingaccount_kafka (
                                     accountid INT,
                                     userid    INT,
                                     balance   FLOAT,
                                     isblocked INT,
                                     ts BIGINT,
                                     PRIMARY KEY (accountid) NOT ENFORCED
) WITH (
      'connector' = 'kafka',
      'topic' = 'postgresql.oltp_server.pixels_bench_sf10x.savingaccount',
      'properties.bootstrap.servers' = 'realtime-kafka-2:29092',
      'properties.group.id' = 'flink-pixels-savingaccount',
      'format' = 'debezium-avro-confluent',
      'debezium-avro-confluent.url' = 'http://realtime-kafka-2:8081',
      'scan.startup.mode' = 'earliest-offset'
      );

CREATE TABLE checkingaccount_kafka (
                                       accountid INT,
                                       userid INT,
                                       balance FLOAT,
                                       isblocked INT,
                                       ts BIGINT,
                                       PRIMARY KEY (accountid) NOT ENFORCED
) WITH (
      'connector' = 'kafka',
      'topic' = 'postgresql.oltp_server.pixels_bench_sf10x.checkingaccount',
      'properties.bootstrap.servers' = 'realtime-kafka-2:29092',
      'properties.group.id' = 'flink-pixels-checkingaccount',
      'format' = 'debezium-avro-confluent',
      'debezium-avro-confluent.url' = 'http://realtime-kafka-2:8081',
      'scan.startup.mode' = 'earliest-offset'
      );

CREATE TABLE transfer_kafka (
                                id        INT,
                                sourceid  INT,
                                targetid  INT,
                                amount    FLOAT,
                                type      CHAR(10),
                                ts BIGINT,
                                PRIMARY KEY (id) NOT ENFORCED
) WITH (
      'connector' = 'kafka',
      'topic' = 'postgresql.oltp_server.pixels_bench_sf10x.transfer',
      'properties.bootstrap.servers' = 'realtime-kafka-2:29092',
      'properties.group.id' = 'flink-pixels-transfer',
      'format' = 'debezium-avro-confluent',
      'debezium-avro-confluent.url' = 'http://realtime-kafka-2:8081',
      'scan.startup.mode' = 'earliest-offset'
      );

CREATE TABLE checking_kafka (
                                id        INT,
                                sourceid  INT,
                                targetid  INT,
                                amount    FLOAT,
                                type      CHAR(10),
                                ts BIGINT,
                                PRIMARY KEY (id) NOT ENFORCED
) WITH (
      'connector' = 'kafka',
      'topic' = 'postgresql.oltp_server.pixels_bench_sf10x.checking',
      'properties.bootstrap.servers' = 'realtime-kafka-2:29092',
      'properties.group.id' = 'flink-pixels-checking',
      'format' = 'debezium-avro-confluent',
      'debezium-avro-confluent.url' = 'http://realtime-kafka-2:8081',
      'scan.startup.mode' = 'earliest-offset'
      );

CREATE TABLE loanapps_kafka (
                                id          INT,
                                applicantid INT,
                                amount      FLOAT,
                                duration    INT,
                                status      CHAR(12),
                                ts BIGINT,
                                PRIMARY KEY (id) NOT ENFORCED
) WITH (
      'connector' = 'kafka',
      'topic' = 'postgresql.oltp_server.pixels_bench_sf10x.loanapps',
      'properties.bootstrap.servers' = 'realtime-kafka-2:29092',
      'properties.group.id' = 'flink-pixels-loanapps',
      'format' = 'debezium-avro-confluent',
      'debezium-avro-confluent.url' = 'http://realtime-kafka-2:8081',
      'scan.startup.mode' = 'earliest-offset'
      );

CREATE TABLE loantrans_kafka (
                                 id                  INT,
                                 applicantid         INT,
                                 appid               INT,
                                 amount              FLOAT,
                                 status              CHAR(12),
                                 ts BIGINT,
                                 duration            INT,
                                 contract_timestamp  BIGINT,
                                 delinquency         INT,
                                 PRIMARY KEY (id) NOT ENFORCED
) WITH (
      'connector' = 'kafka',
      'topic' = 'postgresql.oltp_server.pixels_bench_sf10x.loantrans',
      'properties.bootstrap.servers' = 'realtime-kafka-2:29092',
      'properties.group.id' = 'flink-pixels-loantrans',
      'format' = 'debezium-avro-confluent',
      'debezium-avro-confluent.url' = 'http://realtime-kafka-2:8081',
      'scan.startup.mode' = 'earliest-offset'
      );

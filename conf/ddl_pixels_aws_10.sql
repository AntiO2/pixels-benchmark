CREATE TABLE IF NOT EXISTS freshness (
                                         f_tx_id int,
                                         f_cli_id int
) WITH (
      storage='s3',
      paths='s3://home-zinuo/hybench/sf10/freshness/',
      pk='f_cli_id'
      );

CREATE TABLE IF NOT EXISTS customer (
    custID int,
    companyID int,
    gender char(6),
    name char(15),
    age int,
    phone char(11),
    province char(15),
    city char(15),
    loan_balance real,
    saving_credit int,
    checking_credit int,
    loan_credit int,
    Isblocked int,
    created_date Date,
    last_update_timestamp timestamp
    ) WITH (
          storage='s3',
          paths='s3://home-zinuo/hybench/sf10/customer/',
          pk='custID',
          pk_scheme='rocksdb'
          );

CREATE TABLE IF NOT EXISTS company (
   companyID int,
   name varchar(60),
   category varchar(60),
   staff_size int,
   loan_balance real,
   phone char(11),
    province char(15),
    city char(15),
    saving_credit int,
    checking_credit int,
    loan_credit int,
    Isblocked int,
    created_date Date,
    last_update_timestamp timestamp
    ) WITH (
          storage='s3',
          paths='s3://home-zinuo/hybench/sf10/company/',
          pk='companyID',
          pk_scheme='rocksdb'
          );

CREATE TABLE IF NOT EXISTS savingAccount (
                                             accountID int,
                                             userID int,
                                             balance real,
                                             Isblocked int,
                                             ts timestamp
) WITH (
      storage='s3',
      paths='s3://home-zinuo/hybench/sf10/savingAccount/',
      pk='accountID',
      pk_scheme='rocksdb'
      );

CREATE TABLE IF NOT EXISTS checkingAccount (
                                               accountID int,
                                               userID int,
                                               balance real,
                                               Isblocked int,
                                               ts timestamp
) WITH (
      storage='s3',
      paths='s3://home-zinuo/hybench/sf10/checkingAccount/',
      pk='accountID',
      pk_scheme='rocksdb'
      );

CREATE TABLE IF NOT EXISTS transfer (
                                        id int,
                                        sourceID int,
                                        targetID int,
                                        amount real,
                                        type char(10),
                                        ts timestamp
    ) WITH (
          storage='s3',
          paths='s3://home-zinuo/hybench/sf10/transfer/',
          pk='id',
          pk_scheme='rocksdb'
          );

CREATE TABLE IF NOT EXISTS checking (
                                        id int,
                                        sourceID int,
                                        targetID int,
                                        amount real,
                                        type char(10),
                                        ts timestamp
    ) WITH (
          storage='s3',
          paths='s3://home-zinuo/hybench/sf10/checking/',
          pk='id',
          pk_scheme='rocksdb'
          );

CREATE TABLE IF NOT EXISTS loanapps (
                                        id int,
                                        applicantID int,
                                        amount real,
                                        duration int,
                                        status char(12),
                                        ts timestamp
    ) WITH (
          storage='s3',
          paths='s3://home-zinuo/hybench/sf10/loanapps/',
          pk='id',
          pk_scheme='rocksdb'
          );

CREATE TABLE IF NOT EXISTS loantrans (
                                         id int,
                                         applicantID int,
                                         appID int,
                                         amount real,
                                         status char(12),
                                        ts timestamp,
    duration int,
    contract_timestamp timestamp,
    delinquency int
    ) WITH (
          storage='s3',
          paths='s3://home-zinuo/hybench/sf10/loantrans/',
          pk='id',
          pk_scheme='rocksdb'
          );

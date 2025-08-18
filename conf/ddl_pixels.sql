CREATE TABLE IF NOT EXISTS freshness(
                                        f_tx_id integer,
                                        f_cli_id integer
) WITH (storage='file', paths='file:///tmp/data/pixels_bench_sf1x/freshness/'
    pk='f_cli_id');

CREATE TABLE customer (
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
) WITH (storage='file', paths='file:///tmp/data/pixels_bench_sf1x/customer/' );

CREATE TABLE company (
companyID int,
name varchar,
category varchar,
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
) WITH (storage='file', paths='file:///tmp/data/pixels_bench_sf1x/company/',
                           pk='companyID', pk_scheme='rocksdb' );


CREATE TABLE savingAccount (
                               accountID int,
                               userID int,
                               balance real,
                               Isblocked int,
                               timestamp timestamp,
                               fresh_ts timestamp default current_timestamp
)
    WITH (
        storage='file',
        paths='file:///tmp/data/pixels_bench_sf1x/savingAccount/',
        pk='accountID',
        pk_scheme='rocksdb'
        );

CREATE TABLE checkingAccount (
                                 accountID int,
                                 userID int,
                                 balance real,
                                 Isblocked int,
                                 timestamp timestamp
)
    WITH (
        storage='file',
        paths='file:///tmp/data/pixels_bench_sf1x/checkingAccount/',
        pk='accountID',
        pk_scheme='rocksdb'
        );

CREATE TABLE transfer (
                          id int,
                          sourceID int,
                          targetID int,
                          amount real,
                          type char(10),
                          timestamp timestamp,
                          fresh_ts timestamp default current_timestamp
)
    WITH (
        storage='file',
        paths='file:///tmp/data/pixels_bench_sf1x/transfer/',
        pk='id',
        pk_scheme='rocksdb'
        );

CREATE TABLE checking (
                          id int,
                          sourceID int,
                          targetID int,
                          amount real,
                          type char(10),
                          timestamp timestamp
)
    WITH (
        storage='file',
        paths='file:///tmp/data/pixels_bench_sf1x/checking/',
        pk='id',
        pk_scheme='rocksdb'
        );

CREATE TABLE loanapps (
                          id int,
                          applicantID int,
                          amount real,
                          duration int,
                          status char(12)

)
    WITH (
        storage='file',
        paths='file:///tmp/data/pixels_bench_sf1x/loanapps/',
        pk='id',
        pk_scheme='rocksdb'
        );

CREATE TABLE loantrans (
                           id int,
                           applicantID int,
                           appID int,
                           amount real,
                           status char(12),
                           timestamp timestamp,
                           duration int,
                           contract_timestamp timestamp,
                           delinquency int
)
    WITH (
        storage='file',
        paths='file:///tmp/data/pixels_bench_sf1x/loantrans/',
        pk='id',
        pk_scheme='rocksdb'
        );
CREATE TABLE freshness
(
    f_cli_id int,
    f_tx_id  int,
    primary key (f_cli_id)
);

CREATE TABLE IF NOT EXISTS  customer (
custID int ,
companyID int,
gender varchar(6),
name varchar(15),
age int,
phone varchar(11),
province varchar(15),
city varchar(15),
loan_balance decimal(15,2),
saving_credit int,
checking_credit int,
loan_credit int,
Isblocked int,
created_date Date,
last_update_timestamp timestamp default current_timestamp,
primary key(custid)
);

CREATE TABLE IF NOT EXISTS  company (
companyID int PRIMARY KEY,
name varchar(100),
category varchar(100),
staff_size int,
loan_balance decimal(15,2),
phone varchar(11),
province varchar(15),
city varchar(15),
saving_credit int,
checking_credit int,
loan_credit int,
Isblocked int,
created_date Date,
last_update_timestamp timestamp default current_timestamp
);


CREATE TABLE IF NOT EXISTS  savingAccount (
 accountID int PRIMARY KEY,
 userID int,
 balance decimal(15,2),
 Isblocked int,
 savingAccount_ts timestamp default current_timestamp
);

CREATE TABLE IF NOT EXISTS  checkingAccount (
accountID int PRIMARY KEY,
userID int,
balance decimal(15,2),
Isblocked int,
checkingAccount_ts timestamp
);

CREATE TABLE IF NOT EXISTS  transfer (
  id bigint  auto_increment ,
  sourceID int,
  targetID int,
  amount decimal(15,2),
  type varchar(100),
  transfer_ts timestamp default current_timestamp,
  fresh_ts timestamp  default current_timestamp,
  PRIMARY KEY(id)
);

CREATE TABLE IF NOT EXISTS  checking (
  id int auto_increment,
  sourceID int,
  targetID int,
  amount decimal(15,2),
  type varchar(100),
  checking_ts timestamp default current_timestamp,
   PRIMARY KEY(id)
);

CREATE TABLE IF NOT EXISTS  loanapps (
  id int auto_increment,
  applicantID int,
  amount decimal(15,2),
  duration int,
  status varchar(12),
  loanapps_ts timestamp default current_timestamp,
  PRIMARY KEY(id)
);

CREATE TABLE IF NOT EXISTS  loantrans (
  id int auto_increment,
  applicantID int,
  appID int,
  amount decimal(15,2),
  status varchar(12),
  loantrans_ts timestamp,
  duration int,
  contract_timestamp timestamp default current_timestamp,
  delinquency int,
  PRIMARY KEY(id)
) ;

ALTER TABLE freshness SET TIFLASH REPLICA 1;
ALTER TABLE checking SET TIFLASH REPLICA 1;
ALTER TABLE checkingAccount SET TIFLASH REPLICA 1;
ALTER TABLE company SET TIFLASH REPLICA 1;
ALTER TABLE customer SET TIFLASH REPLICA 1;
ALTER TABLE loanapps SET TIFLASH REPLICA 1;
ALTER TABLE loantrans SET TIFLASH REPLICA 1;
ALTER TABLE savingAccount SET TIFLASH REPLICA 1;
ALTER TABLE transfer SET TIFLASH REPLICA 1;
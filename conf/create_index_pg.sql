ALTER TABLE freshness ADD PRIMARY KEY (f_cli_id);
ALTER TABLE customer ADD PRIMARY KEY (custID);
ALTER TABLE company ADD PRIMARY KEY (companyID);
ALTER TABLE savingAccount ADD PRIMARY KEY (accountID);
ALTER TABLE checkingAccount ADD PRIMARY KEY (accountID);
ALTER TABLE transfer ADD PRIMARY KEY (id);
ALTER TABLE checking ADD PRIMARY KEY (id);
ALTER TABLE loanapps ADD PRIMARY KEY (id);
ALTER TABLE loantrans ADD PRIMARY KEY (id);


create index IF NOT EXISTS  idx_loanapps_1 on loanapps ( applicantid );

create index IF NOT EXISTS  idx_loanapps_2 on loanapps ( timestamp );

create index IF NOT EXISTS  idx_loantrans_1 on loantrans ( applicantid );

create index IF NOT EXISTS  idx_loantrans_2 on loantrans ( timestamp );

create index IF NOT EXISTS  idx_transfer_1 on transfer ( sourceid );

create index IF NOT EXISTS  idx_transfer_2 on transfer ( targetid );

create index IF NOT EXISTS  idx_transfer_3 on transfer ( type );

create index IF NOT EXISTS  idx_checking_1 on checking ( sourceid );

create index IF NOT EXISTS  idx_checking_2 on checking ( targetid );

create index IF NOT EXISTS  idx_customer_1 on customer ( companyid );

create index IF NOT EXISTS  idx_company_1 on company ( category );

create index IF NOT EXISTS  idx_sa_1 on savingaccount ( userid );

create index IF NOT EXISTS  idx_ca_1 on checkingaccount ( userid );




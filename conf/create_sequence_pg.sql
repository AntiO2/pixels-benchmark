-- transfer
CREATE SEQUENCE IF NOT EXISTS transfer_id_seq;
SELECT SETVAL('transfer_id_seq', (SELECT max(id) FROM transfer));
ALTER TABLE transfer OWNER TO pixels;
ALTER SEQUENCE transfer_id_seq OWNER TO pixels;
ALTER TABLE transfer ALTER COLUMN id SET DEFAULT nextval('transfer_id_seq'::regclass);
ALTER SEQUENCE transfer_id_seq OWNED BY transfer.id;

-- checking
CREATE SEQUENCE IF NOT EXISTS checking_id_seq;
SELECT SETVAL('checking_id_seq', (SELECT max(id) FROM checking));
ALTER TABLE checking OWNER TO pixels;
ALTER SEQUENCE checking_id_seq OWNER TO pixels;
ALTER TABLE checking ALTER COLUMN id SET DEFAULT nextval('checking_id_seq'::regclass);
ALTER SEQUENCE checking_id_seq OWNED BY checking.id;

-- loanapps
CREATE SEQUENCE IF NOT EXISTS loanapps_id_seq;
SELECT SETVAL('loanapps_id_seq', (SELECT max(id) FROM loanapps));
ALTER TABLE loanapps OWNER TO pixels;
ALTER SEQUENCE loanapps_id_seq OWNER TO pixels;
ALTER TABLE loanapps ALTER COLUMN id SET DEFAULT nextval('loanapps_id_seq'::regclass);
ALTER SEQUENCE loanapps_id_seq OWNED BY loanapps.id;

-- loantrans
CREATE SEQUENCE IF NOT EXISTS loantrans_id_seq;
SELECT SETVAL('loantrans_id_seq', (SELECT max(id) FROM loantrans));
ALTER TABLE loantrans OWNER TO pixels;
ALTER SEQUENCE loantrans_id_seq OWNER TO pixels;
ALTER TABLE loantrans ALTER COLUMN id SET DEFAULT nextval('loantrans_id_seq'::regclass);
ALTER SEQUENCE loantrans_id_seq OWNED BY loantrans.id;

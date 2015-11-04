/* NEW COLUMNS OF EXISTING TABLES */

ALTER TABLE external_holding_info ADD COLUMN barcode character varying(255) NULL;
ALTER TABLE external_holding_info ADD COLUMN shelvinglocation character varying(255) NULL;
ALTER TABLE external_holding_info ADD CONSTRAINT external_holding_info_barcode UNIQUE (barcode);

ALTER TABLE external_record_info ADD COLUMN copyright character varying(255) NULL;
ALTER TABLE external_record_info ADD COLUMN publication_status character varying(255) NOT NULL DEFAULT 'CLOSED';

ALTER TABLE holding_reservations ADD COLUMN completed boolean NOT NULL DEFAULT 'f';
ALTER TABLE holding_reservations ADD COLUMN on_hold boolean NOT NULL DEFAULT 'f';

ALTER TABLE records ADD COLUMN external_info_updated timestamp NULL;

/* UPDATE THE NEW COLUMN'S VALUES */

UPDATE holding_reservations
SET completed = 't'
FROM holding_reservations AS hr
INNER JOIN reservations AS r
ON hr.reservation_id = r.id
WHERE r.status = 'COMPLETED'
AND holding_reservations.id = hr.id;

UPDATE holding_reservations
SET completed = 't'
FROM holding_reservations AS hr
INNER JOIN reservations AS r
ON hr.reservation_id = r.id
INNER JOIN holdings AS h
ON hr.holding_id = h.id
WHERE r.status = 'ACTIVE'
AND h.status = 'AVAILABLE'
AND holding_reservations.id = hr.id;

/* NEW TABLES */

CREATE TABLE reproduction_standard_options
(
  id integer NOT NULL,
  copyrightprice numeric(7,2) NOT NULL,
  deliverytime integer NOT NULL,
  enabled boolean NOT NULL,
  level character varying(255) NOT NULL,
  material_type character varying(255) NOT NULL,
  optiondescription_en character varying(255) NOT NULL,
  optiondescription_nl character varying(255) NOT NULL,
  optionname_en character varying(50) NOT NULL,
  optionname_nl character varying(50) NOT NULL,
  price numeric(7,2) NOT NULL,
  CONSTRAINT reproduction_standard_options_pkey PRIMARY KEY (id),
  CONSTRAINT reproduction_standard_options_deliverytime_check CHECK (deliverytime >= 0),
  CONSTRAINT reproduction_standard_options_price_check CHECK (price >= 0::numeric)
)
WITH (
  OIDS=FALSE
);

CREATE TABLE orders
(
  id bigint NOT NULL,
  amount bigint NOT NULL,
  createdat timestamp without time zone NOT NULL,
  description character varying(100),
  ordercode character varying(50),
  payed integer NOT NULL,
  paymentmethod integer NOT NULL,
  refundedamount bigint NOT NULL,
  refundedat timestamp without time zone,
  updatedat timestamp without time zone NOT NULL,
  CONSTRAINT orders_pkey PRIMARY KEY (id),
  CONSTRAINT orders_ordercode_key UNIQUE (ordercode),
  CONSTRAINT orders_amount_check CHECK (amount >= 0),
  CONSTRAINT orders_refundedamount_check CHECK (refundedamount >= 0)
)
WITH (
  OIDS=FALSE
);

CREATE TABLE reproductions
(
  id integer NOT NULL,
  comment character varying(255), 
  creation_date timestamp without time zone NOT NULL,
  customeremail character varying(255) NOT NULL,
  customername character varying(255) NOT NULL,
  date date NOT NULL,
  date_has_order_details date,
  deliverytimecomment text,
  discount numeric(7,2) NOT NULL,
  offer_ready_immediatly boolean,
  printed boolean,
  requestlocale character varying(255) NOT NULL,
  status character varying(255) NOT NULL,
  token character varying(36) NOT NULL,
  order_id bigint,
  CONSTRAINT reproductions_pkey PRIMARY KEY (id),
  CONSTRAINT fke6904ac73d5b738a FOREIGN KEY (order_id)
      REFERENCES orders (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT reproductions_copyright_price_check CHECK (copyright_price >= 0::numeric),
  CONSTRAINT reproductions_discount_check CHECK (discount >= 0::numeric)
)
WITH (
  OIDS=FALSE
);

CREATE TABLE holding_reproductions
(
  id integer NOT NULL,
  comment character varying(255),
  completed boolean NOT NULL,
  copyrightprice numeric(7,2),
  customreproductioncustomer text,
  customreproductionreply text,
  deliverytime integer,
  insor boolean NOT NULL,
  on_hold boolean NOT NULL,
  price numeric(7,2),
  holding_id integer,
  reproduction_id integer,
  reproduction_standard_option_id integer,
  CONSTRAINT holding_reproductions_pkey PRIMARY KEY (id),
  CONSTRAINT fkfbac824b1d683e65 FOREIGN KEY (holding_id)
      REFERENCES holdings (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fkfbac824b4f851764 FOREIGN KEY (reproduction_standard_option_id)
      REFERENCES reproduction_standard_options (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fkfbac824b6a2b63aa FOREIGN KEY (reproduction_id)
      REFERENCES reproductions (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT holding_reproductions_deliverytime_check CHECK (deliverytime >= 0)
)
WITH (
  OIDS=FALSE
);

/* FOREIGN KEY INDEXES ON BOTH OLD AND NEW TABLES */

CREATE INDEX records_external_info_fk ON records (external_info_id);

CREATE INDEX holdings_record_fk ON holdings (record_id);
CREATE INDEX holdings_external_info_fk ON holdings (external_info_id);

CREATE INDEX holding_reservations_holding_fk ON holding_reservations (holding_id);
CREATE INDEX holding_reservations_reservation_fk ON holding_reservations (reservation_id);

CREATE INDEX holding_reproductions_holding_fk ON holding_reproductions (holding_id);
CREATE INDEX holding_reproductions_reproduction_fk ON holding_reproductions (reproduction_id);

/* OTHER INDEXES ON BOTH OLD AND NEW TABLES */

CREATE INDEX holding_reproductions_on_hold_idx ON holding_reproductions (on_hold);
CREATE INDEX holding_reproductions_completed_idx ON holding_reproductions (completed);

CREATE INDEX holding_reservations_on_hold_idx ON holding_reservations (on_hold);
CREATE INDEX holding_reservations_completed_idx ON holding_reservations (completed);

/* NEW INSERTS FROM initial-data.sql */

INSERT INTO authorities VALUES (12, 'Create reproductions.', 'ROLE_REPRODUCTION_CREATE');
INSERT INTO authorities VALUES (13, 'View reproductions.', 'ROLE_REPRODUCTION_VIEW');
INSERT INTO authorities VALUES (14, 'Modify reproductions.', 'ROLE_REPRODUCTION_MODIFY');
INSERT INTO authorities VALUES (15, 'Delete reproductions.', 'ROLE_REPRODUCTION_DELETE');

INSERT INTO group_permissions VALUES (2, 12);
INSERT INTO group_permissions VALUES (2, 13);
INSERT INTO group_permissions VALUES (2, 14);
INSERT INTO group_permissions VALUES (2, 15);

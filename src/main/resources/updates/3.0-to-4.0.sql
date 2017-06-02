BEGIN;

/* ADD NEW COLUMNS */

ALTER TABLE recordpermissions ADD COLUMN date_granted date;
ALTER TABLE recordpermissions ADD COLUMN org_request_pids character varying(500);

ALTER TABLE external_record_info ADD COLUMN restriction character varying(255) NOT NULL DEFAULT 'OPEN';

/* REMOVE OLD COLUMNS */

ALTER TABLE records DROP COLUMN embargo;
ALTER TABLE records DROP COLUMN restriction;
ALTER TABLE records DROP COLUMN restriction_type;
ALTER TABLE records DROP COLUMN comments;
ALTER TABLE records DROP COLUMN contact_id;

ALTER TABLE holdings DROP COLUMN floor;
ALTER TABLE holdings DROP COLUMN direction;
ALTER TABLE holdings DROP COLUMN cabinet;
ALTER TABLE holdings DROP COLUMN shelf;

ALTER TABLE reservations DROP COLUMN special;
ALTER TABLE reservations DROP COLUMN queueNo;
ALTER TABLE reservations DROP COLUMN permission_id;

ALTER TABLE permissions DROP COLUMN status;
ALTER TABLE permissions DROP COLUMN date_from;
ALTER TABLE permissions DROP COLUMN date_to;

/* UPDATE OLD COLUMNS */

ALTER TABLE permissions ALTER COLUMN explanation DROP NOT NULL;
ALTER TABLE permissions ALTER COLUMN address DROP NOT NULL;

/* DROP OLD TABLES */

DROP TABLE contacts;

/* ADD NEW TABLES */

CREATE TABLE archive_holding_info
(
  id integer NOT NULL,
  format character varying(50),
  meter character varying(50),
  note text,
  numbers character varying(50),
  shelvinglocation character varying(255),
  record_id integer NOT NULL,
  CONSTRAINT archive_holding_info_pkey PRIMARY KEY (id),
  CONSTRAINT fk67393d07ebb43f2f FOREIGN KEY (record_id)
  REFERENCES records (id) MATCH SIMPLE
  ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);

CREATE TABLE reservation_permissions
(
  reservation_id integer NOT NULL,
  permission_id integer NOT NULL,
  CONSTRAINT reservation_permissions_pkey PRIMARY KEY (reservation_id, permission_id),
  CONSTRAINT fk84f1449159b33e6d FOREIGN KEY (permission_id)
  REFERENCES permissions (id) MATCH SIMPLE
  ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk84f14491b3f0de98 FOREIGN KEY (reservation_id)
  REFERENCES reservations (id) MATCH SIMPLE
  ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);

/* CREATE NEW FOREIGN KEY INDEXES */

CREATE INDEX archive_holding_info_record_fk ON archive_holding_info (record_id);
CREATE INDEX recordpermissions_permission_fk ON recordpermissions (permission_id);
CREATE INDEX recordpermissions_record_fk ON recordpermissions (record_id);
CREATE INDEX reservation_permissions_reservation_fk ON reservation_permissions (reservation_id);
CREATE INDEX reservation_permissions_permission_fk ON reservation_permissions (permission_id);
CREATE INDEX records_record_fk ON records (parent_id);

/* CREATE NEW INDEXES */

CREATE INDEX reservations_visitorname_idx ON reservations (visitorname);
CREATE INDEX reservations_visitoremail_idx ON reservations (visitoremail);
CREATE INDEX reservations_status_idx ON reservations (status);
CREATE INDEX reservations_creation_date_idx ON reservations (creation_date);

CREATE INDEX reproductions_customername_idx ON reproductions (customername);
CREATE INDEX reproductions_customeremail_idx ON reproductions (customeremail);
CREATE INDEX reproductions_status_idx ON reproductions (status);
CREATE INDEX reproductions_creation_date_idx ON reproductions (creation_date);

CREATE INDEX permissions_name_idx ON permissions (name);
CREATE INDEX permissions_email_idx ON permissions (email);
CREATE INDEX permissions_explanation_idx ON permissions (explanation);
CREATE INDEX permissions_research_organization_idx ON permissions (research_organization);
CREATE INDEX permissions_research_subject_idx ON permissions (research_subject);
CREATE INDEX permissions_address_idx ON permissions (address);

CREATE INDEX holding_reservations_printed_idx ON holding_reservations (printed);

CREATE INDEX holding_reproductions_printed_idx ON holding_reproductions (printed);

CREATE INDEX recordpermissions_date_granted_idx ON recordpermissions (date_granted);
CREATE INDEX recordpermissions_granted_idx ON recordpermissions (granted);

CREATE INDEX holdings_signature_idx ON holdings (signature);
CREATE INDEX holdings_status_idx ON holdings (status);

CREATE INDEX external_record_info_title_idx ON external_record_info (title);

/* RESET THE EXTERNAL INFO OF ARCHIVES */

UPDATE records
SET external_info_updated = NULL
FROM records AS r
INNER JOIN external_record_info AS eri
ON r.external_info_id = eri.id
WHERE eri.material_type = 'ARCHIVE'
AND records.id = r.id;

/* END COMMIT */

COMMIT;

BEGIN;

/* ADD NEW COLUMNS */

ALTER TABLE external_record_info ADD COLUMN restriction character varying(255) NOT NULL DEFAULT 'OPEN';

/* REMOVE OLD COLUMNS */

ALTER TABLE records DROP COLUMN embargo;
ALTER TABLE records DROP COLUMN restriction;
ALTER TABLE records DROP COLUMN restriction_type;

ALTER TABLE reservations DROP COLUMN queueNo;
ALTER TABLE reservations DROP COLUMN permission_id;

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

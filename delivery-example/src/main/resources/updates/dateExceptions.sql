CREATE TABLE reservation_date_exceptions
(
  id integer NOT NULL,
  exception_startDate DATE NOT NULL,
  exception_endDate DATE,
  description CHARACTER VARYING(255)
)
WITH (
  OIDS=FALSE
);

/* NEW INSERTS FROM initial-data.sql */

INSERT INTO authorities VALUES (16, 'View date exceptions.', 'ROLE_DATE_EXCEPTION_VIEW');
INSERT INTO authorities VALUES (17, 'Create date exceptions.', 'ROLE_DATE_EXCEPTION_CREATE');
INSERT INTO authorities VALUES (18, 'Modify date exceptions.', 'ROLE_DATE_EXCEPTION_MODIFY');
INSERT INTO authorities VALUES (19, 'Delete date exceptions.', 'ROLE_DATE_EXCEPTION_DELETE');

INSERT INTO group_permissions VALUES (3, 16);
INSERT INTO group_permissions VALUES (3, 17);
INSERT INTO group_permissions VALUES (3, 18);
INSERT INTO group_permissions VALUES (3, 19);

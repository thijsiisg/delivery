BEGIN;

/* ADD NEW COLUMNS */

ALTER TABLE external_record_info ADD COLUMN restriction character varying(255) NOT NULL DEFAULT 'OPEN';

/* REMOVE OLD COLUMNS */

ALTER TABLE records DROP COLUMN embargo;
ALTER TABLE records DROP COLUMN restriction;
ALTER TABLE records DROP COLUMN restriction_type;

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

BEGIN;

/* NEW COLUMN ON EXISTING TABLE */

ALTER TABLE reproductions ADD COLUMN offer_mail_reminder_sent boolean DEFAULT false;

/* END COMMIT*/
COMMIT;

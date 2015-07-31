--
-- Copyright (C) 2013 International Institute of Social History
--
-- Licensed under the Apache License, Version 2.0 (the "License");
-- you may not use this file except in compliance with the License.
-- You may obtain a copy of the License at
--
--     http://www.apache.org/licenses/LICENSE-2.0
--
-- Unless required by applicable law or agreed to in writing, software
-- distributed under the License is distributed on an "AS IS" BASIS,
-- WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
-- See the License for the specific language governing permissions and
-- limitations under the License.
--

--
-- Data for Name: authorities; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO authorities VALUES (1, 'Modify users and authorities.', 'ROLE_USER_MODIFY');
INSERT INTO authorities VALUES (2, 'Modify record metadata.', 'ROLE_RECORD_MODIFY');
INSERT INTO authorities VALUES (3, 'View record contact data.', 'ROLE_RECORD_CONTACT_VIEW');
INSERT INTO authorities VALUES (4, 'View reservations.', 'ROLE_RESERVATION_VIEW');
INSERT INTO authorities VALUES (5, 'Modify reservations.', 'ROLE_RESERVATION_MODIFY');
INSERT INTO authorities VALUES (6, 'View permissions.', 'ROLE_PERMISSION_VIEW');
INSERT INTO authorities VALUES (7, 'Modify permissions.', 'ROLE_PERMISSION_MODIFY');
INSERT INTO authorities VALUES (8, 'Delete permissions.', 'ROLE_PERMISSION_DELETE');
INSERT INTO authorities VALUES (9, 'Delete reservations.', 'ROLE_RESERVATION_DELETE');
INSERT INTO authorities VALUES (10, 'Delete records.', 'ROLE_RECORD_DELETE');
INSERT INTO authorities VALUES (11, 'Create reservations.', 'ROLE_RESERVATION_CREATE');
INSERT INTO authorities VALUES (12, 'Create reproductions.', 'ROLE_REPRODUCTION_CREATE');
INSERT INTO authorities VALUES (13, 'View reproductions.', 'ROLE_REPRODUCTION_VIEW');
INSERT INTO authorities VALUES (14, 'Modify reproductions.', 'ROLE_REPRODUCTION_MODIFY');
INSERT INTO authorities VALUES (15, 'Delete reproductions.', 'ROLE_REPRODUCTION_DELETE');

--
-- Data for Name: groups; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO groups VALUES (1, 'Magazijnmedewerkers', 'Levering');
INSERT INTO groups VALUES (2, 'Admins', 'Administrator');
INSERT INTO groups VALUES (3, 'Infobalie', 'Infobalie');
INSERT INTO groups VALUES (4, 'Metadata beheer', 'Metadata beheer');

--
-- Data for Name: group_permissions; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO group_permissions VALUES (2, 1);
INSERT INTO group_permissions VALUES (2, 2);
INSERT INTO group_permissions VALUES (2, 3);
INSERT INTO group_permissions VALUES (2, 4);
INSERT INTO group_permissions VALUES (2, 5);
INSERT INTO group_permissions VALUES (2, 6);
INSERT INTO group_permissions VALUES (2, 7);
INSERT INTO group_permissions VALUES (2, 8);
INSERT INTO group_permissions VALUES (2, 9);
INSERT INTO group_permissions VALUES (2, 10);
INSERT INTO group_permissions VALUES (2, 11);
INSERT INTO group_permissions VALUES (2, 12);
INSERT INTO group_permissions VALUES (2, 13);
INSERT INTO group_permissions VALUES (2, 14);
INSERT INTO group_permissions VALUES (2, 15);
INSERT INTO group_permissions VALUES (1, 4);
INSERT INTO group_permissions VALUES (1, 5);
INSERT INTO group_permissions VALUES (3, 7);
INSERT INTO group_permissions VALUES (3, 6);
INSERT INTO group_permissions VALUES (3, 11);
INSERT INTO group_permissions VALUES (3, 4);
INSERT INTO group_permissions VALUES (3, 5);
INSERT INTO group_permissions VALUES (4, 2);
INSERT INTO group_permissions VALUES (4, 3);



--
-- Data for Name: users; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO users VALUES (1, 'swo@iisg.nl');
INSERT INTO users VALUES (2, 'lwo@iisg.nl');
INSERT INTO users VALUES (3, 'etu@iisg.nl');
INSERT INTO users VALUES (4, 'kerim');

--
-- Data for Name: user_groups; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO user_groups VALUES (1, 2);
INSERT INTO user_groups VALUES (2, 2);
INSERT INTO user_groups VALUES (3, 2);
INSERT INTO user_groups VALUES (4, 2);

--
-- Data for Name: reproduction_standard_options; Type: TABLE DATA; Schema: public; Owner postgres

INSERT INTO reproduction_standard_options VALUES (1, 1, 't', 'MASTER', 'BOOK', 'The complete brochure/book as a PDF.', 'De hele brochure/boek als een PDF.', 'PDF', 'PDF', 10.50);
INSERT INTO reproduction_standard_options VALUES (2, 2, 't', 'MASTER', 'SOUND', 'The complete tape/cassette in MP3.', 'De hele tape/cassette als een MP3.', 'MP3', 'MP3', 5.45);
INSERT INTO reproduction_standard_options VALUES (3, 3, 't', 'MASTER', 'MOVING_VISUAL', 'The complete tape/film in MP4.', 'De hele tape/film als een MP4.', 'MP4', 'MP4', 9.75);
INSERT INTO reproduction_standard_options VALUES (4, 4, 't', 'MASTER', 'VISUAL', 'The complete picture/poster/object as a TIFF of 300 dpi.', 'De hele afbeelding/poster/object als een TIFF van 300 dpi.', 'TIFF', 'TIFF', 13.00);
INSERT INTO reproduction_standard_options VALUES (5, 5, 't', 'LEVEL1', 'VISUAL', 'The complete picture/poster/object as a JPEG.', 'De hele afbeelding/poster/object als een JPEG.', 'JPEG', 'JPEG', 2.00);
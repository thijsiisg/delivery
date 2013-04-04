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

--
-- Data for Name: user_groups; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO user_groups VALUES (1, 2);
INSERT INTO user_groups VALUES (2, 2);
INSERT INTO user_groups VALUES (3, 2);

# --- First database schema

-- Author: Nemo

# --- !Ups
CREATE TABLE hat (
  id                        integer NOT NULL PRIMARY KEY,
  name                   	varchar(255) NOT NULL
);

CREATE SEQUENCE user_id_seq;
-- needs \"\" because user is a reserved SQL table. To keep the Scala Play convention we keep the singular as name
CREATE TABLE `user` (
  id                        integer NOT NULL DEFAULT nextval('user_id_seq') PRIMARY KEY,
  name                   	varchar(255) NOT NULL
);


CREATE SEQUENCE thinking_session_id_seq;
CREATE TABLE thinking_session (
  id                        integer NOT NULL DEFAULT nextval('thinking_session_id_seq') PRIMARY KEY,
  owner						integer NOT NULL REFERENCES `user`(id),
  name                   	varchar(255) NOT NULL,
  current_hat				integer REFERENCES hat(id)
);


CREATE SEQUENCE card_id_seq;
CREATE TABLE card (
  id                        integer NOT NULL DEFAULT nextval('card_id_seq') PRIMARY KEY,
  thinking_session_id			integer NOT NULL REFERENCES thinking_session(id),
  content                   text NOT NULL,
  hat						integer REFERENCES hat(id),
  creator					integer REFERENCES `user`(id)
);

  
-- preload hat table with the 6 hats
INSERT INTO hat VALUES (1,'white');
INSERT INTO hat VALUES (2,'green');
INSERT INTO hat VALUES (3,'yellow');
INSERT INTO hat VALUES (4,'red');
INSERT INTO hat VALUES (5,'black');
INSERT INTO hat VALUES (6,'blue');


-- preload user table with the developer dummies
INSERT INTO user VALUES (1,'Anamika');
INSERT INTO user VALUES (2,'Manu');
INSERT INTO user VALUES (3,'Dom');
INSERT INTO user VALUES (4,'David');


INSERT INTO thinking_session VALUES (1,1,'Manus Birthday Present (Should be something awesome...)',1);



# --- !Downs
DROP TABLE if exists hat;

DROP TABLE if exists `user`;
DROP SEQUENCE if EXISTS user_id_seq;

DROP TABLE if exists thinking_session;
DROP SEQUENCE if EXISTS thinking_session_id_seq;

DROP TABLE if exists card;
DROP SEQUENCE if EXISTS card_id_seq;



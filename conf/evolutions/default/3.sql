-- Author: Nemo

# --- !Ups
CREATE TABLE event (
  id                 	integer NOT NULL PRIMARY KEY,
  type					varchar(255) NOT NULL,
  thinking_session		integer NOT NULL REFERENCES thinking_session(id),
  hat                  	integer NOT NULL REFERENCES hat(id),
  user					integer REFERENCES user(id),
  card					integer REFERENCES card(id),
  time					datetime DEFAULT NOW()
);

# --- !Downs
DROP TABLE if exists event;
-- Author: Nemo

# --- !Ups
CREATE TABLE hat_flow (
  index                 integer NOT NULL,
  thinking_session		integer NOT NULL REFERENCES thinking_session(id),
  hat                  	integer NOT NULL,
  PRIMARY KEY (index,thinking_session)
);

INSERT INTO hat_flow VALUES (0,1,1);
INSERT INTO hat_flow VALUES (1,1,2);
INSERT INTO hat_flow VALUES (2,1,3);
INSERT INTO hat_flow VALUES (3,1,4);
INSERT INTO hat_flow VALUES (4,1,5);
INSERT INTO hat_flow VALUES (5,1,6);

# --- !Downs
DROP TABLE if exists hat_flow;
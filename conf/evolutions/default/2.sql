-- Author: Nemo

# --- !Ups
CREATE TABLE hat_flow (
  index                 BIGINT NOT NULL,
  thinking_session		BIGINT NOT NULL REFERENCES thinking_session(id),
  hat                  	BIGINT NOT NULL REFERENCES hat(id),
  time_limit			integer,
  alone_time			integer,
  PRIMARY KEY (index,thinking_session)
);

INSERT INTO hat_flow VALUES (0,0,1,-1,-1);
INSERT INTO hat_flow VALUES (1,0,2,-1,-1);
INSERT INTO hat_flow VALUES (2,0,3,-1,-1);
INSERT INTO hat_flow VALUES (3,0,4,-1,-1);
INSERT INTO hat_flow VALUES (4,0,5,-1,-1);
INSERT INTO hat_flow VALUES (5,0,6,-1,-1);

# --- !Downs
DROP TABLE if exists hat_flow;
DROP TABLE if exists hat_elapsed;

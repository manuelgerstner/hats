-- Author: Nemo

# --- !Ups
CREATE TABLE participating (
  thinking_session		BIGINT NOT NULL REFERENCES thinking_session(id),
  user                 	BIGINT NOT NULL REFERENCES user(id),
  token					BIGINT, 
  ready					BIT DEFAULT False,
  time					datetime DEFAULT NOW(),
  PRIMARY KEY (thinking_session,user)
);

# --- !Downs
DROP TABLE if exists participating;
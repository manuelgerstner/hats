# --- First database schema

# --- !Ups
CREATE SEQUENCE idea_id_seq;
CREATE TABLE idea (
  id                        integer NOT NULL DEFAULT nextval('idea_id_seq') PRIMARY KEY,
  title                     varchar(255) NOT NULL,
  content                   text NOT NULL
);


# --- !Downs

DROP TABLE if exists idea;
DROP SEQUENCE if EXISTS idea_id_seq;

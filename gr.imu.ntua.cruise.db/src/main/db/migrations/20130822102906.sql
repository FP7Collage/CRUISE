create table sequence(
  sequence_name varchar(100) primary key unique,
  sequence_index INT NOT NULL
) engine=innodb;

create table schema_history(
  history_date timestamp default now(),
  description text not null
) engine=innodb;


create table bookmarks(
  id int primary key not null unique,
  `url` varchar(255) not null ,
  `terms` varchar(255) null,
  `query` varchar(255) null,
  `source` varchar(255) null,
  `createdAt` timestamp not null
);

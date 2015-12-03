create table rating(
  id int primary key not null unique,
  `session` varchar(255) not null ,
  `url` varchar(255) not null ,
  `terms` varchar(255) null,
  `query` varchar(255) null,
  `source` varchar(255) null,
  `rating` varchar(255) null,
  `createdAt` timestamp not null
);

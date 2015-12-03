create table `action`(
  id int primary key not null unique,
  `session` varchar(255) not null ,
  `content` varchar(255) not null ,
  `action` varchar(255) null,
  `createdAt` timestamp not null
);

drop database if exists cruise;
drop database if exists cruise_test;

create database cruise;
create database cruise_test;

grant all privileges on cruise.* to 'cruise'@'localhost' identified by '1234';
grant all privileges on cruise_test.* to 'cruise'@'localhost' identified by '1234';


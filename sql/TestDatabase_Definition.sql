create user microservice_test with encrypted password 'microservice_test';

create database microservice_test with owner microservice_test;

# To connect from Linux console using the new user
psql -d microservice_test -U microservice_test

create schema eat;

create table eat.ingredient (
  id	smallserial	not null	constraint ingredient_pk primary key,
  name 	varchar(64) 	not null
);

create unique index ingredient_name_uindex on eat.ingredient (name);


create table eat.pizza(
  id	smallserial		not null	constraint pizza_pk primary key,
  name 	varchar(64),
  cost 	double precision 	not null
);

create unique index pizza_name_uindex on eat.pizza (name);


create table eat.pizza_ingredient(
  pizza_id	smallint not null	constraint pizza_ingredient_pizza_id_fk references eat.pizza,
  ingredient_id smallint not null    	constraint pizza_ingredient_ingredient_id_fk references eat.ingredient,
  constraint pizza_ingredient_pk primary key (pizza_id, ingredient_id)
);


create table eat.order(
  id		serial				not null	constraint order_pk primary key,
  code		varchar(64)			not null,
  created 	timestamp without time zone	not null
);

create unique index order_code_uindex on eat.order (code);


create table eat.order_line(
  id		serial			not null	constraint order_line_pk primary key,
  order_id      int                     not null	constraint order_line_order_id_fk references eat.order,
  pizza_id	smallint 		not null	constraint order_line_pizza_id_fk references eat.pizza,
  cost 		double precision 	not null,
  amount        smallint                not null
);


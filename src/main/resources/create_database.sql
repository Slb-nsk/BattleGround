create table users(
	username varchar(50) not null primary key,
	password varchar(100) not null,
	enabled boolean not null
);
create table authorities (
	username varchar(50) not null,
	authority varchar(50) not null,
	constraint fk_authorities_users foreign key(username) references users(username)
);
create unique index ix_auth_username on authorities (username,authority);
create table players (
	name varchar(50) not null primary key,         
        rating INTEGER,
        damage INTEGER,
        lifecapacity INTEGER
);

insert into users(username,password,enabled)
	values('admin','123',true);
insert into authorities(username,authority) 
	values('admin','ROLE_ADMIN');
insert into players(name,rating,damage,lifecapacity)
        values('admin',100,10,100);
insert into players(name,rating,damage,lifecapacity)
        values('dump',100,10,100);
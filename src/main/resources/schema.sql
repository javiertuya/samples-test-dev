--Primero se deben borrar todas las tablas (de detalle a maestro) y lugo anyadirlas (de maestro a detalle)
--(en este caso en cada aplicacion se usa solo una tabla, por lo que no hace falta)

--Para giis.demo.tkrun:
drop table Course;
drop table Registration;
drop table Payment;
drop table Invoice;

create table Course 
(course_id int primary key not null, 
course_name varchar(50) not null, 
objectives varchar(200) not null, 
course_location varchar(50) not null, 
course_date date not null, 
course_start_regperiod date not null, 
course_end_regperiod date not null, 
total_places int not null, 
course_fee int not null,
course_state varchar(50) not null, 
teacher_name varchar (50) not null,
check(course_start_period <= course_end_period), 
check (course_end_period < course_start_date));

create table Registration 
(reg_id int primary key not null, 
reg_name varchar(50) not null, 
reg_surnames varchar(50) not null, 
reg_phone varchar(9) not null, 
reg_email varchar(100) not null, 
reg_date date not null, 
reg_state varchar(50) not null, 
course_id int, 
foreign key(course_id) references Course(course_id));

create table Payment 
(payment_id int primary key not null, amount int not null, 
payment_date date not null,
reg_id int, 
foreign key (reg_id) references Registration(reg_id));

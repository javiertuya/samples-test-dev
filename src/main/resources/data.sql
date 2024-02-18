--Datos para carga inicial de la base de datos

--Para giis.demo.tkrun:
delete from Course;
delete from Registration;
delete from Invoice;
delete from Payment;

-- Datos para la tabla Course
insert into Course values
(1, 'Python Programming Course', 'Learn basic Python', 1, '2024-03-01', '2024-02-01', '2024-02-15', 30, 500, 'Active', 'Juan Pérez'),
(2, 'Graphic Design course', 'Develop basic Photoshop and ilustration skills', 2, '2024-04-25', '2024-02-01', '2024-03-15', 25, 600, 'Active', 'Ana Gómez');
(3, 'Software Engineering', 'Learn how to create domain models', 2, '2024-05-10', '2024-03-01', '2024-04-15', 25, 600, 'Active', 'José Fanjul');


-- Datos para la tabla Registration
insert into Registration values
(1, 'Carlos', 'González', '123456789', 'carlos@email.com', '2024-02-01', 'Confirmed', 1),
(2, 'María', 'López', '987654321', 'maria@email.com', '2024-02-05', 'Received', 2);
(3, 'Nombre', 'Random', '666777888', 'random@yopmail.com', '2024-02-12', 'Received', 2);

-- Datos para la tabla Payment
insert into Payment values
(1, 500, '2024-02-15', 1);
(2, 300, '2024-02-18', 3);


CREATE TABLE jobs
(
    id SERIAL NOT NULL PRIMARY KEY,
    name VARCHAR(20) NOT NULL
);
CREATE TABLE persons
(
    id SERIAL NOT NULL PRIMARY KEY,
    name VARCHAR(50) NOT NUll,
    age INTEGER NOT NUll,
    birth DATE NOT NULL,
    job INTEGER NOT NULL REFERENCES jobs(id),
    weight DOUBLE PRECISION
);

INSERT INTO jobs (name) VALUES
('Software Engineer'),
('Data Analyst'),
('Marketing Specialist'),
('Accountant'),
('Project Manager');

INSERT INTO persons (name, age, birth, job, weight) VALUES
('John Doe', 25, '1998-03-15', 1, 68.5),
('Jane Smith', 32, '1991-09-22', 2, 62.2),
('Michael Johnson', 41, '1982-05-10', 3, 75.8),
('Sarah Wilson', 28, '1995-11-07', 4, 60.1),
('David Brown', 37, '1986-08-29', 5, 83.6);
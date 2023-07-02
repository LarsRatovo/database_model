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
    weight DECIMAL(12,2)
);

CREATE TABLE transfer
(
    person INTEGER NOT NULL REFERENCES persons(id),
    transfer_date DATE NOT NULL
);
-- Test rows for the "jobs" table
INSERT INTO jobs (name) VALUES
    ('Engineer'),
    ('Manager'),
    ('Analyst');

-- Test rows for the "persons" table
INSERT INTO persons (name, age, birth, job, weight) VALUES
    ('John Doe', 30, '1993-05-15', 1, 75.5),
    ('Jane Smith', 42, '1981-10-02', 2, 68.2),
    ('Michael Johnson', 28, '1995-02-28', 3, 80.1);

-- Test rows for the "transfer" table
INSERT INTO transfer (person, job, transfer_date) VALUES
    (1, 2, '2022-09-01'),
    (2, 3, '2023-01-15'),
    (3, 1, '2023-05-10');

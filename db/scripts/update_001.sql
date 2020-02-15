create table if not exists vacancy(
             id SERIAL PRIMARY KEY NOT NULL,
             name VARCHAR (1000) UNIQUE,
             text TEXT,
             link TEXT,
             date TIMESTAMP
);
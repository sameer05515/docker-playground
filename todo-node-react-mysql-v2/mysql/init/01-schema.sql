CREATE DATABASE IF NOT EXISTS todo_db;

USE todo_db;

CREATE TABLE IF NOT EXISTS todos (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    completed BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

INSERT INTO todos (title, description, completed)
SELECT 'Learn Node.js', 'Build a REST API with Express', FALSE
WHERE NOT EXISTS (
    SELECT 1 FROM todos WHERE title = 'Learn Node.js'
);

INSERT INTO todos (title, description, completed)
SELECT 'Learn React', 'Build UI with React and Vite', FALSE
WHERE NOT EXISTS (
    SELECT 1 FROM todos WHERE title = 'Learn React'
);

INSERT INTO todos (title, description, completed)
SELECT 'Learn Docker', 'Containerize the application with Docker Compose', TRUE
WHERE NOT EXISTS (
    SELECT 1 FROM todos WHERE title = 'Learn Docker'
);

CREATE TABLE users(
    login VARCHAR(100) PRIMARY KEY,
    isActive boolean DEFAULT false,
    password VARCHAR(100) DEFAULT NULL
);

CREATE TABLE activation_users(
    login VARCHAR(100) PRIMARY KEY,
    email VARCHAR(100) NOT NULL,
    activation_token VARCHAR(100) NOT NULL,
    activation_sent boolean DEFAULT false
);
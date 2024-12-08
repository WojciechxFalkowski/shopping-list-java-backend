-- Tabela users
CREATE TABLE users (
                       id UUID PRIMARY KEY,
                       username VARCHAR(50) NOT NULL,
                       email VARCHAR(100) NOT NULL UNIQUE,
                       password VARCHAR(255) NOT NULL,
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
                       updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);

-- Tabela shopping_lists
CREATE TABLE shopping_lists (
                                id UUID PRIMARY KEY,
                                name VARCHAR(100) NOT NULL,
                                list_code VARCHAR(10) NOT NULL UNIQUE,
                                owner_id UUID NOT NULL REFERENCES users(id),
                                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
                                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);

-- Tabela list_items
CREATE TABLE list_items (
                            id UUID PRIMARY KEY,
                            list_id UUID NOT NULL REFERENCES shopping_lists(id),
                            name VARCHAR(100) NOT NULL,
                            quantity INT DEFAULT 1 NOT NULL,
                            purchased BOOLEAN DEFAULT false NOT NULL,
                            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
                            updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);

-- Tabela shared_lists
CREATE TABLE shared_lists (
                              id UUID PRIMARY KEY,
                              user_id UUID NOT NULL REFERENCES users(id),
                              list_id UUID NOT NULL REFERENCES shopping_lists(id),
                              joined_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
                              created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
                              updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);

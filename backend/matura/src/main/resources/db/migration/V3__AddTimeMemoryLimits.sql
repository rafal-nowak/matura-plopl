-- Utworzenie nowej tabeli subtask_result_entity
CREATE TABLE subtask_result_entity
(
    id             INTEGER                     NOT NULL PRIMARY KEY,
    score          INTEGER                     NOT NULL,
    submission_id  INTEGER                     NOT NULL,
    subtask_number INTEGER                     NOT NULL,
    created_at     TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    description    TEXT                        NOT NULL
);

-- Utworzenie nowej tabeli test_result_entity
CREATE TABLE test_result_entity
(
    id                INTEGER                     NOT NULL PRIMARY KEY,
    memory            INTEGER,
    subtask_result_id INTEGER                     NOT NULL,
    time              INTEGER,
    verdict           SMALLINT                    NOT NULL CHECK (verdict BETWEEN 0 AND 7),
    created_at        TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    message           TEXT
);

-- Utworzenie sekwencji dla test_result_entity
CREATE SEQUENCE test_result_id_seq START WITH 1 INCREMENT BY 1;

-- Utworzenie sekwencji dla subtask_result_entity
CREATE SEQUENCE subtask_result_id_seq START WITH 1 INCREMENT BY 1;
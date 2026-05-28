--liquibase formatted sql

--changeset magofrays:update-role
ALTER TABLE role ALTER COLUMN access_list TYPE VARCHAR(255)[] USING ARRAY[access_list::VARCHAR(255)];
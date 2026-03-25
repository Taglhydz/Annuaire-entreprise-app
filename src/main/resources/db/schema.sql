CREATE DATABASE IF NOT EXISTS annuaire_entreprise CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE annuaire_entreprise;

CREATE TABLE IF NOT EXISTS site (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    ville VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS service (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    nom VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS salarie (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    telephone_fixe VARCHAR(20),
    telephone_portable VARCHAR(20),
    email VARCHAR(180),
    site_id BIGINT NOT NULL,
    service_id BIGINT NOT NULL,
    CONSTRAINT fk_salarie_site FOREIGN KEY (site_id) REFERENCES site(id),
    CONSTRAINT fk_salarie_service FOREIGN KEY (service_id) REFERENCES service(id)
);

CREATE TABLE IF NOT EXISTS admin_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(80) NOT NULL UNIQUE,
    password_hash VARCHAR(120) NOT NULL
);

CREATE INDEX idx_salarie_nom ON salarie(nom);
CREATE INDEX idx_salarie_site ON salarie(site_id);
CREATE INDEX idx_salarie_service ON salarie(service_id);

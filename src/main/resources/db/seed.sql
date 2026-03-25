USE annuaire_entreprise;

-- ============================================================
-- RESET DES DONNEES (script rejouable)
-- ============================================================
SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE salarie;
TRUNCATE TABLE service;
TRUNCATE TABLE site;
TRUNCATE TABLE admin_user;
SET FOREIGN_KEY_CHECKS = 1;

-- ============================================================
-- SITES (CONSIGNES)
-- ============================================================
INSERT INTO site (ville) VALUES
    ('Paris'),
    ('Nantes'),
    ('Toulouse'),
    ('Nice'),
    ('Lille');

-- ============================================================
-- SERVICES
-- ============================================================
INSERT INTO service (nom) VALUES
    ('Comptabilite'),
    ('Production'),
    ('Accueil'),
    ('Informatique'),
    ('Commercial'),
    ('Ressources Humaines'),
    ('Marketing'),
    ('Direction');

-- ============================================================
-- ADMIN USERS
-- ============================================================
INSERT INTO admin_user (username, password_hash) VALUES
    ('admin', '$2a$12$3BcbbIaO9dD0KpxsXeafje6HLXThIOmgvA86XfFqVJaJSs6rqBDjO');

-- ============================================================
-- SALARIES (> 1000 CONSIGNES) : 1200 ENTREES
-- Repartition automatique sur les 5 sites et 8 services
-- ============================================================
SET SESSION cte_max_recursion_depth = 2000;

INSERT INTO salarie (
    nom,
    prenom,
    telephone_fixe,
    telephone_portable,
    email,
    site_id,
    service_id
)
WITH RECURSIVE seq AS (
    SELECT 1 AS n
    UNION ALL
    SELECT n + 1 FROM seq WHERE n < 1200
)
SELECT
    CONCAT('Nom', LPAD(seq.n, 4, '0')),
    CONCAT('Prenom', LPAD(seq.n, 4, '0')),
    CONCAT('0', LPAD(100000000 + seq.n, 9, '0')),
    CONCAT('06', LPAD(seq.n, 8, '0')),
    CONCAT('user', LPAD(seq.n, 4, '0'), '@entreprise.fr'),
    s.id,
    sv.id
FROM seq
JOIN site s
    ON s.ville = CASE MOD(seq.n - 1, 5)
        WHEN 0 THEN 'Paris'
        WHEN 1 THEN 'Nantes'
        WHEN 2 THEN 'Toulouse'
        WHEN 3 THEN 'Nice'
        ELSE 'Lille'
    END
JOIN service sv
    ON sv.nom = CASE MOD(seq.n - 1, 8)
        WHEN 0 THEN 'Comptabilite'
        WHEN 1 THEN 'Production'
        WHEN 2 THEN 'Accueil'
        WHEN 3 THEN 'Informatique'
        WHEN 4 THEN 'Commercial'
        WHEN 5 THEN 'Ressources Humaines'
        WHEN 6 THEN 'Marketing'
        ELSE 'Direction'
    END;

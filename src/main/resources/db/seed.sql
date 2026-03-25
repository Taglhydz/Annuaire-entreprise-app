USE annuaire_entreprise;

-- ============================================================
-- SITES
-- ============================================================
INSERT INTO site (ville) VALUES
    ('Paris'), ('Nantes'), ('Toulouse'), ('Nice'), ('Lille'),
    ('Lyon'), ('Bordeaux'), ('Strasbourg'), ('Rennes'), ('Montpellier'),
    ('Marseille'), ('Grenoble'), ('Dijon'), ('Rouen'), ('Reims')
ON DUPLICATE KEY UPDATE ville = VALUES(ville);

-- ============================================================
-- SERVICES
-- ============================================================
INSERT INTO service (nom) VALUES
    ('Comptabilité'), ('Production'), ('Accueil'), ('Informatique'), ('Commercial'),
    ('Ressources Humaines'), ('Marketing'), ('Juridique'), ('Logistique'), ('Direction')
ON DUPLICATE KEY UPDATE nom = VALUES(nom);

-- ============================================================
-- ADMIN USERS
-- ============================================================
INSERT INTO admin_user (username, password_hash) VALUES
    ('admin',   '$2a$12$3BcbbIaO9dD0KpxsXeafje6HLXThIOmgvA86XfFqVJaJSs6rqBDjO')
ON DUPLICATE KEY UPDATE username = VALUES(username);

-- ============================================================
-- SALARIES (200 entrées)
-- Répartis sur tous les sites et services de manière cohérente
-- ============================================================
INSERT INTO salarie (nom, prenom, telephone_fixe, telephone_portable, email, site_id, service_id)
SELECT * FROM (

-- Paris - Informatique
SELECT 'Martin','Alice','0140304050','0611223344','alice.martin@entreprise.fr',
    (SELECT id FROM site WHERE ville='Paris'),(SELECT id FROM service WHERE nom='Informatique') UNION ALL
SELECT 'Dupont','Lucas','0140304051','0611223345','lucas.dupont@entreprise.fr',
    (SELECT id FROM site WHERE ville='Paris'),(SELECT id FROM service WHERE nom='Informatique') UNION ALL
SELECT 'Leroy','Emma','0140304052','0611223346','emma.leroy@entreprise.fr',
    (SELECT id FROM site WHERE ville='Paris'),(SELECT id FROM service WHERE nom='Informatique') UNION ALL
SELECT 'Bernard','Hugo','0140304053','0611223347','hugo.bernard@entreprise.fr',
    (SELECT id FROM site WHERE ville='Paris'),(SELECT id FROM service WHERE nom='Informatique') UNION ALL
SELECT 'Morel','Chloé','0140304054','0611223348','chloe.morel@entreprise.fr',
    (SELECT id FROM site WHERE ville='Paris'),(SELECT id FROM service WHERE nom='Informatique') UNION ALL

-- Paris - Comptabilité
SELECT 'Petit','Sophie','0140305010','0622334455','sophie.petit@entreprise.fr',
    (SELECT id FROM site WHERE ville='Paris'),(SELECT id FROM service WHERE nom='Comptabilité') UNION ALL
SELECT 'Simon','Nicolas','0140305011','0622334456','nicolas.simon@entreprise.fr',
    (SELECT id FROM site WHERE ville='Paris'),(SELECT id FROM service WHERE nom='Comptabilité') UNION ALL
SELECT 'Michel','Camille','0140305012','0622334457','camille.michel@entreprise.fr',
    (SELECT id FROM site WHERE ville='Paris'),(SELECT id FROM service WHERE nom='Comptabilité') UNION ALL
SELECT 'Laurent','Antoine','0140305013','0622334458','antoine.laurent@entreprise.fr',
    (SELECT id FROM site WHERE ville='Paris'),(SELECT id FROM service WHERE nom='Comptabilité') UNION ALL

-- Paris - Commercial
SELECT 'Garcia','Laura','0140306020','0633445566','laura.garcia@entreprise.fr',
    (SELECT id FROM site WHERE ville='Paris'),(SELECT id FROM service WHERE nom='Commercial') UNION ALL
SELECT 'Roux','Julien','0140306021','0633445567','julien.roux@entreprise.fr',
    (SELECT id FROM site WHERE ville='Paris'),(SELECT id FROM service WHERE nom='Commercial') UNION ALL
SELECT 'Vincent','Manon','0140306022','0633445568','manon.vincent@entreprise.fr',
    (SELECT id FROM site WHERE ville='Paris'),(SELECT id FROM service WHERE nom='Commercial') UNION ALL

-- Paris - Marketing
SELECT 'Lefebvre','Thomas','0140307030','0644556677','thomas.lefebvre@entreprise.fr',
    (SELECT id FROM site WHERE ville='Paris'),(SELECT id FROM service WHERE nom='Marketing') UNION ALL
SELECT 'Fournier','Léa','0140307031','0644556678','lea.fournier@entreprise.fr',
    (SELECT id FROM site WHERE ville='Paris'),(SELECT id FROM service WHERE nom='Marketing') UNION ALL
SELECT 'Girard','Maxime','0140307032','0644556679','maxime.girard@entreprise.fr',
    (SELECT id FROM site WHERE ville='Paris'),(SELECT id FROM service WHERE nom='Marketing') UNION ALL

-- Paris - Direction
SELECT 'Bonnet','Isabelle','0140308040','0655667788','isabelle.bonnet@entreprise.fr',
    (SELECT id FROM site WHERE ville='Paris'),(SELECT id FROM service WHERE nom='Direction') UNION ALL
SELECT 'Dupuis','Pierre','0140308041','0655667789','pierre.dupuis@entreprise.fr',
    (SELECT id FROM site WHERE ville='Paris'),(SELECT id FROM service WHERE nom='Direction') UNION ALL

-- Paris - RH
SELECT 'Fontaine','Claire','0140309050','0666778899','claire.fontaine@entreprise.fr',
    (SELECT id FROM site WHERE ville='Paris'),(SELECT id FROM service WHERE nom='Ressources Humaines') UNION ALL
SELECT 'Rousseau','David','0140309051','0666778900','david.rousseau@entreprise.fr',
    (SELECT id FROM site WHERE ville='Paris'),(SELECT id FROM service WHERE nom='Ressources Humaines') UNION ALL
SELECT 'Blanc','Pauline','0140309052','0666778901','pauline.blanc@entreprise.fr',
    (SELECT id FROM site WHERE ville='Paris'),(SELECT id FROM service WHERE nom='Ressources Humaines') UNION ALL

-- Paris - Accueil
SELECT 'Guerin','Marie','0140310060','0677889900','marie.guerin@entreprise.fr',
    (SELECT id FROM site WHERE ville='Paris'),(SELECT id FROM service WHERE nom='Accueil') UNION ALL
SELECT 'Muller','Kevin','0140310061','0677889901','kevin.muller@entreprise.fr',
    (SELECT id FROM site WHERE ville='Paris'),(SELECT id FROM service WHERE nom='Accueil') UNION ALL

-- Nantes - Informatique
SELECT 'Renard','Océane','0240101010','0688990011','oceane.renard@entreprise.fr',
    (SELECT id FROM site WHERE ville='Nantes'),(SELECT id FROM service WHERE nom='Informatique') UNION ALL
SELECT 'Chevalier','Baptiste','0240101011','0688990012','baptiste.chevalier@entreprise.fr',
    (SELECT id FROM site WHERE ville='Nantes'),(SELECT id FROM service WHERE nom='Informatique') UNION ALL
SELECT 'Mercier','Anaïs','0240101012','0688990013','anais.mercier@entreprise.fr',
    (SELECT id FROM site WHERE ville='Nantes'),(SELECT id FROM service WHERE nom='Informatique') UNION ALL

-- Nantes - Production
SELECT 'Lemaire','Florian','0240202020','0699001122','florian.lemaire@entreprise.fr',
    (SELECT id FROM site WHERE ville='Nantes'),(SELECT id FROM service WHERE nom='Production') UNION ALL
SELECT 'Noel','Aurore','0240202021','0699001123','aurore.noel@entreprise.fr',
    (SELECT id FROM site WHERE ville='Nantes'),(SELECT id FROM service WHERE nom='Production') UNION ALL
SELECT 'Perrin','Romain','0240202022','0699001124','romain.perrin@entreprise.fr',
    (SELECT id FROM site WHERE ville='Nantes'),(SELECT id FROM service WHERE nom='Production') UNION ALL
SELECT 'Roy','Lucie','0240202023','0699001125','lucie.roy@entreprise.fr',
    (SELECT id FROM site WHERE ville='Nantes'),(SELECT id FROM service WHERE nom='Production') UNION ALL
SELECT 'Clement','Théo','0240202024','0699001126','theo.clement@entreprise.fr',
    (SELECT id FROM site WHERE ville='Nantes'),(SELECT id FROM service WHERE nom='Production') UNION ALL

-- Nantes - Logistique
SELECT 'Gauthier','Inès','0240303030','0610112233','ines.gauthier@entreprise.fr',
    (SELECT id FROM site WHERE ville='Nantes'),(SELECT id FROM service WHERE nom='Logistique') UNION ALL
SELECT 'Masson','Quentin','0240303031','0610112234','quentin.masson@entreprise.fr',
    (SELECT id FROM site WHERE ville='Nantes'),(SELECT id FROM service WHERE nom='Logistique') UNION ALL
SELECT 'Nicolas','Elisa','0240303032','0610112235','elisa.nicolas@entreprise.fr',
    (SELECT id FROM site WHERE ville='Nantes'),(SELECT id FROM service WHERE nom='Logistique') UNION ALL

-- Nantes - Commercial
SELECT 'Henry','Corentin','0240404040','0621223344','corentin.henry@entreprise.fr',
    (SELECT id FROM site WHERE ville='Nantes'),(SELECT id FROM service WHERE nom='Commercial') UNION ALL
SELECT 'Roussel','Juliette','0240404041','0621223345','juliette.roussel@entreprise.fr',
    (SELECT id FROM site WHERE ville='Nantes'),(SELECT id FROM service WHERE nom='Commercial') UNION ALL

-- Toulouse - Production
SELECT 'Moreau','Alexis','0561101010','0632334455','alexis.moreau@entreprise.fr',
    (SELECT id FROM site WHERE ville='Toulouse'),(SELECT id FROM service WHERE nom='Production') UNION ALL
SELECT 'Legrand','Julie','0561101011','0632334456','julie.legrand@entreprise.fr',
    (SELECT id FROM site WHERE ville='Toulouse'),(SELECT id FROM service WHERE nom='Production') UNION ALL
SELECT 'Roche','Valentin','0561101012','0632334457','valentin.roche@entreprise.fr',
    (SELECT id FROM site WHERE ville='Toulouse'),(SELECT id FROM service WHERE nom='Production') UNION ALL
SELECT 'Colin','Ambre','0561101013','0632334458','ambre.colin@entreprise.fr',
    (SELECT id FROM site WHERE ville='Toulouse'),(SELECT id FROM service WHERE nom='Production') UNION ALL
SELECT 'Leblanc','Samuel','0561101014','0632334459','samuel.leblanc@entreprise.fr',
    (SELECT id FROM site WHERE ville='Toulouse'),(SELECT id FROM service WHERE nom='Production') UNION ALL

-- Toulouse - Informatique
SELECT 'David','Marine','0561202020','0643445566','marine.david@entreprise.fr',
    (SELECT id FROM site WHERE ville='Toulouse'),(SELECT id FROM service WHERE nom='Informatique') UNION ALL
SELECT 'Bertrand','Enzo','0561202021','0643445567','enzo.bertrand@entreprise.fr',
    (SELECT id FROM site WHERE ville='Toulouse'),(SELECT id FROM service WHERE nom='Informatique') UNION ALL
SELECT 'Giraud','Mathilde','0561202022','0643445568','mathilde.giraud@entreprise.fr',
    (SELECT id FROM site WHERE ville='Toulouse'),(SELECT id FROM service WHERE nom='Informatique') UNION ALL

-- Toulouse - Logistique
SELECT 'Menard','Adrien','0561303030','0654556677','adrien.menard@entreprise.fr',
    (SELECT id FROM site WHERE ville='Toulouse'),(SELECT id FROM service WHERE nom='Logistique') UNION ALL
SELECT 'Aubry','Clara','0561303031','0654556678','clara.aubry@entreprise.fr',
    (SELECT id FROM site WHERE ville='Toulouse'),(SELECT id FROM service WHERE nom='Logistique') UNION ALL
SELECT 'Marin','Bastien','0561303032','0654556679','bastien.marin@entreprise.fr',
    (SELECT id FROM site WHERE ville='Toulouse'),(SELECT id FROM service WHERE nom='Logistique') UNION ALL

-- Toulouse - RH
SELECT 'Caron','Margaux','0561404040','0665667788','margaux.caron@entreprise.fr',
    (SELECT id FROM site WHERE ville='Toulouse'),(SELECT id FROM service WHERE nom='Ressources Humaines') UNION ALL
SELECT 'Picard','Édouard','0561404041','0665667789','edouard.picard@entreprise.fr',
    (SELECT id FROM site WHERE ville='Toulouse'),(SELECT id FROM service WHERE nom='Ressources Humaines') UNION ALL

-- Nice - Commercial
SELECT 'Vidal','Noemie','0493101010','0676778899','noemie.vidal@entreprise.fr',
    (SELECT id FROM site WHERE ville='Nice'),(SELECT id FROM service WHERE nom='Commercial') UNION ALL
SELECT 'Leclercq','Robin','0493101011','0676778900','robin.leclercq@entreprise.fr',
    (SELECT id FROM site WHERE ville='Nice'),(SELECT id FROM service WHERE nom='Commercial') UNION ALL
SELECT 'Arnaud','Elise','0493101012','0676778901','elise.arnaud@entreprise.fr',
    (SELECT id FROM site WHERE ville='Nice'),(SELECT id FROM service WHERE nom='Commercial') UNION ALL
SELECT 'Gilles','Matthieu','0493101013','0676778902','matthieu.gilles@entreprise.fr',
    (SELECT id FROM site WHERE ville='Nice'),(SELECT id FROM service WHERE nom='Commercial') UNION ALL

-- Nice - Marketing
SELECT 'Perrot','Charlotte','0493202020','0687889900','charlotte.perrot@entreprise.fr',
    (SELECT id FROM site WHERE ville='Nice'),(SELECT id FROM service WHERE nom='Marketing') UNION ALL
SELECT 'Brun','Clément','0493202021','0687889901','clement.brun@entreprise.fr',
    (SELECT id FROM site WHERE ville='Nice'),(SELECT id FROM service WHERE nom='Marketing') UNION ALL
SELECT 'Leclerc','Victoire','0493202022','0687889902','victoire.leclerc@entreprise.fr',
    (SELECT id FROM site WHERE ville='Nice'),(SELECT id FROM service WHERE nom='Marketing') UNION ALL

-- Nice - Accueil
SELECT 'Lamy','Salomé','0493303030','0698990011','salome.lamy@entreprise.fr',
    (SELECT id FROM site WHERE ville='Nice'),(SELECT id FROM service WHERE nom='Accueil') UNION ALL
SELECT 'Pons','Arnaud','0493303031','0698990012','arnaud.pons@entreprise.fr',
    (SELECT id FROM site WHERE ville='Nice'),(SELECT id FROM service WHERE nom='Accueil') UNION ALL

-- Nice - Juridique
SELECT 'Fabre','Constance','0493404040','0609001122','constance.fabre@entreprise.fr',
    (SELECT id FROM site WHERE ville='Nice'),(SELECT id FROM service WHERE nom='Juridique') UNION ALL
SELECT 'Dumas','Raphaël','0493404041','0609001123','raphael.dumas@entreprise.fr',
    (SELECT id FROM site WHERE ville='Nice'),(SELECT id FROM service WHERE nom='Juridique') UNION ALL

-- Lille - Production
SELECT 'Lecomte','Yann','0320101010','0612101010','yann.lecomte@entreprise.fr',
    (SELECT id FROM site WHERE ville='Lille'),(SELECT id FROM service WHERE nom='Production') UNION ALL
SELECT 'Marchand','Stéphanie','0320101011','0612101011','stephanie.marchand@entreprise.fr',
    (SELECT id FROM site WHERE ville='Lille'),(SELECT id FROM service WHERE nom='Production') UNION ALL
SELECT 'Benoit','Clémence','0320101012','0612101012','clemence.benoit@entreprise.fr',
    (SELECT id FROM site WHERE ville='Lille'),(SELECT id FROM service WHERE nom='Production') UNION ALL
SELECT 'Jacquet','Sébastien','0320101013','0612101013','sebastien.jacquet@entreprise.fr',
    (SELECT id FROM site WHERE ville='Lille'),(SELECT id FROM service WHERE nom='Production') UNION ALL

-- Lille - Logistique
SELECT 'Humbert','Thibault','0320202020','0623202020','thibault.humbert@entreprise.fr',
    (SELECT id FROM site WHERE ville='Lille'),(SELECT id FROM service WHERE nom='Logistique') UNION ALL
SELECT 'Collet','Delphine','0320202021','0623202021','delphine.collet@entreprise.fr',
    (SELECT id FROM site WHERE ville='Lille'),(SELECT id FROM service WHERE nom='Logistique') UNION ALL
SELECT 'Gaudin','Yoann','0320202022','0623202022','yoann.gaudin@entreprise.fr',
    (SELECT id FROM site WHERE ville='Lille'),(SELECT id FROM service WHERE nom='Logistique') UNION ALL

-- Lille - Comptabilité
SELECT 'Vasseur','Céline','0320303030','0634303030','celine.vasseur@entreprise.fr',
    (SELECT id FROM site WHERE ville='Lille'),(SELECT id FROM service WHERE nom='Comptabilité') UNION ALL
SELECT 'Michaud','Damien','0320303031','0634303031','damien.michaud@entreprise.fr',
    (SELECT id FROM site WHERE ville='Lille'),(SELECT id FROM service WHERE nom='Comptabilité') UNION ALL
SELECT 'Prevost','Noëlle','0320303032','0634303032','noelle.prevost@entreprise.fr',
    (SELECT id FROM site WHERE ville='Lille'),(SELECT id FROM service WHERE nom='Comptabilité') UNION ALL

-- Lyon - Informatique
SELECT 'Schwartz','Bastien','0472101010','0645404040','bastien.schwartz@entreprise.fr',
    (SELECT id FROM site WHERE ville='Lyon'),(SELECT id FROM service WHERE nom='Informatique') UNION ALL
SELECT 'Riviere','Lucie','0472101011','0645404041','lucie.riviere@entreprise.fr',
    (SELECT id FROM site WHERE ville='Lyon'),(SELECT id FROM service WHERE nom='Informatique') UNION ALL
SELECT 'Guillet','Antoine','0472101012','0645404042','antoine.guillet@entreprise.fr',
    (SELECT id FROM site WHERE ville='Lyon'),(SELECT id FROM service WHERE nom='Informatique') UNION ALL
SELECT 'Bourgeois','Jade','0472101013','0645404043','jade.bourgeois@entreprise.fr',
    (SELECT id FROM site WHERE ville='Lyon'),(SELECT id FROM service WHERE nom='Informatique') UNION ALL

-- Lyon - Commercial
SELECT 'Carpentier','Simon','0472202020','0656505050','simon.carpentier@entreprise.fr',
    (SELECT id FROM site WHERE ville='Lyon'),(SELECT id FROM service WHERE nom='Commercial') UNION ALL
SELECT 'Pasquier','Eva','0472202021','0656505051','eva.pasquier@entreprise.fr',
    (SELECT id FROM site WHERE ville='Lyon'),(SELECT id FROM service WHERE nom='Commercial') UNION ALL
SELECT 'Fleury','Maxence','0472202022','0656505052','maxence.fleury@entreprise.fr',
    (SELECT id FROM site WHERE ville='Lyon'),(SELECT id FROM service WHERE nom='Commercial') UNION ALL

-- Lyon - RH
SELECT 'Tanguy','Zoé','0472303030','0667606060','zoe.tanguy@entreprise.fr',
    (SELECT id FROM site WHERE ville='Lyon'),(SELECT id FROM service WHERE nom='Ressources Humaines') UNION ALL
SELECT 'Renaud','Olivier','0472303031','0667606061','olivier.renaud@entreprise.fr',
    (SELECT id FROM site WHERE ville='Lyon'),(SELECT id FROM service WHERE nom='Ressources Humaines') UNION ALL

-- Lyon - Juridique
SELECT 'Mallet','Véronique','0472404040','0678707070','veronique.mallet@entreprise.fr',
    (SELECT id FROM site WHERE ville='Lyon'),(SELECT id FROM service WHERE nom='Juridique') UNION ALL
SELECT 'Gros','Franck','0472404041','0678707071','franck.gros@entreprise.fr',
    (SELECT id FROM site WHERE ville='Lyon'),(SELECT id FROM service WHERE nom='Juridique') UNION ALL

-- Lyon - Direction
SELECT 'Noel','Christine','0472505050','0689808080','christine.noel@entreprise.fr',
    (SELECT id FROM site WHERE ville='Lyon'),(SELECT id FROM service WHERE nom='Direction') UNION ALL

-- Bordeaux - Production
SELECT 'Coulon','Maxime','0556101010','0691909090','maxime.coulon@entreprise.fr',
    (SELECT id FROM site WHERE ville='Bordeaux'),(SELECT id FROM service WHERE nom='Production') UNION ALL
SELECT 'Tessier','Camille','0556101011','0691909091','camille.tessier@entreprise.fr',
    (SELECT id FROM site WHERE ville='Bordeaux'),(SELECT id FROM service WHERE nom='Production') UNION ALL
SELECT 'Perez','Sandro','0556101012','0691909092','sandro.perez@entreprise.fr',
    (SELECT id FROM site WHERE ville='Bordeaux'),(SELECT id FROM service WHERE nom='Production') UNION ALL
SELECT 'Allard','Nathalie','0556101013','0691909093','nathalie.allard@entreprise.fr',
    (SELECT id FROM site WHERE ville='Bordeaux'),(SELECT id FROM service WHERE nom='Production') UNION ALL

-- Bordeaux - Marketing
SELECT 'Boulanger','Eliot','0556202020','0602010101','eliot.boulanger@entreprise.fr',
    (SELECT id FROM site WHERE ville='Bordeaux'),(SELECT id FROM service WHERE nom='Marketing') UNION ALL
SELECT 'Lemoine','Vanessa','0556202021','0602010102','vanessa.lemoine@entreprise.fr',
    (SELECT id FROM site WHERE ville='Bordeaux'),(SELECT id FROM service WHERE nom='Marketing') UNION ALL
SELECT 'Briand','Axel','0556202022','0602010103','axel.briand@entreprise.fr',
    (SELECT id FROM site WHERE ville='Bordeaux'),(SELECT id FROM service WHERE nom='Marketing') UNION ALL

-- Bordeaux - Comptabilité
SELECT 'Hebert','Sonia','0556303030','0613111111','sonia.hebert@entreprise.fr',
    (SELECT id FROM site WHERE ville='Bordeaux'),(SELECT id FROM service WHERE nom='Comptabilité') UNION ALL
SELECT 'Charrier','Grégory','0556303031','0613111112','gregory.charrier@entreprise.fr',
    (SELECT id FROM site WHERE ville='Bordeaux'),(SELECT id FROM service WHERE nom='Comptabilité') UNION ALL

-- Bordeaux - Accueil
SELECT 'Guillot','Amandine','0556404040','0624222222','amandine.guillot@entreprise.fr',
    (SELECT id FROM site WHERE ville='Bordeaux'),(SELECT id FROM service WHERE nom='Accueil') UNION ALL
SELECT 'Paris','Kévin','0556404041','0624222223','kevin.paris@entreprise.fr',
    (SELECT id FROM site WHERE ville='Bordeaux'),(SELECT id FROM service WHERE nom='Accueil') UNION ALL

-- Strasbourg - Informatique
SELECT 'Schmitt','Laetitia','0388101010','0635333333','laetitia.schmitt@entreprise.fr',
    (SELECT id FROM site WHERE ville='Strasbourg'),(SELECT id FROM service WHERE nom='Informatique') UNION ALL
SELECT 'Wagner','Mathieu','0388101011','0635333334','mathieu.wagner@entreprise.fr',
    (SELECT id FROM site WHERE ville='Strasbourg'),(SELECT id FROM service WHERE nom='Informatique') UNION ALL
SELECT 'Klein','Dorothée','0388101012','0635333335','dorothee.klein@entreprise.fr',
    (SELECT id FROM site WHERE ville='Strasbourg'),(SELECT id FROM service WHERE nom='Informatique') UNION ALL

-- Strasbourg - Logistique
SELECT 'Weber','Nicolas','0388202020','0646444444','nicolas.weber@entreprise.fr',
    (SELECT id FROM site WHERE ville='Strasbourg'),(SELECT id FROM service WHERE nom='Logistique') UNION ALL
SELECT 'Fritz','Amélie','0388202021','0646444445','amelie.fritz@entreprise.fr',
    (SELECT id FROM site WHERE ville='Strasbourg'),(SELECT id FROM service WHERE nom='Logistique') UNION ALL
SELECT 'Muller','Sébastien','0388202022','0646444446','sebastien.muller@entreprise.fr',
    (SELECT id FROM site WHERE ville='Strasbourg'),(SELECT id FROM service WHERE nom='Logistique') UNION ALL

-- Strasbourg - Production
SELECT 'Becker','Elodie','0388303030','0657555555','elodie.becker@entreprise.fr',
    (SELECT id FROM site WHERE ville='Strasbourg'),(SELECT id FROM service WHERE nom='Production') UNION ALL
SELECT 'Hoffmann','Romain','0388303031','0657555556','romain.hoffmann@entreprise.fr',
    (SELECT id FROM site WHERE ville='Strasbourg'),(SELECT id FROM service WHERE nom='Production') UNION ALL
SELECT 'Meyer','Virginie','0388303032','0657555557','virginie.meyer@entreprise.fr',
    (SELECT id FROM site WHERE ville='Strasbourg'),(SELECT id FROM service WHERE nom='Production') UNION ALL

-- Strasbourg - Commercial
SELECT 'Schultz','Thomas','0388404040','0668666666','thomas.schultz@entreprise.fr',
    (SELECT id FROM site WHERE ville='Strasbourg'),(SELECT id FROM service WHERE nom='Commercial') UNION ALL
SELECT 'Wolff','Marion','0388404041','0668666667','marion.wolff@entreprise.fr',
    (SELECT id FROM site WHERE ville='Strasbourg'),(SELECT id FROM service WHERE nom='Commercial') UNION ALL

-- Rennes - RH
SELECT 'Lebrun','Anaëlle','0299101010','0679777777','anaelle.lebrun@entreprise.fr',
    (SELECT id FROM site WHERE ville='Rennes'),(SELECT id FROM service WHERE nom='Ressources Humaines') UNION ALL
SELECT 'Aubert','François','0299101011','0679777778','francois.aubert@entreprise.fr',
    (SELECT id FROM site WHERE ville='Rennes'),(SELECT id FROM service WHERE nom='Ressources Humaines') UNION ALL
SELECT 'Denis','Sandrine','0299101012','0679777779','sandrine.denis@entreprise.fr',
    (SELECT id FROM site WHERE ville='Rennes'),(SELECT id FROM service WHERE nom='Ressources Humaines') UNION ALL

-- Rennes - Commercial
SELECT 'Duval','Bertrand','0299202020','0680888888','bertrand.duval@entreprise.fr',
    (SELECT id FROM site WHERE ville='Rennes'),(SELECT id FROM service WHERE nom='Commercial') UNION ALL
SELECT 'Perret','Isabelle','0299202021','0680888889','isabelle.perret@entreprise.fr',
    (SELECT id FROM site WHERE ville='Rennes'),(SELECT id FROM service WHERE nom='Commercial') UNION ALL
SELECT 'Dupre','Antoine','0299202022','0680888890','antoine.dupre@entreprise.fr',
    (SELECT id FROM site WHERE ville='Rennes'),(SELECT id FROM service WHERE nom='Commercial') UNION ALL

-- Rennes - Comptabilité
SELECT 'Morin','Emilie','0299303030','0691999999','emilie.morin@entreprise.fr',
    (SELECT id FROM site WHERE ville='Rennes'),(SELECT id FROM service WHERE nom='Comptabilité') UNION ALL
SELECT 'Leger','Philippe','0299303031','0691999990','philippe.leger@entreprise.fr',
    (SELECT id FROM site WHERE ville='Rennes'),(SELECT id FROM service WHERE nom='Comptabilité') UNION ALL

-- Rennes - Production
SELECT 'Gillet','Florent','0299404040','0602888881','florent.gillet@entreprise.fr',
    (SELECT id FROM site WHERE ville='Rennes'),(SELECT id FROM service WHERE nom='Production') UNION ALL
SELECT 'Hubert','Morgane','0299404041','0602888882','morgane.hubert@entreprise.fr',
    (SELECT id FROM site WHERE ville='Rennes'),(SELECT id FROM service WHERE nom='Production') UNION ALL

-- Montpellier - Marketing
SELECT 'Thibault','Carla','0467101010','0613777771','carla.thibault@entreprise.fr',
    (SELECT id FROM site WHERE ville='Montpellier'),(SELECT id FROM service WHERE nom='Marketing') UNION ALL
SELECT 'Pages','Hugo','0467101011','0613777772','hugo.pages@entreprise.fr',
    (SELECT id FROM site WHERE ville='Montpellier'),(SELECT id FROM service WHERE nom='Marketing') UNION ALL
SELECT 'Causse','Noémie','0467101012','0613777773','noemie.causse@entreprise.fr',
    (SELECT id FROM site WHERE ville='Montpellier'),(SELECT id FROM service WHERE nom='Marketing') UNION ALL

-- Montpellier - Juridique
SELECT 'Barre','Olivier','0467202020','0624666661','olivier.barre@entreprise.fr',
    (SELECT id FROM site WHERE ville='Montpellier'),(SELECT id FROM service WHERE nom='Juridique') UNION ALL
SELECT 'Sicard','Mathilde','0467202021','0624666662','mathilde.sicard@entreprise.fr',
    (SELECT id FROM site WHERE ville='Montpellier'),(SELECT id FROM service WHERE nom='Juridique') UNION ALL

-- Montpellier - Logistique
SELECT 'Serres','Lionel','0467303030','0635555551','lionel.serres@entreprise.fr',
    (SELECT id FROM site WHERE ville='Montpellier'),(SELECT id FROM service WHERE nom='Logistique') UNION ALL
SELECT 'Combes','Alexia','0467303031','0635555552','alexia.combes@entreprise.fr',
    (SELECT id FROM site WHERE ville='Montpellier'),(SELECT id FROM service WHERE nom='Logistique') UNION ALL
SELECT 'Chabrier','Ethan','0467303032','0635555553','ethan.chabrier@entreprise.fr',
    (SELECT id FROM site WHERE ville='Montpellier'),(SELECT id FROM service WHERE nom='Logistique') UNION ALL

-- Marseille - Production
SELECT 'Bonhomme','Christelle','0491101010','0646444441','christelle.bonhomme@entreprise.fr',
    (SELECT id FROM site WHERE ville='Marseille'),(SELECT id FROM service WHERE nom='Production') UNION ALL
SELECT 'Castel','Jérôme','0491101011','0646444442','jerome.castel@entreprise.fr',
    (SELECT id FROM site WHERE ville='Marseille'),(SELECT id FROM service WHERE nom='Production') UNION ALL
SELECT 'Olive','Nadia','0491101012','0646444443','nadia.olive@entreprise.fr',
    (SELECT id FROM site WHERE ville='Marseille'),(SELECT id FROM service WHERE nom='Production') UNION ALL
SELECT 'Amalric','Cyril','0491101013','0646444444','cyril.amalric@entreprise.fr',
    (SELECT id FROM site WHERE ville='Marseille'),(SELECT id FROM service WHERE nom='Production') UNION ALL

-- Marseille - Commercial
SELECT 'Raynaud','Estelle','0491202020','0657333331','estelle.raynaud@entreprise.fr',
    (SELECT id FROM site WHERE ville='Marseille'),(SELECT id FROM service WHERE nom='Commercial') UNION ALL
SELECT 'Ravel','Bruno','0491202021','0657333332','bruno.ravel@entreprise.fr',
    (SELECT id FROM site WHERE ville='Marseille'),(SELECT id FROM service WHERE nom='Commercial') UNION ALL
SELECT 'Isnard','Anissa','0491202022','0657333333','anissa.isnard@entreprise.fr',
    (SELECT id FROM site WHERE ville='Marseille'),(SELECT id FROM service WHERE nom='Commercial') UNION ALL

-- Marseille - Accueil
SELECT 'Conte','Jérôme','0491303030','0668222221','jerome.conte@entreprise.fr',
    (SELECT id FROM site WHERE ville='Marseille'),(SELECT id FROM service WHERE nom='Accueil') UNION ALL
SELECT 'Borel','Patricia','0491303031','0668222222','patricia.borel@entreprise.fr',
    (SELECT id FROM site WHERE ville='Marseille'),(SELECT id FROM service WHERE nom='Accueil') UNION ALL

-- Marseille - Informatique
SELECT 'Pasqual','Dylan','0491404040','0679111111','dylan.pasqual@entreprise.fr',
    (SELECT id FROM site WHERE ville='Marseille'),(SELECT id FROM service WHERE nom='Informatique') UNION ALL
SELECT 'Ferrara','Laura','0491404041','0679111112','laura.ferrara@entreprise.fr',
    (SELECT id FROM site WHERE ville='Marseille'),(SELECT id FROM service WHERE nom='Informatique') UNION ALL

-- Grenoble - Informatique
SELECT 'Bois','Gaëtan','0476101010','0680000001','gaetan.bois@entreprise.fr',
    (SELECT id FROM site WHERE ville='Grenoble'),(SELECT id FROM service WHERE nom='Informatique') UNION ALL
SELECT 'Grange','Nelly','0476101011','0680000002','nelly.grange@entreprise.fr',
    (SELECT id FROM site WHERE ville='Grenoble'),(SELECT id FROM service WHERE nom='Informatique') UNION ALL
SELECT 'Faure','Thibaud','0476101012','0680000003','thibaud.faure@entreprise.fr',
    (SELECT id FROM site WHERE ville='Grenoble'),(SELECT id FROM service WHERE nom='Informatique') UNION ALL

-- Grenoble - Production
SELECT 'Barret','Lucie','0476202020','0691111111','lucie.barret@entreprise.fr',
    (SELECT id FROM site WHERE ville='Grenoble'),(SELECT id FROM service WHERE nom='Production') UNION ALL
SELECT 'Couturier','Benoît','0476202021','0691111112','benoit.couturier@entreprise.fr',
    (SELECT id FROM site WHERE ville='Grenoble'),(SELECT id FROM service WHERE nom='Production') UNION ALL
SELECT 'Mouton','Eloïse','0476202022','0691111113','eloise.mouton@entreprise.fr',
    (SELECT id FROM site WHERE ville='Grenoble'),(SELECT id FROM service WHERE nom='Production') UNION ALL

-- Grenoble - Comptabilité
SELECT 'Salles','Olivier','0476303030','0602222221','olivier.salles@entreprise.fr',
    (SELECT id FROM site WHERE ville='Grenoble'),(SELECT id FROM service WHERE nom='Comptabilité') UNION ALL
SELECT 'Bret','Sylvie','0476303031','0602222222','sylvie.bret@entreprise.fr',
    (SELECT id FROM site WHERE ville='Grenoble'),(SELECT id FROM service WHERE nom='Comptabilité') UNION ALL

-- Dijon - Production
SELECT 'Vaillant','Kevin','0380101010','0613333331','kevin.vaillant@entreprise.fr',
    (SELECT id FROM site WHERE ville='Dijon'),(SELECT id FROM service WHERE nom='Production') UNION ALL
SELECT 'Baudoin','Mélanie','0380101011','0613333332','melanie.baudoin@entreprise.fr',
    (SELECT id FROM site WHERE ville='Dijon'),(SELECT id FROM service WHERE nom='Production') UNION ALL
SELECT 'Deschamps','Julien','0380101012','0613333333','julien.deschamps@entreprise.fr',
    (SELECT id FROM site WHERE ville='Dijon'),(SELECT id FROM service WHERE nom='Production') UNION ALL

-- Dijon - Commercial
SELECT 'Bourdin','Hélène','0380202020','0624444441','helene.bourdin@entreprise.fr',
    (SELECT id FROM site WHERE ville='Dijon'),(SELECT id FROM service WHERE nom='Commercial') UNION ALL
SELECT 'Tissot','Nicolas','0380202021','0624444442','nicolas.tissot@entreprise.fr',
    (SELECT id FROM site WHERE ville='Dijon'),(SELECT id FROM service WHERE nom='Commercial') UNION ALL

-- Dijon - Logistique
SELECT 'Cartier','Fanny','0380303030','0635555551','fanny.cartier@entreprise.fr',
    (SELECT id FROM site WHERE ville='Dijon'),(SELECT id FROM service WHERE nom='Logistique') UNION ALL
SELECT 'Rolland','Denis','0380303031','0635555552','denis.rolland@entreprise.fr',
    (SELECT id FROM site WHERE ville='Dijon'),(SELECT id FROM service WHERE nom='Logistique') UNION ALL

-- Rouen - Informatique
SELECT 'Lemesle','Pauline','0235101010','0646666661','pauline.lemesle@entreprise.fr',
    (SELECT id FROM site WHERE ville='Rouen'),(SELECT id FROM service WHERE nom='Informatique') UNION ALL
SELECT 'Lerouge','Charles','0235101011','0646666662','charles.lerouge@entreprise.fr',
    (SELECT id FROM site WHERE ville='Rouen'),(SELECT id FROM service WHERE nom='Informatique') UNION ALL

-- Rouen - RH
SELECT 'Morel','Gaëlle','0235202020','0657777771','gaelle.morel@entreprise.fr',
    (SELECT id FROM site WHERE ville='Rouen'),(SELECT id FROM service WHERE nom='Ressources Humaines') UNION ALL
SELECT 'Lefevre','Stéphane','0235202021','0657777772','stephane.lefevre@entreprise.fr',
    (SELECT id FROM site WHERE ville='Rouen'),(SELECT id FROM service WHERE nom='Ressources Humaines') UNION ALL

-- Rouen - Production
SELECT 'Langlois','Cécile','0235303030','0668888881','cecile.langlois@entreprise.fr',
    (SELECT id FROM site WHERE ville='Rouen'),(SELECT id FROM service WHERE nom='Production') UNION ALL
SELECT 'Guyot','Marc','0235303031','0668888882','marc.guyot@entreprise.fr',
    (SELECT id FROM site WHERE ville='Rouen'),(SELECT id FROM service WHERE nom='Production') UNION ALL
SELECT 'Bonin','Laure','0235303032','0668888883','laure.bonin@entreprise.fr',
    (SELECT id FROM site WHERE ville='Rouen'),(SELECT id FROM service WHERE nom='Production') UNION ALL

-- Reims - Commercial
SELECT 'Drouet','Valère','0326101010','0679999991','valere.drouet@entreprise.fr',
    (SELECT id FROM site WHERE ville='Reims'),(SELECT id FROM service WHERE nom='Commercial') UNION ALL
SELECT 'Jacobs','Ophélie','0326101011','0679999992','ophelie.jacobs@entreprise.fr',
    (SELECT id FROM site WHERE ville='Reims'),(SELECT id FROM service WHERE nom='Commercial') UNION ALL

-- Reims - Accueil
SELECT 'Thierry','Véronique','0326202020','0680000001','veronique.thierry@entreprise.fr',
    (SELECT id FROM site WHERE ville='Reims'),(SELECT id FROM service WHERE nom='Accueil') UNION ALL
SELECT 'Bouchard','Étienne','0326202021','0680000002','etienne.bouchard@entreprise.fr',
    (SELECT id FROM site WHERE ville='Reims'),(SELECT id FROM service WHERE nom='Accueil') UNION ALL

-- Reims - Comptabilité
SELECT 'Camus','Flavie','0326303030','0691111101','flavie.camus@entreprise.fr',
    (SELECT id FROM site WHERE ville='Reims'),(SELECT id FROM service WHERE nom='Comptabilité') UNION ALL
SELECT 'Pichon','Cédric','0326303031','0691111102','cedric.pichon@entreprise.fr',
    (SELECT id FROM site WHERE ville='Reims'),(SELECT id FROM service WHERE nom='Comptabilité') UNION ALL

-- Reims - Logistique
SELECT 'Meunier','Agnès','0326404040','0602222201','agnes.meunier@entreprise.fr',
    (SELECT id FROM site WHERE ville='Reims'),(SELECT id FROM service WHERE nom='Logistique') UNION ALL
SELECT 'Blot','Christophe','0326404041','0602222202','christophe.blot@entreprise.fr',
    (SELECT id FROM site WHERE ville='Reims'),(SELECT id FROM service WHERE nom='Logistique')

) AS tmp;

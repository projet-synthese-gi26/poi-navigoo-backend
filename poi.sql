-- 1. Extensions nécessaires
CREATE EXTENSION IF NOT EXISTS postgis;
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- 2. Types composites pour adresse et contact
DO $$ BEGIN
    CREATE TYPE address_type AS (
        street_number TEXT,
        street_name TEXT,
        city TEXT,
        state_province TEXT,
        postal_code TEXT,
        country TEXT,
        informal_address TEXT	-- e.g. Situé à 200m de Total Mokolo Melen
    );
EXCEPTION WHEN duplicate_object THEN NULL; END $$;

DO $$ BEGIN
    CREATE TYPE contact_person_type AS (
        name TEXT,
        role TEXT,
        phone TEXT,
        email TEXT
    );
EXCEPTION WHEN duplicate_object THEN NULL; END $$;

-- 3. Table principale : Points d’Intérêt
CREATE TABLE IF NOT EXISTS point_of_interest (
    poi_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    town_id UUID,                      -- FK vers table de villes
    created_by_user_id UUID,           -- FK vers table des utilisateurs

    poi_name TEXT NOT NULL,
    poi_type TEXT NOT NULL,            -- Ex: 'RESTAURANT', 'HOTEL'
    poi_category TEXT NOT NULL,        -- Ex: 'Food & Drink', 'Transport'
    poi_long_name TEXT,
    poi_short_name TEXT,
    poi_friendly_name TEXT,
    poi_description TEXT,

    poi_logo BYTEA,                    -- Pour petites images/logo
    poi_images TEXT[],                 -- Tableau d'URLs

    location_geog GEOGRAPHY(Point, 4326) NOT NULL, -- Latitude/Longitude

    poi_address address_type,          -- Adresse structurée

    phone_number TEXT,
    website_url TEXT,

    poi_amenities TEXT[],              -- Ex: ['Wi-Fi', 'Parking', "Jardin"]
    poi_keywords TEXT[],               -- Ex: ["BUS STOP", "HOPITAL", "CENTRE VILLE"]
    poi_type_tags TEXT[],              -- Tags complémentaires

    operation_time_plan JSONB,         -- Exemple: { "Monday": "8:00-18:00", ... }

    poi_contacts contact_person_type[],-- Plusieurs contacts possibles

    popularity_score FLOAT DEFAULT 0,  -- Score calculé

    is_active BOOLEAN DEFAULT TRUE, -- Modération par le super admin
    deactivation_reason TEXT,        -- Motif de désactivation
    deactivated_by_user_id UUID REFERENCES app_user(user_id), -- User qui a désactivé, souvent le super admin

    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_by_user_id UUID,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- 4. Table pour organisations/tenants (si non existante)
-- Permet d’associer toute action/statistique à l’organisation cliente.
CREATE TABLE IF NOT EXISTS organization (
    organization_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    org_name TEXT NOT NULL,
    org_code TEXT UNIQUE,
    org_type TEXT, -- (optionnel) : Entreprise, collectivité, etc.
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
    is_active BOOLEAN DEFAULT TRUE
   
);

-- 4. Table des utilisateurs (rattachés à une organisation)
CREATE TABLE IF NOT EXISTS app_user (
    user_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    org_id UUID NOT NULL REFERENCES organization(org_id) ON DELETE CASCADE,
    username TEXT NOT NULL,
    email TEXT,
    phone TEXT,
    password_hash TEXT,
    role TEXT NOT NULL, -- e.g. 'USER', 'ADMIN', 'SUPER_ADMIN'
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);


-- 5. Table pour les accès/consultations/statistiques par plateforme et organisation
CREATE TABLE IF NOT EXISTS poi_access_log (
    access_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    poi_id UUID NOT NULL REFERENCES point_of_interest(poi_id) ON DELETE CASCADE,
    organization_id UUID NOT NULL REFERENCES organization(organization_id) ON DELETE CASCADE,
    platform_type TEXT NOT NULL,    -- 'Android', 'iOS', 'Linux', 'Web', 'Windows', etc.
    user_id UUID,                   -- Utilisateur effectif (optionnel, selon anonymat)
    access_type TEXT,               -- 'view', 'click', 'review', etc.
    access_datetime TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    metadata JSONB                  -- Détail additionnel (IP, User-Agent, version app, etc.)
);

-- 6. Table des reviews (avis/commentaires) avec rattachement à l’organisation et à la plateforme
CREATE TABLE IF NOT EXISTS poi_review (
    review_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    poi_id UUID NOT NULL REFERENCES point_of_interest(poi_id) ON DELETE CASCADE,
    user_id UUID NOT NULL, -- FK vers utilisateurs
    organization_id UUID NOT NULL REFERENCES organization(organization_id) ON DELETE CASCADE,
    platform_type TEXT NOT NULL,    -- Plateforme source du review
    rating INT CHECK (rating >= 1 AND rating <= 5),
    review_text TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    likes INT DEFAULT 0,
    dislikes INT DEFAULT 0
);

-- 7. Table de statistiques d’usage par organisation et plateforme (usage analytique)
CREATE TABLE IF NOT EXISTS poi_platform_stat (
    stat_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    org_id UUID NOT NULL REFERENCES organization(org_id) ON DELETE CASCADE,
    poi_id UUID REFERENCES point_of_interest(poi_id) ON DELETE CASCADE,
    platform_type TEXT NOT NULL, -- e.g. 'IOS', 'ANDROID', 'LINUX', etc.
    stat_date DATE NOT NULL,
    views INT DEFAULT 0,
    reviews INT DEFAULT 0,
    likes INT DEFAULT 0,
    dislikes INT DEFAULT 0
    -- Ajoute ici d'autres KPIs si besoin (ex: favoris, partages)
);


-- 7. Indexes pour performance (tables principales et logs)
CREATE INDEX IF NOT EXISTS idx_poi_org_id         ON point_of_interest (org_id);
CREATE INDEX IF NOT EXISTS idx_poi_type            ON point_of_interest (poi_type);
CREATE INDEX IF NOT EXISTS idx_poi_category        ON point_of_interest (poi_category);
CREATE INDEX IF NOT EXISTS idx_poi_name            ON point_of_interest (poi_name);
CREATE INDEX IF NOT EXISTS idx_poi_phone_number    ON point_of_interest (phone_number);

CREATE INDEX IF NOT EXISTS idx_poi_is_active      ON point_of_interest (is_active);

CREATE INDEX IF NOT EXISTS idx_poi_keywords        ON point_of_interest USING GIN (poi_keywords);
CREATE INDEX IF NOT EXISTS idx_poi_amenities       ON point_of_interest USING GIN (poi_amenities);
CREATE INDEX IF NOT EXISTS idx_poi_type_tags       ON point_of_interest USING GIN (poi_type_tags);

CREATE INDEX IF NOT EXISTS idx_poi_location_geog   ON point_of_interest USING GIST (location_geog);

CREATE INDEX IF NOT EXISTS idx_access_log_poi      ON poi_access_log (poi_id);
CREATE INDEX IF NOT EXISTS idx_access_log_org      ON poi_access_log (organization_id);
CREATE INDEX IF NOT EXISTS idx_access_log_platform ON poi_access_log (platform_type);
CREATE INDEX IF NOT EXISTS idx_access_log_date     ON poi_access_log (access_datetime);

CREATE INDEX IF NOT EXISTS idx_poi_review_poi_id   ON poi_review (poi_id);
CREATE INDEX IF NOT EXISTS idx_poi_review_user_id  ON poi_review (user_id);
CREATE INDEX IF NOT EXISTS idx_poi_review_org      ON poi_review (organization_id);
CREATE INDEX IF NOT EXISTS idx_poi_review_platform ON poi_review (platform_type);
CREATE INDEX IF NOT EXISTS idx_stat_org_platform  ON poi_platform_stat (org_id, platform_type, stat_date);

-- 8. Vues matérialisées pour agrégats rapides multi-tenants / multi-plateformes

-- Statistiques d’accès par type de plateforme et organisation
CREATE MATERIALIZED VIEW IF NOT EXISTS mv_stats_poi_access_by_platform_org AS
SELECT
    organization_id,
    platform_type,
    poi_id,
    COUNT(*) AS access_count,
    MIN(access_datetime) AS first_access,
    MAX(access_datetime) AS last_access
FROM
    poi_access_log
GROUP BY organization_id, platform_type, poi_id;

-- Statistiques reviews par plateforme et organisation
CREATE MATERIALIZED VIEW IF NOT EXISTS mv_stats_poi_reviews_by_platform_org AS
SELECT
    organization_id,
    platform_type,
    poi_id,
    COUNT(*) AS review_count,
    AVG(rating) AS avg_rating,
    SUM(likes) AS total_likes,
    SUM(dislikes) AS total_dislikes
FROM
    poi_review
GROUP BY organization_id, platform_type, poi_id;

-- Statistiques globales classiques
CREATE MATERIALIZED VIEW IF NOT EXISTS mv_poi_by_type AS
SELECT org_id, poi_type, COUNT(*) AS poi_count
FROM point_of_interest
WHERE is_active
GROUP BY org_id, poi_type;
WHERE is_active
GROUP BY org_id, poi_type;

CREATE MATERIALIZED VIEW IF NOT EXISTS mv_poi_by_category AS
SELECT org_id, poi_category, COUNT(*) AS poi_count
FROM point_of_interest
WHERE is_active
GROUP BY org_id, poi_category;

CREATE MATERIALIZED VIEW IF NOT EXISTS mv_poi_by_popularity AS
SELECT
    org_id,
    poi_id,
    poi_name,
    poi_type,
    poi_category,
    location_geog,
    popularity_score
FROM point_of_interest
WHERE is_active
ORDER BY org_id, popularity_score DESC;


-- 9. Exemple de recherche spatiale : POI dans un rayon donné (ici 5 km)
-- Remplacer :lon et :lat par les valeurs réelles
/*
SELECT
    poi_id,
    poi_name,
    poi_type,
    poi_category,
    ST_AsText(location_geog) AS location_wkt,
    ST_Distance(location_geog, ST_SetSRID(ST_MakePoint(:lon, :lat), 4326)::geography) AS distance_meters
FROM
    point_of_interest
WHERE
    ST_DWithin(
        location_geog,
        ST_SetSRID(ST_MakePoint(:lon, :lat), 4326)::geography,
        5000
    )
ORDER BY
    distance_meters;
*/

-- 10. Conseils de maintenance
-- REFRESH MATERIALIZED VIEW mv_stats_poi_access_by_platform_org;
-- REFRESH MATERIALIZED VIEW mv_stats_poi_reviews_by_platform_org;
-- REFRESH MATERIALIZED VIEW mv_poi_by_type;
-- REFRESH MATERIALIZED VIEW mv_poi_by_category;
-- REFRESH MATERIALIZED VIEW mv_poi_by_popularity;
-- (Automatisable via pg_cron, pgAgent, ou tâches programmées côté backend)

-- 10. Exemple de recherche spatiale (multi-tenant & activation)
-- Paramètres : :org_id, :lon, :lat
/*
SELECT
    poi_id,
    poi_name,
    poi_type,
    poi_category,
    ST_AsText(location_geog) AS location_wkt,
    ST_Distance(location_geog, ST_SetSRID(ST_MakePoint(:lon, :lat), 4326)::geography) AS distance_meters
FROM
    point_of_interest
WHERE
    org_id = :org_id
    AND is_active
    AND ST_DWithin(
        location_geog,
        ST_SetSRID(ST_MakePoint(:lon, :lat), 4326)::geography,
        5000
    )
ORDER BY
    distance_meters;
*/

-- 11. Conseils de maintenance
-- REFRESH MATERIALIZED VIEW mv_poi_by_type;
-- REFRESH MATERIALIZED VIEW mv_poi_by_category;
-- REFRESH MATERIALIZED VIEW mv_poi_by_popularity;
-- (Automatisable via pg_cron ou un backend)


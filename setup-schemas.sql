-- Ejecuta este script en el SQL Editor de NeonDB
-- NeonDB Console > SQL Editor > Pega aqui y ejecuta

CREATE SCHEMA IF NOT EXISTS auth_schema;
CREATE SCHEMA IF NOT EXISTS product_schema;
CREATE SCHEMA IF NOT EXISTS sale_schema;

-- Los servicios crearan sus tablas automaticamente con JPA/Hibernate
-- al iniciar por primera vez (ddl-auto: update)

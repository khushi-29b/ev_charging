-- Schema inferred from the DAO classes in src/com/ev/dao
-- Review and adjust column types/constraints as needed for your actual data.

CREATE TABLE IF NOT EXISTS discom (
    discom_id   SERIAL PRIMARY KEY,
    discom_name VARCHAR(150) NOT NULL
);

CREATE TABLE IF NOT EXISTS charging_station (
    station_id      SERIAL PRIMARY KEY,
    operator_name   VARCHAR(150) NOT NULL,
    latitude        DOUBLE PRECISION NOT NULL,
    longitude       DOUBLE PRECISION NOT NULL,
    discom_id       INTEGER REFERENCES discom(discom_id),
    transformer_id  INTEGER
);

CREATE TABLE IF NOT EXISTS "User" (
    user_id    SERIAL PRIMARY KEY,
    kyc_status VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS vehicle (
    vehicle_id        SERIAL PRIMARY KEY,
    user_id           INTEGER REFERENCES "User"(user_id),
    make              VARCHAR(100),
    battery_capacity  DOUBLE PRECISION
);

CREATE TABLE IF NOT EXISTS connector (
    connector_id    SERIAL PRIMARY KEY,
    connector_type  VARCHAR(50),
    station_id      INTEGER REFERENCES charging_station(station_id)
);

CREATE TABLE IF NOT EXISTS charging_session (
    session_id    SERIAL PRIMARY KEY,
    connector_id  INTEGER REFERENCES connector(connector_id),
    vehicle_id    INTEGER REFERENCES vehicle(vehicle_id),
    start_time    TIMESTAMP DEFAULT NOW(),
    end_time      TIMESTAMP,
    total_kwh     DOUBLE PRECISION
);

CREATE TABLE IF NOT EXISTS maintenance_ticket (
    ticket_id    SERIAL PRIMARY KEY,
    station_id   INTEGER REFERENCES charging_station(station_id),
    issue_desc   TEXT,
    opened_time  TIMESTAMP DEFAULT NOW(),
    closed_time  TIMESTAMP
);

CREATE TABLE IF NOT EXISTS payment (
    payment_id      SERIAL PRIMARY KEY,
    session_id      INTEGER REFERENCES charging_session(session_id),
    payment_method  VARCHAR(50),
    amount          NUMERIC(10,2)
);

-- Optional: a little sample data so the app isn't empty on first load
INSERT INTO discom (discom_name) VALUES ('Sample DISCOM') ON CONFLICT DO NOTHING;

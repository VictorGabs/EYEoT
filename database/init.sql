-- ** Database generated with pgModeler (PostgreSQL Database Modeler).
-- ** pgModeler version: 1.2.3
-- ** PostgreSQL version: 18.0
-- ** Project Site: pgmodeler.io
-- ** Model Author: ---
-- object: eyeot | type: ROLE --
-- DROP ROLE IF EXISTS eyeot;
CREATE ROLE eyeot WITH 
	CREATEDB
	INHERIT
	LOGIN
	 PASSWORD '********';
-- ddl-end --


-- ** Database creation must be performed outside a multi lined SQL file. 
-- ** These commands were put in this file only as a convenience.

-- object: eyeot | type: DATABASE --
-- DROP DATABASE IF EXISTS eyeot;
CREATE DATABASE eyeot
	ENCODING = 'UTF8'
	LC_COLLATE = 'pt_BR.UTF-8'
	LC_CTYPE = 'pt_BR.UTF-8'
	TABLESPACE = pg_default
	OWNER = eyeot;
-- ddl-end --


SET check_function_bodies = false;
-- ddl-end --

SET search_path TO pg_catalog,public;
-- ddl-end --

-- object: public.eyeot_id_user_sequence | type: SEQUENCE --
-- DROP SEQUENCE IF EXISTS public.eyeot_id_user_sequence CASCADE;
CREATE SEQUENCE public.eyeot_id_user_sequence
	INCREMENT BY 1
	MINVALUE 0
	MAXVALUE 2147483647
	START WITH 1
	CACHE 1
	NO CYCLE
	OWNED BY NONE;

-- ddl-end --
ALTER SEQUENCE public.eyeot_id_user_sequence OWNER TO eyeot;
-- ddl-end --

-- object: public.eyeot_id_domain_sequence | type: SEQUENCE --
-- DROP SEQUENCE IF EXISTS public.eyeot_id_domain_sequence CASCADE;
CREATE SEQUENCE public.eyeot_id_domain_sequence
	INCREMENT BY 1
	MINVALUE 0
	MAXVALUE 2147483647
	START WITH 1
	CACHE 1
	NO CYCLE
	OWNED BY NONE;

-- ddl-end --
ALTER SEQUENCE public.eyeot_id_domain_sequence OWNER TO eyeot;
-- ddl-end --

-- object: public.eyeot_id_device_sequence | type: SEQUENCE --
-- DROP SEQUENCE IF EXISTS public.eyeot_id_device_sequence CASCADE;
CREATE SEQUENCE public.eyeot_id_device_sequence
	INCREMENT BY 1
	MINVALUE 0
	MAXVALUE 2147483647
	START WITH 1
	CACHE 1
	NO CYCLE
	OWNED BY NONE;

-- ddl-end --
ALTER SEQUENCE public.eyeot_id_device_sequence OWNER TO eyeot;
-- ddl-end --

-- object: public.omni_id_telemetry_sequence | type: SEQUENCE --
-- DROP SEQUENCE IF EXISTS public.omni_id_telemetry_sequence CASCADE;
CREATE SEQUENCE public.omni_id_telemetry_sequence
	INCREMENT BY 1
	MINVALUE 0
	MAXVALUE 2147483647
	START WITH 1
	CACHE 1
	NO CYCLE
	OWNED BY NONE;

-- ddl-end --
ALTER SEQUENCE public.omni_id_telemetry_sequence OWNER TO eyeot;
-- ddl-end --

-- object: public.omni_telemetrydata_accelerometer | type: TABLE --
-- DROP TABLE IF EXISTS public.omni_telemetrydata_accelerometer CASCADE;
CREATE TABLE public.omni_telemetrydata_accelerometer (
	id_telemetrydata bigint NOT NULL,
	index smallint NOT NULL,
	xvalue double precision,
	yvalue double precision,
	zvalue double precision,
	CONSTRAINT omni_telemetrydata_accelerometer_pk PRIMARY KEY (id_telemetrydata,index)
);
-- ddl-end --
ALTER TABLE public.omni_telemetrydata_accelerometer OWNER TO eyeot;
-- ddl-end --

-- object: public.omni_telemetrydata_gps | type: TABLE --
-- DROP TABLE IF EXISTS public.omni_telemetrydata_gps CASCADE;
CREATE TABLE public.omni_telemetrydata_gps (
	id_telemetrydata bigint NOT NULL,
	index smallint NOT NULL,
	latitude numeric(9,6),
	longitude numeric(10,6),
	altitude numeric(6,2),
	CONSTRAINT omni_telemetrydata_gps_pk PRIMARY KEY (id_telemetrydata,index)
);
-- ddl-end --
ALTER TABLE public.omni_telemetrydata_gps OWNER TO eyeot;
-- ddl-end --

-- object: public.omni_telemetrydata_integer | type: TABLE --
-- DROP TABLE IF EXISTS public.omni_telemetrydata_integer CASCADE;
CREATE TABLE public.omni_telemetrydata_integer (
	id_telemetrydata bigint NOT NULL,
	index smallint NOT NULL,
	integervalue integer NOT NULL,
	CONSTRAINT omni_telemetrydata_integer_pk PRIMARY KEY (id_telemetrydata,index)
);
-- ddl-end --
ALTER TABLE public.omni_telemetrydata_integer OWNER TO eyeot;
-- ddl-end --

-- object: public.omni_telemetrydata_real | type: TABLE --
-- DROP TABLE IF EXISTS public.omni_telemetrydata_real CASCADE;
CREATE TABLE public.omni_telemetrydata_real (
	id_telemetrydata bigint NOT NULL,
	index smallint NOT NULL,
	realvalue real,
	CONSTRAINT omni_telemetrydata_real_pk PRIMARY KEY (id_telemetrydata,index)
);
-- ddl-end --
ALTER TABLE public.omni_telemetrydata_real OWNER TO eyeot;
-- ddl-end --

-- object: public.omni_telemetrydata_boolean | type: TABLE --
-- DROP TABLE IF EXISTS public.omni_telemetrydata_boolean CASCADE;
CREATE TABLE public.omni_telemetrydata_boolean (
	id_telemetrydata bigint NOT NULL,
	index smallint NOT NULL,
	booleanvalue boolean,
	CONSTRAINT omni_telemetrydata_boolean_pk PRIMARY KEY (id_telemetrydata,index)
);
-- ddl-end --
ALTER TABLE public.omni_telemetrydata_boolean OWNER TO eyeot;
-- ddl-end --

-- object: public.eyeot_sensor | type: TABLE --
-- DROP TABLE IF EXISTS public.eyeot_sensor CASCADE;
CREATE TABLE public.eyeot_sensor (
	id_device integer NOT NULL,
	index smallint NOT NULL,
	sensortype integer NOT NULL,
	CONSTRAINT eyeot_sensor_pk PRIMARY KEY (id_device,index)
);
-- ddl-end --
ALTER TABLE public.eyeot_sensor OWNER TO eyeot;
-- ddl-end --

-- object: public.eyeot_user | type: TABLE --
-- DROP TABLE IF EXISTS public.eyeot_user CASCADE;
CREATE TABLE public.eyeot_user (
	id_user integer NOT NULL DEFAULT nextval('public.eyeot_id_user_sequence'::regclass),
	name character varying(255) NOT NULL,
	phone character varying(14),
	email1 character varying(128) NOT NULL,
	email2 character varying(128),
	pw character varying(255) NOT NULL,
	papel character varying(255),
	CONSTRAINT eyeot_user_pkey PRIMARY KEY (id_user),
	CONSTRAINT eyeot_user_papel_check CHECK (((papel)::text = ANY ((ARRAY['ROLE_ADMIN'::character varying, 'ROLE_USER'::character varying])::text[])))
);
-- ddl-end --
ALTER TABLE public.eyeot_user OWNER TO eyeot;
-- ddl-end --

-- object: public.eyeot_domain | type: TABLE --
-- DROP TABLE IF EXISTS public.eyeot_domain CASCADE;
CREATE TABLE public.eyeot_domain (
	id_domain integer NOT NULL DEFAULT nextval('public.eyeot_id_domain_sequence'::regclass),
	id_user integer NOT NULL,
	domainname character varying(255) NOT NULL,
	description text,
	CONSTRAINT eyeot_domain_pkey PRIMARY KEY (id_domain)
);
-- ddl-end --
ALTER TABLE public.eyeot_domain OWNER TO eyeot;
-- ddl-end --

-- object: public.omni_telemetrydata | type: TABLE --
-- DROP TABLE IF EXISTS public.omni_telemetrydata CASCADE;
CREATE TABLE public.omni_telemetrydata (
	id_telemetrydata bigint NOT NULL DEFAULT nextval('public.omni_id_telemetry_sequence'::regclass),
	deviceid character(8) NOT NULL,
	"timestamp" timestamp DEFAULT CURRENT_TIMESTAMP,
	sourceaddress inet,
	port smallint,
	CONSTRAINT telemetrydata_pk PRIMARY KEY (id_telemetrydata)
);
-- ddl-end --
ALTER TABLE public.omni_telemetrydata OWNER TO eyeot;
-- ddl-end --

-- object: public.eyeot_devices | type: TABLE --
-- DROP TABLE IF EXISTS public.eyeot_devices CASCADE;
CREATE TABLE public.eyeot_devices (
	id_device integer NOT NULL DEFAULT nextval('public.eyeot_id_device_sequence'::regclass),
	id_domain integer,
	deviceid character(8) NOT NULL,
	creationdate timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
	enable boolean NOT NULL,
	latitude numeric(9,6),
	longitude numeric(10,6),
	altitude numeric(6,2),
	id_owner integer,
	CONSTRAINT eyeot_device_pkey PRIMARY KEY (id_device),
	CONSTRAINT uk_eyeot_devices_deviceid UNIQUE (deviceid)
);
-- ddl-end --
ALTER TABLE public.eyeot_devices OWNER TO eyeot;
-- ddl-end --

-- object: public.eyeot_id_sensortype_sequence | type: SEQUENCE --
-- DROP SEQUENCE IF EXISTS public.eyeot_id_sensortype_sequence CASCADE;
CREATE SEQUENCE public.eyeot_id_sensortype_sequence
	INCREMENT BY 1
	MINVALUE 0
	MAXVALUE 2147483647
	START WITH 1
	CACHE 1
	NO CYCLE
	OWNED BY NONE;

-- ddl-end --
ALTER SEQUENCE public.eyeot_id_sensortype_sequence OWNER TO eyeot;
-- ddl-end --

-- object: public.eyeot_sensor_type | type: TABLE --
-- DROP TABLE IF EXISTS public.eyeot_sensor_type CASCADE;
CREATE TABLE public.eyeot_sensor_type (
	id_sensortype integer NOT NULL DEFAULT nextval('public.eyeot_id_sensortype_sequence'::regclass),
	description character varying(256) NOT NULL,
	datatype smallint NOT NULL,
	minvalue numeric(38,2),
	maxvalue numeric(38,2),
	physicalquantity character varying(64) NOT NULL,
	measureunit character varying(12) NOT NULL,
	average_interval integer,
	retention_period integer,
	summary_period integer,
	CONSTRAINT eyeot_sensor_type_pk PRIMARY KEY (id_sensortype),
	CONSTRAINT eyeot_sensor_type_retention_period_check CHECK (((retention_period IS NULL) OR (retention_period > 0))),
	CONSTRAINT eyeot_sensor_type_summary_period_check CHECK (((summary_period IS NULL) OR (summary_period > 0))),
	CONSTRAINT chk_eyeot_sensor_type_summary_period CHECK (((summary_period IS NULL) OR (summary_period = ANY (ARRAY[30, 60, 720, 1440]))))
);
-- ddl-end --
ALTER TABLE public.eyeot_sensor_type OWNER TO eyeot;
-- ddl-end --

-- object: public.notificar_nova_telemetria | type: FUNCTION --
-- DROP FUNCTION IF EXISTS public.notificar_nova_telemetria() CASCADE;
CREATE OR REPLACE FUNCTION public.notificar_nova_telemetria ()
	RETURNS trigger
	LANGUAGE plpgsql
	VOLATILE 
	CALLED ON NULL INPUT
	SECURITY INVOKER
	PARALLEL UNSAFE
	COST 100
	AS 
$function$
BEGIN
    PERFORM pg_notify(
        'nova_telemetria',
        json_build_object(
            'idTelemetryData', NEW.id_telemetrydata,
            'deviceId', trim(NEW.deviceid),
            'timestamp', NEW.timestamp
        )::text
    );

    RETURN NEW;
END;
$function$;
-- ddl-end --
ALTER FUNCTION public.notificar_nova_telemetria() OWNER TO eyeot;
-- ddl-end --

-- object: trg_nova_telemetria | type: TRIGGER --
-- DROP TRIGGER IF EXISTS trg_nova_telemetria ON public.omni_telemetrydata CASCADE;
CREATE OR REPLACE TRIGGER trg_nova_telemetria
	AFTER INSERT 
	ON public.omni_telemetrydata
	FOR EACH ROW
	EXECUTE PROCEDURE public.notificar_nova_telemetria();
-- ddl-end --

-- object: public.eyeot_telemetry_points | type: VIEW --
-- DROP VIEW IF EXISTS public.eyeot_telemetry_points CASCADE;
CREATE OR REPLACE VIEW public.eyeot_telemetry_points
AS 
SELECT t.id_telemetrydata,
    d.id_device,
    s.index AS sensor_index,
    st.description AS sensor_type,
    st.datatype AS data_type,
    st.physicalquantity AS physical_quantity,
    st.measureunit AS measure_unit,
    t."timestamp" AS measured_at,
    (tr.realvalue)::double precision AS value,
    NULL::double precision AS value2,
    NULL::double precision AS value3,
    NULL::boolean AS boolean_value
   FROM ((((omni_telemetrydata t
     JOIN eyeot_devices d ON ((TRIM(BOTH FROM d.deviceid) = TRIM(BOTH FROM t.deviceid))))
     JOIN omni_telemetrydata_real tr ON ((tr.id_telemetrydata = t.id_telemetrydata)))
     JOIN eyeot_sensor s ON (((s.id_device = d.id_device) AND (s.index = tr.index))))
     JOIN eyeot_sensor_type st ON ((st.id_sensortype = s.sensortype)))
  WHERE (st.datatype = 1)
UNION ALL
 SELECT t.id_telemetrydata,
    d.id_device,
    s.index AS sensor_index,
    st.description AS sensor_type,
    st.datatype AS data_type,
    st.physicalquantity AS physical_quantity,
    st.measureunit AS measure_unit,
    t."timestamp" AS measured_at,
    NULL::double precision AS value,
    NULL::double precision AS value2,
    NULL::double precision AS value3,
    tb.booleanvalue AS boolean_value
   FROM ((((omni_telemetrydata t
     JOIN eyeot_devices d ON ((TRIM(BOTH FROM d.deviceid) = TRIM(BOTH FROM t.deviceid))))
     JOIN omni_telemetrydata_boolean tb ON ((tb.id_telemetrydata = t.id_telemetrydata)))
     JOIN eyeot_sensor s ON (((s.id_device = d.id_device) AND (s.index = tb.index))))
     JOIN eyeot_sensor_type st ON ((st.id_sensortype = s.sensortype)))
  WHERE (st.datatype = 2)
UNION ALL
 SELECT t.id_telemetrydata,
    d.id_device,
    s.index AS sensor_index,
    st.description AS sensor_type,
    st.datatype AS data_type,
    st.physicalquantity AS physical_quantity,
    st.measureunit AS measure_unit,
    t."timestamp" AS measured_at,
    (tg.latitude)::double precision AS value,
    (tg.longitude)::double precision AS value2,
    (tg.altitude)::double precision AS value3,
    NULL::boolean AS boolean_value
   FROM ((((omni_telemetrydata t
     JOIN eyeot_devices d ON ((TRIM(BOTH FROM d.deviceid) = TRIM(BOTH FROM t.deviceid))))
     JOIN omni_telemetrydata_gps tg ON ((tg.id_telemetrydata = t.id_telemetrydata)))
     JOIN eyeot_sensor s ON (((s.id_device = d.id_device) AND (s.index = tg.index))))
     JOIN eyeot_sensor_type st ON ((st.id_sensortype = s.sensortype)))
  WHERE (st.datatype = 3)
UNION ALL
 SELECT t.id_telemetrydata,
    d.id_device,
    s.index AS sensor_index,
    st.description AS sensor_type,
    st.datatype AS data_type,
    st.physicalquantity AS physical_quantity,
    st.measureunit AS measure_unit,
    t."timestamp" AS measured_at,
    ta.xvalue AS value,
    ta.yvalue AS value2,
    ta.zvalue AS value3,
    NULL::boolean AS boolean_value
   FROM ((((omni_telemetrydata t
     JOIN eyeot_devices d ON ((TRIM(BOTH FROM d.deviceid) = TRIM(BOTH FROM t.deviceid))))
     JOIN omni_telemetrydata_accelerometer ta ON ((ta.id_telemetrydata = t.id_telemetrydata)))
     JOIN eyeot_sensor s ON (((s.id_device = d.id_device) AND (s.index = ta.index))))
     JOIN eyeot_sensor_type st ON ((st.id_sensortype = s.sensortype)))
  WHERE (st.datatype = 4)
UNION ALL
 SELECT t.id_telemetrydata,
    d.id_device,
    s.index AS sensor_index,
    st.description AS sensor_type,
    st.datatype AS data_type,
    st.physicalquantity AS physical_quantity,
    st.measureunit AS measure_unit,
    t."timestamp" AS measured_at,
    (ti.integervalue)::double precision AS value,
    NULL::double precision AS value2,
    NULL::double precision AS value3,
    NULL::boolean AS boolean_value
   FROM ((((omni_telemetrydata t
     JOIN eyeot_devices d ON ((TRIM(BOTH FROM d.deviceid) = TRIM(BOTH FROM t.deviceid))))
     JOIN omni_telemetrydata_integer ti ON ((ti.id_telemetrydata = t.id_telemetrydata)))
     JOIN eyeot_sensor s ON (((s.id_device = d.id_device) AND (s.index = ti.index))))
     JOIN eyeot_sensor_type st ON ((st.id_sensortype = s.sensortype)))
  WHERE (st.datatype = 5);
-- ddl-end --
ALTER VIEW public.eyeot_telemetry_points OWNER TO eyeot;
-- ddl-end --

-- object: public.eyeot_sensor_statistics_period | type: TABLE --
-- DROP TABLE IF EXISTS public.eyeot_sensor_statistics_period CASCADE;
CREATE TABLE public.eyeot_sensor_statistics_period (
	id_device integer NOT NULL,
	index smallint NOT NULL,
	interval_start timestamp NOT NULL,
	interval_end timestamp NOT NULL,
	min_value double precision NOT NULL,
	max_value double precision NOT NULL,
	sum_value double precision NOT NULL,
	sum_value_squared double precision NOT NULL,
	average_value double precision NOT NULL,
	median_value double precision NOT NULL,
	standard_deviation double precision NOT NULL,
	total_count bigint NOT NULL,
	aggregated_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
	amplitude_value double precision NOT NULL DEFAULT 0,
	summary_period integer NOT NULL,
	CONSTRAINT eyeot_sensor_statistics_period_interval_check CHECK ((interval_end > interval_start)),
	CONSTRAINT eyeot_sensor_statistics_period_count_check CHECK ((total_count > 0)),
	CONSTRAINT eyeot_sensor_statistics_period_pk PRIMARY KEY (id_device,index,interval_start,summary_period)
);
-- ddl-end --
ALTER TABLE public.eyeot_sensor_statistics_period OWNER TO eyeot;
-- ddl-end --

-- object: idx_omni_telemetrydata_timestamp | type: INDEX --
-- DROP INDEX IF EXISTS public.idx_omni_telemetrydata_timestamp CASCADE;
CREATE INDEX idx_omni_telemetrydata_timestamp ON public.omni_telemetrydata
USING btree
(
	"timestamp"
)
WITH (FILLFACTOR = 90);
-- ddl-end --

-- object: omni_telemetrydata_accelerometer_fk | type: CONSTRAINT --
-- ALTER TABLE public.omni_telemetrydata_accelerometer DROP CONSTRAINT IF EXISTS omni_telemetrydata_accelerometer_fk CASCADE;
ALTER TABLE public.omni_telemetrydata_accelerometer ADD CONSTRAINT omni_telemetrydata_accelerometer_fk FOREIGN KEY (id_telemetrydata)
REFERENCES public.omni_telemetrydata (id_telemetrydata) MATCH SIMPLE
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: omni_telemetrydata_gps_fk | type: CONSTRAINT --
-- ALTER TABLE public.omni_telemetrydata_gps DROP CONSTRAINT IF EXISTS omni_telemetrydata_gps_fk CASCADE;
ALTER TABLE public.omni_telemetrydata_gps ADD CONSTRAINT omni_telemetrydata_gps_fk FOREIGN KEY (id_telemetrydata)
REFERENCES public.omni_telemetrydata (id_telemetrydata) MATCH SIMPLE
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: omni_telemetrydata_integer_fk | type: CONSTRAINT --
-- ALTER TABLE public.omni_telemetrydata_integer DROP CONSTRAINT IF EXISTS omni_telemetrydata_integer_fk CASCADE;
ALTER TABLE public.omni_telemetrydata_integer ADD CONSTRAINT omni_telemetrydata_integer_fk FOREIGN KEY (id_telemetrydata)
REFERENCES public.omni_telemetrydata (id_telemetrydata) MATCH SIMPLE
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: omni_telemetrydata_real_fk | type: CONSTRAINT --
-- ALTER TABLE public.omni_telemetrydata_real DROP CONSTRAINT IF EXISTS omni_telemetrydata_real_fk CASCADE;
ALTER TABLE public.omni_telemetrydata_real ADD CONSTRAINT omni_telemetrydata_real_fk FOREIGN KEY (id_telemetrydata)
REFERENCES public.omni_telemetrydata (id_telemetrydata) MATCH SIMPLE
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: omni_telemetrydata_boolean_fk | type: CONSTRAINT --
-- ALTER TABLE public.omni_telemetrydata_boolean DROP CONSTRAINT IF EXISTS omni_telemetrydata_boolean_fk CASCADE;
ALTER TABLE public.omni_telemetrydata_boolean ADD CONSTRAINT omni_telemetrydata_boolean_fk FOREIGN KEY (id_telemetrydata)
REFERENCES public.omni_telemetrydata (id_telemetrydata) MATCH SIMPLE
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: eyeot_sensor_fk | type: CONSTRAINT --
-- ALTER TABLE public.eyeot_sensor DROP CONSTRAINT IF EXISTS eyeot_sensor_fk CASCADE;
ALTER TABLE public.eyeot_sensor ADD CONSTRAINT eyeot_sensor_fk FOREIGN KEY (id_device)
REFERENCES public.eyeot_devices (id_device) MATCH SIMPLE
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: eyeot_sensor_sensortype_fk | type: CONSTRAINT --
-- ALTER TABLE public.eyeot_sensor DROP CONSTRAINT IF EXISTS eyeot_sensor_sensortype_fk CASCADE;
ALTER TABLE public.eyeot_sensor ADD CONSTRAINT eyeot_sensor_sensortype_fk FOREIGN KEY (sensortype)
REFERENCES public.eyeot_sensor_type (id_sensortype) MATCH SIMPLE
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: eyeot_domain_fk | type: CONSTRAINT --
-- ALTER TABLE public.eyeot_domain DROP CONSTRAINT IF EXISTS eyeot_domain_fk CASCADE;
ALTER TABLE public.eyeot_domain ADD CONSTRAINT eyeot_domain_fk FOREIGN KEY (id_user)
REFERENCES public.eyeot_user (id_user) MATCH SIMPLE
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: eyeot_device_fk | type: CONSTRAINT --
-- ALTER TABLE public.eyeot_devices DROP CONSTRAINT IF EXISTS eyeot_device_fk CASCADE;
ALTER TABLE public.eyeot_devices ADD CONSTRAINT eyeot_device_fk FOREIGN KEY (id_domain)
REFERENCES public.eyeot_domain (id_domain) MATCH SIMPLE
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: fkkeewo2rnmgqh4rultum8vl3q4 | type: CONSTRAINT --
-- ALTER TABLE public.eyeot_devices DROP CONSTRAINT IF EXISTS fkkeewo2rnmgqh4rultum8vl3q4 CASCADE;
ALTER TABLE public.eyeot_devices ADD CONSTRAINT fkkeewo2rnmgqh4rultum8vl3q4 FOREIGN KEY (id_owner)
REFERENCES public.eyeot_user (id_user) MATCH SIMPLE
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: eyeot_sensor_statistics_period_sensor_fk | type: CONSTRAINT --
-- ALTER TABLE public.eyeot_sensor_statistics_period DROP CONSTRAINT IF EXISTS eyeot_sensor_statistics_period_sensor_fk CASCADE;
ALTER TABLE public.eyeot_sensor_statistics_period ADD CONSTRAINT eyeot_sensor_statistics_period_sensor_fk FOREIGN KEY (id_device,index)
REFERENCES public.eyeot_sensor (id_device,index) MATCH SIMPLE
ON DELETE CASCADE ON UPDATE NO ACTION;
-- ddl-end --



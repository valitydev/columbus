CREATE TABLE columb.city_locations_ru
(
  geoname_id integer NOT NULL,
  locale_code text,
  continent_code text,
  continent_name text,
  country_iso_code text,
  country_name text,
  subdivision_1_iso_code text,
  subdivision_1_name text,
  subdivision_2_iso_code text,
  subdivision_2_name text,
  city_name text,
  metro_code text,
  time_zone text,
  CONSTRAINT "geoname_id_pkey" PRIMARY KEY ("geoname_id")
);

CREATE UNIQUE INDEX "geoname_id_unique_idx"
  ON columb.city_locations_ru
  USING btree
  (geoname_id);


CREATE TABLE columb.city_locations_eng
(
  geoname_id integer NOT NULL,
  locale_code text,
  continent_code text,
  continent_name text,
  country_iso_code text,
  country_name text,
  subdivision_1_iso_code text,
  subdivision_1_name text,
  subdivision_2_iso_code text,
  subdivision_2_name text,
  city_name text,
  metro_code text,
  time_zone text,
  CONSTRAINT "geoname_id_pkey" PRIMARY KEY ("geoname_id")
);

CREATE UNIQUE INDEX "geoname_id_unique_idx"
  ON columb.city_locations_eng
  USING btree
  (geoname_id);

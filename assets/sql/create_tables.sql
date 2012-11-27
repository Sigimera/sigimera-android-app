CREATE TABLE crises (
	_id TEXT PRIMARY KEY,
	dc_title TEXT,
	dc_description TEXT,
	latitude REAL,
	longitude REAL,
	subject TEXT,
	crisis_alertLevel TEXT,
	crisis_severity TEXT,
	crisis_severity_hash_value TEXT,
	crisis_severity_hash_unit TEXT,
	crisis_population TEXT,
	crisis_population_hash_value TEXT,
	crisis_population_hash_unit TEXT,
	crisis_vulnerability TEXT,
	dc_date TEXT,
	schema_startDate TEXT,
	schema_endDate TEXT,
	short_title TEXT,
	type_icon TEXT
);

CREATE TABLE countries (
	crisis_id TEXT,
	country_name TEXT
);

CREATE TABLE user_info (
	_id TEXT PRIMARY KEY,
	uploaded_images INTEGER,
	posted_comments INTEGER,
	reported_locations INTEGER,
	reported_missing_people INTEGER,
	near_crisis_id TEXT,
	latitude REAL,
	longitude REAL
);

CREATE TABLE crises_stats (
	_id TEXT PRIMARY KEY,
	first_crisis_at TEXT,
	latest_crisis_at TEXT,
	today_crises INTEGER,
	total_crises INTEGER,
	number_of_earthquakes INTEGER,
	number_of_floods INTEGER,
	number_of_cyclones INTEGER,
	number_of_volcanoes INTEGER,
	uploaded_images INTEGER,
	posted_comments INTEGER,
	reported_locations INTEGER,
	reported_missing_people INTEGER
);

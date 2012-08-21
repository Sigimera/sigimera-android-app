CREATE TABLE crises (
	_id TEXT PRIMARY KEY,
	dc_title TEXT,
	dc_description TEXT,
	latitude REAL,
	longitude REAL,
	subject TEXT,
	crisis_alertLevel TEXT,
	crisis_severity TEXT,
	crisis_population TEXT,
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

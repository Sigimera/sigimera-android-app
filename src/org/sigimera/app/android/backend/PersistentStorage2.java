package org.sigimera.app.android.backend;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.sigimera.app.android.controller.ApplicationController;
import org.sigimera.app.android.controller.PersistanceController;
import org.sigimera.app.android.model.Constants;
import org.sigimera.app.android.model.CrisesStats;
import org.sigimera.app.android.model.Crisis;
import org.sigimera.app.android.model.UsersStats;
import org.sigimera.app.android.util.Common;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class PersistentStorage2 extends SQLiteOpenHelper{
	
	private static PersistentStorage2 instance = null;

    private final static int DB_VERSION = 1;
    private final static String DB_NAME = "sigimera.db";
    private final static String TABLE_CRISES = "crises";
    private final static String TABLE_COUNTRIES = "countries";
    private final static String TABLE_CRISES_STATS = "crises_stats";
    private final static String TABLE_USERS_STATS = "user_stats";
    private final static String TABLE_NEAR_CRISES = "near_crises";

    private final Context context;
    
    public static PersistentStorage2 getInstance() {
        if ( null == instance )
            instance = new PersistentStorage2(ApplicationController.getInstance().getApplicationContext());
        return instance;
    }

    private PersistentStorage2(Context _context) {
        super(_context, DB_NAME, null, DB_VERSION);
        this.context = _context;
    }

	@Override
	public void onCreate(SQLiteDatabase _db) {
		 this.executeSQLScript(_db, "sql/create_tables.sql");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}

	/*
	 * Execute SQLScript
	 */
	private void executeSQLScript(SQLiteDatabase _database, String _sqlscript) {
        AssetManager assetManager = this.context.getAssets();
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        byte buf[] = new byte[1024];
        int len;
        InputStream is = null;
        try {
            is = assetManager.open(_sqlscript);
            while ( (len = is.read(buf) ) != -1) {
                os.write(buf, 0, len);
            }
            os.close();
            is.close();

            String[] script = os.toString().split(";");
            for (int i = 0; i < script.length; i++) {
                String sqlStatement = script[i].trim();
                if ( sqlStatement.length() > 0 ) {
                    _database.execSQL(sqlStatement + ";");
                }
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
	
	/**
	 * Check if the crisis is already saved
	 * @param _id
	 * @return
	 */	
	private boolean checkIfCrisisExists(String _id) {
        SQLiteDatabase db = getReadableDatabase();
        db.beginTransaction();
        Cursor cursor = db.rawQuery("SELECT count(_id) FROM "+TABLE_CRISES+" WHERE _id = '"+_id+"'", null);
        cursor.moveToFirst();
        
        boolean returnValue;
        if ( cursor.getInt(0) == 1 )
            returnValue = true;
        else
            returnValue = false;
        db.endTransaction();
        db.close();
        return returnValue;
    }
	
	/****************************************************************
	 * From here on are methods for manipulation crises and databases
	 ****************************************************************/
	
	/**
	 * Get the last X crises divided into pages
	 * 	 
	 * @param _number The number of crisis which should be retrieved
	 * @param _page The number of the page  
	 * @return 
	 */
	public synchronized ArrayList<Crisis> getLatestCrisesList(int _number, int _page) {
		SQLiteDatabase db = getReadableDatabase();
		Cursor c = db.rawQuery("SELECT * FROM "+TABLE_CRISES+" ORDER BY dc_date DESC LIMIT "+_number+" OFFSET " +((_page-1) * _number), null);
		ArrayList<Crisis> crises = _extractCrises(c);
		
		db.close();
		return crises;
	}
	
	/**
	 * Receive the countries in which the crisis occurred. 
	 * 
	 * @param _crisisID The unique crisis id.
	 * @return a list of countries as String 
	 */
	public synchronized ArrayList<String> getCountries(String _crisisID) {
		SQLiteDatabase db = getReadableDatabase();
	        
		Cursor c = db.rawQuery("SELECT country_name FROM "+TABLE_COUNTRIES+" WHERE crisis_id='" + _crisisID + "'", null);
		ArrayList<String> countries = new ArrayList<String>();
		
		while ( c.moveToNext() ) {
			countries.add(c.getString(0));
	    }
		c.close();
		db.close();

		return countries;
	}
	
	/**
	 * Get statistical information about user
	 * @return
	 */
	public synchronized UsersStats getUsersStats() {
    	UsersStats stats = null;
    	SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_USERS_STATS + " LIMIT 1", null);
        stats = _extractUsersStats(c);
        
        c.close();
        db.close();
        
    	return stats;
    }
	
	/**
	 * Retrieve the crisis ID of the nearest crisis
	 * 
	 * @return
	 */
	public synchronized String getNearCrisis() {
		String crisisID = null;		
		SQLiteDatabase db = getReadableDatabase();
		
		// Get the nearest crisis
        Cursor nearCrisisCursor = db.rawQuery("SELECT * FROM "+TABLE_NEAR_CRISES , null);
        if ( nearCrisisCursor != null && nearCrisisCursor.moveToFirst() ) {
        	crisisID = nearCrisisCursor.getString(nearCrisisCursor.getColumnIndex("_id"));
        	
        	// If the crisis is not in cache
        	if ( crisisID == null ) {
        		Log.i("[PERSISTENT STORAGE]", "Retrive the near crisis: " + crisisID);
        	}
        }
        db.close();
        
        return crisisID;
	}
	
	/**
	 * Get a list of today crises IDs
	 * 
	 * @return
	 */
	public synchronized ArrayList<Crisis> getTodayCrises() {
		ArrayList<Crisis> crisesList = new ArrayList<Crisis>();
		
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Calendar cal = Calendar.getInstance();		
		String todayDate = format.format(cal.getTime());
		
    	SQLiteDatabase db = getReadableDatabase();
    	
    	// Get today crises
        Cursor todayCursor = db.rawQuery("SELECT * FROM "+TABLE_CRISES+" WHERE date(dc_date) >= date('" + todayDate +"') ORDER BY dc_date DESC", null);
        crisesList = this._extractCrises(todayCursor);
    	
        db.close();
    	
    	return crisesList;
	}
	
	/**
	 * Get the crisis ID of the latest crisis
	 * 
	 * @return
	 */
	public synchronized String getLatestCrisis() {
		String crisisID = null;
		SQLiteDatabase db = getReadableDatabase();
		
		// Get the latest crisis
		Cursor latestCrisisCursor = db.rawQuery("SELECT * FROM "+TABLE_CRISES+" ORDER BY dc_date DESC LIMIT 1", null);
        if ( latestCrisisCursor != null && latestCrisisCursor.moveToFirst() ) {
        	crisisID = latestCrisisCursor.getString(latestCrisisCursor.getColumnIndex("_id"));
        	
        	// If the crisis is not in cache
        	if ( crisisID == null ) {
        		Log.i("[PERSISTENT STORAGE]", "Retrive the latest crisis: " + crisisID);
        	}
        }
		
		db.close();
		
		return crisisID;
	}
    
	/**
	 * Get statistical information about crises
	 * 
	 * @return
	 */
    public synchronized CrisesStats getCrisesStats() {
    	CrisesStats stats = null;
    	SQLiteDatabase db = getReadableDatabase();
    	
    	// Get the crises statistics
    	Cursor statsCursor = db.rawQuery("SELECT * FROM " + TABLE_CRISES_STATS + " LIMIT 1", null);    	     	        
	    stats = this._extractCrisesStats(statsCursor);
    	    	
        db.close();
        
    	return stats;
    }
    
    /**
     * Get the crisis by id
     * @param _crisisID
     * @return
     */
    public synchronized Crisis getCrisis(String _crisisID) {
      SQLiteDatabase db = getReadableDatabase();

      Cursor c = db.rawQuery("SELECT * FROM "+TABLE_CRISES+" WHERE _id='" + _crisisID + "'", null);
      Crisis crisis = null;
      if ( c.getCount() != 0) crisis = this._extractCrisis(c);
      
      c.close();
      db.close();

      return crisis;
    }
    
    /**
     * Get the size of the cache
     * @return
     */
    public synchronized long getCacheSize() {    	
    	SQLiteDatabase db = getReadableDatabase();
    	File db_file = this.context.getDatabasePath(db.getPath());
    	long db_size = db_file.getTotalSpace();
    	db.close();
    	
    	return db_size;
    }
    
    public ArrayList<Crisis> getNearCrises() {
    	ArrayList<Crisis> crises = new ArrayList<Crisis>();
    	SQLiteDatabase db = getReadableDatabase();
    	
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_NEAR_CRISES + " near INNER JOIN " + TABLE_CRISES + " crises ON near._id=crises._id ORDER BY dc_date DESC", null);
        crises = this._extractCrises(c);
        
        db.close();

		return crises;
	}
    
    
    /****************************************************************
	 * From here on are methods for saving 
	 ****************************************************************/
    
    public synchronized boolean addCrisesStats(JSONObject _crisesStats) throws JSONException {        	
    	if ( _crisesStats == null ) return false;
    	
    	Log.i("[PERSISTANCE STORAGE]", "Updating crises statistics");
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        
        values.put("_id", "crises_stats");
        values.put("first_crisis_at", _crisesStats.getString("first_crisis_at"));
        values.put("latest_crisis_at", _crisesStats.getString("latest_crisis_at"));
        values.put("total_crises", _crisesStats.getInt("total_crises"));
        values.put("today_crises", _crisesStats.getInt("today_crises"));

        JSONObject numberOf = _crisesStats.getJSONObject("number_of");
        values.put("number_of_earthquakes", numberOf.getInt("earthquakes"));
        values.put("number_of_floods", numberOf.getInt("floods"));
        values.put("number_of_cyclones", numberOf.getInt("cyclones"));
        values.put("number_of_volcanoes", numberOf.getInt("volcanoes"));

        values.put("uploaded_images", _crisesStats.getInt("uploaded_images"));
        values.put("posted_comments", _crisesStats.getInt("posted_comments"));
        values.put("reported_locations", _crisesStats.getInt("reported_locations"));
        values.put("reported_missing_people", _crisesStats.getInt("reported_missing_people"));

        db.insert(TABLE_CRISES_STATS, null, values);
        db.close();        

        return true;
    }
    
    public synchronized boolean addCrisis(JSONObject _crisis) throws JSONException {
    	if ( _crisis == null ) return false;
        String crisisID = _crisis.getString("_id");
        if ( checkIfCrisisExists(crisisID) ) {
            // TODO: Delete old crisis or implement some type of update mechanism
            Log.d(Constants.LOG_TAG_SIGIMERA_APP, "Crisis was found! Not updating it...");
            return false;
        } else {
            SQLiteDatabase db = getWritableDatabase();
            
            ContentValues values = new ContentValues();
            values.put("_id", _crisis.getString("_id"));
            
            values.put("short_title", PersistanceController.getInstance().getShortTitle(_crisis));
//            if ( _crisis.has("subject") )
//            	values.put("type_icon", Common.getCrisisIcon(_crisis.getString("subject")) + "");
            
            if ( _crisis.has("foaf_based_near") ) {
            	values.put("longitude", Double.valueOf(_crisis.getJSONArray("foaf_based_near").get(0).toString()));
            	values.put("latitude", Double.valueOf(_crisis.getJSONArray("foaf_based_near").get(1).toString()));
            }
            if ( _crisis.has("dc_title") )
            	values.put("dc_title", _crisis.getString("dc_title"));
            if ( _crisis.has("dc_description") )
            	values.put("dc_description", _crisis.getString("dc_description"));
            if ( _crisis.has("dc_date") )
            	values.put("dc_date", _crisis.getString("dc_date"));
            if ( _crisis.has("schema_startDate") )
            	values.put("schema_startDate", _crisis.getString("schema_startDate"));
            if ( _crisis.has("schema_endDate") )
            	values.put("schema_endDate", _crisis.getString("schema_endDate"));
            if ( _crisis.has("subject") )
            	values.put("subject", _crisis.getString("subject"));
            if ( _crisis.has("crisis_alertLevel") )
            	values.put("crisis_alertLevel", _crisis.getString("crisis_alertLevel"));
            if ( _crisis.has("crisis_severity") )
            	values.put("crisis_severity", _crisis.getString("crisis_severity"));
            if ( _crisis.has("crisis_population") )
            	values.put("crisis_population", _crisis.getString("crisis_population"));
            if ( _crisis.has("crisis_vulnerability") )
            	values.put("crisis_vulnerability", _crisis.getString("crisis_vulnerability"));            
            
            if ( _crisis.has("crisis_severity_hash") && !_crisis.isNull("crisis_severity_hash")  ) {
            	JSONObject severity_hash = _crisis.getJSONObject("crisis_severity_hash");
            	if ( !severity_hash.isNull("value") )
            		values.put("crisis_severity_hash_value", severity_hash.getString("value"));
            	
            	if ( !severity_hash.isNull("unit") )
            		values.put("crisis_severity_hash_unit", severity_hash.getString("unit"));
            }

            if ( _crisis.has("crisis_population_hash") && !_crisis.isNull("crisis_population_hash") ) {
            	JSONObject population_hash = _crisis.getJSONObject("crisis_population_hash");
            	if ( !population_hash.isNull("value") )
            		values.put("crisis_population_hash_value", population_hash.getString("value"));
            	
            	if ( !population_hash.isNull("unit") )
            		values.put("crisis_population_hash_unit", population_hash.getString("unit"));
            }

            db.insert(TABLE_CRISES, null, values);
            
            /**
             * Add country to separate table
             */
            JSONArray countries = (JSONArray)_crisis.getJSONArray("gn_parentCountry");
            ContentValues countryValues = new ContentValues();
            for ( int count=0; count < countries.length(); count++ ) {
            	countryValues.put("crisis_id", crisisID);
                countryValues.put("country_name", countries.getString(count));
            }
            if ( countryValues.size() > 0 )
            	db.insert(TABLE_COUNTRIES, null, countryValues);
            
            db.close();
        }
        return true;
    }
    
    public synchronized boolean addUsersStats(JSONObject _usersStats) throws JSONException {
    	if ( _usersStats == null ) return false;
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        
        values.put("name", _usersStats.getString("name"));
        values.put("username", _usersStats.getString("username"));
        values.put("uploaded_images", _usersStats.getInt("uploaded_images"));
        values.put("posted_comments", _usersStats.getInt("posted_comments"));
        values.put("reported_locations", _usersStats.getInt("reported_locations"));
        values.put("reported_missing_people", _usersStats.getInt("reported_missing_people"));        
        
        long status = db.insert(TABLE_USERS_STATS, null, values);
        if ( status == -1 )
        	Log.i("[PERSISTANT STORAGE]", "ERROR inserting the values " + values + " into the table " + TABLE_USERS_STATS);
        else 
        	Log.i("[PERSISTANT STORAGE]", "AFFECTED ROWS " + status);
        db.close();
                        
        Log.i("[PERSISTANT STORAGE]", "Adding users stats: " + values);

        return true;
    }
    
    /****************************************************************
	 * From here on are methods for UPDATING
     * @throws JSONException 
	 ****************************************************************/
    
    public synchronized boolean updateCrisesStats(JSONObject _crisesStats) throws JSONException {
    	if (_crisesStats == null) { return false; }
    	
    	Log.i("[PERSISTENT STORAGE]", "Updating crises statistics");
    	SQLiteDatabase db = getWritableDatabase();
    	ContentValues values = new ContentValues();
    	
    	values.put("_id", "crises_stats");
        values.put("first_crisis_at", _crisesStats.getString("first_crisis_at"));
        values.put("latest_crisis_at", _crisesStats.getString("latest_crisis_at"));
        values.put("total_crises", _crisesStats.getInt("total_crises"));
        values.put("today_crises", _crisesStats.getInt("today_crises"));

        JSONObject numberOf = _crisesStats.getJSONObject("number_of");
        values.put("number_of_earthquakes", numberOf.getInt("earthquakes"));
        values.put("number_of_floods", numberOf.getInt("floods"));
        values.put("number_of_cyclones", numberOf.getInt("cyclones"));
        values.put("number_of_volcanoes", numberOf.getInt("volcanoes"));

        values.put("uploaded_images", _crisesStats.getInt("uploaded_images"));
        values.put("posted_comments", _crisesStats.getInt("posted_comments"));
        values.put("reported_locations", _crisesStats.getInt("reported_locations"));
        values.put("reported_missing_people", _crisesStats.getInt("reported_missing_people"));
        
        int affectedRows = db.update(TABLE_CRISES_STATS, values, null, null);
        if (affectedRows == 0) {
        	Log.i("[PERSISTENT STORAGE]", "Updating of crises statistics failed. Affected rows: " + affectedRows);
        	
        	Log.i("[PERSISTENT STORAGE]", "Inserting crises statistics into db");
        	db.insert(TABLE_CRISES_STATS, null, values);
        }
    	
    	return true;
    }
	
    public synchronized boolean updateNearCrises(JSONArray crises){
    	if ( crises == null ) return false;
    	
    	Log.i("[PERSISTENT STORAGE]", "Updating near crises");
    	ContentValues values = new ContentValues();    	
    	
    	try {
    		SQLiteDatabase db = getWritableDatabase();
    		
    		int affetedRows = db.delete(TABLE_NEAR_CRISES, null, null);
    		if (affetedRows != 0) 
				Log.i("[PERSISTENT STORAGE]", "Delete all entries about ");
    		
    		for (int i=0; i < crises.length(); i++) {
    			JSONObject crisis = crises.getJSONObject(i);
	    		values.put("_id", crisis.getString("_id"));
		    	db.insert(TABLE_NEAR_CRISES, null, values);
		    	Log.i("[PERSISTENT STORAGE]", "Insert near crisis with id: " + crisis.getString("_id"));
    		}
    		db.close();
    		
    		for (int i=0; i < crises.length(); i++) {
    			JSONObject crisis = crises.getJSONObject(i);
    			this.addCrisis(crisis);
    		}
    		
    	} catch(JSONException e) {
    		e.printStackTrace();
    	}

    	return true;
    }
    
    public synchronized boolean updateNearCrisesRadius(int _radius, String _userID) {    	
    	if ( _userID == null ) return false;
    	
    	Log.i("[PERSISTENT STORAGE]", "Updating near crises radius for user " + _userID);
    	SQLiteDatabase db = getWritableDatabase();
    	ContentValues values = new ContentValues();  
    	
    	values.put("radius", _radius);
    	
    	int number_of_rows = db.update(TABLE_USERS_STATS, values, "username = '" + _userID  + "'", null);
    	Log.i("[PERSISTENT STORAGE]", "Affected rows: " + number_of_rows);
    	
    	if ( number_of_rows == 0 ) {
    		Log.i("[PERSISTENT STORAGE]", "Radius is not set. Insert near crises radius in db: " + _radius + 
    				" --- affected rows: " + db.insert(TABLE_USERS_STATS, null, values));
    	} else
    		Log.i("[PERSISTENT STORAGE]", "Near crises radius updated: " + _radius);
    	db.close();
    	
    	return true;
    }
    
    /****************************************************************
	 * From here on are methods for extractions
	 ****************************************************************/
    
	private ArrayList<Crisis> _extractCrises(Cursor _c) {
    	ArrayList<Crisis> crises = new ArrayList<Crisis>();
    	
    	if ( _c != null ) {
    		while ( _c.moveToNext() ) {
    			Crisis crisis = new Crisis();
    			crisis.setID(_c.getString(_c.getColumnIndex("_id")));
    			crisis.setTypeIcon(Common.getCrisisIcon(_c.getString(_c.getColumnIndex("subject"))) + "");
    			crisis.setShortTitle(_c.getString(_c.getColumnIndex("short_title")));
    			crisis.setDate(_c.getString(_c.getColumnIndex("dc_date")));
    			crises.add(crisis);
    		}
    	}
    	
    	return crises;
    }
	
	private Crisis _extractCrisis(Cursor _c) {
        Crisis crisis = null;
        boolean hasEntry = _c.moveToFirst();
        if ( hasEntry ) {
            crisis = new Crisis();
            crisis.setID(_c.getString(_c.getColumnIndex("_id")));
            crisis.setAlertLevel(_c.getString(_c.getColumnIndex("crisis_alertLevel")));
            crisis.setDate(_c.getString(_c.getColumnIndex("dc_date")));
            crisis.setDescription(_c.getString(_c.getColumnIndex("dc_description")));
            crisis.setEndDate(_c.getString(_c.getColumnIndex("schema_endDate")));
            crisis.setLatitude(_c.getDouble(_c.getColumnIndex("latitude")));
            crisis.setLongitude(_c.getDouble(_c.getColumnIndex("longitude")));
            crisis.setSubject(_c.getString(_c.getColumnIndex("subject")));
            crisis.setPopulation(_c.getString(_c.getColumnIndex("crisis_population")));
            crisis.setPopulationHashValue(_c.getString(_c.getColumnIndex("crisis_population_hash_value")));
            crisis.setPopulationHashUnit(_c.getString(_c.getColumnIndex("crisis_population_hash_unit")));
            crisis.setSeverity(_c.getString(_c.getColumnIndex("crisis_severity")));
            crisis.setSeverityHashValue(_c.getString(_c.getColumnIndex("crisis_severity_hash_value")));
            crisis.setSeverityHashUnit(_c.getString(_c.getColumnIndex("crisis_severity_hash_unit")));
            crisis.setShortTitle(_c.getString(_c.getColumnIndex("short_title")));
            crisis.setStartDate(_c.getString(_c.getColumnIndex("schema_startDate")));
            crisis.setTitle(_c.getString(_c.getColumnIndex("dc_title")));
            crisis.setTypeIcon(_c.getString(_c.getColumnIndex("type_icon")));
            crisis.setVulnerability(_c.getString(_c.getColumnIndex("crisis_vulnerability")));
            crisis.setCountries(getCountries(crisis.getID()));
            
            Log.i("[PERSISTANT STORAGE]", "Extracting crisis: " +  
            		crisis.getID() + " - " +
            		crisis.getAlertLevel() + " - " +
            		crisis.getDate() + " - " +
            		crisis.getDescription() + " - " +
            		crisis.getEndDate() + " - " +
            		crisis.getLatitude() + " - " +
            		crisis.getLongitude() + " - " +
        			crisis.getSubject() + " - " +
        			crisis.getPopulation() + " - " +
        			crisis.getPopulationHashUnit() + " - " +
        			crisis.getPopulationHashValue() + " - " +
        			crisis.getSeverity() + " - " +
        			crisis.getSeverityHashUnit() + " - " +
        			crisis.getSeverityHashValue() + " - " +
        			crisis.getShortTitle() + " - " +
        			crisis.getStartDate() + " - " +
        			crisis.getTitle() + " - " +
        			crisis.getTypeIcon() + " - " +
        			crisis.getVulnerability() + " - " +
        			crisis.getVulnerabilityHashValue());
        }
        return crisis;
    }
	
	private CrisesStats _extractCrisesStats(Cursor statsCrisesCursor) {
        CrisesStats stats = null;        
        boolean hasEntry = statsCrisesCursor.moveToFirst();
        if ( hasEntry ) {
        	stats = new CrisesStats();
        	stats.setId(statsCrisesCursor.getString(statsCrisesCursor.getColumnIndex("_id")));
        	stats.setLatestCrisisAt(statsCrisesCursor.getString(statsCrisesCursor.getColumnIndex("latest_crisis_at")));
        	stats.setTotalCrises(statsCrisesCursor.getInt(statsCrisesCursor.getColumnIndex("total_crises")));
        	stats.setFirstCrisisAt(statsCrisesCursor.getString(statsCrisesCursor.getColumnIndex("first_crisis_at")));
        	stats.setNumberOfCyclones(statsCrisesCursor.getInt(statsCrisesCursor.getColumnIndex("number_of_cyclones")));
        	stats.setNumberOfFloods(statsCrisesCursor.getInt(statsCrisesCursor.getColumnIndex("number_of_floods")));
        	stats.setNumberOfEarthquakes(statsCrisesCursor.getInt(statsCrisesCursor.getColumnIndex("number_of_earthquakes")));
        	stats.setNumberOfVolcanoes(statsCrisesCursor.getInt(statsCrisesCursor.getColumnIndex("number_of_volcanoes")));
        	stats.setUploadedImages(statsCrisesCursor.getInt(statsCrisesCursor.getColumnIndex("uploaded_images")));
        	stats.setPostedComments(statsCrisesCursor.getInt(statsCrisesCursor.getColumnIndex("posted_comments")));
        	stats.setReportedLocations(statsCrisesCursor.getInt(statsCrisesCursor.getColumnIndex("reported_locations")));
        	stats.setReportedMissingPeople(statsCrisesCursor.getInt(statsCrisesCursor.getColumnIndex("reported_missing_people")));
        	
        	Log.i("[PERSISTANT STORAGE]", "Extracting crises stats: " +  
        			stats.getId() + " - " +
        			stats.getLatestCrisisAt() + " - " +
        			stats.getTotalCrises() + " - " +
        			stats.getFirstCrisisAt() + " - " +
        			stats.getNumberOfCyclones() + " - " +
        			stats.getNumberOfFloods() + " - " +
        			stats.getNumberOfEarthquakes() + " - " +
        			stats.getNumberOfVolcanoes() + " - " +
        			stats.getUploadedImages() + " - " +
        			stats.getPostedComments() + " - " +
        			stats.getReportedLocations() + " - " +
        			stats.getReportedMissingPeople());
        }
        
        return stats;
    }
    
    private UsersStats _extractUsersStats(Cursor _c) {
    	UsersStats stats = null;
        boolean hasEntry = _c.moveToFirst();
        if ( hasEntry ) {
        	stats = new UsersStats();
        	stats.setName(_c.getString(_c.getColumnIndex("name")));
        	stats.setUsername(_c.getString(_c.getColumnIndex("username")));
        	stats.setUploadedImages(_c.getInt(_c.getColumnIndex("uploaded_images")));
        	stats.setPostedComments(_c.getInt(_c.getColumnIndex("posted_comments")));
        	stats.setReportedLocations(_c.getInt(_c.getColumnIndex("reported_locations")));
        	stats.setReportedMissingPeople(_c.getInt(_c.getColumnIndex("reported_missing_people"))); 
        	stats.setLatitude(_c.getDouble(_c.getColumnIndex("latitude")));
        	stats.setLongitude(_c.getDouble(_c.getColumnIndex("longitude")));
        	stats.setRadius(_c.getInt(_c.getColumnIndex("radius")));
        	
        	Log.d("[PERSISTANT STORAGE]", "Extracting users stats: " +  
        			stats.getName() + " - " +
        			stats.getUsername() + " - " +
        			stats.getPostedComments() + " - " +
        			stats.getReportedLocations() + " - " +
        			stats.getReportedMissingPeople() + " - " +
        			stats.getUploadedImages() + " - " +
        			stats.getLatitude() + " - " +
        			stats.getLongitude()+ " - " + 
        			stats.getRadius());
        }
        return stats;
    }
}

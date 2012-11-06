package org.sigimera.app.android.backend;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.sigimera.app.android.controller.ApplicationController;
import org.sigimera.app.android.controller.CrisesController;
import org.sigimera.app.android.model.CrisesStats;
import org.sigimera.app.android.model.Crisis;
import org.sigimera.app.android.util.Common;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class PersistentStorage extends SQLiteOpenHelper {
    private static PersistentStorage instance = null;

    private final static int DB_VERSION = 1;
    private final static String DB_NAME = "sigimera.s3db";
    private final static String TABLE_CRISES = "crises";
    private final static String TABLE_COUNTRIES = "countries";
    private final static String TABLE_USER = "user_info";
    private final static String TABLE_CRISES_STATS = "crises_stats";

    private SQLiteDatabase db;

    private final Context context;

    public static PersistentStorage getInstance() {
        if ( null == instance )
            instance = new PersistentStorage(ApplicationController.getInstance().getApplicationContext());
        return instance;
    }

    private PersistentStorage(Context _context) {
        super(_context, DB_NAME, null, DB_VERSION);
        this.context = _context;
    }

    /**
     * Use this method to determine if the application was started the first time.
     */
    @Override
    public void onCreate(SQLiteDatabase _db) {
        this.executeSQLScript(_db, "sql/create_tables.sql");
    }

    @Override
    public void onUpgrade(SQLiteDatabase _db, int _oldVersion, int _newVersion) {
        /**
         * TODO: If schema version is changed. Not before first release...
         */
    }

    public void openDatabaseReadOnly() {
        this.db = getReadableDatabase();
    }

    public void openDatabaseWrite() {
        this.db = getWritableDatabase();
    }
    
    public boolean addNearCrisisInfos(JSONObject _crisis) throws JSONException {
    	if ( _crisis == null  ) return false;
    	this.openDatabaseWrite();
    	ContentValues values = new ContentValues();  
    	
    	values.put("_id", "current_user");
    	values.put("near_crisis_id", _crisis.getString("_id"));
    	if ( _crisis.has("foaf_based_near") ) {
        	values.put("longitude", (Double)_crisis.getJSONArray("foaf_based_near").get(0));
        	values.put("latitude", (Double)_crisis.getJSONArray("foaf_based_near").get(1));
        }
    	
    	this.db.insert(TABLE_USER, null, values);
    	
    	this.onExit();
    	return true;
    }
    
    public boolean addCrisesStats(JSONObject _crisesStats) throws JSONException {
    	if ( _crisesStats == null ) return false;
        this.openDatabaseWrite();
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
        
        this.db.insert(TABLE_CRISES_STATS, null, values);
        
        this.onExit();
        return true;
    }
    
    public CrisesStats getCrisesStats() {
    	CrisesStats stats = null;
    	this.openDatabaseReadOnly();
        Cursor c = this.db.rawQuery("SELECT * FROM " + TABLE_CRISES_STATS + " LIMIT 1", null);
        stats = _extractCrisesStats(c);
        this.onExit();
    	return stats;
    }

    public boolean addCrisis(JSONObject _crisis) throws JSONException {
    	if ( _crisis == null ) return false;
        String crisisID = _crisis.getString("_id");
        if ( checkIfCrisisExists(crisisID) ) {
            // TODO: Delete old crisis or implement some type of update mechanism
            System.err.println("Crisis was found! Not updating it...");
            return false;
        } else {
            this.openDatabaseWrite();
            
            ContentValues values = new ContentValues();
            values.put("_id", _crisis.getString("_id"));
            
            values.put("short_title", CrisesController.getInstance().getShortTitle(_crisis));
            if ( _crisis.has("subject") )
            	values.put("type_icon", Common.getCrisisIcon(_crisis.getString("subject")) + "");
            
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
            

            this.db.insert(TABLE_CRISES, null, values);
            
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
            	this.db.insert(TABLE_COUNTRIES, null, countryValues);
            
            this.onExit();
        }
        return true;
    }
    
    public Cursor getTodayCrisesList() {
    	SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Calendar cal = Calendar.getInstance();		
		String todayDate = format.format(cal.getTime());
    	
        this.openDatabaseReadOnly();
        return this.db.rawQuery("SELECT * FROM "+TABLE_CRISES+" WHERE date(dc_date) >= date('" + todayDate +"') ORDER BY dc_date DESC", null);
    }

    public Cursor getLatestCrisesList(int _number, int _page) {
        this.openDatabaseReadOnly();
        return this.db.rawQuery("SELECT * FROM "+TABLE_CRISES+" ORDER BY dc_date DESC LIMIT "+_number+" OFFSET " +((_page-1) * _number), null);
    }

    public Crisis getCrisis(String _crisisID) {
        this.openDatabaseReadOnly();

        Cursor c = this.db.rawQuery("SELECT * FROM "+TABLE_CRISES+" WHERE _id='" + _crisisID + "'", null);
        Crisis crisis = null;
        if ( c.getCount() != 0) crisis = this._extractCrisis(c);
        this.onExit();

        return crisis;
    }
    
    public Crisis getNearestCrisis() {
        this.openDatabaseReadOnly();

        Cursor c = this.db.rawQuery("SELECT near_crisis_id FROM "+TABLE_USER, null);
        Crisis crisis = null;
        if ( c.moveToFirst() ) {
        	crisis = getCrisis(c.getString(0));
        }
        this.onExit();

        return crisis;
    }

    public Crisis getLatestCrisis() {
        this.openDatabaseReadOnly();

        Cursor c = this.db.rawQuery("SELECT * FROM "+TABLE_CRISES+" ORDER BY dc_date DESC LIMIT 1", null);
        Crisis crisis = null;
        if ( c.getCount() != 0) crisis = this._extractCrisis(c);
        this.onExit();

        return crisis;
    }

    public ArrayList<String> getCountries(String _crisisID) {
        this.openDatabaseReadOnly();

        Cursor c = this.db.rawQuery("SELECT country_name FROM "+TABLE_COUNTRIES+" WHERE crisis_id='" + _crisisID + "'", null);
        ArrayList<String> countries = new ArrayList<String>();

        for (int count=0; count < c.getCount(); count++) {
            c.moveToPosition(count);
            countries.add(c.getString(count));
        }

        this.onExit();

        return countries;
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
            crisis.setSeverity(_c.getString(_c.getColumnIndex("crisis_severity")));
            crisis.setShortTitle(_c.getString(_c.getColumnIndex("short_title")));
            crisis.setStartDate(_c.getString(_c.getColumnIndex("schema_startDate")));
            crisis.setTitle(_c.getString(_c.getColumnIndex("dc_title")));
            crisis.setTypeIcon(_c.getString(_c.getColumnIndex("type_icon")));
            crisis.setVulnerability(_c.getString(_c.getColumnIndex("crisis_vulnerability")));
            crisis.setCountries(getCountries(crisis.getID()));
        }
        return crisis;
    }
    
    private CrisesStats _extractCrisesStats(Cursor _c) {
        CrisesStats stats = null;
        boolean hasEntry = _c.moveToFirst();
        if ( hasEntry ) {
        	stats = new CrisesStats();
        	stats.setId(_c.getString(_c.getColumnIndex("_id")));
        	stats.setLatestCrisisAt(_c.getString(_c.getColumnIndex("latest_crisis_at")));
        	stats.setTotalCrises(_c.getInt(_c.getColumnIndex("total_crises")));
        	stats.setFirstCrisisAt(_c.getString(_c.getColumnIndex("first_crisis_at")));
        	stats.setNumberOfCyclones(_c.getInt(_c.getColumnIndex("number_of_cyclones")));
        	stats.setNumberOfFloods(_c.getInt(_c.getColumnIndex("number_of_floods")));
        	stats.setNumberOfEarthquakes(_c.getInt(_c.getColumnIndex("number_of_earthquakes")));
        	stats.setNumberOfVolcanoes(_c.getInt(_c.getColumnIndex("number_of_volcanoes")));
        	stats.setUploadedImages(_c.getInt(_c.getColumnIndex("uploaded_images")));
        	stats.setPostedComments(_c.getInt(_c.getColumnIndex("posted_comments")));
        	stats.setReportedLocations(_c.getInt(_c.getColumnIndex("reported_locations")));
        	stats.setReportedMissingPeople(_c.getInt(_c.getColumnIndex("reported_missing_people")));
        }
        return stats;
    }
    

    public long getCrisesNumber() {
        return DatabaseUtils.queryNumEntries(getReadableDatabase(), TABLE_CRISES);
    }

    public long getCountriesNumber() {
        return DatabaseUtils.queryNumEntries(getReadableDatabase(), TABLE_COUNTRIES);
    }
    
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

    public void onExit() { this.db.close(); }

}

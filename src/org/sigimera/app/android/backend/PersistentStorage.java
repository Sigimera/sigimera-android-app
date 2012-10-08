package org.sigimera.app.android.backend;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.sigimera.app.android.controller.ApplicationController;
import org.sigimera.app.android.controller.CrisesController;
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
            	values.put("longitude", (Double)_crisis.getJSONArray("foaf_based_near").get(0));
            	values.put("latitude", (Double)_crisis.getJSONArray("foaf_based_near").get(1));
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

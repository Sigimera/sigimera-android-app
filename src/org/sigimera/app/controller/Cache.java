package org.sigimera.app.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Cache extends SQLiteOpenHelper {
	private final static int DB_VERSION = 1;
	private final static String DB_NAME = "sigimera.s3db";
	private final static String TABLE_CRISES = "crises";
	private final static String TABLE_COUNTRIES = "countries";
	
	private final Context context;

	public Cache(Context _context) {
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
	
	public boolean addCrisis(JSONObject _crisis) throws JSONException {
		if ( checkIfCrisisExists(_crisis.getString("_id")) ) {
			// TODO: Delete old crisis
			System.out.println("Crisis was found! Not updating it...");
			return false;
		} else {
			Iterator<?> iter = _crisis.keys();
			ContentValues values = new ContentValues();
			while ( iter.hasNext() ) {
				Object keyObject = iter.next();
				if ( keyObject instanceof String ) {
					Object valueObject;
					try {
						valueObject = _crisis.get((String) keyObject);
						if ( valueObject instanceof String ) {
							values.put((String)keyObject, (String)valueObject);
						} else System.err.println("Not able to add value for key '" + keyObject + "'");
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			SQLiteDatabase db = getWritableDatabase();
			db.beginTransaction();
			db.insert("crises", null, values);
			db.endTransaction();
		}
		return true;
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
				/**
				 * TODO: Delete System.out.println
				 */
				System.out.println("Found SQL statement: " + sqlStatement);
				if ( sqlStatement.length() > 0 ) {
					_database.execSQL(sqlStatement + ";");
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}

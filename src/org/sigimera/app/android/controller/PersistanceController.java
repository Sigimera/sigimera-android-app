package org.sigimera.app.android.controller;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.sigimera.app.android.backend.PersistentStorage2;
import org.sigimera.app.android.backend.network.CrisesHttpHelper;
import org.sigimera.app.android.backend.network.NearCrisesHttpHelper;
import org.sigimera.app.android.backend.network.SingleCrisisHttpHelper;
import org.sigimera.app.android.backend.network.StatisticCrisesHttpHelper;
import org.sigimera.app.android.backend.network.StatisticUsersHttpHelper;
import org.sigimera.app.android.model.CrisesStats;
import org.sigimera.app.android.model.Crisis;
import org.sigimera.app.android.model.UsersStats;
import org.sigimera.app.android.util.Common;

import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

/**
 * This class is responsible for saving and retrieving the concepts persistent.
 * 
 * @author Corneliu-Valentin Stanciu
 * @email corneliu.stanciu@sigimera.org
 */
public class PersistanceController {

	private static PersistanceController instance = null;
	private PersistentStorage2 pershandler;

	private PersistanceController() {
		this.pershandler = ApplicationController.getInstance()
				.getPersistentStorageHandler();
	}

	public static PersistanceController getInstance() {
		if (null == instance)
			instance = new PersistanceController();
		return instance;
	}
	
	/**
	 * 
	 * @param _authToken
	 * @return
	 */
	public CrisesStats getCrisesStats(String _authToken, Location _location) {
    	CrisesStats stats = this.pershandler.getCrisesStats();
    	if ( null == stats && _authToken != null ) {
    		AsyncTask<String, Void, JSONObject> crisesStatsHelper = new StatisticCrisesHttpHelper().execute(_authToken);
    		AsyncTask<String, Void, JSONArray> crisesNearHelper = new NearCrisesHttpHelper().execute(_authToken, 1 +"", _location.getLatitude()+"", _location.getLongitude()+"");
    		try {
				this.pershandler.addCrisesStats(crisesStatsHelper.get());						
				this.pershandler.addNearCrises(crisesNearHelper.get());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		stats = this.pershandler.getCrisesStats();
    	} else if (stats.getNearestCrisis() == null && _authToken != null ) {
    		AsyncTask<String, Void, JSONArray> crisesNearHelper = new NearCrisesHttpHelper().execute(_authToken, 1 +"", _location.getLatitude()+"", _location.getLongitude()+"");
    		try {
				this.pershandler.addNearCrises(crisesNearHelper.get());
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		stats = this.pershandler.getCrisesStats();
    	}    	    	
    	
    	return stats;
    }
    
	/**
	 * 
	 * @param _authToken
	 * @return
	 */
    public UsersStats getUsersStats(String _authToken) {
    	UsersStats stats = this.pershandler.getUsersStats();
    	if ( (null == stats && _authToken != null) || (stats != null && stats.getUsername() == null ) ) {
    		AsyncTask<String, Void, JSONObject> crisesStatsHelper = new StatisticUsersHttpHelper().execute(_authToken);
    		try {    			
    			Log.d("[CRISES CONTROLLER]", "Response from API: " + crisesStatsHelper.get());
				this.pershandler.addUsersStats(crisesStatsHelper.get());				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		stats = this.pershandler.getUsersStats();
    	}
    	return stats;    	
    }
    
    public Crisis getCrisis(String _authToken, String _crisisID) {
    	Crisis crisis = this.pershandler.getCrisis(_crisisID);
    	if ( null == crisis ) {
    		AsyncTask<String, Void, JSONObject> singleCrisisTask = new SingleCrisisHttpHelper().execute(_authToken, _crisisID);
    		try {
    			this.pershandler.addCrisis(singleCrisisTask.get());
    		} catch (JSONException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		} catch (InterruptedException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		} catch (ExecutionException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
    		crisis = this.pershandler.getCrisis(_crisisID);
    	}
    	return crisis;
    }
    
    public boolean updateNearCrises(String _auth_token, int _page, Location _location) {
    	AsyncTask<String, Void, JSONArray> crisesHelper = new NearCrisesHttpHelper().execute(_auth_token, _page+"", _location.getLatitude()+"", _location.getLongitude()+"");
    	JSONArray retArray = null;
        try {
            retArray = crisesHelper.get();
            this.pershandler.updateNearCrisesAndLocation(retArray, _location);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ExecutionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    	return true;
	}

	/**
	 * Retrieve a crisis page with X crisis.
	 * 
	 * @param _authToken The authentication token as retrieved after successful login
	 * @param _pageThe page to retrieve, starting from page 1 (one)
	 * @return 
	 */
	public ArrayList<Crisis> getCrises(String _authToken, int _page) {
		ArrayList<Crisis> crises = this.pershandler.getLatestCrisesList(10,
				_page);
		if (crises.isEmpty()) {
			storeLatestCrises(_authToken, _page);
			crises = this.pershandler.getLatestCrisesList(10, _page);
		}
		return crises;
	}
	
	/**
	  *
	  * @param crisis The single crisis JSON object as received from the Sigimera REST API
	  * @return The shortened title that is human-readable
	  */
	 public String getShortTitle(JSONObject crisis) {
	     String title = "";
	     try {
	         title += crisis.getString("crisis_alertLevel");
	         title += " ";
	         if ( crisis.has("subject") )
	         	title += crisis.getString("subject");
	         title += " alert ";
	
	         if ( crisis.has("gn_parentCountry") && crisis.getJSONArray("gn_parentCountry").length() > 0 ){
	             title += " in ";
	             title += Common.capitalize(crisis.getJSONArray("gn_parentCountry").get(0).toString());
	         }
	         return title;
	     } catch (JSONException e) {
	         e.printStackTrace();
	     }
	     return null;
	 }
	
	/**
	 * 
	 * @return
	 */
	public long getCacheSize() { 
		return this.pershandler.getCacheSize(); 
	}

	

	/****************************************************************
	 * 
	 ****************************************************************/

	private void storeLatestCrises(String _auth_token, int _page) {
		AsyncTask<String, Void, JSONArray> crisesHelper = new CrisesHttpHelper()
				.execute(_auth_token, _page + "");
		JSONArray crises = null;
		try {
			crises = crisesHelper.get();
			if (crises != null) {
				for (int count = 0; count < crises.length(); count++) {
					try {
						JSONObject crisis = (JSONObject) crises.get(count);
						this.pershandler.addCrisis(crisis);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

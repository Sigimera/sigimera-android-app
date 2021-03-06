package org.sigimera.app.android.controller;
/**
 * Sigimera Crises Information Platform Android Client
 * Copyright (C) 2013 by Sigimera
 * All Rights Reserved
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 2 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
import java.util.ArrayList;
import java.util.Date;
import java.util.Observable;
import java.util.concurrent.ExecutionException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.sigimera.app.android.backend.PersistentStorage;
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
public class PersistanceController extends Observable {

	private static PersistanceController instance = null;
	private PersistentStorage pershandler;

	private PersistanceController() {
		this.pershandler = ApplicationController.getInstance()
				.getPersistentStorageHandler();
	}

	public static PersistanceController getInstance() {
		if (null == instance)
			instance = new PersistanceController();
		return instance;
	}
	
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
	
	/**
	 * 
	 * @param _authToken
	 * @return
	 */
	public CrisesStats getCrisesStats(String _authToken) {
    	CrisesStats stats = this.pershandler.getCrisesStats();
    	
    	if ( null == stats && _authToken != null ) {
    		AsyncTask<String, Void, JSONObject> crisesStatsHelper = new StatisticCrisesHttpHelper().execute(_authToken);
    		try {    			
				this.pershandler.addCrisesStats(crisesStatsHelper.get());						
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
	
	public long getCacheSize() { 
		return this.pershandler.getCacheSize(); 
	}
	
	public Crisis getNearCrisis(String _auth_token, Location _location) {
		String crisisID = this.pershandler.getNearCrisis();
		if ( crisisID == null ) {
			updateNearCrises(_auth_token, 1, _location);
			crisisID = this.pershandler.getNearCrisis();
		}
		Log.i("[PERSISTANCE CONTROLLER]", "Retrieve near crisis id: " + crisisID);
		
		if (crisisID != null)
			return getCrisis(_auth_token, crisisID);
		
		return null;
	}
	
	public Crisis getLatestCrisis(String _auth_token) {
		String crisisID = this.pershandler.getLatestCrisis();
		Log.i("[PERSISTANCE CONTROLLER]", "Retrieve latest crisis id: " + crisisID);
		
		if (crisisID != null)
			return getCrisis(_auth_token, crisisID);
		
		return null;
	}
	
	public ArrayList<Crisis> getTodayCrises() {
		ArrayList<Crisis> crisesList = this.pershandler.getTodayCrises();
		
		return crisesList;
	}
	
	public ArrayList<Crisis> getNearCrises() {
		return this.pershandler.getNearCrises();
	}
	
	
	/**************************************************************
	 *  Methods for UPDATE functionality
	 **************************************************************/
	
	/**
	 * Update crises statistics
	 * 
	 * @param _auth_token
	 * @param _location
	 */
	private void updateCrisesStats(String _auth_token, Location _location) {
		Log.i("[PERSISTENT CONTROLLER]", "Check if there are new crises statistics");
		CrisesStats crisesStats = getCrisesStats(_auth_token);
		
		if ( crisesStats != null && crisesStats.getLatestCrisisAt() != null) {
			Date date = Common.getDate(crisesStats.getLatestCrisisAt());			
			
			AsyncTask<String, Void, JSONObject> crisesStatsHelper = new StatisticCrisesHttpHelper().execute(_auth_token);				
    		try {
    			JSONObject tmpStats = crisesStatsHelper.get();
    			if ( tmpStats != null ) {
	    			String latestCrisisAt = tmpStats.getString("latest_crisis_at");
	    			if ( latestCrisisAt != null && Common.getDate(latestCrisisAt).after(date) ) {    
	    				Log.i("[PERSISTENT CONTROLLER]", "There are new crises statistics available. Update the existing ones");
	    				this.pershandler.updateCrisesStats(crisesStatsHelper.get());
//	    				this.pershandler.addCrisesStats(crisesStatsHelper.get());	
	    			}else
	    				Log.i("[PERSISTENT CONTROLLER]", "Crises statistics are up to date.");
    			}
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
		} else
			Log.i("[PERSISTENT CONTROLLER]", "Crises statistics or latest crisis date are empty.");
	}
	
	/**
	 * Update user statistics
	 * 
	 * @param _auth_token
	 */
	public void updateUserStats(String _auth_token) {
		Log.i("[PERSISTENT CONTROLLER]", "Check if there are new user statistics");
		UsersStats userStats = getUsersStats(_auth_token);
		
		if (userStats != null) {
			//TODO: figure some update mechanism for user stats
		}
	}
	
	public void updateNearCrisesRadius(int _radius, String _userID) {
		if (this.pershandler.updateNearCrisesRadius(_radius, _userID))
			this.changesNotifier(_radius);
	}
	
	public void updateNearCrises(String _auth_token, int _page, Location _location) {
		if ( _location != null && _auth_token != null ) {
	        try {
	        	AsyncTask<String, Void, JSONArray> crisesHelper = new NearCrisesHttpHelper().execute(_auth_token, _page+"", _location.getLatitude()+"", _location.getLongitude()+"");
	        	JSONArray retArray = null;
	            retArray = crisesHelper.get();
	            this.pershandler.updateNearCrises(retArray);
	        } catch (InterruptedException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	        } catch (ExecutionException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	        } catch (Exception e) {
	        	e.printStackTrace();
	        }
		}
	}
	
	/**
	 * Method that synchronise everything.
	 * 
	 * @throws InterruptedException 
	 */
	public void updateEverything(String _auth_token) throws InterruptedException {
		Log.i("[PERSISTENT CONTROLLER]", "BEGIN TO UPDATE EVERYTHING with auth token: " + _auth_token);
		ApplicationController.getInstance().setEverythingUpdated(false);				
		Location lastLocation = LocationController.getInstance().getLastKnownLocation();
		Thread.sleep(1000);
		
		Log.i("[PERSISTENT CONTROLLER]", "UPDATE CRISES");
		this.storeLatestCrises(_auth_token, 1);
		Thread.sleep(1000);
		
		Log.i("[PERSISTENT CONTROLLER]", "UPDATE CRISES STATISTICS");
		this.updateCrisesStats(_auth_token, lastLocation);
		Thread.sleep(1000);
		
//		Log.i("[PERSISTENT CONTROLLER]", "UPDATE NEAR CRISES");
//		this.updateNearCrises(_auth_token, 1, lastLocation);
//		Thread.sleep(1000);
		
		Log.i("[PERSISTENT CONTROLLER]", "UPDATE USER STATISTICS");
		this.updateUserStats(_auth_token);
		Thread.sleep(1000);
		
		ApplicationController.getInstance().setEverythingUpdated(true);
		ApplicationController.getInstance().setUpdatedOnce(true);
		Log.i("[PERSISTENT CONTROLLER]", "END TO UPDATE EVERYTHING");
	}
	
	private void changesNotifier(int radius) {
		setChanged();
		notifyObservers(radius);
	}
}

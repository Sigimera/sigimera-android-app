/**
 * Sigimera Crises Information Platform Android Client
 * Copyright (C) 2012 by Sigimera
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
package org.sigimera.app.android.controller;

import java.util.concurrent.ExecutionException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.sigimera.app.android.backend.PersistentStorage;
import org.sigimera.app.android.backend.network.CrisesHttpHelper;
import org.sigimera.app.android.backend.network.NearCrisesHttpHelper;
import org.sigimera.app.android.backend.network.SingleCrisisHttpHelper;
import org.sigimera.app.android.backend.network.StatisticCrisesHttpHelper;
import org.sigimera.app.android.model.CrisesStats;
import org.sigimera.app.android.model.Crisis;

import android.database.Cursor;
import android.location.Location;
import android.os.AsyncTask;

/**
 * @author Corneliu-Valentin Stanciu, Alex Oberhauser
 * @email  corneliu.stanciu@sigimera.org, alex.oberhauser@sigimera.org
 */
public class CrisesController {
    private static CrisesController instance = null;
    private PersistentStorage pershandler;

    private CrisesController() {
        this.pershandler = ApplicationController.getInstance().getPersistentStorageHandler();
    }

    public static CrisesController getInstance() {
        if ( null == instance )
            instance = new CrisesController();
        return instance;
    }

    private void storeLatestCrises(String _auth_token, int _page) {
        AsyncTask<String, Void, JSONArray> crisesHelper = new CrisesHttpHelper().execute(_auth_token, _page+"");
        JSONArray crises = null;
        try {
            crises = crisesHelper.get();
            if ( crises != null ) {
	            for ( int count = 0; count < crises.length(); count++ ) {
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
     * Retrieve a crisis page with X crisis.
     *
     * @param _authToken The authentication token as retrieved after successful login
     * @param _page The page to retrieve, starting from page 1 (one)
     * @return
     */
    public Cursor getCrises(String _authToken, int _page) {
        Cursor c = this.pershandler.getLatestCrisesList(10, _page);        
        if ( c.getCount() == 0 ) {
            storeLatestCrises(_authToken, _page);
            c = this.pershandler.getLatestCrisesList(10, _page);
        }
        return c;
    }
    
    public Cursor getTodayCrises(String _authToken) {
        return this.pershandler.getTodayCrisesList();
    }

    public Crisis getLatestCrisis(String _authToken) {
        Crisis crisis = this.pershandler.getLatestCrisis();
        if ( crisis == null && _authToken != null ) {
            storeLatestCrises(_authToken, 1);
            crisis = this.pershandler.getLatestCrisis();
        }
        return crisis;
    }
    
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
     * TODO: Extract the crises from the cache...
     * @param _auth_token
     * @param _page
     * @param _location
     * @return
     */
    public JSONArray getNearCrises(String _auth_token, int _page, Location _location) {
        AsyncTask<String, Void, JSONArray> crisesHelper = new NearCrisesHttpHelper().execute(_auth_token, _page+"", _location.getLatitude()+"", _location.getLongitude()+"");
        JSONArray retArray = null;
        try {
            retArray = crisesHelper.get();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ExecutionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return retArray;
    }
    
    /**
     * 
     * @param _auth_token
     * @param _location
     * @return
     */
    public Crisis getNearCrisis(String _authToken, Location _location) {    	   
        Crisis crisis = this.pershandler.getNearestCrisis();        
        if ( null == crisis ) {     	
            AsyncTask<String, Void, JSONArray> crisesHelper = new NearCrisesHttpHelper().execute(_authToken, 1+"", _location.getLatitude()+"", _location.getLongitude()+"");
            try {
            	JSONArray crisesArray = crisesHelper.get();
            	JSONObject nearestCrisis = (JSONObject) crisesArray.get(0);            	
            	
            	this.pershandler.addNearCrisisInfos(nearestCrisis);
            	this.pershandler.addCrisis(nearestCrisis);
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            crisis = this.pershandler.getNearestCrisis();
        }
        
        return crisis;
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
                title += capitalize(crisis.getJSONArray("gn_parentCountry").get(0).toString());
            }
            return title;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String capitalize(String s) {
        if (s.length() == 0) return s;
        return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
    }
}

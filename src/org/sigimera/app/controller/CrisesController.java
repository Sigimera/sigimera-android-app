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
package org.sigimera.app.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutionException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.sigimera.app.helper.crises.CrisesHttpHelper;
import org.sigimera.app.helper.crises.NearCrisesHttpHelper;
import org.sigimera.app.model.Constants;
import org.sigimera.app.util.Config;

import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

/**
 * @author Corneliu-Valentin Stanciu, Alex Oberhauser
 * @email  corneliu.stanciu@sigimera.org, alex.oberhauser@sigimera.org
 */
public class CrisesController {
	private static CrisesController instance = null;
	
	private CrisesController() {}
	
	public static CrisesController getInstance() {
		if ( null == instance )
			instance = new CrisesController();
		return instance;
	}
	
	/**
	 * TODO: Extract the crises from the cache...
	 * Retrieve a crisis page with X crisis.
	 * 
	 * @param _auth_token The authentication token as retrieved after successful login
	 * @param _page The page to retrieve, starting from page 1 (one)
	 * @return
	 */
	public JSONArray getCrises(String _auth_token, int _page) {
		AsyncTask<String, Void, JSONArray> crisesHelper = new CrisesHttpHelper().execute(_auth_token, _page+"");
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
	 * @param crisis The single crisis JSON object as received from the Sigimera REST API
	 * @return The shortened title that is human-readable
	 */
	public String getShortTitle(JSONObject crisis) {
		String title = "";
		try {
			title += crisis.getString("crisis_alertLevel");
			title += " ";
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

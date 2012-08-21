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

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.sigimera.app.model.Constants;
import org.sigimera.app.util.Config;

import android.location.Location;
import android.util.Log;

/**
 * 
 * @author Corneliu-Valentin Stanciu
 * @email  corneliu.stanciu@sigimera.org
 */
public class CrisesController {
	private static CrisesController instance = null;
	
	private final String HOST = Config.getInstance().getAPIHost()+"/crises.json?output=short&auth_token=";
	private final String FREE_HOST = Config.getInstance().getFreeAPIHost();
	
	private CrisesController() {}
	
	public static CrisesController getInstance() {
		if ( null == instance )
			instance = new CrisesController();
		return instance;
	}
	
	/**
	 * Retrieve a crisis page with X crisis.
	 * 
	 * @param _auth_token The authentication token as retrieved after successful login
	 * @param _page The page to retrieve, starting from page 1 (one)
	 * @return
	 */
	public JSONArray getCrises(String _auth_token, int _page) {
		HttpClient httpclient = new DefaultHttpClient();
		HttpGet request;
		if ( _auth_token != null )
			request = new HttpGet(HOST + _auth_token + "&page=" + _page);
		else
			request = new HttpGet(FREE_HOST);
		try {
			Log.i(Constants.LOG_TAG_SIGIMERA_APP, "API CALL: " + request.getURI());
			HttpResponse result = httpclient.execute(request);
			JSONArray json_response = new JSONArray(new BufferedReader(new InputStreamReader(result.getEntity().getContent())).readLine());
			return json_response;
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			httpclient.getConnectionManager().shutdown();
		}
		return null;
	}
	
	public JSONArray getNearCrises(String _auth_token, int _page, Location _location) {
		HttpClient httpclient = new DefaultHttpClient();
		HttpGet request = new HttpGet(HOST + _auth_token + "&page=" + _page 
				+ "&lat=" + _location.getLatitude() + "&lon=" + _location.getLongitude() 
				+ "&radius=" + Constants.LOCATION_RADIUS);
		try {
			Log.i(Constants.LOG_TAG_SIGIMERA_APP, "API CALL: " + request.getURI());
			HttpResponse result = httpclient.execute(request);
			JSONArray json_response = new JSONArray(new BufferedReader(new InputStreamReader(result.getEntity().getContent())).readLine());
			return json_response;
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			httpclient.getConnectionManager().shutdown();
		}
		return null;
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

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
import org.sigimera.app.model.SigimeraConstants;
import org.sigimera.app.util.Config;

import android.location.Location;
import android.util.Log;

public class CrisesController {
	private static CrisesController instance = null;
	
	private final String HOST = Config.getInstance().getAPIHost()+"/crises.json?output=short&auth_token=";
	
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
		HttpGet request = new HttpGet(HOST + _auth_token + "&page=" + _page);
		try {
			Log.i(SigimeraConstants.LOG_TAG_SIGIMERA_APP, "API CALL: " + request.getURI());
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
				+ "&radius=" + SigimeraConstants.LOCATION_RADIUS);
		try {
			Log.i(SigimeraConstants.LOG_TAG_SIGIMERA_APP, "API CALL: " + request.getURI());
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
			title += crisis.getJSONArray("dc_subject").get(0);
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

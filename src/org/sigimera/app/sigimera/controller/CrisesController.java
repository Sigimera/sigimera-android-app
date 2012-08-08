package org.sigimera.app.sigimera.controller;

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
import org.sigimera.app.sigimera.Config;

public class CrisesController {
	private static CrisesController instance = null;
	
	private static final String HOST = Config.API_HOST + "/crises.json?auth_token=";
	
	private CrisesController() {}
	
	public static CrisesController getInstance() {
		if ( null == instance )
			instance = new CrisesController();
		return instance;
	}
	
	public JSONArray getCrises(String _auth_token, int _page) {
		HttpClient httpclient = new DefaultHttpClient();
		HttpGet request = new HttpGet(HOST + _auth_token + "&page=" + _page);
		try {
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
}

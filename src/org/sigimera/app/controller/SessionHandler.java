package org.sigimera.app.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import org.sigimera.app.exception.AuthenticationErrorException;
import org.sigimera.app.util.Config;

import android.content.SharedPreferences;

public class SessionHandler {
	private static SessionHandler instance = null;
	
	private SharedPreferences settings;
	private final String HOST = Config.WWW_HOST+"/tokens.json";
	
	private SessionHandler(SharedPreferences _settings) {
		this.settings = _settings;
	}
	
	public static SessionHandler getInstance(SharedPreferences _settings) {
		if ( null == instance )
			instance = new SessionHandler(_settings);
		return instance;
	}
	
	public boolean login(String _email, String _password) {	
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost request = new HttpPost(HOST);
		
		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		pairs.add(new BasicNameValuePair("user[email]", _email));
		pairs.add(new BasicNameValuePair("user[password]", _password));		
        		
		try {
			request.setEntity(new UrlEncodedFormEntity(pairs));
			HttpResponse result = httpclient.execute(request);
			JSONObject json_response = new JSONObject(new BufferedReader(new InputStreamReader(result.getEntity().getContent())).readLine());
			if ( json_response.has("auth_token") ) {
				SharedPreferences.Editor editor = settings.edit();
				editor.putString("auth_token", json_response.getString("auth_token"));
				editor.commit();
				return true;
			} else if ( json_response.has("error") ) {
				return false;
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			httpclient.getConnectionManager().shutdown();
		}
		return false;
	}
	
	public String getAuthenticationToken() throws AuthenticationErrorException {
		String auth_token = this.settings.getString("auth_token", null);
		if ( null == auth_token )
			throw new AuthenticationErrorException();
		else
			return auth_token;
	}
}

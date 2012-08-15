package org.sigimera.app.fragment;

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
import org.sigimera.app.controller.ApplicationController;
import org.sigimera.app.util.Config;

import android.content.SharedPreferences;
import android.os.AsyncTask;

public class LoginHttpHelper extends AsyncTask<String, Void, Boolean> {

	private final String HOST = Config.getInstance().getWWWHost()+"/tokens.json";
	
	@Override
	protected Boolean doInBackground(String... params) {
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost request = new HttpPost(HOST);
		
		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		pairs.add(new BasicNameValuePair("user[email]", params[0]));
		pairs.add(new BasicNameValuePair("user[password]", params[1]));		
        		
		try {
			request.setEntity(new UrlEncodedFormEntity(pairs));
			HttpResponse result = httpclient.execute(request);
			JSONObject json_response = new JSONObject(new BufferedReader(new InputStreamReader(result.getEntity().getContent())).readLine());
			if ( json_response.has("auth_token") ) {
				SharedPreferences.Editor editor = ApplicationController.getInstance().getSharedPreferences().edit();
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

}
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
package org.sigimera.app.android.backend.network;

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
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import org.sigimera.app.android.controller.ApplicationController;
import org.sigimera.app.android.util.Config;

import android.content.SharedPreferences;
import android.os.AsyncTask;

public class LoginHttpHelper extends AsyncTask<String, Void, Boolean> {

	private final String HOST = Config.getInstance().getWWWHost()+"/tokens.json";
	
	@Override
	protected Boolean doInBackground(String... params) {
        HttpClient httpclient = new MyHttpClient(ApplicationController.getInstance().getApplicationContext());
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

package org.sigimera.app.android.backend.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.json.JSONArray;
import org.json.JSONException;
import org.sigimera.app.android.controller.ApplicationController;
import org.sigimera.app.android.model.Constants;
import org.sigimera.app.android.util.Config;

import android.os.AsyncTask;
import android.util.Log;

public class NearCrisesHttpHelper extends AsyncTask<String, Void, JSONArray> {

	private final String HOST = Config.getInstance().getAPIHost()
			+ "/crises.json?output=short&auth_token=";

	@Override
	protected JSONArray doInBackground(String... _params) {
		String auth_token = _params[0];
		String page = _params[1];
		String latitude = _params[2];
		String longitude = _params[3];

		int radius = Constants.LOCATION_RADIUS;
		//TODO:
//		if (CrisesController.getInstance().getNearCrisesRadius() != 0)
//			radius = CrisesController.getInstance().getNearCrisesRadius();

		HttpClient httpclient = new MyHttpClient(ApplicationController
				.getInstance().getApplicationContext());
		HttpGet request = new HttpGet(HOST + auth_token + "&page=" + page
				+ "&lat=" + latitude + "&lon=" + longitude + "&radius="
				+ radius);
		try {
			Log.i("[NEAR CRISES HTTP HELPER]", "API CALL: " + request.getURI());
			HttpResponse result = httpclient.execute(request);
			HttpEntity entity = result.getEntity();
			if (entity != null) {
				BufferedReader bf = new BufferedReader(new InputStreamReader(
						entity.getContent()));
				if (bf != null) {
					JSONArray json_response = new JSONArray(bf.readLine());
					Log.i("[NEAR CRISES HTTP HELPER]", "RESPONSE: " + json_response);
					return json_response;
				}
			}
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

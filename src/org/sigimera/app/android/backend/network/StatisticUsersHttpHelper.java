package org.sigimera.app.android.backend.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.json.JSONException;
import org.json.JSONObject;
import org.sigimera.app.android.controller.ApplicationController;
import org.sigimera.app.android.model.Constants;
import org.sigimera.app.android.util.Config;

import android.os.AsyncTask;
import android.util.Log;

public class StatisticUsersHttpHelper extends AsyncTask<String, Void, JSONObject>{
	
	private final String HOST = Config.getInstance().getAPIHost()+"/stats/users.json?auth_token=";

	@Override
	protected JSONObject doInBackground(String... _params) {
		String auth_token = _params[0];

    	try { Thread.sleep(1000); } catch (InterruptedException e1) { e1.printStackTrace();	} // Respect the API limits
        HttpClient httpclient = new MyHttpClient(ApplicationController.getInstance().getApplicationContext());
        HttpGet request = new HttpGet(HOST + auth_token);
        
        try {
            Log.d(Constants.LOG_TAG_SIGIMERA_APP, "API CALL: " + request.getURI());
            HttpResponse result = httpclient.execute(request);
            JSONObject json_response = new JSONObject(new BufferedReader(new InputStreamReader(result.getEntity().getContent())).readLine());
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

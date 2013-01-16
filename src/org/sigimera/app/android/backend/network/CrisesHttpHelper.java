package org.sigimera.app.android.backend.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

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

public class CrisesHttpHelper extends AsyncTask<String, Void, JSONArray> {

    private final String HOST = Config.getInstance().getAPIHost()+"/crises.json?output=short&auth_token=";
    private final String FREE_HOST = Config.getInstance().getFreeAPIHost();
    
    @Override
    protected void onPreExecute() {    	
    	super.onPreExecute();
    	ApplicationController.getInstance().setAPICallBusy(true);    	
    }
    
    @Override
    protected void onPostExecute(JSONArray result) {
    	super.onPostExecute(result);
    	ApplicationController.getInstance().setAPICallBusy(false);
    }

    @Override
    protected JSONArray doInBackground(String... _params) {
        String auth_token = _params[0];
        String page = _params[1];

        HttpClient httpclient = new MyHttpClient(ApplicationController.getInstance().getApplicationContext());
        HttpGet request;
        if ( auth_token != null )
            request = new HttpGet(HOST + auth_token + "&page=" + page);
        else
            request = new HttpGet(FREE_HOST);
        try {
            Log.d(Constants.LOG_TAG_SIGIMERA_APP, "API CALL: " + request.getURI());
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

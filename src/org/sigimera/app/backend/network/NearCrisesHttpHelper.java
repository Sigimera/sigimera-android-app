package org.sigimera.app.backend.network;

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
import org.sigimera.app.model.Constants;
import org.sigimera.app.util.Config;

import android.os.AsyncTask;
import android.util.Log;

public class NearCrisesHttpHelper extends AsyncTask<String, Void, JSONArray> {

    private final String HOST = Config.getInstance().getAPIHost()+"/crises.json?output=short&auth_token=";

    @Override
    protected JSONArray doInBackground(String... _params) {
        String auth_token = _params[0];
        String page = _params[1];
        String latitude = _params[2];
        String longitude = _params[3];

        HttpClient httpclient = new DefaultHttpClient();
        HttpGet request = new HttpGet(HOST + auth_token + "&page=" + page
                + "&lat=" + latitude + "&lon=" + longitude
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

}

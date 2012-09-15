package org.sigimera.app.backend.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;
import org.sigimera.app.model.Constants;
import org.sigimera.app.util.Config;

import android.os.AsyncTask;
import android.util.Log;

public class SingleCrisisHttpHelper extends AsyncTask<String, Void, JSONObject> {

    private final String HOST = Config.getInstance().getAPIHost()+"/crises/";

    @Override
    protected JSONObject doInBackground(String... _params) {
    	try { Thread.sleep(2000); } catch (InterruptedException e1) { e1.printStackTrace();	} // Respect the API limits
        String auth_token = _params[0];
        if ( auth_token == null ) return null;

        String crisis_id = _params[1];

        DefaultHttpClient httpclient = new DefaultHttpClient();
        httpclient.getCookieStore().clear(); // We do not use cookies for the authentication...
        HttpGet request = new HttpGet(HOST + crisis_id + ".json?auth_token=" + auth_token);

        try {
            Log.i(Constants.LOG_TAG_SIGIMERA_APP, "API CALL: " + request.getURI());

            HttpResponse result = httpclient.execute(request);
            String response = new BufferedReader(new InputStreamReader(result.getEntity().getContent())).readLine();
            JSONObject json_response = new JSONObject(response);
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

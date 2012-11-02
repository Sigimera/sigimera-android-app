package org.sigimera.app.android.backend.network;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.DefaultHttpClient;
import org.sigimera.app.android.controller.ApplicationController;
import org.sigimera.app.android.model.Constants;
import org.sigimera.app.android.util.Config;

import com.google.android.gcm.GCMRegistrar;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

/**
 * This AsyncTask is responsible for the update of the GPS Coordinates of the mobile device. It 
 * sends an update request to the Sigimera API and could be called manually, e.g. if REFRESH 
 * message was received or if location is changed.
 * 
 * @author Alex Oberhauser
 *
 */
public class LocationUpdaterHttpHelper extends AsyncTask<String, Void, Boolean> {

    private final String HOST = Config.getInstance().getAPIHost()+"/gcm/location/";

    @Override
    protected Boolean doInBackground(String... _params) {
        String auth_token = _params[0];
        String latitude = _params[1];
        String longitude = _params[2];

        HttpClient httpclient = new DefaultHttpClient();
        HttpPut request;
        if ( auth_token != null ) {
        	Context context = ApplicationController.getInstance().getApplicationContext();
            request = new HttpPut(HOST + GCMRegistrar.getRegistrationId(context) + "?auth_token=" + auth_token + "&lat=" + latitude + "&lon=" + longitude);
	        try {
	            Log.d(Constants.LOG_TAG_SIGIMERA_APP, "API CALL: " + request.getURI());
	            httpclient.execute(request);
	            // TODO: Check here the returned HTTP status
	            return true;
	        } catch (ClientProtocolException e) {
	            e.printStackTrace();
	        } catch (IOException e) {
	            e.printStackTrace();
	        } catch (IllegalStateException e) {
	            e.printStackTrace();
	        } finally {
	            httpclient.getConnectionManager().shutdown();
	        }
        }
        return false;
    }

}

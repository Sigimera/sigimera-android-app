package org.sigimera.app;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;
import org.sigimera.app.controller.SessionHandler;
import org.sigimera.app.exception.AuthenticationErrorException;
import org.sigimera.app.util.Config;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.widget.Toast;

import com.google.android.gcm.GCMBaseIntentService;

/**
 * This Intent is called when the GCM executing process has finished.
 */
public class GCMIntentService extends GCMBaseIntentService {
	private static final String HOST = Config.getInstance().getAPIHost() + "/gcm";
	private final Handler mainThreadHandler;
	
	public GCMIntentService() {
		super(GCMIntentService.class.getName());
		this.mainThreadHandler = new Handler();
	}
	
	@Override
	protected void onError(Context arg0, String arg1) {
		// TODO Auto-generated method stub
	}

	@Override
	protected void onMessage(Context _context, Intent _message) {
		final Intent msg = _message;
		this.mainThreadHandler.post(new Runnable() {
            public void run() {
            	final String type = msg.getStringExtra("sig_message_type");
            	if ( type.equalsIgnoreCase("NEW_CRISIS") ) {
            		StringBuffer message = new StringBuffer();
            		message.append(msg.getStringExtra("sig_message_type"));
            		message.append(" :: ");
            		message.append(msg.getStringExtra("crisis_id") );
            		Toast.makeText(getApplicationContext(), message.toString(), Toast.LENGTH_LONG).show();
            	} else if ( type.equalsIgnoreCase("CRISIS_ALERT") ) {
     
            	}
                /**
                 * TODO: Fetch here the crisis and store it to the local data structure (and/or cache)
                 */
            }
        });
	}

	@Override
	protected void onRegistered(Context _context, String _regID) {
		HttpClient httpclient = new DefaultHttpClient();
		try {
			try { Thread.sleep(2000); } catch (InterruptedException e) { e.printStackTrace(); }
			String authToken = SessionHandler.getInstance(null).getAuthenticationToken();
			HttpPost request = new HttpPost(HOST + "?auth_token="+authToken+"&reg_id="+_regID+"&device_name="+android.os.Build.MODEL.replace(" ", "+"));
			HttpResponse response = httpclient.execute(request);
			
			final String message;
			if ( response.getStatusLine().getStatusCode() == 201 ) message = "Successfully subscribed!";
			else {
				JSONObject json = new JSONObject(new BufferedReader(new InputStreamReader(response.getEntity().getContent())).readLine());
				message = json.getString("error");
			}
			this.mainThreadHandler.post(new Runnable() {
	            public void run() {
	                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
	                /**
	                 * TODO: Fetch here the crisis and store it to the local data structure (and/or cache)
	                 */
	            }
	        });
		} catch (AuthenticationErrorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
	}

	@Override
	protected void onUnregistered(Context _context, String _regID) {
		HttpClient httpclient = new DefaultHttpClient();
		try {
			try { Thread.sleep(2000); } catch (InterruptedException e) { e.printStackTrace(); }
			String authToken = SessionHandler.getInstance(null).getAuthenticationToken();
			HttpDelete request = new HttpDelete(HOST + "/"+_regID+"?auth_token="+authToken);
			httpclient.execute(request);
		} catch (AuthenticationErrorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			httpclient.getConnectionManager().shutdown();
		}
	}

}

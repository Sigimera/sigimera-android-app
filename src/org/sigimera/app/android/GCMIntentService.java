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
package org.sigimera.app.android;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Random;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.json.JSONException;
import org.json.JSONObject;
import org.sigimera.app.android.R;
import org.sigimera.app.android.backend.network.LocationUpdaterHttpHelper;
import org.sigimera.app.android.backend.network.MyHttpClient;
import org.sigimera.app.android.controller.ApplicationController;
import org.sigimera.app.android.controller.CrisesController;
import org.sigimera.app.android.controller.LocationController;
import org.sigimera.app.android.controller.SessionHandler;
import org.sigimera.app.android.exception.AuthenticationErrorException;
import org.sigimera.app.android.model.Constants;
import org.sigimera.app.android.model.Crisis;
import org.sigimera.app.android.util.Config;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.view.Gravity;
import android.widget.Toast;

import com.google.android.gcm.GCMBaseIntentService;
import com.google.android.gcm.GCMRegistrar;

/**
 * This Intent is called when the GCM executing process has finished.
 *
 * @author Alex Oberhauser
 * @email alex.oberhauser@sigimera.org
 */
public class GCMIntentService extends GCMBaseIntentService {
	private final Handler mainThreadHandler;

	public GCMIntentService() {
		super(GCMIntentService.class.getName());
		this.mainThreadHandler = new Handler();
	}

	@Override
	protected void onError(Context arg0, String arg1) {
		// TODO Auto-generated method stub
		System.err.println("Error occurred: " + arg1);
	}

	@Override
	protected void onMessage(Context _context, Intent _message) {
		final Intent msg = _message;
		this.mainThreadHandler.post(new Runnable() {
            public void run() {
            	ApplicationController controller = ApplicationController.getInstance();
            	controller.init(getApplicationContext(), getSharedPreferences(Constants.PREFS_NAME, 0), null);
                String authToken = controller.getSharedPreferences().getString("auth_token", null);
            	final String type = msg.getStringExtra("sig_message_type");
            	if ( type.equalsIgnoreCase("NEW_CRISIS") ) {
            		/**
            		 * XXX: Blocks UI: Shift this code into a separate background thread
            		 */
            		Crisis crisis = CrisesController.getInstance().getCrisis(authToken, msg.getStringExtra("crisis_id"));

        			StringBuffer message = new StringBuffer();
            		if ( crisis != null ) {
            			message.append(crisis.getID());
            			message.append(" was stored successfully!");
            		} else {
            			message.append("Not able to get crisis!");
            		}
        			Toast.makeText(getApplicationContext(), message.toString(), Toast.LENGTH_LONG).show();
            	} else if ( type.equalsIgnoreCase("PING") ) {
            		String ns = Context.NOTIFICATION_SERVICE;
            		NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);

            		Intent notificationIntent = new Intent();
            		PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(),
            		        0, notificationIntent,
            		        PendingIntent.FLAG_CANCEL_CURRENT);

            		Notification notification = new NotificationCompat.Builder(getApplicationContext())
            			.setContentTitle("Sigimera PING!")
            			.setContentText("Congratulations, push notifcation received!")
            			.setSmallIcon(R.drawable.sigimera_logo)
            			.setAutoCancel(true)
            			.setDefaults(Notification.DEFAULT_ALL)
            			.setContentIntent(contentIntent)
            			.build();

            		mNotificationManager.notify(Constants.PING_ID, notification);
            	} else if ( type.equalsIgnoreCase("CRISIS_ALERT") ) {
            		String ns = Context.NOTIFICATION_SERVICE;
            		NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);

            		/**
            		 * XXX: Not working with random ID. That makes always the latest notification clickable,
            		 * but not the older ones.
            		 */
            		int notificationID = new Random().nextInt();
            		Intent notificationIntent = new Intent(getApplicationContext(), CrisisAlertActivity.class);
            		notificationIntent.putExtra("notification_id", notificationID);
            		notificationIntent.putExtra("crisis_id", msg.getStringExtra("crisis_id"));
            		notificationIntent.putExtra("crisis_type", msg.getStringExtra("crisis_type"));
            		PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(),
            		        0, notificationIntent,
            		        PendingIntent.FLAG_CANCEL_CURRENT);

            		Notification notification = new NotificationCompat.Builder(getApplicationContext())
            		.setContentTitle("CRISIS ALERT!")
                    .setContentText("Crisis found: " + msg.getStringExtra("crisis_id"))
                    .setSmallIcon(R.drawable.alert_red)
                    .setOngoing(true)
                    .setAutoCancel(true)
            		.setDefaults(Notification.DEFAULT_ALL)
                    .setContentIntent(contentIntent)
            		.build();

            		mNotificationManager.notify(notificationID, notification);
            	} else if ( type.equalsIgnoreCase("SHARED_CRISIS") ) {
            		Intent intent = new Intent(GCMIntentService.this, CrisisActivity.class);
            		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            		intent.putExtra(Constants.CRISIS_ID, msg.getStringExtra("crisis_id"));
            		intent.putExtra(Constants.WINDOW_TYPE, Constants.WINDOW_TYPE_SHARED_CRISIS);
            		startActivity(intent);
            	} else if ( type.equalsIgnoreCase("UNREGISTER_DEVICE") ) {
            		GCMRegistrar.unregister(ApplicationController.getInstance().getApplicationContext());
            	} else if ( type.equalsIgnoreCase("REFRESH") ) {
            		LocationUpdaterHttpHelper locUpdater = new LocationUpdaterHttpHelper();
            		Location loc = LocationController.getInstance().getLastKnownLocation();
            		if ( loc != null ) {
	            		String latitude = loc.getLatitude() + "";
	            		String longitude = loc.getLongitude() + "";
	            		if ( authToken != null )
	            			locUpdater.execute(authToken, latitude, longitude);
            		} else {
            			// TODO: Notify the user that the update location flow has not worked.
            		}
            	}
            }
        });
	}

	@Override
	protected void onRegistered(Context _context, String _regID) {
		final String HOST = Config.getInstance().getAPIHost() + "/gcm";
        HttpClient httpclient = new MyHttpClient(ApplicationController.getInstance().getApplicationContext());
		try {
			try { Thread.sleep(1000); } catch (InterruptedException e) { e.printStackTrace(); }
			String authToken = SessionHandler.getInstance(null).getAuthenticationToken();
			HttpPost request = new HttpPost(HOST + "?auth_token="+authToken+"&reg_id="+_regID+"&device_name="+android.os.Build.MODEL.replace(" ", "+")+"&android_api_level="+android.os.Build.VERSION.SDK_INT);
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
		final String HOST = Config.getInstance().getAPIHost() + "/gcm";
        HttpClient httpclient = new MyHttpClient(ApplicationController.getInstance().getApplicationContext());
		try {
			String authToken = SessionHandler.getInstance(null).getAuthenticationToken();
			final String toastMessage;
			if ( _regID != null && !_regID.equals("") ) {
				HttpDelete request = new HttpDelete(HOST + "/"+_regID+"?auth_token="+authToken);
				httpclient.execute(request);
				toastMessage = "Successfully unregistered!";
			} else {
				toastMessage = "You are not registered to receive push notifications.";
			}
			this.mainThreadHandler.post(new Runnable() {
	            public void run() {
	    			Toast toast = Toast.makeText(getApplicationContext(), toastMessage, Toast.LENGTH_LONG);
	    			toast.setGravity(Gravity.TOP, 0, 0);
	    			toast.show();
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
		} finally {
			httpclient.getConnectionManager().shutdown();
		}
	}

}

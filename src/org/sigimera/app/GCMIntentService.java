package org.sigimera.app;


import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.sigimera.app.controller.SessionHandler;
import org.sigimera.app.exception.AuthenticationErrorException;
import org.sigimera.app.util.Config;

import android.content.Context;
import android.content.Intent;

import com.google.android.gcm.GCMBaseIntentService;

public class GCMIntentService extends GCMBaseIntentService {
	private static final String HOST = Config.getInstance().getAPIHost() + "/gcm";

	@Override
	protected void onError(Context arg0, String arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onMessage(Context _context, Intent _message) {
		// TODO Auto-generated method stub
		String message_type = _message.getStringExtra("message_type");
		System.out.println("Crisis ID received '" + _message.getStringExtra("crisis_id") + "' with message type '" + message_type + '!');
	}

	@Override
	protected void onRegistered(Context _context, String _regID) {
		try {
			try { Thread.sleep(2000); } catch (InterruptedException e) { e.printStackTrace(); }
			String authToken = SessionHandler.getInstance(null).getAuthenticationToken();
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost request = new HttpPost(HOST + "?auth_token="+authToken+"&reg_id="+_regID);
			HttpResponse result = httpclient.execute(request);
			System.out.println("Headers: " + result.getStatusLine().getStatusCode());
		} catch (AuthenticationErrorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	protected void onUnregistered(Context _context, String _regID) {
		try {
			try { Thread.sleep(2000); } catch (InterruptedException e) { e.printStackTrace(); }
			String authToken = SessionHandler.getInstance(null).getAuthenticationToken();
			HttpClient httpclient = new DefaultHttpClient();
			HttpDelete request = new HttpDelete(HOST + "/"+_regID+"?auth_token="+authToken);
			HttpResponse result = httpclient.execute(request);
			System.out.println("Headers: " + result.getStatusLine().getStatusCode());
		} catch (AuthenticationErrorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}

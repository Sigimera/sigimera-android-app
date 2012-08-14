package org.sigimera.app.controller;

import org.sigimera.app.Notification;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.google.android.gcm.GCMBaseIntentService;

public class GCMIntentService extends GCMBaseIntentService {

	@Override
	protected void onError(Context arg0, String arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onMessage(Context arg0, Intent arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onRegistered(Context arg0, String arg1) {
		// TODO Auto-generated method stub
		new Notification(arg0, arg1, Toast.LENGTH_LONG);
	}

	@Override
	protected void onUnregistered(Context arg0, String arg1) {
		// TODO Auto-generated method stub
		new Notification(arg0, arg1, Toast.LENGTH_LONG);

	}

}

package org.sigimera.app;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;

public class CrisisAlertActivity extends Activity {
	
	@Override
	public void onCreate(Bundle _savedInstanceState) {
		super.onCreate(_savedInstanceState);
		setContentView(R.layout.crisis_alert);
		/**
		 * TODO: Show here extended crisis alert information...
		 */
		String ns = Context.NOTIFICATION_SERVICE;
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);
		int notification_id = this.getIntent().getIntExtra("notification_id", -1);
		System.out.println(notification_id);
		mNotificationManager.cancel("CRISIS_ALERT", notification_id);
	}
}

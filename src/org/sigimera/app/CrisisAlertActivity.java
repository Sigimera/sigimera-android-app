package org.sigimera.app;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

/**
 * This window visualizes a crisis alert message and should be triggered by the
 * crisis alert notification received from the Sigimera Platform.
 * 
 * @author Alex Oberhauser
 *
 */
public class CrisisAlertActivity extends Activity {
	
	@Override
	public void onCreate(Bundle _savedInstanceState) {
		super.onCreate(_savedInstanceState);
		setContentView(R.layout.crisis_alert);

		String ns = Context.NOTIFICATION_SERVICE;
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);
		int notification_id = getIntent().getIntExtra("notification_id", -1);
		String crisisID = getIntent().getStringExtra("crisis_id");
		mNotificationManager.cancel("CRISIS_ALERT", notification_id);
		
		/**
		 * TODO: Show here extended crisis alert information...
		 */
		
		TextView bottomBoxTitle = (TextView) findViewById(R.id.bottom_box_title);
		bottomBoxTitle.setText("Crisis Alert");
		
		TextView bottomBoxSummary = (TextView) findViewById(R.id.bottom_box_summary);
		bottomBoxSummary.setText("The following crisis occurred near your current (or specified) location: " + crisisID);
		
	}
}

package org.sigimera.app;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.widget.ImageView;
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
		String crisisType = getIntent().getStringExtra("crisis_type");
		mNotificationManager.cancel("CRISIS_ALERT", notification_id);
		
		/**
		 * TODO: Show here extended crisis alert information...
		 */
		
		ImageView typeImage = (ImageView) findViewById(R.id.alert_type_icon);
		if ( "EARTHQUAKE".equalsIgnoreCase(crisisType) )
			typeImage.setImageResource(R.drawable.earthquake);
		else if ( "CYCLONE".equalsIgnoreCase(crisisType) )
			typeImage.setImageResource(R.drawable.cyclone);
		else if ( "FLOOD".equalsIgnoreCase(crisisType) )
			typeImage.setImageResource(R.drawable.flood);
		else if ( "VOLCANO".equalsIgnoreCase(crisisType) )
			typeImage.setImageResource(R.drawable.volcano);
		
		TextView bottomBoxTitle = (TextView) findViewById(R.id.bottom_box_title);
		bottomBoxTitle.setText("Crisis Alert");
		
		TextView bottomBoxSummary = (TextView) findViewById(R.id.bottom_box_summary);
		bottomBoxSummary.setText("Crisis alarm near your current (or specified) location.\n see http://www.sigimera.org/crises/" + crisisID);
	}
}

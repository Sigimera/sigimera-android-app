/**
 * Sigimera Crises Information Platform Android Client
 * Copyright (C) 2013 by Sigimera
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

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * This window visualises a crisis alert message and should be triggered by the
 * crisis alert notification received from the Sigimera Platform.
 * 
 * @author Alex Oberhauser
 * 
 */
public class CrisisAlertActivity extends Activity {

	@Override
	public final void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.crisis_alert);

		String ns = Context.NOTIFICATION_SERVICE;
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);
		int notificationId = getIntent().getIntExtra("notification_id", -1);
		String crisisID = getIntent().getStringExtra("crisis_id");
		String crisisType = getIntent().getStringExtra("crisis_type");
		mNotificationManager.cancel("CRISIS_ALERT", notificationId);

		/**
		 * TODO: Show here extended crisis alert information...
		 */

		ImageView typeImage = (ImageView) findViewById(R.id.alert_type_icon);
		if ("EARTHQUAKE".equalsIgnoreCase(crisisType)) {
			typeImage.setImageResource(R.drawable.earthquake);
		} else if ("CYCLONE".equalsIgnoreCase(crisisType)) {
			typeImage.setImageResource(R.drawable.cyclone);
		} else if ("FLOOD".equalsIgnoreCase(crisisType)) {
			typeImage.setImageResource(R.drawable.flood);
		} else if ("VOLCANO".equalsIgnoreCase(crisisType)) {
			typeImage.setImageResource(R.drawable.volcano);
		}

		TextView bottomBoxTitle = (TextView) findViewById(R.id.bottom_box_title);
		bottomBoxTitle.setText("Crisis Alert");

		TextView bottomBoxSummary = (TextView) findViewById(R.id.bottom_box_summary);
		bottomBoxSummary
				.setText("Crisis alarm near your current(or specified) location"
						+ ".\n "
						+ "see http://www.sigimera.org/crises/"
						+ crisisID);
	}
}

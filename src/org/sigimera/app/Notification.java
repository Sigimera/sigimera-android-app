package org.sigimera.app;

import android.content.Context;
import android.widget.Toast;

public class Notification extends Toast {

	public Notification(Context context, String message, int duration) {
		super(context);
		Toast.makeText(context, message, duration).show();
	}	
}

package org.sigimera.app;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;
/**
 * Show a toast message.
 * 
 * @author Corneliu-Valentin Stanciu
 */
public class Notification extends Toast {

	/**
	 * Show a new notification as toast message.
	 * 
	 * @param context The application context ()
	 * @param message The message that should be displayed
	 * @param duration The message Toast.LENGTH_LONG or Toast.LENGTH_SHORT 
	 */
	public Notification(Context context, String message, int duration) {
		super(context);
		Toast toast = Toast.makeText(context, message, duration);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
	}	
}

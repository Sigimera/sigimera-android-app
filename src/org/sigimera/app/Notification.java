package org.sigimera.app;

import android.content.Context;
import android.widget.Toast;
/**
 * Show a toast message.
 * 
 * @author Corneliu-Valentin Stanciu
 */
public class Notification extends Toast {

	/**
	 * Create a new notification as toast message.
	 * 
	 * @param context The application context ()
	 * @param message The message that should be displayed
	 * @param duration The message Toast.LENGTH_LONG or Toast.LENGTH_SHORT 
	 */
	public Notification(Context context, String message, int duration) {
		super(context);
		Toast.makeText(context, message, duration).show();
	}	
}

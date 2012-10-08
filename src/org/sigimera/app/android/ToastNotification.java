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

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

/**
 * Show a toast message.
 * 
 * @author Corneliu-Valentin Stanciu
 * @email  corneliu.stanciu@sigimera.org
 */
public class ToastNotification extends Toast {

	/**
	 * Show a new notification as toast message.
	 * 
	 * @param context The application context ()
	 * @param message The message that should be displayed
	 * @param duration The message Toast.LENGTH_LONG or Toast.LENGTH_SHORT 
	 */
	public ToastNotification(Context context, String message, int duration) {
		super(context);
		Toast toast = Toast.makeText(context, message, duration);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
	}
}

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

import org.sigimera.app.android.controller.ApplicationController;
import org.sigimera.app.android.util.Common;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * 
 * @author Corneliu-Valentin Stanciu
 * @e-mail corneliu.stanciu@sigimera.org
 */
public class OfflineMode extends BroadcastReceiver {

	@Override
	public final void onReceive(final Context context, final Intent intent) {
		ApplicationController appController = ApplicationController
				.getInstance();
		if (appController.getActionbar() != null) {
			if (Common.hasInternet()) {
				appController.getActionbar().setIcon(
						context.getResources().getDrawable(
								R.drawable.sigimera_logo));
			} else {
				appController.getActionbar().setIcon(
						context.getResources().getDrawable(
								R.drawable.sigimera_logo_offline));
			}
		}
	}
}

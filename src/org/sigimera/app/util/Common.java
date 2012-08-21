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
package org.sigimera.app.util;

import org.sigimera.app.R;
import org.sigimera.app.model.Constants;

import android.content.Intent;

/**
 * Class which sum all the methods used over different windows.
 * 
 * @author Corneliu-Valentin Stanciu
 * @email  corneliu.stanciu@sigimera.org
 */
public class Common {
	private static final String CRISIS_URL = "http://www.sigimera.org/crises/";

	/**
	 * Share the crisis with your friend and/or the world.
	 */
	public static Intent shareCrisis(String crisisID) {
		Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
		shareIntent.setType("text/plain");
		shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, CRISIS_URL + crisisID);
		return shareIntent;
	}
	
	/**
	 * Get the crisis icon based on the type of crisis (e.g. earthquake)
	 * @param subject
	 * @return
	 */
	public static int getCrisisIcon(String subject) {
		if (subject.contains(Constants.FLOOD))
			return R.drawable.flood;
		else if (subject.contains(Constants.EARTHQUAKE))
			return R.drawable.earthquake;
		else if (subject.contains(Constants.CYCLONE))
			return R.drawable.cyclone;
		else if (subject.contains(Constants.VOLCANO))
			return R.drawable.volcano;
		return 0;
	}
}

package org.sigimera.app.controller;

import android.content.Intent;

/**
 * Class which sum all the methods used over different windows.
 * 
 * @author Corneliu-Valentin Stanciu
 *
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
}

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
package org.sigimera.app.android.model;

/**
 * Class which includes all the constants used in the Sigimera App.
 * 
 * @author Corneliu-Valentin Stanciu
 * @email  corneliu.stanciu@sigimera.org
 */
public abstract class Constants {
	
	/**
	 * LOGs
	 */
	public static final String LOG_TAG_SIGIMERA_APP = "SigimeraApp";
	public static final String LOG_TAG_LOCATION_CONTROLLER = "LocationController";
	public static final String LOG_TAG_CONFIG = "Config";
	
	/**
	 * Location radius in kilometres
	 */
	public static final int LOCATION_RADIUS = 200;
	
	public static final double MAX_DISTANCE_NEAR_CRISIS = 5000;
	public static final double MIN_DISTANCE_NEAR_CRISIS = 50;
	
	
	/**
	 * Keys
	 */
	public static final String CRISES_LIST = "crises_list";	
	public static final String CRISIS = "crisis";
	public static final String CRISIS_ID = "crisis_id";
	public static final String LATITUDE = "latitude";
	public static final String LONGITUDE = "longitude";
	public static final String CRISIS_TYPE = "crisis_type";
	public static final String TOP = "top";
	public static final String BOTTOM = "bottom";	
	public static final String ICON = "icon";
	public static final String ARROW = "rightArrow";
	public static final String WINDOW_TYPE = "window_type"; 
	
	/**
	 * Crisis types
	 */
	public static final String EARTHQUAKE = "earthquake";
	public static final String FLOOD = "flood";
	public static final String CYCLONE = "cyclone";
	public static final String VOLCANO = "volcano";
	
	/**
	 * Style handling
	 */
	public static final String LATEST_CRISIS = "LATEST_CRISIS";
	public static final String NEAR_CRISIS = "NEAR_CRISIS";
	
	/**
	 * Window types
	 */
	public static final String WINDOW_TYPE_SHARED_CRISIS = "shared_crisis";
	
	/**
	 * Notification Identifier
	 */
	public static final int PING_ID = 12627;
	
	/**
	 * Preferences
	 */
	public static final String PREFS_NAME = "session_handler_preferences";
		
}

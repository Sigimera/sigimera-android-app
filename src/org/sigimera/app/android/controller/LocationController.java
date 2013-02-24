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
package org.sigimera.app.android.controller;

import org.sigimera.app.android.exception.AuthenticationErrorException;
import org.sigimera.app.android.model.Constants;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

/**
 * Handle the location found via network or GPS.
 * @author Corneliu-Valentin Stanciu
 * @e-mail  corneliu.stanciu@sigimera.org
 */
public class LocationController {	
	
	private static LocationController instance = null;

	private LocationManager locationManager = null;
	
	/**
	 * Singleton pattern
	 */
	public static LocationController getInstance() {
		if ( instance == null ) instance = new LocationController();
		return instance;
	}	
	
	private LocationController() {
		// Acquire a reference to the system Location Manager
		Context context = ApplicationController.getInstance().getApplicationContext();
		if ( context != null )
			locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		else
			Log.e(Constants.LOG_TAG_LOCATION_CONTROLLER, "The context is null! We cannot update the location");
		
		// Define a listener that responds to location updates
		LocationListener locationListener = new LocationListener() {
		    public void onLocationChanged(Location location) {
		    	// Called when a new location is found by the network location provider.
		    	Log.i(Constants.LOG_TAG_LOCATION_CONTROLLER, "Found new location: " + location);
		    	
		    	if ( ApplicationController.getInstance().isEverythingUpdated() ) {
					try {
						final String auth_token = ApplicationController.getInstance().getSessionHandler().getAuthenticationToken();
						PersistanceController.getInstance().updateNearCrises(auth_token, 1, location);
					} catch (AuthenticationErrorException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		    	}
		    }

		    public void onProviderEnabled(String provider) {}

		    public void onProviderDisabled(String provider) {}

			@Override
			public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
				// TODO Auto-generated method stub			
			}
		  };

		// Register the listener with the Location Manager to receive location updates
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 5000, locationListener);
	}
	
	/**
	 * Get the last known location saved via network
	 * @return location Includes GPS coordinates, the time when was found, accuracy, etc
	 */
	public Location getLastKnownLocation() {
		return locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
	}
	
}

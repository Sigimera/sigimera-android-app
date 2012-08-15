package org.sigimera.app.controller;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;

/**
 * Handle the location found via network or GPS. 
 * 
 * @author Corneliu-Valentin Stanciu
 *
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
		locationManager = (LocationManager) ApplicationController.getInstance()
				.getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
		
		/**
		 *  TODO: Maybe in a future step we will react on location updates.
		 */		
		// Define a listener that responds to location updates
//		LocationListener locationListener = new LocationListener() {
//		    public void onLocationChanged(Location location) {
//		      // Called when a new location is found by the network location provider.
//		    	Log.i(SigimeraConstants.LOG_TAG_LOCATION_CONTROLLER, "Found new location: " + location);
//		    }
//
//		    public void onProviderEnabled(String provider) {}
//
//		    public void onProviderDisabled(String provider) {}
//
//			@Override
//			public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
//				// TODO Auto-generated method stub			
//			}
//		  };
//
//		// Register the listener with the Location Manager to receive location updates
//		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 100, locationListener);
	}
	
	/**
	 * Get the last known location saved via network
	 * @return location Includes GPS coordinates, the time when was found, accuracy, etc
	 */
	public Location getLastKnownLocation() {
		return locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
	}
	
}

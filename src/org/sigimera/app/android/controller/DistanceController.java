package org.sigimera.app.android.controller;

import java.math.BigDecimal;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.sigimera.app.android.exception.AuthenticationErrorException;
import org.sigimera.app.android.model.Constants;

import android.location.Location;

public class DistanceController {

	private static String auth_token;
	private static String nearCrisisID = null;
	private static int decimalPlaces = 2;
	private static double smallestDistance = Constants.MAX_DISTANCE_NEAR_CRISIS;

	private static DistanceController instance = null;

	/**
	 * Singleton pattern
	 */
	public static DistanceController getInstance() {
		if (instance == null)
			instance = new DistanceController();
		return instance;
	}

	private DistanceController() {
	}
	
	public static String getNearCrisis() {
		return nearCrisisID;
	}

	/**
	 * Retrieve the distance to the next near crisis and set the crisisID of it
	 * in ApplicationController.
	 * 
	 * @return
	 */
	public static double getNearCrisisDistance() {
		try {
			auth_token = ApplicationController.getInstance()
					.getSessionHandler().getAuthenticationToken();
		} catch (AuthenticationErrorException e) {
			// TODO: go to Login Window
		}
		Location userLocation = LocationController.getInstance().getLastKnownLocation();

		JSONArray retArray = CrisesController.getInstance().getNearCrises(auth_token, 1, userLocation);
		if ( retArray != null ) {
			for (int count = 0; count < retArray.length(); count++) {
				try {
					JSONObject crisisJSON = (JSONObject) retArray.get(count);
					double latitude = (Double) crisisJSON.getJSONArray("foaf_based_near").get(1);
					double longitude = (Double) crisisJSON.getJSONArray("foaf_based_near").get(0);
	
					double tmpDistance = computeDistance(
							userLocation.getLatitude(),
							userLocation.getLongitude(), latitude, longitude);
					if (tmpDistance < smallestDistance) {
						smallestDistance = tmpDistance;
						nearCrisisID = crisisJSON.getString("_id");
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		System.out.println(smallestDistance);
		return smallestDistance;
	}

	/**
	 * Compute the distance between two GPS locations based on Vincenty Formula.
	 * 
	 * @param lat1
	 * @param long1
	 * @param lat2
	 * @param long2
	 * @return
	 */
	private static double computeDistance(double lat1, double long1,
			double lat2, double long2) {
		double earthRadius = 6371;
		double dLatBot = Math.toRadians(lat2 - lat1);
		double dLonBot = Math.toRadians(long2 - long1);
		double a = Math.sin(dLatBot / 2) * Math.sin(dLatBot / 2)
				+ Math.cos(Math.toRadians(lat1))
				* Math.cos(Math.toRadians(lat2)) * Math.sin(dLonBot / 2)
				* Math.sin(dLonBot / 2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		double distance = earthRadius * c;

		BigDecimal bigD = new BigDecimal(distance);
		bigD = bigD.setScale(decimalPlaces, BigDecimal.ROUND_HALF_UP);

		return bigD.doubleValue();
	}
}

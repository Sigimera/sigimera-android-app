package org.sigimera.app.android.controller;

import java.math.BigDecimal;

import org.sigimera.app.android.model.Crisis;

import android.location.Location;

public abstract class DistanceController {
	private static int decimalPlaces = 2;

	/**
	 * 
	 * @param _nearCrisis
	 * @param _userLocation
	 * @return
	 */
	public static double getNearCrisisDistance(Crisis _nearCrisis, Location _userLocation) {
		if ( _nearCrisis != null && _userLocation != null ) {
			return computeDistance(
					_userLocation.getLatitude(), _userLocation.getLongitude(), 
					_nearCrisis.getLatitude(), _nearCrisis.getLongitude());
		}
		return -1;
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
	public static double computeDistance(double lat1, double long1,
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

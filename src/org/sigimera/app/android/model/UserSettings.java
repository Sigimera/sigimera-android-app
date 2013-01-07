package org.sigimera.app.android.model;

public class UserSettings extends User{
	public int radius;
	public double latitude;
	public double longitude;
	
	public UserSettings(){
		super();
	}
	
	/**
	 * Setter
	 */
	public void setRadius(int radius) {
		this.radius = radius;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	
	/**
	 * Getter
	 */
	public int getRadius() {
		return radius;
	}
	public double getLatitude() {
		return latitude;
	}
	public double getLongitude() {
		return longitude;
	}
}

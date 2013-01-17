package org.sigimera.app.android.model;

import java.io.Serializable;

public class UsersStats extends User implements Serializable {	
	private static final long serialVersionUID = -2526641247855691670L;
		
	private int postedComments;
	private int uploadedImages;
	private int reportedLocations;
	private int reportedMissingPeople;	
	private int radius;
	private double latitude;
	private double longitude;
		
	/**
	 * Setter
	 */
	public void setPostedComments(int postedComments) {
		this.postedComments = postedComments;
	}
	public void setUploadedImages(int uploadedImages) {
		this.uploadedImages = uploadedImages;
	}
	public void setReportedLocations(int reportedLocations) {
		this.reportedLocations = reportedLocations;
	}
	public void setReportedMissingPeople(int reportedMissingPeople) {
		this.reportedMissingPeople = reportedMissingPeople;
	}
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
	public int getPostedComments() {
		return postedComments;
	}
	public int getUploadedImages() {
		return uploadedImages;
	}
	public int getReportedLocations() {
		return reportedLocations;
	}
	public int getReportedMissingPeople() {
		return reportedMissingPeople;
	}
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

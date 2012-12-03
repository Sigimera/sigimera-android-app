package org.sigimera.app.android.model;

import java.io.Serializable;

public class UsersStats implements Serializable {	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2526641247855691670L;
	
	private String name;
	private String username;
	private int postedComments;
	private int uploadedImages;
	private int reportedLocations;
	private int reportedMissingPeople;
	
	public String getName() {
		return name;
	}
	public String getUsername() {
		return username;
	}
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
	public void setName(String name) {
		this.name = name;
	}
	public void setUsername(String username) {
		this.username = username;
	}	
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
}

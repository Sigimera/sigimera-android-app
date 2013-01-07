package org.sigimera.app.android.model;

import java.io.Serializable;

public class UsersStats extends User implements Serializable {	
	private static final long serialVersionUID = -2526641247855691670L;
		
	public int postedComments;
	public int uploadedImages;
	public int reportedLocations;
	public int reportedMissingPeople;	
		
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
}

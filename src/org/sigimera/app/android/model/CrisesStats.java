package org.sigimera.app.android.model;

import java.io.Serializable;

public class CrisesStats implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -9109609309346239337L;
	
	private String id;
	private String firstCrisisAt;
	private String latestCrisisAt;
	private int totalCrises;
	private int numberOfEarthquakes;
	private int numberOfFloods;
	private int numberOfCyclones;
	private int numberOfVolcanoes;
	private int uploadedImages;
	private int postedComments;
	private int reportedLocations;
	private int reportedMissingPeople;
	public int getReportedMissingPeople() {
		return reportedMissingPeople;
	}
	public void setReportedMissingPeople(int reportedMissingPeople) {
		this.reportedMissingPeople = reportedMissingPeople;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getFirstCrisisAt() {
		return firstCrisisAt;
	}
	public void setFirstCrisisAt(String firstCrisisAt) {
		this.firstCrisisAt = firstCrisisAt;
	}
	public String getLatestCrisisAt() {
		return latestCrisisAt;
	}
	public void setLatestCrisisAt(String latestCrisisAt) {
		this.latestCrisisAt = latestCrisisAt;
	}
	public int getTotalCrises() {
		return totalCrises;
	}
	public void setTotalCrises(int totalCrises) {
		this.totalCrises = totalCrises;
	}
	public int getNumberOfEarthquakes() {
		return numberOfEarthquakes;
	}
	public void setNumberOfEarthquakes(int numberOfEarthquakes) {
		this.numberOfEarthquakes = numberOfEarthquakes;
	}
	public int getNumberOfFloods() {
		return numberOfFloods;
	}
	public void setNumberOfFloods(int numberOfFloods) {
		this.numberOfFloods = numberOfFloods;
	}
	public int getNumberOfCyclones() {
		return numberOfCyclones;
	}
	public void setNumberOfCyclones(int numberOfCyclones) {
		this.numberOfCyclones = numberOfCyclones;
	}
	public int getNumberOfVolcanoes() {
		return numberOfVolcanoes;
	}
	public void setNumberOfVolcanoes(int numberOfVolcanoes) {
		this.numberOfVolcanoes = numberOfVolcanoes;
	}
	public int getUploadedImages() {
		return uploadedImages;
	}
	public void setUploadedImages(int uploadedImages) {
		this.uploadedImages = uploadedImages;
	}
	public int getPostedComments() {
		return postedComments;
	}
	public void setPostedComments(int postedComments) {
		this.postedComments = postedComments;
	}
	public int getReportedLocations() {
		return reportedLocations;
	}
	public void setReportedLocations(int reportedLocations) {
		this.reportedLocations = reportedLocations;
	}
}

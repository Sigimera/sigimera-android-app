package org.sigimera.app.model;

import java.io.Serializable;
import java.util.ArrayList;

public class Crisis implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5271696827884512361L;
	
	private String id;
	private String title;
	private String description;
	private double latitude;
	private double longitude;
	private String subject;
	private String alertLevel;
	private String severity;
	private String population;
	private String vulnerability;
	private String date;
	private String startDate;
	private String endDate;
	private String shortTitle;
	private String typeIcon;
	private ArrayList<String> countries;
	
	public String getID() {
		return id;
	}
	public void setID(String _id) {
		this.id = _id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getAlertLevel() {
		return alertLevel;
	}
	public void setAlertLevel(String alertLevel) {
		this.alertLevel = alertLevel;
	}
	public String getSeverity() {
		return severity;
	}
	public void setSeverity(String severity) {
		this.severity = severity;
	}
	public String getPopulation() {
		return population;
	}
	public void setPopulation(String population) {
		this.population = population;
	}
	public String getVulnerability() {
		return vulnerability;
	}
	public void setVulnerability(String vulnerability) {
		this.vulnerability = vulnerability;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	public String getShortTitle() {
		return shortTitle;
	}
	public void setShortTitle(String shortTitle) {
		this.shortTitle = shortTitle;
	}
	public String getTypeIcon() {
		return typeIcon;
	}
	public void setTypeIcon(String typeIcon) {
		this.typeIcon = typeIcon;
	}
	
	public ArrayList<String> getCountries() {
		return countries;
	}
	
	public void setCountries(ArrayList<String> _countries) {
		this.countries = _countries;
	}
	
}

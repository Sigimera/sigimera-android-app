package org.sigimera.app.android.model;

public class User {
	public String id;
	public String name;
	public String username;

	public User() {}
	
	/**
	 * Setter
	 */	
	public void setName(String name) {
		this.name = name;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	
	/**
	 * Getter
	 */
	public String getName() {
		return name;
	}
	public String getUsername() {
		return username;
	}
}

package org.sigimera.app.android.model;

public class User {
	public String id;
	public String name;
	public String username;
	public String email;

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
	public void setEmail(String email) {
		this.email = email;
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
	public String getEmail() {
		return email;
	}
}

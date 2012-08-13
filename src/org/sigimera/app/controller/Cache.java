package org.sigimera.app.controller;

import java.util.Date;
import java.util.Vector;

public class Cache {
	private static Cache instance = null;
	
	private Cache() {}
	
	public static Cache getIntance() {
		if ( null == instance )
			instance = new Cache();
		return instance;
	}
	
	public boolean addCrisis(String _crisisAsJson) {
		return false;
	}
	
	public Vector<String> getLatestXCrises(int _number) {
		Vector<String> crisesList = new Vector<String>();
		
		return crisesList;
	}
	
	public int totalCachedCrises() { return -1; }
	public Date lastUpdate() { return null; }
}

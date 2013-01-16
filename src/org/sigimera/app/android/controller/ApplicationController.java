/**
 * Sigimera Crises Information Platform Android Client
 * Copyright (C) 2012 by Sigimera
 * All Rights Reserved
 * 
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 2 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package org.sigimera.app.android.controller;

import org.sigimera.app.android.backend.PersistentStorage2;

import android.app.ActionBar;
import android.content.Context;
import android.content.SharedPreferences;

public class ApplicationController {
	public static ApplicationController instance = null;
	
	private SessionHandler sessionHandler;
	
	private Context context;
	private SharedPreferences settings;
	private PersistentStorage2 pershandler;
	private ActionBar actionBar;
	
	private boolean updatingEverything = false;
	
	private ApplicationController() {}
	
	public static ApplicationController getInstance() {
		if ( null == instance )
			instance = new ApplicationController();
		return instance;
	}
	
	public void init(Context _context, SharedPreferences _settings, ActionBar _actionBar) {
		this.context = _context;
		this.settings = _settings;
		this.pershandler = PersistentStorage2.getInstance();
		this.sessionHandler = SessionHandler.getInstance(this.settings);
		this.actionBar = _actionBar;
	}
	
	public void setApplicationContext(Context _context) {
		this.context = _context;
	}
	
	public void setSharedPreferences(SharedPreferences _settings) {
		this.settings = _settings;
	}
	
	public void setActionbar(ActionBar _actionBar) {
		this.actionBar = _actionBar;				
	}
	
	public Context getApplicationContext() { return this.context; }
	public PersistentStorage2 getPersistentStorageHandler() { return this.pershandler; }
	public ActionBar getActionbar() { return this.actionBar; }
	
	public SessionHandler getSessionHandler() { return this.sessionHandler; }
	
	public SharedPreferences getSharedPreferences() { return this.settings; }
	public SharedPreferences getSharedPreferences(String preferenceName) {
    	return context.getSharedPreferences(preferenceName, 0);    	
    }
		
	public synchronized boolean isEverythingUpdated() {
		return updatingEverything;
	}	
	
	public synchronized void setEverythingUpdated(boolean _updatingEverything) {
		updatingEverything = _updatingEverything;
	}
}

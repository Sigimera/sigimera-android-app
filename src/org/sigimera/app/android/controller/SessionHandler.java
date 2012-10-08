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

import java.util.concurrent.ExecutionException;

import org.sigimera.app.android.exception.AuthenticationErrorException;
import org.sigimera.app.android.helper.LoginHttpHelper;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;

public class SessionHandler {
	private static SessionHandler instance = null;
	private SharedPreferences settings;
	
	private SessionHandler() {
		this.settings = ApplicationController.getInstance().getSharedPreferences();
	}
	
	public static SessionHandler getInstance(SharedPreferences _settings) {
		if ( null == instance )
			instance = new SessionHandler();
		return instance;
	}
	
	public boolean login(String _email, String _password) {	
		AsyncTask<String, Void, Boolean> login = new LoginHttpHelper().execute(_email, _password);
		try {
			return login.get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean logout() {
		Editor editor = this.settings.edit();
		editor.clear();
		editor.commit();
		return true;
	}
	
	public String getAuthenticationToken() throws AuthenticationErrorException {
		String auth_token = this.settings.getString("auth_token", null);
		if ( null == auth_token )
			throw new AuthenticationErrorException();
		else
			return auth_token;
	}
}

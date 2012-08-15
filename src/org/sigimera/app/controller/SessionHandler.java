package org.sigimera.app.controller;

import java.util.concurrent.ExecutionException;

import org.sigimera.app.exception.AuthenticationErrorException;
import org.sigimera.app.fragement.LoginHttpHelper;

import android.content.SharedPreferences;
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
	
	public String getAuthenticationToken() throws AuthenticationErrorException {
		String auth_token = this.settings.getString("auth_token", null);
		if ( null == auth_token )
			throw new AuthenticationErrorException();
		else
			return auth_token;
	}
}

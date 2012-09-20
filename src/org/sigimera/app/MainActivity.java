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
package org.sigimera.app;

import org.sigimera.app.CrisesListFragment.CrisesListListener;
import org.sigimera.app.LoginFragment.LoginListener;
import org.sigimera.app.controller.ApplicationController;
import org.sigimera.app.controller.CrisesController;
import org.sigimera.app.controller.SessionHandler;
import org.sigimera.app.exception.AuthenticationErrorException;
import org.sigimera.app.model.Constants;
import org.sigimera.app.util.Config;

import com.google.android.gcm.GCMRegistrar;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

/**
 * @author Corneliu-Valentin Stanciu, Alex Oberhauser
 * @email  corneliu.stanciu@sigimera.org, alex.oberhauser@sigimera.org
 */
public class MainActivity extends FragmentActivity implements LoginListener, CrisesListListener {
	private SessionHandler session_handler;
	private PageAdapter pageAdapter;

	private Fragment fragmentPageOne;
	private Fragment fragmentPageTwo;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		ApplicationController appController = ApplicationController.getInstance();
		appController.init(getApplicationContext(), getSessionSettings());

		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		if (activeNetwork != null && activeNetwork.isConnected()) {

			this.session_handler = appController.getSessionHandler();
			CrisesController.getInstance();

			try {
				this.session_handler.getAuthenticationToken();
				
				/**
				 * BEGIN: Google Cloud Messaging
				 */
				if ( Config.getInstance().getGcmProjectId() != null ) {
					try {
						GCMRegistrar.checkDevice(this); GCMRegistrar.checkManifest(this);
						final String regId = GCMRegistrar.getRegistrationId(this);
						if (regId.equals("")) GCMRegistrar.register(this, Config.getInstance().getGcmProjectId());
//						else GCMRegistrar.unregister(this);
					} catch (Exception e) {
						Log.v(Constants.LOG_TAG_SIGIMERA_APP, "Device meets not the GCM requirements. Exception: " + e);
					}
				}
				/**
				 * END: Google Cloud Messaging
				 */

				String windowType = getIntent().getStringExtra(Constants.WINDOW_TYPE);
				// Passing the crises list and first crisis to the fragments
				
				String[] titles = { "Last Crises", "Crisis Info" };
				
				fragmentPageOne = new CrisesListFragment();										
				fragmentPageTwo = new CrisisFragement();
				
				String crisisID = null;
				int currentPage = 0;
								
				if ( windowType != null && windowType.equalsIgnoreCase(Constants.WINDOW_TYPE_SHARED_CRISIS)) {
					crisisID = getIntent().getStringExtra(Constants.CRISES_ID);
					currentPage = 1;
				}
				
				fragmentPageTwo.setArguments(forwardFragmentProperty(
						new Bundle(), Constants.CRISIS, crisisID));
				newDoubleWindow(titles, fragmentPageOne, fragmentPageTwo, currentPage);
			} catch (AuthenticationErrorException e) {
				String[] titles = { "Login", "Last 10 crises" };

				// If no token -> login + crises list (free)
				fragmentPageOne = new LoginFragment();
				fragmentPageTwo = new CrisesListFragment();			
				newDoubleWindow(titles, fragmentPageOne, fragmentPageTwo, 0);
			}
		} else {
			new ToastNotification(getApplicationContext(), "No internet connection", Toast.LENGTH_LONG);
		}
	}

	public void onLoginClicked() {		
		EditText emailView = (EditText) findViewById(R.id.email_input_field);
		EditText passwordView = (EditText) findViewById(R.id.password_input_field);

		if (session_handler.login(emailView.getText().toString(), passwordView.getText().toString())) {
			fragmentPageOne = new CrisesListFragment();		;
			
			fragmentPageTwo = new CrisisFragement();
			fragmentPageTwo.setArguments(forwardFragmentProperty(
					new Bundle(), Constants.CRISIS, null));			
			
			String[] titles = {"Last Crises", "Crisis Info"}; 
			newDoubleWindow(titles, fragmentPageOne, fragmentPageTwo, 0);
		} else {
			new ToastNotification(getApplicationContext(), "Email or password were incorrect!", Toast.LENGTH_SHORT);
		}
	}

	public void onCrisesListItemClicked(String crisisID) {					
			String[] titles = { "Last Crises", "Crisis Info" };
			
			// Instantiate a new crisis info fragment and set the crisis JSONObject in bundle					
			fragmentPageTwo = new CrisisFragement();	
			fragmentPageTwo.setArguments(forwardFragmentProperty(
					new Bundle(), Constants.CRISIS, crisisID));
			newDoubleWindow(titles, fragmentPageOne, fragmentPageTwo, 1);
	}
	
	private void newDoubleWindow(String[] titles, Fragment pageOne, Fragment pageTwo, int currentItem) {
		ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
		this.pageAdapter = new PageAdapter(getSupportFragmentManager(),titles , pageOne, pageTwo);
		viewPager.setAdapter(this.pageAdapter);
		this.fragmentPageOne = pageOne;
		this.fragmentPageTwo = pageTwo;
		viewPager.setCurrentItem(currentItem, true);
	}
	
	private Bundle forwardFragmentProperty(Bundle bundle, String key, String value) {
		bundle.putString(key, value);
		return bundle;
	}
	
	public SharedPreferences getSessionSettings() {
		String PREFS_NAME = "session_handler_preferences";
		return getSharedPreferences(PREFS_NAME, 0);
	}
}

/**
 * Sigimera Crises Information Platform Android Client
 * Copyright (C) 2013 by Sigimera
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
package org.sigimera.app.android;

import org.sigimera.app.android.backend.network.LocationUpdaterHttpHelper;
import org.sigimera.app.android.controller.ApplicationController;
import org.sigimera.app.android.controller.LocationController;
import org.sigimera.app.android.controller.PersistanceController;
import org.sigimera.app.android.exception.AuthenticationErrorException;
import org.sigimera.app.android.model.Constants;
import org.sigimera.app.android.util.Common;
import org.sigimera.app.android.util.Config;

import com.google.android.gcm.GCMRegistrar;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.TabHost;
import android.widget.Toast;

/**
 * Activity which initialise the tabs and starts the application.
 * 
 * @author Corneliu-Valentin Stanciu
 * @e-mail corneliu.stanciu@sigimera.org
 */
public class MainActivity extends FragmentActivity {

	/**
	 * Authentication token.
	 */
	private String authToken;

	/**
	 * Hosting the tabs.
	 */
	private TabHost mTabHost;

	/**
	 * View pager used for scrolling to the left and right.
	 */
	private ViewPager mViewPager;

	/**
	 * The main container of tabs and view pager.
	 */
	private TabsAdapter mTabsAdapter;

	/**
	 * Progress dialog for waiting while loading.
	 */
	private ProgressDialog progressDialog = null;

	/**
	 * 
	 */
	private final Handler guiHandler = new Handler();

	/**
	 * Thread which calls the login error method.
	 */
	private final Runnable errorLogin = new Runnable() {
		@Override
		public void run() {
			showLoginErrorToast();
		}
	};

	/**
	 * Thread which calls the method for setting the tabs if the login was
	 * successfully or there exists an authentication token.
	 */
	private final Runnable successfulLogin = new Runnable() {
		@Override
		public void run() {
			setTabsAfterLogin();
		}
	};

	@Override
	protected final void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_main);
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		ApplicationController appController = ApplicationController
				.getInstance();

		int currentapiVersion = Build.VERSION.SDK_INT;
		if (currentapiVersion >= android.os.Build.VERSION_CODES.HONEYCOMB) {
			appController.init(getApplicationContext(),
					getSharedPreferences(Constants.PREFS_NAME, 0),
					getActionBar());
			if (!Common.hasInternet()) {
				getActionBar().setIcon(
						getResources().getDrawable(
								R.drawable.sigimera_logo_offline));
			}
		} else {
			appController.init(getApplicationContext(),
					getSharedPreferences(Constants.PREFS_NAME, 0), null);
		}

		// Initialize the tabs
		initTabs();

		if (savedInstanceState != null) {
			mTabHost.setCurrentTabByTag(savedInstanceState.getString("tab"));
		}
	}

	/**
	 * Initialise Google Cloud Messaging.
	 */
	private void initGCM() {
		if (Config.getInstance().getGcmProjectId() != null) {
			try {
				GCMRegistrar.checkDevice(this);
				GCMRegistrar.checkManifest(this);
				final String regId = GCMRegistrar.getRegistrationId(this);
				if (regId.equals("")) {
					GCMRegistrar.register(this, Config.getInstance()
							.getGcmProjectId());
				}
			} catch (Exception e) {
				Log.v(Constants.LOG_TAG_SIGIMERA_APP,
						"Device meets not the GCM requirements. Exception: "
								+ e);
			}
		}
	}

	/**
	 * Initialise of tabs.
	 */
	private void initTabs() {
		mTabHost = (TabHost) findViewById(android.R.id.tabhost);
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mTabHost.setup();

		try {
			authToken = ApplicationController.getInstance().getSessionHandler()
					.getAuthenticationToken();

			setTabsAfterLogin();
		} catch (AuthenticationErrorException e) {
			setTabsBeforeLogin();
		}
	}

	@Override
	protected final void onSaveInstanceState(final Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString("tab", mTabHost.getCurrentTabTag());
	}

	@Override
	public final boolean onCreateOptionsMenu(final Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		MenuItem itemUpdateLocation = menu.findItem(R.id.menu_update_location);
		itemUpdateLocation.setTitle("Update your location");

		MenuItem itemUpdateEverything = menu
				.findItem(R.id.menu_update_everything);
		itemUpdateEverything.setTitle("Update everything");
		return super.onCreateOptionsMenu(menu);
	}

	/**
	 * Login Listener defined in login.xml layout.
	 * 
	 * @param view
	 *            - Android internal needs.
	 * @see http://developer.android.com/reference
	 *      /android/view/View.html#attr_android:onClickandroid:onClick
	 */
	public final void loginClicked(final View view) {
		progressDialog = ProgressDialog.show(MainActivity.this, null,
				"Authentication in progress...", false);
		Thread worker = new Thread() {
			@Override
			public void run() {
				Looper.prepare();

				EditText emailView = (EditText) findViewById(R.id.email_input_field);
				EditText passwordView = (EditText) findViewById(R.id.password_input_field);

				if (ApplicationController
						.getInstance()
						.getSessionHandler()
						.login(emailView.getText().toString(),
								passwordView.getText().toString())) {

					guiHandler.post(successfulLogin);
				} else {
					guiHandler.post(errorLogin);
				}
			}
		};
		worker.start();
	}

	/**
	 * The click listener defined in statistic_fragment.xml.
	 * 
	 * @param view
	 *            - Android internal needs.
	 * @see http://developer.android.com/reference
	 *      /android/view/View.html#attr_android:onClickandroid:onClick
	 */
	public final void allCrisesClicked(final View view) {
		if (mViewPager != null) {
			mViewPager.setCurrentItem(1, true);
		}
	}

	/**
	 * Create account listener in login.xml layout.
	 * 
	 * @param view
	 *            - Android internal needs
	 * @see http://developer.android.com/reference
	 *      /android/view/View.html#attr_android:onClickandroid:onClick
	 */
	public final void accountClicked(final View view) {
		Uri uri = Uri.parse("https://www.sigimera.org/register");
		startActivity(new Intent(Intent.ACTION_VIEW, uri));
	}

	@Override
	public final boolean onOptionsItemSelected(final MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_update_location:
			LocationUpdaterHttpHelper locUpdater = new LocationUpdaterHttpHelper();
			Location loc = LocationController.getInstance()
					.getLastKnownLocation();
			if (loc != null) {
				Toast toast = Toast.makeText(getApplicationContext(),
						"Trying to update your current location...",
						Toast.LENGTH_LONG);
				toast.setGravity(Gravity.TOP, 0, 0);
				toast.show();
				String latitude = loc.getLatitude() + "";
				String longitude = loc.getLongitude() + "";
				authToken = ApplicationController.getInstance()
						.getSharedPreferences().getString("auth_token", null);
				if (authToken != null) {
					locUpdater.execute(authToken, latitude, longitude);
				}
			} else {
				Toast toast = Toast.makeText(getApplicationContext(),
						"Not able to update location! "
								+ "Please active location access...",
						Toast.LENGTH_LONG);
				toast.setGravity(Gravity.TOP, 0, 0);
				toast.show();
			}
			return true;
		case R.id.menu_update_everything:
			try {
				PersistanceController.getInstance().updateEverything(authToken);
			} catch (InterruptedException e) {
				Log.e("[MAIN ACTIVITY]",
						"The thread coudn't sleep betheen api calls.");
			}
			return true;
		case R.id.menu_logout:
			ApplicationController.getInstance().getSessionHandler().logout();
			setTabsBeforeLogin();
			return true;
		case R.id.menu_unregister:
			GCMRegistrar.unregister(getApplicationContext());
			return true;
		case R.id.about:
			showAboutDialog();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/**
	 * Close the progress dialog.
	 */
	public final void closeProgressDialog() {
		if (progressDialog != null) {
			progressDialog.dismiss();
			progressDialog = null;
		}
	}

	/**
	 * Shows the about dialog.
	 */
	public final void showAboutDialog() {
		AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
				.create();
		dialog.setTitle("About");
		dialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(final DialogInterface dialog,
							final int which) {
						dialog.cancel();
					}
				});

		WebView wv = new WebView(this);
		wv.setBackgroundColor(Color.BLACK);

		StringBuffer strbuffer = new StringBuffer();
		strbuffer.append("<small><font color='white'>");
		strbuffer.append("<h3 style='text-align: center'>"
				+ this.getString(R.string.app_name) + "</h3>");

		strbuffer.append("<p>This is the official App of the Crises "
				+ "Information Platform Sigimera. It provides "
				+ "the following functionality:</p>");
		strbuffer.append("<ul>");
		strbuffer.append("<li>Get crises (natural disaster) information."
				+ "Currently floods, earthquakes, cyclones "
				+ "and volcanic erruptions.</li>");
		strbuffer.append("<li>Get crises alerts via push notifications.</li>");
		strbuffer.append("<li>Get new crises via push notifications.</li>");
		strbuffer.append("<li>Manage your App via "
				+ "<a href='http://www.sigimera.org/mobile_devices'>"
				+ "<span style='color: #00FFFF'>mobile device management"
				+ "website </span></a>.");
		strbuffer.append("</ul>");
		strbuffer.append("<p>&copy; 2012 <a href='http://www.sigimera.org'>"
				+ "<span style='color: #00FFFF'>Sigimera</span></a>. "
				+ "All rights reserved.</p>");

		wv.loadData(strbuffer.toString(), "text/html", "utf-8");

		dialog.setView(wv);
		dialog.show();
	}

	/**
	 * Shows the error toast message if the login failed.
	 */
	public final void showLoginErrorToast() {
		new ToastNotification(getApplicationContext(),
				"Email or password are incorrect!", Toast.LENGTH_SHORT);
		closeProgressDialog();
	}

	/**
	 * Set the order of tabs if the login was successfully or there exists a
	 * authentication token.
	 */
	private void setTabsAfterLogin() {
		this.mTabHost.clearAllTabs();
		this.mTabsAdapter = new TabsAdapter(this, this.mTabHost,
				this.mViewPager);

		this.mTabsAdapter.addTab(
				this.mTabHost.newTabSpec("Home").setIndicator("Home"),
				StatisticFragment.class, null);
		this.mTabsAdapter.addTab(this.mTabHost.newTabSpec("Crises")
				.setIndicator("Crises"), CrisesListFragment.class, null);
		this.mTabsAdapter.addTab(
				mTabHost.newTabSpec("Profile").setIndicator("Profile"),
				ProfileFragment.class, null);
		initGCM();
		closeProgressDialog();
	}

	/**
	 * Set the order of tabs if there is no authentication token.
	 */
	private void setTabsBeforeLogin() {
		this.mTabHost.clearAllTabs();
		this.mTabsAdapter = new TabsAdapter(this, this.mTabHost,
				this.mViewPager);
		mTabsAdapter.addTab(mTabHost.newTabSpec("login").setIndicator("Login"),
				LoginFragment.class, null);
		mTabsAdapter.addTab(mTabHost.newTabSpec("Crises")
				.setIndicator("Crises"), CrisesListFragment.class, null);
		closeProgressDialog();
	}
}

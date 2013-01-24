package org.sigimera.app.android;

import org.sigimera.app.android.R;
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

public class MainActivity extends FragmentActivity {
	private String authToken;
	
	private TabHost mTabHost;
	private ViewPager mViewPager;
	private TabsAdapter mTabsAdapter;

	private ProgressDialog progressDialog = null;

	private final Handler guiHandler = new Handler();
	private final Runnable errorLogin = new Runnable() {
		@Override
		public void run() {
			showLoginErrorToast();
		}
	};
	private final Runnable successfulLogin = new Runnable() {
		@Override
		public void run() {
			setTabsAfterLogin();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
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
			if (!Common.hasInternet())
				getActionBar().setIcon(
						getResources().getDrawable(
								R.drawable.sigimera_logo_offline));
		} else {
			appController.init(getApplicationContext(),
					getSharedPreferences(Constants.PREFS_NAME, 0), null);
		}

		// Initialize the tabs
		initTabs();

		if (savedInstanceState != null)
			mTabHost.setCurrentTabByTag(savedInstanceState.getString("tab"));
	}

	/**
	 * Initialize Google Cloud Messaging.
	 */
	private void initGCM() {
		if (Config.getInstance().getGcmProjectId() != null) {
			try {
				GCMRegistrar.checkDevice(this);
				GCMRegistrar.checkManifest(this);
				final String regId = GCMRegistrar.getRegistrationId(this);
				if (regId.equals(""))
					GCMRegistrar.register(this, Config.getInstance()
							.getGcmProjectId());
			} catch (Exception e) {
				Log.v(Constants.LOG_TAG_SIGIMERA_APP,
						"Device meets not the GCM requirements. Exception: "
								+ e);
			}
		}
	}

	/**
	 * Initialize of tabs.
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
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString("tab", mTabHost.getCurrentTabTag());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		MenuItem itemUpdateLocation = menu.findItem(R.id.menu_update_location);
		itemUpdateLocation.setTitle("Update your location");
		
		MenuItem itemUpdateEverything = menu.findItem(R.id.menu_update_everything);
		itemUpdateEverything.setTitle("Update everything");
		return super.onCreateOptionsMenu(menu);
	}

	/**
	 * Login Listener defined in login.xml layout
	 * 
	 * @param view
	 */
	public void loginClicked(View view) {
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

	public void allCrisesClicked(View view) {
		if (mViewPager != null)
			mViewPager.setCurrentItem(1, true);
	}

	/**
	 * Create account listener in login.xml layout
	 * 
	 * @param view
	 */
	public void accountClicked(View view) {
		Uri uri = Uri.parse("https://www.sigimera.org/register");
		startActivity(new Intent(Intent.ACTION_VIEW, uri));
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
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
				if (authToken != null)
					locUpdater.execute(authToken, latitude, longitude);
			} else {
				Toast toast = Toast
						.makeText(
								getApplicationContext(),
								"Not able to update location! Please active location access...",
								Toast.LENGTH_LONG);
				toast.setGravity(Gravity.TOP, 0, 0);
				toast.show();
			}
			return true;
		case R.id.menu_update_everything:
			try {
				PersistanceController.getInstance().updateEverything(authToken);
			} catch (InterruptedException e) {
				Log.e("[MAIN ACTIVITY]", "The thread coudn't sleep betheen api calls.");
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

	public void closeProgressDialog() {
		if (progressDialog != null) {
			progressDialog.dismiss();
			progressDialog = null;
		}
	}

	public void showAboutDialog() {
		AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
				.create();
		dialog.setTitle("About");
		dialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});

		WebView wv = new WebView(this);
		wv.setBackgroundColor(Color.BLACK);

		StringBuffer strbuffer = new StringBuffer();
		strbuffer.append("<small><font color='white'>");
		strbuffer.append("<h3 style='text-align: center'>"
				+ this.getString(R.string.app_name) + "</h3>");

		strbuffer
				.append("<p>This is the official App of the Crises Information Platform Sigimera. It provides the following functionality:</p>");
		strbuffer.append("<ul>");
		strbuffer
				.append("<li>Get crises (natural disaster) information. Currently floods, earthquakes, cyclones and volcanic erruptions.</li>");
		strbuffer.append("<li>Get crises alerts via push notifications.</li>");
		strbuffer.append("<li>Get new crises via push notifications.</li>");
		strbuffer
				.append("<li>Manage your App via <a href='http://www.sigimera.org/mobile_devices'><span style='color: #00FFFF'>mobile device management website</span></a>.");
		strbuffer.append("</ul>");
		strbuffer
				.append("<p>&copy; 2012 <a href='http://www.sigimera.org'><span style='color: #00FFFF'>Sigimera</span></a>. All rights reserved.</p>");

		wv.loadData(strbuffer.toString(), "text/html", "utf-8");

		dialog.setView(wv);
		dialog.show();
	}

	public void showLoginErrorToast() {
		new ToastNotification(getApplicationContext(),
				"Email or password are incorrect!", Toast.LENGTH_SHORT);
		closeProgressDialog();
	}

	private void setTabsAfterLogin() {
		//Before setting the tabs get all information
		try {
			authToken = ApplicationController.getInstance().getSessionHandler().getAuthenticationToken();
			PersistanceController.getInstance().updateEverything(authToken);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (AuthenticationErrorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
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

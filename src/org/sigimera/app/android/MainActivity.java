package org.sigimera.app.android;

import java.util.ArrayList;

import org.sigimera.app.android.R;
import org.sigimera.app.android.controller.ApplicationController;
import org.sigimera.app.android.controller.CrisesController;
import org.sigimera.app.android.controller.SessionHandler;
import org.sigimera.app.android.exception.AuthenticationErrorException;
import org.sigimera.app.android.model.Constants;
import org.sigimera.app.android.util.Common;
import org.sigimera.app.android.util.Config;

import com.google.android.gcm.GCMRegistrar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.Toast;

public class MainActivity extends FragmentActivity {
	private TabHost mTabHost;
	private ViewPager mViewPager;
	private TabsAdapter mTabsAdapter;
	private SessionHandler session_handler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		ApplicationController appController = ApplicationController
				.getInstance();
		appController.init(getApplicationContext(), getSessionSettings());

		if (Common.hasInternet()) {
			this.session_handler = appController.getSessionHandler();
			CrisesController.getInstance();

			// Initialize of GCM
			initGCM();
			
			// Initialize the tabs
			initTabs();

			if (savedInstanceState != null)
				mTabHost.setCurrentTabByTag(savedInstanceState.getString("tab"));

		} else
			new ToastNotification(getApplicationContext(),
					"No internet connection", Toast.LENGTH_LONG);
	}

	/**
	 * Initialize Google Cloud Messaging
	 */
	private void initGCM() {
		if ( Config.getInstance().getGcmProjectId() != null ) {
			try {
				GCMRegistrar.checkDevice(this); GCMRegistrar.checkManifest(this);
				final String regId = GCMRegistrar.getRegistrationId(this);
				if (regId.equals("")) GCMRegistrar.register(this, Config.getInstance().getGcmProjectId());
			} catch (Exception e) {
				Log.v(Constants.LOG_TAG_SIGIMERA_APP, "Device meets not the GCM requirements. Exception: " + e);
			}
		}
	}
	
	/**
	 * Initialize of tabs
	 */
	private void initTabs() {
		mTabHost = (TabHost) findViewById(android.R.id.tabhost);
		mTabHost.setup();
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mTabsAdapter = new TabsAdapter(this, mTabHost, mViewPager);

		try {
			this.session_handler.getAuthenticationToken();

			mTabsAdapter.addTab(mTabHost.newTabSpec("Home")
					.setIndicator("Home"), StatisticFragment.class, null);
			mTabsAdapter.addTab(
					mTabHost.newTabSpec("Crises").setIndicator("Crises"),
					CrisesListFragment.class, null);
		} catch (AuthenticationErrorException e) {
			mTabsAdapter.addTab(
					mTabHost.newTabSpec("login").setIndicator("Login"),
					LoginFragment.class, null);
			mTabsAdapter.addTab(mTabHost.newTabSpec("Last 10 crises")
					.setIndicator("Last 10 Crises"), CrisesListFragment.class,
					null);
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
		return true;
	}

	/**
	 * Get the session handler preferences 
	 * @return
	 */
	public SharedPreferences getSessionSettings() {
		String PREFS_NAME = "session_handler_preferences";
		return getSharedPreferences(PREFS_NAME, 0);
	}

	/**
	 * Login Listener defined in login.xml layout
	 * @param view
	 */
	public void loginClicked(View view) {
		EditText emailView = (EditText) findViewById(R.id.email_input_field);
		EditText passwordView = (EditText) findViewById(R.id.password_input_field);

		if (session_handler.login(emailView.getText().toString(), passwordView
				.getText().toString())) {
			mTabHost.clearAllTabs();
			mTabsAdapter = new TabsAdapter(this, mTabHost, mViewPager);

			mTabsAdapter.addTab(mTabHost.newTabSpec("home")
					.setIndicator("Home"), StatisticFragment.class, null);
			mTabsAdapter.addTab(
					mTabHost.newTabSpec("crises").setIndicator("Crises"),
					CrisesListFragment.class, null);

		} else {
			new ToastNotification(getApplicationContext(),
					"Email or password were incorrect!", Toast.LENGTH_SHORT);
		}
	}

	/**
	 * Create account listener in login.xml layout
	 * @param view
	 */
	public void accountClicked(View view) {
		Uri uri = Uri.parse("https://www.sigimera.org/register");
		startActivity(new Intent(Intent.ACTION_VIEW, uri));
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_logout:
			this.session_handler.logout();
			mTabHost.clearAllTabs();
			mTabsAdapter = new TabsAdapter(this, mTabHost, mViewPager);
			mTabsAdapter.addTab(
					mTabHost.newTabSpec("login").setIndicator("Login"),
					LoginFragment.class, null);
			mTabsAdapter.addTab(mTabHost.newTabSpec("Last 10 crises")
					.setIndicator("Last 10 Crises"), CrisesListFragment.class,
					null);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	
	/**
	 * This is a helper class that implements the management of tabs and all
	 * details of connecting a ViewPager with associated TabHost. It relies on a
	 * trick. Normally a tab host has a simple API for supplying a View or
	 * Intent that each tab will show. This is not sufficient for switching
	 * between pages. So instead we make the content part of the tab host 0dp
	 * high (it is not shown) and the TabsAdapter supplies its own dummy view to
	 * show as the tab content. It listens to changes in tabs, and takes care of
	 * switch to the correct paged in the ViewPager whenever the selected tab
	 * changes.
	 */
	public static class TabsAdapter extends FragmentPagerAdapter implements
			TabHost.OnTabChangeListener, ViewPager.OnPageChangeListener {
		private final Context mContext;
		private final TabHost mTabHost;
		private final ViewPager mViewPager;
		private final ArrayList<TabInfo> mTabs = new ArrayList<TabInfo>();

		static final class TabInfo {
//			private final String tag;
			private final Class<?> clss;
			private final Bundle args;

			TabInfo(String _tag, Class<?> _class, Bundle _args) {
//				tag = _tag;
				clss = _class;
				args = _args;
			}
		}

		static class DummyTabFactory implements TabHost.TabContentFactory {
			private final Context mContext;

			public DummyTabFactory(Context context) {
				mContext = context;
			}

			@Override
			public View createTabContent(String tag) {
				View v = new View(mContext);
				v.setMinimumWidth(0);
				v.setMinimumHeight(0);
				return v;
			}
		}

		public TabsAdapter(FragmentActivity activity, TabHost tabHost,
				ViewPager pager) {
			super(activity.getSupportFragmentManager());
			mContext = activity;
			mTabHost = tabHost;
			mViewPager = pager;
			mTabHost.setOnTabChangedListener(this);
			mViewPager.setAdapter(this);
			mViewPager.setOnPageChangeListener(this);
		}

		public void addTab(TabHost.TabSpec tabSpec, Class<?> clss, Bundle args) {
			tabSpec.setContent(new DummyTabFactory(mContext));
			String tag = tabSpec.getTag();

			TabInfo info = new TabInfo(tag, clss, args);
			mTabs.add(info);
			mTabHost.addTab(tabSpec);
			notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			return mTabs.size();
		}

		@Override
		public Fragment getItem(int position) {
			TabInfo info = mTabs.get(position);
			return Fragment.instantiate(mContext, info.clss.getName(),
					info.args);
		}

		@Override
		public void onTabChanged(String tabId) {
			int position = mTabHost.getCurrentTab();
			mViewPager.setCurrentItem(position);
		}

		@Override
		public void onPageScrolled(int position, float positionOffset,
				int positionOffsetPixels) {
		}

		@Override
		public void onPageSelected(int position) {
			// Unfortunately when TabHost changes the current tab, it kindly
			// also takes care of putting focus on it when not in touch mode.
			// The jerk.
			// This hack tries to prevent this from pulling focus out of our
			// ViewPager.
			TabWidget widget = mTabHost.getTabWidget();
			int oldFocusability = widget.getDescendantFocusability();
			widget.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
			mTabHost.setCurrentTab(position);
			widget.setDescendantFocusability(oldFocusability);
		}

		@Override
		public void onPageScrollStateChanged(int state) {
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			super.destroyItem(container, position, object);
			FragmentManager manager = ((Fragment) object).getFragmentManager();
			FragmentTransaction trans = manager.beginTransaction();
			trans.remove((Fragment) object);
			trans.commit();
		}
	}
}

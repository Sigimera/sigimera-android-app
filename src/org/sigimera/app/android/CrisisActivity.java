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
package org.sigimera.app.android;

import java.util.ArrayList;
import java.util.HashMap;

import org.sigimera.app.android.R;
import org.sigimera.app.android.controller.ApplicationController;
import org.sigimera.app.android.controller.CrisesController;
import org.sigimera.app.android.model.Constants;
import org.sigimera.app.android.model.Crisis;
import org.sigimera.app.android.util.Common;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

/**
 * @author Corneliu-Valentin Stanciu, Alex Oberhauser
 * @email corneliu.stanciu@sigimera.org, alex.oberhauser@sigimera.org
 */
public class CrisisActivity extends Activity {
	private static final int MENU_SHARE = 0x0010;
//	private static final int MENU_ABOUT = 0x0020;
//	private static final int MENU_ADD = 0x0030;
//	private static final int MENU_COMMENT = 0x0040;
	
	private String alertLevel = null;
	private String severity = null;
	private String description = null;
	private String countryConcat = "";
	private String affectedPeople = null;
	private String crisisType = null;
	private ArrayList<String> countries = new ArrayList<String>();
	
	private Double latitude;
	private Double longitude;

	private Crisis crisis;
	private ListView list;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.crisis_info);

		this.list = (ListView) findViewById(R.id.crisis_info);
		this.list.setOnItemClickListener(this.listClickListener);	
				
		String crisisID = getIntent().getStringExtra(Constants.CRISIS_ID);

		String authToken = ApplicationController.getInstance().getSharedPreferences().getString("auth_token", null);
		if ( null != crisisID ) {
			this.crisis = CrisesController.getInstance().getCrisis(authToken, crisisID);
		} else {
			this.crisis = CrisesController.getInstance().getLatestCrisis(authToken);
		}
		
		updateGUI();
	}

	private void updateGUI() {
		ArrayList<HashMap<String, String>> collectionList = new ArrayList<HashMap<String, String>>();

		alertLevel = crisis.getAlertLevel();
		severity = crisis.getSeverity();
		description = crisis.getDescription();
		countries = crisis.getCountries();
		affectedPeople = crisis.getPopulation();
		crisisType = crisis.getSubject();
		latitude = crisis.getLatitude();
		longitude = crisis.getLongitude();
		
		collectionList.add(getListEntry("See crisis on map", 
					"Lat: " + latitude + " -- Long: " + longitude, 
					String.valueOf(R.drawable.glyphicons_242_google_maps_white)));
		
		if (description != null)
			collectionList.add(getListEntry(description.substring(0, 80) + " ...", 
					"Description", String.valueOf(R.drawable.glyphicons_030_pencil_white)));
		if (affectedPeople != null)
			collectionList.add(getListEntry(affectedPeople, "Affected people",
					String.valueOf(R.drawable.glyphicons_024_parents_white)));
		if (alertLevel != null)
			collectionList.add(getListEntry(alertLevel, "Alert Level",
					String.valueOf(R.drawable.glyphicons_196_circle_exclamation_mark_white)));
		if (severity != null)
			collectionList.add(getListEntry(Common.capitalize(severity), 
					"Severity", String.valueOf(R.drawable.glyphicons_079_signal_white)));	
		
		if ( this.countries != null && this.countries.size() > 0 ) {
			StringBuffer countryConcat = new StringBuffer("");
			for ( String country : countries ) {
				countryConcat.append(Common.capitalize(country));
				countryConcat.append(", ");
			}
			collectionList.add(getListEntry(countryConcat.toString(), "Country",
					String.valueOf(R.drawable.glyphicons_266_flag_white)));
		}

		// Add list to the view
		SimpleAdapter adapterCollectionList = new SimpleAdapter(this,
						collectionList, R.layout.list_entry, new String[] { Constants.ICON, Constants.TOP,
						Constants.BOTTOM, Constants.ARROW }, new int[] { R.id.icon, R.id.topText,
						R.id.bottomText });
		this.list.setAdapter(adapterCollectionList);
	}

	/**
	 * Creating the list entry which needs to be added to the list.
	 * 
	 * @param top The text which should be showed on top of the list entry
	 * @param bottom The text which should be showed at the bottom of the list entry
	 * @param icon The icon should be in format: String.valueOf(R.drawable.MyIcon)
	 * @return The map having an icon, top and bottom text. (list entry)
	 */
	private HashMap<String, String> getListEntry(String top, String bottom, String icon) {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(Constants.ICON, icon);
		map.put(Constants.TOP, top);
		map.put(Constants.BOTTOM, bottom);
		return map;
	}

	private OnItemClickListener listClickListener = new OnItemClickListener() {
		public void onItemClick(AdapterView<?> list, View view, int position,
				long id) {
			String text = null;
			switch (position) {
			case 0:
				if ( null != latitude && null != longitude ) {
					Intent mapActivity = new Intent(ApplicationController.getInstance().getApplicationContext(), FullMapActivity.class);
					mapActivity.putExtra(Constants.LATITUDE, latitude.toString());
					mapActivity.putExtra(Constants.LONGITUDE, longitude.toString());
					mapActivity.putExtra(Constants.CRISIS_TYPE, crisisType);
					startActivity(mapActivity);
				}
				break;
			case 1:
				text = description;
				break;				
			case 2:
				text = affectedPeople;
				break;
			case 3:
				text = "Alert level: " + alertLevel;
				break;
			case 4:
				text = Common.capitalize(severity);
				break;
			case 5:
				text = "Country: " + countryConcat;
				break;			
			}
			if ( null != text )
				new ToastNotification(getApplicationContext(), text, Toast.LENGTH_SHORT);
		}
	};
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    menu.add(0, MENU_SHARE, 20, "Share crisis").setIcon(R.drawable.glyphicons_326_share);
//		menu.add(0, MENU_ABOUT, 30, "About").setIcon(R.drawable.about_icon);
//		menu.add(0, MENU_ADD, 30, "Add").setIcon(R.drawable.glyphicons_190_circle_plus);
//		menu.add(0, MENU_COMMENT, 40, "Comment").setIcon(R.drawable.glyphicons_309_comments);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case MENU_SHARE:
				if ( crisis.getID() != null )
					this.startActivity(Common.shareCrisis(crisis.getID()));
				else
					new ToastNotification(ApplicationController.getInstance().getApplicationContext(), 
						"Failed to read the crisis ID", Toast.LENGTH_SHORT);
				return true;
//			case MENU_ABOUT:
//				new ToastNotification(ApplicationController.getInstance().getApplicationContext(), 
//						"TODO: provide content for about window", Toast.LENGTH_SHORT);
//				return true;			
//			case MENU_ADD:
//				new ToastNotification(ApplicationController.getInstance().getApplicationContext(), 
//						"TODO: provide content for add window", Toast.LENGTH_SHORT);
//				return true;
//			case MENU_COMMENT:
//				new ToastNotification(ApplicationController.getInstance().getApplicationContext(), 
//						"TODO: provide content for comment window", Toast.LENGTH_SHORT);
//				return true;				
		}
		return false;
	}
}

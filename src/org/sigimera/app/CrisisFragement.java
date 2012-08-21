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

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.sigimera.app.controller.ApplicationController;
import org.sigimera.app.controller.Common;
import org.sigimera.app.controller.CrisesController;
import org.sigimera.app.model.Constants;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

/**
 * @author Corneliu-Valentin Stanciu
 * @email corneliu.stanciu@sigimera.org
 */
public class CrisisFragement extends ListFragment {
	private Activity activity;
	private CrisesController crisisController = CrisesController.getInstance();

	private static final String BOTTOM = "bottom";
	private static final String TOP = "top";
	private static final String ICON = "icon";
	private static final String ARROW = "rightArrow";

	private static final int MENU_SHARE = 0x0010;
	private static final int MENU_ABOUT = 0x0020;

	private JSONObject crisis;

	private String alertLevel = null;
	private String severity = null;
	private String description = null;
	private JSONArray country = null;
	private String countryConcat = "";
	private String affectedPeople = null;
	
	@Override
    public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setHasOptionsMenu(true);					
		ListView list = this.getListView();
		list.setOnItemClickListener(listClickListener);
		
		activity = getActivity();
		
		try {
			this.crisis = new JSONObject(getArguments().getString(Constants.CRISIS));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		updateGUI();
	}

	private void updateGUI() {
		ArrayList<HashMap<String, String>> collectionList = new ArrayList<HashMap<String, String>>();

		try {
			alertLevel = crisis.getString("crisis_alertLevel");
			severity = crisis.getString("crisis_severity");
			description = crisis.getString("dc_description");
			country = crisis.getJSONArray("gn_parentCountry");
			affectedPeople = crisis.getString("crisis_population");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		//TODO: show as bottom text the GPS coordinates
		collectionList.add(getListEntry("Map", "See crisis on map", 
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
			collectionList.add(getListEntry(CrisesController.getInstance().capitalize(severity), 
					"Severity", String.valueOf(R.drawable.glyphicons_079_signal_white)));

		if (country != null && country.length() > 0) {
			for (int i = 0; i < country.length(); i++) {
				try {
					countryConcat += crisisController.capitalize(String.valueOf(country.get(i)));
					if (i != 0)
						countryConcat += ", ";
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			collectionList.add(getListEntry(countryConcat, "Country",
					String.valueOf(R.drawable.glyphicons_266_flag_white)));
		}			

		// Add list to the view
		SimpleAdapter adapterCollectionList = new SimpleAdapter(this.activity,
				collectionList, R.layout.list_entry, new String[] { ICON, TOP,
						BOTTOM, ARROW }, new int[] { R.id.icon, R.id.topText,
						R.id.bottomText });
		setListAdapter(adapterCollectionList);
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
		map.put(ICON, icon);
		map.put(TOP, top);
		map.put(BOTTOM, bottom);
		return map;
	}

	private OnItemClickListener listClickListener = new OnItemClickListener() {
		public void onItemClick(AdapterView<?> list, View view, int position,
				long id) {
			String text = "";
			switch (position) {
			case 0:
				startActivity(new Intent(getActivity(), FullMapActivity.class));
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
				text = crisisController.capitalize(severity);
				break;
			case 5:
				text = "Country: " + countryConcat;
				break;			
			}
			new Notification(activity, text, Toast.LENGTH_SHORT);
		}
	};

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// TODO Add your menu entries here
		super.onCreateOptionsMenu(menu, inflater);
		menu.add(0, MENU_SHARE, 20, "Share crisis").setIcon(R.drawable.glyphicons_326_share);
		menu.add(0, MENU_ABOUT, 30, "About").setIcon(R.drawable.about_icon);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_SHARE:
			try {
				this.startActivity(Common.shareCrisis(crisis.getString("_id")));
			} catch (JSONException e) {
				new Notification(ApplicationController.getInstance().getApplicationContext(), 
						"Failed to read the crisis ID", Toast.LENGTH_SHORT);
			}
			return true;
		case MENU_ABOUT:
			new Notification(ApplicationController.getInstance().getApplicationContext(), 
					"TODO: provide content for about window", Toast.LENGTH_SHORT);
			return true;
		}
		return false;
	}
}

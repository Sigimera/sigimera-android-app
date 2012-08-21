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

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SimpleAdapter;
import android.widget.Toast;

/**
 * @author Corneliu-Valentin Stanciu
 * @email corneliu.stanciu@sigimera.org
 */
public class CrisesListFragment extends ListFragment {
	private JSONArray crises;
	private CrisesController crisisControler;
	private Context context;
	
	private CrisesListListener crisesListListener;
	
	// The communication with the MainActivity
	public interface CrisesListListener {
		public void onCrisesListItemClicked(AdapterView<?> arg0, View arg1, int arg2, long arg3);
	}

	@Override
    public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		registerForContextMenu(getListView());
		getListView().setOnItemClickListener(clickListener);
		
		this.context = getActivity();
		this.crisesListListener = (CrisesListListener) getActivity();
		this.crisisControler = CrisesController.getInstance();				
		
		try {
			this.crises = new JSONArray(getArguments().getString(Constants.CRISES_LIST));
			showCrises();
		} catch (JSONException e) {
			new Notification(getActivity(), "No crises attached to the fragment", Toast.LENGTH_LONG);
		}						
	}
	
	private void showCrises() {
		ArrayList<HashMap<String, String>> buttonList = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> map = new HashMap<String, String>();		
		
		try {
			for ( int count = 0; count < crises.length(); count++ ) {
				try {
					JSONObject crisis = (JSONObject) crises.get(count);
//					Cache cache = ApplicationController.getInstance().getCache();
//					cache.addCrisis(crisis);
//					System.out.println("Cache number of crises: " + cache.getCrisesNumber());
	
					map = new HashMap<String, String>();
					map.put("top", crisisControler.getShortTitle(crisis));
	
					String crisis_type = crisis.getString("subject");					
					map.put("icon", Common.getCrisisIcon(crisis_type) + "");
	
					map.put("bottom", crisis.getString("dc_date"));
	
					buttonList.add(map);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		} catch (NullPointerException e) {
			//TODO: Auto-generated catch block
			e.printStackTrace();
		}					
		
		SimpleAdapter adapterMainList = new SimpleAdapter(context, buttonList,
				R.layout.list_entry, new String[] { "icon", "top", "bottom" }, new int[] {
						R.id.icon, R.id.topText, R.id.bottomText });		
		setListAdapter(adapterMainList);
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
	                                ContextMenuInfo menuInfo) {
	    super.onCreateContextMenu(menu, v, menuInfo);
	    menu.setHeaderTitle("Options");
	    menu.setHeaderIcon(R.drawable.sigimera_logo);	    
	    MenuInflater inflater = getActivity().getMenuInflater();
	    inflater.inflate(R.menu.list_menu, menu);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
	    // Handle item selection
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
	    switch (item.getItemId()) {
	        case R.id.open:
	        	crisesListListener.onCrisesListItemClicked(null, null, info.position, 0);
	            return true;
	        case R.id.share:
				try {
					this.startActivity(Common.shareCrisis(((JSONObject) crises.get(info.position)).getString("_id")));
				} catch (JSONException e) {
					new Notification(ApplicationController.getInstance().getApplicationContext(), 
							"Failed to get the crisis", Toast.LENGTH_SHORT);
				}
	            return true;
	        default:
	            return super.onContextItemSelected(item);
	    }
	}
	
	OnItemClickListener clickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			crisesListListener.onCrisesListItemClicked(arg0, arg1, arg2, arg3);
		}
	};
}
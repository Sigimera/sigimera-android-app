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
import java.util.Iterator;

import org.sigimera.app.android.R;
import org.sigimera.app.android.controller.ApplicationController;
import org.sigimera.app.android.controller.CrisesController;
import org.sigimera.app.android.exception.AuthenticationErrorException;
import org.sigimera.app.android.model.Constants;
import org.sigimera.app.android.model.Crisis;
import org.sigimera.app.android.util.Common;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;

/**
 * @author Corneliu-Valentin Stanciu, Alex Oberhauser
 * @email corneliu.stanciu@sigimera.org, alex.oberhauser@sigimera.org
 */
public class CrisesListFragment extends Fragment {
	private ArrayList<Crisis> crises = new ArrayList<Crisis>();;
	private ListView list;
	
	private int page = 1;
//	private boolean showMore = true;
	private SimpleAdapter adapterMainList;
	
	private final Handler guiHandler = new Handler();
	private final Runnable updateGUI = new Runnable() {		
		@Override
		public void run() {
			showCrises();
		}
	};
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.crises_list, container, false);

		this.list = (ListView) view.findViewById(R.id.crisis_list);		
		this.list.setOnItemClickListener(this.clickListener);
//		this.list.setOnScrollListener(this.scrollListener);
		registerForContextMenu(this.list);
		
		if ( getArguments() != null ) {
			Object object = getArguments().getSerializable("crises");
			if ( object != null )
				crises = (ArrayList<Crisis>) object;	
		}		
		
		Thread worker = new Thread() {
			@Override
			public void run() {
				Looper.prepare();
				String auth_token = null;
				try {
					auth_token = ApplicationController.getInstance().getSessionHandler().getAuthenticationToken();
				} catch (AuthenticationErrorException e) {
					Log.d(Constants.LOG_TAG_SIGIMERA_APP, "Fetching public crises list...");
				}
				
				if ( crises.isEmpty() )
					crises = CrisesController.getInstance().getCrises(auth_token, page);
				
				guiHandler.post(updateGUI);
			}
		};
		worker.start();

		return view;
	}	
	
	private void showCrises() {
		ArrayList<HashMap<String, String>> crisesList = new ArrayList<HashMap<String, String>>();
		
		HashMap<String, String> listEntry;
		
		Iterator<Crisis> iter = this.crises.iterator();
		while ( iter.hasNext() ) {
			Crisis entry = iter.next();
			
			listEntry = new HashMap<String, String>();
			listEntry.put("type_icon", entry.getTypeIcon());
			listEntry.put("short_title", entry.getShortTitle());
			listEntry.put("dc_date", Common.getTimeAgoInWords(Common.getMiliseconds(entry.getDate())));
			
			crisesList.add(listEntry);
		}
		
		this.adapterMainList = new SimpleAdapter(getActivity(), crisesList, R.layout.list_entry, 
				new String[] { "type_icon", "short_title", "dc_date" },
				new int[] { R.id.icon, R.id.topText, R.id.bottomText });	

		this.list.setAdapter(adapterMainList);	
	}	
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {	    
	    menu.setHeaderTitle("Options");
	    menu.setHeaderIcon(R.drawable.sigimera_logo);	    
	    MenuInflater inflater = new MenuInflater(getActivity());
	    inflater.inflate(R.menu.list_menu, menu);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
	    switch (item.getItemId()) {
	        case R.id.open:
	        	Intent crisisActivity = new Intent(getActivity(), CrisisActivity.class);
				crisisActivity.putExtra(Constants.CRISIS_ID, getCrisisID(info.position));
				startActivity(crisisActivity);
	            return true;
	        case R.id.share:
	        	this.startActivity(Common.shareCrisis(getCrisisID(info.position), getCrisisShortTitle(info.position)));
	            return true;
	        default:
	            return super.onContextItemSelected(item);
	    }
	}
	
	OnItemClickListener clickListener = new OnItemClickListener() {
		public void onItemClick(AdapterView<?> arg0, View arg1, int _position,
				long arg3) {
			Intent crisisActivity = new Intent(getActivity(), CrisisActivity.class);
			crisisActivity.putExtra(Constants.CRISIS_ID, crises.get(_position).getID());

			startActivity(crisisActivity);
		}
	};
	
//	OnScrollListener scrollListener = new OnScrollListener() {		
//		@Override
//		public void onScrollStateChanged(AbsListView view, int scrollState) {}
//		
//		@Override
//		public void onScroll(AbsListView view, int firstVisibleItem,
//				int visibleItemCount, int totalItemCount) {
//			
//			if ( adapterMainList != null ) {
//				int lastItem = firstVisibleItem + visibleItemCount;
//				if ( lastItem == totalItemCount && showMore ) {
//					System.out.println("DEBUG");
//					page += 1;
//					cursor = CrisesController.getInstance().getCrises(auth_token, page);
////					cursor.registerDataSetObserver(new DataSetObserver() {});
////					adapterMainList.notifyDataSetChanged();
////					adapterMainList.bindView(arg0, arg1, arg2)
//					showMore = false;
//				}
//			}
//		}
//	};
	
	/**
	 * Get the crisis ID from cursor.
	 * @param position The row number in crises cursor.
	 * @return crisis ID
	 */
	private String getCrisisID(int position) {
		return this.crises.get(position).getID();
	}
	
	private String getCrisisShortTitle(int position) {
		return this.crises.get(position).getShortTitle();
	}
	
//	private void showMoreCrises() {
//		cursor = CrisesController.getInstance().getCrises(auth_token, page);
//		showMore = true;
//	}	
}

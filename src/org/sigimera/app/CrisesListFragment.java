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

import org.sigimera.app.controller.ApplicationController;
import org.sigimera.app.controller.CrisesController;
import org.sigimera.app.exception.AuthenticationErrorException;
import org.sigimera.app.util.Common;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;

/**
 * @author Corneliu-Valentin Stanciu, Alex Oberhauser
 * @email corneliu.stanciu@sigimera.org, alex.oberhauser@sigimera.org
 */
public class CrisesListFragment extends ListFragment {
	private Context context;
	private Cursor c;
	
	private CrisesListListener crisesListListener;
	
	// The communication with the MainActivity
	public interface CrisesListListener {
		public void onCrisesListItemClicked(String  crisisID);
	}

	@Override
    public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		registerForContextMenu(getListView());
		getListView().setOnItemClickListener(clickListener);
		
		this.context = getActivity();
		this.crisesListListener = (CrisesListListener) getActivity();			

		showCrises();					
	}
	
	private void showCrises() {
		String auth_token = null;
		try {
			ApplicationController.getInstance().getSessionHandler().getAuthenticationToken();
		} catch (AuthenticationErrorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.c = CrisesController.getInstance().getCrises(auth_token, 1);
		
		SimpleCursorAdapter adapterMainList = new SimpleCursorAdapter(context, R.layout.list_entry, this.c, 
				new String[] { "type_icon", "short_title", "dc_date" },
				new int[] { R.id.icon, R.id.topText, R.id.bottomText }, SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
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
	        	this.crisesListListener.onCrisesListItemClicked(getCrisisID(info.position));
	            return true;
	        case R.id.share:
	        	this.startActivity(Common.shareCrisis(getCrisisID(info.position)));
	            return true;
	        default:
	            return super.onContextItemSelected(item);
	    }
	}
	
	OnItemClickListener clickListener = new OnItemClickListener() {
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			c.moveToPosition(arg2);
			crisesListListener.onCrisesListItemClicked(c.getString(c.getColumnIndex("_id")));
		}
	};
	
	/**
	 * Get the crisis ID from cursor.
	 * @param position The row number in crises cursor.
	 * @return crisis ID
	 */
	private String getCrisisID(int position) {
		boolean success = this.c.moveToPosition(position);
		if ( success ) {
			return this.c.getString(this.c.getColumnIndex("_id"));
		}
		return null;
	}
}

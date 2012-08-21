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

import java.util.List;

import org.sigimera.app.controller.Common;
import org.sigimera.app.model.Constants;
import org.sigimera.app.model.map.CollectionOverlay;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
/**
 * 
 * @author Corneliu-Valentin Stanciu
 * @email  corneliu.stanciu@sigimera.org
 */
public class FullMapActivity extends MapActivity{
	private MapController mapControl;
	private List<Overlay> mapOverlays;	
	private CollectionOverlay collectionOverlay;
	
	private Double latitude;
	private Double longitude;
	private String crisisType;
	
	private final Handler guiHandler = new Handler();
	private final Runnable updateCollection = new Runnable() {
		public void run() {
			updateMap();
		}
	};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.full_map);
		Intent intent = getIntent();
		
		latitude = Double.valueOf(intent.getStringExtra(Constants.LATITUDE));
		longitude = Double.valueOf(intent.getStringExtra(Constants.LONGITUDE));
		crisisType = intent.getStringExtra(Constants.CRISIS_TYPE);		
		
		final ProgressDialog progressDialog = ProgressDialog.show(
				FullMapActivity.this, null, "Loading crisis map...", false);
		
		Thread seeker = new Thread() {
			@Override
			public void run() {
				Looper.prepare();
				try {
					final MapView mapView = (MapView) findViewById(R.id.mapview);					
					mapView.setSatellite(true);
					mapOverlays = mapView.getOverlays();
					collectionOverlay = new CollectionOverlay(getResources().getDrawable(Common.getCrisisIcon(crisisType)));

					mapControl = mapView.getController();
					mapControl.setZoom(4);					
					mapControl.stopPanning();
					mapControl.setCenter(new GeoPoint(0, 0));
				} finally {
					guiHandler.post(updateCollection);
					progressDialog.dismiss();
				}
			}
		};
		seeker.start();
	}
	
	private void updateMap() {
		GeoPoint geo = new GeoPoint((int) (latitude * 1E6), (int) (longitude * 1E6));
		mapControl.setCenter(geo);
		OverlayItem overlayitem = new OverlayItem(geo, "", "");
		this.collectionOverlay.addOverlay(overlayitem);
		this.mapOverlays.add(this.collectionOverlay);
	}
	
	@Override
	protected boolean isRouteDisplayed() { return false;}
}

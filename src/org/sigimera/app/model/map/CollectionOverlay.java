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
package org.sigimera.app.model.map;

import java.util.ArrayList;

import android.graphics.drawable.Drawable;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

public class CollectionOverlay extends ItemizedOverlay<OverlayItem> {
	private final ArrayList<OverlayItem> mapOverlays = new ArrayList<OverlayItem>();
	
	public CollectionOverlay(Drawable _defaultMarker) {
		super(boundCenterBottom(_defaultMarker));
	}

	public void addOverlay(OverlayItem _overlay) {
	    this.mapOverlays.add(_overlay);
	    populate();
	}
	@Override
	protected OverlayItem createItem(int _idx) { return this.mapOverlays.get(_idx); }

	@Override
	public int size() { return this.mapOverlays.size(); }

}

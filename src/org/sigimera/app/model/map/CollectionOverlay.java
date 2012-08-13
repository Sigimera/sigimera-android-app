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

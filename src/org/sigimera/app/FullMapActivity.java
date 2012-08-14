package org.sigimera.app;

import android.os.Bundle;

import com.google.android.maps.MapActivity;

public class FullMapActivity extends MapActivity{

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.full_map);
	}
	
	@Override
	protected boolean isRouteDisplayed() { return false;}
}

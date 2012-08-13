package org.sigimera.app;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.sigimera.app.model.map.CollectionOverlay;

import android.app.ProgressDialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class CrisisActivity extends MapActivity{
	private MapController mapControl;
	private List<Overlay> mapOverlays;
	private Drawable mapIcon;
	private CollectionOverlay collectionOverlay;
	
	private JSONObject crisis;
	
	private final Handler guiHandler = new Handler();
	private final Runnable updateCollection = new Runnable() {
		public void run() {
			 updateGUI();
		}	
	};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.crisis);
		
		try {
			crisis = new JSONObject(getIntent().getStringExtra("crisis"));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		final MapView mapView = (MapView) findViewById(R.id.mapView);
        mapView.setSatellite(true);
        mapOverlays = mapView.getOverlays();
        
        this.mapIcon = getResources().getDrawable(R.drawable.earthquake);
        this.collectionOverlay = new CollectionOverlay(mapIcon);
        
        final ProgressDialog progressDialog = ProgressDialog.show(CrisisActivity.this, null, "Loading crisis info...", false);
        
        Thread seeker = new Thread() {
        	@Override
        	public void run() {
        		Looper.prepare();
        		try {
        			mapControl = mapView.getController();
        			mapControl.setZoom(2);
        			mapControl.stopPanning();
        			
        			try {
        				String lon_str = crisis.getJSONArray("foaf_based_near").get(0).toString();
        				String lat_str = crisis.getJSONArray("foaf_based_near").get(1).toString();
        				Float lon = Float.valueOf(lon_str);
						Float lat = Float.valueOf(lat_str);
						GeoPoint geo = new GeoPoint((int)(lat * 1E6), (int)(lon * 1E6));
						mapControl.setCenter(geo);
						
						String country = null; 
						if ( crisis.getJSONArray("gn_parentCountry").length() > 0 )
							country = (String) crisis.getJSONArray("gn_parentCountry").get(0);
						
						OverlayItem overlayitem = new OverlayItem(geo, country , "");
						collectionOverlay.addOverlay(overlayitem);
					} catch (JSONException e) {
						mapControl.setCenter(new GeoPoint(0, 0));
					}        			        			        			        		
        		} finally {
        			guiHandler.post(updateCollection);
        			progressDialog.dismiss();
        		}
        	}
        };
        seeker.start();
	}
	
	private void updateGUI() {
		this.mapOverlays.add(this.collectionOverlay);
	}

	@Override
	protected boolean isRouteDisplayed() { return false; }
}

package org.sigimera.app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.sigimera.app.model.map.CollectionOverlay;

import android.app.ProgressDialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ListView;
import android.widget.SimpleAdapter;

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
	
	private static final String BOTTOM = "bottom";
	private static final String TOP = "top";
	private static final String ICON = "icon";
	private static final String ARROW = "rightArrow";
	
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
			String crisis_type = crisis.getJSONArray("dc_subject").getString(0);
			if (crisis_type.contains("flood"))
				this.mapIcon = getResources().getDrawable(R.drawable.flood);
			else if (crisis_type.contains("earthquake"))
				this.mapIcon = getResources().getDrawable(R.drawable.earthquake);
			else if (crisis_type.contains("cyclone"))
				this.mapIcon = getResources().getDrawable(R.drawable.cyclone);
			else if (crisis_type.contains("volcano"))
				this.mapIcon = getResources().getDrawable(R.drawable.volcano);
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
		ArrayList<HashMap<String, String>> collectionList = new ArrayList<HashMap<String, String>>();
		Map<String, String> map;
		
		String alertLevel = null;
		String severity = null;
		String description = null;
		try {			
			alertLevel = crisis.getString("crisis_alertLevel");
			severity = crisis.getString("crisis_severity");
			description = crisis.getString("dc_description");			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		if ( alertLevel != null )
			collectionList.add(getListEntry(alertLevel, "Alert Level", String.valueOf(R.drawable.cyclone)));
		if ( severity != null )
			collectionList.add(getListEntry(severity, "Severity", String.valueOf(R.drawable.earthquake)));
			
		
//		map.put(ICON, R.drawable. + "");
//		String divebase = entry.getDiveBase();
//		if ( divebase == null || divebase.equals("") )
//			divebase = "Collection: " + entry.getID();
//		map.put(TOP, divebase);
//		map.put(BOTTOM, entry.getCountry() + " (" + entry.getStartDate() + " - " + entry.getStopDate() + ")");
//		map.put(ARROW, R.drawable.right_arrow_icon + "");
//		collectionList.add(map);	
//				
//		try {
//			double latDouble = new Double(entry.getLatitude());
//			double lonDouble = new Double(entry.getLongitude());
//			if ( latDouble != 0.0 && lonDouble != 0.0 ) {
//				int lat = (int) (latDouble * 1E6);
//				int lon = (int) (lonDouble * 1E6);
//				GeoPoint geo = new GeoPoint(lat, lon);
//				OverlayItem overlayitem = new OverlayItem(geo, entry.getCountry(), "");
//				this.collectionOverlay.addOverlay(overlayitem);   
//			}
//		} catch (Exception e) {
//			// No GPS coordinates, no icon on the map...
//		}
//			
		ListView list = (ListView)findViewById(R.id.crisisInfoList);
		SimpleAdapter adapterCollectionList = new SimpleAdapter(CrisisActivity.this, collectionList, 
					R.layout.list_entry, new String[]{ ICON, TOP, BOTTOM, ARROW },
					new int[] { R.id.icon, R.id.topText, R.id.bottomText });
		list.setAdapter(adapterCollectionList);
	        
		this.mapOverlays.add(this.collectionOverlay);
	}
	
	private HashMap<String, String> getListEntry(String top, String bottom, String icon){
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(ICON, icon);
		map.put(TOP, top);
		map.put(BOTTOM, bottom);
		return map;
	}

	@Override
	protected boolean isRouteDisplayed() { return false; }
}

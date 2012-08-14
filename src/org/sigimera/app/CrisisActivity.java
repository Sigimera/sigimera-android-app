package org.sigimera.app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.sigimera.app.controller.Common;
import org.sigimera.app.controller.CrisesController;
import org.sigimera.app.model.map.CollectionOverlay;

import android.app.ProgressDialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class CrisisActivity extends MapActivity {
	private ListView list;
	private MapController mapControl;
	private List<Overlay> mapOverlays;
	private Drawable mapIcon;
	private CollectionOverlay collectionOverlay;
	private CrisesController crisisController = CrisesController.getInstance();

	private static final String BOTTOM = "bottom";
	private static final String TOP = "top";
	private static final String ICON = "icon";
	private static final String ARROW = "rightArrow";

	private static final int MENU_SHARE = 0x0010;
	private static final int MENU_ABOUT = 0x0020;

	private JSONObject crisis;

	private final Handler guiHandler = new Handler();
	private final Runnable updateCollection = new Runnable() {
		public void run() {
			updateGUI();
		}
	};

	private String alertLevel = null;
	private String severity = null;
	private String description = null;
	private JSONArray country = null;
	private String countryConcat = "";
	private String affectedPeople = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.crisis);

		list = (ListView) findViewById(R.id.crisis_info_list);
		list.setOnItemClickListener(this.listClickListener);

		final ProgressDialog progressDialog = ProgressDialog.show(
				CrisisActivity.this, null, "Loading crisis info...", false);

		Thread seeker = new Thread() {
			@Override
			public void run() {
				Looper.prepare();
				try {
					try {
						crisis = new JSONObject(getIntent().getStringExtra(
								"crisis"));
						String crisis_type = crisis.getJSONArray("dc_subject")
								.getString(0);
						System.out.println(crisis_type);
						if (crisis_type.contains("flood"))
							mapIcon = getResources().getDrawable(
									R.drawable.flood);
						else if (crisis_type.contains("earthquake"))
							mapIcon = getResources().getDrawable(
									R.drawable.earthquake);
						else if (crisis_type.contains("cyclone"))
							mapIcon = getResources().getDrawable(
									R.drawable.cyclone);
						else if (crisis_type.contains("volcano"))
							mapIcon = getResources().getDrawable(
									R.drawable.volcano);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					final MapView mapView = (MapView) findViewById(R.id.map_view);
					mapView.setSatellite(true);
					mapOverlays = mapView.getOverlays();

					collectionOverlay = new CollectionOverlay(mapIcon);

					mapControl = mapView.getController();
					mapControl.setZoom(2);
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

	private void updateGUI() {
		ArrayList<HashMap<String, String>> collectionList = new ArrayList<HashMap<String, String>>();

		try {
			alertLevel = crisis.getString("crisis_alertLevel");
			severity = crisis.getString("crisis_severity");
			description = crisis.getString("dc_description");
			country = crisis.getJSONArray("gn_parentCountry");
			affectedPeople = crisis.getString("crisis_population");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if ( description != null )
			collectionList.add(getListEntry(description.substring(0, 80)
					+ " ...", "Description",
					String.valueOf(R.drawable.glyphicons_030_pencil_white)));
		if ( affectedPeople != null )
			collectionList.add(getListEntry(affectedPeople, "Affected people",
					String.valueOf(R.drawable.glyphicons_024_parents_white)));
		if (alertLevel != null )
			collectionList
					.add(getListEntry(
							alertLevel,
							"Alert Level",
							String.valueOf(R.drawable.glyphicons_196_circle_exclamation_mark_white)));
		if (severity != null)
			collectionList.add(getListEntry(CrisesController.getInstance()
					.capitalize(severity), "Severity", String
					.valueOf(R.drawable.glyphicons_079_signal_white)));

		if (country != null && country.length() > 0) {

			for (int i = 0; i < country.length(); i++) {
				try {
					countryConcat += crisisController.capitalize(String
							.valueOf(country.get(i)));
					if (i != 0)
						countryConcat += ", ";
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			collectionList.add(getListEntry(countryConcat, "Country",
					String.valueOf(R.drawable.glyphicons_266_flag_white)));
		}

		try {
			String lon_str = crisis.getJSONArray("foaf_based_near").get(0)
					.toString();
			String lat_str = crisis.getJSONArray("foaf_based_near").get(1)
					.toString();
			Float lon = Float.valueOf(lon_str);
			Float lat = Float.valueOf(lat_str);

			GeoPoint geo = new GeoPoint((int) (lat * 1E6), (int) (lon * 1E6));
			OverlayItem overlayitem = new OverlayItem(geo, countryConcat, "");
			this.collectionOverlay.addOverlay(overlayitem);
		} catch (JSONException e) {
			// No GPS coordinates, no icon on the map...
		}
		this.mapOverlays.add(this.collectionOverlay);

		// Add list to the view
		SimpleAdapter adapterCollectionList = new SimpleAdapter(
				CrisisActivity.this, collectionList, R.layout.list_entry,
				new String[] { ICON, TOP, BOTTOM, ARROW }, new int[] {
						R.id.icon, R.id.topText, R.id.bottomText });
		list.setAdapter(adapterCollectionList);
	}

	/**
	 * Creating the list entry which needs to be added to the list.
	 * 
	 * @param top
	 *            The text which should be showed on top of the list entry
	 * @param bottom
	 *            The text which should be showed at the bottom of the list
	 *            entry
	 * @param icon
	 *            The icon should be in format:
	 *            String.valueOf(R.drawable.MyIcon)
	 * @return The map having an icon, top and bottom text. (list entry)
	 */
	private HashMap<String, String> getListEntry(String top, String bottom,
			String icon) {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(ICON, icon);
		map.put(TOP, top);
		map.put(BOTTOM, bottom);
		return map;
	}

	private OnItemClickListener listClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> list, View view, int position,
				long id) {
			String text = "";
			switch (position) {
			case 0:
				text = description;
				break;
			case 1:
				text = "Alert level: " + alertLevel;
				break;
			case 2:
				text = crisisController.capitalize(severity);
				break;
			case 3:
				text = "Country: " + countryConcat;
				break;
			}

			new Notification(getApplicationContext(), text, Toast.LENGTH_SHORT);
		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, MENU_SHARE, 20, "Share crisis").setIcon(
				R.drawable.glyphicons_326_share);
		menu.add(0, MENU_ABOUT, 30, "About").setIcon(R.drawable.about_icon);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_SHARE:
			try {
				this.startActivity(Common.shareCrisis(crisis.getString("_id")));
			} catch (JSONException e) {
				new Notification(getApplicationContext(),
						"Failed to read the crisis ID", Toast.LENGTH_SHORT);
			}
			return true;
		case MENU_ABOUT:
			new Notification(getApplicationContext(),
					"TODO: provide content for about window",
					Toast.LENGTH_SHORT);
			return true;
		}
		return false;
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
}

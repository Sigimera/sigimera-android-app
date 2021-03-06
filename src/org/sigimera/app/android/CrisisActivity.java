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

import java.util.List;

import org.sigimera.app.android.controller.DistanceController;
import org.sigimera.app.android.controller.LocationController;
import org.sigimera.app.android.controller.PersistanceController;
import org.sigimera.app.android.model.Constants;
import org.sigimera.app.android.model.Crisis;
import org.sigimera.app.android.model.map.CollectionOverlay;
import org.sigimera.app.android.util.Common;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

import android.app.ProgressDialog;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.ShareActionProvider;
import android.widget.Toast;

/**
 * @author Corneliu-Valentin Stanciu, Alex Oberhauser
 * @e-mail corneliu.stanciu@sigimera.org, alex.oberhauser@sigimera.org
 */
public class CrisisActivity extends MapActivity {

	/**
	 * The crisis instance.
	 */
	private Crisis crisis;

	/**
	 * The google map controller.
	 */
	private MapController mapControl;

	/**
	 * Map overlays for adding items on the map.
	 */
	private List<Overlay> mapOverlays;

	/**
	 * The collections where the items are added.
	 */
	private CollectionOverlay collectionOverlay;

	/**
	 * GUI handler needed for starting the thread in background.
	 */
	private final Handler guiHandler = new Handler();

	/**
	 * Method which is stared in background for updating the GUI.
	 */
	private final Runnable updateCollection = new Runnable() {
		public void run() {
			try {
				updateMap();
			} catch ( Exception e ) {
				System.out.println("Not able to update map, through exception: " + e.toString());
			}
		}
	};

	@Override
	protected final void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.crisis_info);
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		final ProgressDialog progressDialog = ProgressDialog.show(
				CrisisActivity.this, null, "Loading crisis map...", false);

		Thread seeker = new Thread() {
			@Override
			public void run() {
				Looper.prepare();
				try {
					String crisisID = getIntent().getStringExtra(
							Constants.CRISIS_ID);
					String authToken = getSharedPreferences(
							Constants.PREFS_NAME, 0).getString("auth_token",
							null);

					if (null != crisisID) {
						crisis = PersistanceController.getInstance().getCrisis(
								authToken, crisisID);
					} else {
						crisis = PersistanceController.getInstance()
								.getLatestCrisis(authToken);
					}

					final MapView mapView = (MapView) findViewById(R.id.mapview);
					mapView.setSatellite(true);

					mapOverlays = mapView.getOverlays();
					collectionOverlay = new CollectionOverlay(getResources()
							.getDrawable(
									Common.getCrisisIcon(crisis.getSubject())));

					mapControl = mapView.getController();
					mapControl.setZoom(4);
					mapControl.stopPanning();
					mapControl.setCenter(new GeoPoint(0, 0));

					WebView infoview = (WebView) findViewById(R.id.crisis_info_webview);
					infoview.setBackgroundColor(Color.BLACK);

					Location userLocation = LocationController.getInstance()
							.getLastKnownLocation();

					StringBuffer content = new StringBuffer();
					content.append("<html>");
					content.append("<body style='color: white;'>");

					content.append(getCrisisHTMLContent(userLocation));

					content.append("<br />");
					content.append("</body>");
					content.append("</html>");
					infoview.loadData(content.toString(), "text/html", "UTF-8");

				} finally {
					progressDialog.dismiss();
				}
				guiHandler.post(updateCollection);
			}
		};
		seeker.start();
	}

	@Override
	public final boolean onCreateOptionsMenu(final Menu menu) {
		getMenuInflater().inflate(R.menu.crisis_info_menu, menu);
		MenuItem menuItem = menu.findItem(R.id.menu_share);
		ShareActionProvider mShareActionProvider = (ShareActionProvider) menuItem
				.getActionProvider();
		mShareActionProvider
				.setShareHistoryFileName("crisis_share_history.xml");
		if (crisis == null) {
			String authToken = getSharedPreferences(Constants.PREFS_NAME, 0)
					.getString("auth_token", null);
			crisis = PersistanceController.getInstance().getLatestCrisis(
					authToken);
		}
		mShareActionProvider.setShareIntent(Common.shareCrisis(crisis.getID(),
				crisis.getShortTitle()));

		return super.onCreateOptionsMenu(menu);
	}

	/**
	 * Retrieves the content of a crisis as HTML blob.
	 * 
	 * @param userLocation
	 *            The users location
	 * @return The content of crisis as HTML blob
	 */
	private String getCrisisHTMLContent(final Location userLocation) {
		if (null == crisis) {
			return "No crisis found!";
		}

		StringBuffer content = new StringBuffer();

		// Show crisis title
		content.append("<i>" + crisis.getShortTitle() + "</i>");
		content.append("<hr />");

		// Show distance, date and severity|population|subject in a table
		content.append("<table width='100%'>");
		content.append("<tr>");
		content.append(this.getTableHTMLContent(
				DistanceController.computeDistance(userLocation.getLatitude(),
						userLocation.getLongitude(), crisis.getLatitude(),
						crisis.getLongitude())
						+ "km", "Distance"));
		content.append(this.getTableHTMLSeparator());
		content.append(this.getTableHTMLContent(Common.getTimeAgoInWords(Common
				.getMiliseconds(crisis.getDate())), "Date"));
		content.append(this.getTableHTMLSeparator());

		if (crisis.getSeverityHashValue() != null
				&& crisis.getSeverityHashUnit() != null) {
			content.append(this.getTableHTMLContent(
					crisis.getSeverityHashValue()
							+ crisis.getSeverityHashUnit(), "Severity"));
		} else if (crisis.getPopulationHashValue() != null
				&& crisis.getPopulationHashUnit() != null) {
			content.append(this.getTableHTMLContent(
					crisis.getPopulationHashValue()
							+ crisis.getPopulationHashUnit(), "Affected people"));
		} else {
			content.append(this.getTableHTMLContent(
					Common.capitalize(crisis.getSubject()), "Type"));
		}
		content.append("</tr>");
		content.append("</table>");

		content.append("<hr />");

		// Show the crisis descrioption
		content.append("<p style='text-align: justify'><small>"
				+ crisis.getDescription() + "</small></p>");

		// Show the start and end dates
		if (crisis.getPopulationHashValue() != null) {
			content.append("<table width='100%'>");
			content.append("<tr>");
			content.append(this.getTableHTMLContent(
					crisis.getPopulationHashValue(), "Affected people"));
			content.append(this.getTableHTMLSeparator());
			content.append(this.getTableHTMLContent(crisis.getStartDate(),
					"Start date"));
			content.append("</tr>");
			content.append("</table>");
		}

		return content.toString();
	}

	/**
	 * Update the map with the coordinates of the forwarded crisis.
	 */
	private void updateMap() {
		GeoPoint geo = new GeoPoint((int) (crisis.getLatitude() * 1E6),
				(int) (crisis.getLongitude() * 1E6));
		mapControl.setCenter(geo);
		OverlayItem overlayitem = new OverlayItem(geo, "", "");
		this.collectionOverlay.addOverlay(overlayitem);
		this.mapOverlays.add(this.collectionOverlay);
	}

	/**
	 * Retrieve the content of crisis in a table HTML blob.
	 *  
	 * @param content The crisis content
	 * @param helpText The text which will be showed beyond the content.
	 * @return The crisis content as HTML table
	 */
	private String getTableHTMLContent(final String content,
			final String helpText) {
		StringBuffer element = new StringBuffer();
		element.append("<td>");
		element.append(content + "<br/>");
		element.append("<small style='color: #00FFFF;'><small>" + helpText
				+ "</small></small>");
		element.append("</td>");
		return element.toString();
	}

	/**
	 * 
	 * @return an HTML table separator (as row - <td>).
	 */
	private String getTableHTMLSeparator() {
		return "<td style='border-left: solid 1px white'></td>";
	}

	@Override
	protected final boolean isRouteDisplayed() { return false; }
}

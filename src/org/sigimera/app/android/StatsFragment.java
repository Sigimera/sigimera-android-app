package org.sigimera.app.android;

import org.sigimera.app.android.controller.DistanceController;
import org.sigimera.app.android.controller.LocationController;
import org.sigimera.app.android.model.Constants;
import org.sigimera.app.android.model.Crisis;
import org.sigimera.app.android.util.Common;

import android.annotation.SuppressLint;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

public class StatsFragment extends Fragment {

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@SuppressLint("NewApi")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.stats_fragment, container, false);

		WebView statWebview = (WebView) view.findViewById(R.id.stat_webview);

		Crisis crisis = (Crisis) getArguments().getSerializable("crisis");
		String style = (String) getArguments().getSerializable("style");
		
		if ( crisis != null ) {
			Location userLocation = LocationController.getInstance().getLastKnownLocation();
			if (userLocation != null) {

				StringBuffer content = new StringBuffer();
				content.append("<html>");
				content.append("<body style='background: #545959; color: white;'>");
				content.append("<table width='100%'>");
				content.append("<tr>");
				
				if ( style.equalsIgnoreCase(Constants.NEAR_CRISIS) )
					content.append(this.getNearCrisisHTMLContent(crisis));
				else if ( style.equalsIgnoreCase(Constants.LATEST_CRISIS) )
					content.append(this.getLatestCrisisHTMLContent(crisis, userLocation));

				content.append("</tr>");
				content.append("</table>");
				content.append("<br />");

				if (Common.hasInternet()) {
					content.append("<img width='100%' src='http://maps.googleapis.com/maps/api/staticmap?markers=icon:"
							+ Common.getCrisisIconURL(crisis.getSubject())
							+ "|"
							+ crisis.getLatitude()
							+ ","
							+ crisis.getLongitude()
							+ "&markers="
							+ userLocation.getLatitude()
							+ ","
							+ userLocation.getLongitude()
							+ "&size=500x180&scale=2&sensor=true' />");
				} else {
					content.append(this.getHTMLError(
						"No connection detected.", 
						"In order to show the distance from your location to the nearest crisis on map, please turn on the internet on this device."));
				}
				content.append("</body>");
				content.append("</html>");
				statWebview.loadData(content.toString(), "text/html", "UTF-8");
			} else {
				statWebview.loadData(this.getHTMLError(
						"Not able to get your current location", 
						"In order to show the distance from your location to the nearest crisis on map, please turn on the location access."), 
						"text/html", "UTF-8");
			}
		} else {
			statWebview.loadData(this.getHTMLError(
					"There are no crises near you",  
					"In order to see crises near you, you have to enable and extend the radius on PROFILE tab."),
					"text/html", "UTF-8");
		}

		return view;
	}

	private String getNearCrisisHTMLContent(Crisis crisis) {
		StringBuffer content = new StringBuffer();		
		content.append(this.getTableHTMLContent(Common.getTimeAgoInWords(Common.getMiliseconds(crisis.getDate())), "Date"));					
		content.append(this.getHTMLSeparator());		
		content.append(this.getTableHTMLContent(Common.capitalize(crisis.getAlertLevel()), "Alert Level"));		
		content.append(this.getHTMLSeparator());
		
		if ( crisis.getSeverityHashValue() != null && crisis.getSeverityHashUnit() != null )
			content.append(this.getTableHTMLContent(crisis.getSeverityHashValue() + crisis.getSeverityHashUnit(), "Severity"));				
		else if ( crisis.getPopulationHashValue() != null && crisis.getPopulationHashUnit() != null )
			content.append(this.getTableHTMLContent(crisis.getPopulationHashValue() + crisis.getPopulationHashUnit(), "Affected people"));
		else if ( !crisis.getCountries().isEmpty() )
			content.append(this.getTableHTMLContent(Common.capitalize(crisis.getCountries().get(0)), "Country"));
		else
			content.append(this.getTableHTMLContent(Common.capitalize(crisis.getSubject()), "Type"));
				
		return content.toString();
	}

	private String getLatestCrisisHTMLContent(Crisis crisis, Location userLocation) {		
		StringBuffer content = new StringBuffer();		
		content.append(this.getTableHTMLContent(DistanceController.computeDistance(
				userLocation.getLatitude(), userLocation.getLongitude(), 
				crisis.getLatitude(), crisis.getLongitude())+ "km", "Distance"));					
		content.append(this.getHTMLSeparator());		
		content.append(this.getTableHTMLContent(Common.capitalize(crisis.getAlertLevel()), "Alert Level"));		
		content.append(this.getHTMLSeparator());
		
		if ( crisis.getSeverityHashValue() != null && crisis.getSeverityHashUnit() != null )
			content.append(this.getTableHTMLContent(crisis.getSeverityHashValue() + crisis.getSeverityHashUnit(), "Severity"));				
		else if ( crisis.getPopulationHashValue() != null && crisis.getPopulationHashUnit() != null )
			content.append(this.getTableHTMLContent(crisis.getPopulationHashValue() + crisis.getPopulationHashUnit(), "Affected people"));
		else if ( !crisis.getCountries().isEmpty() )
			content.append(this.getTableHTMLContent(Common.capitalize(crisis.getCountries().get(0)), "Country"));
		else
			content.append(this.getTableHTMLContent(Common.capitalize(crisis.getSubject()), "Type"));
				
		return content.toString();
	}
	
	private String getTableHTMLContent(String content, String helpText) {
		StringBuffer element = new StringBuffer();
		element.append("<td>");
		element.append(content + "<br/>");
		element.append("<small><small>" + helpText + "</small></small>");
		element.append("</td>");
		return element.toString();
	}	
	
	private String getHTMLSeparator() {
		return "<td style='border-left: solid 1px white'></td>";
	}
	
	private String getHTMLError(String title, String message) {
		StringBuffer content = new StringBuffer();
		content.append("<html>");
		content.append("<body style='background: #545959; color: white;'>");
		content.append("<h3>" + title + "</h3>");
		content.append("<small>" + message + "</small>");
		content.append("</body>");
		content.append("</html>");
		return content.toString();
	}
}

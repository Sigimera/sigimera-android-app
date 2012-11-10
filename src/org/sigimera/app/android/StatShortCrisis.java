package org.sigimera.app.android;

import org.sigimera.app.android.controller.LocationController;
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

public class StatShortCrisis extends Fragment{

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}
	
	@SuppressLint("NewApi")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.stat_short_crisis, container, false);
			
		Crisis crisis = (Crisis) getArguments().getSerializable("crisis");
		
		//TODO: show the direction of user with respect to the crisis location ("don't go in that direction ... it could be dangerous :)")
//		Location crisislocation = new Location("");
//		crisislocation.setLatitude(crisis.getLatitude());
//		crisislocation.setLongitude(crisis.getLongitude());		
//		
		Location userLocation = LocationController.getInstance().getLastKnownLocation();
		if ( userLocation != null ) {
			userLocation.getBearing();		
			
			WebView statWebview = (WebView) view.findViewById(R.id.stat_webview);
			StringBuffer content = new StringBuffer();
			content.append("<html>");
			content.append("<body style='background: #545959; color: white;'>");
			content.append("<table width='100%'>");
			content.append("<tr>");
			
	//		content.append("<td>");
	//		content.append(crisis.getSubject() + "<br/>");
	//		content.append("<small><small>Type</small></small>");
	//		content.append("</td>");
	//		
	//		content.append("<td style='border-left: solid 1px white'></td>");
			
	//		content.append("<td>");
	//		content.append(crisis.getAlertLevel() + "<br/>");
	//		content.append("<small><small>Alert level</small></small>");
	//		content.append("</td>");
	//		
	//		content.append("<td style='border-left: solid 1px white'></td>");
			
			content.append("<td>");
			content.append(crisis.getCountries().get(0) + "<br/>");
			content.append("<small><small>Country</small></small>");
			content.append("</td>");
			
			content.append("<td style='border-left: solid 1px white'></td>");
			
			content.append("<td>");
			content.append("24442<br/>");
			content.append("<small><small>Affected people</small></small>");
			content.append("</td>");
			
			content.append("<td style='border-left: solid 1px white'></td>");
			
			content.append("<td>");				
			content.append("6.2<br/>");
			content.append("<small><small>Magnitude</small></small>");
			content.append("</td>");
			content.append("");	
							
			content.append("</tr>");
			content.append("</table>");
			content.append("<br />");
			if ( Common.hasInternet() )
				content.append("<img width='100%' src='http://maps.googleapis.com/maps/api/staticmap?markers=icon:http://maps.google.com/mapfiles/ms/micons/blue.png|" + crisis.getLatitude() + "," + crisis.getLongitude() + "&markers=" + userLocation.getLatitude() + "," + userLocation.getLongitude() + "&size=500x180&scale=2&sensor=true' />");
			else{
				content.append("<h3>No connection detected.</h3>");
				content.append("<small>In order to show the distance from your location to the nearest crisis on map, please turn on the internet on this device.</small>");
			}
			content.append("</body>");
			content.append("</html>");
					
			statWebview.loadData(content.toString(), "text/html", "UTF-8");
		} else {
			WebView statWebview = (WebView) view.findViewById(R.id.stat_webview);
			StringBuffer content = new StringBuffer();
			content.append("<html>");
			content.append("<body style='background: #545959; color: white;'>");
			content.append("<h3>Not able to get your current location</h3>");
			content.append("<small>In order to show the distance from your location to the nearest crisis on map, please turn on the location access.</small>");
			content.append("</body>");
			content.append("</html>");
			
			statWebview.loadData(content.toString(), "text/html", "UTF-8");
		}
		
		
		return view;
	}
}

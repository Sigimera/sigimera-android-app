package org.sigimera.app.android;

import org.sigimera.app.android.controller.LocationController;
import org.sigimera.app.android.model.Constants;
import org.sigimera.app.android.model.Crisis;
import org.sigimera.app.android.util.Common;

import android.annotation.SuppressLint;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

public class NearCrisisFragment extends Fragment{

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}
	
	@SuppressLint("NewApi")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.near_crisis, container, false);
			
		WebView statWebview = (WebView) view.findViewById(R.id.stat_webview);
		
		Crisis crisis = (Crisis) getArguments().getSerializable("crisis");	
		if ( crisis != null ) {
		
			//TODO: show the direction of user with respect to the crisis location ("don't go in that direction ... it could be dangerous :)")
	//		Location crisislocation = new Location("");
	//		crisislocation.setLatitude(crisis.getLatitude());
	//		crisislocation.setLongitude(crisis.getLongitude());		
	//		
			Location userLocation = LocationController.getInstance().getLastKnownLocation();
			if ( userLocation != null ) {
				userLocation.getBearing();		
				
				StringBuffer content = new StringBuffer();
				content.append("<html>");
				content.append("<body style='background: #545959; color: white;'>");
				content.append("<table width='100%'>");
				content.append("<tr>");
				
				// If there is no country => show date
				if ( crisis.getCountries() != null && !crisis.getCountries().isEmpty() ) {
					content.append("<td>");
					content.append(Common.capitalize(crisis.getCountries().get(0)) + "<br/>");
					content.append("<small><small>Country</small></small>");
					content.append("</td>");
				} else {
					content.append("<td>");
					content.append(Common.getTimeAgoInWords(Common.getMiliseconds(crisis.getDate())) + "<br/>");
					content.append("<small><small>Date</small></small>");
					content.append("</td>");
				}
				
				content.append("<td style='border-left: solid 1px white'></td>");
				
				// If there is no affected people hash saved => show the date		
				if ( !crisis.getPopulationHashValue().contains("null") ) {
					content.append("<td>");
					content.append( crisis.getPopulationHashValue() + "<br/>");
					content.append("<small><small>Affected people</small></small>");
					content.append("</td>");
				} else {
					content.append("<td>");
					content.append(Common.getTimeAgoInWords(Common.getMiliseconds(crisis.getDate())) + "<br/>");
					content.append("<small><small>Date</small></small>");
					content.append("</td>");
				}
				
				content.append("<td style='border-left: solid 1px white'></td>");
				
				// If there is no severity hash saved => show the crisis type			
				if ( !crisis.getSeverityHashValue().contains("null") ) {
					content.append("<td>");
					content.append(crisis.getSeverityHashValue() + "<br/>");
					content.append("<small><small>Magnitude</small></small>");
					content.append("</td>");
					content.append("");
				} else {
					content.append("<td>");
					content.append(Common.capitalize(crisis.getSubject()) + "<br/>");
					content.append("<small><small>Crisis Type</small></small>");
					content.append("</td>");
					content.append("");
				}
	
				content.append("</tr>");
				content.append("</table>");
				content.append("<br />");
				
				if ( Common.hasInternet() ){
					content.append("<img width='100%' src='http://maps.googleapis.com/maps/api/staticmap?markers=icon:" + Common.getCrisisIconURL(crisis.getSubject()) + "|" + crisis.getLatitude() + "," + crisis.getLongitude() + "&markers=" + userLocation.getLatitude() + "," + userLocation.getLongitude() + "&size=500x180&scale=2&sensor=true' />");
				}else {
					content.append("<h3>No connection detected.</h3>");
					content.append("<small>In order to show the distance from your location to the nearest crisis on map, please turn on the internet on this device.</small>");
				}
				content.append("</body>");
				content.append("</html>");
						
				statWebview.loadData(content.toString(), "text/html", "UTF-8");
			} else {
				StringBuffer content = new StringBuffer();
				content.append("<html>");
				content.append("<body style='background: #545959; color: white;'>");
				content.append("<h3>Not able to get your current location</h3>");
				content.append("<small>In order to show the distance from your location to the nearest crisis on map, please turn on the location access.</small>");
				content.append("</body>");
				content.append("</html>");
				
				statWebview.loadData(content.toString(), "text/html", "UTF-8");
			}
		} else {
			StringBuffer content = new StringBuffer();
			content.append("<html>");
			content.append("<body style='background: #545959; color: white;'>");
			content.append("<h3>Not able to get a crises near you</h3>");
			content.append("<small>In order to see crises near you, you have to update your location (see location button on the top)</small>");
			content.append("</body>");
			content.append("</html>");
			
			statWebview.loadData(content.toString(), "text/html", "UTF-8");
		}
		
		return view;
	}
}

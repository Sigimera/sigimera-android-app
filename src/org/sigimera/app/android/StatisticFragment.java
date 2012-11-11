package org.sigimera.app.android;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.sigimera.app.android.R;
import org.sigimera.app.android.controller.ApplicationController;
import org.sigimera.app.android.controller.CrisesController;
import org.sigimera.app.android.controller.DistanceController;
import org.sigimera.app.android.controller.LocationController;
import org.sigimera.app.android.exception.AuthenticationErrorException;
import org.sigimera.app.android.model.CrisesStats;
import org.sigimera.app.android.model.Crisis;
import org.sigimera.app.android.util.Common;

import android.database.Cursor;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

public class StatisticFragment extends Fragment {

	private Crisis latestCrisis = null;
	private Crisis nearCrisis = null;
	private Cursor todayCrises = null;
	private String auth_token = null;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.statistic, container, false);		
		
		Location userLocation = LocationController.getInstance().getLastKnownLocation();
				
		try {			
			this.auth_token = ApplicationController.getInstance().getSessionHandler().getAuthenticationToken();	
			this.latestCrisis = CrisesController.getInstance().getLatestCrisis(this.auth_token);
			this.nearCrisis = CrisesController.getInstance().getNearCrisis(this.auth_token, userLocation);
		} catch (AuthenticationErrorException e) {
			//TODO: send to login window
			System.err.println("Error on authentification" + e.getLocalizedMessage());
		}
		
		// Set distance in km until the near crisis 
		if ( this.auth_token != null ) { 
			Button nearCrisisButton = (Button) view.findViewById(R.id.button0);			
			double nearDistance = DistanceController.getNearCrisisDistance(this.auth_token, this.nearCrisis, userLocation);
			if ( nearDistance != -1.0 )
				nearCrisisButton.setText(Html.fromHtml(nearDistance + " km" + "<br/><small><i>" + "Near crisis" + "</i></small>"));
			else
				nearCrisisButton.setText(Html.fromHtml("unknown<br/><small><i>" + "No near crisis" + "</i></small>"));
			nearCrisisButton.setOnClickListener(this.nearCrisisListenter);
		}
		
		// Set the number of crises today
		if ( this.auth_token != null ) { 
			Cursor c = CrisesController.getInstance().getTodayCrises(this.auth_token);
			
			Button todayCrisesButton = (Button) view.findViewById(R.id.button1);
			if ( c.getCount() == 0 )
				todayCrisesButton.setText(Html.fromHtml("No Crises<br/><small><i>" + "Today" + "</i></small>"));
			else 
				todayCrisesButton.setText(Html.fromHtml(c.getCount() + " Crises<br/><small><i>" + "Today" + "</i></small>"));
			this.todayCrises = c;
			todayCrisesButton.setOnClickListener(this.todayCrisesListenter);
		}
		
		// Set the time ago since latest crisis
		if ( this.latestCrisis != null ) {
			Button latestCrisisButton = (Button) view.findViewById(R.id.button2);
			latestCrisisButton.setText(Html.fromHtml(Common.getTimeAgoInWordsSplitted(Common.getMiliseconds(this.latestCrisis.getDate())) + "<br/><small><i>" + "Latest crisis" + "</i></small>"));
			latestCrisisButton.setOnClickListener(this.latestCrisisListenter);
		}
		
		// Set total number of crises
		Button totalCrisesButton = (Button) view.findViewById(R.id.button3);		
		CrisesStats stats = CrisesController.getInstance().getCrisesStats(this.auth_token);
		if ( stats != null ) {
			SimpleDateFormat inputFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
			SimpleDateFormat outputFormatter = new SimpleDateFormat("d. MMMM yyyy");
			Date date = new Date();
			try {
				date = inputFormatter.parse(stats.getFirstCrisisAt());
			} catch ( Exception e) {
				e.printStackTrace();
			}
			totalCrisesButton.setText(Html.fromHtml(stats.getTotalCrises() + " crises since<br/><small><i>" + outputFormatter.format(date) + "</i></small>"));
			totalCrisesButton.setOnClickListener(this.allCrisesListenter);
		}

		//Show one view at start
		Fragment nearCrisisFrament = new NearCrisisFragment();	
		Bundle bundle = new Bundle();
        bundle.putSerializable("crisis", this.nearCrisis);	        
        nearCrisisFrament.setArguments(bundle);        
		showFragment(nearCrisisFrament);
		
		return view;
	}
	
	private void showFragment(Fragment _newFragment) {
		FragmentManager fragManager = getFragmentManager();
		FragmentTransaction fragTransaction = fragManager.beginTransaction();
		fragTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        fragTransaction.replace(R.id.main_frag_container, _newFragment);
        fragTransaction.commit();
	}

	// Near crisis button listener
	private OnClickListener nearCrisisListenter = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Fragment nearCrisisFrament = new NearCrisisFragment();
			
			Bundle bundle = new Bundle();
	        bundle.putSerializable("crisis", nearCrisis);	        
	        nearCrisisFrament.setArguments(bundle);
	        
			showFragment(nearCrisisFrament);
		}
	};

	// Today crises button listener
	private OnClickListener todayCrisesListenter = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Fragment todayCrisesFrament = new CrisesListFragment();	
			
			Bundle bundle = new Bundle();
	        bundle.putSerializable("crises", todayCrises.toString());	        
	        todayCrisesFrament.setArguments(bundle);
	        
			showFragment(todayCrisesFrament);
		}
	};

	// Latest crisis button listener
	private OnClickListener latestCrisisListenter = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Fragment latestCrisisFrament = new NearCrisisFragment();	
			
			Bundle bundle = new Bundle();
	        bundle.putSerializable("crisis", latestCrisis);	        
	        latestCrisisFrament.setArguments(bundle);
	        
			showFragment(latestCrisisFrament);
		}
	};

	// All crises button listener
	private OnClickListener allCrisesListenter = new OnClickListener() {
		@Override
		public void onClick(View v) {				        
			showFragment(new CrisesListFragment());
		}
	};
}

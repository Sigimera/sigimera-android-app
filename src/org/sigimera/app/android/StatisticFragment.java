package org.sigimera.app.android;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.ocpsoft.pretty.time.PrettyTime;
import org.sigimera.app.android.R;
import org.sigimera.app.android.controller.ApplicationController;
import org.sigimera.app.android.controller.CrisesController;
import org.sigimera.app.android.controller.DistanceController;
import org.sigimera.app.android.controller.LocationController;
import org.sigimera.app.android.exception.AuthenticationErrorException;
import org.sigimera.app.android.model.Crisis;

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
import android.widget.CompoundButton;
import android.widget.Switch;

public class StatisticFragment extends Fragment implements OnClickListener{

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.statistic, container, false);		
		
		Location userLocation = LocationController.getInstance().getLastKnownLocation();
		
		Crisis latestCrisis = null;
		Crisis nearCrisis = null;
		String auth_token = null;
		try {			
			auth_token = ApplicationController.getInstance().getSessionHandler().getAuthenticationToken();	
			latestCrisis = CrisesController.getInstance().getLatestCrisis(auth_token);
			nearCrisis = CrisesController.getInstance().getNearCrisis(auth_token, userLocation);
			
			System.err.println(CrisesController.getInstance().getNearCrises(auth_token, 1, userLocation));
		} catch (AuthenticationErrorException e) {
			//TODO: send to login window
			System.err.println("Error on authentification" + e.getLocalizedMessage());
		}
		
		/**
		 * TODO: save the near crisis in persistence over CrisisController
		 */
		if ( auth_token != null ) { 
			Button nearCrisisButton = (Button) view.findViewById(R.id.button0);
			nearCrisisButton.setText(Html.fromHtml(DistanceController.getNearCrisisDistance(auth_token, nearCrisis, userLocation) + " km" + "<br/><small><i>" + "Near crisis" + "</i></small>"));
			nearCrisisButton.setOnClickListener(this);
		}
		
		Button todayCrisesButton = (Button) view.findViewById(R.id.button1);
		todayCrisesButton.setText(Html.fromHtml("7 crises" + "<br/><small><i>" + "Today" + "</i></small>"));
		
		if ( latestCrisis != null ) {
			Button latestCrisisButton = (Button) view.findViewById(R.id.button2);
			latestCrisisButton.setText(Html.fromHtml(getTimeAgoInWords(getMiliseconds(latestCrisis.getDate())) + "<br/><small><i>" + "Latest crisis" + "</i></small>"));
		}
		
		Button totalCrises = (Button) view.findViewById(R.id.button3);
		totalCrises.setText(Html.fromHtml("320" + " crises since<br/><small><i>" + "1 Aug. 2012" + "</i></small>"));								
		

        FragmentManager fragManager = getFragmentManager();
        FragmentTransaction fragTransaction = fragManager.beginTransaction();
        Fragment frag = new StatShortCrisis();
        Bundle bundle = new Bundle();
        bundle.putSerializable("crisis", nearCrisis);
        frag.setArguments(bundle);
        fragTransaction.add(R.id.main_frag_container, frag);
        fragTransaction.commit();
		
		return view;
	}
	
	/**
	 * Convert date into milliseconds.
	 * 
	 * @param crisisDate
	 * @return
	 */
	private long getMiliseconds(String crisisDate) {		  
		try {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm"); 
			Date date = (Date) formatter.parse(crisisDate);
			Calendar cal=Calendar.getInstance();
			cal.setTime(date);			
			return cal.getTimeInMillis();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 				
		return 0;
	}
	
	/**
	 * Convert time from milliseconds in time ago.
	 * 
	 * @param miliseconds
	 * @return
	 */
	private String getTimeAgoInWords(long miliseconds) {		
		PrettyTime p = new PrettyTime();
		return p.format(new Date(Long.parseLong("1348488000000")));
	}

	@Override
	public void onClick(View v) {
		Button tmpView = (Button) v;
		System.out.println("DEBUG");
	}
}

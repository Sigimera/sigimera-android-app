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
import org.sigimera.app.android.exception.AuthenticationErrorException;
import org.sigimera.app.android.model.Crisis;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class StatisticFragment extends Fragment {

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@SuppressLint("NewApi")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.statistic, container, false);

		Button nearCrisis = (Button) view.findViewById(R.id.button0);
		nearCrisis.setText(Html.fromHtml(DistanceController.getNearCrisisDistance() + " km" + "<br/><small><i>" + "Near crisis" + "</i></small>"));
		
		Button todayCrises = (Button) view.findViewById(R.id.button1);
		todayCrises.setText(Html.fromHtml("7" + "<br/><small><i>" + "Today crises" + "</i></small>"));
		
		Button latestCrisis = (Button) view.findViewById(R.id.button2);
		
		
		Button totalCrises = (Button) view.findViewById(R.id.button3);
		totalCrises.setText(Html.fromHtml("320" + "<br/><small><i>" + "Total crises" + "</i></small>"));
		try {
			String auth_token = ApplicationController.getInstance().getSessionHandler().getAuthenticationToken();
			Crisis crisis = CrisesController.getInstance().getLatestCrisis(auth_token);
			
			latestCrisis.setText(Html.fromHtml(getTimeAgoInWords(getMiliseconds(crisis.getDate())) + "<br/><small><i>" + "Latest crisis" + "</i></small>"));
		} catch (AuthenticationErrorException e) {
			//TODO: send to login window
			System.err.println("Error on authentification" + e.getLocalizedMessage());
		}
		
		
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
}

package org.sigimera.app.android;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.sigimera.app.android.R;
import org.sigimera.app.android.controller.ApplicationController;
import org.sigimera.app.android.controller.CrisesController;
import org.sigimera.app.android.controller.DistanceController;
import org.sigimera.app.android.controller.LocationController;
import org.sigimera.app.android.exception.AuthenticationErrorException;
import org.sigimera.app.android.model.Constants;
import org.sigimera.app.android.model.CrisesStats;
import org.sigimera.app.android.model.Crisis;
import org.sigimera.app.android.util.Common;

import android.app.ProgressDialog;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

public class StatisticFragment extends Fragment {

	private Crisis latestCrisis = null;
	private Crisis nearCrisis = null;
//	private Cursor todayCrises = null;
	private String auth_token = null;
	
	private View view;
    private Location userLocation;
    
    private ProgressDialog progessDialog = null;

    private final Handler guiHandler = new Handler();
    private final Runnable updateGUI = new Runnable() {
        @Override
        public void run() { updateStatistics(); }
    };

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.statistic, container, false);
		
		progessDialog = ProgressDialog.show(getActivity(), "Preparing crises information!", 
        		"Please be patient until the information are ready...");
        Thread worker = new Thread() {
            @Override
            public void run() {
                try {
                	Looper.prepare();
                    userLocation = LocationController.getInstance().getLastKnownLocation();

                    auth_token = ApplicationController.getInstance().getSessionHandler().getAuthenticationToken();
                    latestCrisis = CrisesController.getInstance().getLatestCrisis(auth_token);
                    nearCrisis = CrisesController.getInstance().getNearCrisis(auth_token, userLocation);

                    guiHandler.post(updateGUI);
                } catch (AuthenticationErrorException e) {
                    // SHOULD NEVER OCCUR: Check before calling this window.
                    Log.e(Constants.LOG_TAG_SIGIMERA_APP, "Error on authentification" + e.getLocalizedMessage());
                }
            }
        };
        worker.start();
		
        return view;        
	}
	
	private void updateStatistics() {
        if ( auth_token != null ) {

        	// Set distance in km until the near crisis 
        	Button nearCrisisButton = (Button) view.findViewById(R.id.button0);			
			double nearDistance = DistanceController.getNearCrisisDistance(this.auth_token, this.nearCrisis, userLocation);
			if ( nearDistance != -1.0 )
				nearCrisisButton.setText(Html.fromHtml(nearDistance + " km" + "<br/><small><i>" + "Near crisis" + "</i></small>"));
			else
				nearCrisisButton.setText(Html.fromHtml("unknown<br/><small><i>" + "No near crisis" + "</i></small>"));
			nearCrisisButton.setOnClickListener(this.nearCrisisListenter);
        	
			// Set the number of crises today
			ArrayList<Crisis> crises = CrisesController.getInstance().getTodayCrises(this.auth_token);			
			Button todayCrisesButton = (Button) view.findViewById(R.id.button1);
			if ( crises.size() == 0 ) {
				todayCrisesButton.setEnabled(false);
				todayCrisesButton.setText(Html.fromHtml("No Crises<br/><small><i>" + "Today" + "</i></small>"));
			} else 
				todayCrisesButton.setText(Html.fromHtml(crises.size() + " Crises<br/><small><i>" + "Today" + "</i></small>"));
//			this.todayCrises = c;
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
     			Fragment nearCrisisFrament = new StatsFragment();	
     			Bundle bundle = new Bundle();
     	        bundle.putSerializable("crisis", this.nearCrisis);
     	        bundle.putSerializable("style", Constants.NEAR_CRISIS);
     	        nearCrisisFrament.setArguments(bundle);        
     			showFragment(nearCrisisFrament);     			
	}
	
	private void showFragment(Fragment _newFragment) {
		FragmentManager fragManager = getFragmentManager();
		FragmentTransaction fragTransaction = fragManager.beginTransaction();
		fragTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        fragTransaction.replace(R.id.main_frag_container, _newFragment);
        fragTransaction.commit();
        closingProgressDialog();
	}
	
	public void closingProgressDialog() {
    	if ( progessDialog != null ) {
    		progessDialog.dismiss();
    		progessDialog = null;
    	}
    }
	

	// Near crisis button listener
	private OnClickListener nearCrisisListenter = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Fragment nearCrisisFrament = new StatsFragment();
			
			Bundle bundle = new Bundle();
	        bundle.putSerializable("crisis", nearCrisis);
	        bundle.putSerializable("style", Constants.NEAR_CRISIS);
	        nearCrisisFrament.setArguments(bundle);
	        
			showFragment(nearCrisisFrament);
		}
	};

	// Today crises button listener
	private OnClickListener todayCrisesListenter = new OnClickListener() {
		@Override
		public void onClick(View v) {
			/**
			 * XXX: This shows the complete crises list...
			 */
//			Fragment todayCrisesFrament = new CrisesListFragment();	
//			
//			Bundle bundle = new Bundle();
//	        bundle.putSerializable("crises", todayCrises.toString());	        
//	        todayCrisesFrament.setArguments(bundle);
//	        
//			showFragment(todayCrisesFrament);
		}
	};

	// Latest crisis button listener
	private OnClickListener latestCrisisListenter = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Fragment latestCrisisFrament = new StatsFragment();	
			
			Bundle bundle = new Bundle();
	        bundle.putSerializable("crisis", latestCrisis);
	        bundle.putSerializable("style", Constants.LATEST_CRISIS);
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

package org.sigimera.app.android;

/**
 * Sigimera Crises Information Platform Android Client
 * Copyright (C) 2013 by Sigimera
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Observable;
import java.util.Observer;

import org.sigimera.app.android.controller.ApplicationController;
import org.sigimera.app.android.controller.DistanceController;
import org.sigimera.app.android.controller.LocationController;
import org.sigimera.app.android.controller.PersistanceController;
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

/**
 * Statistic fragment showing the statistical information.
 * 
 * @author Corneliu-Valentin Stanciu
 * @e-mail corneliu.stanciu@sigimera.org
 */
public class StatisticFragment extends Fragment {
	
	/**
	 * Authentication token.
	 */
	private String authToken = null;
	
	/**
	 * Crises statistics.
	 */
	private CrisesStats crisesStats = null;

	/**
	 * Nearest crisis to the user.
	 */
	private Crisis nearestCrisis = null;
	
	/**
	 * Latest occurred crisis.
	 */
	private Crisis latestCrisis = null;
	
	/**
	 * Today's occurred crises.
	 */
	private ArrayList<Crisis> todayCrises = null;

	/**
	 * This view.
	 */
	private View view = null;
	
	/**
	 * "Last" location of user. 
	 */
	private Location userLocation = null;

	/**
	 * Progress dialog for waiting while loading.
	 */
	private ProgressDialog progessDialog = null;

	/**
	 * GUI handler needed for starting the thread in background.
	 */
	private final Handler guiHandler = new Handler();
	
	/**
	 * Method which is stared in background for updating the GUI.
	 */
	private final Runnable updateGUI = new Runnable() {
		@Override
		public void run() {
			updateStatistics();
		}
	};

	@Override
	public final void onActivityCreated(final Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public final View onCreateView(final LayoutInflater inflater,
			final ViewGroup container, final Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.statistic, container, false);

		progessDialog = ProgressDialog.show(getActivity(),
				"Preparing crises information!",
				"Please be patient until the information are ready...", false);
		progessDialog.setCancelable(true);
		Thread worker = new Thread() {
			@Override
			public void run() {
				try {
					Looper.prepare();
					authToken = ApplicationController.getInstance()
							.getSessionHandler().getAuthenticationToken();
					userLocation = LocationController.getInstance()
							.getLastKnownLocation();

					crisesStats = PersistanceController.getInstance()
							.getCrisesStats(authToken);

					guiHandler.post(updateGUI);
				} catch (AuthenticationErrorException e) {
					// SHOULD NEVER OCCUR: Check before calling this window
					Log.e(Constants.LOG_TAG_SIGIMERA_APP,
							"Error on authentification"
									+ e.getLocalizedMessage());
				}
			}
		};
		worker.start();

		return view;
	}

	/**
	 * 
	 */
	private void updateStatistics() {
		latestCrisis = PersistanceController.getInstance().getLatestCrisis(
				authToken);
		nearestCrisis = PersistanceController.getInstance().getNearCrisis(
				authToken, userLocation);
		todayCrises = PersistanceController.getInstance().getTodayCrises();

		/*
		 *  Set distance in km until the near crisis.
		 */
		Button nearCrisisButton = (Button) view.findViewById(R.id.button0);
		double nearDistance = DistanceController.getNearCrisisDistance(
				nearestCrisis, userLocation);
		if (nearDistance != -1.0) {
			nearCrisisButton.setText(Html.fromHtml(nearDistance + " km"
					+ "<br/><small><i>Nearest crisis</i></small>"));
		} else {
			nearCrisisButton.setText(Html.fromHtml("No"
					+ "<br/><small><i>Nearest crisis</i></small>"));
		}
		nearCrisisButton.setOnClickListener(nearCrisisListenter);

		/*
		 *  Set the number of crises today.
		 */
		Button todayCrisesButton = (Button) view.findViewById(R.id.button1);
		if (todayCrises == null || todayCrises.size() == 0) {
			todayCrisesButton.setEnabled(false);
			todayCrisesButton.setText(Html.fromHtml("No crises"
					+ "<br/><small><i>Today</i></small>"));
		} else {
			todayCrisesButton.setText(Html.fromHtml(todayCrises.size()
					+ " crises<br/><small><i>Today</i></small>"));
		}
		todayCrisesButton.setOnClickListener(todayCrisesListenter);

		/*
		 *  Set the time ago since latest crisis
		 */
		Button latestCrisisButton = (Button) view.findViewById(R.id.button2);
		if (latestCrisis == null) {
			latestCrisisButton.setText(Html.fromHtml("No infos<br/>about"
					+ "<br/><small><i>Latest crisis</i></small>"));
		} else {
			latestCrisisButton.setText(Html.fromHtml(Common
					.getTimeAgoInWordsSplitted(Common
							.getMiliseconds(latestCrisis.getDate()))
					+ "<br/><small><i>Latest crisis</i></small>"));
		}
		latestCrisisButton.setOnClickListener(this.latestCrisisListenter);

		/*
		 *  Set total number of crises
		 */
		Button totalCrisesButton = (Button) view.findViewById(R.id.button3);
		if (crisesStats != null) {
			SimpleDateFormat inputFormatter = new SimpleDateFormat(
					"yyyy-MM-dd'T'HH:mm:ss'Z'");
			SimpleDateFormat outputFormatter = new SimpleDateFormat(
					"d. MMMM yyyy");
			Date date = new Date();
			try {
				if (crisesStats.getFirstCrisisAt() != null) {
					date = inputFormatter.parse(crisesStats.getFirstCrisisAt());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			totalCrisesButton.setText(Html.fromHtml(crisesStats
					.getTotalCrises()
					+ " crises since<br/><small><i>"
					+ outputFormatter.format(date) + "</i></small>"));
		}

		/*
		 *  Show one view at start.
		 */
		Fragment nearCrisisFrament = new StatsFragment();
		Bundle bundle = new Bundle();
		bundle.putSerializable("crisis", nearestCrisis);
		bundle.putSerializable("style", Constants.NEAR_CRISIS);
		nearCrisisFrament.setArguments(bundle);
		showFragment(nearCrisisFrament);
		closingProgressDialog();
	}

	/**
	 * Shows a given fragment in the bottom of the statistic window.
	 * 
	 * @param newFragment The fragment which should be loaded.
	 */
	private void showFragment(final Fragment newFragment) {
		FragmentManager fragManager = getFragmentManager();
		if (fragManager != null) {
			FragmentTransaction fragTransaction = fragManager
					.beginTransaction();
			fragTransaction
					.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
			fragTransaction.replace(R.id.main_frag_container, newFragment);
			fragTransaction.commit();
		}
	}

	/**
	 * 
	 */
	public final void closingProgressDialog() {
		if (progessDialog != null) {
			progessDialog.dismiss();
			progessDialog = null;
		}
	}

	/**
	 * Near crisis button listener.
	 */
	private OnClickListener nearCrisisListenter = new OnClickListener() {
		@Override
		public void onClick(final View v) {
			Fragment nearCrisisFrament = new StatsFragment();

			Bundle bundle = new Bundle();
			bundle.putSerializable("crisis", nearestCrisis);
			bundle.putSerializable("style", Constants.NEAR_CRISIS);
			nearCrisisFrament.setArguments(bundle);

			showFragment(nearCrisisFrament);
		}
	};

	/**
	 * Today crises button listener.
	 */
	private OnClickListener todayCrisesListenter = new OnClickListener() {
		@Override
		public void onClick(final View v) {
			Fragment todayCrisesFrament = new CrisesListFragment();

			Bundle bundle = new Bundle();
			bundle.putSerializable("crises", todayCrises);
			todayCrisesFrament.setArguments(bundle);

			showFragment(todayCrisesFrament);
		}
	};

	/**
	 * Latest crisis button listener.
	 */
	private OnClickListener latestCrisisListenter = new OnClickListener() {
		@Override
		public void onClick(final View v) {
			Fragment latestCrisisFrament = new StatsFragment();

			Bundle bundle = new Bundle();
			bundle.putSerializable("crisis", latestCrisis);
			bundle.putSerializable("style", Constants.LATEST_CRISIS);
			latestCrisisFrament.setArguments(bundle);

			showFragment(latestCrisisFrament);
		}
	};
}

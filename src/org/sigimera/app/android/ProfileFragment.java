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
package org.sigimera.app.android;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.sigimera.app.android.controller.ApplicationController;
import org.sigimera.app.android.controller.LocationController;
import org.sigimera.app.android.controller.PersistanceController;
import org.sigimera.app.android.exception.AuthenticationErrorException;
import org.sigimera.app.android.model.Constants;
import org.sigimera.app.android.model.UsersStats;
import org.sigimera.app.android.util.Common;
import org.sigimera.app.android.util.MD5Util;

import android.app.ProgressDialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

/**
 * 
 * @author Corneliu-Valentin Stanciu
 * @e-mail corneliu.stanciu@sigimera.org
 */
public class ProfileFragment extends Fragment {
	private View view = null;
	private Drawable drawable = null;
	private String authToken = null;

	private boolean firstTimeFlag = true;

	private CheckBox enableNearCrises = null;
	private SeekBar nearCrisisRadius = null;
	private TextView nearCrisisRadiusValue = null;
	private TextView overwriteLocation = null;

	private UsersStats stats = null;
	private int radius = 0;

	private ProgressDialog progessDialog = null;

	private final Handler guiHandler = new Handler();
	private final Runnable updateGUI = new Runnable() {
		@Override
		public void run() {
			updateProfile();
		}
	};

	@Override
	public final void onActivityCreated(final Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public final View onCreateView(final LayoutInflater inflater,
			final ViewGroup container, final Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.profile_fragment, container, false);

		progessDialog = ProgressDialog.show(getActivity(),
				"Preparing profile information!",
				"Please be patient until the information are ready...", true);
		progessDialog.setCancelable(true);
		Thread worker = new Thread() {
			@Override
			public void run() {
				try {
					Looper.prepare();
					authToken = ApplicationController.getInstance()
							.getSessionHandler().getAuthenticationToken();

					stats = PersistanceController.getInstance().getUsersStats(
							authToken);

					if (stats == null) {
						Log.d("[PROFILE FRAGMENT]", "User stats are empty.");
					}

					if (stats != null && stats.getUsername() != null) {
						InputStream is = (InputStream) getAvatarURL(
								stats.getUsername()).getContent();
						drawable = Drawable.createFromStream(is, "src name");
						radius = stats.getRadius();
					}

					guiHandler.post(updateGUI);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (AuthenticationErrorException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		worker.start();

		return view;
	}

	/**
	 * Update the profile.
	 */
	private void updateProfile() {
		StringBuffer content;

		if (stats != null) {
			content = new StringBuffer();
			content.append("<p>");
			content.append("<b>" + stats.getName() + "</b>");
			content.append("<br/>");
			content.append("<small><i>" + stats.getName() + "</i></small>");
			content.append("<br/>");
			content.append("<br/>");
			content.append("<small>");
			content.append("Used space:");
			content.append(Common.transformTwoDecimalDoubleNumber(
						PersistanceController.getInstance().getCacheSize()
							/ (1000.0 * 1000.0 * 1000.0)) + " Mb");
			content.append("</small>");
			content.append("</p>");

			TextView name = (TextView) view.findViewById(R.id.name);
			name.setText(Html.fromHtml(content.toString()));

			TextView images = (TextView) view.findViewById(R.id.images);
			images.setText(Html.fromHtml("<p><b>" + stats.getUploadedImages()
					+ "</b><br/><small>Images</small></p>"));

			TextView locations = (TextView) view.findViewById(R.id.location);
			locations.setText(Html.fromHtml("<p><b>"
					+ stats.getReportedLocations()
					+ "</b><br/><small>Locations</small></p>"));

			TextView missingPeople = (TextView) view
					.findViewById(R.id.missing_people);
			missingPeople.setText(Html.fromHtml("<p><b>"
					+ stats.getReportedMissingPeople()
					+ "</b><br/><small>Missing People</small></p>"));

			TextView comments = (TextView) view.findViewById(R.id.comments);
			comments.setText(Html.fromHtml("<p><b>" + stats.getPostedComments()
					+ "</b><br/><small>Comments</small></p>"));
		}

		ImageView avatar = (ImageView) view.findViewById(R.id.avatar);
		avatar.setImageDrawable(drawable);

		content = new StringBuffer();
		content.append("Enable crises near you");
		content.append("<br />");
		content.append("<small><small>" 
				+ "CRISES window will list only crises in the selected radius"
				+ "</small></small>");

		enableNearCrises = (CheckBox) view
				.findViewById(R.id.enable_near_crises);
		enableNearCrises.setOnCheckedChangeListener(checkedChangeListener);
		enableNearCrises.setText(Html.fromHtml(content.toString()));

		nearCrisisRadiusValue = (TextView) view
				.findViewById(R.id.near_crisis_radius_value);

		nearCrisisRadius = (SeekBar) view.findViewById(R.id.near_crisis_radius);
		nearCrisisRadius.setOnSeekBarChangeListener(seekBarChangeListener);

		// overwriteLocation = (TextView) view
		// .findViewById(R.id.overwrite_location);

		if (radius == 0) {
			disableNearCrisesView();
			radius = Constants.LOCATION_RADIUS;
		} else {
			enableNearCrisesView();
		}

		nearCrisisRadiusValue.setText("Near crisis radius: " + radius + " km");
		nearCrisisRadius.setProgress(radius);

		progessDialog.dismiss();

		firstTimeFlag = false;
	}

	/**
	 * Retrieve the gravatar.com image from the email address.
	 * 
	 * @param email
	 *            The email address.
	 * @return the URL of the gravatar.com image or null if the is no image
	 *         attached to this email address on gravatar.com
	 */
	private URL getAvatarURL(final String email) {
		if (email != null) {
			try {
				String emailHash = MD5Util.md5Hex(email.toLowerCase().trim());
				URL url = new URL("http://www.gravatar.com/avatar/" + emailHash);
				return url;
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * 
	 */
	private OnCheckedChangeListener checkedChangeListener = new OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(final CompoundButton buttonView,
				final boolean isChecked) {
			if (!firstTimeFlag) {
				if (isChecked) {
					enableNearCrisesView();
				} else {
					disableNearCrisesView();
				}
			}
		}
	};

	/**
	 * 
	 */
	private OnSeekBarChangeListener seekBarChangeListener = new OnSeekBarChangeListener() {
		private int radiusProgress = 0;

		@Override
		public void onProgressChanged(final SeekBar seekBar,
				final int progress, final boolean fromUser) {
			nearCrisisRadiusValue.setText("Near crisis radius: " + progress
					+ " km");
			radiusProgress = progress;
		}

		@Override
		public void onStopTrackingTouch(final SeekBar seekBar) {
			PersistanceController.getInstance().updateNearCrisesRadius(
					radiusProgress, stats.getUsername());
			PersistanceController.getInstance().updateNearCrises(authToken, 1,
					LocationController.getInstance().getLastKnownLocation());
		}

		@Override
		public void onStartTrackingTouch(final SeekBar seekBar) {
		}
	};

	/**
	 * Enable near crises view and update the radius.
	 */
	private void enableNearCrisesView() {
		this.nearCrisisRadius.setEnabled(true);
		this.nearCrisisRadiusValue.setEnabled(true);
		this.enableNearCrises.setChecked(true);
		// this.overwriteLocation.setEnabled(true);
		if (!firstTimeFlag) {
			PersistanceController.getInstance().updateNearCrisesRadius(radius,
					stats.getUsername());
			PersistanceController.getInstance().updateNearCrises(authToken, 1,
					LocationController.getInstance().getLastKnownLocation());
		}
	}

	/**
	 * Disable near crises view and update the radius.
	 */
	private void disableNearCrisesView() {
		this.nearCrisisRadius.setEnabled(false);
		this.nearCrisisRadiusValue.setEnabled(false);
		this.enableNearCrises.setChecked(false);
		// this.overwriteLocation.setEnabled(false);
		if (!firstTimeFlag) {
			PersistanceController.getInstance().updateNearCrisesRadius(0,
					stats.getUsername());
			PersistanceController.getInstance().updateNearCrises(authToken, 1,
					LocationController.getInstance().getLastKnownLocation());
		}
	}
}

package org.sigimera.app.android;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.sigimera.app.android.controller.ApplicationController;
import org.sigimera.app.android.controller.CrisesController;
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

public class ProfileFragment extends Fragment {

	private View view;
	private Drawable drawable;
	private String auth_token;
	
	private SeekBar nearCrisisRadius;
	private TextView nearCrisisRadiusValue;
	private TextView overwriteLocation;
	
	private UsersStats stats;
	
	private ProgressDialog progessDialog = null;

	private final Handler guiHandler = new Handler();
	private final Runnable updateGUI = new Runnable() {
		@Override
		public void run() {
			updateProfile();
		}
	};

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
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
					auth_token = ApplicationController.getInstance().getSessionHandler().getAuthenticationToken();
										
					stats = CrisesController.getInstance().getUsersStats(auth_token);
					
					if (stats == null) Log.d("[PROFILE FRAGMENT]", "User stats are empty."); 
					
					if ( stats != null && stats.getUsername() != null) {
						InputStream is = (InputStream) getAvatarURL(stats.getUsername()).getContent();
						drawable = Drawable.createFromStream(is, "src name");
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
	
	private void updateProfile() {
		long size = ApplicationController.getInstance().getCacheSize();
		
		StringBuffer content = new StringBuffer();
		content.append("<p>");
		content.append("<b>" + stats.getName() + "</b>");
		content.append("<br/>");
		content.append("<small><i>" + stats.getName() + "</i></small>");		
		content.append("<br/>");
		content.append("<br/>");
		content.append("<small>" + "Used space: " + Common.transformTwoDecimalDoubleNumber(size / (1000.0 * 1000.0 * 1000.0)) + " Mb" + "</small>");
		content.append("</p>");
		
		TextView name = (TextView) view.findViewById(R.id.name);
		name.setText(Html.fromHtml(content.toString()));	
		
		TextView images = (TextView) view.findViewById(R.id.images);
		images.setText(Html.fromHtml("<p><b>" + stats.getUploadedImages() + "</b><br/><small>Images</small></p>"));
		
		TextView locations = (TextView) view.findViewById(R.id.location);
		locations.setText(Html.fromHtml("<p><b>" + stats.getReportedLocations() + "</b><br/><small>Locations</small></p>"));
		
		TextView missingPeople = (TextView) view.findViewById(R.id.missing_people);
		missingPeople.setText(Html.fromHtml("<p><b>" + stats.getReportedMissingPeople() + "</b><br/><small>Missing People</small></p>"));
		
		TextView comments = (TextView) view.findViewById(R.id.comments);
		comments.setText(Html.fromHtml("<p><b>" + stats.getPostedComments() + "</b><br/><small>Comments</small></p>"));
		
		ImageView avatar = (ImageView) view.findViewById(R.id.avatar);
		avatar.setImageDrawable(drawable);
		
		CheckBox enableNearCrises = (CheckBox) view.findViewById(R.id.enable_near_crises);
		enableNearCrises.setOnCheckedChangeListener(checkedChangeListener);
		
		nearCrisisRadiusValue = (TextView) view.findViewById(R.id.near_crisis_radius_value);
		nearCrisisRadiusValue.setText("Near crisis radius: " + Constants.LOCATION_RADIUS + " km");
		
		nearCrisisRadius = (SeekBar) view.findViewById(R.id.near_crisis_radius);
		nearCrisisRadius.setOnSeekBarChangeListener(seekBarChangeListener);
		nearCrisisRadius.setProgress(Constants.LOCATION_RADIUS);
		
		overwriteLocation = (TextView) view.findViewById(R.id.overwrite_location);
		
		// set the near crises view as "disabled"
		disableNearCrisesView();
		
		progessDialog.dismiss();
	}
	
	private URL getAvatarURL(String email) {
		if ( email != null ) {
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
	
	private OnCheckedChangeListener checkedChangeListener = new OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			if ( isChecked )
				enableNearCrisesView();
			else
				disableNearCrisesView();
		}
	};
	
	private OnSeekBarChangeListener seekBarChangeListener = new OnSeekBarChangeListener() {
		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			nearCrisisRadiusValue.setText("Near crisis radius: " + progress + " km");
		}
		
		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {}
		
		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {}
	};
	
	private void enableNearCrisesView() {
		this.nearCrisisRadius.setEnabled(true);
		this.nearCrisisRadiusValue.setEnabled(true);
		this.overwriteLocation.setEnabled(true);
	}
	
	private void disableNearCrisesView() {
		this.nearCrisisRadius.setEnabled(false);
		this.nearCrisisRadiusValue.setEnabled(false);
		this.overwriteLocation.setEnabled(false);
	}
}

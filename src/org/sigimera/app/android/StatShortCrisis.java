package org.sigimera.app.android;

import org.sigimera.app.android.model.Crisis;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
		
		TextView country = (TextView) view.findViewById(R.id.country);
		country.setText(crisis.getCountries().get(0));
		
		TextView severity = (TextView) view.findViewById(R.id.severity);
		severity.setText(crisis.getSeverity());
		
		TextView affectedPeople = (TextView) view.findViewById(R.id.affected_people);		
		affectedPeople.setText(crisis.getPopulation());
		
		return view;
	}
}

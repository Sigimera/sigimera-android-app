package org.sigimera.app.android;

import org.sigimera.app.android.R;
import org.sigimera.app.android.controller.ApplicationController;
import org.sigimera.app.android.controller.SessionHandler;
import org.sigimera.app.android.exception.AuthenticationErrorException;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class DoubleWindowFragment extends Fragment {   
	private View view;
	private PageAdapter pageAdapter;

	private Fragment fragmentPageOne;
	private Fragment fragmentPageTwo;
	
	private ApplicationController appController;
	private SessionHandler sessionHandler;
	
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		this.view = inflater.inflate(R.layout.double_window, container, false);
		this.appController = ApplicationController.getInstance();
		this.appController.init(getActivity(), getSessionSettings());
		this.sessionHandler = appController.getSessionHandler();
		this.initCrisesWindow();
		
		return this.view;
	}
	
    private void initCrisesWindow() {
    	try {
    		this.sessionHandler.getAuthenticationToken();
    		
    		String[] titles = { "Last Crises", "Crisis Info" };
    		fragmentPageOne = new CrisesListFragment();
			fragmentPageOne = new LoginFragment();
//			fragmentPageTwo = new CrisisFragement();
	    	newDoubleWindow(titles, fragmentPageOne, fragmentPageTwo, 0);
    	} catch (AuthenticationErrorException e) {	    	    	    
			String[] titles = { "Login", "Last 10 crises" };				
			fragmentPageOne = new LoginFragment();
			fragmentPageTwo = new CrisesListFragment();			
			newDoubleWindow(titles, fragmentPageOne, fragmentPageTwo, 0);
    	}
    }
    
    private void newDoubleWindow(String[] titles, Fragment pageOne, Fragment pageTwo, int currentItem) {
		ViewPager viewPager = (ViewPager) this.view.findViewById(R.id.viewpager);
		this.pageAdapter = new PageAdapter(getActivity().getSupportFragmentManager(),titles , pageOne, pageTwo);
		viewPager.setAdapter(this.pageAdapter);
		this.fragmentPageOne = pageOne;
		this.fragmentPageTwo = pageTwo;
		viewPager.setCurrentItem(currentItem, true);
	}
    
    // TODO: double implementation of function (see also MainActivity class)
    public SharedPreferences getSessionSettings() {
		String PREFS_NAME = "session_handler_preferences";
		return getActivity().getSharedPreferences(PREFS_NAME, 0);
	}

}

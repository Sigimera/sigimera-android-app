/**
 * Sigimera Crises Information Platform Android Client
 * Copyright (C) 2012 by Sigimera
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
package org.sigimera.app.android.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.ocpsoft.pretty.time.PrettyTime;
import org.sigimera.app.android.R;
import org.sigimera.app.android.controller.ApplicationController;
import org.sigimera.app.android.model.Constants;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Class which sum all the methods used over different windows.
 * 
 * @author Corneliu-Valentin Stanciu
 * @email corneliu.stanciu@sigimera.org
 */
public class Common {

	/**
	 * Share the crisis with your friend and/or the world.
	 */
	public static Intent shareCrisis(String crisisID, String shortTitle) {
		Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
		shareIntent.setType("text/plain");
		
		StringBuffer shareContent = new StringBuffer();
		shareContent.append(shortTitle);
		shareContent.append(" - ");
		shareContent.append("http://www.sigimera.org/crises/");
		shareContent.append(crisisID);
		
		shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareContent.toString());
		return shareIntent;
	}

	/**
	 * Get the crisis icon based on the type of crisis (e.g. earthquake)
	 * 
	 * @param subject
	 * @return
	 */
	public static int getCrisisIcon(String subject) {
		if (subject.contains(Constants.FLOOD))
			return R.drawable.flood;
		else if (subject.contains(Constants.EARTHQUAKE))
			return R.drawable.earthquake;
		else if (subject.contains(Constants.CYCLONE))
			return R.drawable.cyclone;
		else if (subject.contains(Constants.VOLCANO))
			return R.drawable.volcano;
		return 0;
	}

	/**
	 * Get the crisis icon based on the type of crisis (e.g. earthquake)
	 * 
	 * @param subject
	 * @return
	 */
	public static String getCrisisIconURL(String subject) {
		String BASE_URL = "http://www.sigimera.org/img/icons/";
		if (subject.contains(Constants.FLOOD))
			return BASE_URL + "flood_small.png";
		else if (subject.contains(Constants.EARTHQUAKE))
			return BASE_URL + "earthquake_small.png";
		else if (subject.contains(Constants.CYCLONE))
			return BASE_URL + "cyclone_small.png";
		else if (subject.contains(Constants.VOLCANO))
			return BASE_URL + "volcano_small.png";
		return null;
	}

	/**
	 * Check if Internet connections are available.
	 * 
	 * @return
	 */
	public static boolean hasInternet() {
		ConnectivityManager cm = (ConnectivityManager) ApplicationController
				.getInstance().getApplicationContext()
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		if (activeNetwork != null && activeNetwork.isConnected())
			return true;
		else
			return false;
	}

	public static String capitalize(String s) {
		if (s.length() == 0)
			return s;
		return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
	}

	/**
	 * Convert time from milliseconds in time ago.
	 * 
	 * @param miliseconds
	 * @return
	 */
	public static String getTimeAgoInWords(long miliseconds) {
		PrettyTime p = new PrettyTime();
		String timeAgo = p.format(new Date(Long.parseLong(miliseconds + "")));
		return timeAgo;
	}
	
	/**
	 * Convert time from milliseconds in time ago and split it in two lines
	 * 
	 * @param miliseconds
	 * @return
	 */
	public static String getTimeAgoInWordsSplitted(long miliseconds) {
		PrettyTime p = new PrettyTime();
		String timeAgo = p.format(new Date(Long.parseLong(miliseconds + "")));
		timeAgo = timeAgo.replace(" ", "<br/>").replaceFirst("<br/>", " ");
		return timeAgo;
	}
	
	/**
	 * Convert date into milliseconds.
	 * 
	 * @param crisisDate
	 * @return
	 */
	public static long getMiliseconds(String crisisDate) {		  
		try {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm"); 
			Date date = (Date) formatter.parse(crisisDate);
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);			
			return cal.getTimeInMillis();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 				
		return 0;
	}
	
	public static Date getDate(String crisisDate) {
		try {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm"); 
			Date date = (Date) formatter.parse(crisisDate);
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);			
			return cal.getTime();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 		
		return null;
	}
}

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
package org.sigimera.app.android;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * @author Corneliu-Valentin Stanciu
 * @email corneliu.stanciu@sigimera.org
 */
public class PageAdapter extends FragmentStatePagerAdapter {

	private final int NUM_PAGES = 2;
	private String[] titles = {};

	private Fragment fragment;
	private Fragment fragmentPageOne;
	private Fragment fragmentPageTwo;

	public PageAdapter(FragmentManager fm, String[] titles,
			Fragment fragmentPageOne, Fragment fragmentPageTwo) {
		super(fm);
		this.titles = titles;
		this.fragmentPageOne = fragmentPageOne;
		this.fragmentPageTwo = fragmentPageTwo;
	}

	@Override
	public Fragment getItem(int pos) {
		if (pos < 1) {
			this.fragment = fragmentPageOne;
			return fragment;
		} else {
			this.fragment = fragmentPageTwo;
			return fragment;
		}
	}

	@Override
	public int getCount() {
		return NUM_PAGES;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return titles[position];
	}
}

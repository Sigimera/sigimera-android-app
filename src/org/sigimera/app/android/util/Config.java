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

import java.io.IOException;
import java.io.InputStream;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.sigimera.app.android.R;
import org.sigimera.app.android.controller.ApplicationController;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import android.content.Context;


/**
 * <b>ATTENTION:</b> Change this only if you know what you are doing.
 * 
 * @author Corneliu-Valentin Stanciu
 * @email  corneliu.stanciu@sigimera.org
 */
public class Config {		
	/**
	 * The Sigimera Endpoints
	 */
	private String api_host = null;
	private String free_api_host = null;
	private String google_maps_key = null;
	
	/**
	 * The Sigimera Google Cloud Messaging project ID
	 */
	private String gcm_project_id = null;
		
	private static Config instance = null;
	
	public static Config getInstance() {
		if ( instance == null ) instance = new Config();
		return instance;
	}

	private Config() {
		try {
			Context context = ApplicationController.getInstance().getApplicationContext();
			if ( null != context ) {
				InputStream inputStream = context.getResources().openRawResource(R.raw.config);
				InputSource inputSource = new InputSource(inputStream);
					
				XPath xpath = XPathFactory.newInstance().newXPath();
				Node config_node = (Node) xpath.evaluate("//config", inputSource, XPathConstants.NODE);
				
				api_host = xpath.evaluate("api-host/text()", config_node);		
				free_api_host = xpath.evaluate("free-api-host/text()", config_node);
				gcm_project_id = xpath.evaluate("gcm-project-id/text()", config_node);
				google_maps_key = xpath.evaluate("google-maps-key/text()", config_node);
				inputStream.close();
			}
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String getAPIHost() {
		return this.api_host;
	}

	public String getWWWHost() {
		return this.api_host;
	}
	
	public String getFreeAPIHost() {
		return this.free_api_host;
	}
	
	public String getGcmProjectId() {
		return this.gcm_project_id;
	}
	
	public String getGoogleMapsKey() {
		return this.google_maps_key;
	}
}

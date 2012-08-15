package org.sigimera.app.util;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.sigimera.app.R;
import org.sigimera.app.controller.ApplicationController;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

public class Config {	
	/**
	 * <b>ATTENTION:</b> Change this only if you know what you are doing.
	 */
	public static final String LOG_TAG = "SigimeraAndroidApp";
	
	/**
	 * The Sigimera Endpoints
	 */
	private String www_host = null;
	private String api_host = null;
	
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
		InputStream inputStream = ApplicationController.getInstance().getApplicationContext().getResources().openRawResource(R.raw.config);
		InputSource inputSource = new InputSource(inputStream);
				
		try {
			XPath xpath = XPathFactory.newInstance().newXPath();
			Node config_node = (Node) xpath.evaluate("//config", inputSource, XPathConstants.NODE);
			
			api_host = xpath.evaluate("api-host/text()", config_node);			
			www_host = xpath.evaluate("www-host/text()", config_node);
			gcm_project_id = xpath.evaluate("gcm-project-id/text()", config_node);
			inputStream.close();
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String getAPIHost() {
		return this.api_host;
	}

	public String getWWWHost() {
		return this.www_host;
	}
	
	public String getGcmProjectId() {
		return this.gcm_project_id;
	}
}

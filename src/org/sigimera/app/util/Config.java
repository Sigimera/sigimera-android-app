package org.sigimera.app.util;

/**
 * TODO: Out-source the configuration values to an external configuration file.
 */
public abstract class Config {			
	/**
	 * <b>ATTENTION:</b> Change this only if you know what you are doing.
	 */
	public static final String LOG_TAG = "SigimeraAndroidApp";
	
	/**
	 * The Sigimera Google Cloud Messaging project ID
	 */
	public static final String GCM_PROJECT_ID = null;

	/**
	 * The Sigimera Endpoints
	 */
	public final static String WWW_HOST = "http://172.16.1.28:3000/api/v1";
	public final static String API_HOST = "http://172.16.1.28:9292/v1";
}

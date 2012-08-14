package org.sigimera.app.util;

/**
 * TODO: Out-source the configuration values to an external configuration file.
 */
public class Config {			
	/**
	 * <b>ATTENTION:</b> Change this only if you know what you are doing.
	 */
	public static final String LOG_TAG = "SigimeraAndroidApp";
	
	public static final String GCM_PROJECT_ID = "TODO_CHANGE_THIS_TO_GCM_PROJECT_ID";
			
	public String WWW_HOST = "http://172.16.1.28:3000/api/v1";
	public String API_HOST = "http://172.16.1.28:9292/v1";

	/**
	 * Singleton pattern 
	 */
	private static Config instance = null;
	
	public static Config getInstance() {
		if ( instance == null ) instance = new Config();
		return instance;
	}
	
	private Config() {
//		try {
//			settings = new Properties();
//			FileInputStream fis = new FileInputStream("values/settings.xml");
//			settings.loadFromXML(fis);
//			
//			WWW_HOST = settings.getProperty("www.host");
//			API_HOST = settings.getProperty("api.host");
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
}

package org.alfresco.properties;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;

import org.alfresco.service.namespace.NamespaceService;

public class AlfrescoProperties {

	private final static Logger LOGGER = 
			Logger.getLogger(AlfrescoProperties.class.getName());
	
	private   static String   USERNAME="";
	private   static String   PASSWORD="";
	private   static String   URL="";
	private   static String   ROOT="";
	
	/**
	 * Load the connection properties for Alfresco, using a .properties file
	 * @param propertiesFile String with the route of the .properties file
	 */
	public void loadProperties(String propertiesFile){
		Properties p = new Properties();
	 	try {
	 		LOGGER.info("Loading alfresco properties...");
			p.load(new FileInputStream(propertiesFile));
			
			setUserName(p.getProperty("user"));
		  	setPassword(p.getProperty("pass"));
		  	setUrl(p.getProperty("location"));
		  	setRoot("/" + NamespaceService.APP_MODEL_PREFIX + ":"  + p.getProperty("root"));
		  	
		  	LOGGER.info("Alfresco properties OK");
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	/**
	 * Return the user name of the Alfresco connection 
	 * @return the User Name
	 */
	public String getUserName() {
		return USERNAME;
	}


	/**
	 * @param username The USERNAME to set
	 */
	public void setUserName(String userName) {
		USERNAME = userName;
	}


	/**
	 * @return the pASSWORD
	 */
	public String getPassword() {
		return PASSWORD;
	}


	/**
	 * @param pASSWORD the pASSWORD to set
	 */
	public void setPassword(String password) {
		PASSWORD = password;
	}


	/**
	 * @return the aDDRESS
	 */
	public String getUrl() {
		return URL;
	}


	/**
	 * @param Url the aDDRESS to set
	 */
	public void setUrl(String url) {
		URL = url;
	}


	/**
	 * @return the rOOT
	 */
	public String getRoot() {
		return ROOT;
	}


	/**
	 * @param root the rOOT to set
	 */
	public void setRoot(String root) {
		ROOT = root;
	}
	
	public void setProperties(String user, String pass, String url, String root){
		ROOT = "/" + NamespaceService.APP_MODEL_PREFIX + ":"  + root;	
    	USERNAME = user;
	  	PASSWORD = pass;
	  	URL  = url;
	}
	

}

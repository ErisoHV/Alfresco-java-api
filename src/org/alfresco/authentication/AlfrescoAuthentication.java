package org.alfresco.authentication;

import java.util.logging.Logger;

import org.alfresco.properties.AlfrescoProperties;
import org.alfresco.webservice.authentication.AuthenticationFault;
import org.alfresco.webservice.util.AuthenticationUtils;
import org.alfresco.webservice.util.WebServiceFactory;

/**
 * 	AlfrescoAuthentication
 *  @author Erika Hernandez
 */
public class AlfrescoAuthentication
{
	 private   final static Logger LOGGER = 
			 Logger.getLogger(AlfrescoAuthentication.class.getName()); 
	 
	 private static AlfrescoProperties alfProperties = new AlfrescoProperties();

	 private   static boolean  CONNECT = false;

	     
    /**
     * Indicates whether it has established a connection with Alfresco
     * @return true if connection is OK
     */
    public static boolean isConnect(){
    	return CONNECT;
    }
	

    /**
     * 
     * Ends the Alfresco session
     * 
     */
    public static void endSessionAlfresco(){
    	AuthenticationUtils.endSession();
    	CONNECT = false;
    }
    
    
    /**
     * Create an Alfresco connection using WebServiceFactory</b>,  
     * given the route of data file .properties<br>
     * 
     * <b>Properties file:</b><br>
     * location=http\://IP\:PORT/alfresco/soapapi or 
     * http\://IP\:PORT/alfresco/api <br>
     * user=Alfresco user<br>
     * pass=password<br>
     * root=company_home<br>
     *
     * @param 
     * 	<b>properties:</b> Properties file direction
     * @return true if connection is OK
     */
    public static boolean initSessionAlfresco (String properties) {
    	alfProperties.loadProperties(properties);
    	return initSessionAlfresco(alfProperties.getUserName(), 
    			alfProperties.getPassword(), alfProperties.getUrl(), alfProperties.getRoot());
     }
    
    /**
     * Create an Alfresco connection using WebServiceFactory</b>,  
     * given the route of data file .properties<br>
     * 
     * <b>Properties file:</b><br>
     * location=http\://IP\:PORT/alfresco/soapapi or 
     * http\://IP\:PORT/alfresco/api <br>
     * user=Alfresco user<br>
     * pass=password<br>
     * root=company_home<br>
     *
     * @param user User name
     * @param pass Password
     * @param url http\://IP\:PORT/alfresco/soapapi or 
     * http\://IP\:PORT/alfresco/api <br>
     * @param root company_home
     * @return true if connection is OK
     */
    public static boolean initSessionAlfresco(String user, String pass, 
    		String url, String root){	
    	alfProperties.setProperties(user, pass, url, root);
	  	WebServiceFactory.setEndpointAddress(alfProperties.getUrl());
	      try {
				AuthenticationUtils.startSession(alfProperties.getUserName(), 
						alfProperties.getPassword());
			    CONNECT = true;
			    LOGGER.fine("Connected to: " + alfProperties.getUrl() + 
			    		" - User Name: " + alfProperties.getUserName());
			} catch (AuthenticationFault e) {
				CONNECT = false;
				LOGGER.severe("ERROR: can't not connect to " + alfProperties.getUrl() + 
						" with username: " + alfProperties.getUserName());
			}
	      
	      return CONNECT;
    }
}
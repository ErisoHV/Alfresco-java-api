package org.alfresco.users;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.logging.Logger;

import org.alfresco.content.AlfrescoContent;
import org.alfresco.webservice.administration.AdministrationFault;
import org.alfresco.webservice.administration.AdministrationServiceSoapBindingStub;
import org.alfresco.webservice.administration.NewUserDetails;
import org.alfresco.webservice.administration.UserDetails;
import org.alfresco.webservice.administration.UserFilter;
import org.alfresco.webservice.administration.UserQueryResults;
import org.alfresco.webservice.types.NamedValue;
import org.alfresco.webservice.util.Constants;
import org.alfresco.webservice.util.WebServiceFactory;

/**
 * AlfrescoGroup
 * 
 * @author Erika Hernandez
 */
public class AlfrescoUser {
	private final static String HomeFolder = AlfrescoContent.STOREREF.getScheme() + "://" 
			+ AlfrescoContent.STOREREF.getAddress() + "/";
	private final static Logger LOGGER = Logger.getLogger(AlfrescoUser.class.getName());

	/**
	 * Check if the user already exist in Alfresco
	 * 
	 * @param userName
	 *            user name to check
	 * @return true if the user exist
	 */
	public static boolean userExist(String userName) {
		AdministrationServiceSoapBindingStub ad = 
				WebServiceFactory.getAdministrationService();
		try {
			UserFilter filter = new UserFilter();
				filter.setUserName(userName);
			UserQueryResults userResults = ad.queryUsers(filter);
			UserDetails[] userDetailsList = userResults.getUserDetails();
			if (userDetailsList != null){
				return true;
			}
		} catch (RemoteException e) {
			LOGGER.severe("Error - Remote Exception: Server error");
		}
		return false;
	}

	/**
	 * Create an user in Alfresco
	 * 
	 * @param firstname
	 * @param midlename 
	 * @param lastname
	 * @param userName
	 * @param password
	 * @param email
	 * @param orgID
	 */
	public static void createUser(String firstname, String midlename, String lastname, 
			String userName, String password, String email, String orgID) {
		// http://forums.alfresco.com/forum/general/non-technical-alfresco-discussion/user-creation-using-java-api-specific-quota-07212011
		AdministrationServiceSoapBindingStub ad = 
				WebServiceFactory.getAdministrationService();
		try {
			if (!userExist(userName)) {
				NamedValue[] properties = createUserProperties(HomeFolder, firstname, 
						midlename, lastname, email, orgID);
				NewUserDetails dc[] = new NewUserDetails[] { new NewUserDetails(
						userName, password, properties) };
				ad.createUsers(dc);
				LOGGER.info("OK: User created: " + userName);
			}
			else{
				LOGGER.info("The user already exists: " + userName);
			}
		} catch (RemoteException ex) {
			LOGGER.severe("Error - Remote Exception: Server error");
		}
	}

	/**
	 * @param userName
	 */
	public static void deleteUser(String userName) {
		// http://forums.alfresco.com/forum/general/non-technical-alfresco-discussion/user-creation-using-java-api-specific-quota-07212011
		AdministrationServiceSoapBindingStub ad = 
				WebServiceFactory.getAdministrationService();
		try {
			if (userExist(userName)) {
				ad.deleteUsers(new String[] { userName });
				LOGGER.info("OK: User deleted " + userName);
			}
		} catch (RemoteException ex) {
			LOGGER.severe("Error - Remote Exception: Server error");
		}
	}

	// TODO
	/**
	 * @param userName
	 * @throws RemoteException 
	 * @throws AdministrationFault 
	 */
	public static HashMap<String, String> getUser(String userName){
		AdministrationServiceSoapBindingStub ad = 
				WebServiceFactory.getAdministrationService();
		try {
			if (userExist(userName)) {
				UserDetails ud = ad.getUser(userName);
				NamedValue[] properties = ud.getProperties();
				HashMap<String, String> userData = new HashMap<String, String>();
				
				for (NamedValue namedValue : properties) {
					userData.put(namedValue.getName().split("}")[1], namedValue.getValue());
				}
				
				return userData;
				
			}
		} catch (RemoteException ex) {
			LOGGER.severe("Error - Remote Exception: Server error");
		}
		return null;
	}

	/**
	 * 
	 * @param homefolder
	 * @param firstname
	 * @param midlename
	 * @param lastname
	 * @param email
	 * @param orgid
	 * @return
	 */
	private static NamedValue[] createUserProperties(String homefolder, String firstname, 
			String midlename, String lastname, String email, String orgid) {
		return new NamedValue[] {
				new NamedValue(Constants.PROP_USER_HOMEFOLDER, false,homefolder, null),
				new NamedValue(Constants.PROP_USER_FIRSTNAME, false, firstname,null),
				new NamedValue(Constants.PROP_USER_MIDDLENAME, false, midlename, null),
				new NamedValue(Constants.PROP_USER_LASTNAME, false, lastname, null),
				new NamedValue(Constants.PROP_USER_EMAIL, false, email, null),
				new NamedValue(Constants.PROP_USER_ORGID, false, orgid, null) };
	}

}

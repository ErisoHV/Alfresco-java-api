package org.alfresco.users;

import java.rmi.RemoteException;
import java.util.logging.Logger;

import org.alfresco.webservice.accesscontrol.AccessControlFault;
import org.alfresco.webservice.accesscontrol.AccessControlServiceSoapBindingStub;
import org.alfresco.webservice.accesscontrol.AuthorityFilter;
import org.alfresco.webservice.accesscontrol.NewAuthority;
import org.alfresco.webservice.util.Constants;
import org.alfresco.webservice.util.WebServiceFactory;
import org.apache.commons.lang.ArrayUtils;

/**
 * AlfrescoGroup
 * 
 * @author Erika Hernandez
 */
public class AlfrescoGroup {

	private static final String GROUP_AUTHORITY_TYPE = "GROUP";
	private static final Logger LOGGER = Logger.getLogger(AlfrescoGroup.class.getName());
	//Alfresco groups names
	public static final String ADMINISTRATORS_GROUP = "ALFRESCO_ADMINISTRATORS";
	public static final String EMAIL_CONTRIBUTORS = "EMAIL_CONTRIBUTORS";

	/**
	 * Create a group in Alfresco
	 * 
	 * @param groupName
	 *            Group name
	 * @return true if the group has been created successfully
	 * @throws AccessControlFault
	 * @throws RemoteException
	 */
	public static boolean createGroup(String groupName){
		if (groupName != null && !groupName.isEmpty()) {
			AccessControlServiceSoapBindingStub accessControlService = 
					WebServiceFactory.getAccessControlService();
			if (!existGroup(groupName)) {
				try {
					NewAuthority cpGrpAuth = new NewAuthority();
					cpGrpAuth.setAuthorityType(GROUP_AUTHORITY_TYPE);
					cpGrpAuth.setName(groupName);
					NewAuthority[] newAuthorities = new NewAuthority[1];
					newAuthorities[0] = cpGrpAuth;
					accessControlService.createAuthorities(null, newAuthorities);

					LOGGER.info("OK: Group created: " + groupName);
					return true;
				} catch (AccessControlFault e) {
					LOGGER.severe("Error: You don't have sufficient "
							+ "permissions to perform this operation");
					return false;
				} catch (RemoteException e) {
					LOGGER.severe("Error - Remote Exception: Server error");
					return false;
				}
			} else {
				LOGGER.severe("Error: Group " + groupName + " already exists");
			}
		}
		return false;
	}

	/**
	 * Check if the group exist in Alfresco
	 * 
	 * @param groupName
	 *           group name
	 * @return true if the group exist
	 * 	 
	 * */
	private static boolean existGroup(String groupName) {
		AccessControlServiceSoapBindingStub accessControlService =
				WebServiceFactory.getAccessControlService();
		AuthorityFilter authorityFilter = new AuthorityFilter();
		authorityFilter.setAuthorityType(GROUP_AUTHORITY_TYPE);
		String[] allAuthorities = null;
		try {
			allAuthorities = accessControlService.getAllAuthorities(authorityFilter);
		} catch (RemoteException e) {
			LOGGER.severe("Error - Remote Exception: Server error");
		}
		if (allAuthorities != null)
			return (ArrayUtils.contains(allAuthorities, Constants.GROUP_PREFIX + groupName));
		return false;
	}

	// TODO
	/**
	 * Add a user to a group
	 * @param userToAdd user name
	 * @param groupName group name
	 * @return true if ok
	 */
	public static boolean addUsersToGroup(String userToAdd, String groupName){
		if (!AlfrescoUser.userExist(userToAdd)) {
			LOGGER.severe("Error: The user " + userToAdd + " does not exist in Alfresco");
			return false;
		}
		if (!existGroup(groupName)) {
			LOGGER.severe("Error: The group " + groupName + " does not exist in Alfresco");
			return false;
		}

		String[] cpUsers = { userToAdd };
		String parentAuthority = GROUP_AUTHORITY_TYPE + "_" + groupName;
		try {
			WebServiceFactory.getAccessControlService()
				.addChildAuthorities(parentAuthority, cpUsers);
			LOGGER.info("Asignado el usuario " + userToAdd + " al grupo " + groupName);
			return true;
		} catch (AccessControlFault e) {
			LOGGER.severe("Error: You don't have sufficient permissions to perform this operation");
		} catch (RemoteException e) {
			LOGGER.severe("Error: The user " + userToAdd + " already exist in the group: " + groupName);
		}
		
		return false;
	}

	// TODO delele group

}

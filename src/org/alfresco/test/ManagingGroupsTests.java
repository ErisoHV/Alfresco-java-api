package org.alfresco.test;

import org.alfresco.authentication.AlfrescoAuthentication;
import org.alfresco.users.AlfrescoGroup;
import org.alfresco.users.AlfrescoUser;

public class ManagingGroupsTests {

	public static void main(String[] args) {
		if (AlfrescoAuthentication.initSessionAlfresco("alfresco2.properties")){
			AlfrescoGroup.createGroup("desarrollo");
        	AlfrescoGroup.addUsersToGroup("1pedrog", "desarrollo"/*AlfrescoGroup.ADMINISTRATORS_GROUP*/);
        	
        	//add user to ADMINISTRATOR_GROUP
        	AlfrescoGroup.addUsersToGroup("1pedrog", AlfrescoGroup.ADMINISTRATORS_GROUP);
        	
        	//new user
        	AlfrescoUser.createUser("Test02", "02", "o2", "test02", "test02", "test02@test.com", "2");
        	
        	AlfrescoGroup.addUsersToGroup("test02", "desarrollo");

        	AlfrescoAuthentication.endSessionAlfresco();
		}
	}

}

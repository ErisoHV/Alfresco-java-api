package org.alfresco.test;

import java.util.HashMap;

import org.alfresco.authentication.AlfrescoAuthentication;
import org.alfresco.users.AlfrescoUser;

public class ManagingUsersTest {

	public static void main(String[] args) {
		
		if (AlfrescoAuthentication.initSessionAlfresco("alfresco2.properties")){
			//Create the user
			AlfrescoUser.createUser("test01","01" ,"01" , "test01",
					"test01", "test01@gmail.com","1");
			
			//Get user properties
			 HashMap<String, String> data = AlfrescoUser.getUser("test01");
			 System.out.println(data);
			 
			 //Deleting users
			 
			 AlfrescoUser.createUser("Delete","me" ,"please" , "todelete",
						"todelete", "tst@gmail.com","1");
			 
			 AlfrescoUser.deleteUser("todelete");
			 
			 
			 AlfrescoAuthentication.endSessionAlfresco();
		}
		
	}
}

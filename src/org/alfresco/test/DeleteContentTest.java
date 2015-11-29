package org.alfresco.test;

import java.util.HashMap;

import javax.swing.JFileChooser;

import org.alfresco.authentication.AlfrescoAuthentication;
import org.alfresco.content.AlfrescoDocument;
import org.alfresco.content.AlfrescoSpace;

public class DeleteContentTest {
protected static JFileChooser fc = new JFileChooser();
	
	public static void main(String[] args) {
		fc.setDialogTitle("Test Alfresco upload file");
		
		//1. Connect to Alfresco
		if (AlfrescoAuthentication.initSessionAlfresco("alfresco2.properties")){
			
			//2. Create a Directory
            HashMap<String, String> propertiesParent = new HashMap<>(); 
            propertiesParent.put("name", "to delete folder");
            propertiesParent.put("description", "delete me");
            propertiesParent.put("title","delete me, please");
                	
            //create in the root
            AlfrescoSpace.createFolderToRoute(propertiesParent, null);
            
            //3. Delete
            AlfrescoDocument.deleteFile("to delete folder", null);
            
            //Another test...
            propertiesParent.put("name", "do not delete me");
            propertiesParent.put("description", "not");
            propertiesParent.put("title","please");
                	
            //create in the root
            AlfrescoSpace.createFolderToRoute(propertiesParent, null);
            
            propertiesParent.put("name", "delete me");
            propertiesParent.put("description", "please");
            propertiesParent.put("title","please!!!");
            
            //Create child
            AlfrescoSpace.createFolderToRoute(propertiesParent, "do not delete me");
            
            //Delete child :-(
            AlfrescoDocument.deleteFile("delete me", "do not delete me");
                	
		}       	  
	}
}

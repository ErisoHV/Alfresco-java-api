package org.alfresco.test;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import javax.swing.JFileChooser;

import org.alfresco.authentication.AlfrescoAuthentication;
import org.alfresco.content.AlfrescoContent;
import org.alfresco.content.AlfrescoDocument;
import org.alfresco.content.AlfrescoSpace;
import org.alfresco.tools.AlfrescoFiles;

public class UploadFileToRouteTest {

	protected static JFileChooser fc = new JFileChooser();
	
	public static void main(String[] args) {
		fc.setDialogTitle("Test Alfresco upload file");
		
		//1. Connect to Alfresco
		if (AlfrescoAuthentication.initSessionAlfresco("alfresco2.properties")){
			int response = fc.showOpenDialog(fc);

			if (response == JFileChooser.APPROVE_OPTION)
	        {
				File archivoElegido = fc.getSelectedFile();
				
				//2. Build the file properties
				HashMap<String, String> propertiesDoc = new HashMap<>();
            	
            	propertiesDoc.put("name", "nombre_archivo" +
            	new SimpleDateFormat("ddMMyyyyHHmmss").format(new Date()));
            	propertiesDoc.put("description", "Documento-Compiere");
            	propertiesDoc.put("author","admin");
            	propertiesDoc.put("title","Test Java API Alfresco Web Services");
            	
            	if (AlfrescoFiles.isValidMimeType(archivoElegido, MyMimeTypes.MIMETYPES)){
            		System.out.println("valid");
            		//3. The path must exist in Alfresco!!!
            		
            		//3.1 Create a Directory
            		HashMap<String, String> propertiesParent = new HashMap<>(); 
                	propertiesParent.put("name", "2015");
                	propertiesParent.put("description", "2015");
                	propertiesParent.put("title","Alfresco");
                	
                	//create in the root
                	AlfrescoSpace.createFolderToRoute(propertiesParent, null);
                	
                	//child
                	HashMap<String, String> propertiesChild = new HashMap<>(); 
                	propertiesChild.put("name", "Enero");
                	propertiesChild.put("description", "Mes de enero");
                	propertiesChild.put("title","Alfresco");
                	
                	//Way 1: given the reference to parent node
                	AlfrescoSpace.createFolderToReference(propertiesChild, 
                			AlfrescoContent.getContentReference(null, "2015"));
                	//Way 2: given the route
                	//AlfrescoSpace.createFolderToRoute(propertiesChild, "2015");
                	
            		//4. Upload the file!
            		AlfrescoDocument.uploadDocumentToRoute(archivoElegido.getAbsolutePath(), 
            		               		propertiesDoc, "2015/Enero");
            	}
            	else{
            		System.out.println("Invalid mime type!");
            	}
	        }
		}
	}

}

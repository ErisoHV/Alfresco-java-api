package org.alfresco.test;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import javax.swing.JFileChooser;

import org.alfresco.authentication.AlfrescoAuthentication;
import org.alfresco.content.AlfrescoDocument;
import org.alfresco.tools.AlfrescoFiles;

public class UploadFileToRootTest {

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
				HashMap<String, String> propiedadesDoc = new HashMap<>();
            	
            	propiedadesDoc.put("name", "nombre_archivo" +
            	new SimpleDateFormat("ddMMyyyyHHmmss").format(new Date()));
            	propiedadesDoc.put("description", "Documento-Compiere");
            	propiedadesDoc.put("author","admin");
            	propiedadesDoc.put("title","Test Java API Alfresco Web Services");
            	
            	if (AlfrescoFiles.isValidMimeType(archivoElegido, MyMimeTypes.MIMETYPES)){
            		System.out.println("valid");
            		//3. Upload to root
            		AlfrescoDocument.uploadDocumentToReference(archivoElegido.getAbsolutePath(), 
            				propiedadesDoc, null);
            	}
            	else{
            		System.out.println("Invalid mime type!");
            	}
	        }
		}
	}

}

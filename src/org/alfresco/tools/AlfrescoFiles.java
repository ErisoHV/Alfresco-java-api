/**
 * 
 */
package org.alfresco.tools;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.alfresco.test.MyMimeTypes;

import com.j256.simplemagic.ContentInfo;
import com.j256.simplemagic.ContentInfoUtil;


/**
 * 	AlfrescoFiles
 *  @author Erika Hernandez
 */
public class AlfrescoFiles {

	private static final String SUFIX =  "_ddMMyyyyHHmmss";

	public static File getFile(String route){
		File aux = new File(route);
		if (aux.exists()){
			return aux;
	    }
		return null;
	}

    /**
     * Return a file name, replace the spaces by given replace String
     * Return a file name, replaces spaces with replaceString 
     * @param name file name
     * @param replace spaces by replaceString
     * @return
     */
    public static String getFileNameAlfresco(String name, String replaceString, boolean byDate){
    	
    	if (replaceString == null && !byDate)
    		return name;
    	
    	String sufix = "";
    	if (byDate){
    		sufix = new SimpleDateFormat(SUFIX).format(new Date());
    	}

    	String cleanName = "";
    	if (replaceString != null){
    		cleanName = name.replace(" ", replaceString);
    	}
    	
    	int pos = name.lastIndexOf(".");
        if (pos == -1) 
        	return cleanName + sufix;

        return cleanName.substring(0, pos) + sufix;
    }

    public static String getMimeType (File file){
        ContentInfo info = (new ContentInfoUtil()).findMatch(transformBytes(file));
        
        return info.getMimeType();
    }
    
    public static boolean isValidMimeType(File file, String[] myMimeTypes){
    	 ContentInfo info = (new ContentInfoUtil()).findMatch(transformBytes(file));
    	 String mimeType = "";
    	 if (info != null ){
    		 mimeType = info.getMimeType(); 
    		 if (myMimeTypes.length > 0){
	    		 for (int i = 0; i < myMimeTypes.length; i++){
	    			 if (mimeType.equals(myMimeTypes[i]))
	    				 return true;
	    		 }
    		 }
    	 } 
         return false;
    }
    
    
    /**
     * Return the file in <b>byte[]</b> format, to be stored in Alfresco
     * @return File in byte[] format
     */
    public static byte[] transformBytes(File file) {
    	byte[] b = null;
    	try {
	    	int length = (int) file.length();
	    	b = new byte[length];
	    	FileInputStream fin = new FileInputStream(file);
	    	DataInputStream din = new DataInputStream(fin);
	    	din.read(b);
	    	din.close();
    	} catch (Exception ex) {
    		ex.printStackTrace();
    	}
    	return b;
   }
}

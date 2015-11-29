package org.alfresco.test;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import javax.swing.JFileChooser;

import org.alfresco.authentication.AlfrescoAuthentication;
import org.alfresco.content.AlfrescoDocument;
import org.alfresco.model.ContentModel;
import org.alfresco.tools.AlfrescoFiles;
import org.alfresco.users.AlfrescoGroup;
import org.alfresco.users.AlfrescoUser;

import com.j256.simplemagic.ContentInfo;
import com.j256.simplemagic.ContentInfoUtil;
import com.sun.corba.se.impl.javax.rmi.CORBA.Util;

public class subidaArchivoss
{
	protected static JFileChooser fc = new JFileChooser();
     
    public static void main(String[] args) throws Exception
    {
    	
    	fc.setDialogTitle("Test Alfresco");
    	int respuesta = fc.showOpenDialog(fc);
        //Comprobar si se ha pulsado Aceptar
    	
    	
    	
    	
        if (respuesta == JFileChooser.APPROVE_OPTION)
        {
            //Crear un objeto File con el archivo elegido
            File archivoElegido = fc.getSelectedFile();
            
            System.out.println("Directorio: " + archivoElegido.getAbsolutePath());    
            AlfrescoAuthentication.initSessionAlfresco("alfresco2.properties");
            if (AlfrescoAuthentication.isConnect()){
            	
            	Date date = new Date();
            	
            	/**
            	 * Subir Documento
            	 */
//            	//Documento de Prueba
            	HashMap<String, String> propiedadesDoc = new HashMap<>();
            	
            	propiedadesDoc.put("name", "nombre_archivo" + new SimpleDateFormat("ddMMyyyyHHmmss").format(date));
            	propiedadesDoc.put("description", "Documento-Compiere");
            	propiedadesDoc.put("author","admin");
            	propiedadesDoc.put("title","Documento subido desde la API de Java de Alfresco");
            	
            	//Subiendo a la raiz
            	AlfrescoDocument.uploadDocumentToReference(archivoElegido.getAbsolutePath(), 
           		propiedadesDoc, null);
            	
            	//Subiendo a un directorio 
            	//Ojo: debe existir el archivo 2014/Enero
        //    	AlfrescoContent.uploadDocumentToRoute(archivoElegido.getAbsolutePath(), 
        //       		propiedadesDoc, "2014/Enero");
//            	
            	/**
            	 * Borrar Documento
            	 */
            	//AlfrescoContent.deleteFile("nombre_archivo03012015105513", null);
            	
            	
            	/**
            	 * Crear Directorio
            	 */
            	//Directorio de prueba
//           	HashMap<String, String> propiedadesDirPadre = new HashMap<>(); 
//            	propiedadesDirPadre.put("name", "2014");
//            	propiedadesDirPadre.put("description", "Ejercicio Compiere");
//            	propiedadesDirPadre.put("title","Directorio creado desde la API de Java de Alfresco");
//            	
            	//Creando en la raiz
//            	AlfrescoContent.createFolderToRoute(propiedadesDirPadre, null);
            	
            	/**
            	 * Crear directorio dentro de otro
            	 * OJO: descomentar lo anterior para que cree al padre
            	 */
            	//Otro Directorio de prueba
//           	HashMap<String, String> propiedadesDirHijo = new HashMap<>(); 
//            	propiedadesDirHijo.put("name", "1era Semana");
//            	propiedadesDirHijo.put("description", "1era semana - Mes de enero");
//            	propiedadesDirHijo.put("title","Directorio creado desde la API de Java de Alfresco");
            	
            	//Forma 1: dada la referencia
            	//AlfrescoContent.createFolderToReference(propiedadesDirHijo, AlfrescoContent.getContentReference(null, "2014"));
            	//Forma 2: dado un string con la ruta
            	//AlfrescoContent.createFolderToRoute(propiedadesDirHijo, "2014/Enero");
            	
//            	propiedadesDirPadre.put("name", "Facturas_Venta");
//            	propiedadesDirPadre.put("description", "Ejercicio Compiere");

            	/**
            	 * Borrar directorio
            	 */
            	//AlfrescoContent.deleteFile("1era Semana", "2014/Enero");
            	
            	/**
            	 * Crear Usuarios
            	 */
            	// AlfrescoUser.createUser("Usuario","Test" ,"02" , "usuario03", "usuario03", "usuario01@demo.com","1");
            	
            	/**
            	 * Eliminar Usuarios
            	 */
            	
            	//AlfrescoUser.deleteUser("usuario02");
            	//AlfrescoUser.deleteUser("usuario01");
            	
            	/**
            	 * Crear Grupos
            	 */
            	//AlfrescoGroup.createGroup("desarrollo");
            	//AlfrescoGroup.addUsersToGroup("usuario03", "desarrollo"/*AlfrescoGroup.ADMINISTRATORS_GROUP*/);

            	//AlfrescoAuthentication.endSessionAlfresco();
            }
            
        }
    }
}

package org.alfresco.content;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.alfresco.webservice.types.CML;
import org.alfresco.webservice.types.CMLCreate;
import org.alfresco.webservice.types.NamedValue;
import org.alfresco.webservice.types.ParentReference;
import org.alfresco.webservice.types.Predicate;
import org.alfresco.webservice.types.Reference;
import org.alfresco.webservice.util.Constants;
import org.alfresco.webservice.util.WebServiceFactory;

public class AlfrescoSpace {
	private final static Logger LOGGER = Logger.getLogger(AlfrescoSpace.class.getName());

	/**
	 * Create a space or folder in Alfresco, given the reference to the parent
	 * space
	 * 
	 * @param propertiesDir
	 *            Folder properties HashMap (name, description, etc)<br>
	 *            <b>name is mandatory</b>
	 * @param parentRef
	 *            Reference to the parent directory.<br>
	 *            If null the folder will be create in /app:company_home
	 *
	 * @return Reference to the new folder
	 * @throws Exception
	 */
	public static Reference createFolderToReference(
			HashMap<String, String> propertiesDir, Reference parentRef){
		NamedValue[] properties = AlfrescoContent.prepareProperties(
				propertiesDir, false);
		if (properties != null) {
			Reference space = null;
			ParentReference parentReference = null;

			// Prepare route
			if (parentRef != null)
				parentReference = AlfrescoContent.ReferenceToParent(parentRef);
			else {
				parentReference = AlfrescoContent.getCompanyHome();
				LOGGER.log(Level.WARNING,
						"Directorio contenedor es null, se almacenará en Directorio raiz");
			}
			try {
				// check if the folder already exist
				space = new Reference(AlfrescoContent.STOREREF, null, parentReference.getPath()
								+ AlfrescoContent.pathEncoded(propertiesDir.get("name")));
				WebServiceFactory.getRepositoryService().get(new Predicate(new Reference[] {space},
						AlfrescoContent.STOREREF, null));
			} catch (Exception e1) {
				LOGGER.log(Level.INFO, "The folder does not exist, creating...");
				parentReference.setChildName(Constants.createQNameString(
						Constants.NAMESPACE_CONTENT_MODEL, propertiesDir.get("name")));

				CMLCreate create = new CMLCreate("1", parentReference, null,
						null, null, Constants.TYPE_FOLDER, properties);
				CML cml = new CML();
				cml.setCreate(new CMLCreate[] { create });
				try {
					WebServiceFactory.getRepositoryService().update(cml);
					return space;
				} catch (Exception e2) {
					LOGGER.log(Level.SEVERE,
							"Error: Do not find the parent: " + parentRef.getPath());
				}
			}
		}
		return null;
	}

	/**
	 * Create a space or folder in Alfresco, given the reference to the parent
	 * space
	 * 
	 * @param propertiesDir
	 *             Folder properties HashMap (name, description, etc)<br>
	 *            <b>name is mandatory</b>
	 * @param route
	 *            Route to the parent directory.<br>
	 *            If null the folder will be create in /app:company_home
	 * @return
	 * @throws Exception
	 */
	public static Reference createFolderToRoute( HashMap<String, String> propertiesDir, 
			String route){
		NamedValue[] properties = AlfrescoContent.prepareProperties(propertiesDir, false);
		if (properties != null) {
			Reference space = null;
			Reference parentReference = new Reference();
			parentReference.setStore(AlfrescoContent.STOREREF);

			if (route != null && route != "")
				parentReference.setPath(AlfrescoContent.COMPANY_HOME
						+ AlfrescoContent.pathEncoded(route));
			else
				parentReference.setPath(AlfrescoContent.COMPANY_HOME);
			try {
				// Se verifica si el directorio ya existe
				space = new Reference(AlfrescoContent.STOREREF, null, parentReference.getPath()
								+ AlfrescoContent.pathEncoded(propertiesDir.get("name")));
				WebServiceFactory.getRepositoryService().get(new Predicate(new Reference[] {space},
								AlfrescoContent.STOREREF, null));
			} catch (Exception e1) {
				LOGGER.log(Level.INFO, "The folder does not exist, creating...");
				ParentReference parentRef = AlfrescoContent
						.ReferenceToParent(parentReference);
				parentRef.setChildName(Constants.createQNameString(
						Constants.NAMESPACE_CONTENT_MODEL, propertiesDir.get("name")));

				CMLCreate create = new CMLCreate("1", parentRef, null, null, null, 
						Constants.TYPE_FOLDER, properties);
				CML cml = new CML();
				cml.setCreate(new CMLCreate[] { create });

				try {
					WebServiceFactory.getRepositoryService().update(cml);
					return space;
				} catch (Exception e2) {
					LOGGER.log(Level.SEVERE, "Error: Do not find the parent: " 
								+ parentRef.getPath());
				}
			}
		}
		return null;
	}
}

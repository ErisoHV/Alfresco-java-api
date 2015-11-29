package org.alfresco.content;

import java.io.File;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.logging.Logger;

import org.alfresco.tools.AlfrescoFiles;
import org.alfresco.webservice.repository.RepositoryFault;
import org.alfresco.webservice.repository.RepositoryServiceSoapBindingStub;
import org.alfresco.webservice.repository.UpdateResult;
import org.alfresco.webservice.types.CML;
import org.alfresco.webservice.types.CMLCreate;
import org.alfresco.webservice.types.CMLDelete;
import org.alfresco.webservice.types.ContentFormat;
import org.alfresco.webservice.types.NamedValue;
import org.alfresco.webservice.types.ParentReference;
import org.alfresco.webservice.types.Predicate;
import org.alfresco.webservice.types.Reference;
import org.alfresco.webservice.util.Constants;
import org.alfresco.webservice.util.WebServiceFactory;

/**
 * AlfrescoContent
 * 
 * @author Erika Hernandez
 */
public class AlfrescoDocument {
	private final static Logger LOGGER = Logger.getLogger(AlfrescoDocument.class.getName());

	/**
	 * Save a document in Alfresco, given the directory reference
	 *
	 * @param route
	 *            Path of the document
	 * @param propertiesDoc
	 *            document properties HashMap
	 * @param parentRef
	 *            Parent reference, if null the document will be uploaded in
	 *            /app:company_home
	 * @return
	 * @throws RepositoryFault
	 * @throws RemoteException
	 */
	public static boolean uploadDocumentToReference(String route,
			HashMap<String, String> propertiesDoc, Reference parentRef) {
		// Preparing the file
		File file = AlfrescoFiles.getFile(route);
		// Document properties
		NamedValue[] properties = AlfrescoContent.prepareProperties(
				propertiesDoc, true);
		if (file != null && properties != null) {
			ParentReference parentReference = null;
			if (parentRef != null)
				parentReference = AlfrescoContent.ReferenceToParent(parentRef);
			else {
				parentReference = AlfrescoContent.getCompanyHome();
				LOGGER.warning("Parent directory is null, "
						+ " the document will be uploaded in /app:company_home");
			}
			String mimeType = AlfrescoFiles.getMimeType(file);
			if (mimeType != null) {
				ContentFormat contentFormat = new ContentFormat(mimeType,
						"UTF-8");
				parentReference.setChildName(Constants.createQNameString(
						Constants.NAMESPACE_CONTENT_MODEL,
						propertiesDoc.get("name")));

				CMLCreate create = new CMLCreate("1", parentReference, null,
						null, null, Constants.TYPE_CONTENT, properties);
				CML cml = new CML();
				cml.setCreate(new CMLCreate[] { create });
				try {
					UpdateResult[] result = WebServiceFactory
							.getRepositoryService().update(cml);
					Reference document = result[0].getDestination();
					WebServiceFactory.getContentService().write(document,
							Constants.PROP_CONTENT,
							AlfrescoFiles.transformBytes(file), contentFormat);
					LOGGER.info("OK: File/Document uploaded to " + parentReference.getPath());
					return true;
				} catch (RepositoryFault e) {
					LOGGER.severe("Error - Repository Fault: Can not create "
							+ "the document. The destination does not exist");
				} catch (RemoteException e) {
					LOGGER.severe("Error - Remote Exception: Can not create "
									+ "the document. Server error");
				}
			} else {
				LOGGER.severe("Mime Type is null, file type not supported");
			}
		}
		return false;
	}

	/**
	 * Stores a document in Alfresco, given the path and divided by /<br>
	 * e.g: archivo1/documentos/facturas<br>
	 * The path must exist in Alfresco
	 * 
	 * @param DocRoute
	 *            Document route
	 * @param propertiesDoc
	 *            Document properties HashMap
	 * @param route
	 *            Path to folder containing the document. If null, the document
	 *            will be uploaded in /app:company_home
	 * @return
	 */
	public static boolean uploadDocumentToRoute(String DocRoute,
			HashMap<String, String> propertiesDoc, String route) {
		File file = AlfrescoFiles.getFile(DocRoute);
		// Propiedades del Documento
		NamedValue[] properties = AlfrescoContent.prepareProperties(propertiesDoc, true);
		if (file != null && properties != null) {
			Reference parentReference = new Reference();
			parentReference.setStore(AlfrescoContent.STOREREF);

			if (route != null && route != "")
				parentReference.setPath(AlfrescoContent.COMPANY_HOME
						+ AlfrescoContent.pathEncoded(route));
			else
				parentReference.setPath(AlfrescoContent.COMPANY_HOME);

			String mimeType = AlfrescoFiles.getMimeType(file);
			if (mimeType != null) {
				ParentReference parentRef = AlfrescoContent.ReferenceToParent(parentReference);
				parentRef.setChildName(Constants.createQNameString(
						Constants.NAMESPACE_CONTENT_MODEL, propertiesDoc.get("name")));

				ContentFormat contentFormat = new ContentFormat(mimeType, "UTF-8");
				parentRef.setChildName(Constants.createQNameString(
						Constants.NAMESPACE_CONTENT_MODEL, propertiesDoc.get("name")));
				CMLCreate create = new CMLCreate("1", parentRef, null, null, null, 
						Constants.TYPE_CONTENT, properties);
				CML cml = new CML();
				cml.setCreate(new CMLCreate[] { create });
				try {
					UpdateResult[] result = WebServiceFactory
							.getRepositoryService().update(cml);
					Reference document = result[0].getDestination();
					WebServiceFactory.getContentService().write(document,
							Constants.PROP_CONTENT, AlfrescoFiles.transformBytes(file), contentFormat);
					LOGGER.info("OK: File/Document uploaded to " + parentReference.getPath());
					return true;
				} catch (RepositoryFault e) {
					LOGGER.severe("Error - Repository Fault: Can not create "
									+ "the document. The destination does not exist");
				} catch (RemoteException e) {
					LOGGER.severe("Error - Remote Exception: Can not create "
									+ "the document. Server error");
				}
			} else {
				LOGGER.severe("Mime Type is null, file type not supported");
			}
		}
		return false;
	}

	/**
	 * Delete a document or folder/space stored in Alfresco, given the route.<br/>
	 * If it is a directory, its content is also deleted
	 * 
	 * @param name
	 *            File o folder name
	 * @param route
	 *            Space name
	 * @return true if the content was successfully deleted
	 * @throws RepositoryFault
	 * @throws RemoteException
	 */
	public static boolean deleteFile(String name, String route) {
		RepositoryServiceSoapBindingStub repositoryService = WebServiceFactory
				.getRepositoryService();
		Reference parentReference = new Reference();
		parentReference.setStore(AlfrescoContent.STOREREF);

		if (route != null && route != "") {
			parentReference.setPath(AlfrescoContent.COMPANY_HOME
					+ AlfrescoContent.pathEncoded(route));
		} else {
			parentReference.setPath(AlfrescoContent.COMPANY_HOME);
		}
		Reference referenceElement = AlfrescoContent.getContentReference(
				parentReference, name);

		if (referenceElement != null) {
			Predicate predicate = new Predicate();
			predicate.setNodes(new Reference[] { referenceElement });
			CMLDelete delete = new CMLDelete();
			delete.setWhere(predicate);
			// create CML update object
			CML cmlRemove = new CML();
			cmlRemove.setDelete(new CMLDelete[] { delete });

			try {
				repositoryService.update(cmlRemove);
				LOGGER.info("Deleted: " + name);
				return true;
			} catch (RepositoryFault e) {
				e.printStackTrace();
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		} else {
			LOGGER.severe("The file o folder  " + name + " does not exist in the route: "
							+ parentReference.getPath());

		}
		return false;
	}

}

package org.alfresco.content;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.alfresco.model.ContentModel;
import org.alfresco.webservice.repository.QueryResult;
import org.alfresco.webservice.repository.RepositoryFault;
import org.alfresco.webservice.repository.RepositoryServiceSoapBindingStub;
import org.alfresco.webservice.types.NamedValue;
import org.alfresco.webservice.types.Node;
import org.alfresco.webservice.types.ParentReference;
import org.alfresco.webservice.types.Predicate;
import org.alfresco.webservice.types.Query;
import org.alfresco.webservice.types.Reference;
import org.alfresco.webservice.types.ResultSet;
import org.alfresco.webservice.types.ResultSetRow;
import org.alfresco.webservice.types.ResultSetRowNode;
import org.alfresco.webservice.types.Store;
import org.alfresco.webservice.util.Constants;
import org.alfresco.webservice.util.ISO9075;
import org.alfresco.webservice.util.Utils;
import org.alfresco.webservice.util.WebServiceFactory;

public class AlfrescoContent {

	private final static Logger LOGGER 
			= Logger.getLogger(AlfrescoContent.class.getName());
	
	public static final Store STOREREF = new Store(Constants.WORKSPACE_STORE, "SpacesStore");

	public static final String COMPANY_HOME = "/app:company_home";

	
	/**
	 * Return the reference to /app:company_home (Root Alfresco directory)
	 * 
	 * @return ParentReference to /app:company_home
	 */
	protected static ParentReference getCompanyHome() {
		ParentReference companyHomeParent = new ParentReference(STOREREF, null,
				COMPANY_HOME, Constants.ASSOC_CONTAINS, null);
		return companyHomeParent;
	}

	/**
	 * Return the <b>ParentReference</b> for a <b>Reference</b>
	 * 
	 * @param spaceref
	 *            Node Reference
	 * @return Node ParentReference
	 */
	protected static ParentReference ReferenceToParent(Reference spaceref) {
		ParentReference parent = new ParentReference();

		parent.setStore(STOREREF);
		parent.setPath(spaceref.getPath());
		parent.setUuid(spaceref.getUuid());
		parent.setAssociationType(Constants.ASSOC_CONTAINS);

		return parent;
	}
	
	
	/**
     * Retorna un String con la ruta normalizada para Alfresco con el formato ISO9075
     * @param path Ruta del directorio dividida por /
     * Ej: raiz/carpeta1/carpeta2
     * @return Un String con la ruta a un directorio normalizada para Alfresco
     * 	Ej: /cm:raiz/cm:carpeta1/cm:carpeta2
     */
    public static String pathEncoded (String path){
		String paths[] = path.split("/");
		String pathEncoded = "";
		for (int i = 0; i< paths.length; i++){
			pathEncoded += "/cm:" + ISO9075.encode(paths[i]);
		}
		return pathEncoded;
    }
    
    /**
	 * Busca un documento o directorio en Alfresco
	 * @param space Referencia al espacio donde se buscara el elemento, 
	 * si es null se buscará en /app:company_home (Raiz del repositorio de alfresco)
	 * @param contentName Nombre del Contenido o directorio en Alfresco
	 * @return Referencia al directorio o contenido, null si no se encuentra
	 */
	public static Reference getContentReference(Reference space, String contentName) {
		//Se obtiene la referencia al directorio padre
		Reference parentReference;
		if (space != null )
			parentReference  = ReferenceToParent(space);
    	else 
    		parentReference = getCompanyHome();

		QueryResult queryResult = null;
		try {
			queryResult = WebServiceFactory.getRepositoryService().queryChildren(parentReference);
		} catch (Exception ex) {
			LOGGER.log(Level.SEVERE, "Error: no se puede acceder al espacio " + parentReference.getPath());
			return null;
		}
		ResultSet resultSet = queryResult.getResultSet();
		if (resultSet.getTotalRowCount() > 0) {
			//LOGGER.log(Level.INFO, "Encontradas {" + resultSet.getTotalRowCount() + "} referencias a " + contentName + "} en {" + space.getPath() + "}");
			ResultSetRow[] rows = resultSet.getRows();
			
			//Recorriendo
			for (int x = 0; x < rows.length; x++) {
				ResultSetRowNode node = rows[x].getNode();
				Reference ref1 = new Reference(STOREREF, node.getId(), null);
				Query query = null;
				Node[] noderesult = null;
				try {
					noderesult = WebServiceFactory.getRepositoryService().get(
							new Predicate(new Reference[] { ref1 }, STOREREF,query));
					if (noderesult != null){
							for (Node rowi : noderesult){
								NamedValue[] columns1 = rowi.getProperties();
								for (int y1 = 0; y1 < columns1.length; y1++){
									if(rowi.getProperties(y1).getName().endsWith(
											Constants.PROP_NAME)){
										if(rowi.getProperties(y1).getValue().equals(contentName)){
											return rowi.getReference();
										}
									}
								}
						}
					}
				} catch (Exception ex1) {
					LOGGER.log(Level.INFO, "No hay referencias al nodo {" + contentName + "}");
				}
			}
		} else {
			LOGGER.log(Level.SEVERE, "Error: Encontradas {" + resultSet.getTotalRowCount() + "} referencias a {" + contentName + "} en {" + 
					parentReference.getPath() + "} (No hay una única referencia al nodo)");
		}
		return null;
	}
	
	/**
	 * Prepara las propiedades para un documento o un directorio nuevo en Alfresco
	 * Propiedades para un documento:<br>
	 * - name<br>
	 * - description<br>
	 * - author<br>
	 * - title<br>
	 * Propiedades para un directorio:<br>
	 * - name<br>
	 * - description<br>
	 * - title<br>
	 * 
	 * @param properties HashMap con las propiedades del documento. 
	 * La propiedad "name" es obligatoria
	 * @param isDocument Indica si las propiedades pertenecen a un documento. 
	 * Si es falso pertenecen a un directorio
	 * @return NamedValue con las propiedades del documento o directorio
	 */
	public static NamedValue[] prepareProperties (HashMap<String, String> properties, 
			boolean isDocument){
		//name is mandatory
		if (properties.containsKey("name") && properties.get("name") != ""){
			int i = 1;
			if (properties.containsKey("description")) i++;
			if (properties.containsKey("title")) i++;
			if (isDocument){
				if (properties.containsKey("author")) i++;
			}
			NamedValue[] preparedProperties = new NamedValue[i]; 
			i=0;
			
			preparedProperties[i] = Utils.createNamedValue(
					ContentModel.PROP_NAME.toString(), 
					properties.get("name"));
			i++;
			if (properties.containsKey("description")){
				preparedProperties[i] = 
						Utils.createNamedValue(
								ContentModel.PROP_DESCRIPTION.toString(), 
						properties.get("description"));
				i++;
			}
			
			if (properties.containsKey("title")){
				preparedProperties[i] = 
						Utils.createNamedValue(ContentModel.PROP_TITLE.toString(), 
						properties.get("title"));
				i++;
			}
			
			if (isDocument){
				if (properties.containsKey("author")){
					preparedProperties[i] = 
							Utils.createNamedValue(
									ContentModel.PROP_AUTHOR.toPrefixString(), 
							properties.get("author"));
					i++;
				}
			}
			return preparedProperties;
		}
		else{
			LOGGER.severe("The name propertie is mandatory");
		}
		return null;
	}

	 /**
     * Muestra los store de alfresco por la salida estandar
     * @return
     * @throws
     */
    public static void getStores(){
    	RepositoryServiceSoapBindingStub repoService 
    		= WebServiceFactory.getRepositoryService();
		Store[] stores;
		try {
			stores = repoService.getStores();
			for(Store s: stores){
				System.out.println(s.getScheme() + "://" + s.getAddress());
			}
		} catch (RepositoryFault e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
    }
}

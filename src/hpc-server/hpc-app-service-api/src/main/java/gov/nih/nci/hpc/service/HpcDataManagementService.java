/**
 *
 * Copyright SVG, Inc.
 * Copyright Leidos Biomedical Research, Inc
 * 
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/HPC/LICENSE.txt for details.
 */

package gov.nih.nci.hpc.service;

import gov.nih.nci.hpc.domain.datamanagement.HpcCollection;
import gov.nih.nci.hpc.domain.datamanagement.HpcDataObject;
import gov.nih.nci.hpc.domain.datamanagement.HpcSubjectPermission;
import gov.nih.nci.hpc.domain.datatransfer.HpcFileLocation;
import gov.nih.nci.hpc.domain.metadata.HpcMetadataEntries;
import gov.nih.nci.hpc.domain.model.HpcDocConfiguration;
import gov.nih.nci.hpc.exception.HpcException;

import java.util.List;

/**
 * <p>
 * HPC Data Management Application Service Interface.
 * </p>
 *
 * @author <a href="mailto:eran.rosenberg@nih.gov">Eran Rosenberg</a>
 */

public interface HpcDataManagementService 
{   
    /**
     * Create a collection's directory.
     *
     * @param path The collection path.
     * @return true if the directory was created, or false if it already exists.
     * @throws HpcException on service failure.
     */
    public boolean createDirectory(String path) throws HpcException;
    
    /**
     * Check if the path's parent is a directory.
     *
     * @param path The path.
     * @return true if the parent path is a directory, or false otherwise.
     * @throws HpcException on data management system failure.
     */
    public boolean isPathParentDirectory(String path) throws HpcException;   
    
    /**
     * Create a data object's file.
     *
     * @param path The data object path.
     * @return true if the data object file was created, or false if it already exists.
     * @throws HpcException on service failure.
     */
    public boolean createFile(String path) throws HpcException;
    
    /**
     * Delete a path (data object or directory). 
     *
     * @param path The path to delete.
     * @param quiet If set to true, no exception is thrown in case of a failure.
     * @throws HpcException on service failure (unless 'quiet' is set to 'true').
     */
    public void delete(String path, boolean quiet) throws HpcException;
    
    /**
     * Save a record of the deletion in the DB.
     * Note: Currently, there is no 'audit trail' functionality implemented. iRODS has this capability, and there
     * is a plan to use it. This is a temporary solution to have a record of what data objects deleted, by
     * who and when. When the permanent solution is implemented (using iRODS capability) this API method and the
     * DAO behind it should be retired.
     *
     * @param path The path to delete.
     * @param archiveLocation The physical file location in the archive.
     * @param archiveDeleteStatus True if the physical file was successfully removed from archive.
     * @param metadataEntries The metadata associated with this path.
     * @param dataManagementDeleteStatus True if data object was removed from the data management system.
     * @param message (Optional) Error message received in case the deletion request failed. 
     * @throws HpcException on service failure.
     */
    public void saveDataObjectDeletionRequest(String path, HpcFileLocation archiveLocation, 
    		                                  boolean archiveDeleteStatus, HpcMetadataEntries metadataEntries,
    		                                  boolean dataManagementDeleteStatus, String message) 
    		                                 throws HpcException;
    
    /**
     * Set collection permission for a subject (user or group). 
     *
     * @param path The collection path.
     * @param subjectPermission The subject permission request.
     * @throws HpcException on service failure.
     */
    public void setCollectionPermission(String path, HpcSubjectPermission subjectPermission) 
    		                           throws HpcException;
    
    /**
     * Get collection permissions. 
     *
     * @param path The collection path.
     * @return A list of permissions on the collection.
     * @throws HpcException on service failure.
     */
    public List<HpcSubjectPermission> getCollectionPermissions(String path) throws HpcException;
    
    /**
     * Get collection permissions for userId. 
     *
     * @param path The collection path.
     * @param userId The user-id to get permissions for.
     * @return permission on the collection.
     * @throws HpcException on service failure.
     */
    public HpcSubjectPermission getCollectionPermissionForUser(String path, String userId) throws HpcException;

    /**
     * Set data object permission for a subject (user or group). 
     *
     * @param path The data object path.
     * @param subjectPermission The subject permission request.
     * @throws HpcException on service failure.
     */
    public void setDataObjectPermission(String path, HpcSubjectPermission subjectPermission) 
    		                           throws HpcException;
    
    /**
     * Get data object permissions. 
     *
     * @param path The data object path.
     * @return A list of permissions on the data object.
     * @throws HpcException on service failure.
     */
    public List<HpcSubjectPermission> getDataObjectPermissions(String path) throws HpcException;
    
    /**
     * Get data object permission by userId. 
     *
     * @param path The data object path.
     * @param userId The user-id to get permissions for.
     * @return permission on the data object.
     * @throws HpcException on service failure.
     */
    public HpcSubjectPermission getDataObjectPermissionForUser(String path, String userId) throws HpcException;
    
    /**
     * Get data object permission (for the request invoker) 
     *
     * @param path The data object path.
     * @return permission on the data object.
     * @throws HpcException on service failure.
     */
    public HpcSubjectPermission getDataObjectPermission(String path) throws HpcException;

    /**
     * Assign system account as an additional owner of an entity.
     *
     * @param path The entity path.
     * @throws HpcException on service failure.
     */
    public void assignSystemAccountPermission(String path) throws HpcException;
    
    /**
     * Validate a path against a hierarchy definition.
     *
     * @param path The collection path.
     * @param doc Use validation rules of this DOC.
     * @param dataObjectRegistration If true, the service validates if data object registration is allowed 
     *                               in this collection
     * @throws HpcException If the hierarchy is invalid.
     */
    public void validateHierarchy(String path, String doc,
    		                      boolean dataObjectRegistration) 
    		                     throws HpcException;
    
    /**
     * Get collection by its path.
     *
     * @param path The collection's path.
     * @param list An indicator to list sub-collections and data-objects.
     * @return A collection.
     * @throws HpcException on service failure.
     */
    public HpcCollection getCollection(String path, boolean list) throws HpcException;
    
    /**
     * Get collection children by its path. No collection metadata is returned.
     *
     * @param path The collection's path.
     * @return A collection.
     * @throws HpcException on service failure.
     */
    public HpcCollection getCollectionChildren(String path) throws HpcException;

    /**
     * Get data object by its path.
     *
     * @param path The data object's path.
     * @return A data object.
     * @throws HpcException on service failure.
     */
    public HpcDataObject getDataObject(String path) throws HpcException;
    
    /**
     * Get data objects that have their data transfer in-progress.
     *
     * @return A list of data objects.
     * @throws HpcException on service failure.
     */
    public List<HpcDataObject> getDataObjectsInProgress() throws HpcException;
    
    /**
     * Get data objects that have their data stored in temporary archive.
     *
     * @return A list of data objects.
     * @throws HpcException on service failure.
     */
    public List<HpcDataObject> getDataObjectsInTemporaryArchive() throws HpcException;
    
    /**
     * Close connection to Data Management system for the current service call.
     */
    public void closeConnection();
    
    /**
     * Get DOC configuration.
     * 
     * @param doc The DOC to get the configuration for
     * @return DOC configuration.
     */
    public HpcDocConfiguration getDocConfiguration(String doc);

    /**
     * Get a list of all DOCs supported by the system.
     * 
     * @return List of DOCs.
     */
    public List<String> getDocs();
}

 
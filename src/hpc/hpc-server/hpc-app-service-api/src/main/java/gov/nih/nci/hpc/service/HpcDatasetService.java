/**
 * HpcDatasetService.java
 *
 * Copyright SVG, Inc.
 * Copyright Leidos Biomedical Research, Inc
 * 
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/HPC/LICENSE.txt for details.
 */

package gov.nih.nci.hpc.service;

import gov.nih.nci.hpc.domain.dataset.HpcDatasetUserAssociation;
import gov.nih.nci.hpc.domain.dataset.HpcFile;
import gov.nih.nci.hpc.domain.dataset.HpcFileUploadRequest;
import gov.nih.nci.hpc.domain.datatransfer.HpcDataTransferLocations;
import gov.nih.nci.hpc.domain.datatransfer.HpcDataTransferReport;
import gov.nih.nci.hpc.domain.datatransfer.HpcDataTransferRequest;
import gov.nih.nci.hpc.domain.datatransfer.HpcDataTransferStatus;
import gov.nih.nci.hpc.domain.metadata.HpcFileMetadata;
import gov.nih.nci.hpc.domain.metadata.HpcFileMetadataHistory;
import gov.nih.nci.hpc.domain.metadata.HpcFilePrimaryMetadata;
import gov.nih.nci.hpc.domain.metadata.HpcMetadataItem;
import gov.nih.nci.hpc.domain.model.HpcDataset;
import gov.nih.nci.hpc.exception.HpcException;

import java.util.Calendar;
import java.util.List;

/**
 * <p>
 * HPC Dataset Application Service Interface.
 * </p>
 *
 * @author <a href="mailto:eran.rosenberg@nih.gov">Eran Rosenberg</a>
 * @version $Id$
 */

public interface HpcDatasetService 
{         
    /**
     * Add dataset.
     *
     * @param name The dataset name.
     * @param description The dataset description.
     * @param comments The dataset comments.
     * @param persist If set to true, the dataset will be persisted.
     * @return The new dataset.
     * 
     * @throws HpcException
     */
    public HpcDataset addDataset(String name, String description, String comments,
    		                     boolean persist) 
    			                throws HpcException;
    
    /**
     * Add a file to a dataset.
     *
     * @param dataset The dataset.
     * @param uploadRequest A file upload request.
     * @param persist If set to true, the dataset will be persisted.
     * @return The new file.
     * 
     * @throws HpcException
     */
    public HpcFile addFile(HpcDataset dataset, HpcFileUploadRequest uploadRequest,
                           boolean persist) 
                          throws HpcException;
    
    /**
     * Add an upload data transfer request to a dataset.
     *
     * @param dataset The dataset.
     * @param requesterNciUserId The user-id initiated the upload.
     * @param fileId The uploaded file ID.
     * @param locations The transfer source and destination.
     * @param report The data transfer report.
     * @param persist If set to true, the dataset will be persisted.
     * @return The new data transfer request
     * 
     * @throws HpcException
     */
    public HpcDataTransferRequest addDataTransferUploadRequest(
    		              HpcDataset dataset, String requesterNciUserId, 
    		              String fileId, HpcDataTransferLocations locations,
                          HpcDataTransferReport report, boolean persist) 
                          throws HpcException;
    
    /**
     * Add a download data transfer request to a dataset.
     *
     * @param dataset The dataset.
     * @param requesterNciUserId The user-id initiated the upload.
     * @param fileId The uploaded file ID.
     * @param locations The transfer source and destination.
     * @param report The data transfer report.
     * @param persist If set to true, the dataset will be persisted.
     * @return The new data transfer request
     * 
     * @throws HpcException
     */
    public HpcDataTransferRequest addDataTransferDownloadRequest(
    		              HpcDataset dataset, String requesterNciUserId, 
    		              String fileId, HpcDataTransferLocations locations,
                          HpcDataTransferReport report, boolean persist) 
                          throws HpcException;
    
    /**
     * Add metadata items to a file primary metadata in a dataset.
     *
     * @param dataset The dataset.
     * @param fileId The file ID to add the metadata items to.
     * @param metadataItems The metadata items to add.
     * @param persist If set to true, the dataset will be persisted.
     * @return The updated file primary metadata domain object.
     * 
     * @throws HpcException
     */
    public HpcFilePrimaryMetadata 
           addPrimaryMetadataItems(HpcDataset dataset, String fileId,
        	                       List<HpcMetadataItem> metadataItems,
                                   boolean persist) 
                                  throws HpcException;
    
    /**
     * Update file primary metadata in a dataset.
     *
     * @param dataset The dataset.
     * @param fileId The file ID to update the metadata for.
     * @param primaryMetadata The metadata update request.
     * @param persist If set to true, the dataset will be persisted.
     * @return The updated file primary metadata domain object.
     * 
     * @throws HpcException
     */
    public HpcFilePrimaryMetadata 
           updatePrimaryMetadata(HpcDataset dataset, String fileId,
        		                 HpcFilePrimaryMetadata primaryMetadata, 
        		                 boolean persist) 
                                throws HpcException;
    
    /**
     * Associate a project with file in a registered dataset.
     *
     * @param dataset The dataset.
     * @param fileId The file ID to associate the projects with.
     * @param projectId The project ID to associate.
     * @param persist If set to true, the dataset will be persisted.
     * 
     * @throws HpcException
     */
    public void associateProject(HpcDataset dataset, String fileId,
        		                 String projectId, boolean persist) 
                                 throws HpcException;    
    
    /**
     * Create a new version of the file metadata.
     *
     * @param fileId The file ID the metadata is associated with.
     * @param metadata The file metadata.
     * @param persist If set to true, the dataset will be persisted.
     */
    public void addFileMetadataVersion(String fileId, 
    		                            HpcFileMetadata metadata,
    		                            boolean persist)
    		                           throws HpcException;
    
    /**
     * Set a data transfer request status based on a provided data transfer report.
     *
     * @param dataTransferRequest The data transfer request to update.
     * @param dataTransferReport The data transfer report.
     * @return True if the status was updated
     * 
     * @throws HpcException
     */
    public boolean setDataTransferRequestStatus(HpcDataTransferRequest dataTransferRequest, 
                                                HpcDataTransferReport dataTransferReport);
    
    /**
     * Get dataset.
     *
     * @param id The dataset ID.
     * @return The dataset.
     * 
     * @throws HpcException
     */
    public HpcDataset getDataset(String id) throws HpcException;
    
    /**
     * Get file.
     *
     * @param id The file ID.
     * @return The file.
     * 
     * @throws HpcException
     */
    public HpcFile getFile(String id) throws HpcException;
    
    /**
     * Get all datasets in the repository.
     *
     * @return HpcDataset collection, or null if no results found.
     * 
     * @throws HpcException
     */
    public List<HpcDataset> getDatasets() throws HpcException;
    
    /**
     * Get datasets associated with a specific user(s).
     *
     * @param userIds The list of user ids to match.
     * @param association The association between the dataset and the user.
     * @return HpcDataset collection, or null if no results found.
     * 
     * @throws HpcException
     */
    public List<HpcDataset> getDatasets(List<String> userIds, 
    		                            HpcDatasetUserAssociation association) 
        	                           throws HpcException;
    
    /**
     * Get datasets which has 'name' contained within its name.
     *
     * @param name Get datasets which 'name' is contained in their name.
     * @param regex If set to true, the 'name' will be queried as a regular expression. 
     * @return HpcDataset collection, or null if no results found.
     * 
     * @throws HpcException
     */
    public List<HpcDataset> getDatasets(String name, boolean regex) 
    		                           throws HpcException;
    
    /**
     * Get datasets by Project Id
     *
     * @param projectId Get datasets by Project Id
     * @return HpcDataset collection, or null if no results found.
     * 
     * @throws HpcException
     */
    public List<HpcDataset> getDatasetsByProjectId(String projectId) throws HpcException;
    
    /**
     * Get datasets by primary metadata.
     *
     * @param primaryMetadata The meatada to match. All datasets will be 
     *        returned if empty metadata is provided.
     * @return HpcDataset collection, or null if no results found.
     * 
     * @throws HpcException
     */
    public List<HpcDataset> getDatasets(HpcFilePrimaryMetadata primaryMetadata) 
    		                           throws HpcException;
    
    /**
     * Get datasets with specific data transfer status.
     *
     * @param dataTransferStatus The data transfer status to query for.
     * @param uploadRequests Search the upload data transfer requests.
     * @param downloadRequests Search the download data transfer requests.
     * @return HpcDataset collection, or null if no results found.
     * 
     * @throws HpcException
     */
	public List<HpcDataset> getDatasets(HpcDataTransferStatus dataTransferStatus,
			                            Boolean uploadRequests, 
                                        Boolean downloadRequests) throws HpcException;
	
    /**
     * Get datasets by registration date range.
     *
     * @param from The from date.
     * @param from The to date.
     * @return HpcDataset collection, or null if no results found.
     * 
     * @throws HpcException
     */
	public List<HpcDataset> getDatasets(Calendar from, Calendar to) 
			                           throws HpcException;
	
    /**
     * Find a file in dataset.
     *
     * @param dataset The dataset.
     * @param fileId The file ID to search for.
     * @return The file object, or null if not found.
     */
    public HpcFile getFile(HpcDataset dataset, String fileId);
	
    /**
     * Return true if a dataset with a specific name user association exists.
     *
     * @param nciUserId The user id to match.
     * @param association The association between the dataset and the user.
     * @return true if the dataset exists.
     * 
     * @throws HpcException
     */
    public boolean exists(String name, String nciUserId, 
    		              HpcDatasetUserAssociation association) 
        	             throws HpcException;
    
    /**
     * Persist dataset to the DB.
     *
     * @param dataset The dataset to be persisted.
     * 
     * @throws HpcException
     */
    public void persist(HpcDataset dataset) throws HpcException;
    
    /**
     * Persist file metadata history to the DB.
     *
     * @param metadataHistory The file metadata history to be persisted.
     * 
     * @throws HpcException
     */
    public void persist(HpcFileMetadataHistory metadataHistory) throws HpcException;
}

 
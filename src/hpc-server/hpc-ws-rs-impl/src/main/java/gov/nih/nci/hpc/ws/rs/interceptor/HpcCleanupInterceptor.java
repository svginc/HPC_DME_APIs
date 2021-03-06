/**
 * HpcCleanupInterceptor.java
 *
 * Copyright SVG, Inc.
 * Copyright Leidos Biomedical Research, Inc
 * 
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/HPC/LICENSE.txt for details.
 */

package gov.nih.nci.hpc.ws.rs.interceptor;

import gov.nih.nci.hpc.bus.HpcSystemBusService;
import gov.nih.nci.hpc.ws.rs.impl.HpcDataManagementRestServiceImpl;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * <p>
 * HPC Cleanup Interceptor.
 * </p>
 *
 * @author <a href="mailto:eran.rosenberg@nih.gov">Eran Rosenberg</a>
 * @version $Id$
 */

public class HpcCleanupInterceptor 
             extends AbstractPhaseInterceptor<Message> 
{
    //---------------------------------------------------------------------//
    // Instance members
    //---------------------------------------------------------------------//

    // The System Business Service instance.
	@Autowired
    private HpcSystemBusService systemBusService = null;
	
	// The logger instance.
	private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());
	
    //---------------------------------------------------------------------//
    // Constructors
    //---------------------------------------------------------------------//

    /**
     * Default Constructor.
     * 
     */
    private HpcCleanupInterceptor()
    {
    	super(Phase.SEND_ENDING);
    }
    
    //---------------------------------------------------------------------//
    // Methods
    //---------------------------------------------------------------------//
    
    //---------------------------------------------------------------------//
    // AbstractPhaseInterceptor<Message> Interface Implementation
    //---------------------------------------------------------------------//  

    @Override
    public void handleMessage(Message message) 
    {
    	// Close the connection to Data Management.
    	systemBusService.closeConnection();
    	
    	// Clean up files returned by the data object download service.
    	Object fileObj = message.getContextualProperty(
    			                    HpcDataManagementRestServiceImpl.DATA_OBJECT_DOWNLOAD_FILE_MC_ATTRIBUTE);
    	if(fileObj != null && fileObj instanceof File) {
    	   File file = (File) fileObj;
    	   if(!FileUtils.deleteQuietly(file)) {
    		  logger.error("Failed to delete a file: " + file.getAbsolutePath());
    	   }
    	}
    }
} 
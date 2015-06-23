/**
 * HpcManagedDatasetsServiceImpl.java
 *
 * Copyright SVG, Inc.
 * Copyright Leidos Biomedical Research, Inc
 * 
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/HPC/LICENSE.txt for details.
 */

package gov.nih.nci.hpc.service.impl;

import gov.nih.nci.hpc.service.HpcDataTransferService;

import gov.nih.nci.hpc.transfer.impl.GlobusOnlineDataTranfer;
import gov.nih.nci.hpc.transfer.HpcDataTransfer;
import gov.nih.nci.hpc.domain.dataset.HpcDataTransferLocations;
import gov.nih.nci.hpc.domain.error.HpcErrorType;
import gov.nih.nci.hpc.exception.HpcException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * HPC Data Transfer Service Implementation.
 * </p>
 *
 * @author <a href="mailto:Mahidhar.Narra@nih.gov">Mahidhar Narra</a>
 * @version $Id: HpcDataTransferService.java 
 */

public class HpcDataTransferServiceImpl implements HpcDataTransferService
{             
    // The logger instance.
	private final Logger logger = 
			             LoggerFactory.getLogger(this.getClass().getName());
	
    //---------------------------------------------------------------------//
    // Constructors
    //---------------------------------------------------------------------//
	
    /**
     * Default Constructor.
     * 
     * @throws HpcException Constructor is disabled.
     */
    private HpcDataTransferServiceImpl() throws HpcException
    {
    }   
     
    //---------------------------------------------------------------------//
    // HpcDataTransferService Interface Implementation
    //---------------------------------------------------------------------//  
    
    @Override
    public boolean transferDataset(HpcDataTransferLocations transferLocations,String username, String password) throws HpcException
    {   
    	try{
        	HpcDataTransfer hdt = new GlobusOnlineDataTranfer();
        	return hdt.transferDataset(transferLocations,username, password);    		
    	}catch(Exception ex)
    	{
    		throw new HpcException("Error while transfer",HpcErrorType.INVALID_REQUEST_INPUT);
    	}

    }

}
 
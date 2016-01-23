/**
 * HpcUserDAOImpl.java
 *
 * Copyright SVG, Inc.
 * Copyright Leidos Biomedical Research, Inc
 * 
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/HPC/LICENSE.txt for details.
 */

package gov.nih.nci.hpc.dao.postgresql.impl;

import gov.nih.nci.hpc.dao.HpcUserDAO;
import gov.nih.nci.hpc.domain.error.HpcErrorType;
import gov.nih.nci.hpc.domain.model.HpcUser;
import gov.nih.nci.hpc.domain.user.HpcIntegratedSystem;
import gov.nih.nci.hpc.domain.user.HpcIntegratedSystemAccount;
import gov.nih.nci.hpc.domain.user.HpcNciAccount;
import gov.nih.nci.hpc.exception.HpcException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

/**
 * <p>
 * HPC User DAO Implementation.
 * </p>
 *
 * @author <a href="mailto:eran.rosenberg@nih.gov">Eran Rosenberg</a>
 * @version $Id$
 */

public class HpcUserDAOImpl implements HpcUserDAO
{ 
    //---------------------------------------------------------------------//
    // Constants
    //---------------------------------------------------------------------//    
    
    // SQL Queries.
	public final static String UPSERT_SQL = 
		   "insert into public.\"HPC_USER\" ( " +
                    "\"USER_ID\", \"FIRST_NAME\", \"LAST_NAME\", " +
                    "\"GLOBUS_USERNAME\", \"GLOBUS_PASSWORD\", " +
                    "\"IRODS_USERNAME\", \"IRODS_PASSWORD\", " +
                    "\"CREATED\", \"LAST_UPDATED\") " +
                    "values (?, ?, ?, ?, ?, ?, ?, ?, ?) " +
           "on conflict(\"USER_ID\") do update set \"FIRST_NAME\"=excluded.\"FIRST_NAME\", " +
                                                  "\"LAST_NAME\"=excluded.\"LAST_NAME\", " +
                                                  "\"GLOBUS_USERNAME\"=excluded.\"GLOBUS_USERNAME\", " +
                                                  "\"GLOBUS_PASSWORD\"=excluded.\"GLOBUS_PASSWORD\", " +
                                                  "\"IRODS_USERNAME\"=excluded.\"IRODS_USERNAME\", " +
                                                  "\"IRODS_PASSWORD\"=excluded.\"IRODS_PASSWORD\", " +
                                                  "\"CREATED\"=excluded.\"CREATED\", " +
                                                  "\"LAST_UPDATED\"=excluded.\"LAST_UPDATED\"";
	public final static String GET_SQL = "select * from public.\"HPC_USER\" where \"USER_ID\" = ?";
	
    //---------------------------------------------------------------------//
    // Instance members
    //---------------------------------------------------------------------//
	
	// The Spring JDBC Template instance.
	@Autowired
	private JdbcTemplate jdbcTemplate = null;
	
	// Encryptor.
	@Autowired
	HpcEncryptor encryptor = null;
	
	// Row mapper.
	private HpcUserRowMapper rowMapper = new HpcUserRowMapper();
	
    //---------------------------------------------------------------------//
    // Constructors
    //---------------------------------------------------------------------//
	
    /**
     * Constructor for Spring Dependency Injection. 
     */
    private HpcUserDAOImpl() throws HpcException
    {
    }   
    
    //---------------------------------------------------------------------//
    // Methods
    //---------------------------------------------------------------------//
    
    //---------------------------------------------------------------------//
    // HpcManagedUserDAO Interface Implementation
    //---------------------------------------------------------------------//  
    
	@Override
	public void upsert(HpcUser user) throws HpcException
    {
		try {
		     jdbcTemplate.update(UPSERT_SQL,
		                         user.getNciAccount().getUserId(),
		                         user.getNciAccount().getFirstName(),
		                         user.getNciAccount().getLastName(),
		                         user.getDataTransferAccount().getUsername(),
		                         encryptor.encrypt(user.getDataTransferAccount().getPassword()),
		                         user.getDataManagementAccount().getUsername(),
		                         encryptor.encrypt(user.getDataManagementAccount().getPassword()),
		                         user.getCreated(),
		                         user.getLastUpdated());
		     
		} catch(DataAccessException e) {
			    throw new HpcException("Failed to upsert a user: " + e.getMessage(),
			    		               HpcErrorType.DATABASE_ERROR, e);
		}
    }
	
	@Override 
	public HpcUser getUser(String nciUserId) throws HpcException
	{
		try {
		     return jdbcTemplate.queryForObject(GET_SQL, rowMapper, nciUserId);
		     
		} catch(IncorrectResultSizeDataAccessException notFoundEx) {
			    return null;
			    
		} catch(DataAccessException e) {
		        throw new HpcException("Failed to get a user: " + e.getMessage(),
		    	    	               HpcErrorType.DATABASE_ERROR, e);
		}
	}
	
    //---------------------------------------------------------------------//
    // Helper Methods
    //---------------------------------------------------------------------//  
	
	// HpcUser Table to Object mapper.
	private class HpcUserRowMapper implements RowMapper<HpcUser>
	{
		public HpcUser mapRow(ResultSet rs, int rowNum) throws SQLException 
		{
			HpcNciAccount nciAccount = new HpcNciAccount();
			nciAccount.setUserId(rs.getString("USER_ID"));
			nciAccount.setFirstName(rs.getString("FIRST_NAME"));
			nciAccount.setLastName(rs.getString("LAST_NAME"));
			
			HpcIntegratedSystemAccount dataTransferAccount = new HpcIntegratedSystemAccount();
			dataTransferAccount.setIntegratedSystem(HpcIntegratedSystem.GLOBUS);
			dataTransferAccount.setUsername(rs.getString("GLOBUS_USERNAME"));
			dataTransferAccount.setPassword(encryptor.decrypt(rs.getBytes(("GLOBUS_PASSWORD"))));
			
			HpcIntegratedSystemAccount dataManagementAccount = new HpcIntegratedSystemAccount();
			dataManagementAccount.setIntegratedSystem(HpcIntegratedSystem.IRODS);
			dataManagementAccount.setUsername(rs.getString("IRODS_USERNAME"));
			dataManagementAccount.setPassword(encryptor.decrypt(rs.getBytes(("IRODS_PASSWORD"))));
			
        	HpcUser user = new HpcUser();
        	Calendar created = new GregorianCalendar();
        	created.setTime(rs.getDate("CREATED"));
        	user.setCreated(created);
        	
        	Calendar lastUpdated = new GregorianCalendar();
        	lastUpdated.setTime(rs.getDate("LAST_UPDATED"));
        	user.setLastUpdated(lastUpdated);
        	
        	user.setNciAccount(nciAccount);
        	user.setDataTransferAccount(dataTransferAccount);
        	user.setDataManagementAccount(dataManagementAccount);
            
            return user;
		}
	}
}

 
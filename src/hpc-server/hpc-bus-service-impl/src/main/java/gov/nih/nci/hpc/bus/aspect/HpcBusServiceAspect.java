/**
 * HpcBusServiceAspect.java
 *
 * Copyright SVG, Inc.
 * Copyright Leidos Biomedical Research, Inc
 * 
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/HPC/LICENSE.txt for details.
 */

package gov.nih.nci.hpc.bus.aspect;

import gov.nih.nci.hpc.domain.error.HpcErrorType;
import gov.nih.nci.hpc.domain.notification.HpcEventPayloadEntry;
import gov.nih.nci.hpc.domain.notification.HpcEventType;
import gov.nih.nci.hpc.domain.notification.HpcNotificationDeliveryMethod;
import gov.nih.nci.hpc.exception.HpcException;
import gov.nih.nci.hpc.service.HpcNotificationService;

import java.util.ArrayList;
import java.util.List;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * <p>
 * HPC Bus Services Aspect - implement cross cutting concerns:
 * 1. Basic business service profiler - log execution time.
 * 2. Exception logger - logging when exceptions are thrown by Bus Services API impl.
 * 3. Notify System Administrator if an error occurred with an integrated system (iRODS, LDAP, CLEVERSAFE, GLOBUS, POSTGRESQL)
 * </p>
 *
 * @author <a href="mailto:eran.rosenberg@nih.gov">Eran Rosenberg</a>
 */

@Aspect
public class HpcBusServiceAspect
{   
    //---------------------------------------------------------------------//
    // Instance members
    //---------------------------------------------------------------------//

    // Application service instances.
	
	@Autowired
    private HpcNotificationService notificationService = null;
	
	// The system administrator NCI user ID.
	private String systemAdministratorUserId = null;
	
    // The logger instance.
	private final Logger logger = 
			             LoggerFactory.getLogger(this.getClass().getName());
	
    //---------------------------------------------------------------------//
    // Constructors
    //---------------------------------------------------------------------//
	
    /**
     * Default Constructor disabled.
     * 
     */
    private HpcBusServiceAspect() throws HpcException
    {
    	throw new HpcException("Default constructor disabled", 
    		                   HpcErrorType.SPRING_CONFIGURATION_ERROR);
    }  
    
    /**
     * Constructor for Spring Dependency Injection.
     * 
     * @param systemAdministratorUserId The system administrator NCI user ID.
     */
    private HpcBusServiceAspect(String systemAdministratorUserId)
    {
    	this.systemAdministratorUserId = systemAdministratorUserId;
    }  
	
    //---------------------------------------------------------------------//
    // Pointcuts.
    //---------------------------------------------------------------------//
	
    /** 
     * Join Point for all business services that are defined by an interface
     * in the gov.nih.nci.hpc.bus package, and implemented by a concrete class
     * in gov.nih.nci.hpc.bus.impl
     */
	@Pointcut("within(gov.nih.nci.hpc.bus.impl.*) && execution(* gov.nih.nci.hpc.bus.*.*(..))")
	private void busServices() 
	{
		// Intentionally left blank.
	}
	
    //---------------------------------------------------------------------//
    // Advices.
    //---------------------------------------------------------------------//
    
    /** 
     * Advice that logs business service execution time. 
     * 
     * @param joinPoint The join point.
     * @return The advised object return.
     * @throws Throwable The advised object exception.
     */
	@Around("busServices()")
	public Object profileService(ProceedingJoinPoint joinPoint) throws Throwable
    {
		long start = System.currentTimeMillis();
		String businessService = joinPoint.getSignature().toShortString();
		logger.info(businessService + " business service invoked.");
		
		try {
			 return joinPoint.proceed();
			 
		} finally {
			       long executionTime = System.currentTimeMillis() - start;
			       logger.debug(businessService + " business service completed in " + 
			                    executionTime + " milliseconds.");
		}
    }
	
    /** 
     * Advice that logs business service exception. 
     * 
     * @param joinPoint The join point.
     * @param exception The exception to log.
     * @throws Throwable The advised object exception.
     */
	@AfterThrowing (pointcut = "busServices()", throwing = "exception")
    public void logException(JoinPoint joinPoint, HpcException exception) throws Throwable  
	{
		logger.error(joinPoint.getSignature().toShortString() + 
				     " business service error:  " + exception.getMessage(), exception); 
	}
	
    /** 
     * Advice that alerts a system administrator of a problem with an integrated system. 
     * 
     * @param joinPoint The join point.
     * @param exception The exception to log.
     * @throws Throwable The advised object exception.
     */
	@AfterThrowing (pointcut = "busServices()", throwing = "exception")
    public void notifySystemAdmin(JoinPoint joinPoint, HpcException exception) throws Throwable  
	{
		if(exception.getIntegratedSystem() != null) {
		   logger.info("Sending a notification to system admin: " + exception.getMessage());
		   
		   // Create a payload containing the exception data.
		   List<HpcEventPayloadEntry> payloadEntries = new ArrayList<>();
		   
		   HpcEventPayloadEntry integratedSystemPayloadEntry = new HpcEventPayloadEntry();
		   integratedSystemPayloadEntry.setAttribute("INTEGRATED_SYSTEM");
		   integratedSystemPayloadEntry.setValue(exception.getIntegratedSystem().value());
		   payloadEntries.add(integratedSystemPayloadEntry);
		   
		   HpcEventPayloadEntry errorMessage = new HpcEventPayloadEntry();
		   errorMessage.setAttribute("ERROR_MESSAGE");
		   errorMessage.setValue(exception.getMessage());
		   payloadEntries.add(errorMessage);
		   
		   HpcEventPayloadEntry stackTrace = new HpcEventPayloadEntry();
		   stackTrace.setAttribute("STACK_TRACE");
		   stackTrace.setValue(exception.getStackTraceString());
		   payloadEntries.add(stackTrace);
		   
		   // Send the notification.
		   notificationService.sendNotification(systemAdministratorUserId, 
				                                HpcEventType.INTEGRATED_SYSTEM_ERROR, 
			                                    payloadEntries, HpcNotificationDeliveryMethod.EMAIL);
		}
	}
	
    /** 
     * Advice that set up the system account as the request invoker
     * 
     * @param joinPoint The join point.
     * @return The advised object return.
     * @throws Throwable The advised object exception.
     */
	@Around("busServices() && @annotation(gov.nih.nci.hpc.bus.aspect.SystemBusServiceImpl)")
	public Object setSystemRequestInvoker(ProceedingJoinPoint joinPoint) throws Throwable
    {
		logger.info("ERAN: set system request invoker");
		
		try {
			 return joinPoint.proceed();
			 
		} finally {
			       logger.info("ERAN: unset system request invoker"); 
		}
    }
}

 
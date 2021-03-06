/*******************************************************************************
 * Copyright SVG, Inc.
 * Copyright Leidos Biomedical Research, Inc.
 *  
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See https://github.com/CBIIT/HPC_DME_APIs/LICENSE.txt for details.
 ******************************************************************************/
package gov.nih.nci.hpc.cli;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import gov.nih.nci.hpc.cli.util.Constants;
import gov.nih.nci.hpc.cli.util.HpcConfigProperties;

@Component
public abstract class HPCBatchClient {
	@Autowired
	protected HpcConfigProperties configProperties;
	protected String hpcServerURL;
	protected String hpcServerProxyURL;
	protected String hpcServerProxyPort;
	protected String globusNexusURL;
	protected String globusURL;
	protected String hpcCertPath;
	protected String hpcCertPassword;
	protected String hpcDataService;
	protected String hpcCollectionService;
	protected String logDir;
	protected FileWriter fileLogWriter = null;
	protected FileWriter fileRecordWriter = null;
	protected CSVPrinter csvFilePrinter = null;
	protected String loginFile = null;
	protected String globusLoginFile = null;
	protected String logFile = null;
	protected String tokenFile = null;
	protected String logRecordsFile = null;
	protected boolean headerAdded = false;
	protected boolean validateMD5 = false;
	protected boolean inputCredentials = true;
	protected int bufferSize = 0;
	protected int threadCount = 1;
    protected final Logger logger = LoggerFactory.getLogger(getClass().getName());

	public HPCBatchClient() {

	}

	protected void preprocess() {
	    logger.debug("preprocess");
	    try {
			String threadStr = configProperties.getProperty("hpc.job.thread.count");
			logger.debug("hpc.job.thread.count "+threadStr);
			threadCount = Integer.parseInt(threadStr);
		} catch (Exception e) {
		    logger.error("hpc.job.thread.count error " +e.getMessage());
			threadCount = 3;
		}

		hpcServerURL = configProperties.getProperty("hpc.server.url");
		logger.debug("hpc.server.url "+hpcServerURL);
		hpcServerProxyURL = configProperties.getProperty("hpc.server.proxy.url");
		logger.debug("hpc.server.proxy.url "+hpcServerProxyURL);
		hpcServerProxyPort = configProperties.getProperty("hpc.server.proxy.port");
		logger.debug("hpc.server.proxy.port "+hpcServerProxyPort);
		globusNexusURL = configProperties.getProperty("globus.nexus.url");
		logger.debug("globus.nexus.url "+globusNexusURL);
		String checkMD5 = configProperties.getProperty("validate.md5.checksum");
		logger.debug("validate.md5.checksum "+checkMD5);
		if(checkMD5 != null && checkMD5.equalsIgnoreCase("true"))
			validateMD5 = true;	
		globusURL = configProperties.getProperty("globus.url");
		logger.debug("globus.url "+globusURL);
		hpcDataService = configProperties.getProperty("hpc.dataobject.service");
		logger.debug("hpc.dataobject.service "+hpcDataService);
		hpcCertPath = configProperties.getProperty("hpc.ssl.keystore.path");
		logger.debug("hpc.ssl.keystore.path "+hpcCertPath);
		hpcCertPassword = configProperties.getProperty("hpc.ssl.keystore.password");
		logger.debug("hpc.ssl.keystore.password "+hpcCertPassword);
		logDir = configProperties.getProperty("hpc.error-log.dir");
		logger.debug("hpc.error-log.dir "+logDir);
		loginFile = configProperties.getProperty("hpc.login.credentials");
		logger.debug("hpc.login.credentials "+loginFile);
		tokenFile = configProperties.getProperty("hpc.login.token");
		logger.debug("hpc.login.token "+tokenFile);
		globusLoginFile = configProperties.getProperty("hpc.globus.login.token");
        logger.debug("hpc.globus.login.token "+globusLoginFile);
		hpcCollectionService = configProperties.getProperty("hpc.collection.service");
        logger.debug("hpc.collection.service "+hpcCollectionService);
		String bufferSizeStr = configProperties.getProperty("upload.buffer.size");
        logger.debug("upload.buffer.size "+bufferSizeStr);
		try {
			bufferSize = Integer.parseInt(bufferSizeStr);
		} catch (Exception e) {
			System.out.println("Invalid upload.buffer.size value. Setting it to 100000");
			bufferSize = 100000;
		}
		
		String basePath = System.getProperty("HPC_DM_UTILS");
		logger.debug("HPC_DM_UTILS "+basePath);
		if(basePath == null)
		{
			System.out.println("System Property HPC_DM_UTILS is missing");
			System.exit(1);
		}
		
		if(tokenFile != null)
			tokenFile = basePath + File.separator + tokenFile;
		if(loginFile != null)
			loginFile = basePath + File.separator + loginFile;
		globusLoginFile = basePath + File.separator + globusLoginFile;
		logger.debug("globusLoginFile "+globusLoginFile);
		hpcCertPath = basePath + File.separator + hpcCertPath;
		logger.debug("hpcCertPath "+hpcCertPath);
		
		initializeLog();
		
	}

	protected abstract void initializeLog();

	public String process(String fileName) {
		preprocess();
        logger.debug("process");
		BufferedReader bufferedReader = null;
		try {
			String userId = null;
			String password = null;
			String authToken = null;
			if (loginFile == null && tokenFile == null && inputCredentials) {
				jline.console.ConsoleReader reader = new jline.console.ConsoleReader();
				reader.setExpandEvents(false);
				System.out.println("Enter NCI Login UserId:");
				userId = reader.readLine();

				System.out.println("Enter NCI Login password:");
				password = reader.readLine(new Character('*'));
			} else if ( tokenFile != null){
				bufferedReader = new BufferedReader(new FileReader(tokenFile));
				String line = bufferedReader.readLine();
				if (line.isEmpty())
				{
					System.out.println("Invalid Login token in " + tokenFile);
					return Constants.CLI_1;
				}
				else {
					authToken = line;
				}
			} else if ( loginFile != null){
				bufferedReader = new BufferedReader(new FileReader(loginFile));
				String line = bufferedReader.readLine();
				if (line.indexOf(":") == -1)
				{
					System.out.println("Invalid Login credentials in " + tokenFile);
					return Constants.CLI_1;
				}
				else {
					userId = line.substring(0, line.indexOf(":"));
					password = line.substring(line.indexOf(":") + 1);
				}
			}

			logger.debug("fileName "+fileName);
			logger.debug("userId "+userId);
			String errorCode = processFile(fileName, userId, password, authToken);
			logger.debug("errorCode "+errorCode);
			if (errorCode == null || errorCode.equals(Constants.CLI_SUCCESS))
			{
				System.out.println("Cmd process Successful");
				return Constants.CLI_SUCCESS;
			}
			else
			{
				System.out.println("Cmd process is not Successful. Please error log for details.");
				return errorCode;
			}
		} catch (IOException e) {
			System.out.println("Failed to run batch registration" + e.getMessage());
			return Constants.CLI_1;
		} finally {
			if (bufferedReader != null) {
				try {
					bufferedReader.close();
				} catch (IOException e) {
					System.out.println("Failed to read Login credentials file. "+loginFile);
				}
			}
			postProcess();
		}
	}

	protected abstract String processFile(String fileName, String userId, String password, String authToken);

	protected void postProcess() {
	  logger.debug("postProcess");
		try {
			if (fileLogWriter != null) {
				fileLogWriter.flush();
				fileLogWriter.close();
			}

			if (fileRecordWriter != null) {
				fileRecordWriter.flush();
				fileRecordWriter.close();
			}

			if (csvFilePrinter != null)
				csvFilePrinter.close();

		} catch (IOException e) {
			System.out.println("Error while flushing/closing fileWriter/csvPrinter !!!");
		}

	}

	protected void addErrorToLog(String error, int recordLineNumber) throws IOException {
		fileLogWriter.write(recordLineNumber + ": " + error);
		fileLogWriter.write("\n");
	}

	protected void addErrorToLog(String path, String error) throws IOException {
		fileLogWriter.write(path + ": " + error);
		fileLogWriter.write("\n");
	}

	protected void addRecordToLog(String record) throws IOException {
		fileRecordWriter.write(record);
		fileRecordWriter.write("\n");
	}

	protected void addRecordToLog(CSVRecord record, Map<String, Integer> headers) throws IOException {
		Object[] headerArray = new ArrayList<Object>(headers.keySet()).toArray();
		if (!headerAdded)
			csvFilePrinter.printRecord(headerArray);
		else
			csvFilePrinter.println();
		for (Entry<String, Integer> entry : headers.entrySet()) {
			csvFilePrinter.print(record.get(entry.getKey()));
		}
	}

}

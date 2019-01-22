package gov.nih.nci.hpc.integration.s3.impl;

import static gov.nih.nci.hpc.integration.HpcDataTransferProxy.getArchiveDestinationLocation;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.model.CopyObjectRequest;
import com.amazonaws.services.s3.model.CopyObjectResult;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.GetObjectMetadataRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.transfer.Download;
import com.amazonaws.services.s3.transfer.Upload;
import gov.nih.nci.hpc.domain.datamanagement.HpcPathAttributes;
import gov.nih.nci.hpc.domain.datatransfer.HpcArchive;
import gov.nih.nci.hpc.domain.datatransfer.HpcArchiveType;
import gov.nih.nci.hpc.domain.datatransfer.HpcDataObjectDownloadRequest;
import gov.nih.nci.hpc.domain.datatransfer.HpcDataObjectUploadRequest;
import gov.nih.nci.hpc.domain.datatransfer.HpcDataObjectUploadResponse;
import gov.nih.nci.hpc.domain.datatransfer.HpcDataTransferDownloadReport;
import gov.nih.nci.hpc.domain.datatransfer.HpcDataTransferDownloadStatus;
import gov.nih.nci.hpc.domain.datatransfer.HpcDataTransferType;
import gov.nih.nci.hpc.domain.datatransfer.HpcDataTransferUploadStatus;
import gov.nih.nci.hpc.domain.datatransfer.HpcFileLocation;
import gov.nih.nci.hpc.domain.datatransfer.HpcS3Account;
import gov.nih.nci.hpc.domain.datatransfer.HpcS3DownloadDestination;
import gov.nih.nci.hpc.domain.datatransfer.HpcS3UploadSource;
import gov.nih.nci.hpc.domain.error.HpcErrorType;
import gov.nih.nci.hpc.domain.metadata.HpcMetadataEntry;
import gov.nih.nci.hpc.domain.user.HpcIntegratedSystem;
import gov.nih.nci.hpc.domain.user.HpcIntegratedSystemAccount;
import gov.nih.nci.hpc.exception.HpcException;
import gov.nih.nci.hpc.integration.HpcDataTransferProgressListener;
import gov.nih.nci.hpc.integration.HpcDataTransferProxy;

/**
 * HPC Data Transfer Proxy S3 Implementation.
 *
 * @author <a href="mailto:eran.rosenberg@nih.gov">Eran Rosenberg</a>
 */
public class HpcDataTransferProxyImpl implements HpcDataTransferProxy {
  // ---------------------------------------------------------------------//
  // Constants
  // ---------------------------------------------------------------------//

  // The expiration of streaming data request from Cleversafe to AWS S3.
  private static final int S3_STREAM_EXPIRATION = 96;

  // AWS Error XML XPATH
  private static final String ERROR_CODE_XPATH = "/Error/Code";
  private static final String ERROR_MESSAGE_XPATH = "/Error/Message";

  // ---------------------------------------------------------------------//
  // Instance members
  // ---------------------------------------------------------------------//

  // The S3 connection instance.
  @Autowired private HpcS3Connection s3Connection = null;

  // The S3 download executor.
  @Autowired Executor s3Executor = null;

  // The logger instance.
  private final Logger logger = LoggerFactory.getLogger(getClass().getName());

  // ---------------------------------------------------------------------//
  // Constructors
  // ---------------------------------------------------------------------//

  /** Constructor for spring injection. */
  private HpcDataTransferProxyImpl() {}

  // ---------------------------------------------------------------------//
  // Methods
  // ---------------------------------------------------------------------//

  // ---------------------------------------------------------------------//
  // HpcDataTransferProxy Interface Implementation
  // ---------------------------------------------------------------------//

  @Override
  public Object authenticate(HpcIntegratedSystemAccount dataTransferAccount, String url)
      throws HpcException {
    return s3Connection.authenticate(dataTransferAccount, url);
  }

  @Override
  public Object authenticate(HpcS3Account s3Account) throws HpcException {
    return s3Connection.authenticate(s3Account);
  }

  @Override
  public HpcDataObjectUploadResponse uploadDataObject(
      Object authenticatedToken,
      HpcDataObjectUploadRequest uploadRequest,
      HpcArchive baseArchiveDestination,
      Integer uploadRequestURLExpiration,
      HpcDataTransferProgressListener progressListener)
      throws HpcException {
    if (uploadRequest.getGlobusUploadSource() != null) {
      throw new HpcException("Invalid upload source", HpcErrorType.UNEXPECTED_ERROR);
    }

    // Calculate the archive destination.
    HpcFileLocation archiveDestinationLocation =
        getArchiveDestinationLocation(
            baseArchiveDestination.getFileLocation(),
            uploadRequest.getPath(),
            uploadRequest.getCallerObjectId(),
            baseArchiveDestination.getType(),
            false);

    // If the archive destination file exists, generate a new archive destination w/ unique path.
    if (getPathAttributes(authenticatedToken, archiveDestinationLocation, false).getExists()) {
      archiveDestinationLocation =
          getArchiveDestinationLocation(
              baseArchiveDestination.getFileLocation(),
              uploadRequest.getPath(),
              uploadRequest.getCallerObjectId(),
              baseArchiveDestination.getType(),
              true);
    }

    if (uploadRequest.getGenerateUploadRequestURL()) {
      // Generate an upload request URL for the caller to use to upload directly.
      return generateUploadRequestURL(
          authenticatedToken,
          archiveDestinationLocation,
          uploadRequestURLExpiration,
          uploadRequest.getUploadRequestURLChecksum());

    } else if (uploadRequest.getSourceFile() != null) {
      // Upload a file
      return uploadDataObject(
          authenticatedToken,
          uploadRequest.getSourceFile(),
          archiveDestinationLocation,
          progressListener,
          baseArchiveDestination.getType());
    } else {
      // Upload from AWS S3 source.
      return uploadDataObject(
          authenticatedToken,
          uploadRequest.getS3UploadSource(),
          archiveDestinationLocation,
          uploadRequest.getSourceSize(),
          progressListener);
    }
  }

  @Override
  public String downloadDataObject(
      Object authenticatedToken,
      HpcDataObjectDownloadRequest downloadRequest,
      HpcArchive baseArchiveDestination,
      HpcDataTransferProgressListener progressListener)
      throws HpcException {
    if (downloadRequest.getFileDestination() != null) {
      // This is a download request to a local file.
      return downloadDataObject(
          authenticatedToken,
          downloadRequest.getArchiveLocation(),
          downloadRequest.getFileDestination(),
          progressListener);
    } else {
      // This is a download to AWS S3 destination.
      return downloadDataObject(
          authenticatedToken,
          downloadRequest.getArchiveLocation(),
          downloadRequest.getS3Destination(),
          progressListener);
    }
  }

  @Override
  public String generateDownloadRequestURL(
      Object authenticatedToken,
      HpcFileLocation archiveSourceLocation,
      Integer downloadRequestURLExpiration)
      throws HpcException {

    // Calculate the URL expiration date.
    Date expiration = new Date();
    expiration.setTime(expiration.getTime() + 1000 * 60 * 60 * downloadRequestURLExpiration);

    // Create a URL generation request.
    GeneratePresignedUrlRequest generatePresignedUrlRequest =
        new GeneratePresignedUrlRequest(
                archiveSourceLocation.getFileContainerId(), archiveSourceLocation.getFileId())
            .withMethod(HttpMethod.GET)
            .withExpiration(expiration);

    // Generate the pre-signed URL.
    URL url =
        s3Connection
            .getTransferManager(authenticatedToken)
            .getAmazonS3Client()
            .generatePresignedUrl(generatePresignedUrlRequest);

    return url.toString();
  }

  @Override
  public String copyDataObject(
      Object authenticatedToken,
      HpcFileLocation sourceFile,
      HpcFileLocation destinationFile,
      HpcArchive baseArchiveDestination,
      List<HpcMetadataEntry> metadataEntries)
      throws HpcException {

    // Create a S3 update request w/ new metadata. Copy the file to itself.
    CopyObjectRequest copyRequest =
        new CopyObjectRequest(
                sourceFile.getFileContainerId(),
                sourceFile.getFileId(),
                destinationFile.getFileContainerId(),
                destinationFile.getFileId())
            .withNewObjectMetadata(toS3Metadata(metadataEntries));

    try {
      CopyObjectResult copyResult =
          s3Connection
              .getTransferManager(authenticatedToken)
              .getAmazonS3Client()
              .copyObject(copyRequest);
      return copyResult != null ? copyResult.getETag() : null;

    } catch (AmazonServiceException ase) {
      throw new HpcException(
          "[S3] Failed to copy file: " + copyRequest,
          HpcErrorType.DATA_TRANSFER_ERROR,
          HpcIntegratedSystem.CLEVERSAFE,
          ase);

    } catch (AmazonClientException ace) {
      throw new HpcException(
          "[S3] Failed to copy file: " + copyRequest,
          HpcErrorType.DATA_TRANSFER_ERROR,
          HpcIntegratedSystem.CLEVERSAFE,
          ace);
    }
  }

  @Override
  public void deleteDataObject(
      Object authenticatedToken, HpcFileLocation fileLocation, HpcArchive baseArchiveDestination)
      throws HpcException {
    // Create a S3 delete request.
    DeleteObjectRequest deleteRequest =
        new DeleteObjectRequest(fileLocation.getFileContainerId(), fileLocation.getFileId());
    try {
      s3Connection
          .getTransferManager(authenticatedToken)
          .getAmazonS3Client()
          .deleteObject(deleteRequest);

    } catch (AmazonServiceException ase) {
      throw new HpcException(
          "[S3] Failed to delete file: " + deleteRequest,
          HpcErrorType.DATA_TRANSFER_ERROR,
          HpcIntegratedSystem.CLEVERSAFE,
          ase);

    } catch (AmazonClientException ace) {
      throw new HpcException(
          "[S3] Failed to delete file: " + deleteRequest,
          HpcErrorType.DATA_TRANSFER_ERROR,
          HpcIntegratedSystem.CLEVERSAFE,
          ace);
    }
  }

  @Override
  public HpcPathAttributes getPathAttributes(
      Object authenticatedToken, HpcFileLocation fileLocation, boolean getSize)
      throws HpcException {
    HpcPathAttributes pathAttributes = new HpcPathAttributes();
    GetObjectMetadataRequest request = null;

    try {
      boolean objectExists =
          s3Connection
              .getTransferManager(authenticatedToken)
              .getAmazonS3Client()
              .doesObjectExist(fileLocation.getFileContainerId(), fileLocation.getFileId());

      pathAttributes.setIsAccessible(true);
      pathAttributes.setIsDirectory(false);
      pathAttributes.setExists(objectExists);
      pathAttributes.setIsFile(objectExists);

      // Optionally get the file size.
      if (getSize && objectExists) {
        // Create a S3 metadata request.
        request =
            new GetObjectMetadataRequest(
                fileLocation.getFileContainerId(), fileLocation.getFileId());
        pathAttributes.setSize(
            s3Connection
                .getTransferManager(authenticatedToken)
                .getAmazonS3Client()
                .getObjectMetadata(request)
                .getContentLength());
      }

    } catch (AmazonServiceException ase) {
      throw new HpcException(
          "[S3] Failed to get object or metadata: " + ase.getMessage(),
          HpcErrorType.DATA_TRANSFER_ERROR,
          HpcIntegratedSystem.CLEVERSAFE,
          ase);

    } catch (AmazonClientException ace) {
      throw new HpcException(
          "[S3] Failed to get object or metadata: " + ace.getMessage(),
          HpcErrorType.DATA_TRANSFER_ERROR,
          HpcIntegratedSystem.CLEVERSAFE,
          ace);
    }

    return pathAttributes;
  }

  @Override
  public String getFileContainerName(Object authenticatedToken, String fileContainerId)
      throws HpcException {
    return fileContainerId;
  }

  // ---------------------------------------------------------------------//
  // Helper Methods
  // ---------------------------------------------------------------------//

  /**
   * Upload a data object file.
   *
   * @param authenticatedToken An authenticated token.
   * @param sourceFile The file to upload.
   * @param archiveDestinationLocation The archive destination location.
   * @param progressListener (Optional) a progress listener for async notification on transfer
   *     completion.
   * @param archiveType The archive type.
   * @return A data object upload response.
   * @throws HpcException on data transfer system failure.
   */
  private HpcDataObjectUploadResponse uploadDataObject(
      Object authenticatedToken,
      File sourceFile,
      HpcFileLocation archiveDestinationLocation,
      HpcDataTransferProgressListener progressListener,
      HpcArchiveType archiveType)
      throws HpcException {
    // Create a S3 upload request.
    PutObjectRequest request =
        new PutObjectRequest(
            archiveDestinationLocation.getFileContainerId(),
            archiveDestinationLocation.getFileId(),
            sourceFile);

    // Upload the data.
    Upload s3Upload = null;
    Calendar dataTransferStarted = Calendar.getInstance();
    Calendar dataTransferCompleted = null;
    try {
      s3Upload = s3Connection.getTransferManager(authenticatedToken).upload(request);
      if (progressListener == null) {
        // Upload synchronously.
        s3Upload.waitForUploadResult();
        dataTransferCompleted = Calendar.getInstance();
      } else {
        // Upload asynchronously.
        s3Upload.addProgressListener(
            new HpcS3ProgressListener(
                progressListener,
                "upload to"
                    + archiveDestinationLocation.getFileContainerId()
                    + ":"
                    + archiveDestinationLocation.getFileId()));
      }

    } catch (AmazonClientException ace) {
      throw new HpcException(
          "[S3] Failed to upload file.",
          HpcErrorType.DATA_TRANSFER_ERROR,
          HpcIntegratedSystem.CLEVERSAFE,
          ace);

    } catch (InterruptedException ie) {
      Thread.currentThread().interrupt();
    }

    // Upload completed. Create and populate the response object.
    HpcDataObjectUploadResponse uploadResponse = new HpcDataObjectUploadResponse();
    uploadResponse.setArchiveLocation(archiveDestinationLocation);
    uploadResponse.setDataTransferType(HpcDataTransferType.S_3);
    uploadResponse.setDataTransferStarted(dataTransferStarted);
    uploadResponse.setDataTransferCompleted(dataTransferCompleted);
    uploadResponse.setDataTransferRequestId(String.valueOf(s3Upload.hashCode()));
    uploadResponse.setSourceSize(sourceFile.length());
    if (archiveType.equals(HpcArchiveType.ARCHIVE)) {
      uploadResponse.setDataTransferStatus(HpcDataTransferUploadStatus.ARCHIVED);
    } else {
      uploadResponse.setDataTransferStatus(HpcDataTransferUploadStatus.IN_TEMPORARY_ARCHIVE);
    }

    return uploadResponse;
  }

  /**
   * Upload a data object file from AWS S3 source.
   *
   * @param authenticatedToken An authenticated token.
   * @param sourceFile The file to upload.
   * @param archiveDestinationLocation The archive destination location.
   * @param size the size of the file to upload.
   * @param progressListener (Optional) a progress listener for async notification on transfer
   *     completion.
   * @return A data object upload response.
   * @throws HpcException on data transfer system failure.
   */
  private HpcDataObjectUploadResponse uploadDataObject(
      Object authenticatedToken,
      HpcS3UploadSource s3UploadSource,
      HpcFileLocation archiveDestinationLocation,
      Long size,
      HpcDataTransferProgressListener progressListener)
      throws HpcException {
    if (progressListener == null) {
      throw new HpcException(
          "[S3] No progress listener provided for a upload from AWS S3 destination",
          HpcErrorType.UNEXPECTED_ERROR);
    }
    if (size == null) {
      throw new HpcException(
          "[S3] File size not provided for an upload from AWS S3", HpcErrorType.UNEXPECTED_ERROR);
    }

    // Authenticate the S3 account.
    Object s3AccountAuthenticatedToken = s3Connection.authenticate(s3UploadSource.getAccount());

    // Generate a download pre-signed URL for the requested data file from AWS.
    String sourceURL =
        generateDownloadRequestURL(
            s3AccountAuthenticatedToken, s3UploadSource.getSourceLocation(), S3_STREAM_EXPIRATION);

    Calendar dataTransferStarted = Calendar.getInstance();
    CompletableFuture<Void> s3TransferManagerUploadFuture =
        CompletableFuture.runAsync(
            () -> {
              try {
                // Create source URL and open a connection to it.
                InputStream sourceInputStream = new URL(sourceURL).openStream();

                // Create a S3 upload request.
                ObjectMetadata metadata = new ObjectMetadata();
                metadata.setContentLength(size);
                PutObjectRequest request =
                    new PutObjectRequest(
                        archiveDestinationLocation.getFileContainerId(),
                        archiveDestinationLocation.getFileId(),
                        sourceInputStream,
                        metadata);

                // Set the read limit on the request to avoid AWSreset exceptions.
                request.getRequestClientOptions().setReadLimit(getReadLimit(size));

                // Upload asynchronously. AWS transfer manager will perform the upload in its own managed thread.
                Upload s3Upload =
                    s3Connection.getTransferManager(authenticatedToken).upload(request);

                // Attach a progress listener.
                String sourceDestinationLogMessage =
                    "upload from "
                        + s3UploadSource.getSourceLocation().getFileContainerId()
                        + ":"
                        + s3UploadSource.getSourceLocation().getFileId();
                s3Upload.addProgressListener(
                    new HpcS3ProgressListener(progressListener, sourceDestinationLogMessage));

                logger.info(
                    "S3 upload AWS->Cleversafe [{}] started. Source size - {} bytes. Read limit - {}",
                    sourceDestinationLogMessage,
                    size,
                    request.getRequestClientOptions().getReadLimit());

                // Wait for the result. This ensures the input stream to the URL remains opened and connected until the download is complete.
                // Note that this wait for AWS transfer manager completion is done in a separate thread (from s3Executor pool), so callers to
                // the API don't wait.
                s3Upload.waitForUploadResult();

              } catch (AmazonClientException | HpcException | IOException e) {
                logger.error("[S3] Failed to upload from AWS S3 destination: " + e.getMessage(), e);
                progressListener.transferFailed(e.getMessage());

              } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
              }
            },
            s3Executor);

    // Create and populate the response object.
    HpcDataObjectUploadResponse uploadResponse = new HpcDataObjectUploadResponse();
    uploadResponse.setArchiveLocation(archiveDestinationLocation);
    uploadResponse.setDataTransferType(HpcDataTransferType.S_3);
    uploadResponse.setDataTransferStarted(dataTransferStarted);
    uploadResponse.setUploadSource(s3UploadSource.getSourceLocation());
    uploadResponse.setDataTransferRequestId(
        String.valueOf(s3TransferManagerUploadFuture.hashCode()));
    uploadResponse.setDataTransferStatus(HpcDataTransferUploadStatus.STREAMING_IN_PROGRESS);

    return uploadResponse;
  }

  /**
   * Generate an upload request URL.
   *
   * @param authenticatedToken An authenticated token.
   * @param archiveDestinationLocation The archive destination location.
   * @param objectMetadata The S3 object metadata.
   * @param uploadRequestURLExpiration The URL expiration (in hours).
   * @param uploadRequestURLChecksum An optional user provided checksum value to attach to the
   *     generated url.
   * @return A data object upload response containing the upload request URL.
   * @throws HpcException on data transfer system failure.
   */
  private HpcDataObjectUploadResponse generateUploadRequestURL(
      Object authenticatedToken,
      HpcFileLocation archiveDestinationLocation,
      int uploadRequestURLExpiration,
      String uploadRequestURLChecksum)
      throws HpcException {

    // Calculate the URL expiration date.
    Date expiration = new Date();
    expiration.setTime(expiration.getTime() + 1000 * 60 * 60 * uploadRequestURLExpiration);

    // Create a URL generation request.
    GeneratePresignedUrlRequest generatePresignedUrlRequest =
        new GeneratePresignedUrlRequest(
                archiveDestinationLocation.getFileContainerId(),
                archiveDestinationLocation.getFileId())
            .withMethod(HttpMethod.PUT)
            .withExpiration(expiration);

    // Optionally add a checksum header.
    if (!StringUtils.isEmpty(uploadRequestURLChecksum)) {
      generatePresignedUrlRequest.putCustomRequestHeader("md5chksum", uploadRequestURLChecksum);
    }

    // Generate the pre-signed URL.
    URL url =
        s3Connection
            .getTransferManager(authenticatedToken)
            .getAmazonS3Client()
            .generatePresignedUrl(generatePresignedUrlRequest);

    // Create and populate the response object.
    HpcDataObjectUploadResponse uploadResponse = new HpcDataObjectUploadResponse();
    uploadResponse.setArchiveLocation(archiveDestinationLocation);
    uploadResponse.setDataTransferType(HpcDataTransferType.S_3);
    uploadResponse.setDataTransferStarted(Calendar.getInstance());
    uploadResponse.setDataTransferCompleted(null);
    uploadResponse.setDataTransferRequestId(String.valueOf(generatePresignedUrlRequest.hashCode()));
    uploadResponse.setUploadRequestURL(url.toString());
    uploadResponse.setDataTransferStatus(HpcDataTransferUploadStatus.URL_GENERATED);

    return uploadResponse;
  }

  /**
   * Convert HPC metadata entries into S3 metadata object
   *
   * @param metadataEntries The metadata entries to convert
   * @return A S3 metadata object
   */
  private ObjectMetadata toS3Metadata(List<HpcMetadataEntry> metadataEntries) {
    ObjectMetadata objectMetadata = new ObjectMetadata();
    if (metadataEntries != null) {
      for (HpcMetadataEntry metadataEntry : metadataEntries) {
        objectMetadata.addUserMetadata(metadataEntry.getAttribute(), metadataEntry.getValue());
      }
    }

    return objectMetadata;
  }

  /**
   * Download a data object to a local file.
   *
   * @param authenticatedToken An authenticated token.
   * @param archiveLocation The data object archive location.
   * @param destinationLocation The local file destination.
   * @param progressListener (Optional) a progress listener for async notification on transfer
   *     completion.
   * @return A data transfer request Id.
   * @throws HpcException on data transfer system failure.
   */
  private String downloadDataObject(
      Object authenticatedToken,
      HpcFileLocation archiveLocation,
      File destinationLocation,
      HpcDataTransferProgressListener progressListener)
      throws HpcException {
    // Create a S3 download request.
    GetObjectRequest request =
        new GetObjectRequest(archiveLocation.getFileContainerId(), archiveLocation.getFileId());

    // Download the file via S3.
    Download s3Download = null;
    try {
      s3Download =
          s3Connection
              .getTransferManager(authenticatedToken)
              .download(request, destinationLocation);
      if (progressListener == null) {
        // Download synchronously.
        s3Download.waitForCompletion();
      } else {
        // Download asynchronously.
        s3Download.addProgressListener(
            new HpcS3ProgressListener(
                progressListener,
                "download from "
                    + archiveLocation.getFileContainerId()
                    + ":"
                    + archiveLocation.getFileId()));
      }

    } catch (AmazonClientException ace) {
      throw new HpcException(
          "[S3] Failed to download file: [" + ace.getMessage() + "]",
          HpcErrorType.DATA_TRANSFER_ERROR,
          HpcIntegratedSystem.CLEVERSAFE,
          ace);

    } catch (InterruptedException ie) {
      Thread.currentThread().interrupt();
    }

    return String.valueOf(s3Download.hashCode());
  }

  /**
   * Download a data object to AWS S3 destination.
   *
   * @param authenticatedToken An authenticated token.
   * @param archiveLocation The data object archive location.
   * @param s3Destination The S3 destination.
   * @param progressListener (Optional) a progress listener for async notification on transfer
   *     completion.
   * @return A data transfer request Id.
   * @throws HpcException on data transfer failure.
   */
  private String downloadDataObject(
      Object authenticatedToken,
      HpcFileLocation archiveLocation,
      HpcS3DownloadDestination s3Destination,
      HpcDataTransferProgressListener progressListener)
      throws HpcException {
    // Authenticate the S3 account.
    Object s3AccountAuthenticatedToken = s3Connection.authenticate(s3Destination.getAccount());

    // Confirm the S3 bucket is accessible.
    try {
      getPathAttributes(s3AccountAuthenticatedToken, s3Destination.getDestinationLocation(), false);
    } catch (HpcException e) {
      throw new HpcException(
          "Failed to access AWS S3 bucket: ["
              + e.getMessage()
              + "] "
              + s3Destination.getDestinationLocation(),
          HpcErrorType.INVALID_REQUEST_INPUT,
          e);
    }

    // Generate a download pre-signed URL for the requested data file from the Cleversafe archive.
    String sourceURL =
        generateDownloadRequestURL(authenticatedToken, archiveLocation, S3_STREAM_EXPIRATION);

    // If S3 account is provided, we use AWS transfer manager to download the file, otherwise we stream to destination URL.
    if (s3Destination.getAccount() != null) {
      return downloadDataObject(
          s3AccountAuthenticatedToken,
          sourceURL,
          s3Destination.getDestinationLocation(),
          getPathAttributes(authenticatedToken, archiveLocation, true).getSize(),
          progressListener);

    } else {
      return downloadDataObject(sourceURL, s3Destination.getDestinationURL(), progressListener);
    }
  }

  /**
   * Download a data object to a user's AWS S3 by using AWS transfer Manager.
   *
   * @param s3AccountAuthenticatedToken An authenticated token to the user's AWS S3 account.
   * @param sourceURL The download source URL.
   * @param destinationLocation The destination location.
   * @param fileSize The size of the file to download.
   * @param progressListener A progress listener for async notification on transfer completion.
   * @return A data transfer request Id.
   * @throws HpcException on data transfer failure.
   */
  private String downloadDataObject(
      Object s3AccountAuthenticatedToken,
      String sourceURL,
      HpcFileLocation destinationLocation,
      long fileSize,
      HpcDataTransferProgressListener progressListener)
      throws HpcException {
    if (progressListener == null) {
      throw new HpcException(
          "[S3] No progress listener provided for a download to AWS S3 destination",
          HpcErrorType.UNEXPECTED_ERROR);
    }

    CompletableFuture<Void> s3TransferManagerDownloadFuture =
        CompletableFuture.runAsync(
            () -> {
              try {
                // Create source URL and open a connection to it.
                InputStream sourceInputStream = new URL(sourceURL).openStream();

                // Create a S3 upload request.
                ObjectMetadata metadata = new ObjectMetadata();
                metadata.setContentLength(fileSize);
                PutObjectRequest request =
                    new PutObjectRequest(
                        destinationLocation.getFileContainerId(),
                        destinationLocation.getFileId(),
                        sourceInputStream,
                        metadata);

                // Set the read limit on the request to avoid AWSreset exceptions.
                request.getRequestClientOptions().setReadLimit(getReadLimit(fileSize));

                // Upload asynchronously. AWS transfer manager will perform the upload in its own managed thread.
                Upload s3Upload =
                    s3Connection.getTransferManager(s3AccountAuthenticatedToken).upload(request);

                // Attach a progress listener.
                String sourceDestinationLogMessage =
                    "download to "
                        + destinationLocation.getFileContainerId()
                        + ":"
                        + destinationLocation.getFileId();
                s3Upload.addProgressListener(
                    new HpcS3ProgressListener(progressListener, sourceDestinationLogMessage));

                logger.info(
                    "S3 download Cleversafe->AWS [{}] started. Source size - {} bytes. Read limit - {}",
                    sourceDestinationLogMessage,
                    fileSize,
                    request.getRequestClientOptions().getReadLimit());

                // Wait for the result. This ensures the input stream to the URL remains opened and connected until the download is complete.
                // Note that this wait for AWS transfer manager completion is done in a separate thread (from s3Executor pool), so callers to
                // the API don't wait.
                s3Upload.waitForUploadResult();

              } catch (AmazonClientException | HpcException | IOException e) {
                logger.error(
                    "[S3] Failed to downloadload to AWS S3 destination: " + e.getMessage(), e);
                progressListener.transferFailed(e.getMessage());

              } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
              }
            },
            s3Executor);

    return String.valueOf(s3TransferManagerDownloadFuture.hashCode());
  }

  /**
   * Download a data object by streaming from Cleversafe URL to user AWS S3 URL.
   *
   * @param sourceURL The source URL.
   * @param destinationURL The destination URL.
   * @param progressListener (Optional) a progress listener for async notification on transfer
   *     completion.
   * @return A data transfer request Id.
   * @throws HpcException on data transfer failure.
   */
  private String downloadDataObject(
      String sourceURL, String destinationURL, HpcDataTransferProgressListener progressListener)
      throws HpcException {
    CompletableFuture<HpcDataTransferDownloadReport> s3DataStreamingFuture =
        CompletableFuture.supplyAsync(
            () -> {
              HpcDataTransferDownloadReport downloadReport = new HpcDataTransferDownloadReport();
              downloadReport.setStatus(HpcDataTransferDownloadStatus.COMPLETED);

              try {
                // Create source/destination URLs.
                URL srcURL = new URL(sourceURL);
                URL destURL = new URL(destinationURL);

                // Open source/destination URL connections.
                HttpURLConnection sourceConnection = (HttpURLConnection) srcURL.openConnection();
                HttpURLConnection destinationConnection =
                    (HttpURLConnection) destURL.openConnection();
                destinationConnection.setDoOutput(true);
                destinationConnection.setRequestMethod("PUT");

                // Copy data from source to destination.
                downloadReport.setBytesTransferred(
                    IOUtils.copyLarge(
                        sourceConnection.getInputStream(),
                        destinationConnection.getOutputStream()));

                // Confirm the upload to destination URL is successful.
                int destinationResponseCode = destinationConnection.getResponseCode();
                if (destinationResponseCode != 200) {
                  downloadReport.setMessage(toErrorMessage(destinationConnection.getErrorStream()));
                  downloadReport.setStatus(HpcDataTransferDownloadStatus.FAILED);
                }

                // Close the URL connections.
                sourceConnection.disconnect();
                destinationConnection.disconnect();

              } catch (IOException e) {
                downloadReport.setMessage(e.getMessage());
                downloadReport.setStatus(HpcDataTransferDownloadStatus.FAILED);
              }

              return downloadReport;
            },
            s3Executor);

    if (progressListener == null) {
      // Download synchronously.
      try {
        HpcDataTransferDownloadReport downloadReport = s3DataStreamingFuture.get();
        if (downloadReport.getStatus().equals(HpcDataTransferDownloadStatus.FAILED)) {
          throw new HpcException(
              "[S3] Failed to stream data to destination URL: " + downloadReport.getMessage(),
              HpcErrorType.DATA_TRANSFER_ERROR);
        }
      } catch (InterruptedException | ExecutionException e) {
        throw new HpcException(
            "[S3] Failed to stream data to destination URL: " + e.getMessage(),
            HpcErrorType.DATA_TRANSFER_ERROR);
      }

    } else {
      // Download Asynchronously.
      s3DataStreamingFuture.thenAccept(
          downloadReport -> {
            if (downloadReport.getStatus().equals(HpcDataTransferDownloadStatus.FAILED)) {
              progressListener.transferFailed(downloadReport.getMessage());
            } else {
              progressListener.transferCompleted(downloadReport.getBytesTransferred());
            }
          });
    }

    return String.valueOf(s3DataStreamingFuture.hashCode());
  }

  /**
   * Parse error from AWS S3 and generate a message
   *
   * @param errorStream The stream to parse.
   * @return An error message
   */
  private String toErrorMessage(InputStream errorStream) {
    if (errorStream == null) {
      return "";
    }

    try {
      DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
      DocumentBuilder builder = builderFactory.newDocumentBuilder();
      Document xmlDocument = builder.parse(errorStream);
      XPath xPath = XPathFactory.newInstance().newXPath();
      String code =
          (String) xPath.compile(ERROR_CODE_XPATH).evaluate(xmlDocument, XPathConstants.STRING);
      String message =
          (String) xPath.compile(ERROR_MESSAGE_XPATH).evaluate(xmlDocument, XPathConstants.STRING);
      return "[" + code + "] " + message;

    } catch (ParserConfigurationException
        | SAXException
        | XPathExpressionException
        | IOException e) {
      logger.error("Failed to parse XML from AWS S3");
    }

    return "";
  }

  /**
   * Get buffer read limit for a given file size
   *
   * @param fileSize The file size.
   * @return read limit
   */
  private int getReadLimit(long fileSize) {
    try {
      return Math.toIntExact(fileSize + 1);

    } catch (ArithmeticException e) {
      return Integer.MAX_VALUE;
    }
  }
}

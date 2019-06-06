--
-- hpc_data_management_configuration.sql
--
-- Copyright SVG, Inc.
-- Copyright Leidos Biomedical Research, Inc
-- 
-- Distributed under the OSI-approved BSD 3-Clause License.
-- See http://ncip.github.com/HPC/LICENSE.txt for details.
--
--
-- @author <a href="mailto:eran.rosenberg@nih.gov">Eran Rosenberg</a>
--

ALTER TABLE public."HPC_DATA_MANAGEMENT_CONFIGURATION" ADD COLUMN "ARCHIVE_TYPE" text;
UPDATE public."HPC_DATA_MANAGEMENT_CONFIGURATION" SET "ARCHIVE_TYPE"= 'CLEVERSAFE' WHERE "GLOBUS_ARCHIVE_TYPE" = 'TEMPORARY_ARCHIVE';
UPDATE public."HPC_DATA_MANAGEMENT_CONFIGURATION" SET "ARCHIVE_TYPE"= 'POSIX' WHERE "GLOBUS_ARCHIVE_TYPE" = 'ARCHIVE';
ALTER TABLE public."HPC_DATA_MANAGEMENT_CONFIGURATION" ALTER COLUMN "ARCHIVE_TYPE" SET NOT NULL;

ALTER TABLE public."HPC_DATA_MANAGEMENT_CONFIGURATION" ADD COLUMN "ARCHIVE_S3_URL" text;
UPDATE public."HPC_DATA_MANAGEMENT_CONFIGURATION" SET "ARCHIVE_S3_URL"= "S3_URL";

ALTER TABLE public."HPC_DATA_MANAGEMENT_CONFIGURATION" ADD COLUMN "ARCHIVE_S3_VAULT" text;
UPDATE public."HPC_DATA_MANAGEMENT_CONFIGURATION" SET "ARCHIVE_S3_VAULT"= "S3_VAULT";

ALTER TABLE public."HPC_DATA_MANAGEMENT_CONFIGURATION" ADD COLUMN "ARCHIVE_S3_OBJECT_ID" text;
UPDATE public."HPC_DATA_MANAGEMENT_CONFIGURATION" SET "ARCHIVE_S3_OBJECT_ID"= "S3_OBJECT_ID";

ALTER TABLE public."HPC_DATA_MANAGEMENT_CONFIGURATION" ADD COLUMN "ARCHIVE_S3_UPLOAD_REQUEST_URL_EXPIRATION" text;
UPDATE public."HPC_DATA_MANAGEMENT_CONFIGURATION" SET "ARCHIVE_S3_UPLOAD_REQUEST_URL_EXPIRATION"= "S3_UPLOAD_REQUEST_URL_EXPIRATION";

ALTER TABLE public."HPC_DATA_MANAGEMENT_CONFIGURATION" ADD COLUMN "ARCHIVE_GLOBUS_URL" text;
UPDATE public."HPC_DATA_MANAGEMENT_CONFIGURATION" SET "ARCHIVE_GLOBUS_URL"= "GLOBUS_URL";
ALTER TABLE public."HPC_DATA_MANAGEMENT_CONFIGURATION" ALTER COLUMN "ARCHIVE_GLOBUS_URL" SET NOT NULL;

ALTER TABLE public."HPC_DATA_MANAGEMENT_CONFIGURATION" ADD COLUMN "ARCHIVE_GLOBUS_ENDPOINT" text;
UPDATE public."HPC_DATA_MANAGEMENT_CONFIGURATION" SET "ARCHIVE_GLOBUS_ENDPOINT"= 'bf72b354-4982-11e9-bf28-0edbf3a4e7ee' WHERE "GLOBUS_ARCHIVE_TYPE" = 'TEMPORARY_ARCHIVE';
UPDATE public."HPC_DATA_MANAGEMENT_CONFIGURATION" SET "ARCHIVE_GLOBUS_ENDPOINT"= "GLOBUS_ARCHIVE_ENDPOINT" WHERE "GLOBUS_ARCHIVE_TYPE" = 'ARCHIVE';
ALTER TABLE public."HPC_DATA_MANAGEMENT_CONFIGURATION" ALTER COLUMN "ARCHIVE_GLOBUS_ENDPOINT" SET NOT NULL;

ALTER TABLE public."HPC_DATA_MANAGEMENT_CONFIGURATION" ADD COLUMN "ARCHIVE_GLOBUS_PATH" text;
UPDATE public."HPC_DATA_MANAGEMENT_CONFIGURATION" SET "ARCHIVE_GLOBUS_PATH"= CONCAT('/', "ARCHIVE_S3_VAULT", '/', "ARCHIVE_S3_OBJECT_ID") WHERE "GLOBUS_ARCHIVE_TYPE" = 'TEMPORARY_ARCHIVE';
UPDATE public."HPC_DATA_MANAGEMENT_CONFIGURATION" SET "ARCHIVE_GLOBUS_PATH"= "GLOBUS_ARCHIVE_PATH" WHERE "GLOBUS_ARCHIVE_TYPE" = 'ARCHIVE';
ALTER TABLE public."HPC_DATA_MANAGEMENT_CONFIGURATION" ALTER COLUMN "ARCHIVE_GLOBUS_PATH" SET NOT NULL;

ALTER TABLE public."HPC_DATA_MANAGEMENT_CONFIGURATION" ADD COLUMN "ARCHIVE_GLOBUS_DIRECTORY" text;
UPDATE public."HPC_DATA_MANAGEMENT_CONFIGURATION" SET "ARCHIVE_GLOBUS_DIRECTORY"= "GLOBUS_ARCHIVE_DIRECTORY" WHERE "GLOBUS_ARCHIVE_TYPE" = 'ARCHIVE';

COMMENT ON COLUMN public."HPC_DATA_MANAGEMENT_CONFIGURATION"."ARCHIVE_TYPE" IS 
                  'The archive type (Cleversafe or POSIX)';
                  
COMMENT ON COLUMN public."HPC_DATA_MANAGEMENT_CONFIGURATION"."ARCHIVE_S3_URL" IS 
                  'The archive (Cleversafe) S3 URL';  
                  
COMMENT ON COLUMN public."HPC_DATA_MANAGEMENT_CONFIGURATION"."ARCHIVE_S3_VAULT" IS 
                  'The archive (Cleversafe) S3 vault (bucket)';
                  
COMMENT ON COLUMN public."HPC_DATA_MANAGEMENT_CONFIGURATION"."ARCHIVE_S3_OBJECT_ID" IS 
                  'The archive (Cleversafe) S3 object id prefix';
                  
COMMENT ON COLUMN public."HPC_DATA_MANAGEMENT_CONFIGURATION"."ARCHIVE_S3_UPLOAD_REQUEST_URL_EXPIRATION" IS 
                  'The expiration period (in hours) to set when S3 upload request URL is generated';     
                  
COMMENT ON COLUMN public."HPC_DATA_MANAGEMENT_CONFIGURATION"."ARCHIVE_GLOBUS_URL" IS 
                  'The Globus authentication URL';    
                  
COMMENT ON COLUMN public."HPC_DATA_MANAGEMENT_CONFIGURATION"."ARCHIVE_GLOBUS_ENDPOINT" IS 
                  'The archive Globus endpoint ID';   
                   
COMMENT ON COLUMN public."HPC_DATA_MANAGEMENT_CONFIGURATION"."ARCHIVE_GLOBUS_PATH" IS 
                  'The archive Globus endpoint path';    
                  
COMMENT ON COLUMN public."HPC_DATA_MANAGEMENT_CONFIGURATION"."ARCHIVE_GLOBUS_DIRECTORY" IS 
                  'The archive Globus directory (direct file system access to the Globus endpoint path )';        
                     
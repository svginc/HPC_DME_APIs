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
                  
DROP TABLE IF EXISTS public."HPC_S3_ARCHIVE_CONFIGURATION";
CREATE TABLE public."HPC_S3_ARCHIVE_CONFIGURATION"
(
  "ID" text PRIMARY KEY,
  "PROVIDER" text NOT NULL,
  "DATA_MANAGEMENT_CONFIGURATION_ID" text NOT NULL,
  "URL" text NOT NULL,
  "BUCKET" text NOT NULL,
  "OBJECT_ID" text NOT NULL,
  "UPLOAD_REQUEST_URL_EXPIRATION" integer NOT NULL
)
WITH (
  OIDS=FALSE
);  

COMMENT ON TABLE public."HPC_S3_ARCHIVE_CONFIGURATION" IS 
                 'The S3 archive configurations (per DOC) supported by HPC-DME';
COMMENT ON COLUMN public."HPC_S3_ARCHIVE_CONFIGURATION"."ID" IS 
                  'The S3 Configuration ID';
COMMENT ON COLUMN public."HPC_S3_ARCHIVE_CONFIGURATION"."PROVIDER" IS 
                  'The S3 Provider - Cleversafe, Cloudian, AWS, etc';
COMMENT ON COLUMN public."HPC_S3_ARCHIVE_CONFIGURATION"."DATA_MANAGEMENT_CONFIGURATION_ID" IS 
                  'The DM config that own this S3 archive configuration';
COMMENT ON COLUMN public."HPC_S3_ARCHIVE_CONFIGURATION"."URL" IS 
                  'The S3 archive URL';
COMMENT ON COLUMN public."HPC_S3_ARCHIVE_CONFIGURATION"."BUCKET" IS 
                  'The S3 archive bucket';
COMMENT ON COLUMN public."HPC_S3_ARCHIVE_CONFIGURATION"."OBJECT_ID" IS 
                  'The S3 archive object id prefix';
COMMENT ON COLUMN public."HPC_S3_ARCHIVE_CONFIGURATION"."UPLOAD_REQUEST_URL_EXPIRATION" IS 
                  'The expiration period (in hours) to set when S3 upload request URL is generated';  
                  
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";       
INSERT INTO public."HPC_S3_ARCHIVE_CONFIGURATION" ("ID", "PROVIDER", "DATA_MANAGEMENT_CONFIGURATION_ID", "URL", "BUCKET", "OBJECT_ID", "UPLOAD_REQUEST_URL_EXPIRATION")
	SELECT uuid_generate_v4(), 'CLEVERSAFE', "ID", "S3_URL", "S3_VAULT", "S3_OBJECT_ID", "S3_UPLOAD_REQUEST_URL_EXPIRATION" FROM "HPC_DATA_MANAGEMENT_CONFIGURATION" 
	WHERE "S3_ARCHIVE_TYPE" = 'ARCHIVE';
	
ALTER TABLE public."HPC_DATA_MANAGEMENT_CONFIGURATION" ADD COLUMN "S3_UPLOAD_ARCHIVE_CONFIGURATION_ID" text;                  
COMMENT ON COLUMN public."HPC_DATA_MANAGEMENT_CONFIGURATION"."S3_UPLOAD_ARCHIVE_CONFIGURATION_ID" IS 
                  'The S3 archive to be used for uploading new files for this DOC';
	
ALTER TABLE public."HPC_DATA_MANAGEMENT_CONFIGURATION" ADD COLUMN "S3_DEFAULT_DOWNLOAD_ARCHIVE_CONFIGURATION_ID" text;  
COMMENT ON COLUMN public."HPC_DATA_MANAGEMENT_CONFIGURATION"."S3_DEFAULT_DOWNLOAD_ARCHIVE_CONFIGURATION_ID" IS 
                  'The default (was first) S3 archive to use for downloading files for this DOC';   
                  
UPDATE public."HPC_DATA_MANAGEMENT_CONFIGURATION" 
	SET "S3_UPLOAD_ARCHIVE_CONFIGURATION_ID" = public."HPC_S3_ARCHIVE_CONFIGURATION"."ID",
		"S3_DEFAULT_DOWNLOAD_ARCHIVE_CONFIGURATION_ID" = public."HPC_S3_ARCHIVE_CONFIGURATION"."ID"
	FROM public."HPC_S3_ARCHIVE_CONFIGURATION"
		WHERE public."HPC_DATA_MANAGEMENT_CONFIGURATION"."ID" = public."HPC_S3_ARCHIVE_CONFIGURATION"."DATA_MANAGEMENT_CONFIGURATION_ID" AND
			public."HPC_DATA_MANAGEMENT_CONFIGURATION"."S3_ARCHIVE_TYPE" = 'ARCHIVE';

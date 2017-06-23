--
-- hpc_doc_configuration_dev.sql
--
-- Copyright SVG, Inc.
-- Copyright Leidos Biomedical Research, Inc
-- 
-- Distributed under the OSI-approved BSD 3-Clause License.
-- See http://ncip.github.com/HPC/LICENSE.txt for details.
--
--
-- @author <a href="mailto:eran.rosenberg@nih.gov">Eran Rosenberg</a>
-- @version $Id$
--

INSERT INTO public."HPC_DOC_CONFIGURATION" 
VALUES('FNLCR', '/FNL_SF_Archive', 'fr-s-clvrsf-01', 'HPC_DME_Production_Vault', 'HPC_DME_Prod_Archive', 'ARCHIVE',
       '{
      	 "collectionType": "PI_Lab",
      	 "isDataObjectContainer": false,
      	 "subCollections": [
        	{
          	 "collectionType": "Project",
          	 "isDataObjectContainer": false,
          	 "subCollections": [
            	{
              	 "collectionType": "Flowcell",
              	 "isDataObjectContainer": false,
              	 "subCollections": [
                	{
                     "collectionType": "Sample",
                     "isDataObjectContainer": true
                	}
              	 ]
               }
          	 ]
        	}
      	 ]
    	}',
       '{
		 "metadataValidationRules": [  
         {  
            "attribute":"collection_type",
            "mandatory":true,
            "validValues":[  
               "Project",
               "PI_Lab",
               "Flowcell",
               "Sample", 
               "Folder"
            ],
            "ruleEnabled":true,
            "docs":[  
               "FNLCR"
            ]
         },
         {  
            "attribute":"collection_type",
            "mandatory":true,
            "validValues":[  
               "Project",
               "Dataset",
               "Run",
               "Folder"
            ],
            "ruleEnabled":true,
            "docs":[  
               "DUMMY"
            ]
         },
         {  
            "attribute":"name",
            "mandatory":true,
            "collectionTypes":[  
               "Project"
            ],
            "ruleEnabled":true,
            "docs":[  
               "DUMMY"
            ]
         },
         {  
            "attribute":"description",
            "mandatory":true,
            "collectionTypes":[  
               "Project"
            ],
            "ruleEnabled":true,
            "docs":[  
               "DUMMY"
            ]
         },
         {  
            "attribute":"project_type",
            "mandatory":true,
            "defaultValue":"Unspecified",
            "collectionTypes":[  
               "Project"
            ],
            "validValues":[  
               "Unspecified",
               "Umbrella Project",
               "Sequencing",
               "Analysis"
            ],
            "ruleEnabled":true,
            "docs":[  
               "DUMMY"
            ]
         },
         {  
            "attribute":"internal_project_id",
            "mandatory":true,
            "collectionTypes":[  
               "Project"
            ],
            "ruleEnabled":true,
            "docs":[  
               "DUMMY"
            ]
         },
         {  
            "attribute":"source_lab_pi",
            "mandatory":true,
            "collectionTypes":[  
               "Project"
            ],
            "ruleEnabled":true,
            "docs":[  
               "DUMMY"
            ]
         },
         {  
            "attribute":"lab_branch",
            "mandatory":true,
            "collectionTypes":[  
               "Project"
            ],
            "ruleEnabled":true,
            "docs":[  
               "DUMMY"
            ]
         },
         {  
            "attribute":"pi_doc",
            "mandatory":true,
            "collectionTypes":[  
               "Project"
            ],
            "validValues":[  
               "FNLCR",
               "DUMMY",
               "CCBR"
            ],
            "ruleEnabled":true,
            "docs":[  
               "DUMMY"
            ]
         },
         {  
            "attribute":"original_date_created",
            "mandatory":true,
            "collectionTypes":[  
               "Project"
            ],
            "defaultValue":"System-Date",
            "ruleEnabled":true,
            "docs":[  
               "DUMMY"
            ]
         },
         {  
            "attribute":"name",
            "mandatory":true,
            "collectionTypes":[  
               "Dataset"
            ],
            "ruleEnabled":true,
            "docs":[  
               "DUMMY"
            ]
         },
         {  
            "attribute":"description",
            "mandatory":true,
            "collectionTypes":[  
               "Dataset"
            ],
            "ruleEnabled":true,
            "docs":[  
               "DUMMY"
            ]
         },
         {  
            "attribute":"source_lab_pi",
            "mandatory":true,
            "collectionTypes":[  
               "Project"
            ],
            "ruleEnabled":true,
            "docs":[  
               "DUMMY"
            ]
         },
         {  
            "attribute":"lab_branch",
            "mandatory":true,
            "collectionTypes":[  
               "Project"
            ],
            "ruleEnabled":true,
            "docs":[  
               "DUMMY"
            ]
         },
         {  
            "attribute":"pi_doc",
            "mandatory":true,
            "collectionTypes":[  
               "Project"
            ],
            "validValues":[  
               "FNLCR",
               "DUMMY",
               "CCBR"
            ],
            "ruleEnabled":true,
            "docs":[  
               "DUMMY"
            ]
         },
         {  
            "attribute":"original_date_created",
            "mandatory":true,
            "collectionTypes":[  
               "Project"
            ],
            "defaultValue":"System-Date",
            "ruleEnabled":true,
            "docs":[  
               "DUMMY"
            ]
         },
         {  
            "attribute":"phi_content",
            "mandatory":true,
            "collectionTypes":[  
               "Dataset"
            ],
            "defaultValue":"Unspecified",
            "validValues":[  
               "Unspecified",
               "PHI Present",
               "PHI Not Present",
               "Not Specified"
            ],
            "ruleEnabled":true,
            "docs":[  
               "DUMMY"
            ]
         },
         {  
            "attribute":"pii_content",
            "mandatory":true,
            "collectionTypes":[  
               "Dataset"
            ],
            "defaultValue":"Unspecified",
            "validValues":[  
               "Unspecified",
               "PII Present",
               "PII Not Present",
               "Not Specified"
            ],
            "ruleEnabled":true,
            "docs":[  
               "DUMMY"
            ]
         },
         {  
            "attribute":"data_encryption_status",
            "mandatory":true,
            "collectionTypes":[  
               "Dataset"
            ],
            "defaultValue":"Unspecified",
            "validValues":[  
               "Unspecified",
               "Encrypted",
               "Not Encrypted",
               "Not Specified"
            ],
            "ruleEnabled":true,
            "docs":[  
               "DUMMY"
            ]
         },
         {  
            "attribute":"data_compression_status",
            "mandatory":true,
            "collectionTypes":[  
               "Dataset"
            ],
            "defaultValue":"Unspecified",
            "validValues":[  
               "Unspecified",
               "Compressed",
               "Not Compressed",
               "Not Specified"
            ],
            "ruleEnabled":true,
            "docs":[  
               "DUMMY"
            ]
         },
         {  
            "attribute":"funding_organization",
            "mandatory":false,
            "collectionTypes":[  
               "Dataset"
            ],
            "defaultValue":"Unspecified",
            "ruleEnabled":true,
            "docs":[  
               "DUMMY"
            ]
         },
         {  
            "attribute":"flow_cell_id",
            "mandatory":true,
            "collectionTypes":[  
               "Dataset"
            ],
            "ruleEnabled":true,
            "docs":[  
               "DUMMY"
            ]
         },
         {  
            "attribute":"run_id",
            "mandatory":true,
            "collectionTypes":[  
               "Dataset"
            ],
            "ruleEnabled":true,
            "docs":[  
               "DUMMY"
            ]
         },
         {  
            "attribute":"run_date",
            "mandatory":true,
            "collectionTypes":[  
               "Dataset"
            ],
            "ruleEnabled":true,
            "docs":[  
               "DUMMY"
            ]
         },
         {  
            "attribute":"sequencing_platform",
            "mandatory":true,
            "collectionTypes":[  
               "Dataset"
            ],
            "defaultValue":"Unspecified",
            "validValues":[  
               "Unspecified",
               "Illumina-MiSeq",
               "Illumina-NextSeq",
               "Illumina-HiSeq",
               "Illumina-HiSeq X",
               "Ion-PGM",
               "Ion-Proton",
               "PacBio-PacBio RS"
            ],
            "ruleEnabled":true,
            "docs":[  
               "DUMMY"
            ]
         },
         {  
            "attribute":"sequencing_application_type",
            "mandatory":true,
            "collectionTypes":[  
               "Dataset"
            ],
            "defaultValue":"Unspecified",
            "validValues":[  
               "Unspecified",
               "DNA-Seq (Re-Sequencing)",
               "DNA-Seq (De novo assembly)",
               "SNP Analysis / Rearrangement Detection",
               "Exome",
               "ChiP-Seq"
            ],
            "ruleEnabled":true,
            "docs":[  
               "DUMMY"
            ]
         },
         {  
            "attribute":"library_id",
            "mandatory":true,
            "collectionTypes":[  
               "Dataset"
            ],
            "ruleEnabled":true,
            "docs":[  
               "DUMMY"
            ]
         },
         {  
            "attribute":"library_name",
            "mandatory":true,
            "collectionTypes":[  
               "Dataset"
            ],
            "defaultValue":"Unspecified",
            "validValues":[  
               "Unspecified",
               "TruSeq ChIP-Seq",
               "Illumina TrueSeq"
            ],
            "ruleEnabled":true,
            "docs":[  
               "DUMMY"
            ]
         },
         {  
            "attribute":"library_type",
            "mandatory":true,
            "collectionTypes":[  
               "Dataset"
            ],
            "defaultValue":"Unspecified",
            "validValues":[  
               "Unspecified",
               "TruSeq ChIP-Seq",
               "Illumina TrueSeq"
            ],
            "ruleEnabled":true,
            "docs":[  
               "DUMMY"
            ]
         },
         {  
            "attribute":"library_protocol",
            "mandatory":true,
            "collectionTypes":[  
               "Dataset"
            ],
            "defaultValue":"Unspecified",
            "validValues":[  
               "Unspecified",
               "TruSeq ChIP-Seq",
               "Illumina TrueSeq"
            ],
            "ruleEnabled":true,
            "docs":[  
               "DUMMY"
            ]
         },
         {  
            "attribute":"pi_name",
            "mandatory":true,
            "collectionTypes":[  
               "PI_Lab"
            ],
            "ruleEnabled":true,
            "docs":[  
               "FNLCR"
            ]
         },
         {  
            "attribute":"project_id_CSAS",
            "mandatory":true,
            "collectionTypes":[  
               "Project"
            ],
            "ruleEnabled":true,
            "docs":[  
               "FNLCR"
            ]
         },
         {  
            "attribute":"project_name",
            "mandatory":false,
            "collectionTypes":[  
               "Project"
            ],
            "ruleEnabled":true,
            "docs":[  
               "FNLCR"
            ]
         },
         {  
            "attribute":"lab_contact",
            "mandatory":false,
            "collectionTypes":[  
               "Project"
            ],
            "ruleEnabled":true,
            "docs":[  
               "FNLCR"
            ]
         },
         {  
            "attribute":"bioinformatics_contact",
            "mandatory":false,
            "collectionTypes":[  
               "Project"
            ],
            "ruleEnabled":true,
            "docs":[  
               "FNLCR"
            ]
         },
         {  
            "attribute":"project_start_date",
            "mandatory":false,
            "defaultValue":"System-Date",
            "collectionTypes":[  
               "Project"
            ],
            "ruleEnabled":true,
            "docs":[  
               "FNLCR"
            ]
         },
         {  
            "attribute":"grant_funding_agent",
            "mandatory":false,
            "collectionTypes":[  
               "Project"
            ],
            "ruleEnabled":true,
            "docs":[  
               "FNLCR"
            ]
         },
         {  
            "attribute":"flowcell_id",
            "mandatory":true,
            "collectionTypes":[  
               "Flowcell"
            ],
            "ruleEnabled":true,
            "docs":[  
               "FNLCR"
            ]
         },
         {  
            "attribute":"run_name",
            "mandatory":true,
            "collectionTypes":[  
               "Flowcell"
            ],
            "ruleEnabled":true,
            "docs":[  
               "FNLCR"
            ]
         },
         {  
            "attribute":"run_date",
            "mandatory":true,
            "collectionTypes":[  
               "Flowcell"
            ],
            "ruleEnabled":true,
            "docs":[  
               "FNLCR"
            ]
         },
         {  
            "attribute":"sequencing_platform",
            "mandatory":true,
            "collectionTypes":[  
               "Flowcell"
            ],
            "ruleEnabled":true,
            "docs":[  
               "FNLCR"
            ]
         },
         {  
            "attribute":"sequencing_application_type",
            "mandatory":true,
            "collectionTypes":[  
               "Flowcell"
            ],
            "ruleEnabled":true,
            "docs":[  
               "FNLCR"
            ]
         },
         {  
            "attribute":"read_length",
            "mandatory":true,
            "collectionTypes":[  
               "Flowcell"
            ],
            "ruleEnabled":true,
            "docs":[  
               "FNLCR"
            ]
         },
         {  
            "attribute":"pooling",
            "mandatory":true,
            "collectionTypes":[  
               "Flowcell"
            ],
            "ruleEnabled":true,
            "docs":[  
               "FNLCR"
            ]
         },
         {  
            "attribute":"sample_id",
            "mandatory":true,
            "collectionTypes":[  
               "Sample"
            ],
            "ruleEnabled":true,
            "docs":[  
               "FNLCR"
            ]
         },
         {  
            "attribute":"sample_name",
            "mandatory":true,
            "collectionTypes":[  
               "Sample"
            ],
            "ruleEnabled":true,
            "docs":[  
               "FNLCR"
            ]
         },
         {  
            "attribute":"initial_sample_concentration_ngul",
            "mandatory":false,
            "collectionTypes":[  
               "Sample"
            ],
            "ruleEnabled":true,
            "docs":[  
               "FNLCR"
            ]
         },
         {  
            "attribute":"initial_sample_volume_ul",
            "mandatory":false,
            "collectionTypes":[  
               "Sample"
            ],
            "ruleEnabled":true,
            "docs":[  
               "FNLCR"
            ]
         },
         {  
            "attribute":"sfqc_sample_concentration_ngul",
            "mandatory":false,
            "collectionTypes":[  
               "Sample"
            ],
            "ruleEnabled":true,
            "docs":[  
               "FNLCR"
            ]
         },
         {  
            "attribute":"sfqc_sample_size",
            "mandatory":false,
            "collectionTypes":[  
               "Sample"
            ],
            "ruleEnabled":true,
            "docs":[  
               "FNLCR"
            ]
         },
         {  
            "attribute":"RIN",
            "mandatory":false,
            "collectionTypes":[  
               "Sample"
            ],
            "ruleEnabled":true,
            "docs":[  
               "FNLCR"
            ]
         },
         {  
            "attribute":"28s18s",
            "mandatory":false,
            "collectionTypes":[  
               "Sample"
            ],
            "ruleEnabled":true,
            "docs":[  
               "FNLCR"
            ]
         },
         {  
            "attribute":"library_id",
            "mandatory":false,
            "collectionTypes":[  
               "Sample"
            ],
            "ruleEnabled":true,
            "docs":[  
               "FNLCR"
            ]
         },
         {  
            "attribute":"library_lot",
            "mandatory":false,
            "collectionTypes":[  
               "Sample"
            ],
            "ruleEnabled":true,
            "docs":[  
               "FNLCR"
            ]
         },
         {  
            "attribute":"library_name",
            "mandatory":false,
            "collectionTypes":[  
               "Sample"
            ],
            "ruleEnabled":true,
            "docs":[  
               "FNLCR"
            ]
         },
         {  
            "attribute":"sfqc_library_concentration_nM",
            "mandatory":false,
            "collectionTypes":[  
               "Sample"
            ],
            "ruleEnabled":true,
            "docs":[  
               "FNLCR"
            ]
         },
         {  
            "attribute":"sfqc_library_size",
            "mandatory":false,
            "collectionTypes":[  
               "Sample"
            ],
            "ruleEnabled":true,
            "docs":[  
               "FNLCR"
            ]
         },
         {  
            "attribute":"source_id",
            "mandatory":false,
            "collectionTypes":[  
               "Sample"
            ],
            "ruleEnabled":true,
            "docs":[  
               "FNLCR"
            ]
         },
         {  
            "attribute":"source_name",
            "mandatory":false,
            "collectionTypes":[  
               "Sample"
            ],
            "ruleEnabled":true,
            "docs":[  
               "FNLCR"
            ]
         },
         {  
            "attribute":"source_organism",
            "mandatory":true,
            "collectionTypes":[  
               "Sample"
            ],
            "ruleEnabled":true,
            "docs":[  
               "FNLCR"
            ]
         },
         {  
            "attribute":"source_provider",
            "mandatory":false,
            "collectionTypes":[  
               "Sample"
            ],
            "ruleEnabled":true,
            "docs":[  
               "FNLCR"
            ]
         }
      ]
		}',
	   '{
		 "metadataValidationRules": [  
         {  
            "attribute":"name",
            "mandatory":true,
            "ruleEnabled":true,
            "docs":[  
               "DUMMY"
            ]
         },
         {  
            "attribute":"description",
            "mandatory":true,
            "ruleEnabled":true,
            "docs":[  
               "DUMMY"
            ]
         },
         {  
            "attribute":"source_lab_pi",
            "mandatory":true,
            "ruleEnabled":true,
            "docs":[  
               "DUMMY"
            ]
         },
         {  
            "attribute":"lab_branch",
            "mandatory":true,
            "ruleEnabled":true,
            "docs":[  
               "DUMMY"
            ]
         },
         {  
            "attribute":"pi_doc",
            "mandatory":true,
            "validValues":[  
               "FNLCR",
               "DUMMY",
               "CCBR"
            ],
            "ruleEnabled":true,
            "docs":[  
               "DUMMY"
            ]
         },
         {  
            "attribute":"original_date_created",
            "mandatory":true,
            "defaultValue":"System-Date",
            "ruleEnabled":true,
            "docs":[  
               "DUMMY"
            ]
         },
         {  
            "attribute":"phi_content",
            "mandatory":true,
            "collectionTypes":[  
               "Dataset"
            ],
            "defaultValue":"Unspecified",
            "validValues":[  
               "Unspecified",
               "PHI Present",
               "PHI Not Present",
               "Not Specified"
            ],
            "ruleEnabled":true,
            "docs":[  
               "DUMMY"
            ]
         },
         {  
            "attribute":"pii_content",
            "mandatory":true,
            "collectionTypes":[  
               "Dataset"
            ],
            "defaultValue":"Unspecified",
            "validValues":[  
               "Unspecified",
               "PII Present",
               "PII Not Present",
               "Not Specified"
            ],
            "ruleEnabled":true,
            "docs":[  
               "DUMMY"
            ]
         },
         {  
            "attribute":"data_encryption_status",
            "mandatory":true,
            "collectionTypes":[  
               "Dataset"
            ],
            "defaultValue":"Unspecified",
            "validValues":[  
               "Unspecified",
               "Encrypted",
               "Not Encrypted",
               "Not Specified"
            ],
            "ruleEnabled":true,
            "docs":[  
               "DUMMY"
            ]
         },
         {  
            "attribute":"data_compression_status",
            "mandatory":true,
            "collectionTypes":[  
               "Dataset"
            ],
            "defaultValue":"Unspecified",
            "validValues":[  
               "Unspecified",
               "Compressed",
               "Not Compressed",
               "Not Specified"
            ],
            "ruleEnabled":true,
            "docs":[  
               "DUMMY"
            ]
         },
         {  
            "attribute":"funding_organization",
            "mandatory":false,
            "collectionTypes":[  
               "Dataset"
            ],
            "defaultValue":"Unspecified",
            "ruleEnabled":true,
            "docs":[  
               "DUMMY"
            ]
         },
         {  
            "attribute":"flow_cell_id",
            "mandatory":true,
            "collectionTypes":[  
               "Dataset"
            ],
            "ruleEnabled":true,
            "docs":[  
               "DUMMY"
            ]
         },
         {  
            "attribute":"run_id",
            "mandatory":true,
            "collectionTypes":[  
               "Dataset"
            ],
            "ruleEnabled":true,
            "docs":[  
               "DUMMY"
            ]
         },
         {  
            "attribute":"run_date",
            "mandatory":true,
            "collectionTypes":[  
               "Dataset"
            ],
            "ruleEnabled":true,
            "docs":[  
               "DUMMY"
            ]
         },
         {  
            "attribute":"sequencing_platform",
            "mandatory":true,
            "collectionTypes":[  
               "Dataset"
            ],
            "defaultValue":"Unspecified",
            "validValues":[  
               "Unspecified",
               "Illumina-MiSeq",
               "Illumina-NextSeq",
               "Illumina-HiSeq",
               "Illumina-HiSeq X",
               "Ion-PGM",
               "Ion-Proton",
               "PacBio-PacBio RS"
            ],
            "ruleEnabled":true,
            "docs":[  
               "DUMMY"
            ]
         },
         {  
            "attribute":"sequencing_application_type",
            "mandatory":true,
            "collectionTypes":[  
               "Dataset"
            ],
            "defaultValue":"Unspecified",
            "validValues":[  
               "Unspecified",
               "DNA-Seq (Re-Sequencing)",
               "DNA-Seq (De novo assembly)",
               "SNP Analysis / Rearrangement Detection",
               "Exome",
               "ChiP-Seq"
            ],
            "ruleEnabled":true,
            "docs":[  
               "DUMMY"
            ]
         },
         {  
            "attribute":"library_id",
            "mandatory":true,
            "collectionTypes":[  
               "Dataset"
            ],
            "ruleEnabled":true,
            "docs":[  
               "DUMMY"
            ]
         },
         {  
            "attribute":"library_name",
            "mandatory":true,
            "collectionTypes":[  
               "Dataset"
            ],
            "defaultValue":"Unspecified",
            "validValues":[  
               "Unspecified",
               "TruSeq ChIP-Seq",
               "Illumina TrueSeq"
            ],
            "ruleEnabled":true,
            "docs":[  
               "DUMMY"
            ]
         },
         {  
            "attribute":"library_type",
            "mandatory":true,
            "collectionTypes":[  
               "Dataset"
            ],
            "defaultValue":"Unspecified",
            "validValues":[  
               "Unspecified",
               "TruSeq ChIP-Seq",
               "Illumina TrueSeq"
            ],
            "ruleEnabled":true,
            "docs":[  
               "DUMMY"
            ]
         },
         {  
            "attribute":"library_protocol",
            "mandatory":true,
            "collectionTypes":[  
               "Dataset"
            ],
            "defaultValue":"Unspecified",
            "validValues":[  
               "Unspecified",
               "TruSeq ChIP-Seq",
               "Illumina TrueSeq"
            ],
            "ruleEnabled":true,
            "docs":[  
               "DUMMY"
            ]
         },
         {  
            "attribute":"object_name",
            "mandatory":true,
            "ruleEnabled":true,
            "docs":[  
               "FNLCR"
            ]
         },
         {  
            "attribute":"file_type",
            "mandatory":false,
            "ruleEnabled":true,
            "docs":[  
               "FNLCR"
            ]
         },
         {  
            "attribute":"reference_genome",
            "mandatory":false,
            "ruleEnabled":true,
            "docs":[  
               "FNLCR"
            ]
         },
         {  
            "attribute":"reference_annotation",
            "mandatory":false,
            "ruleEnabled":true,
            "docs":[  
               "FNLCR"
            ]
         },
         {  
            "attribute":"software_tool",
            "mandatory":false,
            "ruleEnabled":true,
            "docs":[  
               "FNLCR"
            ]
         },
         {  
            "attribute":"md5_checksum",
            "mandatory":false,
            "ruleEnabled":true,
            "docs":[  
               "FNLCR"
            ]
         },
         {  
            "attribute":"phi_content",
            "mandatory":true,
            "defaultValue":"Unspecified",
            "validValues":[  
               "Unspecified",
               "PHI Present",
               "PHI Not Present",
               "Not Specified"
            ],
            "ruleEnabled":true,
            "docs":[  
               "FNLCR"
            ]
         },
         {  
            "attribute":"pii_content",
            "mandatory":true,
            "defaultValue":"Unspecified",
            "validValues":[  
               "Unspecified",
               "PII Present",
               "PII Not Present",
               "Not Specified"
            ],
            "ruleEnabled":true,
            "docs":[  
               "FNLCR"
            ]
         },
         {  
            "attribute":"data_encryption_status",
            "mandatory":true,
            "defaultValue":"Unspecified",
            "validValues":[  
               "Unspecified",
               "Encrypted",
               "Not Encrypted",
               "Not Specified"
            ],
            "ruleEnabled":true,
            "docs":[  
               "FNLCR"
            ]
         },
         {  
            "attribute":"data_compression_status",
            "mandatory":true,
            "defaultValue":"Unspecified",
            "validValues":[  
               "Unspecified",
               "Compressed",
               "Not Compressed",
               "Not Specified"
            ],
            "ruleEnabled":true,
            "docs":[  
               "FNLCR"
            ]
         }
      ]
	    }');

INSERT INTO public."HPC_DOC_CONFIGURATION" 
VALUES('CCBR', '/CCBR_Archive', 'fr-s-clvrsf-01', 'HPC_DME_Production_Vault', 
       'HPC_DME_Prod_Archive', 'ARCHIVE', NULL, NULL, NULL);

INSERT INTO public."HPC_DOC_CONFIGURATION" 
VALUES('DUMMY', '/TEST_Archive', 'fr-s-clvrsf-01', 'HPC_DME_Production_Vault', 'HPC_DME_Prod_Archive', 'ARCHIVE',
       '{
      	 "collectionType": "Project",
      	 "isDataObjectContainer": true,
      	 "subCollections": [
        	{
          	 "collectionType": "Dataset",
          	 "isDataObjectContainer": true
        	},
        	{
          	 "collectionType": "Run",
          	 "isDataObjectContainer": false
        	},
        	{
          	 "collectionType": "Folder",
          	 "isDataObjectContainer": true
        	}
      	 ]
        }',
       '{
		 "metadataValidationRules": [{
				"attribute": "collection_type",
				"mandatory": true,
				"validValues": [
					"Project",
					"Dataset",
					"Run",
					"Folder"
				],
				"ruleEnabled": true
			},
			{
				"attribute": "name",
				"mandatory": true,
				"collectionTypes": [
					"Project"
				],
				"ruleEnabled": true
			},
			{
				"attribute": "description",
				"mandatory": true,
				"collectionTypes": [
					"Project"
				],
				"ruleEnabled": true
			},
			{
				"attribute": "project_type",
				"mandatory": true,
				"defaultValue": "Unspecified",
				"collectionTypes": [
					"Project"
				],
				"validValues": [
					"Unspecified",
					"Umbrella Project",
					"Sequencing",
					"Analysis"
				],
				"ruleEnabled": true
			},
			{
				"attribute": "internal_project_id",
				"mandatory": true,
				"collectionTypes": [
					"Project"
				],
				"ruleEnabled": true
			},
			{
				"attribute": "source_lab_pi",
				"mandatory": true,
				"collectionTypes": [
					"Project"
				],
				"ruleEnabled": true
			},
			{
				"attribute": "lab_branch",
				"mandatory": true,
				"collectionTypes": [
					"Project"
				],
				"ruleEnabled": true
			},
			{
				"attribute": "pi_doc",
				"mandatory": true,
				"collectionTypes": [
					"Project"
				],
				"validValues": [
					"FNLCR",
					"DUMMY",
					"CCBR"
				],
				"ruleEnabled": true
			},
			{
				"attribute": "original_date_created",
				"mandatory": true,
				"collectionTypes": [
					"Project"
				],
				"defaultValue": "System-Date",
				"ruleEnabled": true
			},
			{
				"attribute": "name",
				"mandatory": true,
				"collectionTypes": [
					"Dataset"
				],
				"ruleEnabled": true
			},
			{
				"attribute": "description",
				"mandatory": true,
				"collectionTypes": [
					"Dataset"
				],
				"ruleEnabled": true
			},
			{
				"attribute": "source_lab_pi",
				"mandatory": true,
				"collectionTypes": [
					"Project"
				],
				"ruleEnabled": true
			},
			{
				"attribute": "lab_branch",
				"mandatory": true,
				"collectionTypes": [
					"Project"
				],
				"ruleEnabled": true
			},
			{
				"attribute": "pi_doc",
				"mandatory": true,
				"collectionTypes": [
					"Project"
				],
				"validValues": [
					"FNLCR",
					"DUMMY",
					"CCBR"
				],
				"ruleEnabled": true
			},
			{
				"attribute": "original_date_created",
				"mandatory": true,
				"collectionTypes": [
					"Project"
				],
				"defaultValue": "System-Date",
				"ruleEnabled": true
			},
			{
				"attribute": "phi_content",
				"mandatory": true,
				"collectionTypes": [
					"Dataset"
				],
				"defaultValue": "Unspecified",
				"validValues": [
					"Unspecified",
					"PHI Present",
					"PHI Not Present",
					"Not Specified"
				],
				"ruleEnabled": true
			},
			{
				"attribute": "pii_content",
				"mandatory": true,
				"collectionTypes": [
					"Dataset"
				],
				"defaultValue": "Unspecified",
				"validValues": [
					"Unspecified",
					"PII Present",
					"PII Not Present",
					"Not Specified"
				],
				"ruleEnabled": true
			},
			{
				"attribute": "data_encryption_status",
				"mandatory": true,
				"collectionTypes": [
					"Dataset"
				],
				"defaultValue": "Unspecified",
				"validValues": [
					"Unspecified",
					"Encrypted",
					"Not Encrypted",
					"Not Specified"
				],
				"ruleEnabled": true
			},
			{
				"attribute": "data_compression_status",
				"mandatory": true,
				"collectionTypes": [
					"Dataset"
				],
				"defaultValue": "Unspecified",
				"validValues": [
					"Unspecified",
					"Compressed",
					"Not Compressed",
					"Not Specified"
				],
				"ruleEnabled": true
			},
			{
				"attribute": "funding_organization",
				"mandatory": false,
				"collectionTypes": [
					"Dataset"
				],
				"defaultValue": "Unspecified",
				"ruleEnabled": true
			},
			{
				"attribute": "flow_cell_id",
				"mandatory": true,
				"collectionTypes": [
					"Dataset"
				],
				"ruleEnabled": true
			},
			{
				"attribute": "run_id",
				"mandatory": true,
				"collectionTypes": [
					"Dataset"
				],
				"ruleEnabled": true
			},
			{
				"attribute": "run_date",
				"mandatory": true,
				"collectionTypes": [
					"Dataset"
				],
				"ruleEnabled": true
			},
			{
				"attribute": "sequencing_platform",
				"mandatory": true,
				"collectionTypes": [
					"Dataset"
				],
				"defaultValue": "Unspecified",
				"validValues": [
					"Unspecified",
					"Illumina-MiSeq",
					"Illumina-NextSeq",
					"Illumina-HiSeq",
					"Illumina-HiSeq X",
					"Ion-PGM",
					"Ion-Proton",
					"PacBio-PacBio RS"
				],
				"ruleEnabled": true
			},
			{
				"attribute": "sequencing_application_type",
				"mandatory": true,
				"collectionTypes": [
					"Dataset"
				],
				"defaultValue": "Unspecified",
				"validValues": [
					"Unspecified",
					"DNA-Seq (Re-Sequencing)",
					"DNA-Seq (De novo assembly)",
					"SNP Analysis / Rearrangement Detection",
					"Exome",
					"ChiP-Seq"
				],
				"ruleEnabled": true
			},
			{
				"attribute": "library_id",
				"mandatory": true,
				"collectionTypes": [
					"Dataset"
				],
				"ruleEnabled": true
			},
			{
				"attribute": "library_name",
				"mandatory": true,
				"collectionTypes": [
					"Dataset"
				],
				"defaultValue": "Unspecified",
				"validValues": [
					"Unspecified",
					"TruSeq ChIP-Seq",
					"Illumina TrueSeq"
				],
				"ruleEnabled": true
			},
			{
				"attribute": "library_type",
				"mandatory": true,
				"collectionTypes": [
					"Dataset"
				],
				"defaultValue": "Unspecified",
				"validValues": [
					"Unspecified",
					"TruSeq ChIP-Seq",
					"Illumina TrueSeq"
				],
				"ruleEnabled": true
			},
			{
				"attribute": "library_protocol",
				"mandatory": true,
				"collectionTypes": [
					"Dataset"
				],
				"defaultValue": "Unspecified",
				"validValues": [
					"Unspecified",
					"TruSeq ChIP-Seq",
					"Illumina TrueSeq"
				],
				"ruleEnabled": true
			}
		 ]
	    }',
	   '{
		 "metadataValidationRules": [{
				"attribute": "name",
				"mandatory": true,
				"ruleEnabled": true
			},
			{
				"attribute": "description",
				"mandatory": true,
				"ruleEnabled": true
			},
			{
				"attribute": "source_lab_pi",
				"mandatory": true,
				"ruleEnabled": true
			},
			{
				"attribute": "lab_branch",
				"mandatory": true,
				"ruleEnabled": true
			},
			{
				"attribute": "pi_doc",
				"mandatory": true,
				"validValues": [
					"FNLCR",
					"DUMMY",
					"CCBR"
				],
				"ruleEnabled": true
			},
			{
				"attribute": "original_date_created",
				"mandatory": true,
				"defaultValue": "System-Date",
				"ruleEnabled": true
			},
			{
				"attribute": "phi_content",
				"mandatory": true,
				"defaultValue": "Unspecified",
				"validValues": [
					"Unspecified",
					"PHI Present",
					"PHI Not Present",
					"Not Specified"
				],
				"ruleEnabled": true
			},
			{
				"attribute": "pii_content",
				"mandatory": true,
				"defaultValue": "Unspecified",
				"validValues": [
					"Unspecified",
					"PII Present",
					"PII Not Present",
					"Not Specified"
				],
				"ruleEnabled": true
			},
			{
				"attribute": "data_encryption_status",
				"mandatory": true,
				"defaultValue": "Unspecified",
				"validValues": [
					"Unspecified",
					"Encrypted",
					"Not Encrypted",
					"Not Specified"
				],
				"ruleEnabled": true
			},
			{
				"attribute": "data_compression_status",
				"mandatory": true,
				"defaultValue": "Unspecified",
				"validValues": [
					"Unspecified",
					"Compressed",
					"Not Compressed",
					"Not Specified"
				],
				"ruleEnabled": true
			},
			{
				"attribute": "funding_organization",
				"mandatory": false,
				"defaultValue": "Unspecified",
				"ruleEnabled": true
			},
			{
				"attribute": "flow_cell_id",
				"mandatory": true,
				"ruleEnabled": true
			},
			{
				"attribute": "run_id",
				"mandatory": true,
				"ruleEnabled": true
			},
			{
				"attribute": "run_date",
				"mandatory": true,
				"ruleEnabled": true
			},
			{
				"attribute": "sequencing_platform",
				"mandatory": true,
				"defaultValue": "Unspecified",
				"validValues": [
					"Unspecified",
					"Illumina-MiSeq",
					"Illumina-NextSeq",
					"Illumina-HiSeq",
					"Illumina-HiSeq X",
					"Ion-PGM",
					"Ion-Proton",
					"PacBio-PacBio RS"
				],
				"ruleEnabled": true
			},
			{
				"attribute": "sequencing_application_type",
				"mandatory": true,
				"defaultValue": "Unspecified",
				"validValues": [
					"Unspecified",
					"DNA-Seq (Re-Sequencing)",
					"DNA-Seq (De novo assembly)",
					"SNP Analysis / Rearrangement Detection",
					"Exome",
					"ChiP-Seq"
				],
				"ruleEnabled": true
			},
			{
				"attribute": "library_id",
				"mandatory": true,
				"ruleEnabled": true
			},
			{
				"attribute": "library_name",
				"mandatory": true,
				"defaultValue": "Unspecified",
				"validValues": [
					"Unspecified",
					"TruSeq ChIP-Seq",
					"Illumina TrueSeq"
				],
				"ruleEnabled": true
			},
			{
				"attribute": "library_type",
				"mandatory": true,
				"defaultValue": "Unspecified",
				"validValues": [
					"Unspecified",
					"TruSeq ChIP-Seq",
					"Illumina TrueSeq"
				],
				"ruleEnabled": true
			},
			{
				"attribute": "library_protocol",
				"mandatory": true,
				"defaultValue": "Unspecified",
				"validValues": [
					"Unspecified",
					"TruSeq ChIP-Seq",
					"Illumina TrueSeq"
				],
				"ruleEnabled": true
			}
		 ]
	    }');

INSERT INTO public."HPC_DOC_CONFIGURATION" 
VALUES('CCR-LEEMAX', '/CCR_LEEMAX_Archive', 'fr-s-clvrsf-01', 'HPC_DME_Production_Vault', 'HPC_DME_Prod_Archive',
       'ARCHIVE', NULL, NULL, NULL);

INSERT INTO public."HPC_DOC_CONFIGURATION" 
VALUES('HiTIF', '/HiTIF_Archive', 'fr-s-clvrsf-01', 'HPC_DME_Production_Vault', 'HPC_DME_Prod_Archive',
       'ARCHIVE', NULL, NULL, NULL);
                                                  
INSERT INTO public."HPC_DOC_CONFIGURATION" 
VALUES('DUMMY_NO_HIER', '/TEST_NO_HIER_Archive', 'fr-s-clvrsf-01', 'HPC_DME_Production_Vault', 'HPC_DME_Prod_Archive',
       'ARCHIVE', NULL, NULL, NULL);

INSERT INTO public."HPC_DOC_CONFIGURATION" 
VALUES('JDACS4C', '/JDACS4C_Archive', 'fr-s-clvrsf-01', 'HPC_DME_Production_Vault', 'HPC_DME_Prod_Archive',
       'ARCHIVE', NULL, NULL, NULL);

INSERT INTO public."HPC_DOC_CONFIGURATION" 
VALUES('ATOM', '/ATOM_Archive', 'fr-s-clvrsf-01', 'HPC_DME_Production_Vault', 'HPC_DME_Prod_Archive',
       'ARCHIVE', NULL, NULL, NULL);

INSERT INTO public."HPC_DOC_CONFIGURATION" 
VALUES('MoCha', '/MoCha_Archive', 'fr-s-clvrsf-01', 'HPC_DME_Production_Vault', 'HPC_DME_Prod_Archive',
       'ARCHIVE', NULL, NULL, NULL);

INSERT INTO public."HPC_DOC_CONFIGURATION" 
VALUES('RAS_INF', '/RAS_INF_Archive', 'fr-s-clvrsf-01', 'HPC_DME_Production_Vault', 'HPC_DME_Prod_Archive',
       'ARCHIVE', NULL, NULL, NULL);

INSERT INTO public."HPC_DOC_CONFIGURATION" 
VALUES('BIOWULF', '/BIOWULF_Archive', 'fr-s-clvrsf-01', 'HPC_DME_Production_Vault', 'HPC_DME_Prod_Archive',
       'ARCHIVE', NULL, NULL, NULL);

INSERT INTO public."HPC_DOC_CONFIGURATION" 
VALUES('DSITP_FNL', '/DSITP_FNL_Archive', 'fr-s-clvrsf-01', 'HPC_DME_Production_Vault', 'HPC_DME_Prod_Archive',
       'ARCHIVE', NULL, NULL, NULL);

import logging
import sys
import os
import json


from metadata.sf_object import SFObject
from metadata.sf_collection import SFCollection
from metadata.sf_helper import SFHelper
from common.sf_utils import SFUtils
from common.sf_audit import SFAudit

def main(args):

    if (len(sys.argv) < 5):
        print("\n Usage: python app.py tarfile_list tarfile_dir extract_path audit_dir dryrun initial_bytes initial_file_count")
        return

    #The file containing the tarlist
    tarfile_list = args[1]

    #The directory containing the tarfiles
    tarfile_dir = args[2]

    #path containing the extracted file
    extract_path = args[3]

    #sub-directory to hold the log and audit files
    audit_dir = args[4]

    # If this is a dryrun or not
    dryrun = args[5].lower() == 'true'

    bytes_stored = 0
    files_registered = 0

    if(args[6] is not None):
        bytes_stored = args[6]
        if(args[7] is not None):
            files_registered = args[7]

    sf_audit = SFAudit(audit_dir, extract_path, bytes_stored, files_registered)
    sf_audit.prep_for_audit()

    for line_filepath in open(tarfile_list).readlines():

        tarfile_name = line_filepath.rstrip()

        tarfile_path = tarfile_dir + '/' + tarfile_name.rstrip()

        # This is a valid tarball, so process
        logging.info("Processing file: " + tarfile_path)

        if (tarfile_name.endswith("supplement.tar") or 'singlecell' in tarfile_name or '10x' in tarfile_name):

            # Register PI collection
            register_collection(tarfile_path, "PI_Lab", tarfile_name, False, dryrun)

            # Register Flowcell collection with Project type parent
            register_collection(tarfile_path, "Flowcell", tarfile_name, True, dryrun)

            # create Object metadata with Flowcell type parent and register object
            register_object(tarfile_path, "Flowcell", tarfile_name, False, tarfile_path, sf_audit, dryrun)

            logging.info('Done processing file: ' + tarfile_path)

            continue;

        tarfile_contents = SFUtils.get_tarball_contents(tarfile_name, tarfile_dir)
        if tarfile_contents is None:
            continue

        # Extract all files and store in extract_path directory
        if not dryrun:
            if not (SFUtils.extract_files_from_tar(tarfile_path, extract_path)):
                # Something wrong with this file path, go to
                # next one and check logs later
                continue;

        #loop through each line in the contents file of this tarball
        #We need to do an upload for each fatq.gz or BAM file
        for line in tarfile_contents.readlines():

            if(line.rstrip().endswith("/")):
                #This is a directory, nothing to do
                continue

            # if SFUtils.path_contains_exclude_str(tarfile_name, line.rstrip()):
            exclusion_list = ['10X', 'Phix', 'PhiX', 'demux', 'demultiplex']
            if any(ext in line.rstrip() for ext in exclusion_list):
                sf_audit.record_exclusion(tarfile_name, line.rstrip(),
                    'Path contains substring from exclusion list')
                continue

            filepath = SFUtils.get_filepath_to_archive(line.rstrip(), extract_path)

            if filepath.endswith('fastq.gz') or filepath.endswith('fastq.gz.md5'):

                # Extract the info for PI metadata
                path = SFUtils.get_meta_path(filepath)
                ext = SFUtils.get_unaligned_ext(filepath)

                # Register PI collection
                register_collection(path, "PI_Lab", tarfile_name, False, sf_audit, dryrun)

                #Register Flowcell collection with Project type parent
                register_collection(path, "Flowcell", tarfile_name, True, sf_audit, dryrun, ext)

                #create Object metadata with Sample type parent and register object
                register_object(path, "Sample", tarfile_name, True, filepath, sf_audit, dryrun)

            elif filepath.endswith('laneBarcode.html') and '/all/' in filepath and not 'Control_Sample' in filepath:

                #Remove the string after the first '/all' because metadata path if present will be before that
                head, sep, tail = line.partition('all/')

                #Remove everything upto the Flowcell_id, because metadata path if present will be after that
                flowcell_id = SFHelper.get_flowcell_id(tarfile_name)
                if flowcell_id in head:

                    path = head.split(flowcell_id + '/')[-1]

                    #Ensure that metadata path does not have the Sample sub-directory and that it is valid
                    if path.count('/') == 1 and '_' in path:

                        #Register the html in flowcell collection

                        path = path + 'laneBarcode.html'
                        logging.info('metadata base: ' + path)

                        # Register PI collection
                        register_collection(path, "PI_Lab", tarfile_name, False, sf_audit, dryrun)

                        # Register Flowcell collection with Project type parent
                        register_collection(path, "Flowcell", tarfile_name, True, sf_audit, dryrun)

                        # create Object metadata with Flowcell type parent and register object
                        register_object(path, "Flowcell", tarfile_name, False, filepath, sf_audit, dryrun)

                    else:
                        # ignore this html
                        SFUtils.record_exclusion(tarfile_name, line.rstrip(), 'html path not valid - may have other sub-directory')
                        continue

                else:
                    #ignore this html
                    SFUtils.record_exclusion(tarfile_name, line.rstrip(), 'html path not valid - could not extract flowcell_id')
                    continue

            else:
                #For now, we ignore files that are not fastq.gz or html
                SFUtils.record_exclusion(tarfile_name, line.rstrip(), 'Not fastq.gz, fastq.gz.md5 or valid html file')

        logging.info('Done processing file: ' + tarfile_path)
        #delete the extracted tar file
        os.system("rm -rf " + extract_path + "*")

    sf_audit.audit_summary()



def register_collection(filepath, type, tarfile_name, has_parent, sf_audit, dryrun, ext = None):

    logging.info("Registering " + type + " collection for " + filepath)
    json_path = sf_audit.audit_path + '/jsons_dryrun'

    # create the audit directory if it does not exist
    if not os.path.exists(json_path):
        os.mkdir(json_path)

    #Build metadata for the collection
    collection = SFCollection(filepath, type, tarfile_name, has_parent, ext)
    collection_metadata = collection.get_metadata()

    #Create the metadata json file
    file_name = filepath.split("/")[-1]
    json_file_name = json_path + '/' + type + "_" + file_name + ".json"
    with open(json_file_name, "w") as fp:
        json.dump(collection_metadata, fp)

    #Prepare the command
    archive_path = SFCollection.get_archive_path(tarfile_name, filepath, type)
    command = "dm_register_collection " + json_file_name + " " + archive_path

    #Audit the command
    logging.info(command)

    #Run the command
    response_header = "collection-registration-response-header.tmp"
    if not dryrun:
        os.system("rm - f " + response_header + " 2>/dev/null")
        os.system(command)

        #Audit the result
        with open(response_header) as f:
            for line in f:
                logging.info(line)



def register_object(filepath, type, tarfile_name, has_parent, fullpath, sf_audit, dryrun):

    global files_registered, bytes_stored
    #Build metadata for the object
    object_to_register = SFObject(filepath, tarfile_name, has_parent, type)
    object_metadata = object_to_register.get_metadata()
    json_path = sf_audit.audit_path + '/jsons_dryrun'

    # create the metadata json file
    file_name = filepath.split("/")[-1]
    json_file_name = json_path + '/' + file_name + ".json"
    with open(json_file_name, "w") as fp:
        json.dump(object_metadata, fp)

    #Prepare the command
    archive_path = SFCollection.get_archive_path(tarfile_name, filepath, type)
    archive_path = archive_path + '/' + file_name
    command = "dm_register_dataobject " + json_file_name + " " + archive_path + " " + fullpath

    #Record the command
    sf_audit.audit_command(command)

    # Run the command
    if not dryrun:
        response_header = "dataObject-registration-response-header.tmp"
        os.system("rm - f " + response_header + " 2>/dev/null")
        os.system(command)

    #Audit the result - No run performed
    sf_audit.audit_upload(tarfile_name, filepath, fullpath, archive_path, dryrun)



main(sys.argv)

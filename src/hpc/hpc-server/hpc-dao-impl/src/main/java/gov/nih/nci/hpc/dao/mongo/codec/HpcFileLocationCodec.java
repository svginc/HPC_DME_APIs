/**
 * HpcFileLocationCodec.java
 *
 * Copyright SVG, Inc.
 * Copyright Leidos Biomedical Research, Inc
 * 
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/HPC/LICENSE.txt for details.
 */

package gov.nih.nci.hpc.dao.mongo.codec;

import gov.nih.nci.hpc.domain.datatransfer.HpcFileLocation;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.Document;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

/**
 * <p>
 * HPC File Location Codec. 
 * </p>
 *
 * @author <a href="mailto:eran.rosenberg@nih.gov">Eran Rosenberg</a>
 * @version $Id$
 */

public class HpcFileLocationCodec extends HpcCodec<HpcFileLocation>
{ 
    //---------------------------------------------------------------------//
    // Constructors
    //---------------------------------------------------------------------//
	
    /**
     * Default Constructor.
     * 
     */
    public HpcFileLocationCodec() 
    {
    }   
    
    //---------------------------------------------------------------------//
    // Methods
    //---------------------------------------------------------------------//
    
    //---------------------------------------------------------------------//
    // Codec<HpcFileLocation> Interface Implementation
    //---------------------------------------------------------------------//  
    
	@Override
	public void encode(BsonWriter writer, HpcFileLocation location,
					   EncoderContext encoderContext) 
	{
		Document document = new Document();

		// Extract the data from the POJO.
		String endpoint = location.getEndpoint();
		String path = location.getPath();

		if(endpoint != null) {
		   document.put(FILE_LOCATION_ENDPOINT_KEY, endpoint);
		}
		if(path != null) {
		   document.put(FILE_LOCATION_PATH_KEY, path);
		}
		
		getRegistry().get(Document.class).encode(writer, document, 
				                                 encoderContext);
	}
 
	@Override
	public HpcFileLocation decode(BsonReader reader, 
			                      DecoderContext decoderContext) 
	{
		// Get the BSON Document.
		Document document = 
	             getRegistry().get(Document.class).decode(reader, 
	            		                                  decoderContext);
		
		// Map the document to HpcDataset instance.
		HpcFileLocation location = new HpcFileLocation();
		location.setEndpoint(document.getString(FILE_LOCATION_ENDPOINT_KEY));
		location.setPath(document.getString(FILE_LOCATION_PATH_KEY));
		
		return location;
	}
	
	@Override
	public Class<HpcFileLocation> getEncoderClass() 
	{
		return HpcFileLocation.class;
	}
}

 
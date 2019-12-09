package com.acme.ocr.alf;

import java.io.InputStream;
import java.io.OutputStream;

import org.alfresco.repo.content.MimetypeMap;
import org.alfresco.repo.content.transform.AbstractContentTransformer2;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.TransformationOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AwsTextractTransformer extends AbstractContentTransformer2 {

	private static final Logger logger = LoggerFactory.getLogger(AwsTextractTransformer.class);

	private AwsTextractService awsTextractService;

	private NodeService nodeService;

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	public void setAwsTextractService(AwsTextractService awsTextractService) {
		this.awsTextractService = awsTextractService;
	}

	@Override
	public boolean isTransformableMimetype(String sourceMimetype, String targetMimetype,
			TransformationOptions options) {
		// TODO mimetype jpeg, png, pdf
		return (MimetypeMap.MIMETYPE_IMAGE_PNG.equals(sourceMimetype) 
				&& MimetypeMap.MIMETYPE_PDF.equals(targetMimetype)
//				&& isScannedDocument(options.getSourceNodeRef())
				);
	}

	private boolean isScannedDocument(NodeRef sourceNodeRef) {
		if (sourceNodeRef != null) {
			return this.nodeService.hasAspect(sourceNodeRef, ScannedDocumentAspect.ASPECT_SCANNED);
		}
		return false;
	}

	@Override
	protected void transformInternal(ContentReader reader, ContentWriter writer, TransformationOptions options)
			throws Exception {

		try (InputStream in = reader.getContentInputStream(); OutputStream out = writer.getContentOutputStream()) {
			//TODO
			logger.debug("transformInternal, id={}", options.getSourceNodeRef().getId());
			this.awsTextractService.generatePDF(in, out, MimetypeMap.MIMETYPE_IMAGE_PNG);
		}

	}

}

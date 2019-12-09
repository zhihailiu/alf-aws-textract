package com.acme.ocr.alf;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

public class AwsTextractService {

	private static final Logger logger = LoggerFactory.getLogger(AwsTextractService.class);

	private String endpoint;
	
	private static final String FILENAME = "file.dat";
	
	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}

	public void generatePDF(InputStream in, OutputStream out, String mimetype) throws IOException {
		logger.debug("generatePDF, mimetype={}", mimetype);

		byte[] imageBytes = IOUtils.toByteArray(in);
		ByteArrayResource data = new ByteArrayResource(imageBytes) {
			@Override
			public String getFilename() {
				return FILENAME;
			}
		};
		
		MultiValueMap<String, Object> request = new LinkedMultiValueMap<>();
	    request.add("name", FILENAME);
	    request.add("filename", FILENAME);
		request.add("file", data);

		RestTemplate restTemplate = new RestTemplate();

		long start = System.currentTimeMillis();
		byte[] pdfBytes = restTemplate.postForObject(endpoint, request, byte[].class);
		long end = System.currentTimeMillis();
		logger.debug("completed, time={} ms, size={} bytes", (end - start), pdfBytes.length);
		
		IOUtils.copy(new ByteArrayInputStream(pdfBytes), out);
	}

}
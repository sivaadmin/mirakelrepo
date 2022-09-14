package com.macys.mirakl.service;

import static com.macys.mirakl.util.OrchConstants.BUCKET;
import static com.macys.mirakl.util.OrchConstants.OBJECT_NAME;
import static com.macys.mirakl.util.OrchConstants.INPUT_FILE_NAME;
import static com.macys.mirakl.util.OrchConstants.OFFER_REQ_FILE;
import static com.macys.mirakl.util.OrchConstants.OFFER_RESP_FILE;
import static com.macys.mirakl.util.OrchConstants.WRONG_FILE;

import java.io.IOException;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.macys.mirakl.exception.MiraklRepositoryException;
import com.macys.mirakl.model.OfferIdRequestData;
import com.macys.mirakl.model.OfferIdResponseData;
import com.macys.mirakl.processor.FileProcessor;

@Transactional
@Service
public class OfferIdService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(OfferIdService.class);
	
	@Autowired
	private CloudStorageService cloudStorageService;

	@Autowired
	private SQLService sqlService;
	
	@Autowired
	private FileProcessor fileProcessor;
	
	@Autowired
	private NotificationService notificationService;

	/**
	 * 
	 * @param payload
	 */
	public void processOfferIdReqRespFile(String payload, String subscription) {
		JSONObject jsonObject = new JSONObject(payload);
		String bucketName = String.valueOf(jsonObject.get(BUCKET));
		String objectName = String.valueOf(jsonObject.get(OBJECT_NAME));
		LOGGER.info("bucket={} objectName={}", bucketName, objectName);
		try {
			String fileName = fileProcessor.getFileName(objectName);
			MDC.put(INPUT_FILE_NAME,fileName);
			String fileOperation = fileProcessor.getFileOperationForOfferId(objectName);
			LOGGER.info("fileName={} fileOperation={}", fileName, fileOperation);
			boolean isValidFileOperation = false;
			
			if (WRONG_FILE.equals(fileOperation)) {
                LOGGER.error("File name or file path or file extension is not proper for Offer file :" + objectName);
                return;
            } else {
            	isValidFileOperation = true;
				// Check for multiple notification
            	boolean processFile = notificationService.processNotification(payload, subscription, isValidFileOperation);
    			if (!processFile) {
					LOGGER.info("Exiting from processOfferIdReqRespFile");
					return;
				}
				
				LOGGER.info("Proceed with file processing after validating notification");
				if (OFFER_REQ_FILE.equals(fileOperation)) {
	                LOGGER.info("OfferId Update Request received.");
	                processOfferIdReqList(bucketName, objectName, fileName);
	            } else if (OFFER_RESP_FILE.equals(fileOperation)) {
	            	LOGGER.info("OfferId Update Response received.");
	            	processOfferIdRespList(bucketName, objectName);
	            }
				
            } 
		} catch (IOException ioe) {
			LOGGER.error("IOException occurred while converting to json: ", ioe);
		} catch (IllegalArgumentException iae) {
			LOGGER.error("IllegalArgumentException occurred while processing offerId json: ", iae);
		} catch (JSONException je) {
			LOGGER.error("JSONException occurred while processing offerId json: ", je);
		} catch (MiraklRepositoryException mre) {
			LOGGER.error("MiraklRepositoryException occurred on interacting with repository: ", mre);
		} catch (Exception e) {
			LOGGER.error("Exception in processOfferIdReqRespFile: ", e);
		}
	}

	private void processOfferIdReqList(String bucketName, String objectName, String fileName)
			throws IOException, MiraklRepositoryException {
		List<OfferIdRequestData> offerIdReqList = cloudStorageService.readOfferIdReqFile(bucketName, objectName);
		if(!offerIdReqList.isEmpty() && offerIdReqList.size()>0) {
			LOGGER.info("Data available in incoming OfferId request JSON file : list size: "+offerIdReqList.size());				
			sqlService.insertOfferIdAuditList(offerIdReqList, fileName);
			LOGGER.info("Success Write in table MP_OFFER_AUDIT table");
		} else {
			LOGGER.error("No data in OfferId request JSON file");
		}
	}

	private void processOfferIdRespList(String bucketName, String objectName)
			throws IOException, MiraklRepositoryException {
		List<OfferIdResponseData> offerIdRespList = cloudStorageService.readOfferIdRespFile(bucketName, objectName);
		if(!offerIdRespList.isEmpty() && offerIdRespList.size()>0) {
			LOGGER.info("Data available in incoming OfferId response JSON file : list size: "+offerIdRespList.size());
			sqlService.updateOfferIdAuditList(offerIdRespList);
			LOGGER.info("Success Update in MP_OFFER_AUDIT table");
			sqlService.updateOfferIdMasterList(offerIdRespList);
			LOGGER.info("Success Update in MP_UPC_MASTER table");
		} else {
			LOGGER.error("No data in OfferId response JSON file");
		}
	}
}

package com.macys.mirakl.service;

import static com.macys.mirakl.util.OrchConstants.BUCKET;
import static com.macys.mirakl.util.OrchConstants.OBJECT_NAME;
import static com.macys.mirakl.util.OrchConstants.FILE_SIZE;
import static com.macys.mirakl.util.OrchConstants.INPUT_FILE_NAME;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.macys.mirakl.config.NotificationConfig;
import com.macys.mirakl.processor.FileProcessor;

@Transactional
@Service
public class NotificationService {

	private static final Logger LOGGER = LoggerFactory.getLogger(NotificationService.class);

	@Autowired
	private SQLService sqlService;

	@Autowired
	private FileProcessor fileProcessor;

	@Value("${com.macys.mp.pubsub.product.update.ol.onprem.pub}")
	private String prdUpdateOLOnPremTopic;

	@Value("${com.macys.mp.pubsub.product.image.ol.onprem.pub}")
	private String prdImageOLOnPremTopic;

	@Value("${com.macys.mp.pubsub.product.update.staging.sub}")
	private String prdUpdateStagingSub;

	@Value("${com.macys.mp.pubsub.product.image.staging.sub}")
	private String prdImageStagingSub;

	@Autowired
	private NotificationConfig.PubsubOutboundGatewayPrdUpdate messagingGatewayPrdUpdate;

	@Autowired
	private NotificationConfig.PubsubOutboundGatewayPrdImage messagingGatewayPrdImage;

	public boolean processNotification(String payload, String subscription, boolean isValidFileOperation) throws Exception {
		boolean processFile = false;
		JSONObject jsonObject = new JSONObject(payload);
		String bucketName = String.valueOf(jsonObject.get(BUCKET));
		LOGGER.info("bucket:{}, subscription:{}", bucketName, subscription);
		try {
			String objectName = String.valueOf(jsonObject.get(OBJECT_NAME));
			String fileName = fileProcessor.getFileName(objectName);
			int fileSize = Integer.parseInt(String.valueOf(jsonObject.get(FILE_SIZE)));
			LOGGER.info("objectName:{}, fileName:{}, fileSize:{}", objectName, fileName, fileSize);
			MDC.put(INPUT_FILE_NAME, fileName);

			synchronized (this) {
				/* Restrict notification check for mp-mirakl-orch for product create/update req
				 files, product update delta files, offerid req/resp files and product update resp
				 files, images ONLY */
				if (0 < fileSize && validateFile(subscription, isValidFileOperation, fileName)) {
					int count = sqlService.findNotificationDetails(subscription, fileName, bucketName);
					LOGGER.info("count=" + count);
					if (count == 0) {
						processFile = true;
						sqlService.insertNotificationDetails(subscription, fileName, bucketName);
						if (prdUpdateStagingSub.equalsIgnoreCase(subscription)) {
							messagingGatewayPrdUpdate.sendToPubsubPrdUpdatePub(objectName);
							LOGGER.info("Published objectName in Payload to topic:" + prdUpdateOLOnPremTopic);
						} else if (prdImageStagingSub.equalsIgnoreCase(subscription)) {
							messagingGatewayPrdImage.sendToPubsubPrdImagePub(objectName);
							LOGGER.info("Published objectName in Payload to topic:" + prdImageOLOnPremTopic);
						}
					} else {
						LOGGER.info("File already received in the same subscription, hence not processing");
					}
				} else {
					LOGGER.info("Either filesize is 0 OR not a valid file and hence not processing");
				}
			}

		} catch (Exception e) {
			LOGGER.error("Exception in processNotification: ", e);
			throw new Exception("Exception in processNotification");
		}
		return processFile;
	}

	private boolean validateFile(String subscription, boolean isValidFileOperation, String fileName) {
		return isValidFileOperation || prdImageStagingSub.equalsIgnoreCase(subscription)
				|| fileName.contains("product_update") || fileName.contains("offer");
	}
}

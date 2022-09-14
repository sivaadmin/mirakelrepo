package com.macys.mirakl.service;

import static com.macys.mirakl.util.OrchConstants.BUCKET;
import static com.macys.mirakl.util.OrchConstants.OBJECT_NAME;
import static com.macys.mirakl.util.OrchConstants.INPUT_FILE_NAME;
import static com.macys.mirakl.util.OrchConstants.PRODUCT_CREATE;
import static com.macys.mirakl.util.OrchConstants.WRONG_FILE;

import java.util.List;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.macys.mirakl.model.MiraklData;
import com.macys.mirakl.processor.FileProcessor;

@Service
public class ProductCreateOrUpdateService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProductCreateOrUpdateService.class);

    @Autowired
    private FileProcessor fileProcessor;

    @Autowired
    private CloudStorageService cloudStorageService;

    @Autowired
    private ProductUpdateService prodUpdateService;

    @Autowired
    private ProductCreateService productCreateService;
    
    @Autowired
	private NotificationService notificationService;

    /**
     *
     * @param payload
     * @throws IllegalAccessException
     */
    public void processIntegrationFile(String payload, String subscription) {
        JSONObject jsonObject = new JSONObject(payload);
        String bucketName = String.valueOf(jsonObject.get(BUCKET));
        String objectName = String.valueOf(jsonObject.get(OBJECT_NAME));
        LOGGER.info("bucket={} objectName={}", bucketName, objectName);
        try {
            String fileName = fileProcessor.getFileName(objectName);
            MDC.put(INPUT_FILE_NAME,fileName);
            String fileOperation = fileProcessor.getFileOperation(objectName);
            LOGGER.info("fileName={} fileOperation={}", fileName, fileOperation);
            boolean isValidFileOperation = false;
            
            if (WRONG_FILE.equals(fileOperation)) {
                LOGGER.error("File name or file path or file extension is not proper for product Create or Update :" + objectName);
                return;
            }
            
            // Check for multiple notification
            isValidFileOperation = true;
            boolean processFile = notificationService.processNotification(payload, subscription, isValidFileOperation);
			if (!processFile) {
 				LOGGER.info("Exiting from processIntegrationFile");
 				return;
 			}
         	
 			LOGGER.info("Proceed with file processing after validating notification");
            List<MiraklData> prdTxns = cloudStorageService.readCreateOrUpdateFile(bucketName, objectName, fileName);
            if (PRODUCT_CREATE.equals(fileOperation)) {
                productCreateService.processProductCreate(fileName, prdTxns);
            } else {
                prodUpdateService.processProductUpdate(fileName, prdTxns);
            }
        }catch (Exception e) {
            LOGGER.error("Exception occurred on processing productCreateOrUpdate json Request file:", e);
        }
    }

}

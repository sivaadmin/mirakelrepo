package com.macys.mirakl.service;

import static com.macys.mirakl.util.OrchConstants.IMAGE1;
import static com.macys.mirakl.util.OrchConstants.IMAGE2;
import static com.macys.mirakl.util.OrchConstants.IMAGE_FILE_NAME;
import static com.macys.mirakl.util.OrchConstants.IMAGE_ID;
import static com.macys.mirakl.util.OrchConstants.IMAGE_TYPE;
import static com.macys.mirakl.util.OrchConstants.IMAGE_URL;
import static com.macys.mirakl.util.OrchConstants.MAIN_IMAGE;
import static com.macys.mirakl.util.OrchConstants.NON_PRIMARY_IMAGE;
import static com.macys.mirakl.util.OrchConstants.OP_DIV_IMAGES;
import static com.macys.mirakl.util.OrchConstants.PRIMARY_IMAGE;
import static com.macys.mirakl.util.OrchConstants.RESP_MESSAGE;
import static com.macys.mirakl.util.OrchConstants.RESP_STATUS;
import static com.macys.mirakl.util.OrchConstants.STATUS_SUCCESS_CODE;
import static com.macys.mirakl.util.OrchConstants.SUCCESS_MESSAGE;
import static com.macys.mirakl.util.OrchConstants.SWATCH_IMAGE;
import static com.macys.mirakl.util.OrchConstants.UPCID;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.MapDifference;
import com.google.common.collect.MapDifference.ValueDifference;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.macys.mirakl.exception.MiraklRepositoryException;
import com.macys.mirakl.model.ImageData;
import com.macys.mirakl.model.ImageMasterData;
import com.macys.mirakl.model.ImageUpdateRespData;

@Transactional
@Service
public class ImageUpdateRespService {
	private static final Logger LOGGER = LoggerFactory.getLogger(ImageUpdateRespService.class);

	@Autowired
	private SQLService sqlService;

	public void processImageRespMessage(String imagePayload) {

		LOGGER.info("Entering processImageRespMessage for processing Image Response message");
		
		try{
			// Parse image response message json
			ImageUpdateRespData imageUpdateRespData = parseImageRespJson(imagePayload);

			// Calculate ImageType from incoming ImageFileName
			findImageType(imageUpdateRespData);

			LOGGER.info("imageUpdateRespData::" + imageUpdateRespData);

			// Fetch the existing master data from MP_IMAGES_MASTER
			String imagesMasterJson = null, productType = null;
			ImageMasterData imageMasterData = null;
			try {
				imageMasterData = sqlService.findImageMasterJSONByUpc(imageUpdateRespData.getUpcId(),
						imageUpdateRespData.getOpDiv());
			} catch (Exception e) {
				LOGGER.error("Error in fetching master data from IMAGES :", e);
				return;
			}
			if (null != imageMasterData) {
				imagesMasterJson = imageMasterData.getImageData();
				productType = imageMasterData.getProductType();
				LOGGER.info("IMAGES master data : {}, product_type : {}", imagesMasterJson, productType);
			}
			
			// Fetch Image ID, image URL and Image Type from MP_IMAGES_RESPONSE to compare
			// with master json
			ImageUpdateRespData imageUpdateRespDataFromDB = null;
			try {
				imageUpdateRespDataFromDB = sqlService.fetchImagesRespMessage(imageUpdateRespData);
			} catch (Exception e) {
				LOGGER.error("Error in fetching data from MP_IMAGES_RESPONSE :", e);
				return;
			}
			LOGGER.info("IMAGES Response data from DB:" + imageUpdateRespDataFromDB);
			if(null!=imageUpdateRespDataFromDB) {		
				String inputFileName = imageUpdateRespDataFromDB.getInputFileName();
				JSONArray jsonArrayImagesIncoming = createImagesRespJsonArray(imageUpdateRespDataFromDB);
				String imagesJsonToBeUpdated = jsonArrayImagesIncoming.toString();
							
				// Update image data in MP_IMAGES_MASTER table for Status code '200' and Message
				// 'Success' in response
				if (imageUpdateRespData.getStatusCode().equalsIgnoreCase(STATUS_SUCCESS_CODE)
						&& imageUpdateRespData.getStatusMessage().equalsIgnoreCase(SUCCESS_MESSAGE)) {

					imagesJsonToBeUpdated = updateImagesMasterJson(imageUpdateRespData, imagesJsonToBeUpdated, imagesMasterJson,
							inputFileName, jsonArrayImagesIncoming);

				}

				// Update MP_IMAGES_RESPONSE with STATUS_CODE, STATUS_MESSAGE, IMG_FILE_NAME,
				// PROCESSING_STAGE and IAS_RESP_RECEIVED_FLAG
				sqlService.updateImagesRespMessage(imageUpdateRespData);

				// Insert in MP_IMAGES_AUDIT for each image response
				sqlService.insertImagesAuditData("UPDATE_RESPONSE", imageUpdateRespData, productType, inputFileName, imagesJsonToBeUpdated);
			}
			
		} catch (Exception e) {
			LOGGER.error("Exception in processImageRespMessage():", e);
		}

		LOGGER.info("Exiting processImageRespMessage for processing Image Response message");
	}

	private void findImageType(ImageUpdateRespData imageUpdateRespData) {
		if (imageUpdateRespData.getImgFileName().contains("_p0")) {
			imageUpdateRespData.setImgType(PRIMARY_IMAGE);
		} else if (imageUpdateRespData.getImgFileName().contains("_1")) {
			imageUpdateRespData.setImgType(IMAGE1);
		} else if (imageUpdateRespData.getImgFileName().contains("_2")) {
			imageUpdateRespData.setImgType(IMAGE2);
		} else if (imageUpdateRespData.getImgFileName().contains("_sw")) {
			imageUpdateRespData.setImgType(SWATCH_IMAGE);
		} else {
			imageUpdateRespData.setImgType(NON_PRIMARY_IMAGE);
		}
	}

	private ImageUpdateRespData parseImageRespJson(String imagePayload) {
		JSONObject imageDataJson = new JSONObject(imagePayload);

		ImageUpdateRespData imageUpdateRespData = new ImageUpdateRespData();
		imageUpdateRespData.setUpcId(String.valueOf(imageDataJson.get(UPCID.toLowerCase())));
		imageUpdateRespData.setOpDiv(String.valueOf(imageDataJson.get(OP_DIV_IMAGES)));
		imageUpdateRespData.setImgFileName(String.valueOf(imageDataJson.get(IMAGE_FILE_NAME)));
		imageUpdateRespData.setStatusCode(String.valueOf(imageDataJson.get(RESP_STATUS.toLowerCase())));
		imageUpdateRespData.setStatusMessage(String.valueOf(imageDataJson.get(RESP_MESSAGE.toLowerCase())));
		return imageUpdateRespData;
	}

	private JSONArray createImagesRespJsonArray(ImageUpdateRespData imageUpdateRespDataFromDB) {
		String imageUrl = imageUpdateRespDataFromDB.getImgUrl();
		String imageType = imageUpdateRespDataFromDB.getImgType();
		if (imageType.equalsIgnoreCase(PRIMARY_IMAGE)
				|| imageType.equalsIgnoreCase(NON_PRIMARY_IMAGE)) {
			imageType = MAIN_IMAGE;
		}
		String imageId = imageUpdateRespDataFromDB.getImgId();
		LOGGER.info("IMAGES RESPONSE data : imageUrl : {}, imageType : {}, imageId : {}", imageUrl, imageType,
				imageId);
		
		JSONObject imagesJson = new JSONObject();
		imagesJson.put(IMAGE_ID, imageId);
		imagesJson.put(IMAGE_TYPE, imageType);
		imagesJson.put(IMAGE_URL, imageUrl);
		LOGGER.info("imagesJson:" + imagesJson);

		JSONArray jsonArrayImagesIncoming = new JSONArray();
		jsonArrayImagesIncoming.put(imagesJson);
		return jsonArrayImagesIncoming;
	}

	private String updateImagesMasterJson(ImageUpdateRespData imageUpdateRespData, String imagesJsonToBeUpdated,
			String imagesMasterJson, String inputFileName, JSONArray jsonArrayImagesIncoming)
			throws MiraklRepositoryException {
		// Compare and replace the particular image data in master json
		if (null != imagesMasterJson) {
			JSONArray imageMasterJSONArray = replaceImagesMasterJson(imagesMasterJson, jsonArrayImagesIncoming);
			
			LOGGER.info("\n\nimageMasterJSONArray:" + imageMasterJSONArray);
			imagesJsonToBeUpdated = imageMasterJSONArray.toString();
			
			ImageMasterData imageMasterDataUpdated = new ImageMasterData();
			imageMasterDataUpdated.setUpcId(imageUpdateRespData.getUpcId());
			imageMasterDataUpdated.setOpDiv(imageUpdateRespData.getOpDiv());
			imageMasterDataUpdated.setImageData(imageMasterJSONArray.toString());
			imageMasterDataUpdated.setFileName(inputFileName);

			// Update master json data in MP_IMAGES_MASTER
			sqlService.updateImagesMasterJson(imageMasterDataUpdated);
		}
		return imagesJsonToBeUpdated;
	}

	private JSONArray replaceImagesMasterJson(String imagesMasterJson, JSONArray jsonArrayImagesIncoming) {
		JSONArray jsonArrayImagesMaster = new JSONArray(imagesMasterJson);
		Map<String, ImageData> changedPropsMap = new HashMap<>();
		Map<String, ImageData> imageDataMasterMap = getImageData(jsonArrayImagesMaster);
		Map<String, ImageData> imageDataIncomingMap = getImageData(jsonArrayImagesIncoming);
		MapDifference<String, ImageData> diffMap = null;
		Map<String, ValueDifference<ImageData>> entriesDifferingMap = null;
		
		diffMap = Maps.difference(imageDataMasterMap, imageDataIncomingMap);
		if(null != diffMap) {
			entriesDifferingMap = diffMap.entriesDiffering();
		}
		
		if (null != entriesDifferingMap && !entriesDifferingMap.isEmpty()) {
			entriesDifferingMap.entrySet()
					.forEach(entry -> changedPropsMap.put(entry.getKey(), entry.getValue().rightValue()));
		}
		imageDataMasterMap.putAll(changedPropsMap);
		
		LOGGER.info("\n\nimageDataMasterMap:" + imageDataMasterMap);
		
		Gson gson = new GsonBuilder().disableHtmlEscaping().create();
		JSONArray imageMasterJSONArray = new JSONArray();
		
		for (Map.Entry<String,ImageData> entry : imageDataMasterMap.entrySet()) {		        
			LOGGER.info("Key = " + entry.getKey() + ", Value = " + entry.getValue());
			ImageData imageData = entry.getValue();
			String json = gson.toJson(imageData);
			imageMasterJSONArray.put(new JSONObject(json));
		}
		return imageMasterJSONArray;
	}
	
	private Map<String, ImageData> getImageData(JSONArray jsonArrayImages) {
		Map<String, ImageData> imageDataMap = new HashMap<>();
		for (var imageData = 0; imageData < jsonArrayImages.length(); imageData++) {
			var imageItem = jsonArrayImages.getJSONObject(imageData);

			ImageData dataImage = ImageData.builder().imageId(imageItem.getString(IMAGE_ID))
					.imageType(imageItem.getString(IMAGE_TYPE)).imageUrl(imageItem.getString(IMAGE_URL)).build();
			imageDataMap.put(dataImage.getImageType(), dataImage);
		}
		LOGGER.info("\nimageDataMap:" + imageDataMap);
		return imageDataMap;
	}
}

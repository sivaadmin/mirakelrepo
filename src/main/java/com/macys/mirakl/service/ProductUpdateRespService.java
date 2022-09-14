package com.macys.mirakl.service;

import static com.macys.mirakl.util.OrchConstants.BUCKET;
import static com.macys.mirakl.util.OrchConstants.FACET_UPDATE_RESP;
import static com.macys.mirakl.util.OrchConstants.OBJECT_NAME;
import static com.macys.mirakl.util.OrchConstants.INPUT_FILE_NAME;
import static com.macys.mirakl.util.OrchConstants.PDF_FILE_NAME;
import static com.macys.mirakl.util.OrchConstants.PRODUCT_UPDATE;
import static com.macys.mirakl.util.OrchConstants.PRODUCT_UPDATE_RESP;
import static com.macys.mirakl.util.OrchConstants.STATUS_SUCCESS_CODE;
import static com.macys.mirakl.util.OrchConstants.SUCCESS_MESSAGE;
import static com.macys.mirakl.util.OrchConstants.UPDATE_FAILED;
import static com.macys.mirakl.util.OrchConstants.UPDATE_SUCCESS;
import static com.macys.mirakl.util.OrchConstants.WRONG_FILE;
import static com.macys.mirakl.util.OrchUtil.getAttributes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.MapDifference;
import com.google.common.collect.MapDifference.ValueDifference;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.macys.mirakl.exception.MiraklRepositoryException;
import com.macys.mirakl.model.AttributeData;
import com.macys.mirakl.model.FacetMasterData;
import com.macys.mirakl.model.FacetUpdateRespData;
import com.macys.mirakl.model.PdfMasterData;
import com.macys.mirakl.model.PdfRespData;
import com.macys.mirakl.model.ProductUpdateRespData;
import com.macys.mirakl.model.StellaMasterData;
import com.macys.mirakl.model.StellaRespData;
import com.macys.mirakl.processor.FileProcessor;

@Transactional
@Service
public class ProductUpdateRespService {
	private static final Logger LOGGER = LoggerFactory.getLogger(ProductUpdateRespService.class);

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
	 * @throws IllegalAccessException
	 */
	public void processPrdUpdateRespFile(String payload, String subscription) {
		JSONObject jsonObjectInput = new JSONObject(payload);
		String bucketName = String.valueOf(jsonObjectInput.get(BUCKET));
		String objectName = String.valueOf(jsonObjectInput.get(OBJECT_NAME));
		LOGGER.info("bucket={} objectName={}", bucketName, objectName);

		try {
			String fileName = fileProcessor.getFileName(objectName);
			MDC.put(INPUT_FILE_NAME,fileName);
			String fileOperation = fileProcessor.getFileOperationForRespFile(objectName);
			LOGGER.info("fileName={} fileOperation={}", fileName, fileOperation);
			boolean isValidFileOperation = false;
			
            if (WRONG_FILE.equals(fileOperation)) {
                LOGGER.error("File name or file path is not proper for product/facet update response file :" + objectName);
                return;
            } else {
            	// Check for multiple notification
            	isValidFileOperation = true;
    			boolean processFile = notificationService.processNotification(payload, subscription, isValidFileOperation);
    			if (!processFile) {
    				LOGGER.info("Exiting from processPrdUpdateRespFile");
    				return;
    			}
    			
    			LOGGER.info("Proceed with file processing after validating notification");
    			if (PRODUCT_UPDATE_RESP.equals(fileOperation)) {
    				processProductUpdResp(bucketName, objectName, fileName);			
                } else if (FACET_UPDATE_RESP.equals(fileOperation)) {
                	processFacetResp(bucketName, objectName, fileName);
                }
            }
		} catch (IOException ioe) {
			LOGGER.error("IOException occurred while converting to json: ", ioe);
		} catch (IllegalArgumentException iae) {
			LOGGER.error("IllegalArgumentException occurred while processing Product Update response json: ", iae);
		} catch (JSONException je) {
			LOGGER.error("JSONException occurred while processing Product Update response json: ", je);
		} catch (Exception e) {
			LOGGER.error("Exception in processPrdUpdateRespFile():", e);
		}
	}

	private void processFacetResp(String bucketName, String objectName, String fileName)
			throws IOException, IllegalAccessException, MiraklRepositoryException {
		LOGGER.info("Inside Facet Update response processing");
		List<FacetUpdateRespData> facetUpdateRespList = cloudStorageService.readFacetUpdateRespFile(bucketName,
				objectName);
		if (!facetUpdateRespList.isEmpty() && facetUpdateRespList.size() > 0) {
			processFacetUpdateResp(facetUpdateRespList, fileName);
		} else {
			LOGGER.error("No data in Facet Update response JSON file");
		}
	}

	private void processProductUpdResp(String bucketName, String objectName, String fileName)
			throws IOException, IllegalAccessException, MiraklRepositoryException {
		LOGGER.info("Inside Product Update response processing");
		List<ProductUpdateRespData> prdUpdateRespList = cloudStorageService.readPrdUpdateRespFile(bucketName,
				objectName);
		if (!prdUpdateRespList.isEmpty() && prdUpdateRespList.size() > 0) {
			LOGGER.info("Data available in incoming Product Update response JSON file");
			if (fileName.contains(PDF_FILE_NAME)) {
				processPdfUpdateResp(prdUpdateRespList, fileName);
			} else {
				processStellaUpdateResp(prdUpdateRespList, fileName);
			}
		} else {
			LOGGER.error("No data in Product Update response JSON file");
		}
	}

	private void processPdfUpdateResp(List<ProductUpdateRespData> prdUpdateRespList, String fileName)
			throws IllegalAccessException, MiraklRepositoryException {
		LOGGER.info("Start processing of PDF Update response");
		List<PdfRespData> pdfUpdateDataList = new ArrayList<PdfRespData>();
		List<PdfRespData> pdfSuccessDataList = new ArrayList<PdfRespData>();
		for (ProductUpdateRespData productData : prdUpdateRespList) {
			LOGGER.info("PDF ProductData:" + productData);
			String status = "";
			PdfMasterData pdfTemp = new PdfMasterData();
			try {
				// Fetch corresponding data from MP_PDF_TEMP table
				pdfTemp = sqlService.findPdfJsonFromTemp(productData.getUpcId(), productData.getOpDiv(),
						productData.getFileNameJson());
			} catch (Exception e) {
				LOGGER.error("Exception in findPdfJsonFromTemp:",e);
				continue;
			}

			// Check if response message is success or error with Message and Status
			if (SUCCESS_MESSAGE.equalsIgnoreCase(productData.getMessage())
					&& STATUS_SUCCESS_CODE.equalsIgnoreCase(productData.getStatus())) {
				status = UPDATE_SUCCESS;
				PdfRespData pdfSuccessData = new PdfRespData(productData.getFileNameJson(), productData.getUpcId(),
						productData.getOpDiv(), pdfTemp.getProductType(), pdfTemp.getPdfData(), status,
						productData.getMessage());
				pdfSuccessDataList.add(pdfSuccessData);
			} else if (!(SUCCESS_MESSAGE.equalsIgnoreCase(productData.getMessage()))) {
				LOGGER.info("Error response received from PDF system for UPC_ID:" + productData.getUpcId());
				status = UPDATE_FAILED;
			}
			PdfRespData pdfRespData = new PdfRespData(productData.getFileNameJson(), productData.getUpcId(),
					productData.getOpDiv(), pdfTemp.getProductType(), pdfTemp.getPdfData(), status,
					productData.getMessage());
			pdfUpdateDataList.add(pdfRespData);
		}

		// Batch update success records - update master table json data with temp json
		// data
		if (!pdfSuccessDataList.isEmpty() && pdfSuccessDataList.size() > 0) {
			sqlService.batchUpdatePdfJsonInMaster(pdfSuccessDataList);
		}
		
		if(!pdfUpdateDataList.isEmpty() && pdfUpdateDataList.size() > 0) {
			// Move SUCCESS/ERROR record from temp to audit table
			LOGGER.info("pdfUpdateDataList.size:"+pdfUpdateDataList.size());
			LOGGER.info("pdfUpdateDataList:"+pdfUpdateDataList);
			sqlService.batchInsertPdfRespAuditData(PRODUCT_UPDATE, pdfUpdateDataList);

			// Delete record from MP_PDF_TEMP table after moving SUCCESS/ERROR record from
			// temp to audit
			sqlService.batchDeletePdfDataInTemp(pdfUpdateDataList);
		}
		
		LOGGER.info("End processing of PDF Update response");
	}

	private void processStellaUpdateResp(List<ProductUpdateRespData> prdUpdateRespList, String fileName)
			throws IllegalAccessException, MiraklRepositoryException {
		LOGGER.info("Start processing of Stella Update response");
		List<StellaRespData> stellaUpdateDataList = new ArrayList<StellaRespData>();
		List<StellaRespData> stellaSuccessDataList = new ArrayList<StellaRespData>();
		for (ProductUpdateRespData productData : prdUpdateRespList) {
			LOGGER.info("Stella ProductData:" + productData);
			String status = "";
			StellaMasterData stellaTemp = new StellaMasterData();
			try {
				// Fetch corresponding data from MP_STELLA_TEMP table
				stellaTemp = sqlService.findStellaJsonFromTemp(productData.getUpcId(),
						productData.getOpDiv(), productData.getFileNameJson());
			} catch (Exception e) {
				LOGGER.error("Exception in findStellaJsonFromTemp:",e);
				continue;
			}

			// Check if response message is success or error with Message and Status
			if (SUCCESS_MESSAGE.equalsIgnoreCase(productData.getMessage())
					&& STATUS_SUCCESS_CODE.equalsIgnoreCase(productData.getStatus())) {
				status = UPDATE_SUCCESS;
				StellaRespData stellaSuccessData = new StellaRespData(productData.getFileNameJson(),
						productData.getUpcId(), productData.getOpDiv(), stellaTemp.getProductType(),
						stellaTemp.getStellaData(), status, productData.getMessage());
				stellaSuccessDataList.add(stellaSuccessData);
			} else if (!(SUCCESS_MESSAGE.equalsIgnoreCase(productData.getMessage()))) {
				LOGGER.info("Error response received from STELLA system for UPC_ID:" + productData.getUpcId());
				status = UPDATE_FAILED;
			}
			
			StellaRespData stellaRespData = new StellaRespData(productData.getFileNameJson(), productData.getUpcId(),
					productData.getOpDiv(), stellaTemp.getProductType(), stellaTemp.getStellaData(), status,
					productData.getMessage());
			stellaUpdateDataList.add(stellaRespData);
		}

		// Batch update success records - update master table json data with temp json
		// data
		if (!stellaSuccessDataList.isEmpty() && stellaSuccessDataList.size() > 0) {
			sqlService.batchUpdateStellaJsonInMaster(stellaSuccessDataList);
		}
		
		if (!stellaUpdateDataList.isEmpty() && stellaUpdateDataList.size() > 0) {
			// Move SUCCESS/ERROR record from temp to audit table
			sqlService.batchInsertStellaRespAuditData(PRODUCT_UPDATE, stellaUpdateDataList);

			// Delete record from MP_PDF_TEMP table after moving SUCCESS/ERROR record from
			// temp to audit
			sqlService.batchDeleteStellaDataInTemp(stellaUpdateDataList);
		}

		LOGGER.info("End processing of Stella Update response");
	}

	private void processFacetUpdateResp(List<FacetUpdateRespData> facetRespList, String fileName)
			throws IllegalAccessException, MiraklRepositoryException {
		LOGGER.info("Start processing of Facet Update response");
		List<FacetUpdateRespData> facetUpdateRespAuditList = new ArrayList<FacetUpdateRespData>();
		List<FacetUpdateRespData> facetUpdateSuccessList = new ArrayList<FacetUpdateRespData>();
		String status = "";
		
		for (FacetUpdateRespData facetRespData : facetRespList) {
			LOGGER.info("Facet FacetUpdateResponse:" + facetRespData);
			String facetTempJson = "";
			try {
				// Fetch corresponding data from MP_FACET_TEMP table
				facetTempJson = sqlService.findFacetJsonFromTemp(facetRespData.getUpcId(), facetRespData.getOpDiv(),
						facetRespData.getFileNameJson());
			} catch (Exception e) {
				LOGGER.error("Exception in findFacetJsonFromTemp:",e);
				continue;
			}

			// Check if response message is success or error with Message and Status
			if (SUCCESS_MESSAGE.equalsIgnoreCase(facetRespData.getMessage())
					&& STATUS_SUCCESS_CODE.equalsIgnoreCase(facetRespData.getStatus())) {
				status = UPDATE_SUCCESS;

				// Fetch corresponding data from MP_FACET_MASTER table
				FacetMasterData facetMasterData = new FacetMasterData();
				try {
					// Fetch corresponding data from MP_FACET_TEMP table
					facetMasterData = sqlService.findFacetMasterJSONByUpc(facetRespData.getUpcId(),
							facetRespData.getOpDiv());
				} catch (Exception e) {
					LOGGER.error("Exception in findFacetMasterJSONByUpc:",e);
					continue;
				}
				JSONArray facetMasterJSONArray = replaceMasterData(facetRespData, facetMasterData);

				FacetUpdateRespData facetSuccessData = new FacetUpdateRespData(facetRespData.getUpcId(),
						facetRespData.getOpDiv(), facetRespData.getProductType(), facetRespData.getDept(),
						facetRespData.getVendor(), facetRespData.getPid(), facetRespData.getNrfColorCode(), status,
						facetRespData.getMessage(), facetRespData.getAttributes(), facetRespData.getFileNameJson(),
						facetMasterJSONArray.toString());
				facetUpdateSuccessList.add(facetSuccessData);

			} else if (!(SUCCESS_MESSAGE.equalsIgnoreCase(facetRespData.getMessage()))) {
				LOGGER.info("Error response received from FACET system for UPC_ID:" + facetRespData.getUpcId());
				status = UPDATE_FAILED;
			}

			FacetUpdateRespData facetRespAuditData = new FacetUpdateRespData(facetRespData.getUpcId(),
					facetRespData.getOpDiv(), facetRespData.getProductType(), facetRespData.getDept(),
					facetRespData.getVendor(), facetRespData.getPid(), facetRespData.getNrfColorCode(), status,
					facetRespData.getMessage(), "", facetRespData.getFileNameJson(), facetTempJson);
			facetUpdateRespAuditList.add(facetRespAuditData);
		}

		// Batch update success records - update master table json data with temp json
		// data
		if (!facetUpdateSuccessList.isEmpty() && facetUpdateSuccessList.size() > 0) {
			sqlService.batchUpdateFacetJsonInMaster(facetUpdateSuccessList);
		}
		
		if (!facetUpdateRespAuditList.isEmpty() && facetUpdateRespAuditList.size() > 0) {
			// Move SUCCESS/ERROR record from temp to audit table
			sqlService.batchInsertFacetRespAuditData(PRODUCT_UPDATE, facetUpdateRespAuditList);

			// Delete record from MP_PDF_TEMP table after moving SUCCESS/ERROR record from
			// temp to audit
			sqlService.batchDeleteFacetDataInTemp(facetUpdateRespAuditList);
		}

		LOGGER.info("End processing of Facet Update response");
	}

	private JSONArray replaceMasterData(FacetUpdateRespData facetRespData, FacetMasterData facetMasterData) {
		JSONArray jsonArrayFacetAttributesMaster;
		JSONArray jsonArrayFacetAttributesResp;
		jsonArrayFacetAttributesMaster = new JSONArray(facetMasterData.getFacetData());
		jsonArrayFacetAttributesResp = new JSONArray(facetRespData.getAttributes());

		Map<String, AttributeData> facetDataRespMap = getAttributes(jsonArrayFacetAttributesResp);
		Map<String, AttributeData> facetDataMasterMap = getAttributes(jsonArrayFacetAttributesMaster);
		MapDifference<String, AttributeData> diffMap = Maps.difference(facetDataMasterMap, facetDataRespMap);
		Map<String, ValueDifference<AttributeData>> entriesDifferingMap = null;
		Map<String, AttributeData> entriesDifferingOnlyRightMap = null;
		Map<String, AttributeData> changedPropsMap = new HashMap<>();
		
		if (null != diffMap) {
			entriesDifferingMap = diffMap.entriesDiffering();
			entriesDifferingOnlyRightMap = diffMap.entriesOnlyOnRight();
		}

		// For attributes existing in both master data and response data
		if (null != entriesDifferingMap && !entriesDifferingMap.isEmpty()) {
			entriesDifferingMap.entrySet().forEach(entry ->
					changedPropsMap.put(entry.getKey(), entry.getValue().rightValue()));
		}

		// For attributes existing only in response data
		if (null != entriesDifferingOnlyRightMap && !entriesDifferingOnlyRightMap.isEmpty()) {
			entriesDifferingOnlyRightMap.entrySet().forEach(entry ->
					changedPropsMap.put(entry.getKey(), entry.getValue()));
		}		
		
		for (Map.Entry<String,AttributeData> entry : changedPropsMap.entrySet()) {		        
			LOGGER.info("changedPropsMap Key = " + entry.getKey() + ", Value = " + entry.getValue());
		}
		facetDataMasterMap.putAll(changedPropsMap);
		
		Gson gson = new GsonBuilder().disableHtmlEscaping().create();
		JSONArray facetMasterJSONArray = new JSONArray();
		LOGGER.info("facetDataMasterMap Data::");
		 
		for (Map.Entry<String,AttributeData> entry : facetDataMasterMap.entrySet()) {		        
			LOGGER.info("Key = " + entry.getKey() + ", Value = " + entry.getValue());
			AttributeData attributeData = entry.getValue();
			String json = gson.toJson(attributeData);
			facetMasterJSONArray.put(new JSONObject(json));
		}
		
		LOGGER.info("facetMasterJSONArray:" + facetMasterJSONArray);
		return facetMasterJSONArray;
	}
}
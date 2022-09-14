package com.macys.mirakl.service;

import static com.macys.mirakl.util.OrchConstants.ATTRIBUTES;
import static com.macys.mirakl.util.OrchConstants.ATTRIBUTE_NAME;
import static com.macys.mirakl.util.OrchConstants.ATTRIBUTE_VALUE;
import static com.macys.mirakl.util.OrchConstants.CREATE_RESP_OP_DIV;
import static com.macys.mirakl.util.OrchConstants.DELTA;
import static com.macys.mirakl.util.OrchConstants.DEPT_JSON;
import static com.macys.mirakl.util.OrchConstants.ERROR_UPCS;
import static com.macys.mirakl.util.OrchConstants.FACET;
import static com.macys.mirakl.util.OrchConstants.IMAGES;
import static com.macys.mirakl.util.OrchConstants.IMAGE_DATA;
import static com.macys.mirakl.util.OrchConstants.INPROCESS;
import static com.macys.mirakl.util.OrchConstants.IS_MAIN_IMG_P0;
import static com.macys.mirakl.util.OrchConstants.MAIN_IMAGE;
import static com.macys.mirakl.util.OrchConstants.MIRAKL;
import static com.macys.mirakl.util.OrchConstants.NON_PRIMARY_IMAGE;
import static com.macys.mirakl.util.OrchConstants.NO_DATA_FOUND;
import static com.macys.mirakl.util.OrchConstants.NO_UPDATES;
import static com.macys.mirakl.util.OrchConstants.NRF_COLOR_CODE_JSON;
import static com.macys.mirakl.util.OrchConstants.OP_DIV;
import static com.macys.mirakl.util.OrchConstants.PDF;
import static com.macys.mirakl.util.OrchConstants.PID_JSON;
import static com.macys.mirakl.util.OrchConstants.PRIMARY_IMAGE;
import static com.macys.mirakl.util.OrchConstants.PRODUCT_TYPE;
import static com.macys.mirakl.util.OrchConstants.PRODUCT_UPDATE;
import static com.macys.mirakl.util.OrchConstants.RESP_MESSAGE;
import static com.macys.mirakl.util.OrchConstants.RESP_STATUS;
import static com.macys.mirakl.util.OrchConstants.STELLA;
import static com.macys.mirakl.util.OrchConstants.UPCID;
import static com.macys.mirakl.util.OrchConstants.UPCID_IMAGES;
import static com.macys.mirakl.util.OrchConstants.VENDOR_JSON;
import static com.macys.mirakl.util.OrchUtil.findDifferenceImagesList;
import static com.macys.mirakl.util.OrchUtil.findJsonDifference;
import static com.macys.mirakl.util.OrchUtil.findJsonDifferenceFacet;
import static com.macys.mirakl.util.OrchUtil.findJsonDifferenceImages;
import static com.macys.mirakl.util.OrchUtil.validateProductType;
import static com.macys.mirakl.util.OrchUtil.validateVendorNumber;

import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.Gson;
import com.macys.mirakl.exception.MiraklRepositoryException;
import com.macys.mirakl.model.AttributeData;
import com.macys.mirakl.model.FacetMasterData;
import com.macys.mirakl.model.ImageData;
import com.macys.mirakl.model.ImageMasterData;
import com.macys.mirakl.model.ImageUpdateRespData;
import com.macys.mirakl.model.MiraklData;
import com.macys.mirakl.model.PdfMasterData;
import com.macys.mirakl.model.ProductDataPDF;
import com.macys.mirakl.model.ProductDataStella;
import com.macys.mirakl.model.StellaMasterData;
import com.macys.mirakl.processor.FileProcessor;

@Transactional
@Service
public class ProductUpdateService {
	private static final Logger LOGGER = LoggerFactory.getLogger(ProductUpdateService.class);

	@Autowired
	private CloudStorageService cloudStorageService;

	@Autowired
	private SQLService sql;

	@Autowired
	private FileProcessor fileProcessor;

	@Value("${com.macys.mirakl.orch.bucket.delta}")
	private String deltaBucket;

	@Value("${com.macys.mirakl.orch.bucket.images.delta}")
	private String imageDeltaBucket;

	public void processProductUpdate(String fileName, List<MiraklData> prdTxns) throws IllegalAccessException, MiraklRepositoryException {
		if (!prdTxns.isEmpty() && prdTxns.size() > 0) {
			List<PdfMasterData> pdfDataList = new ArrayList<>();
			List<StellaMasterData> stellaDataList = new ArrayList<>();
			List<ImageMasterData> imageDataList = new ArrayList<>();
			List<FacetMasterData> facetDataList = new ArrayList<>();
			for (MiraklData miraklData : prdTxns) {

				PdfMasterData pdfInfo = new PdfMasterData(miraklData.getFileName(), miraklData.getUpcId(),
						miraklData.getOpDiv(), miraklData.getProductType(), miraklData.getPdfData());
				pdfDataList.add(pdfInfo);
				StellaMasterData stellaInfo = new StellaMasterData(miraklData.getFileName(), miraklData.getUpcId(),
						miraklData.getOpDiv(), miraklData.getProductType(), miraklData.getStellaData());
				stellaDataList.add(stellaInfo);
				ImageMasterData imageInfo = new ImageMasterData(miraklData.getFileName(), miraklData.getUpcId(),
						miraklData.getOpDiv(), miraklData.getProductType(), miraklData.getImagesData());
				imageDataList.add(imageInfo);
				FacetMasterData facetInfo = new FacetMasterData(miraklData.getFileName(), miraklData.getUpcId(),
						miraklData.getOpDiv(), miraklData.getProductType(), miraklData.getDept(),
						miraklData.getVendor(), miraklData.getPid(), miraklData.getNrfColorCode(),
						miraklData.getFacetData());
				facetDataList.add(facetInfo);
			}
			String operation = PRODUCT_UPDATE;
			sql.batchInsertPdfTempData(pdfDataList, operation);
			sql.batchInsertStellaTempData(stellaDataList, operation);
			sql.batchInsertFacetTempData(facetDataList, operation);
			LOGGER.info("Success Write in TEMP tables");

			processAgainstMasterData(fileName, prdTxns);

		} else {
			LOGGER.error("No data in Product Update JSON file");
		}
	}

	/**
	 *
	 * @param fileName
	 * @param prdTxns
	 * @throws IllegalAccessException
	 * @throws MiraklRepositoryException 
	 */
	private void processAgainstMasterData(String fileName, List<MiraklData> prdTxns) throws IllegalAccessException, MiraklRepositoryException {
		Gson gson = new Gson();
		JSONArray jsonArrayPDF = new JSONArray();
		JSONArray jsonArrayStella = new JSONArray();
		JSONArray jsonArrayImages = new JSONArray();
		JSONArray jsonArrayFacet = new JSONArray();
		
		JSONArray jsonArrayPDFError = new JSONArray();
		JSONArray jsonArrayStellaError = new JSONArray();
		JSONArray jsonArrayImagesError = new JSONArray();
		JSONArray jsonArrayFacetError = new JSONArray();
		
		List<PdfMasterData> pdfNoDeltaList = new ArrayList<>();
		List<StellaMasterData> stellaNoDeltaList = new ArrayList<>();
		List<ImageMasterData> imageDataList = new ArrayList<>();
		List<ImageMasterData> imageNoDeltaList = new ArrayList<>();
		List<FacetMasterData> facetNoDeltaList = new ArrayList<>();
		
		for (MiraklData miraklData : prdTxns) {
			JSONObject prdPDFDelta = identifyDeltaFromIncomingPDF(gson, miraklData, pdfNoDeltaList);
			if (null != prdPDFDelta) {
				if (prdPDFDelta.has(RESP_STATUS)
						&& String.valueOf(prdPDFDelta.get(RESP_STATUS)).equalsIgnoreCase("400")) {
					jsonArrayPDFError.put(prdPDFDelta);
				} else {
					jsonArrayPDF.put(prdPDFDelta);
				}
			}
			
			JSONObject prdStellaDelta = identifyDeltaFromIncomingStella(gson, miraklData, stellaNoDeltaList);
			if (null != prdStellaDelta) {
				if (prdStellaDelta.has(RESP_STATUS)
						&& String.valueOf(prdStellaDelta.get(RESP_STATUS)).equalsIgnoreCase("400")) {
					jsonArrayStellaError.put(prdStellaDelta);
				} else {
					jsonArrayStella.put(prdStellaDelta);
				}
			}
			
			JSONObject prdImagesDelta = identifyDeltaFromIncomingImages(miraklData, imageNoDeltaList);
			if (null != prdImagesDelta) {
				jsonArrayImages.put(prdImagesDelta);
			}
			ImageMasterData imageInfo = new ImageMasterData(miraklData.getFileName(), miraklData.getUpcId(),
					miraklData.getOpDiv(), miraklData.getProductType(), miraklData.getImagesData());
			imageDataList.add(imageInfo);
			
			JSONObject prdFacetDelta = identifyDeltaFromIncomingFacetAttributes(miraklData, facetNoDeltaList);
			if (null != prdFacetDelta) {
				if (prdFacetDelta.has(RESP_STATUS)
						&& String.valueOf(prdFacetDelta.get(RESP_STATUS)).equalsIgnoreCase("400")) {
					jsonArrayFacetError.put(prdFacetDelta);
				} else {
					jsonArrayFacet.put(prdFacetDelta);
				}
			}
		}
		LOGGER.info("pdfNoDeltaList:"+pdfNoDeltaList+" pdfNoDeltaList.size():"+pdfNoDeltaList.size());
		if(0!=pdfNoDeltaList.size()) {
			sql.batchInsertPdfAuditData(PRODUCT_UPDATE,NO_UPDATES,null,pdfNoDeltaList);
			sql.batchDeletePdfDataInTempNoDelta(pdfNoDeltaList);
		}
		LOGGER.info("stellaNoDeltaList:"+stellaNoDeltaList+" stellaNoDeltaList.size():"+stellaNoDeltaList.size());
		if(0!=stellaNoDeltaList.size()) {
			sql.batchInsertStellaAuditData(PRODUCT_UPDATE,NO_UPDATES,null,stellaNoDeltaList);
			sql.batchDeleteStellaDataInTempNoDelta(stellaNoDeltaList);
		}
		LOGGER.info("imageNoDeltaList:"+imageNoDeltaList+" imageNoDeltaList.size():"+imageNoDeltaList.size());
		if(0!=imageNoDeltaList.size()) {
			sql.batchInsertImageAuditData(PRODUCT_UPDATE,NO_UPDATES,null,imageNoDeltaList);
		}
		LOGGER.info("facetNoDeltaList:"+facetNoDeltaList+" facetNoDeltaList.size():"+facetNoDeltaList.size());
		if(0!=facetNoDeltaList.size()) {
			sql.batchInsertFacetAuditData(PRODUCT_UPDATE,NO_UPDATES,null,facetNoDeltaList);
			sql.batchDeleteFacetDataInTempNoDelta(facetNoDeltaList);
		}

		if ((jsonArrayPDF.length() > 0 && !jsonArrayPDF.isNull(0))
				|| (jsonArrayPDFError.length() > 0 && !jsonArrayPDFError.isNull(0))) {
			writeToDeltaBucket(fileName, PDF, jsonArrayPDF, jsonArrayPDFError);
		}
		if ((jsonArrayStella.length() > 0 && !jsonArrayStella.isNull(0))
				|| (jsonArrayStellaError.length() > 0 && !jsonArrayStellaError.isNull(0))) {
			writeToDeltaBucket(fileName, STELLA, jsonArrayStella, jsonArrayStellaError);
		}
		if ((jsonArrayFacet.length() > 0 && !jsonArrayFacet.isNull(0))
				|| (jsonArrayFacetError.length() > 0 && !jsonArrayFacetError.isNull(0))) {
			writeToDeltaBucket(fileName, FACET, jsonArrayFacet, jsonArrayFacetError);
		}
		
		if (jsonArrayImages.length() > 0 && !jsonArrayImages.isNull(0)) {
			writeToDeltaBucket(fileName, IMAGES, jsonArrayImages, jsonArrayImagesError);
			
			List<ImageMasterData> imageDataMasterList = new ArrayList<>();
			for (int i = 0; i < jsonArrayImages.length(); i++) {
				JSONObject imagesObject = jsonArrayImages.getJSONObject(i);
				LOGGER.info(imagesObject.get(UPCID_IMAGES).toString());
				for (ImageMasterData imageData : imageDataList) {
					if ((imagesObject.get(UPCID_IMAGES).toString()).equalsIgnoreCase(imageData.getUpcId())) {
						imageDataMasterList.add(imageData);
					}
				}
			}
			// Batch insert audit data for Images data from temp table
			sql.batchInsertImageAuditData(PRODUCT_UPDATE, INPROCESS, null, imageDataMasterList);
		}		

	}

	/**
	 * 
	 * @param gson
	 * @param product
	 * @param jsonArrayStella
	 * @return
	 * @throws IllegalAccessException
	 */
	public JSONObject identifyDeltaFromIncomingStella(Gson gson, MiraklData product, List<StellaMasterData> stellaNoDeltaList) throws IllegalAccessException {

		String stellaMasterJson = null, productType = null;
		JSONObject jsonStellaDelta = null;
		StellaMasterData stellaMasterData = null;
		try {
			stellaMasterData = sql.findStellaMasterJSONByUpc(product.getUpcId(), product.getOpDiv());
		} catch (Exception e) {
			LOGGER.error("Error in fetching master data from Stella :", e);
			sql.insertAppAuditData(PRODUCT_UPDATE, NO_DATA_FOUND, null, product, STELLA);
			sql.deleteAppDataInTemp(product, STELLA);
		}
		if (null != stellaMasterData) {
			Map<Object, Object> changedPropsStella = null;
			String errorDesc = null;
			stellaMasterJson = stellaMasterData.getStellaData();
			productType = stellaMasterData.getProductType();
			LOGGER.info("Stella Master JSON : {}, productType : {}, incoming productType : {}", stellaMasterJson,
					productType, product.getProductType());
			if (null != stellaMasterJson && validateProductType(product.getProductType(), productType)) {
				ProductDataStella stellaDataMaster = gson.fromJson(stellaMasterJson, ProductDataStella.class);
				ProductDataStella stellaDataIncoming = gson.fromJson(product.getStellaData(), ProductDataStella.class);
				// Compare stellaDataIncoming and stellaDataMaster and find the delta difference
				changedPropsStella = findJsonDifference(stellaDataMaster, stellaDataIncoming);
				if (changedPropsStella.isEmpty()) {
					LOGGER.info("No delta changes identified for Stella for UPC : " + product.getUpcId());
					stellaMasterData.setFileName(product.getFileName());
					stellaMasterData.setUpcId(product.getUpcId());
					stellaMasterData.setOpDiv(product.getOpDiv());
					stellaNoDeltaList.add(stellaMasterData);
					return jsonStellaDelta;
				} else {
					jsonStellaDelta = populateDeltaJSON(product, changedPropsStella, STELLA, errorDesc);
				}

				LOGGER.info("Identified Stella Delta for UPC :" + product.getUpcId() + ", OP_DIV:" + product.getOpDiv()
						+ ", PRODUCT_TYPE:" + product.getProductType());
			} else {
				LOGGER.error("Stella Master record product Type differs for UPC :" + product.getUpcId() + ", OP_DIV:"
						+ product.getOpDiv() + ", Incoming PRODUCT_TYPE:" + product.getProductType()
						+ ", Master PRODUCT_TYPE:" + productType);
				
				errorDesc = "Unable to update attributes: Either Product Type was changed or Product Type selected was invalid";
				if(StringUtils.isBlank(product.getProductType())) {
					product.setProductType("");
				}
				jsonStellaDelta = populateDeltaJSON(product, changedPropsStella, STELLA, errorDesc);
			}
		} else {
			LOGGER.error("Stella Master record Doesn't exist for UPC :" + product.getUpcId() + ", OP_DIV:"
					+ product.getOpDiv() + ", PRODUCT_TYPE:" + product.getProductType());
			LOGGER.error("THIS RECORD NEED TO BE PLACED in STELLA ERROR JSON");
		}

		return jsonStellaDelta;
	}

	/**
	 * 
	 * @param gson
	 * @param product
	 * @param jsonArrayPDF
	 * @return
	 * @throws IllegalAccessException
	 */
	public JSONObject identifyDeltaFromIncomingPDF(Gson gson, MiraklData product, List<PdfMasterData> pdfNoDeltaList)
			throws IllegalAccessException {

		String pdfMasterJson = null, productType = null;
		JSONObject jsonPDFDelta = null;
		PdfMasterData pdfMasterData = null;
		try {
			pdfMasterData = sql.findPdfMasterJSONByUpc(product.getUpcId(), product.getOpDiv());
		} catch (Exception e) {
			LOGGER.error("Error in fetching master data from PDF :", e);
			sql.insertAppAuditData(PRODUCT_UPDATE, NO_DATA_FOUND, null, product, PDF);
			sql.deleteAppDataInTemp(product, PDF);
		}
		if (null != pdfMasterData) {
			Map<Object, Object> changedPropsPDF = null;
			String errorDesc = null;
			pdfMasterJson = pdfMasterData.getPdfData();
			productType = pdfMasterData.getProductType();
			LOGGER.info("PDF Master JSON : {}, productType : {}, incoming productType : {}", pdfMasterJson,
					productType, product.getProductType());
			if (null != pdfMasterJson && validateProductType(product.getProductType(), productType)) {
				ProductDataPDF pdfDataMaster = gson.fromJson(pdfMasterJson, ProductDataPDF.class);
				ProductDataPDF pdfDataIncoming = gson.fromJson(product.getPdfData(), ProductDataPDF.class);

				// Compare pdfDataIncoming and pdfDataMaster and find the delta difference
				changedPropsPDF = findJsonDifference(pdfDataMaster, pdfDataIncoming);
				if (changedPropsPDF.isEmpty()) {
					LOGGER.info("No delta changes identified for PDF for UPC : " + product.getUpcId());
					pdfMasterData.setFileName(product.getFileName());
					pdfMasterData.setUpcId(product.getUpcId());
					pdfMasterData.setOpDiv(product.getOpDiv());;
					pdfNoDeltaList.add(pdfMasterData);
					return jsonPDFDelta;
				} else {
					jsonPDFDelta = populateDeltaJSON(product, changedPropsPDF, PDF, errorDesc);
				}

				LOGGER.info("Identified PDF Delta for UPC :" + product.getUpcId() + ", OP_DIV:" + product.getOpDiv()
						+ ", PRODUCT_TYPE:" + product.getProductType());
			} else {
				LOGGER.error("PDF Master record product type differs for UPC :" + product.getUpcId() + ", OP_DIV:"
						+ product.getOpDiv() + ", Incoming PRODUCT_TYPE:" + product.getProductType()
						+ ", Master PRODUCT_TYPE:" + productType);
				
				errorDesc = "Unable to update attributes: Either Product Type was changed or Product Type selected was invalid";
				if(StringUtils.isBlank(product.getProductType())) {
					product.setProductType("");
				}
				jsonPDFDelta = populateDeltaJSON(product, changedPropsPDF, PDF, errorDesc);
			}
		} else {
			LOGGER.error("PDF Master record Doesn't exist for UPC :" + product.getUpcId() + ", OP_DIV:"
					+ product.getOpDiv() + ", PRODUCT_TYPE:" + product.getProductType());
			LOGGER.error("THIS RECORD NEED TO BE PLACED in PDF ERROR JSON");
		}

		return jsonPDFDelta;
	}

	/**
	 *
	 * @param product
	 * @param jsonArrayImages
	 * @return
	 * @throws IllegalAccessException
	 */
	public JSONObject identifyDeltaFromIncomingImages(MiraklData product, List<ImageMasterData> imageNoDeltaList)
			throws IllegalAccessException {

		String imagesMasterJson = null, productType = null;
		JSONObject jsonImagesDelta = null;
		ImageMasterData imageMasterData = null;
		try {
			imageMasterData = sql.findImageMasterJSONByUpc(product.getUpcId(), product.getOpDiv());
		} catch (Exception e) {
			LOGGER.error("Error in fetching master data from IMAGES :", e);
			sql.insertAppAuditData(PRODUCT_UPDATE, NO_DATA_FOUND, null, product, IMAGES);
		}
		if (null != imageMasterData) {
			String productTypeError = null;
			imagesMasterJson = imageMasterData.getImageData();
			productType = imageMasterData.getProductType();
			LOGGER.info("Images Master JSON : {}, productType : {}, incoming productType : {}", imagesMasterJson,
					productType, product.getProductType());
			if (null != imagesMasterJson && validateProductType(product.getProductType(), productType)) {
				JSONArray jsonArrayImagesMaster = new JSONArray(imagesMasterJson);
				JSONArray jsonArrayImagesIncoming = new JSONArray(product.getImagesData());
				
				List<ImageData> imgDataList = new ArrayList<>();
				List<ImageUpdateRespData> imageDeltaList = new ArrayList<>();
				boolean isMainImgP0 = false;
				// Compare jsonArrayImagesMaster and jsonArrayImagesIncoming and find the delta
				// difference
				imgDataList = findDifferenceImagesList(jsonArrayImagesMaster, jsonArrayImagesIncoming);
				// Rename swatch to swImage in delta list
				Map<Object, Object> changedPropsImages = findJsonDifferenceImages(imgDataList);
					
				if (changedPropsImages.containsKey(MAIN_IMAGE)) {
					// Fetch isMainImgP0 flag value from product confirmation message/OL and
					// populate in delta file, only if mainImage attribute is changed
					
					try {
						isMainImgP0 = sql.getIsMainImgP0ByUpc(product.getUpcId(), product.getOpDiv());
						changedPropsImages.put(IS_MAIN_IMG_P0, isMainImgP0);
					}
					catch (Exception e) {
						LOGGER.error("Error in fetching main image flag from MP_UPC_MASTER table :", e);
						sql.insertAppAuditData(PRODUCT_UPDATE, NO_DATA_FOUND, null, product, IMAGES);
						return jsonImagesDelta;
					}
				}
				if (!imgDataList.isEmpty()) {
					imageDeltaList = createRespData(imgDataList, product, isMainImgP0);

					// Update already existing IN_PROCESS image records for same UPC, OP_DIV, IMAGE_TYPE to ABORTED
					sql.batchUpdateImageRespData(imageDeltaList);

					// Insert details in MP_IMAGES_RESPONSE for delta identified images with status as IN_PROCESS
					sql.batchInsertImageResData(imageDeltaList);
				}

				if (changedPropsImages.isEmpty()) {
					LOGGER.info("No delta changes identified for Images for UPC : " + product.getUpcId());
					imageMasterData.setFileName(product.getFileName());
					imageMasterData.setUpcId(product.getUpcId());
					imageMasterData.setOpDiv(product.getOpDiv());
					imageNoDeltaList.add(imageMasterData);
					return jsonImagesDelta;
				} else {
					jsonImagesDelta = populateDeltaJSON(product, changedPropsImages, IMAGES, productTypeError);
				}

				LOGGER.info("\nIdentified Images Delta for UPC :" + product.getUpcId() + ", OP_DIV:"
						+ product.getOpDiv() + ", PRODUCT_TYPE:" + product.getProductType());
			} else {
				LOGGER.error("Images Master record product type differs for UPC :" + product.getUpcId() + ", OP_DIV:"
						+ product.getOpDiv() + ", Incoming PRODUCT_TYPE:" + product.getProductType()
						+ ", Master PRODUCT_TYPE:" + productType);
			}
		} else {
			LOGGER.error("Images Master record Doesn't exist for UPC :" + product.getUpcId() + ", OP_DIV:"
					+ product.getOpDiv() + ", PRODUCT_TYPE:" + product.getProductType());
			LOGGER.error("THIS RECORD NEED TO BE PLACED in IMAGES_RAY ERROR JSON");
		}

		return jsonImagesDelta;

	}

	/**
	 * Method to identify the delta different of attributes for a UPC level
	 * information. 1. If Existing Record doesn't have facet attributes, we need to
	 * add the incoming attributes 2. If there is a change in Incoming record, we
	 * need to consider only changed attributes 3. If any new attribute is newly
	 * added, we need to consider that new attributes 4. If any attribute value to
	 * be removed from Mirakl, we are expecting to get the attributeName with
	 * attributeValue as empty string
	 *
	 * @param product
	 * @return
	 */
	public JSONObject identifyDeltaFromIncomingFacetAttributes(MiraklData product, List<FacetMasterData> facetNoDeltaList) {
		String facetMaster = null, productType = null, errorDesc = null;
		JSONObject jsonFacetDelta = null;
		String incomingFacetAttributes = product.getFacetData();
		FacetMasterData facetMasterData = null;
		try {
			facetMasterData = sql.findFacetMasterJSONByUpc(product.getUpcId(), product.getOpDiv());
			facetMaster = facetMasterData.getFacetData();
			productType = facetMasterData.getProductType();
		} catch (Exception e) {
			LOGGER.error("Error in fetching master data from FACET_MASTER table :", e);
			sql.insertFacetAuditData(PRODUCT_UPDATE,NO_DATA_FOUND,null,product);
			sql.deleteAppDataInTemp(product, FACET);
		}
		// Identify delta only if there is no ProductType change and vendor number is valid
		boolean isProductTypeValid = validateProductType(product.getProductType(), productType);
		boolean isVendorNbrValid = validateVendorNumber(product.getVendor());
		if (isProductTypeValid && isVendorNbrValid) {
			JSONArray jsonArrayFacetAttributesIncoming = null;
			if (StringUtils.isNotEmpty(incomingFacetAttributes)) {
				jsonArrayFacetAttributesIncoming = new JSONArray(incomingFacetAttributes);
			}

			JSONArray jsonArrayFacetAttributesMaster = null;
			if (StringUtils.isNotEmpty(facetMaster)) {
				jsonArrayFacetAttributesMaster = new JSONArray(facetMaster);
				if (jsonArrayFacetAttributesMaster.length() == 0) {
					LOGGER.info("Facet Master record Doesn't exist for UPC :" + product.getUpcId() + ", OP_DIV:"
							+ product.getOpDiv() + ", PRODUCT_TYPE:" + product.getProductType());
					LOGGER.info("Master data will get replaced with incoming data");
					return createFacetDeltaJSON(product, jsonArrayFacetAttributesIncoming, errorDesc);
				}
			} else {
				LOGGER.info("Facet Master record Doesn't exist for UPC :" + product.getUpcId() + ", OP_DIV:"
						+ product.getOpDiv() + ", PRODUCT_TYPE:" + product.getProductType());
				LOGGER.info("Master data will get replaced with incoming data");
				return createFacetDeltaJSON(product, jsonArrayFacetAttributesIncoming, errorDesc);
			}
			List<AttributeData> changedPropsFacets = findJsonDifferenceFacet(jsonArrayFacetAttributesMaster,
					jsonArrayFacetAttributesIncoming);
			if (changedPropsFacets.isEmpty()) {
				LOGGER.info("No delta changes identified for Facet for UPC : " + product.getUpcId());
				facetMasterData.setFileName(product.getFileName());
				facetMasterData.setUpcId(product.getUpcId());
				facetMasterData.setOpDiv(product.getOpDiv());
				facetMasterData.setDept(product.getDept());
				facetMasterData.setVendor(product.getVendor());
				facetMasterData.setPid(product.getPid());
				facetMasterData.setNrfcolor(product.getNrfColorCode());
				facetNoDeltaList.add(facetMasterData);
				return jsonFacetDelta;
			} else {
				JSONArray changedAttributes = new JSONArray();
				for (AttributeData attributeData : changedPropsFacets) {
					JSONObject attributeKeyValue = new JSONObject();
					try {
						Field changeMap = attributeKeyValue.getClass().getDeclaredField("map");
						changeMap.setAccessible(true);
						;
						changeMap.set(attributeKeyValue, new LinkedHashMap<>());
						changeMap.setAccessible(false);
					} catch (IllegalAccessException | NoSuchFieldException e) {
						LOGGER.error(e.getMessage(), e);
					}
					attributeKeyValue.put(ATTRIBUTE_NAME, attributeData.getAttributeName());
					attributeKeyValue.put(ATTRIBUTE_VALUE, attributeData.getAttributeValue());
					changedAttributes.put(attributeKeyValue);
				}

				jsonFacetDelta = createFacetDeltaJSON(product, changedAttributes, errorDesc);
			}

		} else {
			if(!isProductTypeValid) {
				errorDesc = "Unable to update attributes: Either Product Type was changed or Product Type selected was invalid";
				if(StringUtils.isBlank(product.getProductType())) {
					product.setProductType("");
				}
			} else if (!isVendorNbrValid) {
				errorDesc = "Invalid Vendor Number";
				if(StringUtils.isBlank(product.getVendor())) {
					product.setVendor("");
				}
			}
			
			JSONArray changedAttributes = new JSONArray();
			LOGGER.error("Facet Master record product type differs for UPC :" + product.getUpcId() + ", OP_DIV:"
					+ product.getOpDiv() + ", Incoming PRODUCT_TYPE:" + product.getProductType()
					+ ", Master PRODUCT_TYPE:" + productType);
			jsonFacetDelta = createFacetDeltaJSON(product, changedAttributes, errorDesc);
		
		}
		return jsonFacetDelta;
	}

	private JSONObject createFacetDeltaJSON(MiraklData product, JSONArray changedAttributes, String errorDesc) {
		JSONObject upcFacetDelta = new JSONObject();
		// Ordering the elements in same order as inserted
		try {
			Field changeMap = upcFacetDelta.getClass().getDeclaredField("map");
			changeMap.setAccessible(true);
			;
			changeMap.set(upcFacetDelta, new LinkedHashMap<>());
			changeMap.setAccessible(false);
		} catch (IllegalAccessException | NoSuchFieldException e) {
			LOGGER.error(e.getMessage(), e);
		}
		upcFacetDelta.put(UPCID, product.getUpcId());
		upcFacetDelta.put(PRODUCT_TYPE, product.getProductType());
		upcFacetDelta.put(OP_DIV, product.getOpDiv());
		upcFacetDelta.put(DEPT_JSON, product.getDept());
		upcFacetDelta.put(VENDOR_JSON, product.getVendor());
		upcFacetDelta.put(PID_JSON, product.getPid());
		upcFacetDelta.put(NRF_COLOR_CODE_JSON, product.getNrfColorCode());
		if(StringUtils.isNotEmpty(errorDesc)) {
			upcFacetDelta.put(RESP_STATUS, "400");
			upcFacetDelta.put(RESP_MESSAGE, errorDesc);
		} else {
			upcFacetDelta.put(ATTRIBUTES, changedAttributes);	
		}
		return upcFacetDelta;
	}

	/**
	 * 
	 * @param product
	 * @param jsonArrayApp
	 * @param changedPropsApp
	 */
	private JSONObject populateDeltaJSON(MiraklData product, Map<Object, Object> changedPropsApp, String application,
			String errorDesc) {
		JSONObject prdDeltaJsonObj = new JSONObject();
		JSONObject jsonObjDelta = new JSONObject(changedPropsApp);
		
		// Ordering the elements in same order as inserted
		try {
			Field changeMap = prdDeltaJsonObj.getClass().getDeclaredField("map");
			changeMap.setAccessible(true);
			changeMap.set(prdDeltaJsonObj, new LinkedHashMap<>());
			changeMap.setAccessible(false);
		} catch (IllegalAccessException | NoSuchFieldException e) {
			LOGGER.error(e.getMessage(), e);
		}
		
		if (STELLA.equalsIgnoreCase(application) || PDF.equalsIgnoreCase(application)) {
			prdDeltaJsonObj.put(UPCID, product.getUpcId());
			prdDeltaJsonObj.put(OP_DIV, product.getOpDiv());
			if(StringUtils.isNotEmpty(errorDesc)) {
				prdDeltaJsonObj.put(RESP_STATUS, "400");
				prdDeltaJsonObj.put(RESP_MESSAGE, errorDesc);
			} else {
				prdDeltaJsonObj.put(PRODUCT_TYPE, product.getProductType());
				prdDeltaJsonObj.put(DELTA, jsonObjDelta);				
			}
		} else {
			prdDeltaJsonObj.put(UPCID_IMAGES, product.getUpcId());
			prdDeltaJsonObj.put(CREATE_RESP_OP_DIV, product.getOpDiv());
			Map<String, Object> map = prdDeltaJsonObj.toMap();
			map.putAll(jsonObjDelta.toMap());
			JSONObject mergedJsonObj = new JSONObject(map);
			prdDeltaJsonObj = mergedJsonObj;
		}
		return prdDeltaJsonObj;
	}

	/**
	 * 
	 * @param fileName
	 * @param application
	 * @param jsonArrayApp
	 */
	public void writeToDeltaBucket(String fileName, String application, JSONArray jsonArrayApp, JSONArray jsonArrayError) {
		try {
			String bucket = "";
			JSONObject jsonObjIn = new JSONObject();
			JSONObject jsonObjDelta = new JSONObject();
			if (IMAGES.equalsIgnoreCase(application)) {
				jsonObjIn.put(IMAGE_DATA, jsonArrayApp);
				bucket = imageDeltaBucket;
			} else {
				bucket = deltaBucket;
				if(jsonArrayApp.length() > 0 && !jsonArrayApp.isNull(0)) {
					jsonObjIn.put(application, jsonArrayApp);
				} 
				
				if(jsonArrayError.length() > 0 && !jsonArrayError.isNull(0)) {
					jsonObjIn.put(ERROR_UPCS, jsonArrayError);
				}
			}
			jsonObjDelta.put(MIRAKL, jsonObjIn);

			byte[] appDeltaData = convertToByteArray(jsonObjDelta);
			String appDeltaFilePath = fileProcessor.getFilePath(application, fileName);

			LOGGER.info(application + " Delta upload in cloud storage started");
			cloudStorageService.uploadToCloudStorage(appDeltaFilePath, appDeltaData, bucket);
			LOGGER.info(application + " Delta upload in cloud storage completed");

		} catch (Exception e) {
			LOGGER.error("Error in writing to cloud storage", e);
		}
	}

	/**
	 * 
	 * @param jsonObj
	 * @return
	 */
	public byte[] convertToByteArray(JSONObject jsonObj) {
		return jsonObj.toString().getBytes();

	}
	
	/**
	 * 
	 * @param imgDataList,MiraklData,isMainImgP0
	 * @return
	 */
	public List<ImageUpdateRespData> createRespData(List<ImageData> imgDataList, MiraklData product,
			Boolean isMainImgP0) {
		List<ImageUpdateRespData> imageDeltaList = new ArrayList<>();

		if (null != imgDataList && !imgDataList.isEmpty()) {
			for (ImageData imgData : imgDataList) {
				ImageUpdateRespData imageUpdateRespData = new ImageUpdateRespData();
				if (MAIN_IMAGE.equalsIgnoreCase(imgData.getImageType()) && isMainImgP0) {
					imageUpdateRespData.setImgType(PRIMARY_IMAGE);
				} else if (MAIN_IMAGE.equalsIgnoreCase(imgData.getImageType()) && !isMainImgP0) {
					imageUpdateRespData.setImgType(NON_PRIMARY_IMAGE);
				} else {
					imageUpdateRespData.setImgType(imgData.getImageType());
				}
				imageUpdateRespData.setImgUrl(imgData.getImageUrl());
				imageUpdateRespData.setImgId(imgData.getImageId());
				Timestamp timestamp = new Timestamp(System.currentTimeMillis());
				imageUpdateRespData.setCreatedTs(timestamp);
				imageUpdateRespData.setProcessingStage(INPROCESS);
				imageUpdateRespData.setInputFileName(product.getFileName());
				imageUpdateRespData.setUpcId(product.getUpcId());
				imageUpdateRespData.setOpDiv(product.getOpDiv());
				String deltaFileName = fileProcessor.getName(product.getFileName());
				imageUpdateRespData.setDeltaFileName(deltaFileName);
				imageUpdateRespData.setIasRespReceivedFlag(0);
				imageUpdateRespData.setIlRespSentFlag(0);
				imageDeltaList.add(imageUpdateRespData);
			}
			;
		}
		return imageDeltaList;

	}

}

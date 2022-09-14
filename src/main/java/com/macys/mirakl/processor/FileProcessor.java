package com.macys.mirakl.processor;

import static com.macys.mirakl.util.OrchConstants.ATTRIBUTES;
import static com.macys.mirakl.util.OrchConstants.BCOM_FACET_RESP;
import static com.macys.mirakl.util.OrchConstants.BCOM_PDF_RESP;
import static com.macys.mirakl.util.OrchConstants.BCOM_STELLA_RESP;
import static com.macys.mirakl.util.OrchConstants.DEPT_JSON;
import static com.macys.mirakl.util.OrchConstants.FACET_DATA;
import static com.macys.mirakl.util.OrchConstants.FACET_UPDATE_RESP;
import static com.macys.mirakl.util.OrchConstants.FILE_NAME_JSON;
import static com.macys.mirakl.util.OrchConstants.IMAGE;
import static com.macys.mirakl.util.OrchConstants.IMAGES;
import static com.macys.mirakl.util.OrchConstants.IMAGE_CHANGE_DETECTION;
import static com.macys.mirakl.util.OrchConstants.ITEMS;
import static com.macys.mirakl.util.OrchConstants.MCOM_FACET_RESP;
import static com.macys.mirakl.util.OrchConstants.MCOM_PDF_RESP;
import static com.macys.mirakl.util.OrchConstants.MCOM_STELLA_RESP;
import static com.macys.mirakl.util.OrchConstants.MIRAKL;
import static com.macys.mirakl.util.OrchConstants.MSRP_JSON;
import static com.macys.mirakl.util.OrchConstants.NRF_COLOR_CODE_JSON;
import static com.macys.mirakl.util.OrchConstants.NRF_SIZE_CODE_JSON;
import static com.macys.mirakl.util.OrchConstants.OFFER_ACTIVE_FLAG;
import static com.macys.mirakl.util.OrchConstants.OFFER_DELETED_FLAG;
import static com.macys.mirakl.util.OrchConstants.OFFER_ID;
import static com.macys.mirakl.util.OrchConstants.OFFER_OP_DIV;
import static com.macys.mirakl.util.OrchConstants.OFFER_REQUEST;
import static com.macys.mirakl.util.OrchConstants.OFFER_REQ_FILE;
import static com.macys.mirakl.util.OrchConstants.OFFER_RESPONSE;
import static com.macys.mirakl.util.OrchConstants.OFFER_RESP_FILE;
import static com.macys.mirakl.util.OrchConstants.OFFER_UPC_ID;
import static com.macys.mirakl.util.OrchConstants.OP_DIV;
import static com.macys.mirakl.util.OrchConstants.PDF_DATA;
import static com.macys.mirakl.util.OrchConstants.PID_JSON;
import static com.macys.mirakl.util.OrchConstants.PRODUCT_CREATE;
import static com.macys.mirakl.util.OrchConstants.PRODUCT_DATA;
import static com.macys.mirakl.util.OrchConstants.PRODUCT_TYPE;
import static com.macys.mirakl.util.OrchConstants.PRODUCT_UPDATE;
import static com.macys.mirakl.util.OrchConstants.PRODUCT_UPDATE_RESP;
import static com.macys.mirakl.util.OrchConstants.RESP_MESSAGE;
import static com.macys.mirakl.util.OrchConstants.RESP_STATUS;
import static com.macys.mirakl.util.OrchConstants.STELLA_DATA;
import static com.macys.mirakl.util.OrchConstants.TAX_CODE_JSON;
import static com.macys.mirakl.util.OrchConstants.UPCID;
import static com.macys.mirakl.util.OrchConstants.UPDATE;
import static com.macys.mirakl.util.OrchConstants.VENDOR_JSON;
import static com.macys.mirakl.util.OrchConstants.WRONG_FILE;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.macys.mirakl.model.FacetUpdateRespData;
import com.macys.mirakl.model.MiraklData;
import com.macys.mirakl.model.OfferIdRequestData;
import com.macys.mirakl.model.OfferIdResponseData;
import com.macys.mirakl.model.ProductUpdateRespData;
import com.macys.mirakl.util.FilePrefix;

@Component
public class FileProcessor {

	private static final Logger LOGGER = LoggerFactory.getLogger(FileProcessor.class);

	@Value("${offer.file.version}")
	private String offerFileVersion;

	public String getFileOperation(String objectName) {

		if (Pattern.matches("BCOM[/]product_create[/]BCOM_product_create_[0-9]{6}_[0-9]{6}.json", objectName)
				|| Pattern.matches("MCOM[/]product_create[/]MCOM_product_create_[0-9]{6}_[0-9]{6}.json", objectName)) {
			return PRODUCT_CREATE;
		} else if (Pattern.matches("BCOM[/]product_update[/]BCOM_product_update_[0-9]{6}_[0-9]{6}.json", objectName)
				|| Pattern.matches("MCOM[/]product_update[/]MCOM_product_update_[0-9]{6}_[0-9]{6}.json", objectName)) {
			return PRODUCT_UPDATE;
		} else
			return WRONG_FILE;
	}
	
	public String getFileName(String objectName) {
		int lastIndex = objectName.lastIndexOf("/");
		int length = objectName.length();
		String fileName = objectName.substring(lastIndex+1,length);
		return fileName;
	}

	/**
	 * 
	 * @param is
	 * @return
	 * @throws IOException 
	 */
	public String inputStreamToString(InputStream is) throws IOException {
		StringBuilder sb = new StringBuilder();
		try (InputStreamReader streamReader = new InputStreamReader(is, StandardCharsets.UTF_8);
				BufferedReader reader = new BufferedReader(streamReader)) {
			String line;
			while ((line = reader.readLine()) != null) {
				sb.append(line).append("\n");
			}
		} catch (IOException e) {
			LOGGER.error("IOException:", e);
			throw new IOException("IOException while converting inputStreamToString", e);
		}
		return sb.toString();
	}
	
	public String getFilePath(String application, String fileName) {
		String opDiv = "", fileNamePrefix = "", fileTS = "";
		StringBuilder uploadFilePath = new StringBuilder();
		boolean imageFlow = false;
		
		opDiv = fileName.substring(0, 4);
		String[] fileNameArray = fileName.split(UPDATE, 2);
		fileNamePrefix = fileNameArray[0];
		fileTS = fileNameArray[1];
		
		if(IMAGES.equalsIgnoreCase(application)) {
			imageFlow = true;
			application = IMAGE_CHANGE_DETECTION;
		}
		
		String uploadFolder = opDiv.concat("/").concat(application.toLowerCase());
		if (StringUtils.isNotBlank(uploadFolder)) {
			uploadFilePath.append(uploadFolder).append("/");
		}
		if(imageFlow) {
			uploadFilePath.append(IMAGE).append("_").append(UPDATE).append(fileTS);
		} else {
			uploadFilePath.append(fileNamePrefix).append(UPDATE).append("_").append(application.toLowerCase()).append(fileTS);
		}
		return uploadFilePath.toString();
	}

	/**
	 * 
	 * @param fileName
	 * @param jsonInput
	 * @return
	 */
	public List<MiraklData> processUpdateJSON(String fileName, String jsonInput) throws JSONException {
		JSONObject productUpdatesJson = new JSONObject(jsonInput);
		JSONObject mirakl = productUpdatesJson.getJSONObject(MIRAKL);
		JSONArray productData = mirakl.getJSONArray(PRODUCT_DATA);
		Map<String, MiraklData> upcMiraklDataMap = new HashMap<>();
		for (int i = 0; i < productData.length(); i++) {
			JSONObject product = productData.getJSONObject(i);
			MiraklData data = new MiraklData(fileName, String.valueOf(product.get(UPCID)),
					String.valueOf(product.get(OP_DIV)), String.valueOf(product.get(PRODUCT_TYPE)),
					String.valueOf(product.get(TAX_CODE_JSON)),String.valueOf(product.get(NRF_SIZE_CODE_JSON)),String.valueOf(product.get(MSRP_JSON)),
					String.valueOf(product.get(PDF_DATA)),
					String.valueOf(product.get(STELLA_DATA)),
					String.valueOf(product.get(IMAGES)));
			upcMiraklDataMap.put(data.getUpcId(), data);
		}
		JSONObject facetData = mirakl.getJSONObject(FACET_DATA);
		JSONArray items = facetData.getJSONArray(ITEMS);
		for (int j = 0; j < items.length(); j++) {
			JSONObject facetItem = items.getJSONObject(j);
			String facetUpc = String.valueOf(facetItem.get(UPCID));
			MiraklData miraklDataWithFacet = upcMiraklDataMap.get(facetUpc);
			miraklDataWithFacet.setDept(String.valueOf(facetItem.get(DEPT_JSON)));
			miraklDataWithFacet.setVendor(String.valueOf(facetItem.get(VENDOR_JSON)));
			miraklDataWithFacet.setPid(String.valueOf(facetItem.get(PID_JSON)));
			miraklDataWithFacet.setNrfColorCode(String.valueOf(facetItem.get(NRF_COLOR_CODE_JSON)));
			miraklDataWithFacet.setFacetData(String.valueOf(facetItem.get(ATTRIBUTES)));
			upcMiraklDataMap.replace(facetUpc, miraklDataWithFacet);
		}
		List<MiraklData> prdTxns = upcMiraklDataMap.values().stream().collect(Collectors.toCollection(ArrayList::new));
		return prdTxns;
	}
	
	/**
	 * 
	 * @param jsonInput
	 * @return
	 */
	public List<OfferIdRequestData> processOfferIdReqJSON(String jsonInput) throws JSONException {
		List<OfferIdRequestData> offerIdReqList = new ArrayList<>();
		try {
			JSONObject offerIdReqJson = new JSONObject(jsonInput);
			JSONObject mirakl = offerIdReqJson.getJSONObject(MIRAKL);
			JSONArray offerIdData = mirakl.getJSONArray(OFFER_REQUEST);
			for (int i = 0; i < offerIdData.length(); i++) {
				JSONObject offerJson = offerIdData.getJSONObject(i);
				OfferIdRequestData data = new OfferIdRequestData(String.valueOf(offerJson.get(OFFER_UPC_ID)),
						String.valueOf(offerJson.get(OFFER_OP_DIV)), String.valueOf(offerJson.get(OFFER_ID)),
						offerJson.has(OFFER_ACTIVE_FLAG) ? String.valueOf(offerJson.opt(OFFER_ACTIVE_FLAG)) : null,
						offerJson.has(OFFER_DELETED_FLAG) ? String.valueOf(offerJson.opt(OFFER_DELETED_FLAG)) : null );
				offerIdReqList.add(data);
			}
		} catch (JSONException je) {
			LOGGER.error("Error while converting request json to model object:",je);
			throw new JSONException("Error while converting request json to model object:",je);
		}
		return offerIdReqList;			
	}
	
	/**
	 * 
	 * @param jsonInput
	 * @return
	 */
	public List<OfferIdResponseData> processOfferIdRespJSON(String jsonInput) throws JSONException {
		List<OfferIdResponseData> offerIdRespList = new ArrayList<>();
		try {
			JSONObject offerIdRespJson = new JSONObject(jsonInput);
			JSONObject mirakl = offerIdRespJson.getJSONObject(MIRAKL);
			String fileNameJson = mirakl.getString(FILE_NAME_JSON);
			JSONArray offerIdData = mirakl.getJSONArray(OFFER_RESPONSE);
			for (int i = 0; i < offerIdData.length(); i++) {
				JSONObject offerJson = offerIdData.getJSONObject(i);
				OfferIdResponseData data = new OfferIdResponseData(String.valueOf(offerJson.get(OFFER_UPC_ID)),
						String.valueOf(offerJson.get(OFFER_OP_DIV)), String.valueOf(offerJson.get(OFFER_ID)),
						String.valueOf(offerJson.get(RESP_STATUS)), String.valueOf(offerJson.get(RESP_MESSAGE)), fileNameJson);
				offerIdRespList.add(data);
			}
		} catch (JSONException je) {
			LOGGER.error("Error while converting request json to model object:",je);
			throw new JSONException("Error while converting request json to model object:",je);
		}
		return offerIdRespList;
	}

	// Product Update Response - Convert the incoming json to model object
	public List<ProductUpdateRespData> processPrdUpdateRespJSON(String jsonInput) throws JSONException {
		List<ProductUpdateRespData> prdUpdateRespList = new ArrayList<ProductUpdateRespData>();
		try {
			JSONObject prdUpdateRespJson = new JSONObject(jsonInput);
			JSONObject miraklJsonObj = prdUpdateRespJson.getJSONObject(MIRAKL);
			String fileNameJson = miraklJsonObj.getString(FILE_NAME_JSON);
			JSONArray itemsJsonArr = miraklJsonObj.getJSONArray(ITEMS);
			for (int i = 0; i < itemsJsonArr.length(); i++) {
				JSONObject upcJson = itemsJsonArr.getJSONObject(i);
				ProductUpdateRespData respData = new ProductUpdateRespData(String.valueOf(upcJson.get(UPCID)),
						String.valueOf(upcJson.get(OP_DIV)), String.valueOf(upcJson.get(RESP_STATUS)),
						String.valueOf(upcJson.get(RESP_MESSAGE)), fileNameJson);
				prdUpdateRespList.add(respData);
			}
		} catch (JSONException je) {
			LOGGER.error("Error while converting product update response json to model object:",je);
			throw new JSONException("Error while converting product update response json to model object:"+je);
		}
		return prdUpdateRespList;
	}

	public List<FacetUpdateRespData> processFacetUpdateRespJSON(String jsonInput) throws JSONException {
		List<FacetUpdateRespData> facetUpdateRespList = new ArrayList<FacetUpdateRespData>();
		try {
			JSONObject facetUpdateRespJson = new JSONObject(jsonInput);
			JSONObject miraklJsonObj = facetUpdateRespJson.getJSONObject(MIRAKL);
			String fileNameJson = miraklJsonObj.getString(FILE_NAME_JSON);
			JSONArray itemsJsonArr = miraklJsonObj.getJSONArray(ITEMS);
			for (int i = 0; i < itemsJsonArr.length(); i++) {
				JSONObject upcJson = itemsJsonArr.getJSONObject(i);
				FacetUpdateRespData respData = new FacetUpdateRespData(String.valueOf(upcJson.get(UPCID)),
						String.valueOf(upcJson.get(OP_DIV)), String.valueOf(upcJson.get(PRODUCT_TYPE)),
						String.valueOf(upcJson.get(DEPT_JSON)), String.valueOf(upcJson.get(VENDOR_JSON)),
						String.valueOf(upcJson.get(PID_JSON)), String.valueOf(upcJson.get(NRF_COLOR_CODE_JSON)),
						String.valueOf(upcJson.get(RESP_STATUS)), String.valueOf(upcJson.get(RESP_MESSAGE)),
						String.valueOf(upcJson.get(ATTRIBUTES)), fileNameJson, "");
				facetUpdateRespList.add(respData);
			}
		} catch (JSONException je) {
			LOGGER.error("Error while converting facet update response json to model object:",je);
			throw new JSONException("Error while converting facet update response json to model object:"+je);
		}
		return facetUpdateRespList;
	}

	public String getFileOperationForOfferId(String objectName) {

		if (Pattern.matches("BCOM[/]offer[/]BCOM_" + offerFileVersion + "_[0-9]{6}_[0-9]{6}.json", objectName)
				|| Pattern.matches("MCOM[/]offer[/]MCOM_" +  offerFileVersion + "_[0-9]{6}_[0-9]{6}.json", objectName)) {
			return OFFER_REQ_FILE;
		} else if (Pattern.matches("BCOM[/]offer_response[/]BCOM_" + offerFileVersion + "_[0-9]{6}_[0-9]{6}_1.json", objectName)
				|| Pattern.matches("MCOM[/]offer_response[/]MCOM_" + offerFileVersion + "_[0-9]{6}_[0-9]{6}_1.json", objectName)) {
			return OFFER_RESP_FILE;
		} else {
			return WRONG_FILE;
		}
	}
	
	public String getFileOperationForRespFile(String objectName) {

		if (objectName.startsWith(BCOM_PDF_RESP)
				|| objectName.startsWith(MCOM_PDF_RESP)
				|| objectName.startsWith(BCOM_STELLA_RESP)
				|| objectName.startsWith(MCOM_STELLA_RESP)) {
			return PRODUCT_UPDATE_RESP;
		} else if (objectName.startsWith(BCOM_FACET_RESP)
				|| objectName.startsWith(MCOM_FACET_RESP)) {
			return FACET_UPDATE_RESP;
		} else
			return WRONG_FILE;
	}
	
	public static FilePrefix getFilePrefix(String filename) {
		if (filename.startsWith(FilePrefix.MCOM.name())) {
			return FilePrefix.MCOM;
		} else {
			return FilePrefix.BCOM;
		}
	}

	public String getName(String fileName) {
		String fileTS = "";
		StringBuilder name = new StringBuilder();
		
		String[] fileNameArray = fileName.split(UPDATE, 2);
		fileTS = fileNameArray[1];
		name.append(IMAGE).append("_").append(UPDATE).append(fileTS);
		
		return name.toString();
	}

}
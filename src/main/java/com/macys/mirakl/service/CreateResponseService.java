package com.macys.mirakl.service;

import static com.macys.mirakl.util.OrchConstants.CREATE_RESP_OP_DIV;
import static com.macys.mirakl.util.OrchConstants.IMAGE_UPC;
import static com.macys.mirakl.util.OrchConstants.MSRP_JSON;
import static com.macys.mirakl.util.OrchConstants.NRF_COLOR_CODE_JSON;
import static com.macys.mirakl.util.OrchConstants.NRF_SIZE_CODE_JSON;
import static com.macys.mirakl.util.OrchConstants.PID_JSON;
import static com.macys.mirakl.util.OrchConstants.PRODUCT_SKU;
import static com.macys.mirakl.util.OrchConstants.TAX_CODE_JSON;
import static com.macys.mirakl.util.OrchConstants.UPC;
import static com.macys.mirakl.util.OrchConstants.UPCS;
import static com.macys.mirakl.util.OrchConstants.OP_DIV_BCOM;
import static com.macys.mirakl.util.OrchConstants.OP_DIV_MCOM;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.macys.mirakl.config.CreateResponseConfig;
import com.macys.mirakl.exception.MiraklRepositoryException;
import com.macys.mirakl.model.CreateResponseData;
import com.macys.mirakl.model.PublishLiveRequestData;
import com.macys.mirakl.model.PublishUpcData;
import com.macys.mirakl.model.UpcMasterData;

@Transactional
@Service
public class CreateResponseService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CreateResponseService.class);
	
	@Value("${com.macys.mp.pubsub.create.response.mcom.il.pub}")
	private String createResponseMcomTopic;
	
	@Value("${com.macys.mp.pubsub.create.response.bcom.il.pub}")
	private String createResponseBcomTopic;

	@Autowired
	private SQLService sqlService;
	
	@Autowired
	private CreateResponseConfig.PubsubOutboundGatewayMcom messagingGatewayMcom;
	
	@Autowired
	private CreateResponseConfig.PubsubOutboundGatewayBcom messagingGatewayBcom;

	/**
	 * 
	 * @param message
	 */
	public void processCreateRespFile(String message) {
		List<CreateResponseData> createResponseDatas = new ArrayList<CreateResponseData>();
		try {
			PublishLiveRequestData upcList = processUpcDataJSON(message);
			for (PublishUpcData pu : upcList.getUpcs()) {
				try {
					LOGGER.info("UPC:" + pu.getUpc() + " OpDiv:" + pu.getOpDiv() + " ProductSku:" + pu.getProductSku()
							+ " IsMainImgP0:" + pu.isImageUpc());
					UpcMasterData upcMasterData = null;
					try {
						upcMasterData = sqlService.findUpcMasterDataByUpc(pu.getUpc(), pu.getOpDiv());
					} catch (MiraklRepositoryException mre) {
						LOGGER.error("MiraklRepositoryException occurred on interacting with repository: ", mre);
						continue;
					} 
					// Populate additional data to be added in request and publish to IL topic
					CreateResponseData createResponseData = new CreateResponseData(pu.getProductSku(), pu.getUpc(),
							pu.getOpDiv(), upcMasterData.getPid(), upcMasterData.getNrfColorCode(),
							upcMasterData.getNrfSizeCode(), upcMasterData.getMsrp(), upcMasterData.getTaxCode());
					createResponseDatas.add(createResponseData);

					// Publish individual UPC data with additional details populated onto IL topic
					JSONObject upcJsonObj = populateCreateRespPubJSON(createResponseData);
					if (OP_DIV_MCOM.equalsIgnoreCase(createResponseData.getOpDiv())) {
						messagingGatewayMcom.sendToPubsubCreateRespMcom(upcJsonObj.toString());
						LOGGER.info(
								"Published message: " + upcJsonObj.toString() + " to topic:" + createResponseMcomTopic);
					} else if (OP_DIV_BCOM.equalsIgnoreCase(createResponseData.getOpDiv())) {
						messagingGatewayBcom.sendToPubsubCreateRespBcom(upcJsonObj.toString());
						LOGGER.info(
								"Published message: " + upcJsonObj.toString() + " to topic:" + createResponseBcomTopic);
					} else {
						LOGGER.error("Invalid OP_DIV");
					}
					
				} catch (Exception e) {
					LOGGER.error("Exception in processCreateRespFile():", e);
				}
			}
			if (createResponseDatas.size() > 0) {
				try {
					// Update MAIN_IMG_FLAG and BUYER_APPROVED_FLAG in MP_UPC_MASTER table
					sqlService.updateCreateRespList(upcList.getUpcs());

					// Insert audit details into MP_CREATE_RESP_AUDIT table
					sqlService.insertCreateRespAuditList(createResponseDatas);
					LOGGER.info("Create Response details successfully inserted in MP_CREATE_RESP_AUDIT table");
				} catch (MiraklRepositoryException mre) {
					LOGGER.error("MiraklRepositoryException occurred on interacting with repository: ", mre);
				}
			} else {
				LOGGER.error("No data (modified create response data) available to be inserted into audit table");
			}
		} catch (JSONException je) {
			LOGGER.error("JSONException occurred while processing create response json: ", je);
		} catch (Exception e) {
			LOGGER.error("Exception in processCreateRespFile():", e);
		}
	}
		
	
	/**
	 * 
	 * @param upcData
	 * @return
	 */
	private PublishLiveRequestData processUpcDataJSON(String upcData) throws JSONException {
		LOGGER.info("Inside processUpcDataJSON");
		PublishLiveRequestData upcDataList = new PublishLiveRequestData();
		List<PublishUpcData> publishUpcDatas = new ArrayList<PublishUpcData>();
		JSONObject upcDataJson = new JSONObject(upcData);
		JSONArray upcJsonArray = upcDataJson.getJSONArray(UPCS);
		for (int i = 0; i < upcJsonArray.length(); i++) {
			JSONObject upcJson = upcJsonArray.getJSONObject(i);
			PublishUpcData data = new PublishUpcData(String.valueOf(upcJson.get(UPC)),
					String.valueOf(upcJson.get(PRODUCT_SKU)), String.valueOf(upcJson.get(CREATE_RESP_OP_DIV)),
					Boolean.parseBoolean(String.valueOf(upcJson.get(IMAGE_UPC))));
			publishUpcDatas.add(data);
		}
		upcDataList.setUpcs(publishUpcDatas);		
		return upcDataList;
	}
	
	private JSONObject populateCreateRespPubJSON(CreateResponseData createResponseData) {
		LOGGER.info("Inside populateCreateRespPubJSON");
		JSONObject upcJsonObj = new JSONObject();
		upcJsonObj.put(UPC, createResponseData.getUpc());
		upcJsonObj.put(PRODUCT_SKU, createResponseData.getProductSku());
		upcJsonObj.put(CREATE_RESP_OP_DIV, createResponseData.getOpDiv());
		upcJsonObj.put(PID_JSON, createResponseData.getPid());
		upcJsonObj.put(NRF_COLOR_CODE_JSON, createResponseData.getNrfColorCode());
		upcJsonObj.put(NRF_SIZE_CODE_JSON, createResponseData.getNrfSizeCode());
		upcJsonObj.put(MSRP_JSON, createResponseData.getMsrp());
		upcJsonObj.put(TAX_CODE_JSON, createResponseData.getTaxCode());
		return upcJsonObj;
	}

}

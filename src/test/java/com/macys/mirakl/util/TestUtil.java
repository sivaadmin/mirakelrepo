package com.macys.mirakl.util;

import static com.macys.mirakl.util.OrchConstants.CREATE_RESP_OP_DIV;
import static com.macys.mirakl.util.OrchConstants.MSRP_JSON;
import static com.macys.mirakl.util.OrchConstants.NRF_COLOR_CODE_JSON;
import static com.macys.mirakl.util.OrchConstants.NRF_SIZE_CODE_JSON;
import static com.macys.mirakl.util.OrchConstants.PID_JSON;
import static com.macys.mirakl.util.OrchConstants.PRODUCT_SKU;
import static com.macys.mirakl.util.OrchConstants.TAX_CODE_JSON;
import static com.macys.mirakl.util.OrchConstants.UPC;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.macys.mirakl.model.CreateResponseData;
import com.macys.mirakl.model.OfferIdRequestData;
import com.macys.mirakl.model.OfferIdResponseData;
import com.macys.mirakl.model.PublishUpcData;



public class TestUtil {
	
	public static List<PublishUpcData> generatePublishUpcList() {
		
		List<PublishUpcData> publishUpcList = new ArrayList<PublishUpcData>();	
		
		PublishUpcData upc1 = new PublishUpcData("12345678901","1234567890111111112","12",true);		
		PublishUpcData upc2 = new PublishUpcData("12345678902","1234567890211111112","12",false);
		
		publishUpcList.add(upc1);
		publishUpcList.add(upc2);

		return publishUpcList;
	}
	
	public static List<PublishUpcData> generatePublishUpcListBcom() {
		
		List<PublishUpcData> publishUpcList = new ArrayList<PublishUpcData>();	
		
		PublishUpcData upc1 = new PublishUpcData("12345678901","1234567890111111113","13",true);		
		PublishUpcData upc2 = new PublishUpcData("12345678902","1234567890211111113","13",false);
		
		publishUpcList.add(upc1);
		publishUpcList.add(upc2);

		return publishUpcList;
	}
	
	public static List<CreateResponseData> populateCreateRespFinalList() throws JSONException {
		List<CreateResponseData> createResponseDatas = new ArrayList<CreateResponseData>();
		
		CreateResponseData createResponseData1 = new CreateResponseData("1234567890111111112","12345678901","12","1111","RedColor1","12","12","dummyValue1");
		CreateResponseData createResponseData2 = new CreateResponseData("1234567890211111112","12345678902","12","2222","RedColor2","12","12","dummyValue2");
		
		createResponseDatas.add(createResponseData1);
		createResponseDatas.add(createResponseData2);		
	
		return createResponseDatas;
	}
	
	public static JSONObject populateCreateRespPubJSON() {
		JSONObject upcJsonObj = new JSONObject();
		upcJsonObj.put(UPC, "12345678901");
		upcJsonObj.put(PRODUCT_SKU, "1234567890111111112");
		upcJsonObj.put(CREATE_RESP_OP_DIV, "12");
		upcJsonObj.put(PID_JSON, "1111");
		upcJsonObj.put(NRF_COLOR_CODE_JSON, "RedColor1");
		upcJsonObj.put(NRF_SIZE_CODE_JSON, "12");
		upcJsonObj.put(MSRP_JSON, "12");
		upcJsonObj.put(TAX_CODE_JSON, "dummyValue1");
		return upcJsonObj;
	}
	
	public static JSONObject populateCreateRespPubJSONBcom() {
		JSONObject upcJsonObj = new JSONObject();
		upcJsonObj.put(UPC, "12345678901");
		upcJsonObj.put(PRODUCT_SKU, "1234567890111111113");
		upcJsonObj.put(CREATE_RESP_OP_DIV, "13");
		upcJsonObj.put(PID_JSON, "1111");
		upcJsonObj.put(NRF_COLOR_CODE_JSON, "RedColor1");
		upcJsonObj.put(NRF_SIZE_CODE_JSON, "130");
		upcJsonObj.put(MSRP_JSON, "130");
		upcJsonObj.put(TAX_CODE_JSON, "dummyValue1");
		return upcJsonObj;
	}
	
	public static List<OfferIdRequestData> generateReqList() {	
		
		List<OfferIdRequestData> offerIdReqList = new ArrayList<>();
		
		OfferIdRequestData offerIdRequestData1 = OfferIdRequestData.builder()
				.upcId("12345678901")
				.opDiv("13")
				.offerId("1221")
				.build();
		
		OfferIdRequestData offerIdRequestData2 = OfferIdRequestData.builder()
				.upcId("12345678902")
				.opDiv("13")
				.offerId("1222")
				.build();
		
		OfferIdRequestData offerIdRequestData3 = OfferIdRequestData.builder()
				.upcId("12345678903")
				.opDiv("13")
				.offerId("1223")
				.build();
		
		offerIdReqList.add(offerIdRequestData1);
		offerIdReqList.add(offerIdRequestData2);
		offerIdReqList.add(offerIdRequestData3);
		
		return offerIdReqList;
	}

	public static List<OfferIdResponseData> generateRespList() {	
		
		List<OfferIdResponseData> offerIdRespList = new ArrayList<>();
		
		OfferIdResponseData offerIdRespData1 = OfferIdResponseData.builder()
				.upcId("12345678901")
				.opDiv("13")
				.offerId("1221")
				.status("200")
				.message("Success")
				.fileNameJson("BCOM_offer_220329_191088.json")
				.build();
		
		OfferIdResponseData offerIdRespData2 = OfferIdResponseData.builder()
				.upcId("12345678902")
				.opDiv("13")
				.offerId("1222")
				.status("200")
				.message("Success")
				.fileNameJson("BCOM_offer_220329_191088.json")
				.build();
		
		OfferIdResponseData offerIdRespData3 = OfferIdResponseData.builder()
				.upcId("12345678903")
				.opDiv("13")
				.offerId("1223")
				.status("200")
				.message("Success")
				.fileNameJson("BCOM_offer_220329_191088.json")
				.build();
		
		offerIdRespList.add(offerIdRespData1);
		offerIdRespList.add(offerIdRespData2);
		offerIdRespList.add(offerIdRespData3);
		
		return offerIdRespList;
	}

}
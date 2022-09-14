package com.macys.mirakl.service;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.macys.mirakl.config.CreateResponseConfig;
import com.macys.mirakl.exception.MiraklRepositoryException;
import com.macys.mirakl.model.CreateResponseData;
import com.macys.mirakl.model.PublishUpcData;
import com.macys.mirakl.model.UpcMasterData;
import com.macys.mirakl.util.TestUtil;

@ExtendWith(MockitoExtension.class)
class CreateResponseServiceTest {
	
	@InjectMocks
	CreateResponseService createResponseService;
	
	@Mock
	SQLService sqlService;
	
	@Mock
	CreateResponseConfig.PubsubOutboundGatewayMcom messagingGatewayMcom;
	
	@Mock
	CreateResponseConfig.PubsubOutboundGatewayBcom messagingGatewayBcom;

	@BeforeEach
	void setUp() throws Exception {
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	public void testProcessCreateRespFile_Mcom() throws MiraklRepositoryException {
		
		// given
		UpcMasterData upcMasterData1 = new UpcMasterData();
		upcMasterData1.setPid("1111");
		upcMasterData1.setNrfColorCode("RedColor1");
		upcMasterData1.setNrfSizeCode("12");
		upcMasterData1.setMsrp("12");
		upcMasterData1.setTaxCode("dummyValue1");
		
		UpcMasterData upcMasterData2 = new UpcMasterData();
		upcMasterData2.setPid("2222");
		upcMasterData2.setNrfColorCode("RedColor2");
		upcMasterData2.setNrfSizeCode("12");
		upcMasterData2.setMsrp("12");
		upcMasterData2.setTaxCode("dummyValue2");
		
		List<PublishUpcData> upcList = TestUtil.generatePublishUpcList(); //processUpcDataJSON(message);
		List<CreateResponseData> createResponseDatas = new ArrayList<CreateResponseData>();
		//Populate additional data to be added in request and publish to IL topic
		CreateResponseData createResponseData1 = new CreateResponseData("1234567890111111112","12345678901","12","1111","RedColor1","12","12","dummyValue1");
		CreateResponseData createResponseData2 = new CreateResponseData("1234567890211111112","12345678902","12","2222","RedColor2","12","12","dummyValue2");
		createResponseDatas.add(createResponseData1);
		createResponseDatas.add(createResponseData2);
		
		String message = "{\"upcs\":[{\"upc\":\"12345678901\",\"productsku\":\"1234567890111111112\",\"opdiv\":\"12\",\"imageupc\":true},{\"upc\":\"12345678902\",\"productsku\":\"1234567890211111112\",\"opdiv\":\"12\",\"imageupc\":false}]}";
		
		JSONObject upcJsonObj = TestUtil.populateCreateRespPubJSON();
		
		when(sqlService.findUpcMasterDataByUpc("12345678901", "12")).thenReturn(upcMasterData1);
		when(sqlService.findUpcMasterDataByUpc("12345678902", "12")).thenReturn(upcMasterData2);
		doNothing().when(sqlService).updateCreateRespList(upcList);
		doNothing().when(sqlService).insertCreateRespAuditList(createResponseDatas);
		
		// when
		createResponseService.processCreateRespFile(message);
		
		// then
		verify(sqlService,times(1)).updateCreateRespList(upcList);
		verify(messagingGatewayMcom,times(1)).sendToPubsubCreateRespMcom(upcJsonObj.toString());
		verify(sqlService, times(1)).insertCreateRespAuditList(createResponseDatas);
	
	}
	
	@Test
	public void testProcessCreateRespFile_Bcom() throws MiraklRepositoryException {
		
		// given
		UpcMasterData upcMasterData1 = new UpcMasterData();
		upcMasterData1.setPid("1111");
		upcMasterData1.setNrfColorCode("RedColor1");
		upcMasterData1.setNrfSizeCode("130");
		upcMasterData1.setMsrp("130");
		upcMasterData1.setTaxCode("dummyValue1");
		
		UpcMasterData upcMasterData2 = new UpcMasterData();
		upcMasterData2.setPid("2222");
		upcMasterData2.setNrfColorCode("RedColor2");
		upcMasterData2.setNrfSizeCode("12");
		upcMasterData2.setMsrp("12");
		upcMasterData2.setTaxCode("dummyValue2");
		
		List<PublishUpcData> upcList = TestUtil.generatePublishUpcListBcom(); //processUpcDataJSON(message);
		List<CreateResponseData> createResponseDatas = new ArrayList<CreateResponseData>();
		//Populate additional data to be added in request and publish to IL topic
		CreateResponseData createResponseData1 = new CreateResponseData("1234567890111111113","12345678901","13","1111","RedColor1","130","130","dummyValue1");
		CreateResponseData createResponseData2 = new CreateResponseData("1234567890211111113","12345678902","13","2222","RedColor2","12","12","dummyValue2");
		createResponseDatas.add(createResponseData1);
		createResponseDatas.add(createResponseData2);
		
		String message = "{\"upcs\":[{\"upc\":\"12345678901\",\"productsku\":\"1234567890111111113\",\"opdiv\":\"13\",\"imageupc\":true},{\"upc\":\"12345678902\",\"productsku\":\"1234567890211111113\",\"opdiv\":\"13\",\"imageupc\":false}]}";
		
		JSONObject upcJsonObj = TestUtil.populateCreateRespPubJSONBcom();
		
		when(sqlService.findUpcMasterDataByUpc("12345678901", "13")).thenReturn(upcMasterData1);
		when(sqlService.findUpcMasterDataByUpc("12345678902", "13")).thenReturn(upcMasterData2);
		doNothing().when(sqlService).updateCreateRespList(upcList);
		doNothing().when(sqlService).insertCreateRespAuditList(createResponseDatas);
		
		// when
		createResponseService.processCreateRespFile(message);
		
		// then
		verify(sqlService,times(1)).updateCreateRespList(upcList);
		verify(messagingGatewayBcom,times(1)).sendToPubsubCreateRespBcom(upcJsonObj.toString());
		verify(sqlService, times(1)).insertCreateRespAuditList(createResponseDatas);
	
	}
	
	@Test
	public void testProcessCreateRespFile_MiraklRepositoryException() throws MiraklRepositoryException {
		
		// given
		UpcMasterData upcMasterData1 = new UpcMasterData();
		upcMasterData1.setPid("1111");
		upcMasterData1.setNrfColorCode("RedColor1");
		upcMasterData1.setNrfSizeCode("12");
		upcMasterData1.setMsrp("12");
		upcMasterData1.setTaxCode("dummyValue1");
		
		UpcMasterData upcMasterData2 = new UpcMasterData();
		upcMasterData2.setPid("2222");
		upcMasterData2.setNrfColorCode("RedColor2");
		upcMasterData2.setNrfSizeCode("12");
		upcMasterData2.setMsrp("12");
		upcMasterData2.setTaxCode("dummyValue2");
		
		List<PublishUpcData> upcList = TestUtil.generatePublishUpcList(); //processUpcDataJSON(message);
		List<CreateResponseData> createResponseDatas = new ArrayList<CreateResponseData>();
		//Populate additional data to be added in request and publish to IL topic
		CreateResponseData createResponseData1 = new CreateResponseData("1234567890111111112","12345678901","12","1111","RedColor1","12","12","dummyValue1");
		CreateResponseData createResponseData2 = new CreateResponseData("1234567890211111112","12345678902","12","2222","RedColor2","12","12","dummyValue2");
		createResponseDatas.add(createResponseData1);
		createResponseDatas.add(createResponseData2);
		String message = "{\"upcs\":[{\"upc\":\"12345678901\",\"productsku\":\"1234567890111111112\",\"opdiv\":\"12\",\"imageupc\":true},{\"upc\":\"12345678902\",\"productsku\":\"1234567890211111112\",\"opdiv\":\"12\",\"imageupc\":false}]}";
		doThrow(MiraklRepositoryException.class).when(sqlService).findUpcMasterDataByUpc(any(String.class), any(String.class));
		
		// when
		try {
			createResponseService.processCreateRespFile(message);
		} catch(Exception e) {
			assertTrue(e instanceof Exception,"Exception is expected");
		}
		
		// then
		verify(sqlService,never()).updateCreateRespList(upcList);
	}

}
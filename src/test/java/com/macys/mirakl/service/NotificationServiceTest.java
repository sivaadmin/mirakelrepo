package com.macys.mirakl.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.util.ReflectionTestUtils;

import com.macys.mirakl.config.NotificationConfig;
import com.macys.mirakl.processor.FileProcessor;
import com.macys.mirakl.util.TestResourceInitializer;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {
	
	@InjectMocks
	NotificationService notificationService;

	@Mock
	SQLService sqlService;

	@Mock
	FileProcessor fileProcessor;
	
	@Mock
	NotificationConfig.PubsubOutboundGatewayPrdUpdate messagingGatewayPrdUpdate;
	
	@Mock
	NotificationConfig.PubsubOutboundGatewayPrdImage messagingGatewayPrdImage;
	
	private String pdfStagingPayload;
	private String offerRequestPayload;
	private String imageStagingPayload;

	@BeforeEach
	void setUp() throws Exception {
		ReflectionTestUtils.setField(notificationService, "prdUpdateStagingSub",
				"M.MER.EPC.MIRAKL.PRODUCT.STAGING.ONPERM.ORCH.SUB");
		ReflectionTestUtils.setField(notificationService, "prdImageStagingSub",
				"M.MER.EPC.MIRAKL.PRODUCT.IMAGE.STAGING.SUB");
		pdfStagingPayload = TestResourceInitializer.readJsonFile("request/PdfStagingPayLoad.json");
		offerRequestPayload = TestResourceInitializer.readJsonFile("request/OfferRequestPayLoad.json");
		imageStagingPayload = TestResourceInitializer.readJsonFile("request/ImageStagingPayLoad.json");
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void testProcessPrdUpdateStagingFile() throws Exception {
		// given
		String prdUpdateStagingSub = "M.MER.EPC.MIRAKL.PRODUCT.STAGING.ONPERM.ORCH.SUB";
		String fileName = "MCOM_product_update_pdf_220518_191001.json";
		String objectName = "MCOM/pdf/MCOM_product_update_pdf_220518_191001.json";
		int count = 0;
		when(fileProcessor.getFileName(objectName)).thenReturn(fileName);
		doReturn(count).when(sqlService).findNotificationDetails(any(String.class), any(String.class), any(String.class));
		doNothing().when(sqlService).insertNotificationDetails(any(String.class), any(String.class), any(String.class));
		
		// when
		notificationService.processNotification(pdfStagingPayload, prdUpdateStagingSub, false);		
		
		// then
		verify(sqlService,times(1)).findNotificationDetails(any(String.class), any(String.class), any(String.class));
		verify(sqlService,times(1)).insertNotificationDetails(any(String.class), any(String.class), any(String.class));
		verify(messagingGatewayPrdUpdate,times(1)).sendToPubsubPrdUpdatePub(any(String.class));
		verify(messagingGatewayPrdImage, never()).sendToPubsubPrdImagePub(any(String.class));
	}

	@Test
	void testProcessPrdImageStagingFile() throws Exception {
		// given
		String prdImageStagingSub = "M.MER.EPC.MIRAKL.PRODUCT.IMAGE.STAGING.SUB";
		String fileName = "192114459796_p0.jpg";
		String objectName = "MCOM/product_update_images/192114459796_p0.jpg";
		int count = 0;
		when(fileProcessor.getFileName(objectName)).thenReturn(fileName);
		doReturn(count).when(sqlService).findNotificationDetails(any(String.class), any(String.class), any(String.class));
		doNothing().when(sqlService).insertNotificationDetails(any(String.class), any(String.class), any(String.class));
		
		// when
		notificationService.processNotification(imageStagingPayload, prdImageStagingSub, false);		
		
		// then
		verify(sqlService,times(1)).findNotificationDetails(any(String.class), any(String.class), any(String.class));
		verify(sqlService,times(1)).insertNotificationDetails(any(String.class), any(String.class), any(String.class));
		verify(messagingGatewayPrdUpdate, never()).sendToPubsubPrdUpdatePub(any(String.class));
		verify(messagingGatewayPrdImage,times(1)).sendToPubsubPrdImagePub(any(String.class));
	}
	
	@Test
	void testProcessMultipleNotification() throws Exception {
		// given
		String prdImageStagingSub = "M.MER.EPC.MIRAKL.PRODUCT.IMAGE.STAGING.SUB";
		String fileName = "192114459796_p0.jpg";
		String objectName = "MCOM/product_update_images/192114459796_p0.jpg";
		int count = 1;
		when(fileProcessor.getFileName(objectName)).thenReturn(fileName);
		doReturn(count).when(sqlService).findNotificationDetails(any(String.class), any(String.class), any(String.class));
		
		// when
		notificationService.processNotification(imageStagingPayload, prdImageStagingSub, false);		
		
		// then
		verify(sqlService,times(1)).findNotificationDetails(any(String.class), any(String.class), any(String.class));
	}
	
	@Test
	void testProcessPrdUpdateStagingFileSize_Zero() throws Exception {
		// given
		String payload = "{\r\n"
				+ "  \"kind\": \"storage#object\",\r\n"
				+ "  \"id\": \"mtech-merch-epc-poc-317014-mp_mirakl_orch_product_staging_dev/MCOM/pdf/MCOM_product_update_pdf_220518_191001.json/1659355366413013\",\r\n"
				+ "  \"selfLink\": \"https://www.googleapis.com/storage/v1/b/mtech-merch-epc-poc-317014-mp_mirakl_orch_product_staging_dev/o/MCOM%2Fpdf%2FMCOM_product_update_pdf_220518_191001.json\",\r\n"
				+ "  \"name\": \"MCOM/pdf/MCOM_product_update_pdf_220518_191001.json\",\r\n"
				+ "  \"bucket\": \"mtech-merch-epc-poc-317014-mp_mirakl_orch_product_staging_dev\",\r\n"
				+ "  \"generation\": \"1659355366413013\",\r\n"
				+ "  \"metageneration\": \"1\",\r\n"
				+ "  \"contentType\": \"application/json\",\r\n"
				+ "  \"timeCreated\": \"2022-08-01T12:02:46.531Z\",\r\n"
				+ "  \"updated\": \"2022-08-01T12:02:46.531Z\",\r\n"
				+ "  \"storageClass\": \"STANDARD\",\r\n"
				+ "  \"timeStorageClassUpdated\": \"2022-08-01T12:02:46.531Z\",\r\n"
				+ "  \"size\": \"0\",\r\n"
				+ "  \"md5Hash\": \"DBxC52QJEuRNW+Lh4xjrCQ==\",\r\n"
				+ "  \"mediaLink\": \"https://www.googleapis.com/download/storage/v1/b/mtech-merch-epc-poc-317014-mp_mirakl_orch_product_staging_dev/o/MCOM%2Fpdf%2FMCOM_product_update_pdf_220518_191001.json?generation=1659355366413013&alt=media\",\r\n"
				+ "  \"crc32c\": \"MqkgeQ==\",\r\n"
				+ "  \"etag\": \"CNWlr7nMpfkCEAE=\"\r\n"
				+ "}";
		String prdUpdateStagingSub = "M.MER.EPC.MIRAKL.PRODUCT.STAGING.ONPERM.ORCH.SUB";
		String fileName = "MCOM_product_update_pdf_220518_191001.json";
		String objectName = "MCOM/pdf/MCOM_product_update_pdf_220518_191001.json";
		when(fileProcessor.getFileName(objectName)).thenReturn(fileName);
		
		// when
		notificationService.processNotification(payload, prdUpdateStagingSub, false);		
		
		// then
		verify(sqlService,never()).findNotificationDetails(any(String.class), any(String.class), any(String.class));
		verify(sqlService,never()).insertNotificationDetails(any(String.class), any(String.class), any(String.class));
		verify(messagingGatewayPrdUpdate,never()).sendToPubsubPrdUpdatePub(any(String.class));
	}
	
	@Test
	void testProcessNotification_Exception() throws Exception {
		// given
		String prdUpdateStagingSub = "M.MER.EPC.MIRAKL.PRODUCT.STAGING.ONPERM.ORCH.SUB";
		String fileName = "MCOM_product_update_pdf_220518_191001.json";
		String objectName = "MCOM/pdf/MCOM_product_update_pdf_220518_191001.json";
		when(fileProcessor.getFileName(objectName)).thenReturn(fileName);
		doThrow(EmptyResultDataAccessException.class).when(sqlService).findNotificationDetails(any(String.class), any(String.class), any(String.class));
		
		// when
		Exception thrown = Assertions.assertThrows(Exception.class, () -> {
			notificationService.processNotification(pdfStagingPayload, prdUpdateStagingSub, false);
		});
		
		// then
		Assertions.assertEquals("Exception in processNotification", thrown.getMessage());
		verify(sqlService,times(1)).findNotificationDetails(any(String.class), any(String.class), any(String.class));
		verify(sqlService,never()).insertNotificationDetails(any(String.class), any(String.class), any(String.class));
		verify(messagingGatewayPrdUpdate,never()).sendToPubsubPrdUpdatePub(any(String.class));
	}
	
	@Test
	void testProcessPrdUpdate_OfferReq() throws Exception {
		// given
		String offerReqSub = "M.MER.EPC.MIRAKL.PRODUCT.OFFER.CLOUDORCH.SUB";
		String fileName = "BCOM_offer_220517_191088.json";
		String objectName = "BCOM/offer/BCOM_offer_220517_191088.json";
		int count = 0;
		when(fileProcessor.getFileName(objectName)).thenReturn(fileName);
		doReturn(count).when(sqlService).findNotificationDetails(any(String.class), any(String.class),
				any(String.class));
		doNothing().when(sqlService).insertNotificationDetails(any(String.class), any(String.class), any(String.class));

		// when
		notificationService.processNotification(offerRequestPayload, offerReqSub, true);

		// then
		verify(sqlService, times(1)).findNotificationDetails(any(String.class), any(String.class), any(String.class));
		verify(sqlService, times(1)).insertNotificationDetails(any(String.class), any(String.class), any(String.class));
		verify(messagingGatewayPrdUpdate, never()).sendToPubsubPrdUpdatePub(any(String.class));
		verify(messagingGatewayPrdImage, never()).sendToPubsubPrdImagePub(any(String.class));
	}

}

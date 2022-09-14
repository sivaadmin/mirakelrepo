package com.macys.mirakl.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.macys.mirakl.exception.MiraklRepositoryException;
import com.macys.mirakl.model.OfferIdRequestData;
import com.macys.mirakl.model.OfferIdResponseData;
import com.macys.mirakl.processor.FileProcessor;
import com.macys.mirakl.util.TestUtil;

@ExtendWith(MockitoExtension.class)
class OfferIdServiceTest {

	@InjectMocks
	OfferIdService offerIdService;

	@Mock
	CloudStorageService cloudStorageService;

	@Mock
	SQLService sqlService;

	@Mock
	FileProcessor fileProcessor;
	
	@Mock
	NotificationService notificationService;
	
	String requestSub;
	String responseSub;

	@BeforeEach
	void setUp() throws Exception {
		requestSub = "M.MER.EPC.MIRAKL.PRODUCT.OFFER.CLOUDORCH.SUB";
		responseSub = "projects/mtech-merch-epc-poc-317014/subscriptions/M.MER.EPC.MIRAKL.PRODUCT.OFFER.RESPONSE.CLOUDORCH.SUB";
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	public void testprocessOfferIdReqFile_Success()
			throws Exception {
		// given
		List<OfferIdRequestData> offerIdReqList = TestUtil.generateReqList();
		String fileName = "BCOM_offer_220517_191088.json"; // OfferId update flow request file
		String objectName = "BCOM/offer/BCOM_offer_220517_191088.json";
		String OFFER_FILE = "OFFER_REQ_FILE";
		String payLoad = "{\r\n" + "  \"kind\": \"storage#object\",\r\n"
				+ "  \"id\": \"mtech-merch-epc-poc-317014-il-marketplace-product-offer-poc/BCOM/offer/BCOM_offer_220517_191088.json/1652814902386990\",\r\n"
				+ "  \"selfLink\": \"https://www.googleapis.com/storage/v1/b/mtech-merch-epc-poc-317014-il-marketplace-product-offer-poc/o/BCOM%2Foffer%2FBCOM_offer_220517_191088.json\",\r\n"
				+ "  \"name\": \"BCOM/offer/BCOM_offer_220517_191088.json\",\r\n"
				+ "  \"bucket\": \"mtech-merch-epc-poc-317014-il-marketplace-product-offer-poc\",\r\n"
				+ "  \"generation\": \"1652814902386990\",\r\n" + "  \"metageneration\": \"1\",\r\n"
				+ "  \"contentType\": \"application/json\",\r\n"
				+ "  \"timeCreated\": \"2022-05-17T19:15:02.488Z\",\r\n"
				+ "  \"updated\": \"2022-05-17T19:15:02.488Z\",\r\n" + "  \"storageClass\": \"STANDARD\",\r\n"
				+ "  \"timeStorageClassUpdated\": \"2022-05-17T19:15:02.488Z\",\r\n" + "  \"size\": \"361\",\r\n"
				+ "  \"md5Hash\": \"a/j4WupNd8Loo6nIOLpbWg==\",\r\n"
				+ "  \"mediaLink\": \"https://www.googleapis.com/download/storage/v1/b/mtech-merch-epc-poc-317014-il-marketplace-product-offer-poc/o/BCOM%2Foffer%2FBCOM_offer_220517_191088.json?generation=1652814902386990&alt=media\",\r\n"
				+ "  \"crc32c\": \"wZYa0Q==\",\r\n" + "  \"etag\": \"CK7Ci6mf5/cCEAE=\"\r\n" + "}";
		// OfferId update flow request bucket
		String bucketName = "mtech-merch-epc-poc-317014-il-marketplace-product-offer-poc";
		when(fileProcessor.getFileName(objectName)).thenReturn(fileName);
		when(fileProcessor.getFileOperationForOfferId(objectName)).thenReturn(OFFER_FILE);
		when(notificationService.processNotification(payLoad, requestSub, true)).thenReturn(true);
		when(cloudStorageService.readOfferIdReqFile(bucketName, objectName)).thenReturn(offerIdReqList);
		doNothing().when(sqlService).insertOfferIdAuditList(offerIdReqList, fileName);

		// when
		offerIdService.processOfferIdReqRespFile(payLoad, requestSub);

		// then
		verify(fileProcessor,times(1)).getFileName(objectName);
        verify(fileProcessor,times(1)).getFileOperationForOfferId(objectName);
        verify(notificationService,times(1)).processNotification(payLoad, requestSub, true);
		verify(sqlService, times(1)).insertOfferIdAuditList(offerIdReqList, fileName);
	}
	
	@Test
	public void testprocessOfferIdReqFile_Json_Exception()
			throws Exception {
		// given
		List<OfferIdRequestData> offerIdReqList = TestUtil.generateReqList();
		String fileName = "BCOM_offer_220517_191088.json"; // OfferId update flow request file
		String objectName = "BCOM/offer/BCOM_offer_220517_191088.json";
		String OFFER_FILE = "OFFER_REQ_FILE";
		String payLoad = "{\r\n" + "  \"kind\": \"storage#object\",\r\n"
				+ "  \"id\": \"mtech-merch-epc-poc-317014-il-marketplace-product-offer-poc/BCOM/offer/BCOM_offer_220517_191088.json/1652814902386990\",\r\n"
				+ "  \"selfLink\": \"https://www.googleapis.com/storage/v1/b/mtech-merch-epc-poc-317014-il-marketplace-product-offer-poc/o/BCOM%2Foffer%2FBCOM_offer_220517_191088.json\",\r\n"
				+ "  \"name\": \"BCOM/offer/BCOM_offer_220517_191088.json\",\r\n"
				+ "  \"bucket\": \"mtech-merch-epc-poc-317014-il-marketplace-product-offer-poc\",\r\n"
				+ "  \"generation\": \"1652814902386990\",\r\n" + "  \"metageneration\": \"1\",\r\n"
				+ "  \"contentType\": \"application/json\",\r\n"
				+ "  \"timeCreated\": \"2022-05-17T19:15:02.488Z\",\r\n"
				+ "  \"updated\": \"2022-05-17T19:15:02.488Z\",\r\n" + "  \"storageClass\": \"STANDARD\",\r\n"
				+ "  \"timeStorageClassUpdated\": \"2022-05-17T19:15:02.488Z\",\r\n" + "  \"size\": \"361\",\r\n"
				+ "  \"md5Hash\": \"a/j4WupNd8Loo6nIOLpbWg==\",\r\n"
				+ "  \"mediaLink\": \"https://www.googleapis.com/download/storage/v1/b/mtech-merch-epc-poc-317014-il-marketplace-product-offer-poc/o/BCOM%2Foffer%2FBCOM_offer_220517_191088.json?generation=1652814902386990&alt=media\",\r\n"
				+ "  \"crc32c\": \"wZYa0Q==\",\r\n" + "  \"etag\": \"CK7Ci6mf5/cCEAE=\"\r\n" + "}";
		// OfferId update flow request bucket
		String bucketName = "mtech-merch-epc-poc-317014-il-marketplace-product-offer-poc";
		when(fileProcessor.getFileName(objectName)).thenReturn(fileName);
		when(fileProcessor.getFileOperationForOfferId(objectName)).thenReturn(OFFER_FILE);
		when(notificationService.processNotification(payLoad, requestSub, true)).thenReturn(true);
		doThrow(JSONException.class).when(cloudStorageService).readOfferIdReqFile(bucketName, objectName);
		
		// when
		try {
			offerIdService.processOfferIdReqRespFile(payLoad, requestSub);
		} catch(Exception e) {
			assertTrue(e instanceof Exception,"Exception is expected");
		}

		// then
		verify(fileProcessor,times(1)).getFileName(objectName);
        verify(fileProcessor,times(1)).getFileOperationForOfferId(objectName);
        verify(notificationService,times(1)).processNotification(payLoad, requestSub, true);
		verify(sqlService, never()).insertOfferIdAuditList(offerIdReqList, fileName);
	}
	
	@Test
	public void testprocessOfferIdReqFile_Illegal_Argument_Exception()
			throws Exception {
		// given
		List<OfferIdRequestData> offerIdReqList = TestUtil.generateReqList();
		String fileName = "BCOM_offer_220517_191088.json"; // OfferId update flow request file
		String objectName = "BCOM/offer/BCOM_offer_220517_191088.json";
		String OFFER_FILE = "OFFER_REQ_FILE";
		String payLoad = "{\r\n" + "  \"kind\": \"storage#object\",\r\n"
				+ "  \"id\": \"mtech-merch-epc-poc-317014-il-marketplace-product-offer-poc/BCOM/offer/BCOM_offer_220517_191088.json/1652814902386990\",\r\n"
				+ "  \"selfLink\": \"https://www.googleapis.com/storage/v1/b/mtech-merch-epc-poc-317014-il-marketplace-product-offer-poc/o/BCOM%2Foffer%2FBCOM_offer_220517_191088.json\",\r\n"
				+ "  \"name\": \"BCOM/offer/BCOM_offer_220517_191088.json\",\r\n"
				+ "  \"bucket\": \"mtech-merch-epc-poc-317014-il-marketplace-product-offer-poc\",\r\n"
				+ "  \"generation\": \"1652814902386990\",\r\n" + "  \"metageneration\": \"1\",\r\n"
				+ "  \"contentType\": \"application/json\",\r\n"
				+ "  \"timeCreated\": \"2022-05-17T19:15:02.488Z\",\r\n"
				+ "  \"updated\": \"2022-05-17T19:15:02.488Z\",\r\n" + "  \"storageClass\": \"STANDARD\",\r\n"
				+ "  \"timeStorageClassUpdated\": \"2022-05-17T19:15:02.488Z\",\r\n" + "  \"size\": \"361\",\r\n"
				+ "  \"md5Hash\": \"a/j4WupNd8Loo6nIOLpbWg==\",\r\n"
				+ "  \"mediaLink\": \"https://www.googleapis.com/download/storage/v1/b/mtech-merch-epc-poc-317014-il-marketplace-product-offer-poc/o/BCOM%2Foffer%2FBCOM_offer_220517_191088.json?generation=1652814902386990&alt=media\",\r\n"
				+ "  \"crc32c\": \"wZYa0Q==\",\r\n" + "  \"etag\": \"CK7Ci6mf5/cCEAE=\"\r\n" + "}";
		// OfferId update flow request bucket
		String bucketName = "mtech-merch-epc-poc-317014-il-marketplace-product-offer-poc";
		when(fileProcessor.getFileName(objectName)).thenReturn(fileName);
		when(fileProcessor.getFileOperationForOfferId(objectName)).thenReturn(OFFER_FILE);
		when(notificationService.processNotification(payLoad, requestSub, true)).thenReturn(true);
		doThrow(IllegalArgumentException.class).when(cloudStorageService).readOfferIdReqFile(bucketName, objectName);
		
		// when
		try {
			offerIdService.processOfferIdReqRespFile(payLoad, requestSub);
		} catch(Exception e) {
			assertTrue(e instanceof Exception,"Exception is expected");
		}

		// then
		verify(fileProcessor,times(1)).getFileName(objectName);
        verify(fileProcessor,times(1)).getFileOperationForOfferId(objectName);
        verify(notificationService,times(1)).processNotification(payLoad, requestSub, true);
		verify(sqlService, never()).insertOfferIdAuditList(offerIdReqList, fileName);
	}
	
	@Test
	public void testprocessOfferIdReqFile_IO_Exception()
			throws Exception {
		// given
		List<OfferIdRequestData> offerIdReqList = TestUtil.generateReqList();
		String fileName = "BCOM_offer_220517_191088.json"; // OfferId update flow request file
		String objectName = "BCOM/offer/BCOM_offer_220517_191088.json";
		String OFFER_FILE = "OFFER_REQ_FILE";
		String payLoad = "{\r\n" + "  \"kind\": \"storage#object\",\r\n"
				+ "  \"id\": \"mtech-merch-epc-poc-317014-il-marketplace-product-offer-poc/BCOM/offer/BCOM_offer_220517_191088.json/1652814902386990\",\r\n"
				+ "  \"selfLink\": \"https://www.googleapis.com/storage/v1/b/mtech-merch-epc-poc-317014-il-marketplace-product-offer-poc/o/BCOM%2Foffer%2FBCOM_offer_220517_191088.json\",\r\n"
				+ "  \"name\": \"BCOM/offer/BCOM_offer_220517_191088.json\",\r\n"
				+ "  \"bucket\": \"mtech-merch-epc-poc-317014-il-marketplace-product-offer-poc\",\r\n"
				+ "  \"generation\": \"1652814902386990\",\r\n" + "  \"metageneration\": \"1\",\r\n"
				+ "  \"contentType\": \"application/json\",\r\n"
				+ "  \"timeCreated\": \"2022-05-17T19:15:02.488Z\",\r\n"
				+ "  \"updated\": \"2022-05-17T19:15:02.488Z\",\r\n" + "  \"storageClass\": \"STANDARD\",\r\n"
				+ "  \"timeStorageClassUpdated\": \"2022-05-17T19:15:02.488Z\",\r\n" + "  \"size\": \"361\",\r\n"
				+ "  \"md5Hash\": \"a/j4WupNd8Loo6nIOLpbWg==\",\r\n"
				+ "  \"mediaLink\": \"https://www.googleapis.com/download/storage/v1/b/mtech-merch-epc-poc-317014-il-marketplace-product-offer-poc/o/BCOM%2Foffer%2FBCOM_offer_220517_191088.json?generation=1652814902386990&alt=media\",\r\n"
				+ "  \"crc32c\": \"wZYa0Q==\",\r\n" + "  \"etag\": \"CK7Ci6mf5/cCEAE=\"\r\n" + "}";
		// OfferId update flow request bucket
		String bucketName = "mtech-merch-epc-poc-317014-il-marketplace-product-offer-poc";
		when(fileProcessor.getFileName(objectName)).thenReturn(fileName);
		when(fileProcessor.getFileOperationForOfferId(objectName)).thenReturn(OFFER_FILE);
		when(notificationService.processNotification(payLoad, requestSub, true)).thenReturn(true);
		doThrow(IOException.class).when(cloudStorageService).readOfferIdReqFile(bucketName, objectName);
		
		// when
		try {
			offerIdService.processOfferIdReqRespFile(payLoad, requestSub);
		} catch(Exception e) {
			assertTrue(e instanceof Exception,"Exception is expected");
		}

		// then
		verify(fileProcessor,times(1)).getFileName(objectName);
        verify(fileProcessor,times(1)).getFileOperationForOfferId(objectName);
        verify(notificationService,times(1)).processNotification(payLoad, requestSub, true);
		verify(sqlService, never()).insertOfferIdAuditList(offerIdReqList, fileName);
	}

	@Test
	public void testprocessOfferIdReqFile_IncorrectFilePath()
			throws IllegalArgumentException, JSONException, IOException, MiraklRepositoryException {
		// given
		String fileName = "BCOM_offer_220517_191088.json"; // OfferId update flow request file
		String objectName = "BCOM/offer_response/BCOM_offer_220517_191088.json";
		String payLoad = "{\r\n" + "  \"kind\": \"storage#object\",\r\n"
				+ "  \"id\": \"mtech-merch-epc-poc-317014-il-marketplace-product-offer-poc/BCOM/offer_response/BCOM_offer_220517_191088.json/1652814902386990\",\r\n"
				+ "  \"selfLink\": \"https://www.googleapis.com/storage/v1/b/mtech-merch-epc-poc-317014-il-marketplace-product-offer-poc/o/BCOM%2Foffer%2FBCOM_offer_220517_191088.json\",\r\n"
				+ "  \"name\": \"BCOM/offer_response/BCOM_offer_220517_191088.json\",\r\n"
				+ "  \"bucket\": \"mtech-merch-epc-poc-317014-il-marketplace-product-offer-poc\",\r\n"
				+ "  \"generation\": \"1652814902386990\",\r\n" + "  \"metageneration\": \"1\",\r\n"
				+ "  \"contentType\": \"application/json\",\r\n"
				+ "  \"timeCreated\": \"2022-05-17T19:15:02.488Z\",\r\n"
				+ "  \"updated\": \"2022-05-17T19:15:02.488Z\",\r\n" + "  \"storageClass\": \"STANDARD\",\r\n"
				+ "  \"timeStorageClassUpdated\": \"2022-05-17T19:15:02.488Z\",\r\n" + "  \"size\": \"361\",\r\n"
				+ "  \"md5Hash\": \"a/j4WupNd8Loo6nIOLpbWg==\",\r\n"
				+ "  \"mediaLink\": \"https://www.googleapis.com/download/storage/v1/b/mtech-merch-epc-poc-317014-il-marketplace-product-offer-poc/o/BCOM%2Foffer%2FBCOM_offer_220517_191088.json?generation=1652814902386990&alt=media\",\r\n"
				+ "  \"crc32c\": \"wZYa0Q==\",\r\n" + "  \"etag\": \"CK7Ci6mf5/cCEAE=\"\r\n" + "}";
		
		when(fileProcessor.getFileName(objectName)).thenReturn(fileName);
		when(fileProcessor.getFileOperationForOfferId(objectName)).thenReturn("WRONG_FILE");
		
		// when
		offerIdService.processOfferIdReqRespFile(payLoad, requestSub);

		// then
		assertNotEquals("BCOM/offer/BCOM_offer_220517_191088.json", objectName,"File path is not proper for OfferId request flow");
	}

	@Test
	public void testprocessOfferIdReqFile_EmptyReqJson()
			throws Exception {
		// given
		List<OfferIdRequestData> offerIdReqList = new ArrayList<>();
		String fileName = "BCOM_offer_220517_191088.json"; // OfferId update flow request file
		String objectName = "BCOM/offer/BCOM_offer_220517_191088.json";
		String payLoad = "{\r\n" + "  \"kind\": \"storage#object\",\r\n"
				+ "  \"id\": \"mtech-merch-epc-poc-317014-il-marketplace-product-offer-poc/BCOM/offer/BCOM_offer_220517_191088.json/1652814902386990\",\r\n"
				+ "  \"selfLink\": \"https://www.googleapis.com/storage/v1/b/mtech-merch-epc-poc-317014-il-marketplace-product-offer-poc/o/BCOM%2Foffer%2FBCOM_offer_220517_191088.json\",\r\n"
				+ "  \"name\": \"BCOM/offer/BCOM_offer_220517_191088.json\",\r\n"
				+ "  \"bucket\": \"mtech-merch-epc-poc-317014-il-marketplace-product-offer-poc\",\r\n"
				+ "  \"generation\": \"1652814902386990\",\r\n" + "  \"metageneration\": \"1\",\r\n"
				+ "  \"contentType\": \"application/json\",\r\n"
				+ "  \"timeCreated\": \"2022-05-17T19:15:02.488Z\",\r\n"
				+ "  \"updated\": \"2022-05-17T19:15:02.488Z\",\r\n" + "  \"storageClass\": \"STANDARD\",\r\n"
				+ "  \"timeStorageClassUpdated\": \"2022-05-17T19:15:02.488Z\",\r\n" + "  \"size\": \"361\",\r\n"
				+ "  \"md5Hash\": \"a/j4WupNd8Loo6nIOLpbWg==\",\r\n"
				+ "  \"mediaLink\": \"https://www.googleapis.com/download/storage/v1/b/mtech-merch-epc-poc-317014-il-marketplace-product-offer-poc/o/BCOM%2Foffer%2FBCOM_offer_220517_191088.json?generation=1652814902386990&alt=media\",\r\n"
				+ "  \"crc32c\": \"wZYa0Q==\",\r\n" + "  \"etag\": \"CK7Ci6mf5/cCEAE=\"\r\n" + "}";
		// OfferId update flow request bucket
		String bucketName = "mtech-merch-epc-poc-317014-il-marketplace-product-offer-poc";
		when(fileProcessor.getFileName(objectName)).thenReturn(fileName);
		when(fileProcessor.getFileOperationForOfferId(objectName)).thenReturn("OFFER_REQ_FILE");
		when(notificationService.processNotification(payLoad, requestSub, true)).thenReturn(true);
		when(cloudStorageService.readOfferIdReqFile(bucketName, objectName)).thenReturn(offerIdReqList);

		// when
		offerIdService.processOfferIdReqRespFile(payLoad, requestSub);

		// then
		verify(fileProcessor,times(1)).getFileName(objectName);
        verify(fileProcessor,times(1)).getFileOperationForOfferId(objectName);
        verify(notificationService,times(1)).processNotification(payLoad, requestSub, true);
		assertEquals( 0, offerIdReqList.size(),"No data in OfferId request JSON file");
	}

	@Test
	public void testprocessOfferIdRespFile_Success()
			throws Exception {
		// given
		List<OfferIdResponseData> offerIdRespList = TestUtil.generateRespList();
		String fileName = "BCOM_offer_220517_191088_1.json"; // OfferId update flow response file
		String objectName = "BCOM/offer_response/BCOM_offer_220517_191088_1.json";
		String payLoad = "{\r\n" + "  \"kind\": \"storage#object\",\r\n"
				+ "  \"id\": \"mtech-merch-epc-poc-317014-il-mp-product-offer-response-poc/BCOM/offer_response/BCOM_offer_220517_191088_1.json/1652815227907248\",\r\n"
				+ "  \"selfLink\": \"https://www.googleapis.com/storage/v1/b/mtech-merch-epc-poc-317014-il-mp-product-offer-response-poc/o/BCOM%2Foffer_response%2FBCOM_offer_220517_191088_1.json\",\r\n"
				+ "  \"name\": \"BCOM/offer_response/BCOM_offer_220517_191088_1.json\",\r\n"
				+ "  \"bucket\": \"mtech-merch-epc-poc-317014-il-mp-product-offer-response-poc\",\r\n"
				+ "  \"generation\": \"1652815227907248\",\r\n" + "  \"metageneration\": \"1\",\r\n"
				+ "  \"contentType\": \"application/json\",\r\n"
				+ "  \"timeCreated\": \"2022-05-17T19:20:28.043Z\",\r\n"
				+ "  \"updated\": \"2022-05-17T19:20:28.043Z\",\r\n" + "  \"storageClass\": \"STANDARD\",\r\n"
				+ "  \"timeStorageClassUpdated\": \"2022-05-17T19:20:28.043Z\",\r\n" + "  \"size\": \"422\",\r\n"
				+ "  \"md5Hash\": \"hVJFTBuf7JRFIset910R1g==\",\r\n"
				+ "  \"mediaLink\": \"https://www.googleapis.com/download/storage/v1/b/mtech-merch-epc-poc-317014-il-mp-product-offer-response-poc/o/BCOM%2Foffer_response%2FBCOM_offer_220517_191088_1.json?generation=1652815227907248&alt=media\",\r\n"
				+ "  \"crc32c\": \"svKmIA==\",\r\n" + "  \"etag\": \"CLDZp8Sg5/cCEAE=\"\r\n" + "}";
		// OfferId update flow response bucket
		String bucketName = "mtech-merch-epc-poc-317014-il-mp-product-offer-response-poc";
		when(fileProcessor.getFileName(objectName)).thenReturn(fileName);
		when(fileProcessor.getFileOperationForOfferId(objectName)).thenReturn("OFFER_RESP_FILE");
		when(notificationService.processNotification(payLoad, responseSub, true)).thenReturn(true);
		when(cloudStorageService.readOfferIdRespFile(bucketName, objectName)).thenReturn(offerIdRespList);
		doNothing().when(sqlService).updateOfferIdAuditList(offerIdRespList);
		doNothing().when(sqlService).updateOfferIdMasterList(offerIdRespList);

		// when
		offerIdService.processOfferIdReqRespFile(payLoad, responseSub);

		// then
		verify(fileProcessor,times(1)).getFileName(objectName);
        verify(fileProcessor,times(1)).getFileOperationForOfferId(objectName);
        verify(notificationService,times(1)).processNotification(payLoad, responseSub, true);
		verify(sqlService, times(1)).updateOfferIdAuditList(offerIdRespList);
		verify(sqlService, times(1)).updateOfferIdMasterList(offerIdRespList);
	}

	@Test
	public void testprocessOfferIdRespFile_IncorrectFilePath()
			throws MiraklRepositoryException, IllegalArgumentException, JSONException, IOException {
		// given
		String fileName = "BCOM_offer_220517_191088_1.json"; // OfferId update flow response file
		String objectName = "BCOM/offer_resp/BCOM_offer_220517_191088_1.json";
		String payLoad = "{\r\n" + "  \"kind\": \"storage#object\",\r\n"
				+ "  \"id\": \"mtech-merch-epc-poc-317014-il-mp-product-offer-response-poc/BCOM/offer_resp/BCOM_offer_220517_191088_1.json/1652815227907248\",\r\n"
				+ "  \"selfLink\": \"https://www.googleapis.com/storage/v1/b/mtech-merch-epc-poc-317014-il-mp-product-offer-response-poc/o/BCOM%2Foffer_response%2FBCOM_offer_220517_191088_1.json\",\r\n"
				+ "  \"name\": \"BCOM/offer_resp/BCOM_offer_220517_191088_1.json\",\r\n"
				+ "  \"bucket\": \"mtech-merch-epc-poc-317014-il-mp-product-offer-response-poc\",\r\n"
				+ "  \"generation\": \"1652815227907248\",\r\n" + "  \"metageneration\": \"1\",\r\n"
				+ "  \"contentType\": \"application/json\",\r\n"
				+ "  \"timeCreated\": \"2022-05-17T19:20:28.043Z\",\r\n"
				+ "  \"updated\": \"2022-05-17T19:20:28.043Z\",\r\n" + "  \"storageClass\": \"STANDARD\",\r\n"
				+ "  \"timeStorageClassUpdated\": \"2022-05-17T19:20:28.043Z\",\r\n" + "  \"size\": \"422\",\r\n"
				+ "  \"md5Hash\": \"hVJFTBuf7JRFIset910R1g==\",\r\n"
				+ "  \"mediaLink\": \"https://www.googleapis.com/download/storage/v1/b/mtech-merch-epc-poc-317014-il-mp-product-offer-response-poc/o/BCOM%2Foffer_response%2FBCOM_offer_220517_191088_1.json?generation=1652815227907248&alt=media\",\r\n"
				+ "  \"crc32c\": \"svKmIA==\",\r\n" + "  \"etag\": \"CLDZp8Sg5/cCEAE=\"\r\n" + "}";
		
		when(fileProcessor.getFileName(objectName)).thenReturn(fileName);

		// when
		offerIdService.processOfferIdReqRespFile(payLoad, responseSub);

		// then
		assertNotEquals("BCOM/offer_response/BCOM_offer_220517_191088_1.json", objectName,
				"File path is not proper for OfferId response flow");
	}
	
	@Test
	public void testprocessOfferIdRespFile_EmptyRespJson()
			throws Exception {
		// given
		List<OfferIdResponseData> offerIdRespList = new ArrayList<>();
		String fileName = "BCOM_offer_220517_191088_1.json"; // OfferId update flow response file
		String objectName = "BCOM/offer_response/BCOM_offer_220517_191088_1.json";
		String payLoad = "{\r\n" + "  \"kind\": \"storage#object\",\r\n"
				+ "  \"id\": \"mtech-merch-epc-poc-317014-il-mp-product-offer-response-poc/BCOM/offer_response/BCOM_offer_220517_191088_1.json/1652815227907248\",\r\n"
				+ "  \"selfLink\": \"https://www.googleapis.com/storage/v1/b/mtech-merch-epc-poc-317014-il-mp-product-offer-response-poc/o/BCOM%2Foffer_response%2FBCOM_offer_220517_191088_1.json\",\r\n"
				+ "  \"name\": \"BCOM/offer_response/BCOM_offer_220517_191088_1.json\",\r\n"
				+ "  \"bucket\": \"mtech-merch-epc-poc-317014-il-mp-product-offer-response-poc\",\r\n"
				+ "  \"generation\": \"1652815227907248\",\r\n" + "  \"metageneration\": \"1\",\r\n"
				+ "  \"contentType\": \"application/json\",\r\n"
				+ "  \"timeCreated\": \"2022-05-17T19:20:28.043Z\",\r\n"
				+ "  \"updated\": \"2022-05-17T19:20:28.043Z\",\r\n" + "  \"storageClass\": \"STANDARD\",\r\n"
				+ "  \"timeStorageClassUpdated\": \"2022-05-17T19:20:28.043Z\",\r\n" + "  \"size\": \"422\",\r\n"
				+ "  \"md5Hash\": \"hVJFTBuf7JRFIset910R1g==\",\r\n"
				+ "  \"mediaLink\": \"https://www.googleapis.com/download/storage/v1/b/mtech-merch-epc-poc-317014-il-mp-product-offer-response-poc/o/BCOM%2Foffer_response%2FBCOM_offer_220517_191088_1.json?generation=1652815227907248&alt=media\",\r\n"
				+ "  \"crc32c\": \"svKmIA==\",\r\n" + "  \"etag\": \"CLDZp8Sg5/cCEAE=\"\r\n" + "}";
		// OfferId update flow response bucket
		String bucketName = "mtech-merch-epc-poc-317014-il-mp-product-offer-response-poc";
		when(fileProcessor.getFileName(objectName)).thenReturn(fileName);
		when(fileProcessor.getFileOperationForOfferId(objectName)).thenReturn("OFFER_RESP_FILE");
		when(notificationService.processNotification(payLoad, responseSub, true)).thenReturn(true);
		when(cloudStorageService.readOfferIdRespFile(bucketName, objectName)).thenReturn(offerIdRespList);

		// when
		offerIdService.processOfferIdReqRespFile(payLoad, responseSub);

		// then
		verify(fileProcessor,times(1)).getFileName(objectName);
        verify(fileProcessor,times(1)).getFileOperationForOfferId(objectName);
        verify(notificationService,times(1)).processNotification(payLoad, responseSub, true);
		assertEquals( 0, offerIdRespList.size(),"No data in OfferId response JSON file");
	}
	
	@Test
	public void testprocessOfferIdRespFile_DB_Exception()
			throws Exception {
		// given
		List<OfferIdResponseData> offerIdRespList = TestUtil.generateRespList();
		String fileName = "BCOM_offer_220517_191088_1.json"; // OfferId update flow response file
		String objectName = "BCOM/offer_response/BCOM_offer_220517_191088_1.json";
		String payLoad = "{\r\n" + "  \"kind\": \"storage#object\",\r\n"
				+ "  \"id\": \"mtech-merch-epc-poc-317014-il-mp-product-offer-response-poc/BCOM/offer_response/BCOM_offer_220517_191088_1.json/1652815227907248\",\r\n"
				+ "  \"selfLink\": \"https://www.googleapis.com/storage/v1/b/mtech-merch-epc-poc-317014-il-mp-product-offer-response-poc/o/BCOM%2Foffer_response%2FBCOM_offer_220517_191088_1.json\",\r\n"
				+ "  \"name\": \"BCOM/offer_response/BCOM_offer_220517_191088_1.json\",\r\n"
				+ "  \"bucket\": \"mtech-merch-epc-poc-317014-il-mp-product-offer-response-poc\",\r\n"
				+ "  \"generation\": \"1652815227907248\",\r\n" + "  \"metageneration\": \"1\",\r\n"
				+ "  \"contentType\": \"application/json\",\r\n"
				+ "  \"timeCreated\": \"2022-05-17T19:20:28.043Z\",\r\n"
				+ "  \"updated\": \"2022-05-17T19:20:28.043Z\",\r\n" + "  \"storageClass\": \"STANDARD\",\r\n"
				+ "  \"timeStorageClassUpdated\": \"2022-05-17T19:20:28.043Z\",\r\n" + "  \"size\": \"422\",\r\n"
				+ "  \"md5Hash\": \"hVJFTBuf7JRFIset910R1g==\",\r\n"
				+ "  \"mediaLink\": \"https://www.googleapis.com/download/storage/v1/b/mtech-merch-epc-poc-317014-il-mp-product-offer-response-poc/o/BCOM%2Foffer_response%2FBCOM_offer_220517_191088_1.json?generation=1652815227907248&alt=media\",\r\n"
				+ "  \"crc32c\": \"svKmIA==\",\r\n" + "  \"etag\": \"CLDZp8Sg5/cCEAE=\"\r\n" + "}";
		// OfferId update flow response bucket
		String bucketName = "mtech-merch-epc-poc-317014-il-mp-product-offer-response-poc";
		when(fileProcessor.getFileName(objectName)).thenReturn(fileName);
		when(fileProcessor.getFileOperationForOfferId(objectName)).thenReturn("OFFER_RESP_FILE");
		when(notificationService.processNotification(payLoad, responseSub, true)).thenReturn(true);
		when(cloudStorageService.readOfferIdRespFile(bucketName, objectName)).thenReturn(offerIdRespList);
		doThrow(MiraklRepositoryException.class).when(sqlService).updateOfferIdAuditList(offerIdRespList);

		// when
		try {
			offerIdService.processOfferIdReqRespFile(payLoad, responseSub);
		} catch(Exception e) {
			assertTrue(e instanceof Exception,"Exception is expected");
		}

		// then
		verify(fileProcessor,times(1)).getFileName(objectName);
        verify(fileProcessor,times(1)).getFileOperationForOfferId(objectName);
        verify(notificationService,times(1)).processNotification(payLoad, responseSub, true);
		verify(sqlService, times(1)).updateOfferIdAuditList(offerIdRespList);
		verify(sqlService, never()).updateOfferIdMasterList(offerIdRespList);

	}
	
	@Test
	public void testprocessOfferIdRespFile_Json_Exception()
			throws Exception {
		// given
		List<OfferIdResponseData> offerIdRespList = TestUtil.generateRespList();
		String fileName = "BCOM_offer_220517_191088_1.json"; // OfferId update flow response file
		String objectName = "BCOM/offer_response/BCOM_offer_220517_191088_1.json";
		String payLoad = "{\r\n" + "  \"kind\": \"storage#object\",\r\n"
				+ "  \"id\": \"mtech-merch-epc-poc-317014-il-mp-product-offer-response-poc/BCOM/offer_response/BCOM_offer_220517_191088_1.json/1652815227907248\",\r\n"
				+ "  \"selfLink\": \"https://www.googleapis.com/storage/v1/b/mtech-merch-epc-poc-317014-il-mp-product-offer-response-poc/o/BCOM%2Foffer_response%2FBCOM_offer_220517_191088_1.json\",\r\n"
				+ "  \"name\": \"BCOM/offer_response/BCOM_offer_220517_191088_1.json\",\r\n"
				+ "  \"bucket\": \"mtech-merch-epc-poc-317014-il-mp-product-offer-response-poc\",\r\n"
				+ "  \"generation\": \"1652815227907248\",\r\n" + "  \"metageneration\": \"1\",\r\n"
				+ "  \"contentType\": \"application/json\",\r\n"
				+ "  \"timeCreated\": \"2022-05-17T19:20:28.043Z\",\r\n"
				+ "  \"updated\": \"2022-05-17T19:20:28.043Z\",\r\n" + "  \"storageClass\": \"STANDARD\",\r\n"
				+ "  \"timeStorageClassUpdated\": \"2022-05-17T19:20:28.043Z\",\r\n" + "  \"size\": \"422\",\r\n"
				+ "  \"md5Hash\": \"hVJFTBuf7JRFIset910R1g==\",\r\n"
				+ "  \"mediaLink\": \"https://www.googleapis.com/download/storage/v1/b/mtech-merch-epc-poc-317014-il-mp-product-offer-response-poc/o/BCOM%2Foffer_response%2FBCOM_offer_220517_191088_1.json?generation=1652815227907248&alt=media\",\r\n"
				+ "  \"crc32c\": \"svKmIA==\",\r\n" + "  \"etag\": \"CLDZp8Sg5/cCEAE=\"\r\n" + "}";
		// OfferId update flow response bucket
		String bucketName = "mtech-merch-epc-poc-317014-il-mp-product-offer-response-poc";
		when(fileProcessor.getFileName(objectName)).thenReturn(fileName);
		when(fileProcessor.getFileOperationForOfferId(objectName)).thenReturn("OFFER_RESP_FILE");
		when(notificationService.processNotification(payLoad, responseSub, true)).thenReturn(true);
		doThrow(JSONException.class).when(cloudStorageService).readOfferIdRespFile(bucketName, objectName);
		
		// when
		try {
			offerIdService.processOfferIdReqRespFile(payLoad, responseSub);
		} catch(Exception e) {
			assertTrue(e instanceof Exception,"Exception is expected");
		}

		// then
		verify(fileProcessor,times(1)).getFileName(objectName);
        verify(fileProcessor,times(1)).getFileOperationForOfferId(objectName);
        verify(notificationService,times(1)).processNotification(payLoad, responseSub, true);
		verify(sqlService, never()).updateOfferIdAuditList(offerIdRespList);
		verify(sqlService, never()).updateOfferIdMasterList(offerIdRespList);
	}
	
	@Test
	public void testprocessOfferIdRespFile_Illegal_Argument_Exception()
			throws Exception {
		// given
		List<OfferIdResponseData> offerIdRespList = TestUtil.generateRespList();
		String fileName = "BCOM_offer_220517_191088_1.json"; // OfferId update flow response file
		String objectName = "BCOM/offer_response/BCOM_offer_220517_191088_1.json";
		String payLoad = "{\r\n" + "  \"kind\": \"storage#object\",\r\n"
				+ "  \"id\": \"mtech-merch-epc-poc-317014-il-mp-product-offer-response-poc/BCOM/offer_response/BCOM_offer_220517_191088_1.json/1652815227907248\",\r\n"
				+ "  \"selfLink\": \"https://www.googleapis.com/storage/v1/b/mtech-merch-epc-poc-317014-il-mp-product-offer-response-poc/o/BCOM%2Foffer_response%2FBCOM_offer_220517_191088_1.json\",\r\n"
				+ "  \"name\": \"BCOM/offer_response/BCOM_offer_220517_191088_1.json\",\r\n"
				+ "  \"bucket\": \"mtech-merch-epc-poc-317014-il-mp-product-offer-response-poc\",\r\n"
				+ "  \"generation\": \"1652815227907248\",\r\n" + "  \"metageneration\": \"1\",\r\n"
				+ "  \"contentType\": \"application/json\",\r\n"
				+ "  \"timeCreated\": \"2022-05-17T19:20:28.043Z\",\r\n"
				+ "  \"updated\": \"2022-05-17T19:20:28.043Z\",\r\n" + "  \"storageClass\": \"STANDARD\",\r\n"
				+ "  \"timeStorageClassUpdated\": \"2022-05-17T19:20:28.043Z\",\r\n" + "  \"size\": \"422\",\r\n"
				+ "  \"md5Hash\": \"hVJFTBuf7JRFIset910R1g==\",\r\n"
				+ "  \"mediaLink\": \"https://www.googleapis.com/download/storage/v1/b/mtech-merch-epc-poc-317014-il-mp-product-offer-response-poc/o/BCOM%2Foffer_response%2FBCOM_offer_220517_191088_1.json?generation=1652815227907248&alt=media\",\r\n"
				+ "  \"crc32c\": \"svKmIA==\",\r\n" + "  \"etag\": \"CLDZp8Sg5/cCEAE=\"\r\n" + "}";
		// OfferId update flow response bucket
		String bucketName = "mtech-merch-epc-poc-317014-il-mp-product-offer-response-poc";
		when(fileProcessor.getFileName(objectName)).thenReturn(fileName);
		when(fileProcessor.getFileOperationForOfferId(objectName)).thenReturn("OFFER_RESP_FILE");
		when(notificationService.processNotification(payLoad, responseSub, true)).thenReturn(true);
		doThrow(IllegalArgumentException.class).when(cloudStorageService).readOfferIdRespFile(bucketName, objectName);
		
		// when
		try {
			offerIdService.processOfferIdReqRespFile(payLoad, responseSub);
		} catch(Exception e) {
			assertTrue(e instanceof Exception,"Exception is expected");
		}

		// then
		verify(fileProcessor,times(1)).getFileName(objectName);
        verify(fileProcessor,times(1)).getFileOperationForOfferId(objectName);
        verify(notificationService,times(1)).processNotification(payLoad, responseSub, true);
		verify(sqlService, never()).updateOfferIdAuditList(offerIdRespList);
		verify(sqlService, never()).updateOfferIdMasterList(offerIdRespList);
	}
	
	@Test
	public void testprocessOfferIdRespFile_IO_Exception()
			throws Exception {
		// given
		List<OfferIdResponseData> offerIdRespList = TestUtil.generateRespList();
		String fileName = "BCOM_offer_220517_191088_1.json"; // OfferId update flow response file
		String objectName = "BCOM/offer_response/BCOM_offer_220517_191088_1.json";
		String payLoad = "{\r\n" + "  \"kind\": \"storage#object\",\r\n"
				+ "  \"id\": \"mtech-merch-epc-poc-317014-il-mp-product-offer-response-poc/BCOM/offer_response/BCOM_offer_220517_191088_1.json/1652815227907248\",\r\n"
				+ "  \"selfLink\": \"https://www.googleapis.com/storage/v1/b/mtech-merch-epc-poc-317014-il-mp-product-offer-response-poc/o/BCOM%2Foffer_response%2FBCOM_offer_220517_191088_1.json\",\r\n"
				+ "  \"name\": \"BCOM/offer_response/BCOM_offer_220517_191088_1.json\",\r\n"
				+ "  \"bucket\": \"mtech-merch-epc-poc-317014-il-mp-product-offer-response-poc\",\r\n"
				+ "  \"generation\": \"1652815227907248\",\r\n" + "  \"metageneration\": \"1\",\r\n"
				+ "  \"contentType\": \"application/json\",\r\n"
				+ "  \"timeCreated\": \"2022-05-17T19:20:28.043Z\",\r\n"
				+ "  \"updated\": \"2022-05-17T19:20:28.043Z\",\r\n" + "  \"storageClass\": \"STANDARD\",\r\n"
				+ "  \"timeStorageClassUpdated\": \"2022-05-17T19:20:28.043Z\",\r\n" + "  \"size\": \"422\",\r\n"
				+ "  \"md5Hash\": \"hVJFTBuf7JRFIset910R1g==\",\r\n"
				+ "  \"mediaLink\": \"https://www.googleapis.com/download/storage/v1/b/mtech-merch-epc-poc-317014-il-mp-product-offer-response-poc/o/BCOM%2Foffer_response%2FBCOM_offer_220517_191088_1.json?generation=1652815227907248&alt=media\",\r\n"
				+ "  \"crc32c\": \"svKmIA==\",\r\n" + "  \"etag\": \"CLDZp8Sg5/cCEAE=\"\r\n" + "}";
		// OfferId update flow response bucket
		String bucketName = "mtech-merch-epc-poc-317014-il-mp-product-offer-response-poc";
		when(fileProcessor.getFileName(objectName)).thenReturn(fileName);
		when(fileProcessor.getFileOperationForOfferId(objectName)).thenReturn("OFFER_RESP_FILE");
		when(notificationService.processNotification(payLoad, responseSub, true)).thenReturn(true);
		doThrow(IOException.class).when(cloudStorageService).readOfferIdRespFile(bucketName, objectName);
		
		// when
		try {
			offerIdService.processOfferIdReqRespFile(payLoad, responseSub);
		} catch(Exception e) {
			assertTrue(e instanceof Exception,"Exception is expected");
		}

		// then
		verify(fileProcessor,times(1)).getFileName(objectName);
        verify(fileProcessor,times(1)).getFileOperationForOfferId(objectName);
        verify(notificationService,times(1)).processNotification(payLoad, responseSub, true);
		verify(sqlService, never()).updateOfferIdAuditList(offerIdRespList);
		verify(sqlService, never()).updateOfferIdMasterList(offerIdRespList);
	}
	
	
	@Test
	public void testprocessOfferIdRespFile_MultipleNotification()
			throws Exception {
		// given
		String fileName = "BCOM_offer_220517_191088_1.json"; // OfferId update flow response file
		String objectName = "BCOM/offer_response/BCOM_offer_220517_191088_1.json";
		String payLoad = "{\r\n" + "  \"kind\": \"storage#object\",\r\n"
				+ "  \"id\": \"mtech-merch-epc-poc-317014-il-mp-product-offer-response-poc/BCOM/offer_response/BCOM_offer_220517_191088_1.json/1652815227907248\",\r\n"
				+ "  \"selfLink\": \"https://www.googleapis.com/storage/v1/b/mtech-merch-epc-poc-317014-il-mp-product-offer-response-poc/o/BCOM%2Foffer_response%2FBCOM_offer_220517_191088_1.json\",\r\n"
				+ "  \"name\": \"BCOM/offer_response/BCOM_offer_220517_191088_1.json\",\r\n"
				+ "  \"bucket\": \"mtech-merch-epc-poc-317014-il-mp-product-offer-response-poc\",\r\n"
				+ "  \"generation\": \"1652815227907248\",\r\n" + "  \"metageneration\": \"1\",\r\n"
				+ "  \"contentType\": \"application/json\",\r\n"
				+ "  \"timeCreated\": \"2022-05-17T19:20:28.043Z\",\r\n"
				+ "  \"updated\": \"2022-05-17T19:20:28.043Z\",\r\n" + "  \"storageClass\": \"STANDARD\",\r\n"
				+ "  \"timeStorageClassUpdated\": \"2022-05-17T19:20:28.043Z\",\r\n" + "  \"size\": \"422\",\r\n"
				+ "  \"md5Hash\": \"hVJFTBuf7JRFIset910R1g==\",\r\n"
				+ "  \"mediaLink\": \"https://www.googleapis.com/download/storage/v1/b/mtech-merch-epc-poc-317014-il-mp-product-offer-response-poc/o/BCOM%2Foffer_response%2FBCOM_offer_220517_191088_1.json?generation=1652815227907248&alt=media\",\r\n"
				+ "  \"crc32c\": \"svKmIA==\",\r\n" + "  \"etag\": \"CLDZp8Sg5/cCEAE=\"\r\n" + "}";
		when(fileProcessor.getFileName(objectName)).thenReturn(fileName);
		when(fileProcessor.getFileOperationForOfferId(objectName)).thenReturn("OFFER_RESP_FILE");
		when(notificationService.processNotification(payLoad, responseSub, true)).thenReturn(false);
		
		// when
		offerIdService.processOfferIdReqRespFile(payLoad, responseSub);

		// then
		verify(fileProcessor,times(1)).getFileName(objectName);
        verify(fileProcessor,times(1)).getFileOperationForOfferId(objectName);
        verify(notificationService,times(1)).processNotification(payLoad, responseSub, true);
	}

}

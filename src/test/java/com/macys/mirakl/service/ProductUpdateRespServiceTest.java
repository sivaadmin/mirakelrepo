package com.macys.mirakl.service;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
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
import org.springframework.dao.EmptyResultDataAccessException;

import com.macys.mirakl.exception.MiraklRepositoryException;
import com.macys.mirakl.model.FacetMasterData;
import com.macys.mirakl.model.FacetUpdateRespData;
import com.macys.mirakl.model.PdfMasterData;
import com.macys.mirakl.model.ProductUpdateRespData;
import com.macys.mirakl.model.StellaMasterData;
import com.macys.mirakl.processor.FileProcessor;

@ExtendWith(MockitoExtension.class)
class ProductUpdateRespServiceTest {

	@InjectMocks
	ProductUpdateRespService productUpdateRespService;

	@Mock
	CloudStorageService cloudStorageService;

	@Mock
	SQLService sqlService;

	@Mock
	FileProcessor fileProcessor;
	
	@Mock
	NotificationService notificationService;
	
	String prdUpdateRespSub;
	String prdFacetRespSub;

	@BeforeEach
	void setUp() throws Exception {
		prdUpdateRespSub = "projects/mtech-merch-epc-poc-317014/subscriptions/M.MER.EPC.MIRAKL.PRODUCT.RESPONSE.SUB";
		prdFacetRespSub = "M.MER.EPC.MIRAKL.PRODUCT.RESPONSE.STAGING.SUB";
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void testProcessPrdUpdateRespFile_PDF_Success()
			throws Exception {
		// Given
		List<ProductUpdateRespData> prdUpdateRespList = new ArrayList<>();
		ProductUpdateRespData productUpdateRespData1 = new ProductUpdateRespData("191019881001", "12", "200", "Success",
				"MCOM_product_update_220517_191001.json");
		prdUpdateRespList.add(productUpdateRespData1);
		String payLoad = "{\r\n" + "  \"kind\": \"storage#object\",\r\n"
				+ "  \"id\": \"mtech-merch-epc-poc-317014-mirakl_response/MCOM/product_update_response/MCOM_product_update_pdf_220517_191001.json/1653056620657496\",\r\n"
				+ "  \"selfLink\": \"https://www.googleapis.com/storage/v1/b/mtech-merch-epc-poc-317014-mirakl_response/o/MCOM%2Fproduct_update_response%2FMCOM_product_update_pdf_220517_191001.json\",\r\n"
				+ "  \"name\": \"MCOM/product_update_response/MCOM_product_update_pdf_220517_191001.json\",\r\n"
				+ "  \"bucket\": \"mtech-merch-epc-poc-317014-mirakl_response\",\r\n"
				+ "  \"generation\": \"1653056620657496\",\r\n" + "  \"metageneration\": \"1\",\r\n"
				+ "  \"contentType\": \"application/json\",\r\n"
				+ "  \"timeCreated\": \"2022-05-20T14:23:40.815Z\",\r\n"
				+ "  \"updated\": \"2022-05-20T14:23:40.815Z\",\r\n" + "  \"storageClass\": \"STANDARD\",\r\n"
				+ "  \"timeStorageClassUpdated\": \"2022-05-20T14:23:40.815Z\",\r\n" + "  \"size\": \"375\",\r\n"
				+ "  \"md5Hash\": \"YMu2GO29/hRUPcF9jix8Kg==\",\r\n"
				+ "  \"mediaLink\": \"https://www.googleapis.com/download/storage/v1/b/mtech-merch-epc-poc-317014-mirakl_response/o/MCOM%2Fproduct_update_response%2FMCOM_product_update_pdf_220517_191001.json?generation=1653056620657496&alt=media\",\r\n"
				+ "  \"crc32c\": \"pxsb+g==\",\r\n" + "  \"etag\": \"CNj2q+Wj7vcCEAE=\"\r\n" + "}";
		String fileName = "MCOM_product_update_pdf_220517_191001.json"; // PDF Product Update response file
		String objectName = "MCOM/product_update_response/MCOM_product_update_pdf_220517_191001.json";
		String bucketName = "mtech-merch-epc-poc-317014-mirakl_response";
		String fileOperation = "PRODUCT_UPDATE_RESP";
		PdfMasterData pdfMasterData = new PdfMasterData("MCOM_product_update_220517_191001.json", "191019881001", "12",
				"Cloth", "{\"BrandName\":\"Name001\"}");
		when(fileProcessor.getFileName(objectName)).thenReturn(fileName);
		when(fileProcessor.getFileOperationForRespFile(objectName)).thenReturn(fileOperation);
		when(notificationService.processNotification(payLoad, prdUpdateRespSub, true)).thenReturn(true);
		when(cloudStorageService.readPrdUpdateRespFile(bucketName, objectName)).thenReturn(prdUpdateRespList);
		when(sqlService.findPdfJsonFromTemp(any(String.class), any(String.class), any(String.class)))
				.thenReturn(pdfMasterData);
		doNothing().when(sqlService).batchUpdatePdfJsonInMaster(any(List.class));
		doNothing().when(sqlService).batchInsertPdfRespAuditData(any(String.class), any(List.class));
		doNothing().when(sqlService).batchDeletePdfDataInTemp(any(List.class));

		// When
		productUpdateRespService.processPrdUpdateRespFile(payLoad, prdUpdateRespSub);

		// Then
		verify(sqlService, times(1)).findPdfJsonFromTemp(any(String.class), any(String.class), any(String.class));
		verify(sqlService, times(1)).batchUpdatePdfJsonInMaster(any(List.class));
		verify(sqlService, times(1)).batchInsertPdfRespAuditData(any(String.class), any(List.class));
		verify(sqlService, times(1)).batchDeletePdfDataInTemp(any(List.class));
	}

	@Test
	void testProcessPrdUpdateRespFile_PDF_Error()
			throws Exception {
		// Given
		List<ProductUpdateRespData> prdUpdateRespList = new ArrayList<>();
		ProductUpdateRespData productUpdateRespData1 = new ProductUpdateRespData("191019881001", "12", "4XX",
				"Error received from PDF", "MCOM_product_update_220517_191001.json");
		prdUpdateRespList.add(productUpdateRespData1);
		String payLoad = "{\r\n" + "  \"kind\": \"storage#object\",\r\n"
				+ "  \"id\": \"mtech-merch-epc-poc-317014-mirakl_response/MCOM/product_update_response/MCOM_product_update_pdf_220517_191001.json/1653056620657496\",\r\n"
				+ "  \"selfLink\": \"https://www.googleapis.com/storage/v1/b/mtech-merch-epc-poc-317014-mirakl_response/o/MCOM%2Fproduct_update_response%2FMCOM_product_update_pdf_220517_191001.json\",\r\n"
				+ "  \"name\": \"MCOM/product_update_response/MCOM_product_update_pdf_220517_191001.json\",\r\n"
				+ "  \"bucket\": \"mtech-merch-epc-poc-317014-mirakl_response\",\r\n"
				+ "  \"generation\": \"1653056620657496\",\r\n" + "  \"metageneration\": \"1\",\r\n"
				+ "  \"contentType\": \"application/json\",\r\n"
				+ "  \"timeCreated\": \"2022-05-20T14:23:40.815Z\",\r\n"
				+ "  \"updated\": \"2022-05-20T14:23:40.815Z\",\r\n" + "  \"storageClass\": \"STANDARD\",\r\n"
				+ "  \"timeStorageClassUpdated\": \"2022-05-20T14:23:40.815Z\",\r\n" + "  \"size\": \"375\",\r\n"
				+ "  \"md5Hash\": \"YMu2GO29/hRUPcF9jix8Kg==\",\r\n"
				+ "  \"mediaLink\": \"https://www.googleapis.com/download/storage/v1/b/mtech-merch-epc-poc-317014-mirakl_response/o/MCOM%2Fproduct_update_response%2FMCOM_product_update_pdf_220517_191001.json?generation=1653056620657496&alt=media\",\r\n"
				+ "  \"crc32c\": \"pxsb+g==\",\r\n" + "  \"etag\": \"CNj2q+Wj7vcCEAE=\"\r\n" + "}";
		String fileName = "MCOM_product_update_pdf_220517_191001.json"; // PDF Product Update response file
		String objectName = "MCOM/product_update_response/MCOM_product_update_pdf_220517_191001.json";
		String bucketName = "mtech-merch-epc-poc-317014-mirakl_response";
		String fileOperation = "PRODUCT_UPDATE_RESP";
		when(fileProcessor.getFileOperationForRespFile(objectName)).thenReturn(fileOperation);
		PdfMasterData pdfMasterData = new PdfMasterData("MCOM_product_update_220517_191001.json", "191019881001", "12",
				"Cloth", "{\"BrandName\":\"Name001\"}");
		when(fileProcessor.getFileName(objectName)).thenReturn(fileName);
		when(notificationService.processNotification(payLoad, prdUpdateRespSub, true)).thenReturn(true);
		when(cloudStorageService.readPrdUpdateRespFile(bucketName, objectName)).thenReturn(prdUpdateRespList);
		when(sqlService.findPdfJsonFromTemp(any(String.class), any(String.class), any(String.class)))
				.thenReturn(pdfMasterData);
		doNothing().when(sqlService).batchInsertPdfRespAuditData(any(String.class), any(List.class));
		doNothing().when(sqlService).batchDeletePdfDataInTemp(any(List.class));

		// When
		productUpdateRespService.processPrdUpdateRespFile(payLoad, prdUpdateRespSub);

		// Then
		verify(sqlService, times(1)).findPdfJsonFromTemp(any(String.class), any(String.class), any(String.class));
		verify(sqlService, times(1)).batchInsertPdfRespAuditData(any(String.class), any(List.class));
		verify(sqlService, times(1)).batchDeletePdfDataInTemp(any(List.class));
	}

	@Test
	void testProcessPrdUpdateRespFile_Stella_Success()
			throws Exception {
		// Given
		List<ProductUpdateRespData> prdUpdateRespList = new ArrayList<>();
		ProductUpdateRespData productUpdateRespData1 = new ProductUpdateRespData("191019881001", "12", "200", "Success",
				"MCOM_product_update_220517_191001.json");
		prdUpdateRespList.add(productUpdateRespData1);
		String payLoad = "{\r\n" + "  \"kind\": \"storage#object\",\r\n"
				+ "  \"id\": \"mtech-merch-epc-poc-317014-mirakl_response/MCOM/product_update_response/MCOM_product_update_stella_220517_191001.json/1653056548022688\",\r\n"
				+ "  \"selfLink\": \"https://www.googleapis.com/storage/v1/b/mtech-merch-epc-poc-317014-mirakl_response/o/MCOM%2Fproduct_update_response%2FMCOM_product_update_stella_220517_191001.json\",\r\n"
				+ "  \"name\": \"MCOM/product_update_response/MCOM_product_update_stella_220517_191001.json\",\r\n"
				+ "  \"bucket\": \"mtech-merch-epc-poc-317014-mirakl_response\",\r\n"
				+ "  \"generation\": \"1653056548022688\",\r\n" + "  \"metageneration\": \"1\",\r\n"
				+ "  \"contentType\": \"application/json\",\r\n"
				+ "  \"timeCreated\": \"2022-05-20T14:22:28.138Z\",\r\n"
				+ "  \"updated\": \"2022-05-20T14:22:28.138Z\",\r\n" + "  \"storageClass\": \"STANDARD\",\r\n"
				+ "  \"timeStorageClassUpdated\": \"2022-05-20T14:22:28.138Z\",\r\n" + "  \"size\": \"375\",\r\n"
				+ "  \"md5Hash\": \"YMu2GO29/hRUPcF9jix8Kg==\",\r\n"
				+ "  \"mediaLink\": \"https://www.googleapis.com/download/storage/v1/b/mtech-merch-epc-poc-317014-mirakl_response/o/MCOM%2Fproduct_update_response%2FMCOM_product_update_stella_220517_191001.json?generation=1653056548022688&alt=media\",\r\n"
				+ "  \"crc32c\": \"pxsb+g==\",\r\n" + "  \"etag\": \"CKDT2sKj7vcCEAE=\"\r\n" + "}";
		String fileName = "MCOM_product_update_stella_220517_191001.json";
		String objectName = "MCOM/product_update_response/MCOM_product_update_stella_220517_191001.json";
		String bucketName = "mtech-merch-epc-poc-317014-mirakl_response";
		String fileOperation = "PRODUCT_UPDATE_RESP";
		when(fileProcessor.getFileOperationForRespFile(objectName)).thenReturn(fileOperation);
		StellaMasterData stellaMasterData = new StellaMasterData("MCOM_product_update_220517_191001.json",
				"191019881001", "12", "Cloth",
				"{\"CustomerFacingPidDescription\":\"Name\",\"CustomerFacingColDescription\":\"Open Black001\",\"Long Description\":\"Name\",\"FabricCare\":\"Machine washable\",\"FabricContent\":\"Name\",\"CountryOfOrigin\":\"Made in USA\",\"Warranty\":\"N\",\"InternationalShipping\":\"N\",\"LegalWarnings\":\"None\",\"F&BBullet1\":\"Name\",\"F&BBullet2\":\"Name\",\"F&BBullet3\":\"Name\",\"F&BBullet20\":\"Men|Unisex\",\"ProductDimensions1\":\"Name\",\"ProductDimensions3\":\"Name\"}");
		when(fileProcessor.getFileName(objectName)).thenReturn(fileName);
		when(notificationService.processNotification(payLoad, prdUpdateRespSub, true)).thenReturn(true);
		when(cloudStorageService.readPrdUpdateRespFile(bucketName, objectName)).thenReturn(prdUpdateRespList);
		when(sqlService.findStellaJsonFromTemp(any(String.class), any(String.class), any(String.class)))
				.thenReturn(stellaMasterData);
		doNothing().when(sqlService).batchUpdateStellaJsonInMaster(any(List.class));
		doNothing().when(sqlService).batchInsertStellaRespAuditData(any(String.class), any(List.class));
		doNothing().when(sqlService).batchDeleteStellaDataInTemp(any(List.class));

		// When
		productUpdateRespService.processPrdUpdateRespFile(payLoad, prdUpdateRespSub);

		// Then
		verify(sqlService, times(1)).findStellaJsonFromTemp(any(String.class), any(String.class), any(String.class));
		verify(sqlService, times(1)).batchUpdateStellaJsonInMaster(any(List.class));
		verify(sqlService, times(1)).batchInsertStellaRespAuditData(any(String.class), any(List.class));
		verify(sqlService, times(1)).batchDeleteStellaDataInTemp(any(List.class));
	}

	@Test
	void testProcessPrdUpdateRespFile_Stella_Error()
			throws Exception {
		// Given
		List<ProductUpdateRespData> prdUpdateRespList = new ArrayList<>();
		ProductUpdateRespData productUpdateRespData1 = new ProductUpdateRespData("191019881001", "12", "4xx",
				"Error received from Stella", "MCOM_product_update_220517_191001.json");
		prdUpdateRespList.add(productUpdateRespData1);
		String payLoad = "{\r\n" + "  \"kind\": \"storage#object\",\r\n"
				+ "  \"id\": \"mtech-merch-epc-poc-317014-mirakl_response/MCOM/product_update_response/MCOM_product_update_stella_220517_191001.json/1653056548022688\",\r\n"
				+ "  \"selfLink\": \"https://www.googleapis.com/storage/v1/b/mtech-merch-epc-poc-317014-mirakl_response/o/MCOM%2Fproduct_update_response%2FMCOM_product_update_stella_220517_191001.json\",\r\n"
				+ "  \"name\": \"MCOM/product_update_response/MCOM_product_update_stella_220517_191001.json\",\r\n"
				+ "  \"bucket\": \"mtech-merch-epc-poc-317014-mirakl_response\",\r\n"
				+ "  \"generation\": \"1653056548022688\",\r\n" + "  \"metageneration\": \"1\",\r\n"
				+ "  \"contentType\": \"application/json\",\r\n"
				+ "  \"timeCreated\": \"2022-05-20T14:22:28.138Z\",\r\n"
				+ "  \"updated\": \"2022-05-20T14:22:28.138Z\",\r\n" + "  \"storageClass\": \"STANDARD\",\r\n"
				+ "  \"timeStorageClassUpdated\": \"2022-05-20T14:22:28.138Z\",\r\n" + "  \"size\": \"375\",\r\n"
				+ "  \"md5Hash\": \"YMu2GO29/hRUPcF9jix8Kg==\",\r\n"
				+ "  \"mediaLink\": \"https://www.googleapis.com/download/storage/v1/b/mtech-merch-epc-poc-317014-mirakl_response/o/MCOM%2Fproduct_update_response%2FMCOM_product_update_stella_220517_191001.json?generation=1653056548022688&alt=media\",\r\n"
				+ "  \"crc32c\": \"pxsb+g==\",\r\n" + "  \"etag\": \"CKDT2sKj7vcCEAE=\"\r\n" + "}";
		String fileName = "MCOM_product_update_stella_220517_191001.json";
		String objectName = "MCOM/product_update_response/MCOM_product_update_stella_220517_191001.json";
		String bucketName = "mtech-merch-epc-poc-317014-mirakl_response";
		String fileOperation = "PRODUCT_UPDATE_RESP";
		when(fileProcessor.getFileOperationForRespFile(objectName)).thenReturn(fileOperation);
		StellaMasterData stellaMasterData = new StellaMasterData("MCOM_product_update_220517_191001.json",
				"191019881001", "12", "Cloth",
				"{\"CustomerFacingPidDescription\":\"Name\",\"CustomerFacingColDescription\":\"Open Black001\",\"Long Description\":\"Name\",\"FabricCare\":\"Machine washable\",\"FabricContent\":\"Name\",\"CountryOfOrigin\":\"Made in USA\",\"Warranty\":\"N\",\"InternationalShipping\":\"N\",\"LegalWarnings\":\"None\",\"F&BBullet1\":\"Name\",\"F&BBullet2\":\"Name\",\"F&BBullet3\":\"Name\",\"F&BBullet20\":\"Men|Unisex\",\"ProductDimensions1\":\"Name\",\"ProductDimensions3\":\"Name\"}");
		when(fileProcessor.getFileName(objectName)).thenReturn(fileName);
		when(notificationService.processNotification(payLoad, prdUpdateRespSub, true)).thenReturn(true);
		when(cloudStorageService.readPrdUpdateRespFile(bucketName, objectName)).thenReturn(prdUpdateRespList);
		when(sqlService.findStellaJsonFromTemp(any(String.class), any(String.class), any(String.class)))
				.thenReturn(stellaMasterData);
		doNothing().when(sqlService).batchInsertStellaRespAuditData(any(String.class), any(List.class));
		doNothing().when(sqlService).batchDeleteStellaDataInTemp(any(List.class));

		// When
		productUpdateRespService.processPrdUpdateRespFile(payLoad, prdUpdateRespSub);

		// Then
		verify(sqlService, times(1)).findStellaJsonFromTemp(any(String.class), any(String.class), any(String.class));
		verify(sqlService, times(1)).batchInsertStellaRespAuditData(any(String.class), any(List.class));
		verify(sqlService, times(1)).batchDeleteStellaDataInTemp(any(List.class));
	}

	@Test
	void testProcessPrdUpdateRespFile_Facet_Success()
			throws Exception {
		// Given
		List<FacetUpdateRespData> facetUpdateRespList = new ArrayList<>();
		FacetUpdateRespData facetUpdateRespData1 = new FacetUpdateRespData("191019881001", "12", "Cloth", "902",
				"vendor1", "123_456", "240", "200", "Success",
				"[{\"AttributeName\":\"Occasion\",\"AttributeValue\":\"Active\"},{\"AttributeName\":\"Pet Accessories\",\"AttributeValue\":\"Pet Bowls\"}]",
				"MCOM_product_update_220517_191001.json",
				"[{\"AttributeName\":\"Occasion\",\"AttributeValue\":\"Active\"},{\"AttributeName\":\"Pet Accessories\",\"AttributeValue\":\"Pet Bowls\"}]");
		facetUpdateRespList.add(facetUpdateRespData1);
		String payLoad = "{\r\n" + "  \"kind\": \"storage#object\",\r\n"
				+ "  \"id\": \"mtech-merch-epc-poc-317014-mirakl_response/MCOM/facet/MCOM_product_update_facet_220517_191001.json/1653056540613893\",\r\n"
				+ "  \"selfLink\": \"https://www.googleapis.com/storage/v1/b/mtech-merch-epc-poc-317014-mirakl_response/o/MCOM%2Ffacet%2FMCOM_product_update_facet_220517_191001.json\",\r\n"
				+ "  \"name\": \"MCOM/facet/MCOM_product_update_facet_220517_191001.json\",\r\n"
				+ "  \"bucket\": \"mtech-merch-epc-poc-317014-mirakl_response\",\r\n"
				+ "  \"generation\": \"1653056540613893\",\r\n" + "  \"metageneration\": \"1\",\r\n"
				+ "  \"contentType\": \"application/json\",\r\n"
				+ "  \"timeCreated\": \"2022-05-20T14:22:20.766Z\",\r\n"
				+ "  \"updated\": \"2022-05-20T14:22:20.766Z\",\r\n" + "  \"storageClass\": \"STANDARD\",\r\n"
				+ "  \"timeStorageClassUpdated\": \"2022-05-20T14:22:20.766Z\",\r\n" + "  \"size\": \"988\",\r\n"
				+ "  \"md5Hash\": \"TAK0RtxVIDOqZDKk25ZFSQ==\",\r\n"
				+ "  \"mediaLink\": \"https://www.googleapis.com/download/storage/v1/b/mtech-merch-epc-poc-317014-mirakl_response/o/MCOM%2Ffacet%2FMCOM_product_update_facet_220517_191001.json?generation=1653056540613893&alt=media\",\r\n"
				+ "  \"crc32c\": \"Pcxntw==\",\r\n" + "  \"etag\": \"CIW6lr+j7vcCEAE=\"\r\n" + "}";
		String fileName = "MCOM_product_update_facet_220517_191001.json";
		String objectName = "MCOM/facet/MCOM_product_update_facet_220517_191001.json";
		String bucketName = "mtech-merch-epc-poc-317014-mirakl_response";
		String fileOperation = "FACET_UPDATE_RESP";
		when(fileProcessor.getFileOperationForRespFile(objectName)).thenReturn(fileOperation);
		String facetTempDataJson = "";
		FacetMasterData facetMasterData = new FacetMasterData("MCOM_product_update_220517_191001.json", "191019881001",
				"12", "Cloth", "902", "vendor1", "123_456", "240",
				"[{\"AttributeName\":\"Occasion\",\"AttributeValue\":\"Active\"},{\"AttributeName\":\"Pet Accessories\",\"AttributeValue\":\"Pet Bowls\"}]");
		
		when(fileProcessor.getFileName(objectName)).thenReturn(fileName);
		when(notificationService.processNotification(payLoad, prdFacetRespSub, true)).thenReturn(true);
		when(cloudStorageService.readFacetUpdateRespFile(bucketName, objectName)).thenReturn(facetUpdateRespList);
		when(sqlService.findFacetJsonFromTemp(any(String.class), any(String.class), any(String.class)))
				.thenReturn(facetTempDataJson);
		when(sqlService.findFacetMasterJSONByUpc(any(String.class), any(String.class))).thenReturn(facetMasterData);
		doNothing().when(sqlService).batchUpdateFacetJsonInMaster(any(List.class));
		doNothing().when(sqlService).batchInsertFacetRespAuditData(any(String.class), any(List.class));
		doNothing().when(sqlService).batchDeleteFacetDataInTemp(any(List.class));

		// When
		productUpdateRespService.processPrdUpdateRespFile(payLoad, prdFacetRespSub);

		// Then
		verify(sqlService, times(1)).findFacetJsonFromTemp(any(String.class), any(String.class), any(String.class));
		verify(sqlService, times(1)).findFacetMasterJSONByUpc(any(String.class), any(String.class));
		verify(sqlService, times(1)).batchUpdateFacetJsonInMaster(any(List.class));
		verify(sqlService, times(1)).batchInsertFacetRespAuditData(any(String.class), any(List.class));
		verify(sqlService, times(1)).batchDeleteFacetDataInTemp(any(List.class));
	}

	@Test
	void testProcessPrdUpdateRespFile_Facet_Error()
			throws Exception {
		// Given
		List<FacetUpdateRespData> facetUpdateRespList = new ArrayList<>();
		FacetUpdateRespData facetUpdateRespData1 = new FacetUpdateRespData("191019881001", "12", "Cloth", "902",
				"vendor1", "123_456", "240", "4XX", "Error received from Facet",
				"[{\"AttributeName\":\"Occasion\",\"AttributeValue\":\"Active\"},{\"AttributeName\":\"Pet Accessories\",\"AttributeValue\":\"Pet Bowls\"}]",
				"MCOM_product_update_220517_191001.json",
				"[{\"AttributeName\":\"Occasion\",\"AttributeValue\":\"Active\"},{\"AttributeName\":\"Pet Accessories\",\"AttributeValue\":\"Pet Bowls\"}]");
		facetUpdateRespList.add(facetUpdateRespData1);
		String payLoad = "{\r\n" + "  \"kind\": \"storage#object\",\r\n"
				+ "  \"id\": \"mtech-merch-epc-poc-317014-mirakl_response/MCOM/facet/MCOM_product_update_facet_220517_191001.json/1653056540613893\",\r\n"
				+ "  \"selfLink\": \"https://www.googleapis.com/storage/v1/b/mtech-merch-epc-poc-317014-mirakl_response/o/MCOM%2Ffacet%2FMCOM_product_update_facet_220517_191001.json\",\r\n"
				+ "  \"name\": \"MCOM/facet/MCOM_product_update_facet_220517_191001.json\",\r\n"
				+ "  \"bucket\": \"mtech-merch-epc-poc-317014-mirakl_response\",\r\n"
				+ "  \"generation\": \"1653056540613893\",\r\n" + "  \"metageneration\": \"1\",\r\n"
				+ "  \"contentType\": \"application/json\",\r\n"
				+ "  \"timeCreated\": \"2022-05-20T14:22:20.766Z\",\r\n"
				+ "  \"updated\": \"2022-05-20T14:22:20.766Z\",\r\n" + "  \"storageClass\": \"STANDARD\",\r\n"
				+ "  \"timeStorageClassUpdated\": \"2022-05-20T14:22:20.766Z\",\r\n" + "  \"size\": \"988\",\r\n"
				+ "  \"md5Hash\": \"TAK0RtxVIDOqZDKk25ZFSQ==\",\r\n"
				+ "  \"mediaLink\": \"https://www.googleapis.com/download/storage/v1/b/mtech-merch-epc-poc-317014-mirakl_response/o/MCOM%2Ffacet%2FMCOM_product_update_facet_220517_191001.json?generation=1653056540613893&alt=media\",\r\n"
				+ "  \"crc32c\": \"Pcxntw==\",\r\n" + "  \"etag\": \"CIW6lr+j7vcCEAE=\"\r\n" + "}";
		String fileName = "MCOM_product_update_facet_220517_191001.json";
		String objectName = "MCOM/facet/MCOM_product_update_facet_220517_191001.json";
		String bucketName = "mtech-merch-epc-poc-317014-mirakl_response";
		String fileOperation = "FACET_UPDATE_RESP";
		when(fileProcessor.getFileOperationForRespFile(objectName)).thenReturn(fileOperation);
		String facetTempDataJson = "";
		when(fileProcessor.getFileName(objectName)).thenReturn(fileName);
		when(notificationService.processNotification(payLoad, prdFacetRespSub, true)).thenReturn(true);
		when(cloudStorageService.readFacetUpdateRespFile(bucketName, objectName)).thenReturn(facetUpdateRespList);
		when(sqlService.findFacetJsonFromTemp(any(String.class), any(String.class), any(String.class)))
				.thenReturn(facetTempDataJson);
		doNothing().when(sqlService).batchInsertFacetRespAuditData(any(String.class), any(List.class));
		doNothing().when(sqlService).batchDeleteFacetDataInTemp(any(List.class));

		// When
		productUpdateRespService.processPrdUpdateRespFile(payLoad, prdFacetRespSub);

		// Then
		verify(sqlService, times(1)).findFacetJsonFromTemp(any(String.class), any(String.class), any(String.class));
		verify(sqlService, never()).batchUpdateFacetJsonInMaster(any(List.class));
		verify(sqlService, times(1)).batchInsertFacetRespAuditData(any(String.class), any(List.class));
		verify(sqlService, times(1)).batchDeleteFacetDataInTemp(any(List.class));
	}
	
	@Test
	void testProcessPrdUpdateRespFile_Wrong_FilePath()
			throws IllegalArgumentException, JSONException, IOException, MiraklRepositoryException {
		// Given
		List<FacetUpdateRespData> facetUpdateRespList = new ArrayList<>();
		FacetUpdateRespData facetUpdateRespData1 = new FacetUpdateRespData("191019881001", "12", "Cloth", "902",
				"vendor1", "123_456", "240", "4XX", "Error received from Facet",
				"[{\"AttributeName\":\"Occasion\",\"AttributeValue\":\"Active\"},{\"AttributeName\":\"Pet Accessories\",\"AttributeValue\":\"Pet Bowls\"}]",
				"MCOM_product_update_220517_191001.json",
				"[{\"AttributeName\":\"Occasion\",\"AttributeValue\":\"Active\"},{\"AttributeName\":\"Pet Accessories\",\"AttributeValue\":\"Pet Bowls\"}]");
		facetUpdateRespList.add(facetUpdateRespData1);
		String payLoad = "{\r\n" + "  \"kind\": \"storage#object\",\r\n"
				+ "  \"id\": \"mtech-merch-epc-poc-317014-mirakl_response/MCOM/facet/MCOM_product_update_facet_220517_191001.json/1653056540613893\",\r\n"
				+ "  \"selfLink\": \"https://www.googleapis.com/storage/v1/b/mtech-merch-epc-poc-317014-mirakl_response/o/MCOM%2Fproduct_update_response%2FMCOM_product_update_facet_220517_191001.json\",\r\n"
				+ "  \"name\": \"MCOM/product_update_response/MCOM_product_update_facet_220517_191001.json\",\r\n"
				+ "  \"bucket\": \"mtech-merch-epc-poc-317014-mirakl_response\",\r\n"
				+ "  \"generation\": \"1653056540613893\",\r\n" + "  \"metageneration\": \"1\",\r\n"
				+ "  \"contentType\": \"application/json\",\r\n"
				+ "  \"timeCreated\": \"2022-05-20T14:22:20.766Z\",\r\n"
				+ "  \"updated\": \"2022-05-20T14:22:20.766Z\",\r\n" + "  \"storageClass\": \"STANDARD\",\r\n"
				+ "  \"timeStorageClassUpdated\": \"2022-05-20T14:22:20.766Z\",\r\n" + "  \"size\": \"988\",\r\n"
				+ "  \"md5Hash\": \"TAK0RtxVIDOqZDKk25ZFSQ==\",\r\n"
				+ "  \"mediaLink\": \"https://www.googleapis.com/download/storage/v1/b/mtech-merch-epc-poc-317014-mirakl_response/o/MCOM%2Fproduct_update_response%2FMCOM_product_update_facet_220517_191001.json?generation=1653056540613893&alt=media\",\r\n"
				+ "  \"crc32c\": \"Pcxntw==\",\r\n" + "  \"etag\": \"CIW6lr+j7vcCEAE=\"\r\n" + "}";
		String fileName = "MCOM_product_update_facet_220517_191001.json";
		String objectName = "MCOM/product_update_response/MCOM_product_update_facet_220517_191001.json";
		String fileOperation = "WRONG_FILE";
		when(fileProcessor.getFileOperationForRespFile(objectName)).thenReturn(fileOperation);
		when(fileProcessor.getFileName(objectName)).thenReturn(fileName);

		// When
		productUpdateRespService.processPrdUpdateRespFile(payLoad, prdFacetRespSub);

		// Then
		verify(sqlService, never()).findFacetJsonFromTemp(any(String.class), any(String.class), any(String.class));
		verify(sqlService, never()).batchUpdateFacetJsonInMaster(any(List.class));
		verify(sqlService, never()).batchInsertFacetRespAuditData(any(String.class), any(List.class));
		verify(sqlService, never()).batchDeleteFacetDataInTemp(any(List.class));
	}
	
	@Test
	void testProcessPrdUpdateRespFile_Json_Exception()
			throws Exception {
		// Given
		List<FacetUpdateRespData> facetUpdateRespList = new ArrayList<>();
		FacetUpdateRespData facetUpdateRespData1 = new FacetUpdateRespData("191019881001", "12", "Cloth", "902",
				"vendor1", "123_456", "240", "4XX", "Error received from Facet",
				"",
				"MCOM_product_update_220517_191001.json",
				"[{\"AttributeName\":\"Occasion\",\"AttributeValue\":\"Active\"},{\"AttributeName\":\"Pet Accessories\",\"AttributeValue\":\"Pet Bowls\"}]");
		facetUpdateRespList.add(facetUpdateRespData1);
		String payLoad = "{\r\n" + "  \"kind\": \"storage#object\",\r\n"
				+ "  \"id\": \"mtech-merch-epc-poc-317014-mirakl_response/MCOM/facet/MCOM_product_update_facet_220517_191001.json/1653056540613893\",\r\n"
				+ "  \"selfLink\": \"https://www.googleapis.com/storage/v1/b/mtech-merch-epc-poc-317014-mirakl_response/o/MCOM%2Fproduct_update_response%2FMCOM_product_update_facet_220517_191001.json\",\r\n"
				+ "  \"name\": \"MCOM/product_update_response/MCOM_product_update_facet_220517_191001.json\",\r\n"
				+ "  \"bucket\": \"mtech-merch-epc-poc-317014-mirakl_response\",\r\n"
				+ "  \"generation\": \"1653056540613893\",\r\n" + "  \"metageneration\": \"1\",\r\n"
				+ "  \"contentType\": \"application/json\",\r\n"
				+ "  \"timeCreated\": \"2022-05-20T14:22:20.766Z\",\r\n"
				+ "  \"updated\": \"2022-05-20T14:22:20.766Z\",\r\n" + "  \"storageClass\": \"STANDARD\",\r\n"
				+ "  \"timeStorageClassUpdated\": \"2022-05-20T14:22:20.766Z\",\r\n" + "  \"size\": \"988\",\r\n"
				+ "  \"md5Hash\": \"TAK0RtxVIDOqZDKk25ZFSQ==\",\r\n"
				+ "  \"mediaLink\": \"https://www.googleapis.com/download/storage/v1/b/mtech-merch-epc-poc-317014-mirakl_response/o/MCOM%2Fproduct_update_response%2FMCOM_product_update_facet_220517_191001.json?generation=1653056540613893&alt=media\",\r\n"
				+ "  \"crc32c\": \"Pcxntw==\",\r\n" + "  \"etag\": \"CIW6lr+j7vcCEAE=\"\r\n" + "}";
		String fileName = "MCOM_product_update_facet_220517_191001.json";
		String objectName = "MCOM/product_update_response/MCOM_product_update_facet_220517_191001.json";
		String fileOperation = "FACET_UPDATE_RESP";
		String bucketName = "mtech-merch-epc-poc-317014-mirakl_response";
		when(fileProcessor.getFileOperationForRespFile(objectName)).thenReturn(fileOperation);
		when(fileProcessor.getFileName(objectName)).thenReturn(fileName);
		when(notificationService.processNotification(payLoad, prdFacetRespSub, true)).thenReturn(true);
		doThrow(JSONException.class).when(cloudStorageService).readFacetUpdateRespFile(bucketName, objectName);

		// When
		try {
			productUpdateRespService.processPrdUpdateRespFile(payLoad, prdFacetRespSub);
		} catch(JSONException e) {
			assertTrue(e instanceof JSONException,"Exception is expected");
		}

		// Then
		verify(sqlService, never()).findFacetJsonFromTemp(any(String.class), any(String.class), any(String.class));
		verify(sqlService, never()).batchUpdateFacetJsonInMaster(any(List.class));
		verify(sqlService, never()).batchInsertFacetRespAuditData(any(String.class), any(List.class));
		verify(sqlService, never()).batchDeleteFacetDataInTemp(any(List.class));
	}
	
	@Test
	void testProcessPrdUpdateRespFile_IOException()
			throws Exception {
		// Given
		List<FacetUpdateRespData> facetUpdateRespList = new ArrayList<>();
		FacetUpdateRespData facetUpdateRespData1 = new FacetUpdateRespData("191019881001", "12", "Cloth", "902",
				"vendor1", "123_456", "240", "4XX", "Error received from Facet",
				"",
				"MCOM_product_update_220517_191001.json",
				"[{\"AttributeName\":\"Occasion\",\"AttributeValue\":\"Active\"},{\"AttributeName\":\"Pet Accessories\",\"AttributeValue\":\"Pet Bowls\"}]");
		facetUpdateRespList.add(facetUpdateRespData1);
		String payLoad = "{\r\n" + "  \"kind\": \"storage#object\",\r\n"
				+ "  \"id\": \"mtech-merch-epc-poc-317014-mirakl_response/MCOM/facet/MCOM_product_update_facet_220517_191001.json/1653056540613893\",\r\n"
				+ "  \"selfLink\": \"https://www.googleapis.com/storage/v1/b/mtech-merch-epc-poc-317014-mirakl_response/o/MCOM%2Fproduct_update_response%2FMCOM_product_update_facet_220517_191001.json\",\r\n"
				+ "  \"name\": \"MCOM/product_update_response/MCOM_product_update_facet_220517_191001.json\",\r\n"
				+ "  \"bucket\": \"mtech-merch-epc-poc-317014-mirakl_response\",\r\n"
				+ "  \"generation\": \"1653056540613893\",\r\n" + "  \"metageneration\": \"1\",\r\n"
				+ "  \"contentType\": \"application/json\",\r\n"
				+ "  \"timeCreated\": \"2022-05-20T14:22:20.766Z\",\r\n"
				+ "  \"updated\": \"2022-05-20T14:22:20.766Z\",\r\n" + "  \"storageClass\": \"STANDARD\",\r\n"
				+ "  \"timeStorageClassUpdated\": \"2022-05-20T14:22:20.766Z\",\r\n" + "  \"size\": \"988\",\r\n"
				+ "  \"md5Hash\": \"TAK0RtxVIDOqZDKk25ZFSQ==\",\r\n"
				+ "  \"mediaLink\": \"https://www.googleapis.com/download/storage/v1/b/mtech-merch-epc-poc-317014-mirakl_response/o/MCOM%2Fproduct_update_response%2FMCOM_product_update_facet_220517_191001.json?generation=1653056540613893&alt=media\",\r\n"
				+ "  \"crc32c\": \"Pcxntw==\",\r\n" + "  \"etag\": \"CIW6lr+j7vcCEAE=\"\r\n" + "}";
		String fileName = "MCOM_product_update_facet_220517_191001.json";
		String bucketName = "mtech-merch-epc-poc-317014-mirakl_response";
		String objectName = "MCOM/product_update_response/MCOM_product_update_facet_220517_191001.json";
		String fileOperation = "FACET_UPDATE_RESP";
		when(fileProcessor.getFileOperationForRespFile(objectName)).thenReturn(fileOperation);
		when(fileProcessor.getFileName(objectName)).thenReturn(fileName);
		when(notificationService.processNotification(payLoad, prdFacetRespSub, true)).thenReturn(true);
		doThrow(IOException.class).when(cloudStorageService).readFacetUpdateRespFile(bucketName, objectName);
		
		// When
		try {
			productUpdateRespService.processPrdUpdateRespFile(payLoad, prdFacetRespSub);
		} catch(Exception e) {
			assertTrue(e instanceof Exception,"Exception is expected");
		}

		// Then
		verify(sqlService, never()).findFacetJsonFromTemp(any(String.class), any(String.class), any(String.class));
		verify(sqlService, never()).batchUpdateFacetJsonInMaster(any(List.class));
		verify(sqlService, never()).batchInsertFacetRespAuditData(any(String.class), any(List.class));
		verify(sqlService, never()).batchDeleteFacetDataInTemp(any(List.class));
	}
	
	@Test
	void testProcessPrdUpdateRespFile_IllegalArgumentException()
			throws Exception {
		// Given
		List<FacetUpdateRespData> facetUpdateRespList = new ArrayList<>();
		FacetUpdateRespData facetUpdateRespData1 = new FacetUpdateRespData("191019881001", "12", "Cloth", "902",
				"vendor1", "123_456", "240", "4XX", "Error received from Facet",
				"",
				"MCOM_product_update_220517_191001.json",
				"[{\"AttributeName\":\"Occasion\",\"AttributeValue\":\"Active\"},{\"AttributeName\":\"Pet Accessories\",\"AttributeValue\":\"Pet Bowls\"}]");
		facetUpdateRespList.add(facetUpdateRespData1);
		String payLoad = "{\r\n" + "  \"kind\": \"storage#object\",\r\n"
				+ "  \"id\": \"mtech-merch-epc-poc-317014-mirakl_response/MCOM/facet/MCOM_product_update_facet_220517_191001.json/1653056540613893\",\r\n"
				+ "  \"selfLink\": \"https://www.googleapis.com/storage/v1/b/mtech-merch-epc-poc-317014-mirakl_response/o/MCOM%2Fproduct_update_response%2FMCOM_product_update_facet_220517_191001.json\",\r\n"
				+ "  \"name\": \"MCOM/product_update_response/MCOM_product_update_facet_220517_191001.json\",\r\n"
				+ "  \"bucket\": \"mtech-merch-epc-poc-317014-mirakl_response\",\r\n"
				+ "  \"generation\": \"1653056540613893\",\r\n" + "  \"metageneration\": \"1\",\r\n"
				+ "  \"contentType\": \"application/json\",\r\n"
				+ "  \"timeCreated\": \"2022-05-20T14:22:20.766Z\",\r\n"
				+ "  \"updated\": \"2022-05-20T14:22:20.766Z\",\r\n" + "  \"storageClass\": \"STANDARD\",\r\n"
				+ "  \"timeStorageClassUpdated\": \"2022-05-20T14:22:20.766Z\",\r\n" + "  \"size\": \"988\",\r\n"
				+ "  \"md5Hash\": \"TAK0RtxVIDOqZDKk25ZFSQ==\",\r\n"
				+ "  \"mediaLink\": \"https://www.googleapis.com/download/storage/v1/b/mtech-merch-epc-poc-317014-mirakl_response/o/MCOM%2Fproduct_update_response%2FMCOM_product_update_facet_220517_191001.json?generation=1653056540613893&alt=media\",\r\n"
				+ "  \"crc32c\": \"Pcxntw==\",\r\n" + "  \"etag\": \"CIW6lr+j7vcCEAE=\"\r\n" + "}";
		String fileName = "MCOM_product_update_facet_220517_191001.json";
		String bucketName = "mtech-merch-epc-poc-317014-mirakl_response";
		String objectName = "MCOM/product_update_response/MCOM_product_update_facet_220517_191001.json";
		String fileOperation = "FACET_UPDATE_RESP";
		when(fileProcessor.getFileOperationForRespFile(objectName)).thenReturn(fileOperation);
		when(fileProcessor.getFileName(objectName)).thenReturn(fileName);
		when(notificationService.processNotification(payLoad, prdFacetRespSub, true)).thenReturn(true);
		doThrow(IllegalArgumentException.class).when(cloudStorageService).readFacetUpdateRespFile(bucketName, objectName);
		
		// When
		try {
			productUpdateRespService.processPrdUpdateRespFile(payLoad, prdFacetRespSub);
		} catch(Exception e) {
			assertTrue(e instanceof Exception,"Exception is expected");
		}

		// Then
		verify(sqlService, never()).findFacetJsonFromTemp(any(String.class), any(String.class), any(String.class));
		verify(sqlService, never()).batchUpdateFacetJsonInMaster(any(List.class));
		verify(sqlService, never()).batchInsertFacetRespAuditData(any(String.class), any(List.class));
		verify(sqlService, never()).batchDeleteFacetDataInTemp(any(List.class));
	}
	
	@Test
	void testProcessPrdUpdateRespFile_MultipleNotification()
			throws Exception {
		// Given
		List<FacetUpdateRespData> facetUpdateRespList = new ArrayList<>();
		FacetUpdateRespData facetUpdateRespData1 = new FacetUpdateRespData("191019881001", "12", "Cloth", "902",
				"vendor1", "123_456", "240", "4XX", "Error received from Facet",
				"",
				"MCOM_product_update_220517_191001.json",
				"[{\"AttributeName\":\"Occasion\",\"AttributeValue\":\"Active\"},{\"AttributeName\":\"Pet Accessories\",\"AttributeValue\":\"Pet Bowls\"}]");
		facetUpdateRespList.add(facetUpdateRespData1);
		String payLoad = "{\r\n" + "  \"kind\": \"storage#object\",\r\n"
				+ "  \"id\": \"mtech-merch-epc-poc-317014-mirakl_response/MCOM/facet/MCOM_product_update_facet_220517_191001.json/1653056540613893\",\r\n"
				+ "  \"selfLink\": \"https://www.googleapis.com/storage/v1/b/mtech-merch-epc-poc-317014-mirakl_response/o/MCOM%2Fproduct_update_response%2FMCOM_product_update_facet_220517_191001.json\",\r\n"
				+ "  \"name\": \"MCOM/product_update_response/MCOM_product_update_facet_220517_191001.json\",\r\n"
				+ "  \"bucket\": \"mtech-merch-epc-poc-317014-mirakl_response\",\r\n"
				+ "  \"generation\": \"1653056540613893\",\r\n" + "  \"metageneration\": \"1\",\r\n"
				+ "  \"contentType\": \"application/json\",\r\n"
				+ "  \"timeCreated\": \"2022-05-20T14:22:20.766Z\",\r\n"
				+ "  \"updated\": \"2022-05-20T14:22:20.766Z\",\r\n" + "  \"storageClass\": \"STANDARD\",\r\n"
				+ "  \"timeStorageClassUpdated\": \"2022-05-20T14:22:20.766Z\",\r\n" + "  \"size\": \"988\",\r\n"
				+ "  \"md5Hash\": \"TAK0RtxVIDOqZDKk25ZFSQ==\",\r\n"
				+ "  \"mediaLink\": \"https://www.googleapis.com/download/storage/v1/b/mtech-merch-epc-poc-317014-mirakl_response/o/MCOM%2Fproduct_update_response%2FMCOM_product_update_facet_220517_191001.json?generation=1653056540613893&alt=media\",\r\n"
				+ "  \"crc32c\": \"Pcxntw==\",\r\n" + "  \"etag\": \"CIW6lr+j7vcCEAE=\"\r\n" + "}";
		String fileName = "MCOM_product_update_facet_220517_191001.json";
		String objectName = "MCOM/product_update_response/MCOM_product_update_facet_220517_191001.json";
		String fileOperation = "FACET_UPDATE_RESP";
		when(fileProcessor.getFileName(objectName)).thenReturn(fileName);
		when(fileProcessor.getFileOperationForRespFile(objectName)).thenReturn(fileOperation);
		when(notificationService.processNotification(payLoad, prdFacetRespSub, true)).thenReturn(false);
		
		// When
		productUpdateRespService.processPrdUpdateRespFile(payLoad, prdFacetRespSub);

		// Then
		verify(notificationService,times(1)).processNotification(payLoad, prdFacetRespSub, true);
		verify(sqlService, never()).findFacetJsonFromTemp(any(String.class), any(String.class), any(String.class));
		verify(sqlService, never()).batchUpdateFacetJsonInMaster(any(List.class));
		verify(sqlService, never()).batchInsertFacetRespAuditData(any(String.class), any(List.class));
		verify(sqlService, never()).batchDeleteFacetDataInTemp(any(List.class));
	}
	
	@Test
	void testProcessPrdUpdateRespFile_DB_Exception()
			throws Exception {
		// Given
		List<FacetUpdateRespData> facetUpdateRespList = new ArrayList<>();
		FacetUpdateRespData facetUpdateRespData1 = new FacetUpdateRespData("191019881001", "12", "Cloth", "902",
				"vendor1", "123_456", "240", "4XX", "Error received from Facet",
				"[{\"AttributeName\":\"Occasion\",\"AttributeValue\":\"Active\"},{\"AttributeName\":\"Pet Accessories\",\"AttributeValue\":\"Pet Bowls\"}]",
				"MCOM_product_update_220517_191001.json",
				"[{\"AttributeName\":\"Occasion\",\"AttributeValue\":\"Active\"},{\"AttributeName\":\"Pet Accessories\",\"AttributeValue\":\"Pet Bowls\"}]");
		facetUpdateRespList.add(facetUpdateRespData1);
		String payLoad = "{\r\n" + "  \"kind\": \"storage#object\",\r\n"
				+ "  \"id\": \"mtech-merch-epc-poc-317014-mirakl_response/MCOM/facet/MCOM_product_update_facet_220517_191001.json/1653056540613893\",\r\n"
				+ "  \"selfLink\": \"https://www.googleapis.com/storage/v1/b/mtech-merch-epc-poc-317014-mirakl_response/o/MCOM%2Ffacet%2FMCOM_product_update_facet_220517_191001.json\",\r\n"
				+ "  \"name\": \"MCOM/facet/MCOM_product_update_facet_220517_191001.json\",\r\n"
				+ "  \"bucket\": \"mtech-merch-epc-poc-317014-mirakl_response\",\r\n"
				+ "  \"generation\": \"1653056540613893\",\r\n" + "  \"metageneration\": \"1\",\r\n"
				+ "  \"contentType\": \"application/json\",\r\n"
				+ "  \"timeCreated\": \"2022-05-20T14:22:20.766Z\",\r\n"
				+ "  \"updated\": \"2022-05-20T14:22:20.766Z\",\r\n" + "  \"storageClass\": \"STANDARD\",\r\n"
				+ "  \"timeStorageClassUpdated\": \"2022-05-20T14:22:20.766Z\",\r\n" + "  \"size\": \"988\",\r\n"
				+ "  \"md5Hash\": \"TAK0RtxVIDOqZDKk25ZFSQ==\",\r\n"
				+ "  \"mediaLink\": \"https://www.googleapis.com/download/storage/v1/b/mtech-merch-epc-poc-317014-mirakl_response/o/MCOM%2Ffacet%2FMCOM_product_update_facet_220517_191001.json?generation=1653056540613893&alt=media\",\r\n"
				+ "  \"crc32c\": \"Pcxntw==\",\r\n" + "  \"etag\": \"CIW6lr+j7vcCEAE=\"\r\n" + "}";
		String fileName = "MCOM_product_update_facet_220517_191001.json";
		String objectName = "MCOM/facet/MCOM_product_update_facet_220517_191001.json";
		String bucketName = "mtech-merch-epc-poc-317014-mirakl_response";
		String fileOperation = "FACET_UPDATE_RESP";
		when(fileProcessor.getFileOperationForRespFile(objectName)).thenReturn(fileOperation);
		when(fileProcessor.getFileName(objectName)).thenReturn(fileName);
		when(notificationService.processNotification(payLoad, prdFacetRespSub, true)).thenReturn(true);
		when(cloudStorageService.readFacetUpdateRespFile(bucketName, objectName)).thenReturn(facetUpdateRespList);
		doThrow(EmptyResultDataAccessException.class).when(sqlService).findFacetJsonFromTemp(any(String.class), any(String.class), any(String.class));
		
		// When
		try {
			productUpdateRespService.processPrdUpdateRespFile(payLoad, prdFacetRespSub);
		} catch(Exception e) {
			assertTrue(e instanceof Exception,"Exception is expected");
		}

		// Then
		verify(sqlService, times(1)).findFacetJsonFromTemp(any(String.class), any(String.class), any(String.class));
		verify(sqlService, never()).batchUpdateFacetJsonInMaster(any(List.class));
		verify(sqlService, never()).batchInsertFacetRespAuditData(any(String.class), any(List.class));
		verify(sqlService, never()).batchDeleteFacetDataInTemp(any(List.class));
	}

}
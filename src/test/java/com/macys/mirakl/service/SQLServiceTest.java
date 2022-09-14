package com.macys.mirakl.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.macys.mirakl.dao.CreateResponseDAO;
import com.macys.mirakl.dao.NotificationDAO;
import com.macys.mirakl.dao.OfferIdDAO;
import com.macys.mirakl.dao.ProductAuditDAO;
import com.macys.mirakl.dao.ProductCreateDAO;
import com.macys.mirakl.dao.ProductUpdateDAO;
import com.macys.mirakl.exception.MiraklRepositoryException;
import com.macys.mirakl.model.CreateResponseData;
import com.macys.mirakl.model.FacetMasterData;
import com.macys.mirakl.model.FacetUpdateRespData;
import com.macys.mirakl.model.ImageMasterData;
import com.macys.mirakl.model.ImageUpdateRespData;
import com.macys.mirakl.model.MiraklData;
import com.macys.mirakl.model.OfferIdRequestData;
import com.macys.mirakl.model.OfferIdResponseData;
import com.macys.mirakl.model.PdfMasterData;
import com.macys.mirakl.model.PdfRespData;
import com.macys.mirakl.model.PublishUpcData;
import com.macys.mirakl.model.StellaMasterData;
import com.macys.mirakl.model.StellaRespData;
import com.macys.mirakl.model.UpcMasterData;
import com.macys.mirakl.util.TestUtil;

@ExtendWith(MockitoExtension.class)
class SQLServiceTest {
	
	@InjectMocks
	SQLService sqlService;
	
	@Mock
	CreateResponseDAO createResponseDAO;

	@Mock
	ProductUpdateDAO productUpdateDAO;
	
	@Mock
	OfferIdDAO offerIdDAO;
	
	@Mock
	ProductCreateDAO productCreateDAO;
	
	@Mock
	ProductAuditDAO productAuditDAO;
	
	@Mock
	private NotificationDAO notificationDAO;

	@BeforeEach
	void setUp() throws Exception {
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void testBatchInsertPdfTempData() {
		// given
		List<PdfMasterData> pfdData = new ArrayList<PdfMasterData>();
		String operation = "";
		doNothing().when(productUpdateDAO).batchInsertPdfTempData(any(List.class), any(String.class));
		
		// when
		sqlService.batchInsertPdfTempData(pfdData, operation);
		
		// then
		verify(productUpdateDAO,times(1)).batchInsertPdfTempData(any(List.class), any(String.class));
	}

	@Test
	void testBatchInsertStellaTempData() {
		// given
		List<StellaMasterData> stellaData = new ArrayList<StellaMasterData>();
		String operation = "";
		doNothing().when(productUpdateDAO).batchInsertStellaTempData(any(List.class), any(String.class));
		
		// when
		sqlService.batchInsertStellaTempData(stellaData, operation);
		
		// then
		verify(productUpdateDAO,times(1)).batchInsertStellaTempData(any(List.class), any(String.class));
	}

	@Test
	void testBatchInsertImageTempData() {
		// given
		List<ImageMasterData> imageData = new ArrayList<ImageMasterData>();
		String operation = "";
		doNothing().when(productUpdateDAO).batchInsertImagesTempData(any(List.class), any(String.class));
		
		// when
		sqlService.batchInsertImageTempData(imageData, operation);
		
		// then
		verify(productUpdateDAO,times(1)).batchInsertImagesTempData(any(List.class), any(String.class));
	}

	@Test
	void testBatchInsertFacetTempData() {
		// given
		List<FacetMasterData> facetData = new ArrayList<FacetMasterData>();
		String operation = "";
		doNothing().when(productUpdateDAO).batchInsertFacetTempData(any(List.class), any(String.class));
		
		// when
		sqlService.batchInsertFacetTempData(facetData, operation);
		
		// then
		verify(productUpdateDAO,times(1)).batchInsertFacetTempData(any(List.class), any(String.class));
	}

	@Test
	void testFindPdfMasterJSONByUpc() {
		// given
		PdfMasterData pdfMasterData = new PdfMasterData();
		String opDiv = "";
		String upcId = "";
		doReturn(pdfMasterData).when(productUpdateDAO).findPdfMasterDataByUpc(any(String.class), any(String.class));
		
		// when
		sqlService.findPdfMasterJSONByUpc(upcId, opDiv);
		
		// then
		verify(productUpdateDAO,times(1)).findPdfMasterDataByUpc(any(String.class), any(String.class));
	}

	@Test
	void testFindStellaMasterJSONByUpc() {
		// given
		StellaMasterData stellaMasterData = new StellaMasterData();
		String opDiv = "";
		String upcId = "";
		doReturn(stellaMasterData).when(productUpdateDAO).findStellaMasterDataByUpc(any(String.class), any(String.class));
		
		// when
		sqlService.findStellaMasterJSONByUpc(upcId, opDiv);
		
		// then
		verify(productUpdateDAO,times(1)).findStellaMasterDataByUpc(any(String.class), any(String.class));
	}

	@Test
	void testFindImageMasterJSONByUpc() {
		// given
		ImageMasterData imageMasterData = new ImageMasterData();
		String opDiv = "";
		String upcId = "";
		doReturn(imageMasterData).when(productUpdateDAO).findImageMasterDataByUpc(any(String.class), any(String.class));
		
		// when
		sqlService.findImageMasterJSONByUpc(upcId, opDiv);
		
		// then
		verify(productUpdateDAO,times(1)).findImageMasterDataByUpc(any(String.class), any(String.class));
	}

	@Test
	void testFindFacetMasterJSONByUpc() {
		//Given
		String upcId="1111111111";
		String opDiv ="12";
		FacetMasterData facetMasterData = new FacetMasterData();
		when(productUpdateDAO.findFacetMasterDataByUpc(upcId, opDiv)).thenReturn(facetMasterData);
		
		// When
		FacetMasterData facetMasterDataResp=sqlService.findFacetMasterJSONByUpc( upcId,opDiv);

		//Then
		verify(productUpdateDAO,times(1)).findFacetMasterDataByUpc(upcId,opDiv);
		assertEquals(facetMasterData,facetMasterDataResp,"The FacetMasterData need to be returned from DB");

	}

	@Test
	void testGetIsMainImgP0ByUpc() {
		// given
		boolean isMainImgP0 = false;
		String opDiv = "";
		String upcId = "";
		doReturn(isMainImgP0).when(productUpdateDAO).getIsMainImgP0ByUpc(any(String.class), any(String.class));
		
		// when
		sqlService.getIsMainImgP0ByUpc(upcId, opDiv);
		
		// then
		verify(productUpdateDAO,times(1)).getIsMainImgP0ByUpc(any(String.class), any(String.class));
	}

	@Test
	void testInsertOfferIdAuditList() throws MiraklRepositoryException {
		// given
		List<OfferIdRequestData> offerIdReqList = TestUtil.generateReqList();
		String fileName = "BCOM_offer_220329_191088.json";
		doNothing().when(offerIdDAO).insertOfferIdAuditList(offerIdReqList, fileName);
		
		// when
		sqlService.insertOfferIdAuditList(offerIdReqList, fileName);
		
		// then
		verify(offerIdDAO,times(1)).insertOfferIdAuditList(offerIdReqList, fileName);
	}

	@Test
	void testUpdateOfferIdAuditList() throws MiraklRepositoryException {
		// given
		List<OfferIdResponseData> offerIdRespList = TestUtil.generateRespList();
		doNothing().when(offerIdDAO).updateOfferIdAuditList(offerIdRespList);
		
		// when
		sqlService.updateOfferIdAuditList(offerIdRespList);
		
		// then
		verify(offerIdDAO,times(1)).updateOfferIdAuditList(offerIdRespList);
	}

	@Test
	void testUpdateOfferIdMasterList() throws MiraklRepositoryException {
		// given
		List<OfferIdResponseData> offerIdRespList = TestUtil.generateRespList();
		doNothing().when(offerIdDAO).updateOfferIdMasterList(offerIdRespList);
		
		// when
		sqlService.updateOfferIdMasterList(offerIdRespList);
		
		// then
		verify(offerIdDAO,times(1)).updateOfferIdMasterList(offerIdRespList);
	}

	@Test
	void testInsertCreateRespAuditList() throws MiraklRepositoryException {
		// given
		List<CreateResponseData> createResponseDatas = TestUtil.populateCreateRespFinalList();
		doNothing().when(createResponseDAO).insertCreateRespAuditList(createResponseDatas);
		
		// when
		sqlService.insertCreateRespAuditList(createResponseDatas);
		
		// then
		verify(createResponseDAO,times(1)).insertCreateRespAuditList(createResponseDatas);
	}

	@Test
	void testUpdateCreateRespList() throws MiraklRepositoryException {
		// given
		List<PublishUpcData> upcDataList = TestUtil.generatePublishUpcList();
		doNothing().when(createResponseDAO).updateCreateRespList(upcDataList);
		
		// when
		sqlService.updateCreateRespList(upcDataList);
		
		// then
		verify(createResponseDAO,times(1)).updateCreateRespList(upcDataList);
	}

	@Test
	void testFindUpcMasterDataByUpc() throws MiraklRepositoryException {		
		UpcMasterData upcMasterData = new UpcMasterData();
		upcMasterData.setPid("1111");
		upcMasterData.setNrfColorCode("RedColor1");
		upcMasterData.setNrfSizeCode("12");
		upcMasterData.setMsrp("12");
		upcMasterData.setTaxCode("dummyValue1");
		
		// given
		when(createResponseDAO.findUpcMasterDataByUpc("12345678901", "12")).thenReturn(upcMasterData);
		
		// when
		UpcMasterData upcMasterData1 = sqlService.findUpcMasterDataByUpc("12345678901", "12");
		
		// then
		verify(createResponseDAO,times(1)).findUpcMasterDataByUpc("12345678901", "12");
		assertEquals("1111", upcMasterData1.getPid());
		assertEquals("RedColor1", upcMasterData1.getNrfColorCode());
		assertEquals("12", upcMasterData1.getNrfSizeCode());
		assertEquals("12", upcMasterData1.getMsrp());
		assertEquals("dummyValue1", upcMasterData1.getTaxCode());
	}

	@Test
	void testFindPdfJsonFromTemp() throws MiraklRepositoryException {
		// given
		String upcId = "";
		String opDiv = "";
		String fileNameInJson = "";
		PdfMasterData pdfMasterData = new PdfMasterData();
		doReturn(pdfMasterData).when(productUpdateDAO).findPdfJsonFromTemp(any(String.class), any(String.class), any(String.class));
		
		// when
		sqlService.findPdfJsonFromTemp(upcId, opDiv, fileNameInJson);
		
		// then
		verify(productUpdateDAO,times(1)).findPdfJsonFromTemp(any(String.class), any(String.class), any(String.class));
	}

	@Test
	void testFindStellaJsonFromTemp() throws MiraklRepositoryException {
		// given
		String upcId = "";
		String opDiv = "";
		String fileNameInJson = "";
		StellaMasterData stellaMasterData = new StellaMasterData();
		doReturn(stellaMasterData).when(productUpdateDAO).findStellaJsonFromTemp(any(String.class), any(String.class), any(String.class));
		
		// when
		sqlService.findStellaJsonFromTemp(upcId, opDiv, fileNameInJson);
		
		// then
		verify(productUpdateDAO,times(1)).findStellaJsonFromTemp(any(String.class), any(String.class), any(String.class));
	}

	@Test
	void testBatchDeletePdfDataInTemp() throws MiraklRepositoryException {
		// given
		List<PdfRespData> prdUpdateRespList = new ArrayList<PdfRespData>();
		doNothing().when(productUpdateDAO).batchDeletePdfDataInTemp(any(List.class));
		
		// when
		sqlService.batchDeletePdfDataInTemp(prdUpdateRespList);
		
		// then
		verify(productUpdateDAO,times(1)).batchDeletePdfDataInTemp(any(List.class));
	}

	@Test
	void testBatchDeletePdfDataInTempNoDelta() throws MiraklRepositoryException {
		// given
		List<PdfMasterData> pdfMasterDataList = new ArrayList<PdfMasterData>();
		doNothing().when(productUpdateDAO).batchDeletePdfDataInTempNoDelta(any(List.class));
		
		// when
		sqlService.batchDeletePdfDataInTempNoDelta(pdfMasterDataList);
		
		// then
		verify(productUpdateDAO,times(1)).batchDeletePdfDataInTempNoDelta(any(List.class));
	}

	@Test
	void testBatchDeleteStellaDataInTempNoDelta() throws MiraklRepositoryException {
		// given
		List<StellaMasterData> stellaMasterDataList = new ArrayList<StellaMasterData>();
		doNothing().when(productUpdateDAO).batchDeleteStellaDataInTempNoDelta(any(List.class));
		
		// when
		sqlService.batchDeleteStellaDataInTempNoDelta(stellaMasterDataList);
		
		// then
		verify(productUpdateDAO,times(1)).batchDeleteStellaDataInTempNoDelta(any(List.class));
	}

	@Test
	void testBatchDeleteFacetDataInTempNoDelta() throws MiraklRepositoryException {
		// given
		List<FacetMasterData> facetMasterDataList = new ArrayList<FacetMasterData>();
		doNothing().when(productUpdateDAO).batchDeleteFacetDataInTempNoDelta(any(List.class));
		
		// when
		sqlService.batchDeleteFacetDataInTempNoDelta(facetMasterDataList);
		
		// then
		verify(productUpdateDAO,times(1)).batchDeleteFacetDataInTempNoDelta(any(List.class));
	}

	@Test
	void testBatchDeleteStellaDataInTemp() throws MiraklRepositoryException {
		// given
		List<StellaRespData> prdUpdateRespList = new ArrayList<StellaRespData>();
		doNothing().when(productUpdateDAO).batchDeleteStellaDataInTemp(any(List.class));
		
		// when
		sqlService.batchDeleteStellaDataInTemp(prdUpdateRespList);
		
		// then
		verify(productUpdateDAO,times(1)).batchDeleteStellaDataInTemp(any(List.class));
	}

	@Test
	void testBatchInsertPdfMasterData() {
		// given
		List<PdfMasterData> pfdData = new ArrayList<PdfMasterData>();
		doNothing().when(productCreateDAO).batchInsertPdfMasterData(any(List.class));
		
		// when
		sqlService.batchInsertPdfMasterData(pfdData);
		
		// then
		verify(productCreateDAO,times(1)).batchInsertPdfMasterData(any(List.class));
	}

	@Test
	void testBatchInsertStellaMasterData() {
		// given
		List<StellaMasterData> stellaData = new ArrayList<StellaMasterData>();
		doNothing().when(productCreateDAO).batchInsertStellaMasterData(any(List.class));
		
		// when
		sqlService.batchInsertStellaMasterData(stellaData);
		
		// then
		verify(productCreateDAO,times(1)).batchInsertStellaMasterData(any(List.class));
	}

	@Test
	void testBatchInsertImageMasterData() {
		// given
		List<ImageMasterData> imageData = new ArrayList<ImageMasterData>();
		doNothing().when(productCreateDAO).batchInsertImageMasterData(any(List.class));
		
		// when
		sqlService.batchInsertImageMasterData(imageData);
		
		// then
		verify(productCreateDAO,times(1)).batchInsertImageMasterData(any(List.class));
	}

	@Test
	void testBatchInsertFacetMasterData() {
		// given
		List<FacetMasterData> facetData = new ArrayList<FacetMasterData>();
		doNothing().when(productCreateDAO).batchInsertFacetMasterData(any(List.class));
		
		// when
		sqlService.batchInsertFacetMasterData(facetData);
		
		// then
		verify(productCreateDAO,times(1)).batchInsertFacetMasterData(any(List.class));
	}

	@Test
	void testBatchInsertUpcMasterData() {
		// given
		List<UpcMasterData> upcData = new ArrayList<UpcMasterData>();
		doNothing().when(productCreateDAO).batchInsertUpcMasterData(any(List.class));
		
		// when
		sqlService.batchInsertUpcMasterData(upcData);
		
		// then
		verify(productCreateDAO,times(1)).batchInsertUpcMasterData(any(List.class));
	}

	@Test
	void testBatchInsertPdfAuditData() {
		// given
		List<PdfMasterData> pdfDataList = new ArrayList<PdfMasterData>();
		String operation = "";
		String status = "";
		String errorDesc = "";
		doNothing().when(productAuditDAO).batchInsertPdfAuditData(any(String.class), any(String.class), any(String.class), any(List.class));
		
		// when
		sqlService.batchInsertPdfAuditData(operation, status, errorDesc, pdfDataList);
		
		// then
		verify(productAuditDAO,times(1)).batchInsertPdfAuditData(any(String.class), any(String.class), any(String.class), any(List.class));
	}

	@Test
	void testBatchInsertStellaAuditData() {
		// given
		List<StellaMasterData> stellaDataList = new ArrayList<StellaMasterData>();
		String operation = "";
		String status = "";
		String errorDesc = "";
		doNothing().when(productAuditDAO).batchInsertStellaAuditData(any(String.class), any(String.class), any(String.class), any(List.class));
		
		// when
		sqlService.batchInsertStellaAuditData(operation, status, errorDesc, stellaDataList);
		
		// then
		verify(productAuditDAO,times(1)).batchInsertStellaAuditData(any(String.class), any(String.class), any(String.class), any(List.class));
	}

	@Test
	void testBatchInsertImageAuditData() {
		// given
		List<ImageMasterData> imageDataList = new ArrayList<ImageMasterData>();
		String operation = "";
		String status = "";
		String errorDesc = "";
		doNothing().when(productAuditDAO).batchInsertImageAuditData(any(String.class), any(String.class), any(String.class), any(List.class));
		
		// when
		sqlService.batchInsertImageAuditData(operation, status, errorDesc, imageDataList);
		
		// then
		verify(productAuditDAO,times(1)).batchInsertImageAuditData(any(String.class), any(String.class), any(String.class), any(List.class));
	}

	@Test
	void testBatchInsertFacetAuditData() {
		// given
		List<FacetMasterData> facetDataList = new ArrayList<FacetMasterData>();
		String operation = "";
		String status = "";
		String errorDesc = "";
		doNothing().when(productAuditDAO).batchInsertFacetAuditData(any(String.class), any(String.class), any(String.class), any(List.class));
		
		// when
		sqlService.batchInsertFacetAuditData(operation, status, errorDesc, facetDataList);
		
		// then
		verify(productAuditDAO,times(1)).batchInsertFacetAuditData(any(String.class), any(String.class), any(String.class), any(List.class));
	}

	@Test
	void testBatchInsertPdfRespAuditData() {
		// given
		List<PdfRespData> pdfDataList = new ArrayList<PdfRespData>();
		String operation = "";
		doNothing().when(productAuditDAO).batchInsertPdfRespAuditData(any(String.class), any(List.class));
		
		// when
		sqlService.batchInsertPdfRespAuditData(operation, pdfDataList);
		
		// then
		verify(productAuditDAO,times(1)).batchInsertPdfRespAuditData(any(String.class), any(List.class));
	}

	@Test
	void testBatchInsertStellaRespAuditData() {
		// given
		List<StellaRespData> stellaDataList = new ArrayList<StellaRespData>();
		String operation = "";
		doNothing().when(productAuditDAO).batchInsertStellaRespAuditData(any(String.class), any(List.class));
		
		// when
		sqlService.batchInsertStellaRespAuditData(operation, stellaDataList);
		
		// then
		verify(productAuditDAO,times(1)).batchInsertStellaRespAuditData(any(String.class), any(List.class));
	}

	@Test
	void testFindFacetJsonFromTemp() throws MiraklRepositoryException {
		// given
		String upcId = "";
		String opDiv = "";
		String fileNameInJson = "";
		String facetJson = "";
		doReturn(facetJson).when(productUpdateDAO).findFacetJsonFromTemp(any(String.class), any(String.class), any(String.class));
		
		// when
		sqlService.findFacetJsonFromTemp(upcId, opDiv, fileNameInJson);
		
		// then
		verify(productUpdateDAO,times(1)).findFacetJsonFromTemp(any(String.class), any(String.class), any(String.class));
	}

	@Test
	void testUpdateFacetJsonInMaster() throws MiraklRepositoryException {
		// given
		String upcId = "";
		String opDiv = "";
		String fileName = "";
		String facetJson = "";
		doNothing().when(productUpdateDAO).updateFacetJsonInMaster(any(String.class), any(String.class), any(String.class), any(String.class));		
		
		// when
		sqlService.updateFacetJsonInMaster(upcId, opDiv, fileName, facetJson);
		
		// then
		verify(productUpdateDAO,times(1)).updateFacetJsonInMaster(any(String.class), any(String.class), any(String.class), any(String.class));
	}

	@Test
	void testBatchDeleteFacetDataInTemp() throws MiraklRepositoryException {
		// given
		List<FacetUpdateRespData> facetUpdateRespList = new ArrayList<FacetUpdateRespData>();
		doNothing().when(productUpdateDAO).batchDeleteFacetDataInTemp(any(List.class));
		
		// when
		sqlService.batchDeleteFacetDataInTemp(facetUpdateRespList);
		
		// then
		verify(productUpdateDAO,times(1)).batchDeleteFacetDataInTemp(any(List.class));
	}

	@Test
	void testBatchInsertFacetRespAuditData() {
		// given
		List<FacetUpdateRespData> facetDataList = new ArrayList<FacetUpdateRespData>();
		String operation = "";
		doNothing().when(productAuditDAO).batchInsertFacetRespAuditData(any(String.class), any(List.class));
		
		// when
		sqlService.batchInsertFacetRespAuditData(operation, facetDataList);
		
		// then
		verify(productAuditDAO,times(1)).batchInsertFacetRespAuditData(any(String.class), any(List.class));
	}

	@Test
	void testBatchUpdateFacetJsonInMaster() {
		// given
		List<FacetUpdateRespData> facetSuccesDataList = new ArrayList<FacetUpdateRespData>();
		doNothing().when(productUpdateDAO).batchUpdateFacetJsonInMaster(any(List.class));
		
		// when
		sqlService.batchUpdateFacetJsonInMaster(facetSuccesDataList);
		
		// then
		verify(productUpdateDAO,times(1)).batchUpdateFacetJsonInMaster(any(List.class));
	}

	@Test
	void testBatchUpdatePdfJsonInMaster() {
		// given
		List<PdfRespData> pdfSuccessDataList = new ArrayList<PdfRespData>();
		doNothing().when(productUpdateDAO).batchUpdatePdfJsonInMaster(any(List.class));
		
		// when
		sqlService.batchUpdatePdfJsonInMaster(pdfSuccessDataList);
		
		// then
		verify(productUpdateDAO,times(1)).batchUpdatePdfJsonInMaster(any(List.class));
	}

	@Test
	void testBatchUpdateStellaJsonInMaster() {
		// given
		List<StellaRespData> stellaSuccessDataList = new ArrayList<StellaRespData>();
		doNothing().when(productUpdateDAO).batchUpdateStellaJsonInMaster(any(List.class));
		
		// when
		sqlService.batchUpdateStellaJsonInMaster(stellaSuccessDataList);
		
		// then
		verify(productUpdateDAO,times(1)).batchUpdateStellaJsonInMaster(any(List.class));
	}

	@Test
	void testBatchUpdateImagesMasterDataList() {
		// given
		List<ImageMasterData> imageDatalist = new ArrayList<ImageMasterData>();
		doNothing().when(productUpdateDAO).batchUpdateImagesMasterDataList(any(List.class));
		
		// when
		sqlService.batchUpdateImagesMasterDataList(imageDatalist);
		
		// then
		verify(productUpdateDAO,times(1)).batchUpdateImagesMasterDataList(any(List.class));
	}

	@Test
	void testDeleteAppDataInTemp() {
		// given
		MiraklData product = new MiraklData();	
		String app = "";
		doNothing().when(productUpdateDAO).deleteAppDataInTemp(product, app);
		
		// when
		sqlService.deleteAppDataInTemp(product, app);
		
		// then
		verify(productUpdateDAO,times(1)).deleteAppDataInTemp(product, app);
	}
	
	@Test
	void testInsertAppAuditData() {
		// given
		MiraklData product = new MiraklData();
		String operation = "";
		String status = "";
		String errorDesc = "";	
		String app = "";
		doNothing().when(productAuditDAO).insertAppAuditData(operation, status, errorDesc, product, app);
		
		// when
		sqlService.insertAppAuditData(operation, status, errorDesc, product, app);
		
		// then
		verify(productAuditDAO,times(1)).insertAppAuditData(operation, status, errorDesc, product, app);
	}
	
	@Test
	void testInsertFacetAuditData() {
		// given
		MiraklData product = new MiraklData();
		String operation = "";
		String status = "";
		String errorDesc = "";	
		doNothing().when(productAuditDAO).insertFacetAuditData(operation, status, errorDesc, product);
		
		// when
		sqlService.insertFacetAuditData(operation, status, errorDesc, product);
		
		// then
		verify(productAuditDAO,times(1)).insertFacetAuditData(operation, status, errorDesc, product);
	}
	
	@Test
	public void testUpdateImagesRespMessage() throws MiraklRepositoryException {

		// given
		ImageUpdateRespData imageUpdateRespData = new ImageUpdateRespData();
		doNothing().when(productUpdateDAO).updateImagesRespMessage(imageUpdateRespData);

		// when
		sqlService.updateImagesRespMessage(imageUpdateRespData);

		// then
		verify(productUpdateDAO, times(1)).updateImagesRespMessage(imageUpdateRespData);
	}
	
	@Test
	public void testFetchImagesRespMessage() throws MiraklRepositoryException {

		// given
		ImageUpdateRespData imageUpdateRespData = new ImageUpdateRespData();
		ImageUpdateRespData imageUpdateRespDataFromDB = new ImageUpdateRespData();
		doReturn(imageUpdateRespDataFromDB).when(productUpdateDAO).fetchImagesRespMessage(imageUpdateRespData);

		// when
		imageUpdateRespDataFromDB = sqlService.fetchImagesRespMessage(imageUpdateRespData);

		// then
		verify(productUpdateDAO, times(1)).fetchImagesRespMessage(imageUpdateRespData);
	}
	
	@Test
	public void testUpdateImagesMasterJson() throws MiraklRepositoryException {

		// given
		ImageMasterData imageMasterData = new ImageMasterData();
		doNothing().when(productUpdateDAO).updateImagesMasterJson(imageMasterData);

		// when
		sqlService.updateImagesMasterJson(imageMasterData);

		// then
		verify(productUpdateDAO, times(1)).updateImagesMasterJson(imageMasterData);

	}
	
	@Test
	public void testInsertImagesAuditData() {

		// given
		ImageUpdateRespData imageUpdateRespData = new ImageUpdateRespData();
		String operation = "";
		String productType = "";
		String inputFileName = "";
		String jsonData = "";
		doNothing().when(productAuditDAO).insertImagesAuditData(operation, imageUpdateRespData, productType,
				inputFileName, jsonData);

		// when
		sqlService.insertImagesAuditData(operation, imageUpdateRespData, productType, inputFileName, jsonData);

		// then
		verify(productAuditDAO, times(1)).insertImagesAuditData(operation, imageUpdateRespData, productType,
				inputFileName, jsonData);

	}
	
	@Test
	public void testBatchInsertImageResData() {
		// given
		List<ImageUpdateRespData> imageUpdateRespDataList = new ArrayList<ImageUpdateRespData>();
		doNothing().when(productUpdateDAO).batchInsertImageResData(any(List.class));
		
		// when
		sqlService.batchInsertImageResData(imageUpdateRespDataList);
		
		// then
		verify(productUpdateDAO,times(1)).batchInsertImageResData(any(List.class));
	}
	
	@Test
	public void testFindNotificationDetails() {
		// given
		int count = 0;
		String subName = "";
		String fileName = "";
		String bucketName = "";
		doReturn(count).when(notificationDAO).findNotificationDetails(any(String.class), any(String.class), any(String.class));
		
		// when
		sqlService.findNotificationDetails(subName, fileName, bucketName);	
				
		// then
		verify(notificationDAO,times(1)).findNotificationDetails(any(String.class), any(String.class), any(String.class));
	}
	
	@Test
	public void testInsertNotificationDetails() {
		// given
		String subName = "";
		String fileName = "";
		String bucketName = "";
		doNothing().when(notificationDAO).insertNotificationDetails(any(String.class), any(String.class), any(String.class));
		
		// when
		sqlService.insertNotificationDetails(subName, fileName, bucketName);	
				
		// then
		verify(notificationDAO,times(1)).insertNotificationDetails(any(String.class), any(String.class), any(String.class));		
	}

}

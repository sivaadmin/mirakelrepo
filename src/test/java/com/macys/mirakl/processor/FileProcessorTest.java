package com.macys.mirakl.processor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.ResourceUtils;

import com.macys.mirakl.model.FacetUpdateRespData;
import com.macys.mirakl.model.MiraklData;
import com.macys.mirakl.model.OfferIdRequestData;
import com.macys.mirakl.model.OfferIdResponseData;
import com.macys.mirakl.model.ProductUpdateRespData;
import com.macys.mirakl.util.FilePrefix;


@ExtendWith(MockitoExtension.class)
class FileProcessorTest {

    private FileProcessor fileProcessor;


    @BeforeEach
    void setUp() {
        fileProcessor = new FileProcessor();
    }

    @AfterEach
    void tearDown() {
        fileProcessor = null;
    }

    @Test
    void testGetFileOperation() {
        //Given
        String objectName1 = "MCOM/product_create/MCOM_product_create_220518_010101.json";
        String objectName2 = "MCOM/product_update/MCOM_product_update_220518_010101.json";
        String objectName3 = "MCOM_product_create_220518_010101.json";
        String objectName4 = "MCOM_product_create_220518_010101";
        String objectName5 = "BCOM/product_create/BCOM_product_create_220518_010101.json";
        String objectName6 = "BCOM/product_update/BCOM_product_update_220518_010101.json";

        //When
        String fileOperationCreate = fileProcessor.getFileOperation(objectName1);
        String fileOperationUpdate = fileProcessor.getFileOperation(objectName2);
        String fileOperationWrong = fileProcessor.getFileOperation(objectName3);
        String fileOperationWrong1 = fileProcessor.getFileOperation(objectName4);
        String fileOperationCreateBcom = fileProcessor.getFileOperation(objectName5);
        String fileOperationUpdateMcom = fileProcessor.getFileOperation(objectName6);

        //Then
        assertEquals(fileOperationCreate, "PRODUCT_CREATE");
        assertEquals(fileOperationUpdate, "PRODUCT_UPDATE");
        assertEquals(fileOperationWrong, "WRONG_FILE");
        assertEquals(fileOperationWrong1, "WRONG_FILE");
        assertEquals(fileOperationCreateBcom, "PRODUCT_CREATE");
        assertEquals(fileOperationUpdateMcom, "PRODUCT_UPDATE");


    }

    @Test
    void testGetFileName() {
        //Given
        String objectName = "MCOM/product_create/MCOM_product_create_220518_010101.json";
        String fileName = "MCOM_product_create_220518_010101.json";
        //When
        String actualFileName = fileProcessor.getFileName(objectName);
        //Then
        assertEquals(actualFileName, fileName);
    }

    @Test
    void testGetFileOperationForOfferId() {
        //Given
        String objectName1 = "BCOM/offer/BCOM_offer_220419_075859.json";
        String objectName2 = "MCOM/offer/MCOM_offer_220419_075859.json";
        String objectName3 = "MCOM_offer_220419_075859.json";
        String objectName4 = "BCOM/offer/MCOM_offer_220419_075859.json";
        String objectName5 = "BCOM/offer/BCOM_offer_220419_075859";
        String objectName6 = "BCOM/offer_response/BCOM_offer_220419_075859_1.json";
        String objectName7 = "MCOM/offer_response/MCOM_offer_220419_075859_1.json";
        String objectName8 = "BCOM/offer/BCOM_offer_220419_075859_1.json";
        ReflectionTestUtils.setField(fileProcessor, "offerFileVersion", "offer");

        //When
        String fileOperation1 = fileProcessor.getFileOperationForOfferId(objectName1);
        String fileOperation2 = fileProcessor.getFileOperationForOfferId(objectName2);
        String fileOperation3 = fileProcessor.getFileOperationForOfferId(objectName3);
        String fileOperation4 = fileProcessor.getFileOperationForOfferId(objectName4);
        String fileOperation5 = fileProcessor.getFileOperationForOfferId(objectName5);
        String fileOperation6 = fileProcessor.getFileOperationForOfferId(objectName6);
        String fileOperation7 = fileProcessor.getFileOperationForOfferId(objectName7);
        String fileOperation8 = fileProcessor.getFileOperationForOfferId(objectName8);

        //Then
        assertEquals(fileOperation1, "OFFER_REQ_FILE");
        assertEquals(fileOperation2, "OFFER_REQ_FILE");
        assertEquals(fileOperation3, "WRONG_FILE");
        assertEquals(fileOperation4, "WRONG_FILE");
        assertEquals(fileOperation5, "WRONG_FILE");
        assertEquals(fileOperation6, "OFFER_RESP_FILE");
        assertEquals(fileOperation7, "OFFER_RESP_FILE");
        assertEquals(fileOperation8, "WRONG_FILE");

    }

    @Test
    void testGetFileOperationForSecondOfferVesrion() {
        //Given
        String objectName1 = "BCOM/offer/BCOM_offer_v2_220419_075859.json";
        String objectName2 = "MCOM/offer/MCOM_offer_v2_220419_075859.json";
        String objectName3 = "MCOM_offer_v2_220419_075859.json";
        String objectName4 = "BCOM/offer/MCOM_offer_v2_220419_075859.json";
        String objectName5 = "BCOM/offer/BCOM_offer_v2_220419_075859";
        String objectName6 = "BCOM/offer_response/BCOM_offer_v2_220419_075859_1.json";
        String objectName7 = "MCOM/offer_response/MCOM_offer_v2_220419_075859_1.json";
        String objectName8 = "BCOM/offer/BCOM_offer_v2_2220419_075859_1.json";
        ReflectionTestUtils.setField(fileProcessor, "offerFileVersion", "offer_v2");

        //When
        String fileOperation1 = fileProcessor.getFileOperationForOfferId(objectName1);
        String fileOperation2 = fileProcessor.getFileOperationForOfferId(objectName2);
        String fileOperation3 = fileProcessor.getFileOperationForOfferId(objectName3);
        String fileOperation4 = fileProcessor.getFileOperationForOfferId(objectName4);
        String fileOperation5 = fileProcessor.getFileOperationForOfferId(objectName5);
        String fileOperation6 = fileProcessor.getFileOperationForOfferId(objectName6);
        String fileOperation7 = fileProcessor.getFileOperationForOfferId(objectName7);
        String fileOperation8 = fileProcessor.getFileOperationForOfferId(objectName8);

        //Then
        assertEquals(fileOperation1, "OFFER_REQ_FILE");
        assertEquals(fileOperation2, "OFFER_REQ_FILE");
        assertEquals(fileOperation3, "WRONG_FILE");
        assertEquals(fileOperation4, "WRONG_FILE");
        assertEquals(fileOperation5, "WRONG_FILE");
        assertEquals(fileOperation6, "OFFER_RESP_FILE");
        assertEquals(fileOperation7, "OFFER_RESP_FILE");
        assertEquals(fileOperation8, "WRONG_FILE");

    }

    @Test
    void testInputStreamToString()  throws IOException {
        //Given
        InputStream inputStream = new FileInputStream(ResourceUtils.getFile("classpath:request/MCOM_offer_yyMMdd_hhmmss.json"));


        //When
        String jsonInput =fileProcessor.inputStreamToString(inputStream);
        //Then
        assertThat(jsonInput).contains("12345678901");
        assertThat(jsonInput).contains("12345678902");
    }

    @Test
    void testGetFilePath_for_PDF() {
        //Given
        String application ="PDF";
        String fileName ="BCOM_product_update_220329_191088.json";
        String expectedDeltaFilePath ="BCOM/pdf/BCOM_product_update_pdf_220329_191088.json";
        //When
        String actualDeltaFilePath =fileProcessor.getFilePath(application, fileName);
        //Then
        assertThat(actualDeltaFilePath).isEqualTo(expectedDeltaFilePath);
    }

    @Test
    void testGetFilePath_for_IMAGE() {
        //Given
        String application ="Images";
        String fileName ="BCOM_product_update_220329_191088.json";
        String expectedDeltaFilePath ="BCOM/image_change_detection/image_update_220329_191088.json";
        //When
        String actualDeltaFilePath =fileProcessor.getFilePath(application, fileName);
        //Then
        assertThat(actualDeltaFilePath).isEqualTo(expectedDeltaFilePath);
    }

    @Test
    void testProcessUpdateJSON() throws IOException {
        //Given
        String fileName="MCOM_product_update_yyMMdd_hhmmss.json";
        InputStream in =new FileInputStream(ResourceUtils.getFile("classpath:request/MCOM_product_update_yyMMdd_hhmmss.json"));
        String jsonInput=fileProcessor.inputStreamToString(in);
        //When
        List<MiraklData> miraklDataList= fileProcessor.processUpdateJSON(fileName,jsonInput);
        //Then
        assertThat(miraklDataList).isNotNull().isNotEmpty();
        assertThat(miraklDataList).filteredOn("upcId","12345678901").isNotEmpty();

    }

    @Test
    void testProcessOfferIdReqJSON() throws IOException {
        //Given
        InputStream inputStream = new FileInputStream(ResourceUtils.getFile("classpath:request/MCOM_offer_yyMMdd_hhmmss.json"));
        String jsonInput=fileProcessor.inputStreamToString(inputStream);
        //When
        List<OfferIdRequestData> offerIdReqList=fileProcessor.processOfferIdReqJSON(jsonInput);
        //Then
        assertThat(offerIdReqList).isNotNull().isNotEmpty();
        assertThat(offerIdReqList).hasSize(3);
        assertThat(offerIdReqList).filteredOn("upcId","12345678901").isNotEmpty();
        assertThat(offerIdReqList).filteredOn("upcId","12345678902").isNotEmpty();
    }

    @Test
    void testProcessOfferIdRespJSON() throws IOException {
        //Given
        InputStream inputStream = new FileInputStream(ResourceUtils.getFile("classpath:response/MCOM_offer_220427_010122_1.json"));
        String jsonInput=fileProcessor.inputStreamToString(inputStream);
        //When
        List<OfferIdResponseData> offerIdRespList=fileProcessor.processOfferIdRespJSON(jsonInput);
        //Then
        assertThat(offerIdRespList).isNotNull().isNotEmpty();
        assertThat(offerIdRespList).hasSize(4);
        assertThat(offerIdRespList).filteredOn("upcId","890345167342").isNotEmpty();
        assertThat(offerIdRespList).filteredOn("upcId","890345167359").isNotEmpty();
        assertThat(offerIdRespList).filteredOn("upcId","794278367117").isNotEmpty();
        assertThat(offerIdRespList).filteredOn("upcId","12345678901").isNotEmpty();
    }

    @Test
    void testProcessPrdUpdateRespJSON() throws IOException {
        //Given

        InputStream inputStream = new FileInputStream(ResourceUtils.getFile("classpath:response/MCOM_product_update_pdf_220610_191001_1.json"));
        String jsonInput=fileProcessor.inputStreamToString(inputStream);

        //When
        List<ProductUpdateRespData> productUpdateRespDataList=fileProcessor.processPrdUpdateRespJSON(jsonInput);
        //Then
        assertThat(productUpdateRespDataList).isNotNull().isNotEmpty();
        assertThat(productUpdateRespDataList).hasSize(2);
    }

    @Test
    void testProcessFacetUpdateRespJSON() throws IOException {
        //Given
        InputStream inputStream = new FileInputStream(ResourceUtils.getFile("classpath:response/MCOM_product_update_facet_220610_191001_1.json"));
        String jsonInput=fileProcessor.inputStreamToString(inputStream);
        //When
        List<FacetUpdateRespData>  facetUpdateRespDataList=fileProcessor.processFacetUpdateRespJSON(jsonInput);
        //Then
        assertThat(facetUpdateRespDataList).isNotNull().isNotEmpty();
        assertThat(facetUpdateRespDataList).hasSize(1);
    }

    @Test
    void testGetFileOperationForRespFile(){
        //Given
        String objectNameWrong ="MCOM/offer/MCOM_offer_220614_191001.json";
        String objectNameProductUpdateMcomPdf ="MCOM/product_update_response/MCOM_product_update_pdf_220614_191001_1.json";
        String objectNameMcomFacetUpdate ="MCOM/facet/MCOM_product_update_facet_220614_191001_1.json";
        String objectNameProductUpdateBcomPdf ="BCOM/product_update_response/BCOM_product_update_pdf_220614_191001_1.json";
        String objectNameBcomFacetUpdate ="BCOM/facet/BCOM_product_update_facet_220614_191001_1.json";
        String objectNameProductUpdateMcomStella ="MCOM/product_update_response/MCOM_product_update_stella_220614_191001_1.json";
        String objectNameProductUpdateBcomStella ="BCOM/product_update_response/BCOM_product_update_stella_220614_191001_1.json";

        //When
        String operation1 =fileProcessor.getFileOperationForRespFile(objectNameWrong);
        String operation2 =fileProcessor.getFileOperationForRespFile(objectNameProductUpdateMcomPdf);
        String operation3 =fileProcessor.getFileOperationForRespFile(objectNameMcomFacetUpdate);
        String operation4 =fileProcessor.getFileOperationForRespFile(objectNameBcomFacetUpdate);
        String operation5 =fileProcessor.getFileOperationForRespFile(objectNameProductUpdateBcomPdf);
        String operation6 =fileProcessor.getFileOperationForRespFile(objectNameProductUpdateMcomStella);
        String operation7 =fileProcessor.getFileOperationForRespFile(objectNameProductUpdateBcomStella);

        //Then
        assertThat(operation1).isEqualTo("WRONG_FILE");
        assertThat(operation2).isEqualTo("PRODUCT_UPDATE_RESP");
        assertThat(operation3).isEqualTo("FACET_UPDATE_RESP");
        assertThat(operation4).isEqualTo("FACET_UPDATE_RESP");
        assertThat(operation5).isEqualTo("PRODUCT_UPDATE_RESP");
        assertThat(operation6).isEqualTo("PRODUCT_UPDATE_RESP");
        assertThat(operation7).isEqualTo("PRODUCT_UPDATE_RESP");

    }

    @Test
    public void testGetName() {
        // given
        String fileName = "MCOM_product_update_220715_898991.json";

        // when
        String deltaFileName = fileProcessor.getName(fileName);

        // then
        assertThat(deltaFileName).isEqualTo("image_update_220715_898991.json");
    }
    
    @Test
	void getFilePrefix() {
		String mcomFileName = "MCOM_product_create_220518_010101.json";
		String bcomFileName = "BCOM_product_create_220518_010101.json";
		FilePrefix mcom = FileProcessor.getFilePrefix(mcomFileName);
		FilePrefix bcom = FileProcessor.getFilePrefix(bcomFileName);

		assertEquals(FilePrefix.MCOM, mcom);
		assertEquals(FilePrefix.BCOM, bcom);
	}
}
package com.macys.mirakl.service;

import static com.macys.mirakl.util.OrchConstants.FACET;
import static com.macys.mirakl.util.OrchConstants.IMAGES;
import static com.macys.mirakl.util.OrchConstants.INPROCESS;
import static com.macys.mirakl.util.OrchConstants.NO_DATA_FOUND;
import static com.macys.mirakl.util.OrchConstants.PDF;
import static com.macys.mirakl.util.OrchConstants.PRODUCT_UPDATE;
import static com.macys.mirakl.util.OrchConstants.STELLA;
import static com.macys.mirakl.util.OrchConstants.UPDATE_SUCCESS;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.util.ReflectionTestUtils;

import com.google.gson.Gson;
import com.macys.mirakl.exception.MiraklRepositoryException;
import com.macys.mirakl.model.FacetMasterData;
import com.macys.mirakl.model.ImageMasterData;
import com.macys.mirakl.model.MiraklData;
import com.macys.mirakl.model.PdfMasterData;
import com.macys.mirakl.model.StellaMasterData;
import com.macys.mirakl.processor.FileProcessor;

@ExtendWith(MockitoExtension.class)
class ProductUpdateServiceTest {

    List<MiraklData> miraklDataList = new ArrayList<>();
    List<PdfMasterData> pdfData = new ArrayList<>();
    List<StellaMasterData> stellaData = new ArrayList<>();
    List<ImageMasterData> imageData = new ArrayList<>();
    List<FacetMasterData> facetData = new ArrayList<>();
    MiraklData miraklData;
    String pdfDelta;
    String stellaDelta;
    String imageDelta;
    JSONArray jsonArrayPDFError = new JSONArray();
	JSONArray jsonArrayStellaError = new JSONArray();
	JSONArray jsonArrayImagesError = new JSONArray();
	JSONArray jsonArrayFacetError = new JSONArray();

    @InjectMocks
    ProductUpdateService productUpdateService;

    @Mock
    private SQLService sql;

    @Mock
    private CloudStorageService cloudStorageService;

    @Mock
    private FileProcessor fileProcessor;

    @BeforeEach
    void setUp() {

        //given
        miraklData = MiraklData.builder()
                .fileName("MCOM_product_update_220520_191088.json")
                .upcId("191019881001")
                .opDiv("12")
                .productType("Watch")
                .taxCode("dummyValue")
                .nrfSizeCode("12")
                .msrp("12")
                .dept("902")
                .vendor("1")
                .pid("18-aef5-d5bea37482d5")
                .nrfColorCode("240")
                .pdfData("{\"BrandName\":\"macys_new\"}")
                .stellaData("{\"CustomerFacingPidDescription\":\"Name_New\",\"CustomerFacingCNewescription\":\"OpenWhite\",\"LongDescription\":\"Name_New\",\"FabricCare\":\"Machinewashable\",\"FabricContent\":\"Name_New\",\"CountryOfOrigin\":\"Imported\",\"Warranty\":\"N\",\"InternationalShipping\":\"N\",\"LegalWarnings\":\"None\",\"F&BBullet1\":\"Name_New\",\"F&BBullet2\":\"Name_New\",\"F&BBullet3\":\"Name_New\",\"F&BBullet20\":\"Bowls\",\"ProductDimensions1\":\"Name_New\",\"ProductDimensions3\":\"Name_New\"}")
                .imagesData("[{\"ImageID\":\"fc51acab50ed43efa922eca86b29717_new\",\"ImageType\":\"mainImage\",\"ImageURL\":\"https://media-dev-eu-2.mirakl.net/SOURCE/fc51acab50ed43efa922eca86b29717_new\"},{\"ImageID\":\"fc51acab50ed43efa922eca86b29718_new\",\"ImageType\":\"swatch\",\"ImageURL\":\"https://media-dev-eu-2.mirakl.net/SOURCE/fc51acab50ed43efa922eca86b29718_new\"}]")
                .facetData("[{\"AttributeName\":\"Occasion\",\"AttributeValue\":\"Active\"},{\"AttributeName\":\"PetAccessories\",\"AttributeValue\":\"PetBowls\"},{\"AttributeName\":\"Attribute3\",\"AttributeValue\":\"\"}]")
                .build();

        miraklDataList.add(miraklData);
        PdfMasterData pdfInfo = new PdfMasterData(miraklData.getFileName(), miraklData.getUpcId(),
                miraklData.getOpDiv(), miraklData.getProductType(), miraklData.getPdfData());
        pdfData.add(pdfInfo);
        StellaMasterData stellaInfo = new StellaMasterData(miraklData.getFileName(), miraklData.getUpcId(),
                miraklData.getOpDiv(),
                miraklData.getProductType(), miraklData.getStellaData());
        stellaData.add(stellaInfo);
        ImageMasterData imageInfo = new ImageMasterData(miraklData.getFileName(), miraklData.getUpcId(),
                miraklData.getOpDiv(), miraklData.getProductType(), miraklData.getImagesData());
        imageData.add(imageInfo);

        FacetMasterData facetInfo = new FacetMasterData(miraklData.getFileName(), miraklData.getUpcId(),
                miraklData.getOpDiv(), miraklData.getProductType(), miraklData.getDept(), miraklData.getVendor(),
                miraklData.getPid(), miraklData.getNrfColorCode(), miraklData.getFacetData());
        facetData.add(facetInfo);

        pdfDelta = "{\"MIRKL\":{\"PDF\":[{\"UPCID\":\"191019881001\",\"OP_DIV\":\"12\",\"ProductType\":\"Watch\",\"delta\":{\"BrandName\":\"macys_new\"}}]}}";
        stellaDelta = "{\"MIRKL\":{\"STELLA\":[{\"UPCID\":\"191019881001\",\"OP_DIV\":\"12\",\"ProductType\":\"Watch\",\"delta\":{\"CustomerFacingPidDescription\":\"Name_New\"}}]}}";
        imageDelta = "{\"MIRKL\":{\"imageData\":[{\"isMainImgP0\":\"false\",\"upcId\":\"191019881001\",\"opdiv\":\"12\",\"swImage\":\"https://media-dev-eu-2.mirakl.net/SOURCE/fc51acab50ed43efa922eca86b29718_new\",\"mainImage\":\"https://media-dev-eu-2.mirakl.net/SOURCE/fc51acab50ed43efa922eca86b29717_new\"}]}}";

        ReflectionTestUtils.setField(productUpdateService, "deltaBucket", "deltaBucket");
        ReflectionTestUtils.setField(productUpdateService, "imageDeltaBucket", "imageDeltaBucket");

    }

    @Test
    void testIdentifyDeltaFromIncomingFacetAttributesCase1() {
        //Given
        String facetDataMaster = "[{\"AttributeName\":\"Occasion\",\"AttributeValue\":\"Active\"}," +
                "{\"AttributeName\":\"Pet Accessories\",\"AttributeValue\":\"Pet Bowls\"}]";
        String facetDataIncomingCase01 = "[{\"AttributeName\":\"Occasion\",\"AttributeValue\":\"Not Active\"}," +
                "{\"AttributeName\":\"Pet Accessories\",\"AttributeValue\":\"Pet Bowls\"}]";
        MiraklData product = MiraklData.builder().facetData(facetDataIncomingCase01).upcId("11111111111").opDiv("12")
                .productType("Watch")
                .dept("902").vendor("1").pid("18-aef5-d5bea37482d5").nrfColorCode("240").build();

        String expectedResp = "{\"UPCID\":\"11111111111\",\"ProductType\":\"Watch\",\"OP_DIV\":\"12\",\"dept\":\"902\"," +
                "\"vendor\":\"1\",\"pid\":\"18-aef5-d5bea37482d5\",\"nrfColorCode\":\"240\"," +
                "\"attributes\":[{\"AttributeName\":\"Occasion\",\"AttributeValue\":\"Not Active\"}]}";
        FacetMasterData facetMasterData = FacetMasterData.builder().facetData(facetDataMaster).upcId("11111111111")
                .opDiv("12").productType("Watch").build();
        List<FacetMasterData> facetDataList = new ArrayList<>();
        when(sql.findFacetMasterJSONByUpc("11111111111", "12")).thenReturn(facetMasterData);

        //When
        JSONObject actualResponse = productUpdateService.identifyDeltaFromIncomingFacetAttributes(product,facetDataList);

        //Then
        verify(sql, times(1)).findFacetMasterJSONByUpc("11111111111", "12");
        JSONAssert.assertEquals("Expected JSON and Generated JSON should have same attributes and values",expectedResp, actualResponse, false);

    }

    @Test
    void testIdentifyDeltaFromIncomingFacetAttributesCase2() {
        //Given
        String facetDataMaster = "[{\"AttributeName\":\"Occasion\",\"AttributeValue\":\"Active\"}," +
                "{\"AttributeName\":\"Pet Accessories\",\"AttributeValue\":\"Pet Bowls\"}]";
        String facetDataIncomingCase02 = "[{\"AttributeName\":\"Occasion\",\"AttributeValue\":\"Not Active\"}," +
                "{\"AttributeName\":\"Pet Accessories\",\"AttributeValue\":\"Pet Bowls\"}," +
                "{\"AttributeName\":\"New Attribute\",\"AttributeValue\":\"New Value\"}]";
        MiraklData product = MiraklData.builder().facetData(facetDataIncomingCase02).upcId("11111111111").opDiv("12")
                .productType("Watch")
                .dept("902").vendor("1").pid("18-aef5-d5bea37482d5").nrfColorCode("240").build();

        String expectedResp = "{\"UPCID\":\"11111111111\",\"ProductType\":\"Watch\",\"OP_DIV\":\"12\",\"dept\":\"902\",\"vendor\":\"1\"," +
                "\"pid\":\"18-aef5-d5bea37482d5\",\"nrfColorCode\":\"240\"," +
                "\"attributes\":[{\"AttributeName\":\"Occasion\",\"AttributeValue\":\"Not Active\"}," +
                "{\"AttributeName\":\"New Attribute\",\"AttributeValue\":\"New Value\"}]}";
        FacetMasterData facetMasterData = FacetMasterData.builder().facetData(facetDataMaster).upcId("11111111111")
                .opDiv("12").productType("Watch").build();
        List<FacetMasterData> facetDataList = new ArrayList<>();
        when(sql.findFacetMasterJSONByUpc("11111111111", "12")).thenReturn(facetMasterData);

        //When
        JSONObject actualResponse = productUpdateService.identifyDeltaFromIncomingFacetAttributes(product,facetDataList);

        //Then
        verify(sql, times(1)).findFacetMasterJSONByUpc("11111111111", "12");
        JSONAssert.assertEquals("Expected JSON and Generated JSON should have same attributes and values",expectedResp, actualResponse, false);

    }

    @Test
    void testIdentifyDeltaFromIncomingFacetAttributesCase3() {
        //Given
        String facetDataMaster = "[{\"AttributeName\":\"Occasion\",\"AttributeValue\":\"Active\"}," +
                "{\"AttributeName\":\"Pet Accessories\",\"AttributeValue\":\"Pet Bowls\"}," +
                "{\"AttributeName\":\"Attribute3\",\"AttributeValue\":\"Value3\"}]";
        String facetDataIncomingCase03 = "[{\"AttributeName\":\"Occasion\",\"AttributeValue\":\"Not Active\"}," +
                "{\"AttributeName\":\"Pet Accessories\",\"AttributeValue\":\"Pet Bowls\"}," +
                "{\"AttributeName\":\"Attribute3\",\"AttributeValue\":\"\"}]";
        MiraklData product = MiraklData.builder().facetData(facetDataIncomingCase03).upcId("11111111111").opDiv("12")
                .productType("Watch")
                .dept("902").vendor("1").pid("18-aef5-d5bea37482d5").nrfColorCode("240").build();

        String expectedResp = "{\"UPCID\":\"11111111111\",\"ProductType\":\"Watch\",\"OP_DIV\":\"12\",\"dept\":\"902\",\"vendor\":\"1\"," +
                "\"pid\":\"18-aef5-d5bea37482d5\",\"nrfColorCode\":\"240\"," +
                "\"attributes\":[{\"AttributeName\":\"Occasion\",\"AttributeValue\":\"Not Active\"}," +
                "{\"AttributeName\":\"Attribute3\",\"AttributeValue\":\"\"}]}";
        FacetMasterData facetMasterData = FacetMasterData.builder().facetData(facetDataMaster).upcId("11111111111")
                .opDiv("12").productType("Watch").build();
        List<FacetMasterData> facetDataList = new ArrayList<>();
        when(sql.findFacetMasterJSONByUpc("11111111111", "12")).thenReturn(facetMasterData);

        //When
        JSONObject actualResponse = productUpdateService.identifyDeltaFromIncomingFacetAttributes(product, facetDataList);

        //Then
        verify(sql, times(1)).findFacetMasterJSONByUpc("11111111111", "12");
        JSONAssert.assertEquals("Expected JSON and Generated JSON should have same attributes and values",expectedResp, actualResponse, false);

    }

    @Test
    void testIdentifyDeltaFromIncomingFacetAttributesCase4() {
        //Given
        String facetDataMaster = "[{\"AttributeName\":\"Occasion\",\"AttributeValue\":\"Active\"}," +
                "{\"AttributeName\":\"Pet Accessories\",\"AttributeValue\":\"Pet Bowls\"}," +
                "{\"AttributeName\":\"Attribute3\",\"AttributeValue\":\"Value3\"}]";
        String facetDataIncomingCase04 = "[{\"AttributeName\":\"Occasion\",\"AttributeValue\":\"Not Active\"}," +
                "{\"AttributeName\":\"Pet Accessories\",\"AttributeValue\":\"Pet Bowls\"}]";
        MiraklData product = MiraklData.builder().facetData(facetDataIncomingCase04).upcId("11111111111").opDiv("12")
                .productType("Watch")
                .dept("902").vendor("1").pid("18-aef5-d5bea37482d5").nrfColorCode("240").build();

        String expectedResp = "{\"UPCID\":\"11111111111\",\"ProductType\":\"Watch\",\"OP_DIV\":\"12\",\"dept\":\"902\"," +
                "\"vendor\":\"1\",\"pid\":\"18-aef5-d5bea37482d5\",\"nrfColorCode\":\"240\"," +
                "\"attributes\":[{\"AttributeName\":\"Occasion\",\"AttributeValue\":\"Not Active\"},{\"AttributeName\":\"Attribute3\",\"AttributeValue\":\"\"}]}";
        FacetMasterData facetMasterData = FacetMasterData.builder().facetData(facetDataMaster).upcId("11111111111")
                .opDiv("12").productType("Watch").build();
        List<FacetMasterData> facetDataList = new ArrayList<>();
        when(sql.findFacetMasterJSONByUpc("11111111111", "12")).thenReturn(facetMasterData);

        //When
        JSONObject actualResponse = productUpdateService.identifyDeltaFromIncomingFacetAttributes(product, facetDataList);

        //Then
        verify(sql, times(1)).findFacetMasterJSONByUpc("11111111111", "12");
        JSONAssert.assertEquals("Expected JSON and Generated JSON should have same attributes and values",expectedResp, actualResponse, false);

    }

    @Test
    void testIdentifyDeltaFromIncomingFacetAttributesCase5() {
        //Given
        String facetDataMaster = "[{\"AttributeName\":\"Occasion\",\"AttributeValue\":\"Active\"}," +
                "{\"AttributeName\":\"Pet Accessories\",\"AttributeValue\":\"Pet Bowls\"}," +
                "{\"AttributeName\":\"Attribute3\",\"AttributeValue\":\"Value3\"}]";
        MiraklData product = MiraklData.builder().facetData(facetDataMaster).upcId("11111111111").opDiv("12")
                .productType("Watch")
                .dept("902").vendor("v1").pid("18-aef5-d5bea37482d5").nrfColorCode("240").build();
        String expectedResp = "{\"UPCID\":\"11111111111\",\"ProductType\":\"Watch\",\"OP_DIV\":\"12\",\"dept\":\"902\"," +
                "\"vendor\":\"v1\",\"pid\":\"18-aef5-d5bea37482d5\",\"nrfColorCode\":\"240\"," +
                "\"Status\":\"400\",\"Message\":\"Invalid Vendor Number\"}";

        FacetMasterData facetMasterData = FacetMasterData.builder().facetData(facetDataMaster).upcId("11111111111")
                .opDiv("12").productType("Watch").build();
        List<FacetMasterData> facetDataList = new ArrayList<>();
        when(sql.findFacetMasterJSONByUpc("11111111111", "12")).thenReturn(facetMasterData);

        //When
        JSONObject actualResponse = productUpdateService.identifyDeltaFromIncomingFacetAttributes(product, facetDataList);

        //Then
        verify(sql, times(1)).findFacetMasterJSONByUpc("11111111111", "12");
        JSONAssert.assertEquals("Expected JSON and Generated JSON should have same attributes and values",expectedResp, actualResponse, false);
    }

    @Test
    void testIdentifyDeltaFromIncomingFacetAttributesCase6() {
        //Given
        String facetDataMaster = "[]";
        String facetDataIncomingCase06 = "[{\"AttributeName\":\"Occasion\",\"AttributeValue\":\"Active\"}," +
                "{\"AttributeName\":\"Pet Accessories\",\"AttributeValue\":\"Pet Bowls\"}," +
                "{\"AttributeName\":\"Attribute3\",\"AttributeValue\":\"Value3\"}]";
        MiraklData product = MiraklData.builder().facetData(facetDataIncomingCase06).upcId("11111111111").opDiv("12")
                .productType("Watch")
                .dept("902").vendor("1").pid("18-aef5-d5bea37482d5").nrfColorCode("240").build();

        String expectedResp = "{\"UPCID\":\"11111111111\",\"ProductType\":\"Watch\",\"OP_DIV\":\"12\",\"dept\":\"902\"," +
                "\"vendor\":\"1\",\"pid\":\"18-aef5-d5bea37482d5\",\"nrfColorCode\":\"240\"," +
                "\"attributes\":[{\"AttributeName\":\"Occasion\",\"AttributeValue\":\"Active\"}," +
                "{\"AttributeName\":\"Pet Accessories\",\"AttributeValue\":\"Pet Bowls\"}," +
                "{\"AttributeName\":\"Attribute3\",\"AttributeValue\":\"Value3\"}]}";
        FacetMasterData facetMasterData = FacetMasterData.builder().facetData(facetDataMaster).upcId("11111111111")
                .opDiv("12").productType("Watch").build();
        List<FacetMasterData> facetDataList = new ArrayList<>();
        when(sql.findFacetMasterJSONByUpc("11111111111", "12")).thenReturn(facetMasterData);

        //When
        JSONObject actualResponse = productUpdateService.identifyDeltaFromIncomingFacetAttributes(product, facetDataList);

        //Then
        verify(sql, times(1)).findFacetMasterJSONByUpc("11111111111", "12");
        JSONAssert.assertEquals("Expected JSON and Generated JSON should have same attributes and values",expectedResp, actualResponse, false);

    }

    @Test
    public void TestProcessProductUpdate_Pdf_Delta() throws IllegalAccessException, MiraklRepositoryException {
        //given
        doNothing().when(sql).batchInsertPdfTempData(pdfData, "PRODUCT_UPDATE");
        doNothing().when(sql).batchInsertStellaTempData(stellaData, "PRODUCT_UPDATE");
        doNothing().when(sql).batchInsertFacetTempData(facetData, "PRODUCT_UPDATE");

        PdfMasterData pdfInfo = new PdfMasterData(miraklData.getFileName(), miraklData.getUpcId(),
                miraklData.getOpDiv(), miraklData.getProductType(), "{\"BrandName\":\"macys_old\"}");

        FacetMasterData facetMasterData = FacetMasterData.builder().facetData(null).upcId(null)
                .opDiv(null).productType(null).build();

        when(sql.findPdfMasterJSONByUpc(miraklData.getUpcId(), miraklData.getOpDiv())).thenReturn(pdfInfo);
        when(sql.findStellaMasterJSONByUpc(miraklData.getUpcId(), miraklData.getOpDiv())).thenReturn(null);
        when(sql.findImageMasterJSONByUpc(miraklData.getUpcId(), miraklData.getOpDiv())).thenReturn(null);
        when(sql.findFacetMasterJSONByUpc(miraklData.getUpcId(), miraklData.getOpDiv())).thenReturn(facetMasterData);

        //when
        productUpdateService.processProductUpdate("MCOM_product_delta_220520_191088.json", miraklDataList);

        //then
        verify(cloudStorageService, times(1)).uploadToCloudStorage(null, new JSONObject(pdfDelta).toString().getBytes(), "deltaBucket");
    }

    @Test
    public void TestProcessProductUpdate_Pdf_Delta_prdTxn_is_null() throws IllegalAccessException, MiraklRepositoryException {

        //when
        productUpdateService.processProductUpdate("MCOM_product_delta_220520_191088.json", new ArrayList<>());

        //then
        verify(sql, never()).findStellaMasterJSONByUpc(miraklData.getUpcId(), miraklData.getOpDiv());
    }

    @Test
    public void TestProcessProductUpdate_Stella_Delta() throws IllegalAccessException, MiraklRepositoryException {
        //given
        doNothing().when(sql).batchInsertPdfTempData(pdfData, "PRODUCT_UPDATE");
        doNothing().when(sql).batchInsertStellaTempData(stellaData, "PRODUCT_UPDATE");
        doNothing().when(sql).batchInsertFacetTempData(facetData, "PRODUCT_UPDATE");

        StellaMasterData stellaInfo = new StellaMasterData(miraklData.getFileName(), miraklData.getUpcId(),
                miraklData.getOpDiv(),
                miraklData.getProductType(), "{\"CustomerFacingPidDescription\":\"Name_Old\",\"CustomerFacingCNewescription\":\"OpenWhite\",\"LongDescription\":\"Name_New\",\"FabricCare\":\"Machinewashable\",\"FabricContent\":\"Name_New\",\"CountryOfOrigin\":\"Imported\",\"Warranty\":\"N\",\"InternationalShipping\":\"N\",\"LegalWarnings\":\"None\",\"F&BBullet1\":\"Name_New\",\"F&BBullet2\":\"Name_New\",\"F&BBullet3\":\"Name_New\",\"F&BBullet20\":\"Bowls\",\"ProductDimensions1\":\"Name_New\",\"ProductDimensions3\":\"Name_New\"}");

        FacetMasterData facetMasterData = FacetMasterData.builder().facetData(null).upcId(null)
                .opDiv(null).productType(null).build();

        when(sql.findPdfMasterJSONByUpc(miraklData.getUpcId(), miraklData.getOpDiv())).thenReturn(null);
        when(sql.findStellaMasterJSONByUpc(miraklData.getUpcId(), miraklData.getOpDiv())).thenReturn(stellaInfo);
        when(sql.findImageMasterJSONByUpc(miraklData.getUpcId(), miraklData.getOpDiv())).thenReturn(null);
        when(sql.findFacetMasterJSONByUpc(miraklData.getUpcId(), miraklData.getOpDiv())).thenReturn(facetMasterData);

        //when
        productUpdateService.processProductUpdate("MCOM_product_delta_220520_191088.json", miraklDataList);

        //then
        verify(cloudStorageService, times(1)).uploadToCloudStorage(null, new JSONObject(stellaDelta).toString().getBytes(), "deltaBucket");
    }

    @Test
    public void  TestProcessProductUpdate_when_master_data_is_not_available() throws IllegalAccessException, MiraklRepositoryException {

        //given
        doNothing().when(sql).batchInsertPdfTempData(pdfData, "PRODUCT_UPDATE");
        doNothing().when(sql).batchInsertStellaTempData(stellaData, "PRODUCT_UPDATE");
        doNothing().when(sql).batchInsertFacetTempData(facetData, "PRODUCT_UPDATE");
        FacetMasterData facetMasterData = FacetMasterData.builder().facetData(null).upcId(null)
                .opDiv(null).productType(null).build();

        when(sql.findPdfMasterJSONByUpc(miraklData.getUpcId(), miraklData.getOpDiv())).thenReturn(null);
        when(sql.findStellaMasterJSONByUpc(miraklData.getUpcId(), miraklData.getOpDiv())).thenReturn(null);
        when(sql.findImageMasterJSONByUpc(miraklData.getUpcId(), miraklData.getOpDiv())).thenReturn(null);
        when(sql.findFacetMasterJSONByUpc(miraklData.getUpcId(), miraklData.getOpDiv())).thenReturn(facetMasterData);

        //when
        productUpdateService.processProductUpdate("MCOM_product_delta_220520_191088.json", miraklDataList);

        //then
        verify(cloudStorageService, never()).uploadToCloudStorage(null, new JSONObject(pdfDelta).toString().getBytes(), "deltaBucket");
        verify(cloudStorageService, never()).uploadToCloudStorage(null, new JSONObject(stellaDelta).toString().getBytes(), "deltaBucket");
        verify(cloudStorageService, never()).uploadToCloudStorage(null, new JSONObject(imageDelta).toString().getBytes(), "imageDeltaBucket");
    }

    @Test
    public void  TestProcessProductUpdate_when_product_type_is_null() throws IllegalAccessException, MiraklRepositoryException {

        //given
        doNothing().when(sql).batchInsertPdfTempData(pdfData, "PRODUCT_UPDATE");
        doNothing().when(sql).batchInsertStellaTempData(stellaData, "PRODUCT_UPDATE");
        doNothing().when(sql).batchInsertFacetTempData(facetData, "PRODUCT_UPDATE");

        PdfMasterData pdfInfo = new PdfMasterData(miraklData.getFileName(), miraklData.getUpcId(),
                miraklData.getOpDiv(), null, miraklData.getPdfData());
        StellaMasterData stellaInfo = new StellaMasterData(miraklData.getFileName(), miraklData.getUpcId(),
                miraklData.getOpDiv(),
                null, miraklData.getStellaData());
        ImageMasterData imageInfo = new ImageMasterData(miraklData.getFileName(),
                miraklData.getUpcId(), miraklData.getOpDiv(), null, miraklData.getImagesData());

        FacetMasterData facetMasterData = FacetMasterData.builder().facetData(null).upcId(null)
                .opDiv(null).productType(null).build();

        when(sql.findPdfMasterJSONByUpc(miraklData.getUpcId(), miraklData.getOpDiv())).thenReturn(pdfInfo);
        when(sql.findStellaMasterJSONByUpc(miraklData.getUpcId(), miraklData.getOpDiv())).thenReturn(stellaInfo);
        when(sql.findImageMasterJSONByUpc(miraklData.getUpcId(), miraklData.getOpDiv())).thenReturn(imageInfo);
        when(sql.findFacetMasterJSONByUpc(miraklData.getUpcId(), miraklData.getOpDiv())).thenReturn(facetMasterData);

        //when
        productUpdateService.processProductUpdate("MCOM_product_delta_220520_191088.json", miraklDataList);

        //then
        verify(cloudStorageService, never()).uploadToCloudStorage(null, new JSONObject(pdfDelta).toString().getBytes(), "deltaBucket");
    }

    @Test
    public void TestProcessProductUpdate_when_no_difference_in_incoming_data () throws IllegalAccessException, MiraklRepositoryException {

        //given
        doNothing().when(sql).batchInsertPdfTempData(pdfData, "PRODUCT_UPDATE");
        doNothing().when(sql).batchInsertStellaTempData(stellaData, "PRODUCT_UPDATE");
        doNothing().when(sql).batchInsertFacetTempData(facetData, "PRODUCT_UPDATE");

        PdfMasterData pdfInfo = new PdfMasterData(miraklData.getFileName(), miraklData.getUpcId(),
                miraklData.getOpDiv(), miraklData.getProductType(), miraklData.getPdfData());
        StellaMasterData stellaInfo = new StellaMasterData(miraklData.getFileName(), miraklData.getUpcId(),
                miraklData.getOpDiv(),
                miraklData.getProductType(), miraklData.getStellaData());
        ImageMasterData imageInfo = new ImageMasterData(miraklData.getFileName(),
                miraklData.getUpcId(), miraklData.getOpDiv(), miraklData.getProductType(), miraklData.getImagesData());
        FacetMasterData facetData = new FacetMasterData(miraklData.getFileName(), miraklData.getUpcId(),
                miraklData.getOpDiv(), miraklData.getProductType(), miraklData.getDept(), miraklData.getVendor(),
                miraklData.getPid(), miraklData.getNrfColorCode(), miraklData.getFacetData());

        when(sql.findPdfMasterJSONByUpc(miraklData.getUpcId(), miraklData.getOpDiv())).thenReturn(pdfInfo);
        when(sql.findStellaMasterJSONByUpc(miraklData.getUpcId(), miraklData.getOpDiv())).thenReturn(stellaInfo);
        when(sql.findImageMasterJSONByUpc(miraklData.getUpcId(), miraklData.getOpDiv())).thenReturn(imageInfo);
        when(sql.findFacetMasterJSONByUpc(miraklData.getUpcId(), miraklData.getOpDiv())).thenReturn(facetData);

        //when
        productUpdateService.processProductUpdate("MCOM_product_delta_220520_191088.json", miraklDataList);

        //then
        verify(cloudStorageService, never()).uploadToCloudStorage(null, new JSONObject(pdfDelta).toString().getBytes(), "deltaBucket");
    }

    @Test
    public void testIdentifyDeltaFromIncomingImages() throws IllegalAccessException {

        //given
        ImageMasterData imageInfo = new ImageMasterData(miraklData.getFileName(), miraklData.getUpcId(),
                miraklData.getOpDiv(), miraklData.getProductType(), "[{\"ImageID\":\"fc51acab50ed43efa922eca86b29717_old\",\"ImageType\":\"mainImage\",\"ImageURL\":\"https://media-dev-eu-2.mirakl.net/SOURCE/fc51acab50ed43efa922eca86b29717_old\"},{\"ImageID\":\"fc51acab50ed43efa922eca86b29718_old\",\"ImageType\":\"swatch\",\"ImageURL\":\"https://media-dev-eu-2.mirakl.net/SOURCE/fc51acab50ed43efa922eca86b29718_old\"}]");

        String expectedResp = "{\"upcId\":\"191019881001\",\"opdiv\":\"12\",\"mainImage\":\"https://media-dev-eu-2.mirakl.net/SOURCE/fc51acab50ed43efa922eca86b29717_new\",\"swImage\":\"https://media-dev-eu-2.mirakl.net/SOURCE/fc51acab50ed43efa922eca86b29718_new\",\"isMainImgP0\":false}";
        List<ImageMasterData> imageNoDeltaList = new ArrayList<>();
        boolean isMainImgP0 = false;
        when(sql.findImageMasterJSONByUpc(miraklData.getUpcId(), miraklData.getOpDiv())).thenReturn(imageInfo);
        when(sql.getIsMainImgP0ByUpc(miraklData.getUpcId(), miraklData.getOpDiv())).thenReturn(isMainImgP0);    

        //when
        JSONObject actualResponse = productUpdateService.identifyDeltaFromIncomingImages(miraklData, imageNoDeltaList);

        //then
        JSONAssert.assertEquals("Expected JSON and Generated JSON should have same attributes and values",expectedResp, actualResponse, false);
    }
    
    @Test
    public void testIdentifyDeltaFromIncomingImages_DB_Exception() throws IllegalAccessException {

        //given
    	List<ImageMasterData> imageNoDeltaList = new ArrayList<>();
        doThrow(EmptyResultDataAccessException.class).when(sql).findImageMasterJSONByUpc(miraklData.getUpcId(), miraklData.getOpDiv());

        //when
		try {
			productUpdateService.identifyDeltaFromIncomingImages(miraklData, imageNoDeltaList);
		} catch(Exception e) {
			assertTrue(e instanceof Exception,"Exception is expected");
		}

        //then
		verify(sql, times(1)).findImageMasterJSONByUpc(miraklData.getUpcId(), miraklData.getOpDiv());
		verify(sql, never()).getIsMainImgP0ByUpc(miraklData.getUpcId(), miraklData.getOpDiv());
        verify(sql, never()).batchUpdateImagesMasterDataList(imageData);
		verify(sql, never()).batchInsertImageAuditData(PRODUCT_UPDATE, UPDATE_SUCCESS, null, imageData);
    }
    
    @Test
    public void testIdentifyDeltaFromIncomingPDF_DB_Exception() throws IllegalAccessException {

        //given
    	Gson gson = new Gson();
        List<PdfMasterData> pdfNoDeltaList = new ArrayList<>();
        doThrow(EmptyResultDataAccessException.class).when(sql).findPdfMasterJSONByUpc(miraklData.getUpcId(), miraklData.getOpDiv());

        //when
		try {
			productUpdateService.identifyDeltaFromIncomingPDF(gson, miraklData, pdfNoDeltaList);
		} catch(Exception e) {
			assertTrue(e instanceof Exception,"Exception is expected");
		}

        //then
		verify(sql, times(1)).findPdfMasterJSONByUpc(miraklData.getUpcId(), miraklData.getOpDiv());      
    }

    @Test
    public void testIdentifyDeltaFromIncomingStella_for_StellaUpdate() throws IllegalAccessException {

        //given
        StellaMasterData stellaInfo = new StellaMasterData(miraklData.getFileName(), miraklData.getUpcId(),
                miraklData.getOpDiv(),
                miraklData.getProductType(), "{\"CustomerFacingPidDescription\":\"Name_Old\",\"CustomerFacingCNewescription\":\"OpenWhite\",\"LongDescription\":\"Name_New\",\"FabricCare\":\"Machinewashable\",\"FabricContent\":\"Name_New\",\"CountryOfOrigin\":\"Imported\",\"Warranty\":\"N\",\"InternationalShipping\":\"N\",\"LegalWarnings\":\"None\",\"F&BBullet1\":\"Name_New\",\"F&BBullet2\":\"Name_New\",\"F&BBullet3\":\"Name_New\",\"F&BBullet20\":\"Bowls\",\"ProductDimensions1\":\"Name_New\",\"ProductDimensions3\":\"Name_New\"}");

        String expectedResp = "{\"UPCID\":\"191019881001\",\"OP_DIV\":\"12\",\"ProductType\":\"Watch\",\"delta\":{\"CustomerFacingPidDescription\":\"Name_New\"}}";
        Gson gson = new Gson();
        List<StellaMasterData> stellaDataList = new ArrayList<>();
        when(sql.findStellaMasterJSONByUpc(miraklData.getUpcId(), miraklData.getOpDiv())).thenReturn(stellaInfo);

        //when
        JSONObject actualResponse = productUpdateService.identifyDeltaFromIncomingStella(gson , miraklData, stellaDataList);

        //then
        JSONAssert.assertEquals("Expected JSON and Generated JSON should have same attributes and values",expectedResp, actualResponse, false);
    }

    @Test
    public void testIdentifyDeltaFromIncomingPDF_for_PdfUpdate() throws IllegalAccessException {

        //given
        PdfMasterData pdfInfo = new PdfMasterData(miraklData.getFileName(), miraklData.getUpcId(),
                miraklData.getOpDiv(), miraklData.getProductType(), "{\"BrandName\":\"macys_old\"}");
        String expectedResp = "{\"UPCID\":\"191019881001\",\"OP_DIV\":\"12\",\"ProductType\":\"Watch\",\"delta\":{\"BrandName\":\"macys_new\"}}";
        Gson gson = new Gson();
        List<PdfMasterData> pdfNoDeltaList = new ArrayList<>();
        when(sql.findPdfMasterJSONByUpc(miraklData.getUpcId(), miraklData.getOpDiv())).thenReturn(pdfInfo);

        //when
        JSONObject actualResponse = productUpdateService.identifyDeltaFromIncomingPDF(gson, miraklData, pdfNoDeltaList);

        //then
        JSONAssert.assertEquals("Expected JSON and Generated JSON should have same attributes and values",expectedResp, actualResponse, false);
    }

    @Test
    public void testWriteToDeltaBucket() {

        //given
        String pdfData = "[{\"UPCID\":\"191019881001\",\"OP_DIV\":\"12\",\"ProductType\":\"Watch\",\"delta\":{\"BrandName\":\"macys_new\"}}]";
        String pdfDelta =   "{\"MIRKL\":{\"PDF\":[{\"UPCID\":\"191019881001\",\"OP_DIV\":\"12\",\"ProductType\":\"Watch\",\"delta\":{\"BrandName\":\"macys_new\"}}]}}";
        JSONArray jsonArrayPdf = new JSONArray(pdfData);

        //when
        productUpdateService.writeToDeltaBucket(miraklData.getFileName(), "PDF", jsonArrayPdf, jsonArrayFacetError);

        //then
        verify(cloudStorageService, times(1)).uploadToCloudStorage(null, new JSONObject(pdfDelta).toString().getBytes(), "deltaBucket");

    }

    @Test
    public void testConvertToByteArray() {
        //given
        String pdfData = "{\"UPCID\":\"191019881001\"}";

        //when
        byte[] actualByte = productUpdateService.convertToByteArray(new JSONObject(pdfData));

        //then
        Assertions.assertEquals(pdfData, new String(actualByte), "the actual byte array should contain " + pdfData);

    }
    
    @Test
    public void TestProcessProductUpdate_When_NoMasterData_AllSystems() throws IllegalAccessException, MiraklRepositoryException {

        //given
        doNothing().when(sql).batchInsertPdfTempData(pdfData, "PRODUCT_UPDATE");
        doNothing().when(sql).batchInsertStellaTempData(stellaData, "PRODUCT_UPDATE");
        doNothing().when(sql).batchInsertFacetTempData(facetData, "PRODUCT_UPDATE");

        doThrow(EmptyResultDataAccessException.class).when(sql).findPdfMasterJSONByUpc(miraklData.getUpcId(), miraklData.getOpDiv());
        doThrow(EmptyResultDataAccessException.class).when(sql).findStellaMasterJSONByUpc(miraklData.getUpcId(), miraklData.getOpDiv());
        doThrow(EmptyResultDataAccessException.class).when(sql).findImageMasterJSONByUpc(miraklData.getUpcId(), miraklData.getOpDiv());
        doThrow(EmptyResultDataAccessException.class).when(sql).findFacetMasterJSONByUpc(miraklData.getUpcId(), miraklData.getOpDiv());
        
        doNothing().when(sql).insertAppAuditData(PRODUCT_UPDATE, NO_DATA_FOUND, null, miraklData, PDF);
        doNothing().when(sql).insertAppAuditData(PRODUCT_UPDATE, NO_DATA_FOUND, null, miraklData, STELLA);
        doNothing().when(sql).insertAppAuditData(PRODUCT_UPDATE, NO_DATA_FOUND, null, miraklData, IMAGES);
        doNothing().when(sql).insertFacetAuditData(PRODUCT_UPDATE, NO_DATA_FOUND, null, miraklData);
        doNothing().when(sql).deleteAppDataInTemp(miraklData, PDF);
        doNothing().when(sql).deleteAppDataInTemp(miraklData, STELLA);
        doNothing().when(sql).deleteAppDataInTemp(miraklData, FACET);

        //when
        try {
        	productUpdateService.processProductUpdate("MCOM_product_delta_220520_191088.json", miraklDataList);
		} catch(Exception e) {
			assertTrue(e instanceof Exception,"Exception is expected");
		}

        //then
        verify(sql,times(1)).insertAppAuditData(PRODUCT_UPDATE, NO_DATA_FOUND, null, miraklData, PDF);
        verify(sql,times(1)).insertAppAuditData(PRODUCT_UPDATE, NO_DATA_FOUND, null, miraklData, STELLA);
        verify(sql,times(1)).insertAppAuditData(PRODUCT_UPDATE, NO_DATA_FOUND, null, miraklData, IMAGES);
        verify(sql,times(1)).insertFacetAuditData(PRODUCT_UPDATE, NO_DATA_FOUND, null, miraklData);
        verify(sql,times(1)).deleteAppDataInTemp(miraklData, PDF);
        verify(sql,times(1)).deleteAppDataInTemp(miraklData, STELLA);
        verify(sql,times(1)).deleteAppDataInTemp(miraklData, FACET);
        verify(cloudStorageService, never()).uploadToCloudStorage(null, new JSONObject(pdfDelta).toString().getBytes(), "deltaBucket");
        verify(cloudStorageService, never()).uploadToCloudStorage(null, new JSONObject(stellaDelta).toString().getBytes(), "deltaBucket");
        verify(cloudStorageService, never()).uploadToCloudStorage(null, new JSONObject(imageDelta).toString().getBytes(), "imageDeltaBucket");
    }
    
    @Test
    public void testIdentifyDeltaFromIncomingImages_Success() throws IllegalAccessException, MiraklRepositoryException {

        //given
        ImageMasterData imageInfo = new ImageMasterData(miraklData.getFileName(), miraklData.getUpcId(),
                miraklData.getOpDiv(), miraklData.getProductType(), "[{\"ImageID\":\"fc51acab50ed43efa922eca86b29717_old\",\"ImageType\":\"mainImage\",\"ImageURL\":\"https://media-dev-eu-2.mirakl.net/SOURCE/fc51acab50ed43efa922eca86b29717_old\"},{\"ImageID\":\"fc51acab50ed43efa922eca86b29718_old\",\"ImageType\":\"swatch\",\"ImageURL\":\"https://media-dev-eu-2.mirakl.net/SOURCE/fc51acab50ed43efa922eca86b29718_old\"}]");
        boolean isMainImgP0 = false;
        when(sql.findImageMasterJSONByUpc(miraklData.getUpcId(), miraklData.getOpDiv())).thenReturn(imageInfo);
        when(sql.getIsMainImgP0ByUpc(miraklData.getUpcId(), miraklData.getOpDiv())).thenReturn(isMainImgP0);
        doNothing().when(sql).batchInsertImageAuditData(PRODUCT_UPDATE, INPROCESS, null, imageData);

        //when
        productUpdateService.processProductUpdate("MCOM_product_update_220520_191088.json", miraklDataList);

        //then
		verify(sql, times(1)).findImageMasterJSONByUpc(miraklData.getUpcId(), miraklData.getOpDiv());
		verify(sql, times(1)).getIsMainImgP0ByUpc(miraklData.getUpcId(), miraklData.getOpDiv());
		verify(sql, times(1)).batchInsertImageAuditData(PRODUCT_UPDATE, INPROCESS, null, imageData);
    }

}
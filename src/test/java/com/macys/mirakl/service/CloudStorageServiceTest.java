package com.macys.mirakl.service;

import com.google.cloud.ReadChannel;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.macys.mirakl.model.FacetUpdateRespData;
import com.macys.mirakl.model.MiraklData;
import com.macys.mirakl.model.OfferIdRequestData;
import com.macys.mirakl.model.OfferIdResponseData;
import com.macys.mirakl.model.ProductUpdateRespData;
import com.macys.mirakl.processor.FileProcessor;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class CloudStorageServiceTest {

    @InjectMocks
    private CloudStorageService cloudStorageService;

    @Mock
    private Storage storage;

    @Mock
    private FileProcessor fileProcessor;

    @Mock
    private Blob blob;

    @Mock
    ReadChannel reader;

    @Test
    void testReadCreateOrUpdateFile() throws IOException {
        //Given
        String bucketName = "mtech-merch-epc-poc-317014-mirakl_transfer";
        String objectName = "MCOM/product_create/MCOM_product_create_220518_010101.json";
        String fileName = "MCOM_product_create_220518_010101.json";
        String jsonInput = "";
        List<MiraklData> expectedPrdTxns = new ArrayList<>();
        when(storage.get(BlobId.of(bucketName, objectName))).thenReturn(blob);
        when(blob.exists()).thenReturn(true);
        when(blob.reader()).thenReturn(reader);
        when(fileProcessor.inputStreamToString(any(InputStream.class))).thenReturn(jsonInput);
        when(fileProcessor.processUpdateJSON(fileName, jsonInput)).thenReturn(expectedPrdTxns);
        //When
        List<MiraklData> prdTxns = cloudStorageService.readCreateOrUpdateFile(bucketName, objectName, fileName);
        //Then
        assertThat(prdTxns).hasSameElementsAs(expectedPrdTxns);
    }


    @Test
    void testUploadToCloudStorage() {
        //Given
        String uploadedFile = "";
        JSONObject jsonObj = new JSONObject();
        byte[] arr = jsonObj.toString().getBytes();
        String bucket = "";
        when(storage.create(any(BlobInfo.class), any(byte[].class))).thenReturn(blob);
        //When
        cloudStorageService.uploadToCloudStorage(uploadedFile, arr, bucket);
        //Then
        verify(storage, times(1)).create(any(BlobInfo.class), any(byte[].class));
    }

    @Test
    void testReadOfferIdReqFile() throws IllegalArgumentException, IOException, JSONException {
        //Given
        String bucketName = "mtech-merch-epc-poc-317014-il-marketplace-product-offer-poc";
        String objectName = "BCOM/offer/BCOM_offer_220517_191088.json";
        when(storage.get(BlobId.of(bucketName, objectName))).thenReturn(blob);
        when(blob.exists()).thenReturn(true);
        when(blob.reader()).thenReturn(reader);
        String jsonInput = "";
        when(fileProcessor.inputStreamToString(any(InputStream.class))).thenReturn(jsonInput);
        List<OfferIdRequestData> offerIdReqList = new ArrayList<>();
        when(fileProcessor.processOfferIdReqJSON(jsonInput)).thenReturn(offerIdReqList);
        //When
        List<OfferIdRequestData> offerIdRequestData = cloudStorageService.readOfferIdReqFile(bucketName, objectName);
        //Then
        assertThat(offerIdRequestData).hasSameElementsAs(offerIdReqList);
    }

    @Test
    void testReadOfferIdRespFile() throws IllegalArgumentException, IOException, JSONException {
        //Given
        String bucketName = "mtech-merch-epc-poc-317014-il-mp-product-offer-response-poc";
        String objectName = "BCOM/offer_response/BCOM_offer_220517_191088_1.json";
        when(storage.get(BlobId.of(bucketName, objectName))).thenReturn(blob);
        when(blob.exists()).thenReturn(true);
        when(blob.reader()).thenReturn(reader);
        String jsonInput = "";
        when(fileProcessor.inputStreamToString(any(InputStream.class))).thenReturn(jsonInput);
        List<OfferIdResponseData> offerIdRespList = new ArrayList<>();
        when(fileProcessor.processOfferIdRespJSON(jsonInput)).thenReturn(offerIdRespList);
        //When
        List<OfferIdResponseData> offerIdResponseData = cloudStorageService.readOfferIdRespFile(bucketName, objectName);
        //Then
        assertThat(offerIdResponseData).hasSameElementsAs(offerIdRespList);
    }

    @Test
    void testReadPrdUpdateRespFile() throws IllegalArgumentException, IOException, JSONException {
        //Given
        String bucketName = "mtech-merch-epc-poc-317014-mirakl_response";
        String objectName = "MCOM/product_update_response/MCOM_product_update_pdf_220517_191001.json";
        when(storage.get(BlobId.of(bucketName, objectName))).thenReturn(blob);
        when(blob.exists()).thenReturn(true);
        when(blob.reader()).thenReturn(reader);
        String jsonInput = "";
        when(fileProcessor.inputStreamToString(any(InputStream.class))).thenReturn(jsonInput);
        List<ProductUpdateRespData> prdUpdateRespList = new ArrayList<ProductUpdateRespData>();
        when(fileProcessor.processPrdUpdateRespJSON(jsonInput)).thenReturn(prdUpdateRespList);
        //When
        List<ProductUpdateRespData> prdUpdateRespData = cloudStorageService.readPrdUpdateRespFile(bucketName, objectName);
        //Then
        assertThat(prdUpdateRespData).hasSameElementsAs(prdUpdateRespList);
    }

    @Test
    void testReadFacetUpdateRespFile() throws IllegalArgumentException, IOException, JSONException {
        //Given
        String bucketName = "mtech-merch-epc-poc-317014-mirakl_response";
        String objectName = "MCOM/facet/MCOM_product_update_facet_220517_191001_1.json";
        when(storage.get(BlobId.of(bucketName, objectName))).thenReturn(blob);
        when(blob.exists()).thenReturn(true);
        when(blob.reader()).thenReturn(reader);
        String jsonInput = "";
        when(fileProcessor.inputStreamToString(any(InputStream.class))).thenReturn(jsonInput);
        List<FacetUpdateRespData> facetUpdateRespList = new ArrayList<FacetUpdateRespData>();
        when(fileProcessor.processFacetUpdateRespJSON(jsonInput)).thenReturn(facetUpdateRespList);
        //When
        List<FacetUpdateRespData> facetUpdateRespData = cloudStorageService.readFacetUpdateRespFile(bucketName, objectName);
        //Then
        assertThat(facetUpdateRespData).hasSameElementsAs(facetUpdateRespList);
    }
}
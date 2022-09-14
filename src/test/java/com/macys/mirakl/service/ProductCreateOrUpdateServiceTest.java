package com.macys.mirakl.service;

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

import com.macys.mirakl.model.MiraklData;
import com.macys.mirakl.processor.FileProcessor;

@ExtendWith(MockitoExtension.class)
class ProductCreateOrUpdateServiceTest {

    @InjectMocks
    private ProductCreateOrUpdateService productCreateOrUpdateService;

    @Mock
    private FileProcessor fileProcessor;

    @Mock
    private CloudStorageService cloudStorageService;

    @Mock
    private ProductCreateService productCreateService;
    
    @Mock
    private ProductUpdateService prodUpdateService;
    
    @Mock
	NotificationService notificationService;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testProcessIntegrationFile_for_product_create() throws Exception {
        //Given
    	String subscription = "M.MER.EPC.MIRAKL.TRANSFER.SUB";
        String payload = "{\"kind\":\"storage#object\"," +
                "\"name\":\"MCOM/product_create/MCOM_product_create_220518_010101.json\"," +
                "\"bucket\":\"ILgcpProject-ILbucket\",\"contentType\":\"application/json\"}";
        String objectName = "MCOM/product_create/MCOM_product_create_220518_010101.json";
        String fileName = "MCOM_product_create_220518_010101.json";
        String bucketName = "ILgcpProject-ILbucket";
        List<MiraklData> prdTxns = new ArrayList<>();

        when(fileProcessor.getFileName(objectName)).thenReturn(fileName);
        when(fileProcessor.getFileOperation(objectName)).thenReturn("PRODUCT_CREATE");
        when(notificationService.processNotification(payload, subscription, true)).thenReturn(true);
        when(cloudStorageService.readCreateOrUpdateFile(bucketName,objectName,fileName)).thenReturn(prdTxns);
        //When
        productCreateOrUpdateService.processIntegrationFile(payload, subscription);
        //Then
        verify(fileProcessor,times(1)).getFileName(objectName);
        verify(fileProcessor,times(1)).getFileOperation(objectName);
        verify(notificationService,times(1)).processNotification(payload, subscription, true);
        verify(cloudStorageService,times(1)).readCreateOrUpdateFile(bucketName,objectName,fileName);
        verify(productCreateService,times(1)).processProductCreate(fileName, prdTxns);

    }

    @Test
    void testProcessIntegrationFile_for_wrong_file_path() {
        //Given
    	String subscription = "M.MER.EPC.MIRAKL.TRANSFER.SUB";
        String payload = "{\"kind\":\"storage#object\"," +
                "\"name\":\"MCOM_product_create_220518_010101.json\"," +
                "\"bucket\":\"ILgcpProject-ILbucket\",\"contentType\":\"application/json\"}";
        String objectName = "MCOM_product_create_220518_010101.json";
        String fileName = "MCOM_product_create_220518_010101.json";
        String bucketName = "ILgcpProject-ILbucket";

        when(fileProcessor.getFileName(objectName)).thenReturn(fileName);
        when(fileProcessor.getFileOperation(objectName)).thenReturn("WRONG_FILE");
        //When
        productCreateOrUpdateService.processIntegrationFile(payload, subscription);
        //Then
        verify(cloudStorageService,times(0)).readCreateOrUpdateFile(bucketName,objectName,fileName);
    }

    @Test
    void testProcessIntegrationFile_for_product_update() throws Exception {
        //Given
    	String subscription = "M.MER.EPC.MIRAKL.TRANSFER.SUB";
        String payload = "{\"kind\":\"storage#object\"," +
                "\"name\":\"MCOM/product_create/MCOM_product_update_220518_010101.json\"," +
                "\"bucket\":\"ILgcpProject-ILbucket\",\"contentType\":\"application/json\"}";
        String objectName = "MCOM/product_create/MCOM_product_update_220518_010101.json";
        String fileName = "MCOM_product_update_220518_010101.json";
        String bucketName = "ILgcpProject-ILbucket";
        List<MiraklData> prdTxns = new ArrayList<>();

        when(fileProcessor.getFileName(objectName)).thenReturn(fileName);
        when(fileProcessor.getFileOperation(objectName)).thenReturn("PRODUCT_UPDATE");
        when(notificationService.processNotification(payload, subscription, true)).thenReturn(true);
        when(cloudStorageService.readCreateOrUpdateFile(bucketName,objectName,fileName)).thenReturn(prdTxns);

        //When
        productCreateOrUpdateService.processIntegrationFile(payload, subscription);

        //Then
        verify(fileProcessor,times(1)).getFileName(objectName);
        verify(fileProcessor,times(1)).getFileOperation(objectName);
        verify(notificationService,times(1)).processNotification(payload, subscription, true);
        verify(cloudStorageService,times(1)).readCreateOrUpdateFile(bucketName,objectName,fileName);
        verify(prodUpdateService,times(1)).processProductUpdate(fileName, prdTxns);

    }
    
    @Test
    void testProcessIntegrationFile_for_MultipleNotification() throws Exception {
        //Given
    	String subscription = "M.MER.EPC.MIRAKL.TRANSFER.SUB";
        String payload = "{\"kind\":\"storage#object\"," +
                "\"name\":\"MCOM/product_create/MCOM_product_update_220518_010101.json\"," +
                "\"bucket\":\"ILgcpProject-ILbucket\",\"contentType\":\"application/json\"}";
        String objectName = "MCOM/product_create/MCOM_product_update_220518_010101.json";
        String fileName = "MCOM_product_update_220518_010101.json";

        when(fileProcessor.getFileName(objectName)).thenReturn(fileName);
        when(fileProcessor.getFileOperation(objectName)).thenReturn("PRODUCT_UPDATE");
        when(notificationService.processNotification(payload, subscription, true)).thenReturn(false);

        //When
        productCreateOrUpdateService.processIntegrationFile(payload, subscription);

        //Then
        verify(fileProcessor,times(1)).getFileName(objectName);
        verify(fileProcessor,times(1)).getFileOperation(objectName);
        verify(notificationService,times(1)).processNotification(payload, subscription, true);

    }
}
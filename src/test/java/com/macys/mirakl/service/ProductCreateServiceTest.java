package com.macys.mirakl.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.macys.mirakl.exception.MiraklRepositoryException;
import com.macys.mirakl.model.FacetMasterData;
import com.macys.mirakl.model.ImageMasterData;
import com.macys.mirakl.model.MiraklData;
import com.macys.mirakl.model.PdfMasterData;
import com.macys.mirakl.model.StellaMasterData;
import com.macys.mirakl.util.FilePrefix;

@ExtendWith(MockitoExtension.class)
class ProductCreateServiceTest {
	
	List<MiraklData> miraklDataList = new ArrayList<>();
    List<PdfMasterData> pdfData = new ArrayList<>();
    List<StellaMasterData> stellaData = new ArrayList<>();
    List<ImageMasterData> imageData = new ArrayList<>();
    List<FacetMasterData> facetData = new ArrayList<>();
    MiraklData miraklData;
	private List<String> upcs;
	private List<String> existingUpcs1,existingUpcs2;

    @InjectMocks
    private ProductCreateService productCreateService;

    @Mock
    private SQLService sql;

    @BeforeEach
    void setUp() {
    	
        //given
        miraklData = MiraklData.builder()
                .fileName("MCOM_product_create_220520_191088.json")
                .upcId("191019881001")
                .opDiv("12")
                .productType("Watch")
                .taxCode("dummyValue")
                .nrfSizeCode("12")
                .msrp("12")
                .dept("902")
                .vendor("v1")
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
        
        upcs = Arrays.asList("191019881001");
        existingUpcs2 = Arrays.asList("191019881001");
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testProcessProductCreate_Case1() throws MiraklRepositoryException {

        //Given
    	when(sql.findExistingMasterUpcs(upcs, FilePrefix.MCOM)).thenReturn(existingUpcs1);
        doNothing().when(sql).batchInsertPdfMasterData(any(List.class));
        doNothing().when(sql).batchInsertPdfAuditData(any(String.class),any(String.class),isNull(),any(List.class));
        doNothing().when(sql).batchInsertStellaMasterData(any(List.class));
        doNothing().when(sql).batchInsertStellaAuditData(any(String.class),any(String.class),isNull(),any(List.class));
        doNothing().when(sql).batchInsertImageMasterData( any(List.class));
        doNothing().when(sql).batchInsertImageAuditData(any(String.class),any(String.class),isNull(),any(List.class));
        doNothing().when(sql).batchInsertFacetMasterData( any(List.class));
        doNothing().when(sql).batchInsertFacetAuditData(any(String.class),any(String.class),isNull(),any(List.class));
        doNothing().when(sql).batchInsertUpcMasterData( any(List.class));
        
        //When
         productCreateService.processProductCreate(miraklData.getFileName(), miraklDataList);

        //Then
        verify(sql,times(1)).findExistingMasterUpcs(upcs, FilePrefix.MCOM);
        verify(sql,times(1)).batchInsertPdfMasterData( any(List.class));
        verify(sql,times(1)).batchInsertPdfAuditData(any(String.class),any(String.class),isNull(),any(List.class));
        verify(sql,times(1)).batchInsertStellaMasterData(any(List.class));
        verify(sql,times(1)).batchInsertStellaAuditData(any(String.class),any(String.class),isNull(),any(List.class));
        verify(sql,times(1)).batchInsertImageMasterData( any(List.class));
        verify(sql,times(1)).batchInsertImageAuditData(any(String.class),any(String.class),isNull(),any(List.class));
        verify(sql,times(1)).batchInsertFacetMasterData( any(List.class));
        verify(sql,times(1)).batchInsertFacetAuditData(any(String.class),any(String.class),isNull(),any(List.class));
        verify(sql,times(1)).batchInsertUpcMasterData( any(List.class));

    }
    
    @Test
    void testProcessProductCreate_Case2() throws MiraklRepositoryException {

        //Given
    	when(sql.findExistingMasterUpcs(upcs, FilePrefix.MCOM)).thenReturn(existingUpcs2);
        
        //When
         productCreateService.processProductCreate(miraklData.getFileName(), miraklDataList);

        //Then
        verify(sql,times(1)).findExistingMasterUpcs(upcs, FilePrefix.MCOM);
        verify(sql,never()).batchInsertPdfMasterData( any(List.class));
        verify(sql,never()).batchInsertPdfAuditData(any(String.class),any(String.class),isNull(),any(List.class));
        verify(sql,never()).batchInsertStellaMasterData(any(List.class));
        verify(sql,never()).batchInsertStellaAuditData(any(String.class),any(String.class),isNull(),any(List.class));
        verify(sql,never()).batchInsertImageMasterData( any(List.class));
        verify(sql,never()).batchInsertImageAuditData(any(String.class),any(String.class),isNull(),any(List.class));
        verify(sql,never()).batchInsertFacetMasterData( any(List.class));
        verify(sql,never()).batchInsertFacetAuditData(any(String.class),any(String.class),isNull(),any(List.class));
        verify(sql,never()).batchInsertUpcMasterData( any(List.class));

    }
}
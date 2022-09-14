package com.macys.mirakl.dao;

import java.util.List;

import com.macys.mirakl.model.FacetMasterData;
import com.macys.mirakl.model.FacetUpdateRespData;
import com.macys.mirakl.model.ImageMasterData;
import com.macys.mirakl.model.ImageUpdateRespData;
import com.macys.mirakl.model.MiraklData;
import com.macys.mirakl.model.PdfMasterData;
import com.macys.mirakl.model.PdfRespData;
import com.macys.mirakl.model.StellaMasterData;
import com.macys.mirakl.model.StellaRespData;

public interface ProductAuditDAO {
    void batchInsertPdfAuditData(String operation, String status, String errorDesc, List<PdfMasterData> pdfDataList);

    void batchInsertStellaAuditData(String operation, String status, String errorDesc, List<StellaMasterData> stellaDataList);

    void batchInsertImageAuditData(String operation, String status, String errorDesc, List<ImageMasterData> imageDataList);

    void batchInsertFacetAuditData(String operation, String status, String errorDesc, List<FacetMasterData> facetDataList);
    
    void batchInsertPdfRespAuditData(String operation, List<PdfRespData> pdfDataList);

    void batchInsertStellaRespAuditData(String operation, List<StellaRespData> stellaDataList);
    
    void batchInsertFacetRespAuditData(String operation, List<FacetUpdateRespData> facetDataList);
    
    void insertAppAuditData(String operation, String status, String errorDesc, MiraklData product, String app);

    void insertFacetAuditData(String operation, String status, String errorDesc, MiraklData product);
    
    void insertImagesAuditData(String operation, ImageUpdateRespData imageUpdateRespData, String productType, String inputFileName, String jsonData);

}
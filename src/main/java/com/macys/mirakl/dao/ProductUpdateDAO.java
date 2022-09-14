package com.macys.mirakl.dao;

import java.util.List;

import com.macys.mirakl.exception.MiraklRepositoryException;
import com.macys.mirakl.model.FacetMasterData;
import com.macys.mirakl.model.FacetUpdateRespData;
import com.macys.mirakl.model.ImageMasterData;
import com.macys.mirakl.model.ImageUpdateRespData;
import com.macys.mirakl.model.MiraklData;
import com.macys.mirakl.model.PdfMasterData;
import com.macys.mirakl.model.PdfRespData;
import com.macys.mirakl.model.StellaMasterData;
import com.macys.mirakl.model.StellaRespData;

public interface ProductUpdateDAO {
	
	void batchInsertPdfTempData(List<PdfMasterData> prdTxns, String operation);

	void batchInsertStellaTempData(List<StellaMasterData> prdTxns, String operation);

	void batchInsertImagesTempData(List<ImageMasterData> prdTxns, String operation);
	
	void batchInsertFacetTempData(List<FacetMasterData> prdTxns, String operation);

	PdfMasterData findPdfMasterDataByUpc(String upcId, String opDiv);

	StellaMasterData findStellaMasterDataByUpc(String upcId, String opDiv);
	
	ImageMasterData findImageMasterDataByUpc(String upcId, String opDiv);

	FacetMasterData findFacetMasterDataByUpc(String upcId, String opDiv);

	boolean getIsMainImgP0ByUpc(String upcId, String opDiv);
	
	// Product Update response changes
	PdfMasterData findPdfJsonFromTemp(String upcId, String opDiv, String fileNameInJson);

	StellaMasterData findStellaJsonFromTemp(String upcId, String opDiv, String fileNameInJson);

	void batchDeletePdfDataInTemp(List<PdfRespData> pdfUpdateDataList);
	
	void batchDeletePdfDataInTempNoDelta(List<PdfMasterData> pdfMasterDataList);
	
	void batchDeleteStellaDataInTempNoDelta(List<StellaMasterData> stellaMasterDataList);
	
	void batchDeleteFacetDataInTempNoDelta(List<FacetMasterData> facetMasterDataList);

	void batchDeleteStellaDataInTemp(List<StellaRespData> stellaUpdateDataList);
	
	String findFacetJsonFromTemp(String upcId, String opDiv, String fileNameInJson);
	
	void updateFacetJsonInMaster(String upcId, String opDiv, String fileName, String stellaJson);
	
	void batchDeleteFacetDataInTemp(List<FacetUpdateRespData> facetUpdateRespAuditList);
	
	void batchUpdateFacetJsonInMaster(List<FacetUpdateRespData> facetSuccessRespList);

	void batchUpdatePdfJsonInMaster(List<PdfRespData> pdfSuccessDataList);

	void batchUpdateStellaJsonInMaster(List<StellaRespData> stellaSuccessDataList);
	
	void batchUpdateImagesMasterDataList(List<ImageMasterData> imageDatalist);
		
	void deleteAppDataInTemp(MiraklData product, String app);
	
	void updateImagesRespMessage(ImageUpdateRespData imageUpdateRespData);
	
	ImageUpdateRespData fetchImagesRespMessage(ImageUpdateRespData imageUpdateRespData);
	
	void updateImagesMasterJson(ImageMasterData imageMasterData);

	void batchInsertImageResData(List<ImageUpdateRespData> imageUpdateRespDataList);
	
	public List<ImageUpdateRespData> findImageUpdateRespData();
	
	public void updateImageUpdateRespData(List<ImageUpdateRespData> imageUpdateRespData, String resFilename) throws MiraklRepositoryException;
	
	void batchUpdateImageRespData(List<ImageUpdateRespData> imageUpdateRespDataList);

}

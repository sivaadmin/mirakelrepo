package com.macys.mirakl.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
import com.macys.mirakl.util.FilePrefix;

@Service
public class SQLService {

	@Autowired
	private ProductUpdateDAO productUpdateDAO;
	
	@Autowired
	private OfferIdDAO offerIdDAO;
	
	@Autowired
	private CreateResponseDAO createResponseDAO;

	@Autowired
	private ProductCreateDAO productCreateDAO;

	@Autowired
	private ProductAuditDAO productAuditDAO;
	
	@Autowired
	private NotificationDAO notificationDAO;

	public List<String> findExistingMasterUpcs(List<String> upcs, FilePrefix filePrefix) throws MiraklRepositoryException {
		return productCreateDAO.findExistingMasterUpcs(upcs, filePrefix);
	}

	public void batchInsertPdfTempData(List<PdfMasterData> pfdData, String operation) {
		productUpdateDAO.batchInsertPdfTempData(pfdData, operation);
	}

	public void batchInsertStellaTempData(List<StellaMasterData> stellaData, String operation) {
		productUpdateDAO.batchInsertStellaTempData(stellaData, operation);
	}

	public void batchInsertImageTempData(List<ImageMasterData> imageData, String operation) {
		productUpdateDAO.batchInsertImagesTempData(imageData, operation);
	}

	public void batchInsertFacetTempData(List<FacetMasterData> facetData, String operation) {
		productUpdateDAO.batchInsertFacetTempData(facetData, operation);
	}

	public PdfMasterData findPdfMasterJSONByUpc(String upcId, String opDiv) {
		PdfMasterData pdfMasterData = productUpdateDAO.findPdfMasterDataByUpc(upcId, opDiv);
		return pdfMasterData;
	}

	public StellaMasterData findStellaMasterJSONByUpc(String upcId, String opDiv) {
		StellaMasterData stellaMasterData = productUpdateDAO.findStellaMasterDataByUpc(upcId, opDiv);
		return stellaMasterData;
	}
	
	public ImageMasterData findImageMasterJSONByUpc(String upcId, String opDiv) {
		ImageMasterData imageMasterData = productUpdateDAO.findImageMasterDataByUpc(upcId, opDiv);
		return imageMasterData;
	}

	public FacetMasterData findFacetMasterJSONByUpc(String upcId, String opDiv) {
		FacetMasterData facetMasterData = productUpdateDAO.findFacetMasterDataByUpc(upcId, opDiv);
		return facetMasterData;
	}

	public boolean getIsMainImgP0ByUpc(String upcId, String opDiv) {
		boolean isMainImgP0 = productUpdateDAO.getIsMainImgP0ByUpc(upcId, opDiv);
		return isMainImgP0;
	}
	
	public void insertOfferIdAuditList(List<OfferIdRequestData> offerIdReqList, String fileName) throws MiraklRepositoryException {
		offerIdDAO.insertOfferIdAuditList(offerIdReqList,fileName);
	}
	
	public void updateOfferIdAuditList(List<OfferIdResponseData> offerIdRespList) throws MiraklRepositoryException {
		offerIdDAO.updateOfferIdAuditList(offerIdRespList);
	}
	
	public void updateOfferIdMasterList(List<OfferIdResponseData> offerIdRespList) throws MiraklRepositoryException {
		offerIdDAO.updateOfferIdMasterList(offerIdRespList);
	}
	
	// Start - Create Response changes
	public void insertCreateRespAuditList(List<CreateResponseData> createResponseDatas) throws MiraklRepositoryException {
		createResponseDAO.insertCreateRespAuditList(createResponseDatas);
	}
	
	public void updateCreateRespList(List<PublishUpcData> upcDataList) throws MiraklRepositoryException {
		createResponseDAO.updateCreateRespList(upcDataList);
	}
	
	public UpcMasterData findUpcMasterDataByUpc(String upcId, String opDiv) throws MiraklRepositoryException {
		UpcMasterData upcMasterData = createResponseDAO.findUpcMasterDataByUpc(upcId, opDiv);
		return upcMasterData;
	}
	// End - Create Response changes

	public PdfMasterData findPdfJsonFromTemp(String upcId, String opDiv, String fileNameInJson) throws MiraklRepositoryException {
		PdfMasterData pdfMasterData = productUpdateDAO.findPdfJsonFromTemp(upcId, opDiv, fileNameInJson);
		return pdfMasterData;
	}

	public StellaMasterData findStellaJsonFromTemp(String upcId, String opDiv, String fileNameInJson) throws MiraklRepositoryException {
		StellaMasterData stellaMasterData = productUpdateDAO.findStellaJsonFromTemp(upcId, opDiv, fileNameInJson);
		return stellaMasterData;
	}
	
	public void batchDeletePdfDataInTemp(List<PdfRespData> pdfUpdateDataList) throws MiraklRepositoryException {
		productUpdateDAO.batchDeletePdfDataInTemp(pdfUpdateDataList);
	}
	
	public void batchDeletePdfDataInTempNoDelta(List<PdfMasterData> pdfMasterDataList) throws MiraklRepositoryException {
		productUpdateDAO.batchDeletePdfDataInTempNoDelta(pdfMasterDataList);
	}
	
	public void batchDeleteStellaDataInTempNoDelta(List<StellaMasterData> stellaMasterDataList) throws MiraklRepositoryException {
		productUpdateDAO.batchDeleteStellaDataInTempNoDelta(stellaMasterDataList);
	}
	
	public void batchDeleteFacetDataInTempNoDelta(List<FacetMasterData> facetMasterDataList) throws MiraklRepositoryException {
		productUpdateDAO.batchDeleteFacetDataInTempNoDelta(facetMasterDataList);
	}

	public void batchDeleteStellaDataInTemp(List<StellaRespData> stellaUpdateDataList) throws MiraklRepositoryException {
		productUpdateDAO.batchDeleteStellaDataInTemp(stellaUpdateDataList);
	}

	public void batchInsertPdfMasterData(List<PdfMasterData> pfdData) {
		productCreateDAO.batchInsertPdfMasterData(pfdData);
	}
	
	public void batchInsertStellaMasterData(List<StellaMasterData> stellaData) {
		productCreateDAO.batchInsertStellaMasterData(stellaData);
	}
	public void batchInsertImageMasterData(List<ImageMasterData> imageData) {
		productCreateDAO.batchInsertImageMasterData(imageData);
	}
	public void batchInsertFacetMasterData(List<FacetMasterData> facetData) {
		productCreateDAO.batchInsertFacetMasterData(facetData);
	}
	public void batchInsertUpcMasterData(List<UpcMasterData> upcData) {
		productCreateDAO.batchInsertUpcMasterData(upcData);

	}

	void batchInsertPdfAuditData(String operation, String status, String errorDesc,List<PdfMasterData> pdfDataList) {
		productAuditDAO.batchInsertPdfAuditData(operation,status,errorDesc,pdfDataList);
	}
	void batchInsertStellaAuditData(String operation, String status, String errorDesc,List<StellaMasterData> stellaDataList) {
		productAuditDAO.batchInsertStellaAuditData(operation,status,errorDesc,stellaDataList);
	}
	void batchInsertImageAuditData(String operation, String status, String errorDesc,List<ImageMasterData> imageDataList) {
		productAuditDAO.batchInsertImageAuditData(operation,status,errorDesc,imageDataList);
	}
	void batchInsertFacetAuditData(String operation, String status, String errorDesc,List<FacetMasterData> facetDataList) {
		productAuditDAO.batchInsertFacetAuditData(operation, status,  errorDesc, facetDataList);
	}
	
	void batchInsertPdfRespAuditData(String operation, List<PdfRespData> pdfDataList) {
		productAuditDAO.batchInsertPdfRespAuditData(operation, pdfDataList);
	}
	
	void batchInsertStellaRespAuditData(String operation, List<StellaRespData> stellaDataList) {
		productAuditDAO.batchInsertStellaRespAuditData(operation, stellaDataList);
	}
	
	public String findFacetJsonFromTemp(String upcId, String opDiv, String fileNameInJson) throws MiraklRepositoryException {
		return productUpdateDAO.findFacetJsonFromTemp(upcId, opDiv, fileNameInJson);
	}
	
	public void updateFacetJsonInMaster(String upcId, String opDiv, String fileName, String facetJson) throws MiraklRepositoryException {
		productUpdateDAO.updateFacetJsonInMaster(upcId, opDiv, fileName, facetJson);
	}
	
	public void batchDeleteFacetDataInTemp(List<FacetUpdateRespData> facetUpdateRespAuditList) throws MiraklRepositoryException {
		productUpdateDAO.batchDeleteFacetDataInTemp(facetUpdateRespAuditList);
	}
	
	public void batchInsertFacetRespAuditData(String operation, List<FacetUpdateRespData> facetDataList) {
		productAuditDAO.batchInsertFacetRespAuditData(operation, facetDataList);
	}
	
	public void batchUpdateFacetJsonInMaster(List<FacetUpdateRespData> facetSuccesDataList) {
		productUpdateDAO.batchUpdateFacetJsonInMaster(facetSuccesDataList);
	}
	
	public void batchUpdatePdfJsonInMaster(List<PdfRespData> pdfSuccessDataList) {
		productUpdateDAO.batchUpdatePdfJsonInMaster(pdfSuccessDataList);
	}
	
	public void batchUpdateStellaJsonInMaster(List<StellaRespData> stellaSuccessDataList) {
		productUpdateDAO.batchUpdateStellaJsonInMaster(stellaSuccessDataList);
	}
	
	public void batchUpdateImagesMasterDataList(List<ImageMasterData> imageDatalist) {
		productUpdateDAO.batchUpdateImagesMasterDataList(imageDatalist);
	}
	
	public void deleteAppDataInTemp(MiraklData product, String app) {
		productUpdateDAO.deleteAppDataInTemp(product, app);
	}
	
	public void insertAppAuditData(String operation, String status, String errorDesc, MiraklData product, String app) {
		productAuditDAO.insertAppAuditData(operation, status, errorDesc, product, app);
	}
	
	public void insertFacetAuditData(String operation, String status, String errorDesc, MiraklData product) {
		productAuditDAO.insertFacetAuditData(operation, status, errorDesc, product);
	}
	
	public void updateImagesRespMessage(ImageUpdateRespData imageUpdateRespData) throws MiraklRepositoryException {
		productUpdateDAO.updateImagesRespMessage(imageUpdateRespData);
	}
	
	public ImageUpdateRespData fetchImagesRespMessage(ImageUpdateRespData imageUpdateRespData) throws MiraklRepositoryException {
		ImageUpdateRespData imageUpdateRespDataFromDB = productUpdateDAO.fetchImagesRespMessage(imageUpdateRespData);
		return imageUpdateRespDataFromDB;
	}
	
	public void updateImagesMasterJson(ImageMasterData imageMasterData) throws MiraklRepositoryException {
		productUpdateDAO.updateImagesMasterJson(imageMasterData);
	}
	
	public void insertImagesAuditData(String operation, ImageUpdateRespData imageUpdateRespData, String productType, String inputFileName, String jsonData) {
		productAuditDAO.insertImagesAuditData(operation, imageUpdateRespData, productType, inputFileName, jsonData);
	}
	
	public void batchInsertImageResData(List<ImageUpdateRespData> imageUpdateRespDataList) {
		productUpdateDAO.batchInsertImageResData(imageUpdateRespDataList);
	}
	
	public int findNotificationDetails(String subName, String fileName, String bucketName) {
		return notificationDAO.findNotificationDetails(subName, fileName, bucketName);
	}
	
	public void insertNotificationDetails(String subName, String fileName, String bucketName) {
		notificationDAO.insertNotificationDetails(subName, fileName, bucketName);
	}

	public void deleteOldNotifications(int beforeMinutes) {
		notificationDAO.deleteOldNotifications(beforeMinutes);
	}
	
	public void batchUpdateImageRespData(List<ImageUpdateRespData> imageUpdateRespDataList) {
		productUpdateDAO.batchUpdateImageRespData(imageUpdateRespDataList);
	}
	
}

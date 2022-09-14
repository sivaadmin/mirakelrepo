package com.macys.mirakl.dao;

import static com.macys.mirakl.util.OrchConstants.ERROR_MESSAGE;
import static com.macys.mirakl.util.OrchConstants.IMAGES;
import static com.macys.mirakl.util.OrchConstants.MP_FACET_AUDIT;
import static com.macys.mirakl.util.OrchConstants.MP_IMAGES_AUDIT;
import static com.macys.mirakl.util.OrchConstants.MP_PDF_AUDIT;
import static com.macys.mirakl.util.OrchConstants.MP_STELLA_AUDIT;
import static com.macys.mirakl.util.OrchConstants.PDF;
import static com.macys.mirakl.util.OrchConstants.STATUS_SUCCESS_CODE;
import static com.macys.mirakl.util.OrchConstants.STELLA;
import static com.macys.mirakl.util.OrchConstants.SUCCESS_MESSAGE;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.macys.mirakl.model.FacetMasterData;
import com.macys.mirakl.model.FacetUpdateRespData;
import com.macys.mirakl.model.ImageMasterData;
import com.macys.mirakl.model.ImageUpdateRespData;
import com.macys.mirakl.model.MiraklData;
import com.macys.mirakl.model.PdfMasterData;
import com.macys.mirakl.model.PdfRespData;
import com.macys.mirakl.model.StellaMasterData;
import com.macys.mirakl.model.StellaRespData;

@Repository
public class ProductAuditDAOImpl implements ProductAuditDAO {
	@Autowired
	JdbcTemplate jdbcTemplate;

	@Override
	public void batchInsertPdfAuditData(String operation, String status, String errorDesc,
			List<PdfMasterData> pdfDataList) {
		StringBuilder queryBuilder = new StringBuilder();
		queryBuilder.append("INSERT INTO ").append(MP_PDF_AUDIT)
				.append("(CREATE_TS, OPERATION, STATUS, ERROR_DESC, FILE_NAME, UPC_ID, OP_DIV, PRODUCT_TYPE,PDF_DATA)")
				.append("values(?,?,?,?,?,?,?,?,?)");
		List<Object[]> batchArgsList = new ArrayList<Object[]>();
		for (PdfMasterData pdfMasterData : pdfDataList) {
			Object[] objectArray = { getCreateTs(), operation, status, errorDesc, pdfMasterData.getFileName(),
					pdfMasterData.getUpcId(), pdfMasterData.getOpDiv(), pdfMasterData.getProductType(),
					pdfMasterData.getPdfData() };
			batchArgsList.add(objectArray);
		}
		jdbcTemplate.batchUpdate(queryBuilder.toString(), batchArgsList);

	}

	@Override
	public void batchInsertStellaAuditData(String operation, String status, String errorDesc,
			List<StellaMasterData> stellaDataList) {
		StringBuilder queryBuilder = new StringBuilder();
		queryBuilder.append("INSERT INTO ").append(MP_STELLA_AUDIT).append(
				"(CREATE_TS, OPERATION, STATUS, ERROR_DESC, FILE_NAME,UPC_ID, OP_DIV, PRODUCT_TYPE, STELLA_DATA)")
				.append("values(?,?,?,?,?,?, ?, ?, ?)");
		List<Object[]> batchArgsList = new ArrayList<Object[]>();
		for (StellaMasterData stellaMasterData : stellaDataList) {
			Object[] objectArray = { getCreateTs(), operation, status, errorDesc, stellaMasterData.getFileName(),
					stellaMasterData.getUpcId(), stellaMasterData.getOpDiv(), stellaMasterData.getProductType(),
					stellaMasterData.getStellaData() };
			batchArgsList.add(objectArray);
		}
		jdbcTemplate.batchUpdate(queryBuilder.toString(), batchArgsList);

	}

	@Override
	public void batchInsertImageAuditData(String operation, String status, String errorDesc,
			List<ImageMasterData> imageDataList) {
		StringBuilder queryBuilder = new StringBuilder();
		queryBuilder.append("INSERT INTO ").append(MP_IMAGES_AUDIT).append(
				"(CREATE_TS, OPERATION, STATUS, ERROR_DESC,  FILE_NAME, UPC_ID, OP_DIV, PRODUCT_TYPE, IMAGE_DATA)")
				.append("values(?,?,?,?,?,?,?, ?,?)");
		List<Object[]> batchArgsList = new ArrayList<Object[]>();
		for (ImageMasterData imageMasterData : imageDataList) {
			Object[] objectArray = { getCreateTs(), operation, status, errorDesc, imageMasterData.getFileName(),
					imageMasterData.getUpcId(), imageMasterData.getOpDiv(), imageMasterData.getProductType(),
					imageMasterData.getImageData() };
			batchArgsList.add(objectArray);
		}
		jdbcTemplate.batchUpdate(queryBuilder.toString(), batchArgsList);

	}

	@Override
	public void batchInsertFacetAuditData(String operation, String status, String errorDesc,
			List<FacetMasterData> facetDataList) {
		StringBuilder queryBuilder = new StringBuilder();
		queryBuilder.append("INSERT INTO ").append(MP_FACET_AUDIT).append(
				"(CREATE_TS, OPERATION, STATUS, ERROR_DESC, FILE_NAME, UPC_ID, OP_DIV, PRODUCT_TYPE, DEPT, VENDOR, PID, NRF_COLOR_CODE, FACET_DATA)")
				.append("values(?,?,?,?,?,?,?,?,?,?,?,?,?)");
		List<Object[]> batchArgsList = new ArrayList<Object[]>();
		for (FacetMasterData facetMasterData : facetDataList) {
			Object[] objectArray = { getCreateTs(), operation, status, errorDesc, facetMasterData.getFileName(),
					facetMasterData.getUpcId(), facetMasterData.getOpDiv(), facetMasterData.getProductType(),
					facetMasterData.getDept(), facetMasterData.getVendor(), facetMasterData.getPid(),
					facetMasterData.getNrfcolor(), facetMasterData.getFacetData() };
			batchArgsList.add(objectArray);
		}
		jdbcTemplate.batchUpdate(queryBuilder.toString(), batchArgsList);

	}

	@Override
	public void batchInsertPdfRespAuditData(String operation, List<PdfRespData> pdfDataList) {
		StringBuilder queryBuilder = new StringBuilder();
		queryBuilder.append("INSERT INTO ").append(MP_PDF_AUDIT)
				.append("(CREATE_TS, OPERATION, STATUS, ERROR_DESC, FILE_NAME, UPC_ID, OP_DIV, PRODUCT_TYPE, PDF_DATA)")
				.append("values(?,?,?,?,?,?,?,?,?)");
		List<Object[]> batchArgsList = new ArrayList<Object[]>();
		for (PdfRespData pdfRespData : pdfDataList) {
			Object[] objectArray = { getCreateTs(), operation, pdfRespData.getStatus(), pdfRespData.getErrorDesc(),
					pdfRespData.getFileName(), pdfRespData.getUpcId(), pdfRespData.getOpDiv(),
					pdfRespData.getProductType(), pdfRespData.getPdfData() };
			batchArgsList.add(objectArray);
		}
		jdbcTemplate.batchUpdate(queryBuilder.toString(), batchArgsList);

	}

	@Override
	public void batchInsertStellaRespAuditData(String operation, List<StellaRespData> stellaDataList) {
		StringBuilder queryBuilder = new StringBuilder();
		queryBuilder.append("INSERT INTO ").append(MP_STELLA_AUDIT).append(
				"(CREATE_TS, OPERATION, STATUS, ERROR_DESC, FILE_NAME, UPC_ID, OP_DIV, PRODUCT_TYPE, STELLA_DATA)")
				.append("values(?,?,?,?,?,?, ?, ?, ?)");
		List<Object[]> batchArgsList = new ArrayList<Object[]>();
		for (StellaRespData stellaRespData : stellaDataList) {
			Object[] objectArray = { getCreateTs(), operation, stellaRespData.getStatus(),
					stellaRespData.getErrorDesc(), stellaRespData.getFileName(), stellaRespData.getUpcId(),
					stellaRespData.getOpDiv(), stellaRespData.getProductType(), stellaRespData.getStellaData() };
			batchArgsList.add(objectArray);
		}
		jdbcTemplate.batchUpdate(queryBuilder.toString(), batchArgsList);

	}

	@Override
	public void batchInsertFacetRespAuditData(String operation, List<FacetUpdateRespData> facetRespDataList) {
		StringBuilder queryBuilder = new StringBuilder();
		queryBuilder.append("INSERT INTO ").append(MP_FACET_AUDIT).append(
				"(CREATE_TS, OPERATION, STATUS, ERROR_DESC, FILE_NAME, UPC_ID, OP_DIV, PRODUCT_TYPE, DEPT, VENDOR, PID, NRF_COLOR_CODE, FACET_DATA)")
				.append("values(?,?,?,?,?,?,?,?,?,?,?,?,?)");
		List<Object[]> batchArgsList = new ArrayList<Object[]>();
		for (FacetUpdateRespData facetRespData : facetRespDataList) {
			Object[] objectArray = { getCreateTs(), operation, facetRespData.getStatus(), facetRespData.getMessage(),
					facetRespData.getFileNameJson(), facetRespData.getUpcId(), facetRespData.getOpDiv(),
					facetRespData.getProductType(), facetRespData.getDept(), facetRespData.getVendor(),
					facetRespData.getPid(), facetRespData.getNrfColorCode(), facetRespData.getFacetData() };
			batchArgsList.add(objectArray);
		}
		jdbcTemplate.batchUpdate(queryBuilder.toString(), batchArgsList);
	}
	
	private Timestamp getCreateTs() {
		return new Timestamp(System.currentTimeMillis());
	}

	@Override
	public void insertAppAuditData(String operation, String status, String errorDesc, MiraklData product, String app) {
		
		StringBuilder queryBuilder = new StringBuilder();
		String auditTable = "";
		String dataColumn = "";
		String jsonData = "";
		if(STELLA.equalsIgnoreCase(app)) {
			auditTable = MP_STELLA_AUDIT;
			dataColumn = "STELLA_DATA";
			jsonData = product.getStellaData();
		} else if(PDF.equalsIgnoreCase(app)) {
			auditTable = MP_PDF_AUDIT;
			dataColumn = "PDF_DATA";
			jsonData = product.getPdfData();
		} else if(IMAGES.equalsIgnoreCase(app)) {
			auditTable = MP_IMAGES_AUDIT;
			dataColumn = "IMAGE_DATA";
			jsonData = product.getImagesData();
		}
		queryBuilder.append("INSERT INTO ").append(auditTable)
				.append(" (CREATE_TS, OPERATION, STATUS, ERROR_DESC, FILE_NAME, UPC_ID, OP_DIV, PRODUCT_TYPE, ")
				.append(dataColumn).append(")")
				.append("values(?,?,?,?,?,?, ?, ?, ?)");
		jdbcTemplate.update(queryBuilder.toString(), getCreateTs(), operation, status, errorDesc, product.getFileName(),
				product.getUpcId(), product.getOpDiv(), product.getProductType(), jsonData);
		
	}

	@Override
	public void insertFacetAuditData(String operation, String status, String errorDesc, MiraklData product) {
		
		StringBuilder queryBuilder = new StringBuilder();
		queryBuilder.append("INSERT INTO ").append(MP_FACET_AUDIT)
				.append("(CREATE_TS, OPERATION, STATUS, ERROR_DESC, FILE_NAME, UPC_ID, OP_DIV, PRODUCT_TYPE, DEPT, VENDOR, PID, NRF_COLOR_CODE, FACET_DATA)")
				.append("values(?,?,?,?,?,?,?,?,?,?,?,?,?)");
		jdbcTemplate.update(queryBuilder.toString(),  getCreateTs(), operation, status, errorDesc, product.getFileName(),
				product.getUpcId(), product.getOpDiv(), product.getProductType(),
				product.getDept(), product.getVendor(), product.getPid(),
				product.getNrfColorCode(), product.getFacetData() );
		
	}

	@Override
	public void insertImagesAuditData(String operation, ImageUpdateRespData imageUpdateRespData, String productType,
			String inputFileName, String jsonData) {
		String status = "";
		String errorDesc = "";
		if ((SUCCESS_MESSAGE.equalsIgnoreCase(imageUpdateRespData.getStatusMessage()))
				&& (STATUS_SUCCESS_CODE.equalsIgnoreCase(imageUpdateRespData.getStatusCode()))) {
			status = SUCCESS_MESSAGE;
			// errorDesc defaults to ""
		} else {
			status = ERROR_MESSAGE;
			errorDesc = imageUpdateRespData.getStatusMessage();
		}
		StringBuilder queryBuilder = new StringBuilder();
		queryBuilder.append("INSERT INTO ").append(MP_IMAGES_AUDIT).append(
				" (CREATE_TS, OPERATION, STATUS, ERROR_DESC, FILE_NAME, UPC_ID, OP_DIV, PRODUCT_TYPE, IMAGE_DATA")
				.append(")").append("values(?,?,?,?,?,?, ?, ?, ?)");
		jdbcTemplate.update(queryBuilder.toString(), getCreateTs(), operation, status,
				errorDesc, inputFileName, imageUpdateRespData.getUpcId(),
				imageUpdateRespData.getOpDiv(), productType, jsonData);

	}
}

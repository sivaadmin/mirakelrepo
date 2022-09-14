package com.macys.mirakl.dao;

import static com.macys.mirakl.util.OrchConstants.ABORTED;
import static com.macys.mirakl.util.OrchConstants.FACET;
import static com.macys.mirakl.util.OrchConstants.INPROCESS;
import static com.macys.mirakl.util.OrchConstants.MP_FACET_MASTER;
import static com.macys.mirakl.util.OrchConstants.MP_FACET_TEMP;
import static com.macys.mirakl.util.OrchConstants.MP_IMAGES_MASTER;
import static com.macys.mirakl.util.OrchConstants.MP_IMAGES_RESPONSE;
import static com.macys.mirakl.util.OrchConstants.MP_IMAGES_TEMP;
import static com.macys.mirakl.util.OrchConstants.MP_PDF_MASTER;
import static com.macys.mirakl.util.OrchConstants.MP_PDF_TEMP;
import static com.macys.mirakl.util.OrchConstants.MP_STELLA_MASTER;
import static com.macys.mirakl.util.OrchConstants.MP_STELLA_TEMP;
import static com.macys.mirakl.util.OrchConstants.MP_UPC_MASTER;
import static com.macys.mirakl.util.OrchConstants.PDF;
import static com.macys.mirakl.util.OrchConstants.PROCESSED;
import static com.macys.mirakl.util.OrchConstants.STELLA;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

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

@Repository
public class ProductUpdateDAOImpl implements ProductUpdateDAO {

	@Autowired
	JdbcTemplate jdbcTemplate;

	@Override
	public void batchInsertPdfTempData(List<PdfMasterData> prdTxns, String operation) {

		StringBuilder queryBuilder = new StringBuilder();
		queryBuilder.append("INSERT INTO ").append(MP_PDF_TEMP)
				.append(" (OPERATION,FILE_NAME,UPC_ID,OP_DIV,PRODUCT_TYPE,PDF_DATA) ").append("values (?,?,?,?,?,?)");

		List<Object[]> batchArgsList = new ArrayList<Object[]>();
		for (PdfMasterData prd : prdTxns) {
			Object[] objectArray = { operation, prd.getFileName(), prd.getUpcId(), prd.getOpDiv(), prd.getProductType(),
					prd.getPdfData() };
			batchArgsList.add(objectArray);
		}
		jdbcTemplate.batchUpdate(queryBuilder.toString(), batchArgsList);
	}

	@Override
	public void batchInsertStellaTempData(List<StellaMasterData> prdTxns, String operation) {

		StringBuilder queryBuilder = new StringBuilder();
		queryBuilder.append("INSERT INTO ").append(MP_STELLA_TEMP)
				.append(" (OPERATION,FILE_NAME,UPC_ID,OP_DIV,PRODUCT_TYPE,STELLA_DATA) ")
				.append("values (?,?,?,?,?,?)");

		List<Object[]> batchArgsList = new ArrayList<Object[]>();
		for (StellaMasterData prd : prdTxns) {
			Object[] objectArray = { operation, prd.getFileName(), prd.getUpcId(), prd.getOpDiv(), prd.getProductType(),
					prd.getStellaData() };
			batchArgsList.add(objectArray);
		}
		jdbcTemplate.batchUpdate(queryBuilder.toString(), batchArgsList);
	}

	@Override
	public void batchInsertImagesTempData(List<ImageMasterData> prdTxns, String operation) {

		StringBuilder queryBuilder = new StringBuilder();
		queryBuilder.append("INSERT INTO ").append(MP_IMAGES_TEMP)
				.append(" (OPERATION,FILE_NAME,UPC_ID,OP_DIV,PRODUCT_TYPE,IMAGE_DATA) ").append("values (?,?,?,?,?,?)");

		List<Object[]> batchArgsList = new ArrayList<Object[]>();
		for (ImageMasterData prd : prdTxns) {
			Object[] objectArray = { operation, prd.getFileName(), prd.getUpcId(), prd.getOpDiv(), prd.getProductType(),
					prd.getImageData() };
			batchArgsList.add(objectArray);
		}
		jdbcTemplate.batchUpdate(queryBuilder.toString(), batchArgsList);
	}

	@Override
	public void batchInsertFacetTempData(List<FacetMasterData> facetData, String operation) {
		StringBuilder queryBuilder = new StringBuilder();
		queryBuilder.append("INSERT INTO ").append(MP_FACET_TEMP).append(
				" (OPERATION, FILE_NAME, UPC_ID, OP_DIV, PRODUCT_TYPE, DEPT, VENDOR, PID, NRF_COLOR_CODE, FACET_DATA) ")
				.append("values(?,?,?,?,?,?,?,?,?,?)");
		List<Object[]> batchArgsList = new ArrayList<Object[]>();
		for (FacetMasterData facetMasterData : facetData) {
			Object[] objectArray = { operation, facetMasterData.getFileName(), facetMasterData.getUpcId(),
					facetMasterData.getOpDiv(), facetMasterData.getProductType(), facetMasterData.getDept(),
					facetMasterData.getVendor(), facetMasterData.getPid(), facetMasterData.getNrfcolor(),
					facetMasterData.getFacetData() };
			batchArgsList.add(objectArray);
		}
		jdbcTemplate.batchUpdate(queryBuilder.toString(), batchArgsList);

	}

	@Override
	public PdfMasterData findPdfMasterDataByUpc(String upcId, String opDiv) {
		StringBuilder queryBuilder = new StringBuilder();
		queryBuilder.append("select PRODUCT_TYPE, PDF_DATA from ").append(MP_PDF_MASTER)
				.append(" where UPC_ID=? and OP_DIV=?");
		return jdbcTemplate.queryForObject(queryBuilder.toString(), new PdfMasterDataRowMapper(),
				new Object[] { upcId, opDiv });

	}

	@Override
	public StellaMasterData findStellaMasterDataByUpc(String upcId, String opDiv) {
		StringBuilder queryBuilder = new StringBuilder();
		queryBuilder.append("select PRODUCT_TYPE, STELLA_DATA from ").append(MP_STELLA_MASTER)
				.append(" where UPC_ID=? and OP_DIV=?");
		return jdbcTemplate.queryForObject(queryBuilder.toString(), new StellaMasterDataRowMapper(),
				new Object[] { upcId, opDiv });

	}

	@Override
	public ImageMasterData findImageMasterDataByUpc(String upcId, String opDiv) {
		StringBuilder queryBuilder = new StringBuilder();
		queryBuilder.append("select PRODUCT_TYPE, IMAGE_DATA from ").append(MP_IMAGES_MASTER)
				.append(" where UPC_ID=? and OP_DIV=?");
		return jdbcTemplate.queryForObject(queryBuilder.toString(), new ImageMasterDataRowMapper(),
				new Object[] { upcId, opDiv });
	}

	@Override
	public FacetMasterData findFacetMasterDataByUpc(String upcId, String opDiv) {
		StringBuilder queryBuilder = new StringBuilder();
		queryBuilder.append("select PRODUCT_TYPE,FACET_DATA from ")
				.append(MP_FACET_MASTER)
				.append(" where UPC_ID=? and OP_DIV=?");
		return jdbcTemplate.queryForObject(queryBuilder.toString(), new FacetMasterDataRowMapper() ,
				new Object[] { upcId, opDiv });
	}

	@Override
	public boolean getIsMainImgP0ByUpc(String upcId, String opDiv) {
		StringBuilder queryBuilder = new StringBuilder();
		queryBuilder.append("select MAIN_IMG_FLAG from ").append(MP_UPC_MASTER).append(" where UPC_ID=? and OP_DIV=?");
		return (boolean) jdbcTemplate.queryForObject(queryBuilder.toString(), Boolean.class,
				new Object[] { upcId, opDiv });
	}

	@Override
	public PdfMasterData findPdfJsonFromTemp(String upcId, String opDiv, String fileNameInJson) {
		StringBuilder queryBuilder = new StringBuilder();
		queryBuilder.append("select PRODUCT_TYPE, PDF_DATA from ").append(MP_PDF_TEMP)
				.append(" where UPC_ID = ? and OP_DIV = ? and FILE_NAME = ?");
		return jdbcTemplate.queryForObject(queryBuilder.toString(), new PdfMasterDataRowMapper(),
				new Object[] { upcId, opDiv, fileNameInJson });
	}

	@Override
	public StellaMasterData findStellaJsonFromTemp(String upcId, String opDiv, String fileNameInJson) {
		StringBuilder queryBuilder = new StringBuilder();
		queryBuilder.append("select PRODUCT_TYPE, STELLA_DATA from ").append(MP_STELLA_TEMP)
				.append(" where UPC_ID = ? and OP_DIV = ? and FILE_NAME = ?");
		return jdbcTemplate.queryForObject(queryBuilder.toString(), new StellaMasterDataRowMapper(),
				new Object[] { upcId, opDiv, fileNameInJson });
	}

	@Override
	public void batchDeletePdfDataInTemp(List<PdfRespData> pdfUpdateDataList) {
		StringBuilder queryBuilder = new StringBuilder();
		queryBuilder.append("DELETE FROM ").append(MP_PDF_TEMP)
				.append(" where UPC_ID = ? and OP_DIV = ? and FILE_NAME = ?");

		List<Object[]> batchArgsList = new ArrayList<Object[]>();
		for (PdfRespData pdfRespData : pdfUpdateDataList) {
			Object[] objectArray = { pdfRespData.getUpcId(), pdfRespData.getOpDiv(), pdfRespData.getFileName() };
			batchArgsList.add(objectArray);
		}

		jdbcTemplate.batchUpdate(queryBuilder.toString(), batchArgsList);
	}
	
	@Override
	public void batchDeletePdfDataInTempNoDelta(List<PdfMasterData> pdfMasterDataList) {
		StringBuilder queryBuilder = new StringBuilder();
		queryBuilder.append("DELETE FROM ").append(MP_PDF_TEMP)
				.append(" where UPC_ID = ? and OP_DIV = ? and FILE_NAME = ?");

		List<Object[]> batchArgsList = new ArrayList<Object[]>();
		for (PdfMasterData pdfMasterData : pdfMasterDataList) {
			Object[] objectArray = { pdfMasterData.getUpcId(), pdfMasterData.getOpDiv(), pdfMasterData.getFileName() };
			batchArgsList.add(objectArray);
		}

		jdbcTemplate.batchUpdate(queryBuilder.toString(), batchArgsList);
	}
	
	@Override
	public void batchDeleteStellaDataInTempNoDelta(List<StellaMasterData> stellaMasterDataList) {
		StringBuilder queryBuilder = new StringBuilder();
		queryBuilder.append("DELETE FROM ").append(MP_STELLA_TEMP)
				.append(" where UPC_ID = ? and OP_DIV = ? and FILE_NAME = ?");

		List<Object[]> batchArgsList = new ArrayList<Object[]>();
		for (StellaMasterData stellaData : stellaMasterDataList) {
			Object[] objectArray = { stellaData.getUpcId(), stellaData.getOpDiv(), stellaData.getFileName() };
			batchArgsList.add(objectArray);
		}

		jdbcTemplate.batchUpdate(queryBuilder.toString(), batchArgsList);
	}

	@Override
	public void batchDeleteFacetDataInTempNoDelta(List<FacetMasterData> facetMasterDataList) {
		StringBuilder queryBuilder = new StringBuilder();
		queryBuilder.append("DELETE FROM ").append(MP_FACET_TEMP)
				.append(" where UPC_ID = ? and OP_DIV = ? and FILE_NAME = ?");

		List<Object[]> batchArgsList = new ArrayList<Object[]>();
		for (FacetMasterData facetData : facetMasterDataList) {
			Object[] objectArray = { facetData.getUpcId(), facetData.getOpDiv(), facetData.getFileName() };
			batchArgsList.add(objectArray);
		}

		jdbcTemplate.batchUpdate(queryBuilder.toString(), batchArgsList);
	}

	@Override
	public void batchDeleteStellaDataInTemp(List<StellaRespData> stellaUpdateDataList) {
		StringBuilder queryBuilder = new StringBuilder();
		queryBuilder.append("DELETE FROM ").append(MP_STELLA_TEMP)
				.append(" where UPC_ID = ? and OP_DIV = ? and FILE_NAME = ?");

		List<Object[]> batchArgsList = new ArrayList<Object[]>();
		for (StellaRespData stellaRespData : stellaUpdateDataList) {
			Object[] objectArray = { stellaRespData.getUpcId(), stellaRespData.getOpDiv(), stellaRespData.getFileName() };
			batchArgsList.add(objectArray);
		}

		jdbcTemplate.batchUpdate(queryBuilder.toString(), batchArgsList);
	}

	@Override
	public String findFacetJsonFromTemp(String upcId, String opDiv, String fileNameInJson) {
		StringBuilder queryBuilder = new StringBuilder();
		queryBuilder.append("select FACET_DATA from ").append(MP_FACET_TEMP)
				.append(" where UPC_ID = ? and OP_DIV = ? and FILE_NAME = ?");
		return (String) jdbcTemplate.queryForObject(queryBuilder.toString(), String.class,
				new Object[] { upcId, opDiv, fileNameInJson });
	}

	@Override
	public void updateFacetJsonInMaster(String upcId, String opDiv, String fileName, String facetJson) {
		StringBuilder queryBuilder = new StringBuilder();
		queryBuilder.append("UPDATE ").append(MP_FACET_MASTER)
				.append(" SET FILE_NAME = ?, FACET_DATA = ? where UPC_ID = ? and OP_DIV = ?");
		jdbcTemplate.update(queryBuilder.toString(), new Object[] { fileName, facetJson, upcId, opDiv });
	}

	@Override
	public void batchDeleteFacetDataInTemp(List<FacetUpdateRespData> facetUpdateRespAuditList) {
		StringBuilder queryBuilder = new StringBuilder();
		queryBuilder.append("DELETE FROM ").append(MP_FACET_TEMP)
				.append(" where UPC_ID = ? and OP_DIV = ? and FILE_NAME = ?");

		List<Object[]> batchArgsList = new ArrayList<Object[]>();
		for (FacetUpdateRespData facetData : facetUpdateRespAuditList) {
			Object[] objectArray = { facetData.getUpcId(), facetData.getOpDiv(), facetData.getFileNameJson() };
			batchArgsList.add(objectArray);
		}

		jdbcTemplate.batchUpdate(queryBuilder.toString(), batchArgsList);
	}

	@Override
	public void batchUpdateFacetJsonInMaster(List<FacetUpdateRespData> facetSuccessRespList) {

		StringBuilder queryBuilder = new StringBuilder();
		queryBuilder.append("UPDATE ").append(MP_FACET_MASTER)
				.append(" SET FILE_NAME = ?, FACET_DATA = ? where UPC_ID = ? and OP_DIV = ? ");

		List<Object[]> batchArgsList = new ArrayList<Object[]>();
		for (FacetUpdateRespData facetSuccessData : facetSuccessRespList) {
			Object[] objectArray = { facetSuccessData.getFileNameJson(), facetSuccessData.getFacetData(),
					facetSuccessData.getUpcId(), facetSuccessData.getOpDiv() };
			batchArgsList.add(objectArray);
		}
		jdbcTemplate.batchUpdate(queryBuilder.toString(), batchArgsList);
	}
	
	@Override
	public void batchUpdatePdfJsonInMaster(List<PdfRespData> pdfSuccessDataList) {

		StringBuilder queryBuilder = new StringBuilder();
		queryBuilder.append("UPDATE ").append(MP_PDF_MASTER)
				.append(" SET FILE_NAME = ?, PDF_DATA = ? where UPC_ID = ? and OP_DIV = ? ");

		List<Object[]> batchArgsList = new ArrayList<Object[]>();
		for (PdfRespData pdfSuccessData : pdfSuccessDataList) {
			Object[] objectArray = { pdfSuccessData.getFileName(), pdfSuccessData.getPdfData(),
					pdfSuccessData.getUpcId(), pdfSuccessData.getOpDiv() };
			batchArgsList.add(objectArray);
		}
		jdbcTemplate.batchUpdate(queryBuilder.toString(), batchArgsList);
	}

	@Override
	public void batchUpdateStellaJsonInMaster(List<StellaRespData> stellaSuccessDataList) {

		StringBuilder queryBuilder = new StringBuilder();
		queryBuilder.append("UPDATE ").append(MP_STELLA_MASTER)
				.append(" SET FILE_NAME = ?, STELLA_DATA = ? where UPC_ID = ? and OP_DIV = ? ");

		List<Object[]> batchArgsList = new ArrayList<Object[]>();
		for (StellaRespData stellaSuccessData : stellaSuccessDataList) {
			Object[] objectArray = { stellaSuccessData.getFileName(), stellaSuccessData.getStellaData(),
					stellaSuccessData.getUpcId(), stellaSuccessData.getOpDiv() };
			batchArgsList.add(objectArray);
		}
		jdbcTemplate.batchUpdate(queryBuilder.toString(), batchArgsList);
	}

	@Override
	public void batchUpdateImagesMasterDataList(List<ImageMasterData> imageDataList) {

		StringBuilder queryBuilder = new StringBuilder();
		queryBuilder.append("UPDATE ").append(MP_IMAGES_MASTER)
				.append(" SET FILE_NAME = ?, IMAGE_DATA = ? where UPC_ID = ? and OP_DIV = ? ");

		List<Object[]> batchArgsList = new ArrayList<Object[]>();
		for (ImageMasterData imageData : imageDataList) {
			Object[] objectArray = { imageData.getFileName(), imageData.getImageData(),
					imageData.getUpcId(), imageData.getOpDiv() };
			batchArgsList.add(objectArray);
		}
		jdbcTemplate.batchUpdate(queryBuilder.toString(), batchArgsList);
	}

	@Override
	public void deleteAppDataInTemp(MiraklData product, String app) {
		
		StringBuilder queryBuilder = new StringBuilder();
		String tempTable = "";
		if(STELLA.equalsIgnoreCase(app)) {
			tempTable = MP_STELLA_TEMP;
		} else if(PDF.equalsIgnoreCase(app)) {
			tempTable = MP_PDF_TEMP;
		} else if(FACET.equalsIgnoreCase(app)) {
			tempTable = MP_FACET_TEMP;
		}
		queryBuilder.append("DELETE FROM ").append(tempTable)
				.append(" where UPC_ID = ? and OP_DIV = ? and FILE_NAME = ?");
		jdbcTemplate.update(queryBuilder.toString(), product.getUpcId(), product.getOpDiv(), product.getFileName());
		
	}
	
	@Override
	public void updateImagesRespMessage(ImageUpdateRespData imageUpdateRespData) {
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		StringBuilder queryBuilder = new StringBuilder();
		queryBuilder.append("UPDATE ").append(MP_IMAGES_RESPONSE).append(
				" SET UPDATED_TS = ?, PROCESSING_STAGE = ?, IAS_RESP_RECEIVED_FLAG = ?, IMG_FILE_NAME = ?, STATUS_CODE = ?, STATUS_MESSAGE = ? "
						+ "where UPC_ID = ? and OP_DIV = ? and IMG_TYPE = ? and PROCESSING_STAGE = ? and IAS_RESP_RECEIVED_FLAG = ?");
		jdbcTemplate.update(queryBuilder.toString(),
				new Object[] { timestamp, PROCESSED, true, imageUpdateRespData.getImgFileName(),
						imageUpdateRespData.getStatusCode(), imageUpdateRespData.getStatusMessage(),
						imageUpdateRespData.getUpcId(), imageUpdateRespData.getOpDiv(),
						imageUpdateRespData.getImgType(), INPROCESS, false });
	}

	@Override
	public ImageUpdateRespData fetchImagesRespMessage(ImageUpdateRespData imageUpdateRespData) {

		StringBuilder queryBuilder = new StringBuilder();
		queryBuilder.append("select * from ").append(MP_IMAGES_RESPONSE)
				.append(" where UPC_ID=? and OP_DIV=? and IMG_TYPE=? and PROCESSING_STAGE=? and IAS_RESP_RECEIVED_FLAG=?");
		return jdbcTemplate.queryForObject(queryBuilder.toString(), new ImageResponseDataRowMapper(),
				new Object[] { imageUpdateRespData.getUpcId(), imageUpdateRespData.getOpDiv(),
						imageUpdateRespData.getImgType(), INPROCESS, false });

	}

	@Override
	public void updateImagesMasterJson(ImageMasterData imageMasterData) {
		StringBuilder queryBuilder = new StringBuilder();
		queryBuilder.append("UPDATE ").append(MP_IMAGES_MASTER)
				.append(" SET FILE_NAME = ?, IMAGE_DATA = ? where UPC_ID = ? and OP_DIV = ? ");
		jdbcTemplate.update(queryBuilder.toString(), new Object[] { imageMasterData.getFileName(),
				imageMasterData.getImageData(), imageMasterData.getUpcId(), imageMasterData.getOpDiv() });
	}
	
	@Override
	public void batchInsertImageResData(List<ImageUpdateRespData> imageUpdateRespDataList) {
		StringBuilder queryBuilder = new StringBuilder();
		queryBuilder.append("INSERT INTO ").append(MP_IMAGES_RESPONSE).append(
				"(CREATED_TS,PROCESSING_STAGE,INPUT_FILE_NAME,UPC_ID,OP_DIV,DELTA_FILE_NAME,IMG_ID,IMG_TYPE,IMG_URL,IAS_RESP_RECEIVED_FLAG,IL_RESP_SENT_FLAG)")
				.append("values(?,?,?,?,?,?,?,?,?,?,?)");
		List<Object[]> batchArgsList = new ArrayList<Object[]>();
		for (ImageUpdateRespData imageUpdateRespData : imageUpdateRespDataList) {
			Object[] objectArray = { imageUpdateRespData.getCreatedTs(), imageUpdateRespData.getProcessingStage(),
					imageUpdateRespData.getInputFileName(), imageUpdateRespData.getUpcId(),
					imageUpdateRespData.getOpDiv(),imageUpdateRespData.getDeltaFileName(),imageUpdateRespData.getImgId(),
					imageUpdateRespData.getImgType(), imageUpdateRespData.getImgUrl(),
					imageUpdateRespData.getIasRespReceivedFlag(),imageUpdateRespData.getIlRespSentFlag() };
			batchArgsList.add(objectArray);
		}
		jdbcTemplate.batchUpdate(queryBuilder.toString(), batchArgsList);

	}
	
	@Override
	public List<ImageUpdateRespData> findImageUpdateRespData() {
		StringBuilder queryBuilder = new StringBuilder();
		queryBuilder.append("SELECT * FROM MP_IMAGES_RESPONSE WHERE "
			+ "PROCESSING_STAGE = \"PROCESSED\" "
			+ "and IAS_RESP_RECEIVED_FLAG = true "
			+ "and IL_RESP_SENT_FLAG= false");
		List<ImageUpdateRespData> imageUpdateRespData = jdbcTemplate.query(queryBuilder.toString(), new ImageResponseDataRowMapper());
		return imageUpdateRespData;
	}
	
	@Override
	public void updateImageUpdateRespData(List<ImageUpdateRespData> imageUpdateRespData, String resFilename) throws MiraklRepositoryException {
		boolean ilRespSentFlag = true;
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		StringBuilder queryBuilder = new StringBuilder();
		queryBuilder.append("UPDATE ")
		.append("MP_IMAGES_RESPONSE")
		.append(" SET IL_RESP_SENT_FLAG=?,")
		.append(" RESP_FILE_NAME=?,")
		.append(" UPDATED_TS =? ")
		.append("WHERE UPC_ID = ? AND OP_DIV = ? AND INPUT_FILE_NAME= ? AND IMG_TYPE= ?"
				+ "AND IAS_RESP_RECEIVED_FLAG = true "
				+ "AND IL_RESP_SENT_FLAG= false "
				+ "AND PROCESSING_STAGE = \"PROCESSED\"");
		
		try {
			List<Object[]> batchArgsList = new ArrayList<Object[]>(); 
			for (ImageUpdateRespData respData : imageUpdateRespData) { 
				Object[] objectArray = { ilRespSentFlag, resFilename, timestamp, respData.getUpcId(), 
						respData.getOpDiv(),respData.getInputFileName(),respData.getImgType()}; 
				batchArgsList.add(objectArray);
			}
			jdbcTemplate.batchUpdate(queryBuilder.toString(), batchArgsList);
		} catch(Exception e) {
			throw new MiraklRepositoryException("Exception in updateImageUpdateRespData:"+e);
		}

	}

	@Override
	public void batchUpdateImageRespData(List<ImageUpdateRespData> imageUpdateRespDataList) {

		StringBuilder queryBuilder = new StringBuilder();
		queryBuilder.append("UPDATE ").append(MP_IMAGES_RESPONSE).append(
				" SET PROCESSING_STAGE = ?, STATUS_MESSAGE = ? where UPC_ID = ? and OP_DIV = ? and IMG_TYPE=? and PROCESSING_STAGE=?");

		List<Object[]> batchArgsList = new ArrayList<Object[]>();
		for (ImageUpdateRespData imageData : imageUpdateRespDataList) {
			Object[] objectArray = { ABORTED, "NEW UPDATE REQUEST RECEIVED", imageData.getUpcId(), imageData.getOpDiv(),
					imageData.getImgType(), INPROCESS };
			batchArgsList.add(objectArray);
		}
		jdbcTemplate.batchUpdate(queryBuilder.toString(), batchArgsList);
	}
	
}
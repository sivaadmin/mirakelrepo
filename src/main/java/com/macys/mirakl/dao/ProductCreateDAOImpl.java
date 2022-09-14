package com.macys.mirakl.dao;

import static com.macys.mirakl.util.OrchConstants.MP_FACET_MASTER;
import static com.macys.mirakl.util.OrchConstants.MP_IMAGES_MASTER;
import static com.macys.mirakl.util.OrchConstants.MP_PDF_MASTER;
import static com.macys.mirakl.util.OrchConstants.MP_STELLA_MASTER;
import static com.macys.mirakl.util.OrchConstants.MP_UPC_MASTER;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import com.macys.mirakl.exception.MiraklRepositoryException;
import com.macys.mirakl.model.FacetMasterData;
import com.macys.mirakl.model.ImageMasterData;
import com.macys.mirakl.model.PdfMasterData;
import com.macys.mirakl.model.StellaMasterData;
import com.macys.mirakl.model.UpcMasterData;
import com.macys.mirakl.util.FilePrefix;

@Repository
public class ProductCreateDAOImpl implements ProductCreateDAO {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ProductCreateDAOImpl.class);

	@Autowired
	JdbcTemplate jdbcTemplate;
	
	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	private static final String FETCH_EXISTING_UPCS = "SELECT UPC_ID FROM MP_PDF_MASTER where UPC_ID in (:upcs) and OP_DIV = :opDiv";
	
	@Override
	public List<String> findExistingMasterUpcs(List<String> upcs, FilePrefix filePrefix)
			throws MiraklRepositoryException {

		List<String> existingUpcs = null;

		try {

			SqlParameterSource parameterSource = new MapSqlParameterSource().addValue("upcs", upcs).addValue("opDiv",
					filePrefix.getOpDiv());

			existingUpcs = namedParameterJdbcTemplate.queryForList(FETCH_EXISTING_UPCS, parameterSource, String.class);

		} catch (Exception ex) {
			LOGGER.error("Failed to fetch existing upcs reason", ex);
			throw new MiraklRepositoryException("Failed to fetch existing upcs reason "+ex);

		}
		return existingUpcs;
	}

	@Override
	public void batchInsertUpcMasterData(List<UpcMasterData> upcData) {

		StringBuilder queryBuilder = new StringBuilder();
		queryBuilder.append("INSERT INTO ").append(MP_UPC_MASTER)
				.append("(UPC_ID, OP_DIV, PRODUCT_TYPE, DEPT, VENDOR, PID, NRF_COLOR_CODE, TAX_CODE, NRF_SIZE_CODE, MSRP, OFFER_ID, MAIN_IMG_FLAG, BUYER_APPROVED_FLAG)")
				.append("values (?,?,?,?,?,?,?,?,?,?,?,?,?)");
		List<Object[]> batchArgsList = new ArrayList<Object[]>();
		for (UpcMasterData upcMasterData : upcData) {
			Object[] objectArray = {upcMasterData.getUpcId(), upcMasterData.getOpDiv(), upcMasterData.getProductType(),
					upcMasterData.getDept(), upcMasterData.getVendor(), upcMasterData.getPid(), upcMasterData.getNrfColorCode(),
					upcMasterData.getTaxCode(), upcMasterData.getNrfSizeCode(), upcMasterData.getMsrp(), upcMasterData.getOfferId(), 
					upcMasterData.isMainImgFlag(), upcMasterData.isBuyerApprovedFlag() };
			batchArgsList.add(objectArray);
		}
		jdbcTemplate.batchUpdate(queryBuilder.toString(), batchArgsList);

	}

	@Override
	public void batchInsertPdfMasterData(List<PdfMasterData> pdfData) {

		StringBuilder queryBuilder = new StringBuilder();
		queryBuilder.append("INSERT INTO ").append(MP_PDF_MASTER)
				.append("(FILE_NAME,UPC_ID, OP_DIV, PRODUCT_TYPE,PDF_DATA)").append("values(?,?, ?, ?,?)");
		List<Object[]> batchArgsList = new ArrayList<Object[]>();
		for (PdfMasterData pdfMasterData : pdfData) {
			Object[] objectArray = { pdfMasterData.getFileName(), pdfMasterData.getUpcId(), pdfMasterData.getOpDiv(),
					pdfMasterData.getProductType(), pdfMasterData.getPdfData() };
			batchArgsList.add(objectArray);
		}
		jdbcTemplate.batchUpdate(queryBuilder.toString(), batchArgsList);

	}

	@Override
	public void batchInsertStellaMasterData(List<StellaMasterData> stellaData) {
		StringBuilder queryBuilder = new StringBuilder();
		queryBuilder.append("INSERT INTO ").append(MP_STELLA_MASTER)
				.append("(FILE_NAME,UPC_ID, OP_DIV, PRODUCT_TYPE, STELLA_DATA)")
				.append("values(?,?, ?, ?, ?)");
		List<Object[]> batchArgsList = new ArrayList<Object[]>();
		for (StellaMasterData stellaMasterData : stellaData) {
			Object[] objectArray = { stellaMasterData.getFileName(), stellaMasterData.getUpcId(),
					stellaMasterData.getOpDiv(), stellaMasterData.getProductType(), stellaMasterData.getStellaData() };
			batchArgsList.add(objectArray);
		}
		jdbcTemplate.batchUpdate(queryBuilder.toString(), batchArgsList);
	}

	@Override
	public void batchInsertImageMasterData(List<ImageMasterData> imageData) {
		StringBuilder queryBuilder = new StringBuilder();
		queryBuilder.append("INSERT INTO ").append(MP_IMAGES_MASTER)
		.append("(FILE_NAME, UPC_ID, OP_DIV, PRODUCT_TYPE, IMAGE_DATA)")
		.append("values(?,?,?, ?,?)");
		List<Object[]> batchArgsList = new ArrayList<Object[]>();
		for (ImageMasterData imageMasterData : imageData) {
			Object[] objectArray = { imageMasterData.getFileName(), imageMasterData.getUpcId(),
					imageMasterData.getOpDiv(), imageMasterData.getProductType(), imageMasterData.getImageData() };
			batchArgsList.add(objectArray);
		}
		jdbcTemplate.batchUpdate(queryBuilder.toString(), batchArgsList);
	}

	@Override
	public void batchInsertFacetMasterData(List<FacetMasterData> facetData) {
		StringBuilder queryBuilder = new StringBuilder();
		queryBuilder.append("INSERT INTO ").append(MP_FACET_MASTER)
		.append("(FILE_NAME, UPC_ID, OP_DIV, PRODUCT_TYPE, DEPT, VENDOR, PID, NRF_COLOR_CODE, FACET_DATA)")
		.append("values(?,?,?,?,?,?,?,?,?)");
		List<Object[]> batchArgsList = new ArrayList<Object[]>();
		for (FacetMasterData facetMasterData : facetData) {
			Object[] objectArray = { facetMasterData.getFileName(), facetMasterData.getUpcId(),
					facetMasterData.getOpDiv(), facetMasterData.getProductType(),
					facetMasterData.getDept(), facetMasterData.getVendor(), facetMasterData.getPid(), facetMasterData.getNrfcolor(), 
					facetMasterData.getFacetData() };
			batchArgsList.add(objectArray);
		}
		jdbcTemplate.batchUpdate(queryBuilder.toString(), batchArgsList);

	}

}

package com.macys.mirakl.dao;

import static com.macys.mirakl.util.OrchConstants.PDF_DATA_JSON;
import static com.macys.mirakl.util.OrchConstants.PRODUCT_TYPE_DB;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.macys.mirakl.model.PdfMasterData;

public class PdfMasterDataRowMapper implements RowMapper<PdfMasterData>{

	@Override
	public PdfMasterData mapRow(ResultSet rs, int rowNum) throws SQLException {
		PdfMasterData pdfMasterData = new PdfMasterData();
		pdfMasterData.setProductType(rs.getString(PRODUCT_TYPE_DB));
		pdfMasterData.setPdfData(rs.getString(PDF_DATA_JSON));
		return pdfMasterData;
	}
	
	

}

package com.macys.mirakl.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.macys.mirakl.model.ImageUpdateRespData;

import static com.macys.mirakl.util.OrchConstants.IMG_FILE_NAME;
import static com.macys.mirakl.util.OrchConstants.UPC_ID;
import static com.macys.mirakl.util.OrchConstants.OP_DIV;
import static com.macys.mirakl.util.OrchConstants.STATUS_CODE;
import static com.macys.mirakl.util.OrchConstants.STATUS_MESSAGE;

public class ImageResponseDataRowMapper implements RowMapper<ImageUpdateRespData>{
	
	@Override
	public ImageUpdateRespData mapRow(ResultSet rs, int rowNum) throws SQLException {
		ImageUpdateRespData imageUpdateRespData = new ImageUpdateRespData();
		imageUpdateRespData.setInputFileName(rs.getString("INPUT_FILE_NAME"));
		imageUpdateRespData.setImgUrl(rs.getString("IMG_URL"));
		imageUpdateRespData.setImgId(rs.getString("IMG_ID"));
		imageUpdateRespData.setImgType(rs.getString("IMG_TYPE"));
		imageUpdateRespData.setImgFileName(rs.getString(IMG_FILE_NAME));
		imageUpdateRespData.setUpcId(rs.getString(UPC_ID));
		imageUpdateRespData.setOpDiv(rs.getString(OP_DIV));
		imageUpdateRespData.setStatusCode(rs.getString(STATUS_CODE));
		imageUpdateRespData.setStatusMessage(rs.getString(STATUS_MESSAGE));
		return imageUpdateRespData;
	}

}

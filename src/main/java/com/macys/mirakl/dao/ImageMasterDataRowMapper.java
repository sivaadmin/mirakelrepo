package com.macys.mirakl.dao;

import static com.macys.mirakl.util.OrchConstants.IMAGE_DATA_JSON;
import static com.macys.mirakl.util.OrchConstants.PRODUCT_TYPE_DB;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.macys.mirakl.model.ImageMasterData;

public class ImageMasterDataRowMapper implements RowMapper<ImageMasterData>{
	
	@Override
	public ImageMasterData mapRow(ResultSet rs, int rowNum) throws SQLException {
		ImageMasterData imageMasterData = new ImageMasterData();
		imageMasterData.setProductType(rs.getString(PRODUCT_TYPE_DB));
		imageMasterData.setImageData(rs.getString(IMAGE_DATA_JSON));
		return imageMasterData;
	}

}

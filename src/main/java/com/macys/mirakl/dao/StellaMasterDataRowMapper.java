package com.macys.mirakl.dao;

import static com.macys.mirakl.util.OrchConstants.PRODUCT_TYPE_DB;
import static com.macys.mirakl.util.OrchConstants.STELLA_DATA_JSON;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.macys.mirakl.model.StellaMasterData;

public class StellaMasterDataRowMapper implements RowMapper<StellaMasterData>{

	@Override
	public StellaMasterData mapRow(ResultSet rs, int rowNum) throws SQLException {
		StellaMasterData stellaMasterData = new StellaMasterData();
		stellaMasterData.setProductType(rs.getString(PRODUCT_TYPE_DB));
		stellaMasterData.setStellaData(rs.getString(STELLA_DATA_JSON));
		return stellaMasterData;
	}
	

}

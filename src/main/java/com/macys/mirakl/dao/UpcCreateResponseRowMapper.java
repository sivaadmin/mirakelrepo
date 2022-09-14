package com.macys.mirakl.dao;

import static com.macys.mirakl.util.OrchConstants.MSRP_DB;
import static com.macys.mirakl.util.OrchConstants.NRF_COLOR_CODE_DB;
import static com.macys.mirakl.util.OrchConstants.NRF_SIZE_CODE_DB;
import static com.macys.mirakl.util.OrchConstants.PID_DB;
import static com.macys.mirakl.util.OrchConstants.TAX_CODE_DB;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.macys.mirakl.model.UpcMasterData;

public class UpcCreateResponseRowMapper implements RowMapper<UpcMasterData>{

	@Override
	public UpcMasterData mapRow(ResultSet rs, int rowNum) throws SQLException {
		UpcMasterData upcMasterData = new UpcMasterData();
		upcMasterData.setPid(rs.getString(PID_DB));
		upcMasterData.setNrfColorCode(rs.getString(NRF_COLOR_CODE_DB));
		upcMasterData.setNrfSizeCode(rs.getString(NRF_SIZE_CODE_DB));
		upcMasterData.setMsrp(rs.getString(MSRP_DB));
		upcMasterData.setTaxCode(rs.getString(TAX_CODE_DB));
		return upcMasterData;
	}
}
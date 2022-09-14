package com.macys.mirakl.dao;

import com.macys.mirakl.model.FacetMasterData;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import static com.macys.mirakl.util.OrchConstants.FACET_DATA_DB;
import static com.macys.mirakl.util.OrchConstants.PRODUCT_TYPE_DB;

public class FacetMasterDataRowMapper implements RowMapper<FacetMasterData> {

    @Override
    public FacetMasterData mapRow(ResultSet rs, int rowNum) throws SQLException {
        FacetMasterData facetMasterData = new FacetMasterData();
        facetMasterData.setProductType(rs.getString(PRODUCT_TYPE_DB));
        facetMasterData.setFacetData(rs.getString(FACET_DATA_DB));
        return facetMasterData;
    }
}

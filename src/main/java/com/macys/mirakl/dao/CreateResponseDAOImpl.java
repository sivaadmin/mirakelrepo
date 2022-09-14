package com.macys.mirakl.dao;

import static com.macys.mirakl.util.OrchConstants.MP_CREATE_RESP_AUDIT;
import static com.macys.mirakl.util.OrchConstants.MP_UPC_MASTER;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.macys.mirakl.exception.MiraklRepositoryException;
import com.macys.mirakl.model.CreateResponseData;
import com.macys.mirakl.model.PublishUpcData;
import com.macys.mirakl.model.UpcMasterData;

@Repository
public class CreateResponseDAOImpl implements CreateResponseDAO {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CreateResponseDAOImpl.class);
	
	@Autowired
	JdbcTemplate jdbcTemplate;

	@Override
	public void insertCreateRespAuditList(List<CreateResponseData> createResponseDatas) throws MiraklRepositoryException {
		
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		StringBuilder queryBuilder = new StringBuilder();
		queryBuilder.append("INSERT INTO ")
		.append(MP_CREATE_RESP_AUDIT)
		.append(" (CREATE_TS,UPC_ID,OP_DIV,PRODUCT_SKU,PID,NRF_COLOR_CODE,NRF_SIZE_CODE,MSRP,TAX_CODE) ")
		.append("values (?,?,?,?,?,?,?,?,?)");
		
		try {
			List<Object[]> batchArgsList = new ArrayList<Object[]>();
			for (CreateResponseData data : createResponseDatas) {
				Object[] objectArray = {timestamp, data.getUpc(), data.getOpDiv(), data.getProductSku(), data.getPid(), data.getNrfColorCode()
						, data.getNrfSizeCode(), data.getMsrp(), data.getTaxCode()};
				batchArgsList.add(objectArray);
			}

			jdbcTemplate.batchUpdate(queryBuilder.toString(), batchArgsList);
		} catch(Exception e) {
			LOGGER.error("Exception in insertCreateRespAuditList:",e);
			throw new MiraklRepositoryException("Exception in insertCreateRespAuditList:"+e);
		}			
		
			
	}

	@Override
	public void updateCreateRespList(List<PublishUpcData> upcDataList) throws MiraklRepositoryException {
		boolean isBuyerApproved = true;
		StringBuilder queryBuilder = new StringBuilder();
		queryBuilder.append("UPDATE ")
		.append(MP_UPC_MASTER)
		.append(" SET MAIN_IMG_FLAG=? ")
		.append(" , BUYER_APPROVED_FLAG=? ")
		.append("WHERE UPC_ID = ? AND OP_DIV = ?");

		try {
			List<Object[]> batchArgsList = new ArrayList<Object[]>(); 
			for (PublishUpcData upcData : upcDataList) { 
				Object[] objectArray = {upcData.isImageUpc(), isBuyerApproved, upcData.getUpc(), upcData.getOpDiv()}; 
				batchArgsList.add(objectArray);
			}
			  
			jdbcTemplate.batchUpdate(queryBuilder.toString(), batchArgsList);
		} catch(Exception e) {
			LOGGER.error("Exception in updateCreateRespList:",e);
			throw new MiraklRepositoryException("Exception in updateCreateRespList:"+e);
		}	
		
	}

	@Override
	public UpcMasterData findUpcMasterDataByUpc(String upcId, String opDiv) throws MiraklRepositoryException {
		StringBuilder queryBuilder = new StringBuilder();
		try {
			queryBuilder.append("select PID, NRF_COLOR_CODE, NRF_SIZE_CODE, MSRP, TAX_CODE from ")
			.append(MP_UPC_MASTER)
			.append(" where UPC_ID=? and OP_DIV=?");
			return jdbcTemplate.queryForObject(queryBuilder.toString(), new UpcCreateResponseRowMapper(),
					new Object[] { upcId, opDiv });
		} catch(Exception e) {
			LOGGER.error("Exception in findUpcMasterDataByUpc:",e);
			throw new MiraklRepositoryException("Exception in findUpcMasterDataByUpc:"+e);
		}	

	}
	

}
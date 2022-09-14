package com.macys.mirakl.dao;

import static com.macys.mirakl.util.OrchConstants.ERROR_MESSAGE;
import static com.macys.mirakl.util.OrchConstants.MP_OFFER_AUDIT;
import static com.macys.mirakl.util.OrchConstants.MP_UPC_MASTER;
import static com.macys.mirakl.util.OrchConstants.PENDING;
import static com.macys.mirakl.util.OrchConstants.STATUS_SUCCESS_CODE;
import static com.macys.mirakl.util.OrchConstants.SUCCESS_MESSAGE;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.macys.mirakl.exception.MiraklRepositoryException;
import com.macys.mirakl.model.OfferIdRequestData;
import com.macys.mirakl.model.OfferIdResponseData;

import io.micrometer.core.instrument.util.StringUtils;

@Repository
public class OfferIdDAOImpl implements OfferIdDAO {
	private static final Logger LOGGER = LoggerFactory.getLogger(OfferIdDAOImpl.class);
	
	@Autowired
	JdbcTemplate jdbcTemplate;

	@Override
	public void insertOfferIdAuditList(List<OfferIdRequestData> offerIdReqList, String fileName) throws MiraklRepositoryException {
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		StringBuilder queryBuilder = new StringBuilder();
		queryBuilder.append("INSERT INTO ")
		.append(MP_OFFER_AUDIT)
		.append(" (CREATE_TS,FILE_NAME,UPC_ID,OP_DIV,OFFER_ID,OFFER_ID_STATUS,ERROR_DESC,ACTIVE_FLAG,DELETED_FLAG) ")
		.append("values (?,?,?,?,?,?,?,?,?)");
		
		try {
			List<Object[]> batchArgsList = new ArrayList<Object[]>();
			for (OfferIdRequestData offerData : offerIdReqList) {
				Object[] objectArray = { timestamp, fileName, offerData.getUpcId(), offerData.getOpDiv(),
						offerData.getOfferId(), PENDING, "", offerData.getActive(), offerData.getDeleted()};
				batchArgsList.add(objectArray);
			}

			jdbcTemplate.batchUpdate(queryBuilder.toString(), batchArgsList);
		} catch(Exception e) {
			LOGGER.error("Exception in insertOfferIdAuditList:",e);
			throw new MiraklRepositoryException("Exception in insertOfferIdAuditList:"+e);
		}			
		
	}
	
	@Override
	public void updateOfferIdMasterList(List<OfferIdResponseData> offerIdRespList) throws MiraklRepositoryException {
		StringBuilder queryBuilder = new StringBuilder();
		queryBuilder.append("UPDATE ")
		.append(MP_UPC_MASTER)
		.append(" SET OFFER_ID=? ")
		.append("WHERE UPC_ID = ? and OP_DIV = ? and BUYER_APPROVED_FLAG= ?");

		try {
			List<Object[]> batchArgsList = new ArrayList<Object[]>(); 
			for (OfferIdResponseData offerData : offerIdRespList) { 
				if((SUCCESS_MESSAGE.equalsIgnoreCase(offerData.getMessage())) && (STATUS_SUCCESS_CODE.equalsIgnoreCase(offerData.getStatus()))) {
					Object[] objectArray = {offerData.getOfferId(), offerData.getUpcId(), offerData.getOpDiv(), true}; 
					batchArgsList.add(objectArray);
				}
			}
			  
			jdbcTemplate.batchUpdate(queryBuilder.toString(), batchArgsList);
		} catch(Exception e) {
			LOGGER.error("Exception in updateOfferIdMasterList:",e);
			throw new MiraklRepositoryException("Exception in updateOfferIdMasterList:"+e);
		}	
		
	}

	@Override
	public void updateOfferIdAuditList(List<OfferIdResponseData> offerIdRespList) throws MiraklRepositoryException {

		try {

			StringBuilder queryBuilder = new StringBuilder();
			StringBuilder queryBuilderNoOfferId = new StringBuilder();
			List<Object[]> batchArgsList = new ArrayList<Object[]>();
			List<Object[]> batchArgsListNoOfferId = new ArrayList<Object[]>();

			queryBuilder.append("UPDATE ").append(MP_OFFER_AUDIT).append(
					" SET OFFER_ID_STATUS = ?, ERROR_DESC = ? where UPC_ID = ? and OP_DIV = ? and FILE_NAME=? and OFFER_ID=?");

			queryBuilderNoOfferId.append("UPDATE ").append(MP_OFFER_AUDIT)
					.append(" SET OFFER_ID_STATUS = ?, ERROR_DESC = ? where UPC_ID = ? and OP_DIV = ? and FILE_NAME=?");

			for (OfferIdResponseData offerData : offerIdRespList) {
				processOfferIdResp(batchArgsList, batchArgsListNoOfferId, offerData);
			}

			jdbcTemplate.batchUpdate(queryBuilder.toString(), batchArgsList);
			jdbcTemplate.batchUpdate(queryBuilderNoOfferId.toString(), batchArgsListNoOfferId);

		} catch (Exception e) {
			LOGGER.error("Exception in updateOfferIdAuditList:", e);
			throw new MiraklRepositoryException("Exception in updateOfferIdAuditList:" + e);
		}
	}

	private void processOfferIdResp(List<Object[]> batchArgsList, List<Object[]> batchArgsListNoOfferId,
			OfferIdResponseData offerData) {
		String offerIdStatus = "";
		String errorDesc = "";
		if ((SUCCESS_MESSAGE.equalsIgnoreCase(offerData.getMessage()))
				&& (STATUS_SUCCESS_CODE.equalsIgnoreCase(offerData.getStatus()))) {
			offerIdStatus = SUCCESS_MESSAGE;
			// errorDesc defaults to ""
		} else {
			offerIdStatus = ERROR_MESSAGE;
			errorDesc = offerData.getMessage();
		}
		if (StringUtils.isNotEmpty(offerData.getOfferId())) {
			Object[] objectArray = { offerIdStatus, errorDesc, offerData.getUpcId(), offerData.getOpDiv(),
					offerData.getFileNameJson(), offerData.getOfferId() };
			batchArgsList.add(objectArray);
		} else {
			Object[] objectArray = { offerIdStatus, errorDesc, offerData.getUpcId(), offerData.getOpDiv(),
					offerData.getFileNameJson() };
			batchArgsListNoOfferId.add(objectArray);
		}
	}
}
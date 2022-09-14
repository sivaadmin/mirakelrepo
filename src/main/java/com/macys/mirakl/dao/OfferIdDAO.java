package com.macys.mirakl.dao;

import java.util.List;

import com.macys.mirakl.exception.MiraklRepositoryException;
import com.macys.mirakl.model.OfferIdRequestData;
import com.macys.mirakl.model.OfferIdResponseData;

public interface OfferIdDAO {
	
	void insertOfferIdAuditList(List<OfferIdRequestData> offerIdReqList, String fileName) throws MiraklRepositoryException;
	
	void updateOfferIdAuditList(List<OfferIdResponseData> offerIdRespList) throws MiraklRepositoryException;

	void updateOfferIdMasterList(List<OfferIdResponseData> offerIdRespList) throws MiraklRepositoryException;

}
package com.macys.mirakl.dao;

import java.util.List;

import com.macys.mirakl.exception.MiraklRepositoryException;
import com.macys.mirakl.model.CreateResponseData;
import com.macys.mirakl.model.PublishUpcData;
import com.macys.mirakl.model.UpcMasterData;

public interface CreateResponseDAO {
	
	void insertCreateRespAuditList(List<CreateResponseData> createResponseDatas) throws MiraklRepositoryException;
	
	void updateCreateRespList(List<PublishUpcData> upcDataList) throws MiraklRepositoryException;
	
	UpcMasterData findUpcMasterDataByUpc(String upcId, String opDiv) throws MiraklRepositoryException;

}
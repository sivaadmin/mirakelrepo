package com.macys.mirakl.dao;

import java.util.List;

import com.macys.mirakl.exception.MiraklRepositoryException;
import com.macys.mirakl.model.FacetMasterData;
import com.macys.mirakl.model.ImageMasterData;
import com.macys.mirakl.model.PdfMasterData;
import com.macys.mirakl.model.StellaMasterData;
import com.macys.mirakl.model.UpcMasterData;
import com.macys.mirakl.util.FilePrefix;

public interface ProductCreateDAO {

	List<String> findExistingMasterUpcs(List<String> upcs, FilePrefix filePrefix) throws MiraklRepositoryException;
	void batchInsertPdfMasterData(List<PdfMasterData> pdfData);
	void batchInsertStellaMasterData(List<StellaMasterData> stellaData);
	void batchInsertImageMasterData(List<ImageMasterData> imageData);
	void batchInsertFacetMasterData(List<FacetMasterData> facetData);
	void batchInsertUpcMasterData(List<UpcMasterData> upcData);

}

package com.macys.mirakl.service;

import static com.macys.mirakl.processor.FileProcessor.getFilePrefix;
import static com.macys.mirakl.util.OrchConstants.CREATED;
import static com.macys.mirakl.util.OrchConstants.PRODUCT_CREATE;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.macys.mirakl.exception.MiraklRepositoryException;
import com.macys.mirakl.model.FacetMasterData;
import com.macys.mirakl.model.ImageMasterData;
import com.macys.mirakl.model.MiraklData;
import com.macys.mirakl.model.PdfMasterData;
import com.macys.mirakl.model.StellaMasterData;
import com.macys.mirakl.model.UpcMasterData;
import com.macys.mirakl.util.FilePrefix;

@Transactional
@Service
public class ProductCreateService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProductCreateService.class);
    @Autowired
    private SQLService sql;


    public void processProductCreate(String fileName, List<MiraklData> prdTxns) throws MiraklRepositoryException {
    	
    	FilePrefix filePrefix = getFilePrefix(fileName);

        List<PdfMasterData> pdfData = new ArrayList<>();
        List<StellaMasterData> stellaData = new ArrayList<>();
        List<ImageMasterData> imageData = new ArrayList<>();
        List<FacetMasterData> facetData = new ArrayList<>();
        List<UpcMasterData> upcData = new ArrayList<>();
        
        List<String> upcs = prdTxns.stream().map(val -> val.getUpcId()).collect(Collectors.toList());
        List<String> existingUpcs = sql.findExistingMasterUpcs(upcs, filePrefix);
		if (!CollectionUtils.isEmpty(existingUpcs)) {
			LOGGER.info("Number of UPCs existing in DB already:{}, List of existing UPCs:{}", existingUpcs.size(), existingUpcs);
			existingUpcs.forEach(upc -> {
				prdTxns.removeIf(val -> val.getUpcId().equals(upc));
			});
		}
		LOGGER.info("Number of UPCs to be inserted into DB:{}", prdTxns.size());
		
		if(prdTxns.size()>0) {
			for (MiraklData miraklData : prdTxns) {

	            PdfMasterData pdfInfo = new PdfMasterData(miraklData.getFileName(), miraklData.getUpcId(),
	                    miraklData.getOpDiv(), miraklData.getProductType(), miraklData.getPdfData());
	            pdfData.add(pdfInfo);
	            StellaMasterData stellaInfo = new StellaMasterData(miraklData.getFileName(), miraklData.getUpcId(),
	                    miraklData.getOpDiv(),
	                    miraklData.getProductType(), miraklData.getStellaData());
	            stellaData.add(stellaInfo);
	            ImageMasterData imageInfo = new ImageMasterData(miraklData.getFileName(), miraklData.getUpcId(),
	                    miraklData.getOpDiv(), miraklData.getProductType(), miraklData.getImagesData());
	            imageData.add(imageInfo);
	            FacetMasterData facetInfo =new FacetMasterData(miraklData.getFileName(), miraklData.getUpcId(),
	                    miraklData.getOpDiv(), miraklData.getProductType(), miraklData.getDept(), miraklData.getVendor(), 
	                    miraklData.getPid(), miraklData.getNrfColorCode(),miraklData.getFacetData());
	            facetData.add(facetInfo);
	            UpcMasterData upcInfo = new UpcMasterData(miraklData.getUpcId(), miraklData.getOpDiv(), miraklData.getProductType(),
	            		miraklData.getPid(), miraklData.getNrfColorCode(), miraklData.getNrfSizeCode(),miraklData.getMsrp(),
	                    miraklData.getDept(), miraklData.getVendor(), miraklData.getTaxCode(), null, false, false);
	            upcData.add(upcInfo);
	        }

	        LOGGER.info("Inserting to Master Tables & Audit Tables Start");
	        sql.batchInsertPdfMasterData(pdfData);
	        sql.batchInsertPdfAuditData(PRODUCT_CREATE,CREATED,null,pdfData);
	        sql.batchInsertStellaMasterData(stellaData);
	        sql.batchInsertStellaAuditData(PRODUCT_CREATE,CREATED,null,stellaData);
	        sql.batchInsertImageMasterData(imageData);
	        sql.batchInsertImageAuditData(PRODUCT_CREATE,CREATED,null,imageData);
	        sql.batchInsertFacetMasterData(facetData);
	        sql.batchInsertFacetAuditData(PRODUCT_CREATE,CREATED,null,facetData);
	        sql.batchInsertUpcMasterData(upcData);
	        LOGGER.info("Inserting to Master Tables & Audit Tables End");
		} else {
			LOGGER.info("ProductData list size is 0, no records to be inserted");
		}

    }
}

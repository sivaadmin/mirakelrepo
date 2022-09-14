package com.macys.mirakl.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MiraklData {

	private String fileName;
	private String upcId;
	private String opDiv;
	private String productType;
	private String taxCode;
	private String nrfSizeCode;
	private String msrp;
	private String dept;
	private String vendor;
	private String pid;
	private String nrfColorCode;
	private String pdfData;
	private String stellaData;
	private String imagesData;
	private String facetData;

	public MiraklData(String fileName,  String upcId, String opDiv, String productType,String taxCode, String nrfSizeCode, String msrp,
					  String pdfData, String stellaData, String imagesData) {
		super();
		this.fileName = fileName;
		this.upcId = upcId;
		this.opDiv = opDiv;
		this.productType = productType;
		this.taxCode=taxCode;
		this.nrfSizeCode=nrfSizeCode;
		this.msrp=msrp;
		this.pdfData = pdfData;
		this.stellaData = stellaData;
		this.imagesData = imagesData;
	}

}

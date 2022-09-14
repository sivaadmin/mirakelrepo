package com.macys.mirakl.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpcMasterData {
	private String upcId;
	private String opDiv;
	private String productType;
	private String pid;
	private String nrfColorCode;
	private String nrfSizeCode;
	private String msrp;
	private String dept;
	private String vendor;
	private String taxCode;
	private String offerId;
	private boolean mainImgFlag;
	private boolean buyerApprovedFlag;
}
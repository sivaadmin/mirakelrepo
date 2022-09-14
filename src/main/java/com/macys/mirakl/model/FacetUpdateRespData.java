package com.macys.mirakl.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FacetUpdateRespData {
	
	private String upcId;
	private String opDiv;
	private String productType;
	private String dept;
	private String vendor;
	private String pid;
	private String nrfColorCode;
	private String status;
	private String message;
	private String attributes;
	private String fileNameJson;
	private String facetData;
}

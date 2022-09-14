package com.macys.mirakl.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FacetMasterData {
	private String fileName;
	private String upcId;
	private String opDiv;
	private String productType;
	private String dept;
	private String vendor;
	private String pid;
	private String nrfcolor;
	private String facetData;

}

package com.macys.mirakl.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateResponseData {
    private String productSku;
    private String upc;
    private String opDiv;
    private String pid;
    private String nrfColorCode;
	private String nrfSizeCode;
	private String msrp;
	private String taxCode;	
}
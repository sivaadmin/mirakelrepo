package com.macys.mirakl.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PdfRespData {
	private String fileName;
	private String upcId;
	private String opDiv;
	private String productType;
	private String pdfData;
	private String status;
	private String errorDesc;
}
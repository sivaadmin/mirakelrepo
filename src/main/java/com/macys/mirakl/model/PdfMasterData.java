package com.macys.mirakl.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PdfMasterData {
	private String fileName;
	private String upcId;
	private String opDiv;
	private String productType;
	private String pdfData;

}

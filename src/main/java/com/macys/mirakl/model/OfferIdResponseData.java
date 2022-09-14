package com.macys.mirakl.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OfferIdResponseData {
	
	private String upcId;
	private String opDiv;
	private String offerId;
	private String status;
	private String message;
	private String fileNameJson;

}

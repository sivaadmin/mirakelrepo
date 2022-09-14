package com.macys.mirakl.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StellaMasterData {
	private String fileName;
	private String upcId;
	private String opDiv;
	private String productType;
	private String stellaData;	
}
package com.macys.mirakl.model;

import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductDataPDF {
	
	//PDF_Data	
	@SerializedName("BrandName")
	private String brandName;	

}

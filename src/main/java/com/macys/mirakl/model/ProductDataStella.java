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
public class ProductDataStella {
	
	//Stella_Data	
	@SerializedName("CustomerFacingPidDescription")
	private String customerFacingPidDescription;
	
	@SerializedName("CustomerFacingColDescription")
	private String customerFacingColDescription;	
	
	@SerializedName("Long Description")
	private String longDescription;
	
	@SerializedName("FabricCare")
	private String fabricCare;
	
	@SerializedName("FabricContent")
	private String fabricContent;
	
	@SerializedName("CountryOfOrigin")
	private String countryOfOrigin;
	
	@SerializedName("Warranty")
	private String warranty;
	
	@SerializedName("InternationalShipping")
	private String internationalShipping;
	
	@SerializedName("LegalWarnings")
	private String legalWarnings;
	
	@SerializedName("F&BBullet1")
	private String fAndBBullet1;
	
	@SerializedName("F&BBullet2")
	private String fAndBBullet2;
	
	@SerializedName("F&BBullet3")
	private String fAndBBullet3;
	
	@SerializedName("F&BBullet4")
	private String fAndBBullet4;
	
	@SerializedName("F&BBullet5")
	private String fAndBBullet5;
	
	@SerializedName("F&BBullet20")
	private String fAndBBullet20;
	
	@SerializedName("ProductDimensions1")
	private String productDimensions1;
	
	@SerializedName("ProductDimensions2")
	private String productDimensions2;
	
	@SerializedName("ProductDimensions3")
	private String productDimensions3;
	
	@SerializedName("ShippingDimensionsLength1")
	private String shippingDimensionsLength1;
	
	@SerializedName("ShippingDimensionsHeight1")
	private String shippingDimensionsHeight1;
	
	@SerializedName("ShippingDimensionsWidth1")
	private String shippingDimensionsWidth1;
	
	@SerializedName("ShippingDimensionsWeight")
	private String shippingDimensionsWeight;	

}

package com.macys.mirakl.model;

import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AttributeData {

	@SerializedName("AttributeName")
	String attributeName;
	@SerializedName("AttributeValue")
	String attributeValue;

}

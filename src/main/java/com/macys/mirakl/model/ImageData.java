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
public class ImageData {
	
	@SerializedName("ImageID")
	private String imageId;

	@SerializedName("ImageType")
	private String imageType;
	
	@SerializedName("ImageURL")
	private String imageUrl;

}

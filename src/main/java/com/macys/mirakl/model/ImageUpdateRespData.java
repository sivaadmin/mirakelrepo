package com.macys.mirakl.model;

import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ImageUpdateRespData {
	@JsonIgnore
	private String processingStage;
	@JsonIgnore
	private String inputFileName;
	@JsonProperty("UPCID")
	private String upcId;
	@JsonProperty("OpDiv")
	private String opDiv;
	@JsonIgnore
	private String deltaFileName;
	@JsonIgnore
	private String imgId;
	@JsonProperty("ImageUrl")
	private String imgUrl;
	@JsonIgnore
	private String imgType;
	@JsonIgnore
	private int iasRespReceivedFlag;
	@JsonProperty("ImageFileName")
	private String imgFileName;
	@JsonProperty("Status")
	private String statusCode;
	@JsonProperty("Message")
	private String statusMessage;
	@JsonIgnore
	private int ilRespSentFlag;
	@JsonIgnore
	private String respFileName;
	@JsonIgnore
	private Timestamp createdTs;
	@JsonIgnore
	private Timestamp updatedTs;
	
}
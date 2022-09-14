package com.macys.mirakl.util;

import static com.macys.mirakl.util.OrchConstants.IMAGE_ID;
import static com.macys.mirakl.util.OrchConstants.IMAGE_TYPE;
import static com.macys.mirakl.util.OrchConstants.IMAGE_URL;
import static com.macys.mirakl.util.OrchConstants.SWATCH_IMAGE;
import static com.macys.mirakl.util.OrchConstants.SW_IMAGE;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.MapDifference;
import com.google.common.collect.MapDifference.ValueDifference;
import com.google.common.collect.Maps;
import com.google.gson.annotations.SerializedName;
import com.macys.mirakl.model.AttributeData;
import com.macys.mirakl.model.ImageData;

public final  class OrchUtil {
	private static final Logger LOGGER = LoggerFactory.getLogger(OrchUtil.class);
	
	public static Map<Object, Object> findJsonDifference(Object masterDataJson, Object updateReqJson)
			throws IllegalAccessException {

		Map<Object, Object> changedProperties = new HashMap<Object, Object>();
		for (Field field : masterDataJson.getClass().getDeclaredFields()) {
			// You might want to set modifier to public first (if it is not public yet)
			field.setAccessible(true);
			Object masterValue = field.get(masterDataJson);
			Object newValue = field.get(updateReqJson);
			if (masterValue != null && newValue != null) {
				if (!Objects.equals(masterValue, newValue)) {
					String annotationValue = field.getAnnotation(SerializedName.class).value();
					changedProperties.put(annotationValue, newValue.toString());
				}
			}
		}
		return changedProperties;
	}

	public static List<ImageData> findDifferenceImagesList(JSONArray jsonArrayImagesMaster,
			JSONArray jsonArrayImagesIncoming) throws IllegalAccessException {
		Map<String, ImageData> imageDataMasterMap = new HashMap<String, ImageData>();
		Map<String, ImageData> imageDataIncomingMap = new HashMap<String, ImageData>();
		MapDifference<String, ImageData> diffMap = null;
		Map<String, ValueDifference<ImageData>> entriesDifferingMap = null;
		Map<String, ImageData> entriesDifferingOnlyRightMap = null;
		List<ImageData> imgDataList = new ArrayList<>();

		for (int i = 0; i < jsonArrayImagesIncoming.length(); i++) {

			JSONObject images = jsonArrayImagesIncoming.getJSONObject(i);
			ImageData dataImgIncoming = new ImageData(String.valueOf(images.get(IMAGE_ID)),
					String.valueOf(images.get(IMAGE_TYPE)), String.valueOf(images.get(IMAGE_URL)));
			imageDataIncomingMap.put(String.valueOf(images.get(IMAGE_TYPE)), dataImgIncoming);

			for (int j = 0; j < jsonArrayImagesMaster.length(); j++) {
				JSONObject imagesMaster = jsonArrayImagesMaster.getJSONObject(j);

				ImageData dataImgMast = new ImageData(String.valueOf(imagesMaster.get(IMAGE_ID)),
						String.valueOf(imagesMaster.get(IMAGE_TYPE)), String.valueOf(imagesMaster.get(IMAGE_URL)));
				imageDataMasterMap.put(String.valueOf(imagesMaster.get(IMAGE_TYPE)), dataImgMast);

				diffMap = Maps.difference(imageDataMasterMap, imageDataIncomingMap);
				if (null != diffMap) {
					entriesDifferingMap = diffMap.entriesDiffering();
					entriesDifferingOnlyRightMap = diffMap.entriesOnlyOnRight();
				} else {
					LOGGER.error("ERROR IN FINDING DIFFERENCE FOR IMAGES");
				}

			}

		}

		entriesDifferingMap.entrySet().forEach(entry -> {
			imgDataList.add(entry.getValue().rightValue());
			LOGGER.info(entry.getKey() + " " + entry.getValue().rightValue());
		});
		// Changes to include New Image Addition functionality
		entriesDifferingOnlyRightMap.entrySet().forEach(entry -> {
			imgDataList.add(entry.getValue());
			LOGGER.info(entry.getKey() + " " + entry.getValue());
		});

		return imgDataList;

	}

	public static Map<Object, Object> findJsonDifferenceImages(List<ImageData> imgDataList) throws IllegalAccessException{
		Map<Object, Object> changedPropsMap = new HashMap<Object, Object>();
		for(ImageData imgData : imgDataList){
			if(SWATCH_IMAGE.equalsIgnoreCase(imgData.getImageType())) {
				changedPropsMap.put(SW_IMAGE, imgData.getImageUrl());
			} else {
				changedPropsMap.put(imgData.getImageType(), imgData.getImageUrl());
			}
		}
		return changedPropsMap;
	}

	/**
	 * This method compare master data and incoming data for facet and return the changed data
	 * @param jsonArrayFacetMaster
	 * @param jsonArrayFacetIncoming
	 * @return
	 */
	public static List<AttributeData> findJsonDifferenceFacet(JSONArray jsonArrayFacetMaster,
															  JSONArray jsonArrayFacetIncoming) {
		Map<String, AttributeData> facetDataIncomingMap = getAttributes(jsonArrayFacetIncoming);
		Map<String, AttributeData> facetDataMasterMap = getAttributes(jsonArrayFacetMaster);

		Map<String, AttributeData> changedPropMap = getChangedPropMap(facetDataMasterMap, facetDataIncomingMap);
		return getAttributeList(changedPropMap);
	}

	public static Map<String, AttributeData> getAttributes(JSONArray jsonArrayFacet) {
		Map<String, AttributeData> facetDataMap = new HashMap<>();
		for (var facetData = 0; facetData < jsonArrayFacet.length(); facetData++) {
			var facetItem = jsonArrayFacet.getJSONObject(facetData);

			AttributeData dataFacetMaster = AttributeData.builder()
					.attributeName(facetItem.getString(OrchConstants.ATTRIBUTE_NAME))
					.attributeValue(facetItem.getString(OrchConstants.ATTRIBUTE_VALUE))
					.build();
			facetDataMap.put(dataFacetMaster.getAttributeName(), dataFacetMaster);
		}
		return facetDataMap;
	}


	/**
	 * This method get the difference between incoming data and master data
	 * @param facetDataMasterMap
	 * @param facetDataIncomingMap
	 * @return changedPropsMap
	 *
	 */
	private static Map<String , AttributeData> getChangedPropMap(Map<String, AttributeData> facetDataMasterMap,
																 Map<String, AttributeData> facetDataIncomingMap) {
		MapDifference<String, AttributeData> diffMap = null;
		Map<String, ValueDifference<AttributeData>> entriesDifferingMap = null;
		Map<String, AttributeData> entriesDifferingOnlyRightMap = null;
		Map<String, AttributeData> entriesDifferingOnlyLeftMap = null;
		Map<String, AttributeData> changedPropsMap = new HashMap<>();

		diffMap = Maps.difference(facetDataMasterMap, facetDataIncomingMap);
		if (null != diffMap) {
			entriesDifferingMap = diffMap.entriesDiffering();
			entriesDifferingOnlyRightMap = diffMap.entriesOnlyOnRight();
			entriesDifferingOnlyLeftMap = diffMap.entriesOnlyOnLeft();
		}

		if (null != entriesDifferingMap && !entriesDifferingMap.isEmpty()) {
			entriesDifferingMap.entrySet().forEach(entry ->
					changedPropsMap.put(entry.getKey(), entry.getValue().rightValue()));
		}

		if (null != entriesDifferingOnlyRightMap && !entriesDifferingOnlyRightMap.isEmpty()) {
			entriesDifferingOnlyRightMap.entrySet().forEach(entry ->
					changedPropsMap.put(entry.getKey(), entry.getValue()));
		}

		if (!entriesDifferingOnlyLeftMap.isEmpty()) {
			entriesDifferingOnlyLeftMap.forEach((key, rightAttributeData) -> {
				rightAttributeData.setAttributeValue("");
				changedPropsMap.put(key, rightAttributeData);
			});
		}

		return changedPropsMap;
	}

	private static List<AttributeData> getAttributeList(Map<String, AttributeData> changedPropMap) {
		var attributeDataList = new ArrayList<AttributeData>();
		if (null != changedPropMap && !changedPropMap.isEmpty()) {
			changedPropMap.entrySet().forEach(entry -> attributeDataList.add(entry.getValue()));
		}
		return attributeDataList;
	}

	public static String getCorrelationId() {
		return UUID.randomUUID().toString();
	}
	
	public static boolean validateProductType(String productTypeIncoming, String productTypeMaster) {
		return StringUtils.isNotBlank(productTypeMaster) && StringUtils.isNotBlank(productTypeIncoming)
						&& productTypeMaster.equalsIgnoreCase(productTypeIncoming);
	}
	
	public static boolean validateVendorNumber(String vendorNumberIncoming) {
		return StringUtils.isNotBlank(vendorNumberIncoming)
				&& Pattern.matches("[1-9]|[1-9](\\d{1,2})", vendorNumberIncoming);
	}

}

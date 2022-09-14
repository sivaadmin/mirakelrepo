package com.macys.mirakl.util;

import static com.macys.mirakl.util.OrchUtil.findJsonDifferenceImages;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import org.json.JSONArray;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.macys.mirakl.model.AttributeData;
import com.macys.mirakl.model.ImageData;
import com.macys.mirakl.model.ProductDataStella;


@ExtendWith(MockitoExtension.class)
public class OrchUtilTest {
	
	@Test
	public void testDeltaStellaWhenValueChanges() throws NullPointerException, IllegalAccessException {
		
		//given
		ProductDataStella stellaDataMaster = ProductDataStella.builder()
				.customerFacingPidDescription("Name")
				.customerFacingColDescription("Open Brown")
				.longDescription("Name")
				.fabricCare("Machine washable")
				.fabricContent("Name")
				.countryOfOrigin("Made in USA")
				.warranty("N")
				.internationalShipping("N")
				.legalWarnings("LegalWarnings")
				.fAndBBullet1("Name")
				.fAndBBullet2("Name")
				.fAndBBullet3("Name")
				.fAndBBullet20("Men|Unisex")
				.productDimensions1("Name")
				.productDimensions3("Name")
				.build();
		
		ProductDataStella stellaDataIncoming = ProductDataStella.builder()
				.customerFacingPidDescription("Name_1")
				.customerFacingColDescription("Open Brown_1")
				.longDescription("Name_1")
				.fabricCare("Machine washable_1")
				.countryOfOrigin("Name_1")
				.countryOfOrigin("Made in USA_1")
				.warranty("y")
				.internationalShipping("N")
				.legalWarnings("LegalWarnings")
				.fAndBBullet1("Name")
				.fAndBBullet2("Name")
				.fAndBBullet3("Name")
				.fAndBBullet20("Men|Unisex_1")
				.productDimensions1("Name")
				.productDimensions3("Name")
				.build();
		Map<Object, Object> expectedStellaDataMap = getExpectedDeltaListWhenAnNewAttributeAdded();
		
		//when
		Map<Object, Object> actualStellaDataMap = OrchUtil.findJsonDifference(stellaDataMaster, stellaDataIncoming);

		//then
		assertEquals(expectedStellaDataMap.size(), actualStellaDataMap.size(),"the size of actualAttributeDataList should be equal to " + expectedStellaDataMap.size());
		
		expectedStellaDataMap.forEach((k, v) -> {
			assertTrue(actualStellaDataMap.containsKey(k),"actualStellaData should contains " + k );
			assertEquals( v, actualStellaDataMap.get(k),"value should be equal to " + v + "for key:" + k);
			
		});
		
	}
	
	@Test
	public void testDeltaStellaWhenNoValueChanges() throws NullPointerException, IllegalAccessException {
		
		//given
		ProductDataStella stellaDataMaster = ProductDataStella.builder()
				.customerFacingPidDescription("Name")
				.customerFacingColDescription("Open Brown")
				.longDescription("Name")
				.fabricCare("Machine washable")
				.fabricContent("Name")
				.countryOfOrigin("Made in USA")
				.warranty("N")
				.internationalShipping("N")
				.legalWarnings("LegalWarnings")
				.fAndBBullet1("Name")
				.fAndBBullet2("Name")
				.fAndBBullet3("Name")
				.fAndBBullet20("Men|Unisex")
				.productDimensions1("Name")
				.productDimensions3("Name")
				.build();
		
		ProductDataStella stellaDataIncoming = ProductDataStella.builder()
				.customerFacingPidDescription("Name")
				.customerFacingColDescription("Open Brown")
				.longDescription("Name")
				.fabricCare("Machine washable")
				.fabricContent("Name")
				.countryOfOrigin("Made in USA")
				.warranty("N")
				.internationalShipping("N")
				.legalWarnings("LegalWarnings")
				.fAndBBullet1("Name")
				.fAndBBullet2("Name")
				.fAndBBullet3("Name")
				.fAndBBullet20("Men|Unisex")
				.productDimensions1("Name")
				.productDimensions3("Name")
				.build();

		//when
		Map<Object, Object> actualStellaDataMap = OrchUtil.findJsonDifference(stellaDataMaster, stellaDataIncoming);

		//then
		assertEquals( 0, actualStellaDataMap.size(),"the size of actualStellaeDataList should be equal to zero" );
	}
	
	@Test
	public void testDeltaImageWhenImageChanges() throws IllegalAccessException, JSONException {
		
		//given
		var imageMaster = "[{\"ImageID\":\"f66b063b414842ba838afd7348d40120a\",\"ImageType\":\"mainImage\",\"ImageURL\":\"https://media-dev-eu-2.mirakl.net/SOURCE/f66b063b414842ba838afd7348d40120a\"},{\"ImageID\":\"f66b063b414842ba838afd7348d40121\",\"ImageType\":\"image1\",\"ImageURL\":\"https://media-dev-eu-2.mirakl.net/SOURCE/f66b063b414842ba838afd7348d40121\"}]"; 
		var imageIncoming = "[{\"ImageID\":\"f66b063b414842ba838afd7348d40120\",\"ImageType\":\"mainImage\",\"ImageURL\":\"https://media-dev-eu-2.mirakl.net/SOURCE/f66b063b414842ba838afd7348d40120\"},{\"ImageID\":\"f66b063b414842ba838afd7348d40121\",\"ImageType\":\"image1\",\"ImageURL\":\"https://media-dev-eu-2.mirakl.net/SOURCE/f66b063b414842ba838afd7348d40121\"}]";
		Map<Object, Object> expectedImageDataMap = getExpectedDataListWhenImageChanged();
		
		//when
		List<ImageData> imgDataList=OrchUtil.findDifferenceImagesList(new JSONArray(imageMaster), new JSONArray(imageIncoming));
		Map<Object, Object> actualImageDataMap =findJsonDifferenceImages(imgDataList); 
		//then
		assertEquals(expectedImageDataMap.size(), actualImageDataMap.size(),"the size of actualImageDataList should be equal to " + expectedImageDataMap.size());
		
		expectedImageDataMap.forEach((k, v) -> {
			assertTrue(expectedImageDataMap.containsKey(k),"actualStellaData should contains " + k );
			assertEquals( v, expectedImageDataMap.get(k),"value should be equal to " + v + "for key:" + k);
			
		});
    }
	
	@Test
	public void testDeltaImageWhenImageChanges_AditionalImage() throws IllegalAccessException, JSONException {
		
		//given
		var imageMaster = "[{\"ImageID\":\"f66b063b414842ba838afd7348d40121\",\"ImageType\":\"image1\",\"ImageURL\":\"https://media-dev-eu-2.mirakl.net/SOURCE/f66b063b414842ba838afd7348d40121\"}]"; 
		var imageIncoming = "[{\"ImageID\":\"f66b063b414842ba838afd7348d40120\",\"ImageType\":\"mainImage\",\"ImageURL\":\"https://media-dev-eu-2.mirakl.net/SOURCE/f66b063b414842ba838afd7348d40120\"},{\"ImageID\":\"f66b063b414842ba838afd7348d40121\",\"ImageType\":\"image1\",\"ImageURL\":\"https://media-dev-eu-2.mirakl.net/SOURCE/f66b063b414842ba838afd7348d40121\"}]";
		Map<Object, Object> expectedImageDataMap = getExpectedDataListWhenImageChanged();
		
		//when
		List<ImageData> imgDataList=OrchUtil.findDifferenceImagesList(new JSONArray(imageMaster), new JSONArray(imageIncoming));
		Map<Object, Object> actualImageDataMap =findJsonDifferenceImages(imgDataList); 
		//then
		assertEquals(expectedImageDataMap.size(), actualImageDataMap.size(),"the size of actualImageDataList should be equal to " + expectedImageDataMap.size());
		
		expectedImageDataMap.forEach((k, v) -> {
			assertTrue(expectedImageDataMap.containsKey(k),"actualImageData should contains " + k );
			assertEquals( v, expectedImageDataMap.get(k),"value should be equal to " + v + "for key:" + k);
			
		});
    }
	
	
	@Test
	public void testDeltaImageWhenNoImageChanges() throws IllegalAccessException, JSONException {
		
		//given
		var imageMaster = "[{\"ImageID\":\"f66b063b414842ba838afd7348d40120a\",\"ImageType\":\"mainImage\",\"ImageURL\":\"https://media-dev-eu-2.mirakl.net/SOURCE/f66b063b414842ba838afd7348d40120a\"},{\"ImageID\":\"f66b063b414842ba838afd7348d40121\",\"ImageType\":\"image1\",\"ImageURL\":\"https://media-dev-eu-2.mirakl.net/SOURCE/f66b063b414842ba838afd7348d40121\"}]"; 
		var imageIncoming = "[{\"ImageID\":\"f66b063b414842ba838afd7348d40120a\",\"ImageType\":\"mainImage\",\"ImageURL\":\"https://media-dev-eu-2.mirakl.net/SOURCE/f66b063b414842ba838afd7348d40120a\"},{\"ImageID\":\"f66b063b414842ba838afd7348d40121\",\"ImageType\":\"image1\",\"ImageURL\":\"https://media-dev-eu-2.mirakl.net/SOURCE/f66b063b414842ba838afd7348d40121\"}]"; 

		//when
		List<ImageData> imgDataList=OrchUtil.findDifferenceImagesList(new JSONArray(imageMaster), new JSONArray(imageIncoming));
		Map<Object, Object> actualImageDataMap =findJsonDifferenceImages(imgDataList); 
		//then
		assertEquals( 0, actualImageDataMap.size(),"the size of actualStellaeDataList should be equal to zero" );

	}


	@Test
	public void testDeltaFacetWhenAnAttributeValueChanges() throws NullPointerException {
		
		//given
		var facetMaster = "[{\"AttributeName\":\"Occasion\",\"AttributeValue\":\"Active\"},{\"AttributeName\":\"PetAccessories\",\"AttributeValue\":\"Pet Bowls\"}]"; 
		var facetIncoming = "[{\"AttributeName\":\"Occasion\",\"AttributeValue\":\"Not Active\"},{\"AttributeName\":\"PetAccessories\",\"AttributeValue\":\"Pet Bowls\"}]";
		List<AttributeData> expectedAttributeDataList = getExpectedDataListWhenAnNewAttributeAdded();
		
		//when
		List<AttributeData> actualAttributeDataList = OrchUtil.findJsonDifferenceFacet(new JSONArray(facetMaster), new JSONArray(facetIncoming));
		
		//then
		assertEquals(expectedAttributeDataList.size(), actualAttributeDataList.size(),"the size of actualAttributeDataList should be equal to " + expectedAttributeDataList.size());
		IntStream.range(0, actualAttributeDataList.size()).forEach(
			i -> {
				String actualAttributeName  = actualAttributeDataList.get(i).getAttributeName();
				String actualAttributeValue  = actualAttributeDataList.get(i).getAttributeValue();
				
				AttributeData expectedAttributeData = expectedAttributeDataList.stream()
						  .filter(attributeData -> actualAttributeName.equals(attributeData.getAttributeName()))
						  .findFirst()
						  .orElseThrow(() -> new NullPointerException("attribute Name is not found : " + actualAttributeName ));;	
				
				assertEquals(expectedAttributeData.getAttributeName(),actualAttributeName,"expected attribute name should be equal to " + actualAttributeName );
				assertEquals(expectedAttributeData.getAttributeValue(),actualAttributeValue,"expected attribute value should be equal to " + actualAttributeValue);

			});
    }
	
	@Test
	public void testDeltaFacetWhenNewAttributeValueGetsAdded() throws NullPointerException {
		
		//given
		var facetMaster = "[{\"AttributeName\":\"Occasion\",\"AttributeValue\":\"Active\"},{\"AttributeName\":\"Pet Accessories\",\"AttributeValue\":\"Pet Bowls\"}]"; 
		var facetIncoming = "[{\"AttributeName\":\"Occasion\",\"AttributeValue\":\"Not Active\"},{\"AttributeName\":\"Pet Accessories\",\"AttributeValue\":\"Pet Bowls\"},{\"AttributeName\":\"New Attribute\",\"AttributeValue\":\"New Value\"}]";
		List<AttributeData> expectedAttributeDataList = getExpectedDataListWhenAnAttributeValueChange();

		//when
		List<AttributeData> actualAttributeDataList = OrchUtil.findJsonDifferenceFacet(new JSONArray(facetMaster), new JSONArray(facetIncoming));

		//then
		assertEquals(expectedAttributeDataList.size(), actualAttributeDataList.size(),"the size of actualAttributeDataList should be equal to " + expectedAttributeDataList.size());
		IntStream.range(0, actualAttributeDataList.size()).forEach(
			i -> {
				String actualAttributeName  = actualAttributeDataList.get(i).getAttributeName();
				String actualAttributeValue  = actualAttributeDataList.get(i).getAttributeValue();
				
				AttributeData expectedAttributeData = expectedAttributeDataList.stream()
						  .filter(attributeData -> actualAttributeName.equals(attributeData.getAttributeName()))
						  .findFirst()
						  .orElseThrow(() -> new NullPointerException("attribute Name is not found : " + actualAttributeName ));;	
				
				assertEquals(expectedAttributeData.getAttributeName(),actualAttributeName,"expected attribute name should be equal to " + actualAttributeName );
				assertEquals(expectedAttributeData.getAttributeValue(),actualAttributeValue,"expected attribute value should be equal to " + actualAttributeValue);
	
			});
    }

	@Test
	public void testDeltaFacetWheExistingAttributeValueRemoved() throws NullPointerException {
		
		//given
		var facetMaster = "[{\"AttributeName\":\"Occasion\",\"AttributeValue\":\"Active\"},{\"AttributeName\":\"PetAccessories\",\"AttributeValue\":\"PetBowls\"},{\"AttributeName\":\"Attribute3\",\"AttributeValue\":\"Value3\"}]"; 
		var facetIncoming = "[{\"AttributeName\":\"Occasion\",\"AttributeValue\":\"Not Active\"},{\"AttributeName\":\"PetAccessories\",\"AttributeValue\":\"PetBowls\"},{\"AttributeName\":\"Attribute3\",\"AttributeValue\":\"\"}]";
		List<AttributeData> expectedAttributeDataList = getExpectedDataListWhenAnAttributeValueRemoved();

		//when
		List<AttributeData> actualAttributeDataList = OrchUtil.findJsonDifferenceFacet(new JSONArray(facetMaster), new JSONArray(facetIncoming));
		
		//then
		assertEquals(expectedAttributeDataList.size(), actualAttributeDataList.size(),"the size of actualAttributeDataList should be equal to " + expectedAttributeDataList.size());
		IntStream.range(0, actualAttributeDataList.size()).forEach(
			i -> {
				String actualAttributeName  = actualAttributeDataList.get(i).getAttributeName();
				String actualAttributeValue  = actualAttributeDataList.get(i).getAttributeValue();
				
				AttributeData expectedAttributeData = expectedAttributeDataList.stream()
						  .filter(attributeData -> actualAttributeName.equals(attributeData.getAttributeName()))
						  .findFirst()
						  .orElseThrow(() -> new NullPointerException("attribute Name is not found : " + actualAttributeName ));;	
				
				assertEquals(expectedAttributeData.getAttributeName(),actualAttributeName,"expected attribute name should be equal to " + actualAttributeName );
				assertEquals( expectedAttributeData.getAttributeValue(),actualAttributeValue,"expected attribute value should be equal to " + actualAttributeValue);
	
			});
    }
	
	@Test
	public void testDeltaFacetWhenExistingAttributeRemoved() throws NullPointerException {
		
		//given
		var facetMaster = "[{\"AttributeName\":\"Occasion\",\"AttributeValue\":\"Active\"},{\"AttributeName\":\"Pet Accessories\",\"AttributeValue\":\"Pet Bowls\"},{\"AttributeName\":\"Attribute3\",\"AttributeValue\":\"Value3\"}]"; 
		var facetIncoming = "[{\"AttributeName\":\"Occasion\",\"AttributeValue\":\"Not Active\"},{\"AttributeName\":\"Pet Accessories\",\"AttributeValue\":\"Pet Bowls\"}]";
		List<AttributeData> expectedAttributeDataList = getExpectedDataListWhenAnAttributeRemoved();

		//when
		List<AttributeData> actualAttributeDataList = OrchUtil.findJsonDifferenceFacet(new JSONArray(facetMaster), new JSONArray(facetIncoming));

		//then
		assertEquals(expectedAttributeDataList.size(), actualAttributeDataList.size(),"the size of actualAttributeDataList should be equal to " + expectedAttributeDataList.size());
		IntStream.range(0, actualAttributeDataList.size()).forEach(
			i -> {
				String actualAttributeName  = actualAttributeDataList.get(i).getAttributeName();
				String actualAttributeValue  = actualAttributeDataList.get(i).getAttributeValue();
				
				AttributeData expectedAttributeData = expectedAttributeDataList.stream()
						  .filter(attributeData -> actualAttributeName.equals(attributeData.getAttributeName()))
						  .findFirst()
						  .orElseThrow(() -> new NullPointerException("attribute Name is not found : " + actualAttributeName ));;	
				
				assertEquals(expectedAttributeData.getAttributeName(),actualAttributeName,"expected attribute name should be equal to " + actualAttributeName );
				assertEquals(expectedAttributeData.getAttributeValue(),actualAttributeValue,"expected attribute value should be equal to " + actualAttributeValue);
	
			});
    }
	
	@Test
	public void testDeltaFacetWhenNoChangeInAttributes() throws NullPointerException {
		
		//given
		var facetMaster = "[{\"AttributeName\":\"UPCID\",\"AttributeValue\":\"12345678902\"},{\"AttributeName\":\"Color\",\"AttributeValue\":\"Green\"},{\"AttributeName\":\"ItemColor\",\"AttributeValue\":\"Red\"}]";  
		var facetIncoming = "[{\"AttributeName\":\"UPCID\",\"AttributeValue\":\"12345678902\"},{\"AttributeName\":\"Color\",\"AttributeValue\":\"Green\"},{\"AttributeName\":\"ItemColor\",\"AttributeValue\":\"Red\"}]"; 
		
		//when
		List<AttributeData> actualAttributeDataList = OrchUtil.findJsonDifferenceFacet(new JSONArray(facetMaster), new JSONArray(facetIncoming));
		
		//then
		assertEquals( 0, actualAttributeDataList.size(),"the size of actualAttributeDataList should be equal to zero" );
 
    }
	
	@Test
	public void testDeltaFacetWhenExistingRecordDoesNotHaveFacetAttributesButIncomingRecordHave() throws NullPointerException {
		
		//given
		var facetMaster = "[]"; 
		var facetIncoming = "[{\"AttributeName\":\"Occasion\",\"AttributeValue\":\"Active\"},{\"AttributeName\":\"Pet Accessories\",\"AttributeValue\":\"Pet Bowls\"},{\"AttributeName\":\"Attribute3\",\"AttributeValue\":\"Value3\"}]"; 
		List<AttributeData> expectedAttributeDataList = getExpectedDataWhenExistingRecordDoesNotHaveFacetAttributesButIncomingRecordHave();
		
		//when
		List<AttributeData> actualAttributeDataList = OrchUtil.findJsonDifferenceFacet(new JSONArray(facetMaster), new JSONArray(facetIncoming));
		
		//then
		assertEquals(expectedAttributeDataList.size(), actualAttributeDataList.size(),"the size of actualAttributeDataList should be equal to " + expectedAttributeDataList.size());
		IntStream.range(0, actualAttributeDataList.size()).forEach(
			i -> {
				String actualAttributeName  = actualAttributeDataList.get(i).getAttributeName();
				String actualAttributeValue  = actualAttributeDataList.get(i).getAttributeValue();
				
				AttributeData expectedAttributeData = expectedAttributeDataList.stream()
						  .filter(attributeData -> actualAttributeName.equals(attributeData.getAttributeName()))
						  .findFirst()
						  .orElseThrow(() -> new NullPointerException("attribute Name is not found : " + actualAttributeName ));;	
				
				assertEquals(expectedAttributeData.getAttributeName(),actualAttributeName,"expected attribute name should be equal to " + actualAttributeName );
				assertEquals(expectedAttributeData.getAttributeValue(),actualAttributeValue,"expected attribute value should be equal to " + actualAttributeValue);
	
			});
    }
	
	private List<AttributeData> getExpectedDataListWhenAnAttributeValueChange() {
		 var expectedAttributeDataList = new ArrayList<AttributeData>();
		 
		 var attributeData1 = AttributeData.builder()
				 .attributeName("Occasion")
				 .attributeValue("Not Active")
				 .build();
		 
		 var attributeData2 =AttributeData.builder()
				 .attributeName("New Attribute")
				 .attributeValue("New Value")
				 .build();
		 
		 expectedAttributeDataList.add(attributeData1);
		 expectedAttributeDataList.add(attributeData2);
	 
	 return expectedAttributeDataList;
	}
	
	private List<AttributeData> getExpectedDataListWhenAnNewAttributeAdded() {
		 var expectedAttributeDataList = new ArrayList<AttributeData>();
		 
		 var attributeData1 = AttributeData.builder()
				 .attributeName("Occasion")
				 .attributeValue("Not Active")
				 .build();
		 
		 expectedAttributeDataList.add(attributeData1);
	 
	 return expectedAttributeDataList;
	}
	
	private List<AttributeData> getExpectedDataListWhenAnAttributeValueRemoved() {
		 var expectedAttributeDataList = new ArrayList<AttributeData>();
		 
		 var attributeData1 = AttributeData.builder()
				 .attributeName("Occasion")
				 .attributeValue("Not Active")
				 .build();
		 
		 var attributeData3 = AttributeData.builder()
				 .attributeName("Attribute3")
				 .attributeValue("")
				 .build();
		 
		 expectedAttributeDataList.add(attributeData1);
		 expectedAttributeDataList.add(attributeData3);
	 
	 return expectedAttributeDataList;
	}
	
	private List<AttributeData> getExpectedDataListWhenAnAttributeRemoved() {
		 var expectedAttributeDataList = new ArrayList<AttributeData>();
		 
		 var attributeData1 = AttributeData.builder()
				 .attributeName("Occasion")
				 .attributeValue("Not Active")
				 .build();

		var attributeData3 = AttributeData.builder()
				.attributeName("Attribute3")
				.attributeValue("")
				.build();

		expectedAttributeDataList.add(attributeData1);
		expectedAttributeDataList.add(attributeData3);


		return expectedAttributeDataList;
	}
	
	private List<AttributeData> getExpectedDataWhenExistingRecordDoesNotHaveFacetAttributesButIncomingRecordHave() {
		 var expectedAttributeDataList = new ArrayList<AttributeData>();
		 
		 var attributeData1 = AttributeData.builder()
				 .attributeName("Occasion")
				 .attributeValue("Active")
				 .build();
		 
		 var attributeData2 = AttributeData.builder()
				 .attributeName("Pet Accessories")
				 .attributeValue("Pet Bowls")
				 .build();
		 
		 var attributeData3 = AttributeData.builder()
				 .attributeName("Attribute3")
				 .attributeValue("Value3")
				 .build();
		 
		 expectedAttributeDataList.add(attributeData1);
		 expectedAttributeDataList.add(attributeData2);
		 expectedAttributeDataList.add(attributeData3);
	 
	 return expectedAttributeDataList;
	}
	
	private Map<Object, Object> getExpectedDeltaListWhenAnNewAttributeAdded() {
		Map<Object, Object> deltaStella = new HashMap<>();

		deltaStella.put("Long Description", "Name_1");
		deltaStella.put("CustomerFacingPidDescription", "Name_1");
		deltaStella.put("CustomerFacingColDescription", "Open Brown_1");
		deltaStella.put("Warranty", "y");
		deltaStella.put("F&BBullet20", "Men|Unisex_1");
		deltaStella.put("CountryOfOrigin", "Made in USA_1");
		deltaStella.put("FabricCare", "Machine washable_1");
		
		return deltaStella;
	}

	private Map<Object, Object> getExpectedDataListWhenImageChanged() {
		var expectedImageDataMap = new HashMap<Object, Object>();

		var imageData =  ImageData.builder()
				.imageId("f66b063b414842ba838afd7348d40120")
				.imageType("mainImage")
				.imageUrl("https://media-dev-eu-2.mirakl.net/SOURCE/f66b063b414842ba838afd7348d40120")
				.build();

		expectedImageDataMap.put("mainImage",imageData);

		return expectedImageDataMap;
	}
	
	@Test
    void testValidateProductType() {
        //Given
        String productTypeMaster = "Hand Tools";
        String productTypeIncoming1 = "Hand Tools";
        String productTypeIncoming2 = "Hand Tools Updated";
        String productTypeIncoming3 = "";
        String productTypeIncoming4 = "		";
        
        //When
        boolean isProductTypeValid1 = OrchUtil.validateProductType(productTypeIncoming1,productTypeMaster);
        boolean isProductTypeValid2 = OrchUtil.validateProductType(productTypeIncoming2,productTypeMaster);
        boolean isProductTypeValid3 = OrchUtil.validateProductType(productTypeIncoming3,productTypeMaster);
        boolean isProductTypeValid4 = OrchUtil.validateProductType(productTypeIncoming4,productTypeMaster);
       
        //Then
        assertEquals(isProductTypeValid1, true);
        assertEquals(isProductTypeValid2, false);
        assertEquals(isProductTypeValid3, false);
        assertEquals(isProductTypeValid4, false);       
    }
	
	@Test
    void testValidateVendorNumber() {
        //Given
        String vendorNumber1 = "939";
        String vendorNumber2 = "009";
        String vendorNumber3 = "0";
        String vendorNumber4 = "1";
        String vendorNumber5 = "11";
        String vendorNumber6 = "101";
        String vendorNumber7 = "1000";
        String vendorNumber8 = "";
        String vendorNumber9 = "		";
        
        //When
        boolean isVendorNumberValid1 = OrchUtil.validateVendorNumber(vendorNumber1);
        boolean isVendorNumberValid2 = OrchUtil.validateVendorNumber(vendorNumber2);
        boolean isVendorNumberValid3 = OrchUtil.validateVendorNumber(vendorNumber3);
        boolean isVendorNumberValid4 = OrchUtil.validateVendorNumber(vendorNumber4);
        boolean isVendorNumberValid5 = OrchUtil.validateVendorNumber(vendorNumber5);
        boolean isVendorNumberValid6 = OrchUtil.validateVendorNumber(vendorNumber6);
        boolean isVendorNumberValid7 = OrchUtil.validateVendorNumber(vendorNumber7);
        boolean isVendorNumberValid8 = OrchUtil.validateVendorNumber(vendorNumber8);
        boolean isVendorNumberValid9 = OrchUtil.validateVendorNumber(vendorNumber9);
       
        //Then
        assertEquals(isVendorNumberValid1, true);
        assertEquals(isVendorNumberValid2, false);
        assertEquals(isVendorNumberValid3, false);
        assertEquals(isVendorNumberValid4, true);
        assertEquals(isVendorNumberValid5, true);
        assertEquals(isVendorNumberValid6, true);
        assertEquals(isVendorNumberValid7, false);
        assertEquals(isVendorNumberValid8, false);
        assertEquals(isVendorNumberValid9, false);
    }

}

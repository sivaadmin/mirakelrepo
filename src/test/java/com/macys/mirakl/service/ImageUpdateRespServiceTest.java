package com.macys.mirakl.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.EmptyResultDataAccessException;

import com.macys.mirakl.exception.MiraklRepositoryException;
import com.macys.mirakl.model.ImageMasterData;
import com.macys.mirakl.model.ImageUpdateRespData;

@ExtendWith(MockitoExtension.class)
class ImageUpdateRespServiceTest {
	
	String imagePayload;
	ImageMasterData imageMasterData = null;
	ImageUpdateRespData imageUpdateRespData = new ImageUpdateRespData();
	ImageMasterData imageMasterDataUpdated = new ImageMasterData();
	ImageUpdateRespData imageUpdateRespDataFromDB = new ImageUpdateRespData();
	
	@InjectMocks
    private ImageUpdateRespService imageUpdateRespService;

    @Mock
    private SQLService sqlService;
    
	@BeforeEach
	void setUp() throws Exception {
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void testProcessImageRespMessage_Image1_Success() throws MiraklRepositoryException {
		// given
		imagePayload = "{\"imageFileName\":\"191019881004_1.jpg\",\"upcid\":\"191019881004\",\"opDiv\":\"12\",\"status\":\"200\",\"message\":\"Success\"}";
		imageMasterData = new ImageMasterData("MCOM_create_220523_191001.json", "191019881004", "12", "Watch",
				"[{\"ImageID\": \"f66b063b414842ba838afd7348d40120a\", \"ImageURL\": \"https://media-dev-eu-2.mirakl.net/SOURCE/f66b063b414842ba838afd7348d40120a\", \"ImageType\": \"mainImage\"}, {\"ImageID\": \"f66b063b414842ba838afd7348d40121a\", \"ImageURL\": \"https://media-dev-eu-2.mirakl.net/SOURCE/f66b063b414842ba838afd7348d40121a\", \"ImageType\": \"image1\"}, {\"ImageID\": \"f66b063b414842ba838afd7348d40122a\", \"ImageURL\": \"https://media-dev-eu-2.mirakl.net/SOURCE/f66b063b414842ba838afd7348d40122a\", \"ImageType\": \"image2\"}, {\"ImageID\": \"f66b063b414842ba838afd7348d40123a\", \"ImageURL\": \"https://media-dev-eu-2.mirakl.net/SOURCE/f66b063b414842ba838afd7348d40123a\", \"ImageType\": \"swatch\"}]");
		imageUpdateRespData = ImageUpdateRespData.builder().upcId("191019881004").opDiv("12").imgType("image1")
				.imgFileName("191019881004_1.jpg").statusCode("200").statusMessage("Success").iasRespReceivedFlag(0)
				.build();
		imageUpdateRespDataFromDB = ImageUpdateRespData.builder()
				.inputFileName("MCOM_product_update_220523_198802.json").imgId("f66b063b414842ba838afd7348d40121_upd1")
				.imgType("image1")
				.imgUrl("https://media-dev-eu-2.mirakl.net/SOURCE/f66b063b414842ba838afd7348d40121_upd1").build();
		imageMasterDataUpdated = new ImageMasterData("MCOM_product_update_220523_198802.json", "191019881004", "12", null,
				"[{\"ImageID\":\"f66b063b414842ba838afd7348d40120a\",\"ImageURL\":\"https://media-dev-eu-2.mirakl.net/SOURCE/f66b063b414842ba838afd7348d40120a\",\"ImageType\":\"mainImage\"},{\"ImageID\":\"f66b063b414842ba838afd7348d40123a\",\"ImageURL\":\"https://media-dev-eu-2.mirakl.net/SOURCE/f66b063b414842ba838afd7348d40123a\",\"ImageType\":\"swatch\"},{\"ImageID\":\"f66b063b414842ba838afd7348d40121_upd1\",\"ImageURL\":\"https://media-dev-eu-2.mirakl.net/SOURCE/f66b063b414842ba838afd7348d40121_upd1\",\"ImageType\":\"image1\"},{\"ImageID\":\"f66b063b414842ba838afd7348d40122a\",\"ImageURL\":\"https://media-dev-eu-2.mirakl.net/SOURCE/f66b063b414842ba838afd7348d40122a\",\"ImageType\":\"image2\"}]");

		when(sqlService.findImageMasterJSONByUpc(any(String.class), any(String.class))).thenReturn(imageMasterData);
		when(sqlService.fetchImagesRespMessage(imageUpdateRespData)).thenReturn(imageUpdateRespDataFromDB);
		doNothing().when(sqlService).updateImagesMasterJson(imageMasterDataUpdated);
		doNothing().when(sqlService).updateImagesRespMessage(imageUpdateRespData);
		doNothing().when(sqlService).insertImagesAuditData(any(String.class), any(), any(String.class), any(String.class), any(String.class));

		// when
		imageUpdateRespService.processImageRespMessage(imagePayload);

		// then
		verify(sqlService, times(1)).findImageMasterJSONByUpc(any(String.class), any(String.class));
		verify(sqlService, times(1)).fetchImagesRespMessage(imageUpdateRespData);
		verify(sqlService, times(1)).updateImagesMasterJson(imageMasterDataUpdated);
		verify(sqlService, times(1)).updateImagesRespMessage(imageUpdateRespData);
		verify(sqlService, times(1)).insertImagesAuditData(any(String.class), any(), any(String.class), any(String.class), any(String.class));
	}
	
	@Test
	void testProcessImageRespMessage_Image2_Error() throws MiraklRepositoryException {
		// given
		imagePayload = "{\"imageFileName\":\"191019881004_2.jpg\",\"upcid\":\"191019881004\",\"opDiv\":\"12\",\"status\":\"400\",\"message\":\"Error processing image2\"}";
		imageMasterData = new ImageMasterData("MCOM_create_220523_191001.json", "191019881004", "12", "Watch",
				"[{\"ImageID\": \"f66b063b414842ba838afd7348d40120a\", \"ImageURL\": \"https://media-dev-eu-2.mirakl.net/SOURCE/f66b063b414842ba838afd7348d40120a\", \"ImageType\": \"mainImage\"}, {\"ImageID\": \"f66b063b414842ba838afd7348d40121a\", \"ImageURL\": \"https://media-dev-eu-2.mirakl.net/SOURCE/f66b063b414842ba838afd7348d40121a\", \"ImageType\": \"image1\"}, {\"ImageID\": \"f66b063b414842ba838afd7348d40122a\", \"ImageURL\": \"https://media-dev-eu-2.mirakl.net/SOURCE/f66b063b414842ba838afd7348d40122a\", \"ImageType\": \"image2\"}, {\"ImageID\": \"f66b063b414842ba838afd7348d40123a\", \"ImageURL\": \"https://media-dev-eu-2.mirakl.net/SOURCE/f66b063b414842ba838afd7348d40123a\", \"ImageType\": \"swatch\"}]");
		imageUpdateRespData = ImageUpdateRespData.builder().upcId("191019881004").opDiv("12").imgType("image2")
				.imgFileName("191019881004_2.jpg").statusCode("400").statusMessage("Error processing image2")
				.iasRespReceivedFlag(0).build();
		imageUpdateRespDataFromDB = ImageUpdateRespData.builder()
				.inputFileName("MCOM_product_update_220523_198802.json").imgId("f66b063b414842ba838afd7348d40121_upd1")
				.imgType("image2")
				.imgUrl("https://media-dev-eu-2.mirakl.net/SOURCE/f66b063b414842ba838afd7348d40121_upd1").build();

		when(sqlService.findImageMasterJSONByUpc(any(String.class), any(String.class))).thenReturn(imageMasterData);
		when(sqlService.fetchImagesRespMessage(imageUpdateRespData)).thenReturn(imageUpdateRespDataFromDB);
		doNothing().when(sqlService).updateImagesRespMessage(imageUpdateRespData);
		doNothing().when(sqlService).insertImagesAuditData(any(String.class), any(), any(String.class),
				any(String.class), any(String.class));

		// when
		imageUpdateRespService.processImageRespMessage(imagePayload);

		// then
		verify(sqlService, times(1)).findImageMasterJSONByUpc(any(String.class), any(String.class));
		verify(sqlService, times(1)).fetchImagesRespMessage(imageUpdateRespData);
		verify(sqlService, times(0)).updateImagesMasterJson(imageMasterDataUpdated);
		verify(sqlService, times(1)).updateImagesRespMessage(imageUpdateRespData);
		verify(sqlService, times(1)).insertImagesAuditData(any(String.class), any(), any(String.class),
				any(String.class), any(String.class));
	}
	
	@Test
	void testProcessImageRespMessage_MainImagePrimary_Exception() throws MiraklRepositoryException {
		// given
		imagePayload = "{\"imageFileName\":\"191019881004_p0.jpg\",\"upcid\":\"191019881004\",\"opDiv\":\"12\",\"status\":\"200\",\"message\":\"Success\"}";
		imageMasterData = new ImageMasterData("MCOM_create_220523_191001.json", "191019881004", "12", "Watch",
				"[{\"ImageID\": \"f66b063b414842ba838afd7348d40120a\", \"ImageURL\": \"https://media-dev-eu-2.mirakl.net/SOURCE/f66b063b414842ba838afd7348d40120a\", \"ImageType\": \"mainImage\"}, {\"ImageID\": \"f66b063b414842ba838afd7348d40121a\", \"ImageURL\": \"https://media-dev-eu-2.mirakl.net/SOURCE/f66b063b414842ba838afd7348d40121a\", \"ImageType\": \"image1\"}, {\"ImageID\": \"f66b063b414842ba838afd7348d40122a\", \"ImageURL\": \"https://media-dev-eu-2.mirakl.net/SOURCE/f66b063b414842ba838afd7348d40122a\", \"ImageType\": \"image2\"}, {\"ImageID\": \"f66b063b414842ba838afd7348d40123a\", \"ImageURL\": \"https://media-dev-eu-2.mirakl.net/SOURCE/f66b063b414842ba838afd7348d40123a\", \"ImageType\": \"swatch\"}]");
		imageUpdateRespData = ImageUpdateRespData.builder().upcId("191019881004").opDiv("12").imgType("mainImagePrimary")
				.imgFileName("191019881004_1.jpg").statusCode("200").statusMessage("Success").iasRespReceivedFlag(0)
				.build();
		doThrow(EmptyResultDataAccessException.class).when(sqlService).findImageMasterJSONByUpc(any(String.class), any(String.class));
		
		// when
		imageUpdateRespService.processImageRespMessage(imagePayload);

		// then
		verify(sqlService, times(1)).findImageMasterJSONByUpc(any(String.class), any(String.class));
		verify(sqlService, times(0)).fetchImagesRespMessage(imageUpdateRespData);
		verify(sqlService, times(0)).updateImagesMasterJson(imageMasterDataUpdated);
		verify(sqlService, times(0)).updateImagesRespMessage(imageUpdateRespData);
		verify(sqlService, times(0)).insertImagesAuditData(any(String.class), any(), any(String.class), any(String.class), any(String.class));
	}
	
	@Test
	void testProcessImageRespMessage_MainImageNonPrimary_Exception() throws MiraklRepositoryException {
		// given
		imagePayload = "{\"imageFileName\":\"191019881004.jpg\",\"upcid\":\"191019881004\",\"opDiv\":\"12\",\"status\":\"200\",\"message\":\"Success\"}";
		imageMasterData = new ImageMasterData("MCOM_create_220523_191001.json", "191019881004", "12", "Watch",
				"[{\"ImageID\": \"f66b063b414842ba838afd7348d40120a\", \"ImageURL\": \"https://media-dev-eu-2.mirakl.net/SOURCE/f66b063b414842ba838afd7348d40120a\", \"ImageType\": \"mainImage\"}, {\"ImageID\": \"f66b063b414842ba838afd7348d40121a\", \"ImageURL\": \"https://media-dev-eu-2.mirakl.net/SOURCE/f66b063b414842ba838afd7348d40121a\", \"ImageType\": \"image1\"}, {\"ImageID\": \"f66b063b414842ba838afd7348d40122a\", \"ImageURL\": \"https://media-dev-eu-2.mirakl.net/SOURCE/f66b063b414842ba838afd7348d40122a\", \"ImageType\": \"image2\"}, {\"ImageID\": \"f66b063b414842ba838afd7348d40123a\", \"ImageURL\": \"https://media-dev-eu-2.mirakl.net/SOURCE/f66b063b414842ba838afd7348d40123a\", \"ImageType\": \"swatch\"}]");
		imageUpdateRespData = ImageUpdateRespData.builder().upcId("191019881004").opDiv("12").imgType("mainImageNonPrimary")
				.imgFileName("191019881004.jpg").statusCode("200").statusMessage("Success").iasRespReceivedFlag(0)
				.build();
		imageUpdateRespDataFromDB = ImageUpdateRespData.builder()
				.inputFileName("MCOM_product_update_220523_198802.json").imgId("f66b063b414842ba838afd7348d40121_upd1")
				.imgType("mainImageNonPrimary")
				.imgUrl("https://media-dev-eu-2.mirakl.net/SOURCE/f66b063b414842ba838afd7348d40121_upd1").build();
		when(sqlService.findImageMasterJSONByUpc(any(String.class), any(String.class))).thenReturn(imageMasterData);
		doThrow(EmptyResultDataAccessException.class).when(sqlService).fetchImagesRespMessage(imageUpdateRespDataFromDB);
		
		// when
		imageUpdateRespService.processImageRespMessage(imagePayload);

		// then
		verify(sqlService, times(1)).findImageMasterJSONByUpc(any(String.class), any(String.class));
		verify(sqlService, times(1)).fetchImagesRespMessage(imageUpdateRespData);
		verify(sqlService, times(0)).updateImagesMasterJson(imageMasterDataUpdated);
		verify(sqlService, times(0)).updateImagesRespMessage(imageUpdateRespData);
		verify(sqlService, times(0)).insertImagesAuditData(any(String.class), any(), any(String.class), any(String.class), any(String.class));
	}
}

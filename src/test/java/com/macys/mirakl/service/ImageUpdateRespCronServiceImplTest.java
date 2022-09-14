package com.macys.mirakl.service;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.macys.mirakl.dao.ProductUpdateDAO;
import com.macys.mirakl.model.ImageUpdateRespData;

@ExtendWith(MockitoExtension.class)
public class ImageUpdateRespCronServiceImplTest {
	
	@InjectMocks
    private ImageUpdateRespCronServiceImpl imageUpdateRespCronServiceImpl;
	
    @Mock
    private ProductUpdateDAO productUpdateDAO;
    
    @Mock
	private CloudStorageService cloudStorageService;
	
	@Test
	public void testprocessImageResponse()throws Exception {
		
		// given
		List<ImageUpdateRespData> imgUpdateResp = new ArrayList<ImageUpdateRespData>();
		
		ReflectionTestUtils.setField(imageUpdateRespCronServiceImpl, "imageUpdateRespCronEnable", "yes");
		
		ImageUpdateRespData imageUpdateRespData1 = new ImageUpdateRespData();
		imageUpdateRespData1.setImgUrl("https://media-dev-eu-2.mirakl.net/SOURCE/f66b063b414842ba838afd7348d40121_upd1");
		imageUpdateRespData1.setImgFileName("191019881004_1.jpg");
		imageUpdateRespData1.setUpcId("191019881001");
		imageUpdateRespData1.setOpDiv("12");
		imageUpdateRespData1.setStatusCode("200");
		imageUpdateRespData1.setStatusMessage("Success");
		imgUpdateResp.add(imageUpdateRespData1);

		ImageUpdateRespData imageUpdateRespData2 = new ImageUpdateRespData();
		imageUpdateRespData2.setImgUrl("https://media-dev-eu-2.mirakl.net/SOURCE/f66b063b414842ba838afd7348d40121_upd3");
		imageUpdateRespData2.setImgFileName("191019881004_3.jpg");
		imageUpdateRespData2.setUpcId("191019881003");
		imageUpdateRespData2.setOpDiv("13");
		imageUpdateRespData2.setStatusCode("200");
		imageUpdateRespData2.setStatusMessage("Success");
		imgUpdateResp.add(imageUpdateRespData2);

		String bcomImgRespData = "{\"MIRKL\":{\"images\":[{\"UPCID\":\"191019881001\",\"Status\":\"200\",\"Message\":\"Success\","
				+ "\"ImageFileName\":\"191019881004_1.jpg\",\"ImageUrl\":\"https://media-dev-eu-2.mirakl.net/SOURCE/f66b063b414842ba838afd7348d40121_upd1\","
				+ "\"OpDiv\":\"12\"}]}}";
		String mcomImgRespData = "{\"MIRKL\":{\"images\":[{\"UPCID\":\"191019881003\",\"Status\":\"200\",\"Message\":\"Success\","
				+ "\"ImageFileName\":\"191019881004_3.jpg\",\"ImageUrl\":\"https://media-dev-eu-2.mirakl.net/SOURCE/f66b063b414842ba838afd7348d40121_upd3\","
				+ "\"OpDiv\":\"13\"}]}}";

		when(productUpdateDAO.findImageUpdateRespData()).thenReturn(imgUpdateResp);
		
		// when
		imageUpdateRespCronServiceImpl.processImageUpdateResponseCron();

		//then
		verify(cloudStorageService, never()).uploadToCloudStorage(null, new JSONObject(bcomImgRespData).toString().getBytes(), "deltaBucket");
		verify(productUpdateDAO, never()).updateImageUpdateRespData(null, "fileName");
		
		verify(cloudStorageService, never()).uploadToCloudStorage(null, new JSONObject(mcomImgRespData).toString().getBytes(), "deltaBucket");
		verify(productUpdateDAO, never()).updateImageUpdateRespData(null, "fileName");
		
	}

}

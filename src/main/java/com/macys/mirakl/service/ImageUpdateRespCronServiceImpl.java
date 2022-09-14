package com.macys.mirakl.service;

import static com.macys.mirakl.util.OrchConstants.BCOM;
import static com.macys.mirakl.util.OrchConstants.CRON_IMAGE_UPDATE_RESPONSE;
import static com.macys.mirakl.util.OrchConstants.IMAGE_UPDATE_RESPONSE_DELTA_FOLDER;
import static com.macys.mirakl.util.OrchConstants.IMAG_UPDT_RESP_FILE_NAME_TST_FMT;
import static com.macys.mirakl.util.OrchConstants.IMG_RESP_IMAGES;
import static com.macys.mirakl.util.OrchConstants.INPUT_FILE_NAME;
import static com.macys.mirakl.util.OrchConstants.MCOM;
import static com.macys.mirakl.util.OrchConstants.MIRAKL;
import static com.macys.mirakl.util.OrchConstants.OP_DIV_BCOM;
import static com.macys.mirakl.util.OrchConstants.OP_DIV_MCOM;
import static com.macys.mirakl.util.OrchConstants.X_CORRELATION_ID;
import static com.macys.mirakl.util.OrchConstants.IMAGE_UPDATE_RESP_FILE_NAME;
import static com.macys.mirakl.util.OrchUtil.getCorrelationId;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.macys.mirakl.dao.ProductUpdateDAO;
import com.macys.mirakl.model.ImageUpdateRespData;

@Component
public class ImageUpdateRespCronServiceImpl implements ImageUpdateRespCronService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ImageUpdateRespCronServiceImpl.class);
	
	@Autowired
	private ProductUpdateDAO productUpdateDAO;
	
	@Value("${image.update.response.cron.job.enable:no}")
	private String imageUpdateRespCronEnable;
	
	@Value("${com.macys.mirakl.orch.bucket.images.delta}")
	private String deltaBucket;
	
	@Autowired
	private CloudStorageService cloudStorageService;


	@Scheduled(cron = "${image.update.response.cron.job.schedule}")
	public void processImageUpdateResponseCron() throws Exception {
		try {
			MDC.put(X_CORRELATION_ID, getCorrelationId());
			MDC.put(INPUT_FILE_NAME,CRON_IMAGE_UPDATE_RESPONSE);
					LOGGER.info("ImageUpdateResponseCron: Started");
			if (null != imageUpdateRespCronEnable && imageUpdateRespCronEnable.equalsIgnoreCase("yes")) {
				LOGGER.info("processImageUpdateResponseCron: Processing");
				List<ImageUpdateRespData> imgUpdateResp = productUpdateDAO.findImageUpdateRespData();
				LOGGER.info("ImageUpdateResponseCron: imgUpdateResp" + imgUpdateResp);
				if (null != imgUpdateResp && !imgUpdateResp.isEmpty()) {
					List<ImageUpdateRespData> imgUpdateRespMcom = imgUpdateResp.stream()
							.filter(c -> c.getOpDiv().equals(OP_DIV_MCOM)).collect(Collectors.toList());
					if (null != imgUpdateRespMcom && !imgUpdateRespMcom.isEmpty()) {
						String timeStamp = new SimpleDateFormat(IMAG_UPDT_RESP_FILE_NAME_TST_FMT).format(new java.util.Date());
						String fileName = IMAGE_UPDATE_RESP_FILE_NAME + "_" + timeStamp + ".json";
						boolean isFilewritten = writeToIlBucket(imgUpdateRespMcom, MCOM, fileName);
						if (isFilewritten) {
							productUpdateDAO.updateImageUpdateRespData(imgUpdateRespMcom, fileName);
						}
					}
					List<ImageUpdateRespData> imgUpdateRespBcom = imgUpdateResp.stream()
							.filter(c -> c.getOpDiv().equals(OP_DIV_BCOM)).collect(Collectors.toList());
					if (null != imgUpdateRespBcom && !imgUpdateRespBcom.isEmpty()) {
						String timeStamp = new SimpleDateFormat(IMAG_UPDT_RESP_FILE_NAME_TST_FMT).format(new java.util.Date());
						String fileName = IMAGE_UPDATE_RESP_FILE_NAME + "_" + timeStamp + ".json";
						boolean isFilewritten = writeToIlBucket(imgUpdateRespBcom, BCOM, fileName);
						if (isFilewritten) {
							productUpdateDAO.updateImageUpdateRespData(imgUpdateRespBcom, fileName);
						}
					}
				}
				LOGGER.info("ImageUpdateResponseCron: Finished");
			}
		} finally {
			MDC.remove(X_CORRELATION_ID);
			MDC.remove(INPUT_FILE_NAME);
		}
	}

	private boolean writeToIlBucket(List<ImageUpdateRespData> imgUpdateResp, String opDiv, String fileName) {
		
		JSONObject jsonObjIn = new JSONObject();
		JSONObject jsonObj = new JSONObject();
		try {
			ObjectMapper objm = new ObjectMapper();
			String ss = objm.writeValueAsString(imgUpdateResp);
			JSONArray jsonArray =  new JSONArray(ss);
			jsonObjIn.put(IMG_RESP_IMAGES, jsonArray);
			jsonObj.put(MIRAKL, jsonObjIn);
			LOGGER.info(jsonObj.toString());
			byte[] appDeltaData = convertToByteArray(jsonObj);
			String appDeltaFilePath = getFilePath(IMAGE_UPDATE_RESPONSE_DELTA_FOLDER, fileName, opDiv);
			cloudStorageService.uploadToCloudStorage(appDeltaFilePath, appDeltaData, deltaBucket);
			return true;
		} catch(Exception e) {
			LOGGER.error("Exception:", e);
			return false;
		}
	}
	
	private String getFilePath(String application, String fileName, String opDiv) {
		StringBuilder uploadFilePath = new StringBuilder();
		String uploadFolder = opDiv.concat("/").concat(application.toLowerCase());
		uploadFilePath.append(uploadFolder).append("/");
		uploadFilePath.append(fileName);		
		return uploadFilePath.toString();
	}
	
	private byte[] convertToByteArray(JSONObject jsonObj) {
		return jsonObj.toString().getBytes();

	}

}

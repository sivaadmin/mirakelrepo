package com.macys.mirakl.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.Channels;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.cloud.ReadChannel;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.macys.mirakl.model.FacetUpdateRespData;
import com.macys.mirakl.model.MiraklData;
import com.macys.mirakl.model.OfferIdRequestData;
import com.macys.mirakl.model.OfferIdResponseData;
import com.macys.mirakl.model.ProductUpdateRespData;
import com.macys.mirakl.processor.FileProcessor;

@Service
public class CloudStorageService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CloudStorageService.class);

    @Autowired
    private Storage storage;

    @Autowired
    private FileProcessor fileProcessor;

    /**
     *
     * @param bucketName
     * @param objectName
     * @param fileName
     * @return
     */
    public List<MiraklData> readCreateOrUpdateFile(String bucketName, String objectName, String fileName) {
        List<MiraklData> prdTxns = new ArrayList<MiraklData>();
        InputStream inputStream = readAsStreamFromGCS(bucketName, objectName);
        try {

            String jsonInput = fileProcessor.inputStreamToString(inputStream);
            prdTxns = fileProcessor.processUpdateJSON(fileName, jsonInput);

        }  catch (JSONException e) {
            LOGGER.error("Error in parsing Product update Json in readCreateOrUpdateFile: ", e);
        }
        catch (IOException e) {
            LOGGER.error("Exception in converting the input stream to String in readCreateOrUpdateFile: ", e);
        }
        catch (Exception e) {
            LOGGER.error("Exception in readCreateOrUpdateFile: ", e);
        }
        return prdTxns;

    }


    /**
     *
     * @param bucketName
     * @param objectName
     * @return
     * @throws IllegalArgumentException
     */
    private InputStream readAsStreamFromGCS(String bucketName, String objectName) throws IllegalArgumentException {
		Blob blob = storage.get(BlobId.of(bucketName, objectName));
		if (blob == null || !blob.exists()) {
			throw new IllegalArgumentException("Blob [" + objectName + "] does not exist");
		}
		ReadChannel reader = blob.reader();
		InputStream inputStream = Channels.newInputStream(reader);
		return inputStream;
	}

    /**
     *
     * @param uploadedFile
     * @param arr
     * @param bucket
     */
    public void uploadToCloudStorage(String uploadedFile, byte[] arr, String bucket) {
        BlobId blobId = BlobId.of(bucket, uploadedFile);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
        storage.create(blobInfo, arr);
        LOGGER.info(uploadedFile + " uploaded in Bucket::" + bucket);
    }

    /**
     *
     * @param bucketName
     * @param objectName
     * @return
     * @throws IllegalArgumentException
     * @throws IOException
     * @throws JSONException
     */
    public List<OfferIdRequestData> readOfferIdReqFile(String bucketName, String objectName) throws IllegalArgumentException, IOException, JSONException {
        List<OfferIdRequestData> offerIdReqList = new ArrayList<OfferIdRequestData>();
        InputStream inputStream = readAsStreamFromGCS(bucketName, objectName);
        String jsonInput = fileProcessor.inputStreamToString(inputStream);
        offerIdReqList = fileProcessor.processOfferIdReqJSON(jsonInput);
        return offerIdReqList;
    }

    /**
     *
     * @param bucketName
     * @param objectName
     * @return
     * @throws IllegalArgumentException
     * @throws IOException
     * @throws JSONException
     */
    public List<OfferIdResponseData> readOfferIdRespFile(String bucketName, String objectName) throws IllegalArgumentException, IOException, JSONException {
        List<OfferIdResponseData> offerIdRespList = new ArrayList<OfferIdResponseData>();
        InputStream inputStream = readAsStreamFromGCS(bucketName, objectName);
        String jsonInput = fileProcessor.inputStreamToString(inputStream);
        offerIdRespList = fileProcessor.processOfferIdRespJSON(jsonInput);
        return offerIdRespList;
    }

    /**
     *
     * @param bucketName
     * @param objectName
     * @return
     * @throws IllegalArgumentException
     * @throws IOException
     * @throws JSONException
     */
    public List<ProductUpdateRespData> readPrdUpdateRespFile(String bucketName, String objectName) throws IllegalArgumentException, IOException, JSONException {
        List<ProductUpdateRespData> prdUpdateRespList = new ArrayList<ProductUpdateRespData>();
        InputStream inputStream = readAsStreamFromGCS(bucketName, objectName);
        String jsonInput = fileProcessor.inputStreamToString(inputStream);
        prdUpdateRespList = fileProcessor.processPrdUpdateRespJSON(jsonInput);
        return prdUpdateRespList;
    }

    /**
     *
     * @param bucketName
     * @param objectName
     * @return
     * @throws IllegalArgumentException
     * @throws IOException
     * @throws JSONException
     */
    public List<FacetUpdateRespData> readFacetUpdateRespFile(String bucketName, String objectName) throws IllegalArgumentException, IOException, JSONException {
    	List<FacetUpdateRespData> facetUpdateRespList = new ArrayList<FacetUpdateRespData>();
        InputStream inputStream = readAsStreamFromGCS(bucketName, objectName);
        String jsonInput = fileProcessor.inputStreamToString(inputStream);
        facetUpdateRespList = fileProcessor.processFacetUpdateRespJSON(jsonInput);
        return facetUpdateRespList;
    }

}

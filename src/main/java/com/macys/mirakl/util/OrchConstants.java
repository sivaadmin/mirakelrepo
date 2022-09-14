package com.macys.mirakl.util;

public final class OrchConstants {
	private OrchConstants() {
        // restrict instantiation
	}
	
	public static final String  PENDING = "Pending";
	
	public static final String  PDF ="PDF";
	public static final String  STELLA ="STELLA";	
	public static final String  IMAGES ="Images";
	public static final String  IMAGE ="image";
	public static final String  IMAGE_CHANGE_DETECTION = "image_change_detection";
	
	public static final String  BUCKET ="bucket";
	public static final String  OBJECT_NAME ="name";
	public static final String  FILE_SIZE ="size";
	
	public static final String  UPCID = "UPCID";
	public static final String  UPCID_IMAGES = "upcId";
	public static final String  OP_DIV ="OP_DIV";
	public static final String  DELTA ="delta";
	public static final String  ERROR_UPCS ="ErrorUPCs";
	
	public static final String  IS_MAIN_IMG_P0 ="isMainImgP0";
	public static final String  MIRAKL ="MIRKL";
	public static final String  PRODUCT_DATA ="ProductData";
	public static final String  PDF_DATA ="PDFData";
	public static final String  STELLA_DATA ="StellaData";
	public static final String  IMAGE_DATA ="imageData";
	public static final String  FACET_DATA ="FacetData";
	public static final String  PRODUCT_TYPE ="ProductType";
	public static final String  PRODUCT_TYPE_DB ="PRODUCT_TYPE";
	
	public static final String  IMAGE_ID ="ImageID";
	public static final String  IMAGE_TYPE ="ImageType";
	public static final String  IMAGE_URL ="ImageURL";
	public static final String  MAIN_IMAGE ="mainImage";
	public static final String  SWATCH_IMAGE ="swatch";
	public static final String  SW_IMAGE ="swImage";
	
	public static final String  UPDATE ="update";
	
	public static final String  MP_PDF_TEMP ="MP_PDF_TEMP";
	public static final String  MP_STELLA_TEMP ="MP_STELLA_TEMP";
	public static final String  MP_FACET_TEMP ="MP_FACET_TEMP";
	public static final String  MP_IMAGES_TEMP ="MP_IMAGES_TEMP";
	public static final String  MP_PDF_MASTER ="MP_PDF_MASTER";
	public static final String  MP_STELLA_MASTER ="MP_STELLA_MASTER";
	public static final String  MP_IMAGES_MASTER ="MP_IMAGES_MASTER";
	public static final String  MP_FACET_MASTER = "MP_FACET_MASTER";
	public static final String  IMAGE_DATA_JSON ="IMAGE_DATA";
	public static final String  PDF_DATA_JSON ="PDF_DATA";
	public static final String  STELLA_DATA_JSON ="STELLA_DATA";
	
	public static final String  OFFER_REQUEST ="OfferRequest";
	public static final String  OFFER_RESPONSE ="OfferResponse";
	public static final String  OFFER_UPC_ID ="UpcId";
	public static final String  OFFER_OP_DIV ="OPDiv";
	public static final String  OFFER_ID ="OfferID";
	public static final String  OFFER_ACTIVE_FLAG ="active";
	public static final String  OFFER_DELETED_FLAG ="deleted";
	public static final String  RESP_STATUS="Status";
	public static final String  RESP_MESSAGE="Message";
	public static final String  SUCCESS_MESSAGE="SUCCESS";
	public static final String  ERROR_MESSAGE="ERROR";
	public static final String  STATUS_SUCCESS_CODE="200";
	public static final String  FILE_NAME_JSON ="Filename";
	public static final String  MP_UPC_MASTER ="MP_UPC_MASTER";
	public static final String  MP_OFFER_AUDIT ="MP_OFFER_AUDIT";
	
	public static final String  MP_CREATE_RESP_AUDIT ="MP_CREATE_RESP_AUDIT";
	public static final String  UPCS ="upcs";
	public static final String  UPC ="upc";
	public static final String  CREATE_RESP_OP_DIV ="opdiv";
	public static final String  PRODUCT_SKU ="productsku";
	public static final String  IMAGE_UPC ="imageupc";
	public static final String  PID_DB ="PID";
	public static final String  NRF_COLOR_CODE_DB ="NRF_COLOR_CODE";
	public static final String  NRF_SIZE_CODE_DB ="NRF_SIZE_CODE";
	public static final String  MSRP_DB ="MSRP";
	public static final String  TAX_CODE_DB ="TAX_CODE";
	public static final String  PID_JSON ="pid";
	public static final String  NRF_COLOR_CODE_JSON ="nrfColorCode";
	public static final String  NRF_SIZE_CODE_JSON ="nrfSizeCode";
	public static final String  MSRP_JSON ="msrp";
	public static final String  TAX_CODE_JSON ="taxCode";
	public static final String PRODUCT_CREATE = "PRODUCT_CREATE";
	public static final String PRODUCT_UPDATE = "PRODUCT_UPDATE";
	public static final String WRONG_FILE = "WRONG_FILE";

	public static final String MP_PDF_AUDIT = "MP_PDF_AUDIT ";
	public static final String MP_STELLA_AUDIT = "MP_STELLA_AUDIT";
	public static final String MP_IMAGES_AUDIT = "MP_IMAGES_AUDIT";
	public static final String MP_FACET_AUDIT = "MP_FACET_AUDIT";
	public static final String CREATED = "CREATED";
	public static final String UPDATE_SUCCESS = "UPDATE_SUCCESS";
	public static final String UPDATE_FAILED = "UPDATE_FAILED";
	public static final String NO_UPDATES = "NO_UPDATES";
	public static final String NO_DATA_FOUND = "NO_DATA_FOUND";
	public static final String BCOM_PDF_RESP = "BCOM/product_update_response/BCOM_product_update_pdf_";
	public static final String MCOM_PDF_RESP = "MCOM/product_update_response/MCOM_product_update_pdf_";
	public static final String BCOM_STELLA_RESP = "BCOM/product_update_response/BCOM_product_update_stella_";
	public static final String MCOM_STELLA_RESP = "MCOM/product_update_response/MCOM_product_update_stella_";
	public static final String MCOM_FACET_RESP = "MCOM/facet/MCOM_product_update_facet_";
	public static final String BCOM_FACET_RESP = "BCOM/facet/BCOM_product_update_facet_";
	public static final String PRODUCT_UPDATE_RESP = "PRODUCT_UPDATE_RESP";
	public static final String FACET_UPDATE_RESP = "FACET_UPDATE_RESP";

	public static final String  ITEMS ="items";
	public static final String  DEPT_JSON ="dept";
	public static final String  VENDOR_JSON ="vendor";
	public static final String  ATTRIBUTES ="attributes";
	public static final String  PDF_FILE_NAME ="pdf";

	public static final String  FACET_DATA_DB = "FACET_DATA";
	public static final String  ATTRIBUTE_NAME = "AttributeName";
	public static final String  ATTRIBUTE_VALUE = "AttributeValue";
	public static final String  FACET = "FACET";
	
	public static final String OP_DIV_MCOM ="12";
	public static final String OP_DIV_BCOM ="13";

	public static final String X_CORRELATION_ID ="X-Correlation-Id";
	public static final String INPUT_FILE_NAME= "FileName";

	public static final String OFFER_REQ_FILE = "OFFER_REQ_FILE";
	public static final String OFFER_RESP_FILE = "OFFER_RESP_FILE";
	
	public static final String MP_IMAGES_RESPONSE = "MP_IMAGES_RESPONSE";
	public static final String IMAGE_FILE_NAME = "imageFileName";
	public static final String OP_DIV_IMAGES = "opDiv";
	public static final String INPROCESS = "IN_PROCESS";
	public static final String ABORTED = "ABORTED";
	public static final String PROCESSED = "PROCESSED";
	public static final String PRIMARY_IMAGE = "mainImagePrimary";
	public static final String NON_PRIMARY_IMAGE = "mainImageNonPrimary";
	public static final String IMAGE1 = "image1";
	public static final String IMAGE2 = "image2";
	
	public static final String IMG_FILE_NAME = "IMG_FILE_NAME";
	public static final String UPC_ID = "UPC_ID";
	public static final String STATUS_CODE = "STATUS_CODE";
	public static final String STATUS_MESSAGE = "STATUS_MESSAGE";
	public static final String IMG_RESP_IMAGES = "images";
	public static final String IMAGE_UPDATE_RESPONSE_DELTA_FOLDER = "image_update_response";
	public static final String IMAGE_UPDATE_RESP_FILE_NAME = "image_update_response";
	public static final String MCOM ="MCOM";
	public static final String BCOM ="BCOM";
	public static final String IMAG_UPDT_RESP_FILE_NAME_TST_FMT = "yyMMdd_HHmmss";

	public static final String CRON_IMAGE_UPDATE_RESPONSE = "ImageUpdateResponseCron";
	public static final String CRON_NOTIFICATION_CLEANUP = "NotificationCleanupCron";

}

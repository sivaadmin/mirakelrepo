package com.macys.mirakl.config;

import static com.macys.mirakl.util.OrchConstants.INPUT_FILE_NAME;
import static com.macys.mirakl.util.OrchConstants.X_CORRELATION_ID;
import static com.macys.mirakl.util.OrchUtil.getCorrelationId;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;

import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.google.cloud.spring.pubsub.integration.AckMode;
import com.google.cloud.spring.pubsub.integration.inbound.PubSubInboundChannelAdapter;
import com.google.cloud.spring.pubsub.support.BasicAcknowledgeablePubsubMessage;
import com.google.cloud.spring.pubsub.support.GcpPubSubHeaders;
import com.macys.mirakl.service.ImageUpdateRespService;
import com.macys.mirakl.service.ProductCreateOrUpdateService;
import com.macys.mirakl.service.ProductUpdateRespService;

@Configuration
public class ProductCreateOrUpdateConfig {

	private static final Logger LOGGER = LoggerFactory.getLogger(ProductCreateOrUpdateConfig.class);

	@Value("${com.macys.mp.pubsub.product.create.update.request.sub}")
	private String prdUpdateReqSubscription;
	
	@Value("${com.macys.mp.pubsub.product.update.response.sub}")
	private String prdUpdateRespSubscription;
	
	@Value("${com.macys.mp.pubsub.product.response.staging.sub}")
	private String prdUpdateRespStagingSub;
	
	@Value("${com.macys.mp.pubsub.image.response.sub}")
	private String imageUpdateRespSub;

	@Autowired
	private ProductCreateOrUpdateService productCreateOrUpdateService;
	
	@Autowired
	private ProductUpdateRespService productUpdateRespService;
	
	@Autowired
	private ImageUpdateRespService imageUpdateRespService;

	// Start - Product Update request flow
	@Bean
	public PubSubInboundChannelAdapter messageAdapterPrdUpdateReq(@Qualifier("reqChannelPrdUpdate") MessageChannel reqChannelPrdUpdate,
			PubSubTemplate template) {
		PubSubInboundChannelAdapter adapter = new PubSubInboundChannelAdapter(template, prdUpdateReqSubscription);
		adapter.setOutputChannel(reqChannelPrdUpdate);
		adapter.setAckMode(AckMode.AUTO);
		return adapter;
	}

	@Bean
	MessageChannel reqChannelPrdUpdate() {
		return new DirectChannel();
	}

	@ServiceActivator(inputChannel = "reqChannelPrdUpdate")
	public void receiveReqMessage(@Payload String payload, @Header(GcpPubSubHeaders.ORIGINAL_MESSAGE) BasicAcknowledgeablePubsubMessage message) {

		try {
			MDC.put(X_CORRELATION_ID, getCorrelationId());
			LOGGER.info("Product Create/Update request payload received from Subscription::{} Payload::{}",prdUpdateReqSubscription,payload);
			message.ack();
			productCreateOrUpdateService.processIntegrationFile(payload, prdUpdateReqSubscription);
		} finally {
			MDC.remove(X_CORRELATION_ID);
			MDC.remove(INPUT_FILE_NAME);
		}

	}
	// End - Product Update request flow
	
	// Start - Product Update response flow for PDF/Stella
	@Bean
	public PubSubInboundChannelAdapter messageAdapterPrdUpdateResp(@Qualifier("respChannelPrdUpdate") MessageChannel respChannelPrdUpdate,
			PubSubTemplate template) {
		PubSubInboundChannelAdapter adapter = new PubSubInboundChannelAdapter(template, prdUpdateRespSubscription);
		adapter.setOutputChannel(respChannelPrdUpdate);
		adapter.setAckMode(AckMode.AUTO);
		return adapter;
	}

	@Bean
	MessageChannel respChannelPrdUpdate() {
		return new DirectChannel();
	}

	@ServiceActivator(inputChannel = "respChannelPrdUpdate")
	public void receiveRespMessage(@Payload String payload,
			@Header(GcpPubSubHeaders.ORIGINAL_MESSAGE) BasicAcknowledgeablePubsubMessage message) {

		try {
			MDC.put(X_CORRELATION_ID, getCorrelationId());
			LOGGER.info("Product Update response payload received from Subscription::{} Payload::{}",prdUpdateRespSubscription,payload);
			message.ack();
			productUpdateRespService.processPrdUpdateRespFile(payload, prdUpdateRespSubscription);
		} finally {
			MDC.remove(X_CORRELATION_ID);
			MDC.remove(INPUT_FILE_NAME);
		}
	}
	// End - Product Update response flow for PDF/Stella
	
	// Start - Product Update response flow for Facet
	@Bean
	public PubSubInboundChannelAdapter messageAdapterPrdUpdateRespFacet(@Qualifier("respChannelPrdUpdateFacet") MessageChannel respChannelPrdUpdateFacet,
			PubSubTemplate template) {
		// TODO update below subscription details to notification configured on GCP bucket for receiving facet response file
		PubSubInboundChannelAdapter adapter = new PubSubInboundChannelAdapter(template, prdUpdateRespStagingSub);
		adapter.setOutputChannel(respChannelPrdUpdateFacet);
		adapter.setAckMode(AckMode.AUTO);
		return adapter;
	}

	@Bean
	MessageChannel respChannelPrdUpdateFacet() {
		return new DirectChannel();
	}

	@ServiceActivator(inputChannel = "respChannelPrdUpdateFacet")
	public void receiveFacetRespMessage(@Payload String payload,
			@Header(GcpPubSubHeaders.ORIGINAL_MESSAGE) BasicAcknowledgeablePubsubMessage message) {
		try {
			MDC.put(X_CORRELATION_ID, getCorrelationId());
			LOGGER.info("Facet Product Update response received from Subscription::{} Payload::{}",prdUpdateRespStagingSub,payload);
			message.ack();
			productUpdateRespService.processPrdUpdateRespFile(payload, prdUpdateRespStagingSub);
		} finally {
			MDC.remove(X_CORRELATION_ID);
			MDC.remove(INPUT_FILE_NAME);
		}
	}
	// End - Product Update response flow for Facet
	
	// Start - Image Update response flow
	@Bean
	public PubSubInboundChannelAdapter messageAdapterImageResp(@Qualifier("imageRespChannel") MessageChannel imageRespChannel,
			PubSubTemplate template) {
		PubSubInboundChannelAdapter adapter = new PubSubInboundChannelAdapter(template, imageUpdateRespSub);
		adapter.setOutputChannel(imageRespChannel);
		adapter.setAckMode(AckMode.AUTO);
		return adapter;
	}

	@Bean
	MessageChannel imageRespChannel() {
		return new DirectChannel();
	}

	@ServiceActivator(inputChannel = "imageRespChannel")
	public void receiveImageRespMessage(@Payload String payload,
			@Header(GcpPubSubHeaders.ORIGINAL_MESSAGE) BasicAcknowledgeablePubsubMessage message) {
		try {
			MDC.put(X_CORRELATION_ID, getCorrelationId());
			LOGGER.info("Message received from mp-epc-orch::{} Subscription::{} Payload::{}", message, imageUpdateRespSub, payload);
			message.ack();
			imageUpdateRespService.processImageRespMessage(payload);
		} finally {
			MDC.remove(X_CORRELATION_ID);
		}
	}
	// End - Image Update response flow

}

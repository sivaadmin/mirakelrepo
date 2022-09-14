package com.macys.mirakl.config;

import com.google.cloud.spring.pubsub.integration.AckMode;
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
import org.springframework.messaging.handler.annotation.Payload;

import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.google.cloud.spring.pubsub.integration.inbound.PubSubInboundChannelAdapter;
import com.macys.mirakl.service.OfferIdService;
import org.springframework.messaging.handler.annotation.Header;
import com.google.cloud.spring.pubsub.support.BasicAcknowledgeablePubsubMessage;
import com.google.cloud.spring.pubsub.support.GcpPubSubHeaders;

import static com.macys.mirakl.util.OrchConstants.INPUT_FILE_NAME;
import static com.macys.mirakl.util.OrchConstants.X_CORRELATION_ID;
import static com.macys.mirakl.util.OrchUtil.getCorrelationId;

@Configuration
public class OfferIdConfig {

	private static final Logger LOGGER = LoggerFactory.getLogger(OfferIdConfig.class);

	@Value("${com.macys.mp.pubsub.offer.request.sub}")
	private String requestSubscription;
	
	@Value("${com.macys.mp.pubsub.offer.response.sub}")
	private String responseSubscription;

	@Autowired
	private OfferIdService offerIdService;

	@Bean
	public PubSubInboundChannelAdapter messageAdapterOfferReq(@Qualifier("reqChannelOffer") MessageChannel reqChannelOffer,
			PubSubTemplate template) {
		PubSubInboundChannelAdapter adapter = new PubSubInboundChannelAdapter(template, requestSubscription);
		adapter.setOutputChannel(reqChannelOffer);
		adapter.setAckMode(AckMode.AUTO);
		return adapter;
	}

	@Bean
	MessageChannel reqChannelOffer() {
		return new DirectChannel();
	}
	
	@ServiceActivator(inputChannel = "reqChannelOffer")
	public void receiveReqMessage(@Payload String payload, @Header(GcpPubSubHeaders.ORIGINAL_MESSAGE) BasicAcknowledgeablePubsubMessage message) {
		try {
			MDC.put(X_CORRELATION_ID, getCorrelationId());
			LOGGER.info("OfferId request Payload received from Subscription::{} Payload::{}",requestSubscription,payload);
			message.ack();
			offerIdService.processOfferIdReqRespFile(payload, requestSubscription);
		} finally {
			MDC.remove(X_CORRELATION_ID);
			MDC.remove(INPUT_FILE_NAME);
		}
	}
	
	@Bean
	public PubSubInboundChannelAdapter messageAdapterOfferResp(@Qualifier("respChannelOffer") MessageChannel respChannelOffer,
			PubSubTemplate template) {
		PubSubInboundChannelAdapter adapter = new PubSubInboundChannelAdapter(template, responseSubscription);
		adapter.setOutputChannel(respChannelOffer);
		adapter.setAckMode(AckMode.AUTO);
		return adapter;
	}

	@Bean
	MessageChannel respChannelOffer() {
		return new DirectChannel();
	}
	
	@ServiceActivator(inputChannel = "respChannelOffer")
	public void receiveRespMessage(@Payload String payload, @Header(GcpPubSubHeaders.ORIGINAL_MESSAGE) BasicAcknowledgeablePubsubMessage message) {
		try {
			MDC.put(X_CORRELATION_ID, getCorrelationId());
			LOGGER.info("OfferId response Payload received from Subscription::{} Payload::{}",responseSubscription,payload);
			message.ack();
			offerIdService.processOfferIdReqRespFile(payload, responseSubscription);
		} finally {
			MDC.remove(X_CORRELATION_ID);
			MDC.remove(INPUT_FILE_NAME);
		}
	}

}
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
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.handler.annotation.Payload;

import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.google.cloud.spring.pubsub.integration.inbound.PubSubInboundChannelAdapter;
import com.google.cloud.spring.pubsub.integration.outbound.PubSubMessageHandler;
import com.macys.mirakl.service.NotificationService;
import org.springframework.messaging.handler.annotation.Header;
import com.google.cloud.spring.pubsub.support.BasicAcknowledgeablePubsubMessage;
import com.google.cloud.spring.pubsub.support.GcpPubSubHeaders;

import static com.macys.mirakl.util.OrchConstants.INPUT_FILE_NAME;
import static com.macys.mirakl.util.OrchConstants.X_CORRELATION_ID;
import static com.macys.mirakl.util.OrchUtil.getCorrelationId;

@Configuration
public class NotificationConfig {

	private static final Logger LOGGER = LoggerFactory.getLogger(NotificationConfig.class);

	@Value("${com.macys.mp.pubsub.product.update.staging.sub}")
	private String prdUpdateStagingSub;

	@Value("${com.macys.mp.pubsub.product.image.staging.sub}")
	private String prdImageStagingSub;

	@Value("${com.macys.mp.pubsub.product.update.ol.onprem.pub}")
	private String prdUpdateOLOnPremPub;

	@Value("${com.macys.mp.pubsub.product.image.ol.onprem.pub}")
	private String prdImageOLOnPremPub;

	@Autowired
	private NotificationService notificationService;

	@Bean
	public PubSubInboundChannelAdapter messageAdapterPrdUpdateStaging(
			@Qualifier("reqPrdUpdateStagingChannel") MessageChannel reqPrdUpdateStagingChannel,
			PubSubTemplate template) {
		PubSubInboundChannelAdapter adapter = new PubSubInboundChannelAdapter(template, prdUpdateStagingSub);
		adapter.setOutputChannel(reqPrdUpdateStagingChannel);
		adapter.setAckMode(AckMode.AUTO);
		return adapter;
	}

	@Bean
	MessageChannel reqPrdUpdateStagingChannel() {
		return new DirectChannel();
	}

	@ServiceActivator(inputChannel = "reqPrdUpdateStagingChannel")
	public void receivePrdUpdateStagingMessage(@Payload String payload,
			@Header(GcpPubSubHeaders.ORIGINAL_MESSAGE) BasicAcknowledgeablePubsubMessage message) throws Exception {
		try {
			MDC.put(X_CORRELATION_ID, getCorrelationId());
			LOGGER.info("Product Update Staging Payload received from Subscription::{} Payload::{}",
					prdUpdateStagingSub, payload);
			message.ack();
			notificationService.processNotification(payload, prdUpdateStagingSub, false);
		} finally {
			MDC.remove(X_CORRELATION_ID);
			MDC.remove(INPUT_FILE_NAME);
		}
	}

	@Bean
	public PubSubInboundChannelAdapter messageAdapterPrdImageStaging(
			@Qualifier("reqPrdImageStagingChannel") MessageChannel reqPrdImageStagingChannel, PubSubTemplate template) {
		PubSubInboundChannelAdapter adapter = new PubSubInboundChannelAdapter(template, prdImageStagingSub);
		adapter.setOutputChannel(reqPrdImageStagingChannel);
		adapter.setAckMode(AckMode.AUTO);
		return adapter;
	}

	@Bean
	MessageChannel reqPrdImageStagingChannel() {
		return new DirectChannel();
	}

	@ServiceActivator(inputChannel = "reqPrdImageStagingChannel")
	public void receivePrdImageStagingMessage(@Payload String payload,
			@Header(GcpPubSubHeaders.ORIGINAL_MESSAGE) BasicAcknowledgeablePubsubMessage message) throws Exception {
		try {
			MDC.put(X_CORRELATION_ID, getCorrelationId());
			LOGGER.info("Product Image Staging Payload received from Subscription::{} Payload::{}", prdImageStagingSub,
					payload);
			message.ack();
			notificationService.processNotification(payload, prdImageStagingSub, false);
		} finally {
			MDC.remove(X_CORRELATION_ID);
			MDC.remove(INPUT_FILE_NAME);
		}
	}

	// Publish message
	@Bean
	@ServiceActivator(inputChannel = "prdUpdateOLOnPremPubOutputChannel")
	public MessageHandler messageSenderPrdUpdatePub(PubSubTemplate pubsubTemplate) {
		try {
			MDC.put(X_CORRELATION_ID, getCorrelationId());
			return new PubSubMessageHandler(pubsubTemplate, prdUpdateOLOnPremPub);
		} finally {
			MDC.remove(X_CORRELATION_ID);
		}
	}

	@MessagingGateway(defaultRequestChannel = "prdUpdateOLOnPremPubOutputChannel")
	public interface PubsubOutboundGatewayPrdUpdate {
		void sendToPubsubPrdUpdatePub(String text);
	}

	@Bean
	@ServiceActivator(inputChannel = "prdImageOLOnPremPubOutputChannel")
	public MessageHandler messageSenderPrdImagePub(PubSubTemplate pubsubTemplate) {
		try {
			MDC.put(X_CORRELATION_ID, getCorrelationId());
			return new PubSubMessageHandler(pubsubTemplate, prdImageOLOnPremPub);
		} finally {
			MDC.remove(X_CORRELATION_ID);
		}
	}

	@MessagingGateway(defaultRequestChannel = "prdImageOLOnPremPubOutputChannel")
	public interface PubsubOutboundGatewayPrdImage {
		void sendToPubsubPrdImagePub(String text);
	}

}
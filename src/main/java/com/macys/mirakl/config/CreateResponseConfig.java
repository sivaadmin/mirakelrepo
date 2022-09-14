package com.macys.mirakl.config;

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
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;

import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.google.cloud.spring.pubsub.integration.AckMode;
import com.google.cloud.spring.pubsub.integration.inbound.PubSubInboundChannelAdapter;
import com.google.cloud.spring.pubsub.integration.outbound.PubSubMessageHandler;
import com.google.cloud.spring.pubsub.support.BasicAcknowledgeablePubsubMessage;
import com.google.cloud.spring.pubsub.support.GcpPubSubHeaders;
import com.macys.mirakl.service.CreateResponseService;

@Configuration
public class CreateResponseConfig {

	private static final Logger LOGGER = LoggerFactory.getLogger(CreateResponseConfig.class);
	@Value("${com.macys.mp.pubsub.create.response.mcom.il.pub}")
	private String createResponseMcomPub;
	
	@Value("${com.macys.mp.pubsub.create.response.bcom.il.pub}")
	private String createResponseBcomPub;
	
	@Value("${com.macys.mp.pubsub.create.response.sub}")
	private String createResponseSub;

	@Autowired
	private CreateResponseService createResponseService;
	
	// Start - inbound channel adapter listens to messages from a Google Cloud Pub/Sub subscription and sends them to a Spring channel in an application.
	@Bean
	public PubSubInboundChannelAdapter messageAdapterCreateResp(@Qualifier("createRespChannel") MessageChannel createRespChannel,
			PubSubTemplate template) {
		PubSubInboundChannelAdapter adapter = new PubSubInboundChannelAdapter(template, createResponseSub);
		adapter.setOutputChannel(createRespChannel);
		adapter.setAckMode(AckMode.AUTO);
		return adapter;
	}

	@Bean
	MessageChannel createRespChannel() {
		return new DirectChannel();
	}

	@ServiceActivator(inputChannel = "createRespChannel")
	public void receiveMessageCreateResp(@Payload String payload, @Header(GcpPubSubHeaders.ORIGINAL_MESSAGE) BasicAcknowledgeablePubsubMessage message) {
		try {
			MDC.put(X_CORRELATION_ID, getCorrelationId());
			LOGGER.info("Message received from mp-epc-orch::{} Subscription::{} Payload::{}", message, createResponseSub, payload);
			message.ack();
			createResponseService.processCreateRespFile(payload);
		} finally {
			MDC.remove(X_CORRELATION_ID);
		}
	}
	// End - inbound channel adapter listens to messages from a Google Cloud Pub/Sub subscription and sends them to a Spring channel in an application.
	
	// Start - Outbound channel adapter listens to new messages from a Spring channel and publishes them to a Google Cloud Pub/Sub topic.
	   
	@Bean
	@ServiceActivator(inputChannel = "createRespMcomOutputChannel")
	public MessageHandler messageSenderCreateRespMcom(PubSubTemplate pubsubTemplate) {
		try {
			MDC.put(X_CORRELATION_ID, getCorrelationId());
			return new PubSubMessageHandler(pubsubTemplate, createResponseMcomPub);
		} finally {
			MDC.remove(X_CORRELATION_ID);
		}
	}
	   
	@MessagingGateway(defaultRequestChannel = "createRespMcomOutputChannel")
	public interface PubsubOutboundGatewayMcom {
	   void sendToPubsubCreateRespMcom(String text);
	}
	
	@Bean
	@ServiceActivator(inputChannel = "createRespBcomOutputChannel")
	public MessageHandler messageSenderCreateRespBcom(PubSubTemplate pubsubTemplate) {
		try {
			MDC.put(X_CORRELATION_ID, getCorrelationId());
			return new PubSubMessageHandler(pubsubTemplate, createResponseBcomPub);
		} finally {
			MDC.remove(X_CORRELATION_ID);
		}
	}
	
	@MessagingGateway(defaultRequestChannel = "createRespBcomOutputChannel")
	public interface PubsubOutboundGatewayBcom {
	   void sendToPubsubCreateRespBcom(String text);
	}
	
	// End - Outbound channel adapter listens to new messages from a Spring channel and publishes them to a Google Cloud Pub/Sub topic.

}
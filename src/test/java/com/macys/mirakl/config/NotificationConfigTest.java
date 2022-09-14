package com.macys.mirakl.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.MessageChannel;
import org.springframework.test.util.ReflectionTestUtils;

import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.google.cloud.spring.pubsub.integration.inbound.PubSubInboundChannelAdapter;
import com.google.cloud.spring.pubsub.integration.outbound.PubSubMessageHandler;
import com.google.cloud.spring.pubsub.support.BasicAcknowledgeablePubsubMessage;
import com.google.cloud.spring.pubsub.support.PublisherFactory;
import com.google.cloud.spring.pubsub.support.SubscriberFactory;
import com.macys.mirakl.service.NotificationService;

@ExtendWith(MockitoExtension.class)
class NotificationConfigTest {

	@InjectMocks
	private NotificationConfig notificationConfig;

	@Mock
	private NotificationService notificationService;

	@Mock
	MessageChannel mockMessageChannel;

	@Mock
	private PublisherFactory mockPublisherFactory;

	@Mock
	private SubscriberFactory mockSubscriberFactory;

	@Mock
	private BasicAcknowledgeablePubsubMessage basicAcknowledgeablePubsubMessage;

	@BeforeEach
	void setUp() throws Exception {
		ReflectionTestUtils.setField(notificationConfig, "prdUpdateStagingSub",
				"M.MER.EPC.MIRAKL.PRODUCT.STAGING.ONPERM.ORCH.SUB");
		ReflectionTestUtils.setField(notificationConfig, "prdImageStagingSub",
				"M.MER.EPC.MIRAKL.PRODUCT.IMAGE.STAGING.SUB");
		ReflectionTestUtils.setField(notificationConfig, "prdUpdateOLOnPremPub",
				"M.MER.EPC.MIRAKL.PRODUCT.UPDATE.OL.ONPREM.PUB");
		ReflectionTestUtils.setField(notificationConfig, "prdImageOLOnPremPub",
				"M.MER.EPC.MIRAKL.PRODUCT.IMAGE.OL.ONPREM.PUB");
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void testMessageAdapterPrdUpdateStaging() {
		// Given
		PubSubTemplate pubSubTemplate = new PubSubTemplate(this.mockPublisherFactory, this.mockSubscriberFactory);

		// When
		PubSubInboundChannelAdapter pubSubInboundChannelAdapter = notificationConfig
				.messageAdapterPrdUpdateStaging(mockMessageChannel, pubSubTemplate);
		// Then
		assertEquals(mockMessageChannel, pubSubInboundChannelAdapter.getOutputChannel(),
				"the output channel should be " + mockMessageChannel);

	}

	@Test
	void testReqPrdUpdateStagingChannel() {
		// Given

		// When
		MessageChannel messageChannel = notificationConfig.reqPrdUpdateStagingChannel();

		// Then
		assertNotNull(messageChannel, "MessageChannel should not be Null");
	}

	@Test
	void testReceivePrdUpdateStagingMessage() throws Exception {
		// Given
		String payload = "";
		String prdUpdateStagingSub = "M.MER.EPC.MIRAKL.PRODUCT.STAGING.ONPERM.ORCH.SUB";

		// When
		notificationConfig.receivePrdUpdateStagingMessage(payload, basicAcknowledgeablePubsubMessage);

		// Then
		verify(notificationService, times(1)).processNotification(payload, prdUpdateStagingSub, false);
		verify(basicAcknowledgeablePubsubMessage, times(1)).ack();
	}

	@Test
	void testMessageAdapterPrdImageStaging() {
		// Given
		PubSubTemplate pubSubTemplate = new PubSubTemplate(this.mockPublisherFactory, this.mockSubscriberFactory);

		// When
		PubSubInboundChannelAdapter pubSubInboundChannelAdapter = notificationConfig
				.messageAdapterPrdImageStaging(mockMessageChannel, pubSubTemplate);

		// Then
		assertEquals(mockMessageChannel, pubSubInboundChannelAdapter.getOutputChannel(),
				"the output channel should be " + mockMessageChannel);

	}

	@Test
	void testReqPrdImageStagingChannel() {
		// Given
		// When
		MessageChannel messageChannel = notificationConfig.reqPrdImageStagingChannel();
		// Then
		assertNotNull(messageChannel, "MessageChannel should not be Null");
	}

	@Test
	void testReceivePrdImageStagingMessage() throws Exception {
		// Given
		String payload = "";
		String prdImageStagingSub = "M.MER.EPC.MIRAKL.PRODUCT.IMAGE.STAGING.SUB";

		// When
		notificationConfig.receivePrdImageStagingMessage(payload, basicAcknowledgeablePubsubMessage);

		// Then
		verify(notificationService, times(1)).processNotification(payload, prdImageStagingSub, false);
		verify(basicAcknowledgeablePubsubMessage, times(1)).ack();
	}

	@Test
	void testMessageSenderPrdUpdatePub() {
		// Given
		PubSubTemplate pubSubTemplate = new PubSubTemplate(this.mockPublisherFactory, this.mockSubscriberFactory);

		// When
		PubSubMessageHandler messageHandler = (PubSubMessageHandler) notificationConfig
				.messageSenderPrdUpdatePub(pubSubTemplate);

		// Then
		assertNotNull(messageHandler, "PubSubMessageHandler should not be Null");
	}

	@Test
	void testMessageSenderPrdImagePub() {
		// Given
		PubSubTemplate pubSubTemplate = new PubSubTemplate(this.mockPublisherFactory, this.mockSubscriberFactory);

		// When
		PubSubMessageHandler messageHandler = (PubSubMessageHandler) notificationConfig
				.messageSenderPrdImagePub(pubSubTemplate);

		// Then
		assertNotNull(messageHandler, "PubSubMessageHandler should not be Null");
	}

}

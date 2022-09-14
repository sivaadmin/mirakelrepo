package com.macys.mirakl.config;

import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.google.cloud.spring.pubsub.integration.inbound.PubSubInboundChannelAdapter;
import com.google.cloud.spring.pubsub.support.BasicAcknowledgeablePubsubMessage;
import com.google.cloud.spring.pubsub.support.PublisherFactory;
import com.google.cloud.spring.pubsub.support.SubscriberFactory;
import com.macys.mirakl.service.OfferIdService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.MessageChannel;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OfferIdConfigTest {

    @InjectMocks
    private OfferIdConfig offerIdConfig;

    @Mock
    private OfferIdService offerIdService;

    @Mock
    MessageChannel mockMessageChannel;

    @Mock
    private PublisherFactory mockPublisherFactory;

    @Mock
    private SubscriberFactory mockSubscriberFactory;

    @Mock
    private BasicAcknowledgeablePubsubMessage basicAcknowledgeablePubsubMessage;

    @BeforeEach
	void setUp() {
		ReflectionTestUtils.setField(offerIdConfig, "requestSubscription",
				"M.MER.EPC.MIRAKL.PRODUCT.OFFER.CLOUDORCH.SUB");
		ReflectionTestUtils.setField(offerIdConfig, "responseSubscription",
				"projects/mtech-merch-epc-poc-317014/subscriptions/M.MER.EPC.MIRAKL.PRODUCT.OFFER.RESPONSE.CLOUDORCH.SUB");
	}

    @AfterEach
    void tearDown() {
    }

    @Test
    void testMessageAdapterOfferReq() {
        //Given
        PubSubTemplate pubSubTemplate = new PubSubTemplate(this.mockPublisherFactory, this.mockSubscriberFactory);

        //When
        PubSubInboundChannelAdapter pubSubInboundChannelAdapter = offerIdConfig.messageAdapterOfferReq(mockMessageChannel, pubSubTemplate);
        //Then
        assertEquals(mockMessageChannel, pubSubInboundChannelAdapter.getOutputChannel(), "the output channel should be " + mockMessageChannel);

    }

    @Test
    void testReqChannelOffer() {
        //Given
        //When
        MessageChannel messageChannel = offerIdConfig.reqChannelOffer();
        //Then
        assertNotNull(messageChannel, "MessageChannel should not be Null");
    }

    @Test
    void testMessageAdapterOfferResp() {
        //Given
        PubSubTemplate pubSubTemplate = new PubSubTemplate(this.mockPublisherFactory, this.mockSubscriberFactory);
        //When
        PubSubInboundChannelAdapter pubSubInboundChannelAdapter = offerIdConfig.messageAdapterOfferResp(mockMessageChannel, pubSubTemplate);
        //Then
        assertEquals(mockMessageChannel, pubSubInboundChannelAdapter.getOutputChannel(), "the output channel should be " + mockMessageChannel);
    }

    @Test
    void testRespChannelOffer() {
        //Given
        //When
        MessageChannel messageChannel = offerIdConfig.respChannelOffer();
        //Then
        assertNotNull(messageChannel, "MessageChannel should not be Null");
    }

    @Test
    void testReceiveReqMessage() {
        //Given
        String payload = "";
        String subscription = "M.MER.EPC.MIRAKL.PRODUCT.OFFER.CLOUDORCH.SUB";
        //When
        offerIdConfig.receiveReqMessage(payload, basicAcknowledgeablePubsubMessage);
        //Then
        verify(offerIdService, times(1)).processOfferIdReqRespFile(payload, subscription);
        verify(basicAcknowledgeablePubsubMessage, times(1)).ack();
    }

    @Test
    void testReceiveRespMessage() {
        //Given
        String payload = "";
        String subscription = "projects/mtech-merch-epc-poc-317014/subscriptions/M.MER.EPC.MIRAKL.PRODUCT.OFFER.RESPONSE.CLOUDORCH.SUB";
        //When
        offerIdConfig.receiveRespMessage(payload, basicAcknowledgeablePubsubMessage);
        //Then
        verify(offerIdService, times(1)).processOfferIdReqRespFile(payload, subscription);
        verify(basicAcknowledgeablePubsubMessage, times(1)).ack();
    }
}
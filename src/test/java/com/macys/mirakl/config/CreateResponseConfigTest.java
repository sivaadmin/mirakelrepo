package com.macys.mirakl.config;

import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.google.cloud.spring.pubsub.integration.inbound.PubSubInboundChannelAdapter;
import com.google.cloud.spring.pubsub.integration.outbound.PubSubMessageHandler;
import com.google.cloud.spring.pubsub.support.BasicAcknowledgeablePubsubMessage;
import com.google.cloud.spring.pubsub.support.PublisherFactory;
import com.google.cloud.spring.pubsub.support.SubscriberFactory;
import com.macys.mirakl.service.CreateResponseService;
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
class CreateResponseConfigTest {

    @InjectMocks
    private CreateResponseConfig createResponseConfig;

    @Mock
    private CreateResponseService createResponseService;

    @Mock
    private PublisherFactory mockPublisherFactory;

    @Mock
    private SubscriberFactory mockSubscriberFactory;

    @Mock
    MessageChannel mockMessageChannel;

    @Mock
    BasicAcknowledgeablePubsubMessage basicAcknowledgeablePubsubMessage;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(createResponseConfig, "createResponseMcomPub", "projects/mtech-merch-epc-poc-317014/topics/M.MER.EPC.MIRAKL.CREATE.RESPONSE.CLOUDORCH.PUB");
        ReflectionTestUtils.setField(createResponseConfig, "createResponseBcomPub", "projects/mtech-merch-epc-poc-317014/topics/M.MER.EPC.MIRAKL.CREATE.RESPONSE.CLOUDORCH.PUB");
        ReflectionTestUtils.setField(createResponseConfig, "createResponseSub", "M.MER.EPC.MIRAKL.CREATE.RESPONSE.SUB");

    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testMessageAdapterCreateResp() {

        //Given
        PubSubTemplate pubSubTemplate = new PubSubTemplate(this.mockPublisherFactory, this.mockSubscriberFactory);

        //When
        PubSubInboundChannelAdapter pubSubInboundChannelAdapter = createResponseConfig.messageAdapterCreateResp(mockMessageChannel, pubSubTemplate);

        //Then
        assertEquals(mockMessageChannel, pubSubInboundChannelAdapter.getOutputChannel(), "the output channel should be " + mockMessageChannel);

    }

    @Test
    void testCreateRespChannel() {
        //Given
        //When
        MessageChannel messageChannel = createResponseConfig.createRespChannel();
        //Then
        assertNotNull(messageChannel, "MessageChannel should not be Null");
    }

    @Test
    void testReceiveMessageCreateResp() {
        //Given
        String payload = "";
        //When
        createResponseConfig.receiveMessageCreateResp(payload, basicAcknowledgeablePubsubMessage);
        //Then
        verify(createResponseService, times(1)).processCreateRespFile(payload);
        verify(basicAcknowledgeablePubsubMessage, times(1)).ack();
    }

    @Test
    void testMessageSenderCreateRespMcom() {
        //Given
        PubSubTemplate pubSubTemplate = new PubSubTemplate(this.mockPublisherFactory, this.mockSubscriberFactory);
        //When
        PubSubMessageHandler messageHandler = (PubSubMessageHandler) createResponseConfig.messageSenderCreateRespMcom(pubSubTemplate);
        //Then
        assertNotNull(messageHandler, "PubSubMessageHandler should not be Null");
    }

    @Test
    void testMessageSenderCreateRespBcom() {
        //Given
        PubSubTemplate pubSubTemplate = new PubSubTemplate(this.mockPublisherFactory, this.mockSubscriberFactory);
        //When
        PubSubMessageHandler messageHandler = (PubSubMessageHandler) createResponseConfig.messageSenderCreateRespBcom(pubSubTemplate);
        //Then
        assertNotNull(messageHandler, "PubSubMessageHandler should not be Null");
    }
}
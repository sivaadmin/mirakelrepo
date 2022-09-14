package com.macys.mirakl.config;

import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.google.cloud.spring.pubsub.integration.inbound.PubSubInboundChannelAdapter;
import com.google.cloud.spring.pubsub.support.BasicAcknowledgeablePubsubMessage;
import com.google.cloud.spring.pubsub.support.PublisherFactory;
import com.google.cloud.spring.pubsub.support.SubscriberFactory;
import com.macys.mirakl.service.ProductCreateOrUpdateService;
import com.macys.mirakl.service.ProductUpdateRespService;
import com.macys.mirakl.service.ImageUpdateRespService;
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
class ProductCreateOrUpdateConfigTest {

    @InjectMocks
    private ProductCreateOrUpdateConfig productCreateOrUpdateConfig;

    @Mock
    private ProductCreateOrUpdateService productCreateOrUpdateService;

    @Mock
    private ProductUpdateRespService productUpdateRespService;
    
    @Mock
    private ImageUpdateRespService imageUpdateRespService;

    @Mock
    private PublisherFactory mockPublisherFactory;

    @Mock
    private SubscriberFactory mockSubscriberFactory;

    @Mock
    private MessageChannel mockMessageChannel;

    @Mock
    private BasicAcknowledgeablePubsubMessage basicAcknowledgeablePubsubMessage;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(productCreateOrUpdateConfig, "prdUpdateReqSubscription",
                "M.MER.EPC.MIRAKL.TRANSFER.SUB");
        ReflectionTestUtils.setField(productCreateOrUpdateConfig, "prdUpdateRespSubscription",
                "projects/mtech-merch-epc-poc-317014/subscriptions/M.MER.EPC.MIRAKL.PRODUCT.RESPONSE.SUB");
        ReflectionTestUtils.setField(productCreateOrUpdateConfig, "prdUpdateRespStagingSub",
                "M.MER.EPC.MIRAKL.PRODUCT.RESPONSE.STAGING.SUB");
        ReflectionTestUtils.setField(productCreateOrUpdateConfig, "imageUpdateRespSub",
                "M.MER.EPC.MIRAKL.IMAGE.RESPONSE.SUB");
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testMessageAdapterPrdUpdateReq() {
        //Given
        PubSubTemplate pubSubTemplate = new PubSubTemplate(this.mockPublisherFactory, this.mockSubscriberFactory);

        //When
        PubSubInboundChannelAdapter pubSubInboundChannelAdapter = productCreateOrUpdateConfig.messageAdapterPrdUpdateReq(mockMessageChannel, pubSubTemplate);
        //Then
        assertEquals(mockMessageChannel, pubSubInboundChannelAdapter.getOutputChannel(), "the output channel should be " + mockMessageChannel);
    }

    @Test
    void testReqChannelPrdUpdate() {
        //Given
        //When
        MessageChannel messageChannel = productCreateOrUpdateConfig.reqChannelPrdUpdate();
        //Then
        assertNotNull(messageChannel, "MessageChannel should not be Null");
    }

    @Test
    void testReceiveReqMessage() {
        //Given
        String payload = "";
        String subscription = "M.MER.EPC.MIRAKL.TRANSFER.SUB";
        //When
        productCreateOrUpdateConfig.receiveReqMessage(payload, basicAcknowledgeablePubsubMessage);
        //Then
        verify(productCreateOrUpdateService, times(1)).processIntegrationFile(payload, subscription);
        verify(basicAcknowledgeablePubsubMessage, times(1)).ack();
    }

    @Test
    void testMessageAdapterPrdUpdateResp() {
        //Given
        PubSubTemplate pubSubTemplate = new PubSubTemplate(this.mockPublisherFactory, this.mockSubscriberFactory);
        //When
        PubSubInboundChannelAdapter pubSubInboundChannelAdapter = productCreateOrUpdateConfig.messageAdapterPrdUpdateResp(mockMessageChannel, pubSubTemplate);
        //Then
        assertEquals(mockMessageChannel, pubSubInboundChannelAdapter.getOutputChannel(), "the output channel should be " + mockMessageChannel);
    }

    @Test
    void testRespChannelPrdUpdate() {
        //Given
        //When
        MessageChannel messageChannel = productCreateOrUpdateConfig.respChannelPrdUpdate();
        // Then
        assertNotNull(messageChannel, "MessageChannel should not be Null");
    }

    @Test
    void testReceiveRespMessage() {
        //Given
        String payload = "";
        String subscription = "projects/mtech-merch-epc-poc-317014/subscriptions/M.MER.EPC.MIRAKL.PRODUCT.RESPONSE.SUB";
        //When
        productCreateOrUpdateConfig.receiveRespMessage(payload, basicAcknowledgeablePubsubMessage);
        //Then
        verify(productUpdateRespService, times(1)).processPrdUpdateRespFile(payload, subscription);
        verify(basicAcknowledgeablePubsubMessage, times(1)).ack();
    }

    @Test
    void testMessageAdapterPrdUpdateRespFacet() {
        //Given
        PubSubTemplate pubSubTemplate = new PubSubTemplate(this.mockPublisherFactory, this.mockSubscriberFactory);
        //When
        PubSubInboundChannelAdapter pubSubInboundChannelAdapter = productCreateOrUpdateConfig.messageAdapterPrdUpdateRespFacet(mockMessageChannel, pubSubTemplate);
        //Then
        assertEquals(mockMessageChannel, pubSubInboundChannelAdapter.getOutputChannel(), "the output channel should be " + mockMessageChannel);
    }

    @Test
    void testRespChannelPrdUpdateFacet() {
        //Given
        //When
        MessageChannel messageChannel = productCreateOrUpdateConfig.respChannelPrdUpdateFacet();
        //Then
        assertNotNull(messageChannel, "MessageChannel should not be Null");
    }

    @Test
    void testReceiveFacetRespMessage() {
        //Given
        String payload = "";
        String subscription = "M.MER.EPC.MIRAKL.PRODUCT.RESPONSE.STAGING.SUB";
        //When
        productCreateOrUpdateConfig.receiveFacetRespMessage(payload, basicAcknowledgeablePubsubMessage);
        //Then
        verify(productUpdateRespService, times(1)).processPrdUpdateRespFile(payload, subscription);
        verify(basicAcknowledgeablePubsubMessage, times(1)).ack();
    }
    
    @Test
    void testMessageAdapterImageResp() {
        //Given
        PubSubTemplate pubSubTemplate = new PubSubTemplate(this.mockPublisherFactory, this.mockSubscriberFactory);
        //When
        PubSubInboundChannelAdapter pubSubInboundChannelAdapter = productCreateOrUpdateConfig.messageAdapterImageResp(mockMessageChannel, pubSubTemplate);
        //Then
        assertEquals(mockMessageChannel, pubSubInboundChannelAdapter.getOutputChannel(), "the output channel should be " + mockMessageChannel);
    }

    @Test
    void testImageRespChannel() {
        //Given
        //When
        MessageChannel messageChannel = productCreateOrUpdateConfig.respChannelPrdUpdateFacet();
        //Then
        assertNotNull(messageChannel, "MessageChannel should not be Null");
    }

    @Test
    void testReceiveImageRespMessage() {
        //Given
        String payload = "";
        //When
        productCreateOrUpdateConfig.receiveImageRespMessage(payload, basicAcknowledgeablePubsubMessage);
        //Then
        verify(imageUpdateRespService, times(1)).processImageRespMessage(payload);
        verify(basicAcknowledgeablePubsubMessage, times(1)).ack();
    }
}
package com.macys.mirakl;

import com.google.cloud.spring.autoconfigure.storage.GcpStorageAutoConfiguration;
import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.macys.mirakl.service.CloudStorageService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class MiraklOrchApplicationTests {

	@MockBean
	CloudStorageService cloudStorageService;

	@MockBean
	GcpStorageAutoConfiguration gcpStorageAutoConfiguration;

	@MockBean
	PubSubTemplate pubSubTemplate;

	@Test
	void contextLoads() {
	}

}
package com.lmz.sentiment_analysis;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class SentimentAnalysisApplicationTests {
	//Tests if the application context loads without any issues.
	//If the context fails to load, this test will fail.
	@Test
	void contextLoads() {
	}
}

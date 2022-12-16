package org.usf.assertapi.test;

import org.springframework.web.client.RestTemplate;
import org.usf.assertapi.core.AssertionResult;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j(topic="org.usf.assertapi.core.ApiAssertion")
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class TestContext {

	private static final ThreadLocal<TestContext> threadLocal = new ThreadLocal<>();
	
	private final RestTemplate template;
	private final String traceUri;

	public static void setContext(RestTemplate template, String traceUri) {
		threadLocal.set(new TestContext(template, traceUri));
	}
	
	public static void testCaseOrigin(AssertionResult r) {
		var context = threadLocal.get();
		if(context == null) {
			log.warn("cannot trace result");
		}
		else {
			context.template.put(context.traceUri, r);
		}
	}
	
}
